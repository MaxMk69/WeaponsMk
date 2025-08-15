package net.maxmk.weaponsmk.item.custom;

import net.maxmk.weaponsmk.entity.custom.EndTridentEntity;
import net.maxmk.weaponsmk.entity.custom.FrostTridentEntity;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.Position;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentEffectComponents;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class EndTrident extends NetheriteTrident{
    public EndTrident(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public void releaseUsing(ItemStack pStack, Level pLevel, LivingEntity pEntityLiving, int pTimeLeft) {
        if (pEntityLiving instanceof Player player) {
            int i = this.getUseDuration(pStack, pEntityLiving) - pTimeLeft;
            if (i >= 10) {
                float f = EnchantmentHelper.getTridentSpinAttackStrength(pStack, player);
                if (!(f > 0.0F) || player.isInWaterOrRain()) {
                    if (!isTooDamagedToUse(pStack)) {
                        Holder<SoundEvent> holder = EnchantmentHelper.pickHighestLevel(pStack, EnchantmentEffectComponents.TRIDENT_SOUND).orElse(SoundEvents.TRIDENT_THROW);
                        if (!pLevel.isClientSide) {
                            pStack.hurtAndBreak(1, player, LivingEntity.getSlotForHand(pEntityLiving.getUsedItemHand()));
                            if (f == 0.0F) {
                                EndTridentEntity endTridentEntity = new EndTridentEntity(pLevel, player, pStack);
                                endTridentEntity.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 3F, 0.5F);
                                
                                // Check if player is sneaking for teleport ability
                                boolean isSneaking = player.isShiftKeyDown();
                                if (isSneaking) {
                                    // Mark the trident for teleportation
                                    endTridentEntity.getPersistentData().putBoolean("EndTridentTeleport", true);
                                    endTridentEntity.getPersistentData().putUUID("TeleportPlayer", player.getUUID());
                                    
                                    // Play end teleport sound
                                    pLevel.playSound(null, player, SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 1.0F, 1.0F);
                                    
                                    // Add end particles around player
                                    if (pLevel instanceof net.minecraft.server.level.ServerLevel serverLevel) {
                                        for (int particleCount = 0; particleCount < 25; particleCount++) {
                                            double offsetX = (player.getRandom().nextDouble() - 0.5) * 2.0;
                                            double offsetY = player.getRandom().nextDouble() * 2.0;
                                            double offsetZ = (player.getRandom().nextDouble() - 0.5) * 2.0;
                                            serverLevel.sendParticles(
                                                net.minecraft.core.particles.ParticleTypes.PORTAL,
                                                player.getX() + offsetX, player.getY() + offsetY, player.getZ() + offsetZ,
                                                1, 0.0, 0.0, 0.0, 0.01
                                            );
                                        }
                                    }
                                }
                                
                                if (player.hasInfiniteMaterials()) {
                                    endTridentEntity.pickup = AbstractArrow.Pickup.CREATIVE_ONLY;
                                }

                                pLevel.addFreshEntity(endTridentEntity);
                                pLevel.playSound(null, endTridentEntity, holder.value(), SoundSource.PLAYERS, 1.0F, 1.0F);
                                if (!player.hasInfiniteMaterials()) {
                                    player.getInventory().removeItem(pStack);
                                }
                            }
                        }

                        player.awardStat(Stats.ITEM_USED.get(this));
                        if (f > 0.0F) {
                            float f7 = player.getYRot();
                            float f1 = player.getXRot();
                            float f2 = -Mth.sin(f7 * (float) (Math.PI / 180.0)) * Mth.cos(f1 * (float) (Math.PI / 180.0));
                            float f3 = -Mth.sin(f1 * (float) (Math.PI / 180.0));
                            float f4 = Mth.cos(f7 * (float) (Math.PI / 180.0)) * Mth.cos(f1 * (float) (Math.PI / 180.0));
                            float f5 = Mth.sqrt(f2 * f2 + f3 * f3 + f4 * f4);
                            f2 *= f / f5;
                            f3 *= f / f5;
                            f4 *= f / f5;
                            player.push((double)f2, (double)f3, (double)f4);
                            player.startAutoSpinAttack(20, 8.0F, pStack);
                            if (player.onGround()) {
                                float f6 = 1.1999999F;
                                player.move(MoverType.SELF, new Vec3(0.0, 1.1999999F, 0.0));
                            }

                            pLevel.playSound(null, player, holder.value(), SoundSource.PLAYERS, 1.0F, 1.0F);
                        }
                    }
                }
            }
        }
    }

    private static boolean isTooDamagedToUse(ItemStack pStack) {
        return pStack.getDamageValue() >= pStack.getMaxDamage() - 1;
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        return true;
    }

    @Override
    public Projectile asProjectile(Level pLevel, Position pPos, ItemStack pStack, Direction pDirection) {
        EndTridentEntity endTridentEntity = new EndTridentEntity(pLevel, pPos.x(), pPos.y(), pPos.z(), pStack.copyWithCount(1));
        endTridentEntity.pickup = AbstractArrow.Pickup.ALLOWED;
        return endTridentEntity;
    }
}

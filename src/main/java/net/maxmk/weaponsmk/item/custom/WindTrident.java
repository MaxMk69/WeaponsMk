package net.maxmk.weaponsmk.item.custom;

import net.maxmk.weaponsmk.entity.custom.FrostTridentEntity;
import net.maxmk.weaponsmk.entity.custom.WindTridentEntity;
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
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.item.enchantment.EnchantmentEffectComponents;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class WindTrident extends NetheriteTrident{
    public WindTrident(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public void releaseUsing(ItemStack pStack, Level pLevel, LivingEntity pEntityLiving, int pTimeLeft) {
        if (pEntityLiving instanceof Player player) {
            int i = this.getUseDuration(pStack, pEntityLiving) - pTimeLeft;
            if (i >= 10) {
                float f = EnchantmentHelper.getTridentSpinAttackStrength(pStack, player);
                
                // Check if player has riptide enchantment
                int riptideLevel = EnchantmentHelper.getItemEnchantmentLevel(
                    pLevel.registryAccess().lookupOrThrow(net.minecraft.core.registries.Registries.ENCHANTMENT).getOrThrow(Enchantments.RIPTIDE),
                    pStack);
                
                // Special wind trident ability: riptide works on land
                if (riptideLevel > 0 && !player.isInWaterOrRain() && player.onGround()) {
                    // Check cooldown (3 seconds)
                    long currentTime = player.level().getGameTime();
                    long lastLeapTime = player.getPersistentData().getLong("WindTridentLastLeap");
                    if (currentTime - lastLeapTime < 60) { // 60 ticks = 3 seconds
                        return; // Still on cooldown
                    }
                    
                    // Wind leap ability - scale with riptide level (reduced values)
                    // Riptide 1: 1 block up, 3 blocks forward
                    // Riptide 2: 2 blocks up, 4 blocks forward
                    // Riptide 3: 3 blocks up, 5 blocks forward
                    float leapHeight = Math.min(riptideLevel * 0.4F, 1.2F); // Cap at 3 blocks
                    float leapDistance = Math.min(riptideLevel * 0.5F + 1F, 2.5F); // Much smaller forward distance
                    
                    // Calculate forward direction based on player's look direction
                    float yRot = player.getYRot() * (float) (Math.PI / 180.0);
                    float xRot = player.getXRot() * (float) (Math.PI / 180.0);
                    
                    // Forward vector
                    float forwardX = -Mth.sin(yRot) * Mth.cos(xRot);
                    float forwardZ = Mth.cos(yRot) * Mth.cos(xRot);
                    
                    // Apply the leap - use velocity but with much smaller values
                    Vec3 leapVector = new Vec3(forwardX * leapDistance, leapHeight, forwardZ * leapDistance);
                    player.setDeltaMovement(leapVector);
                    
                    // Debug info
                    System.out.println("Wind Trident: Riptide " + riptideLevel + " - Leaping " + leapHeight + " blocks up and " + leapDistance + " blocks forward!");
                    
                    // Mark player for ground slam effect
                    player.getPersistentData().putBoolean("WindTridentLeaping", true);
                    
                    // Update cooldown
                    player.getPersistentData().putLong("WindTridentLastLeap", currentTime);
                    
                    // Play wind sound
                    pLevel.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.TRIDENT_THROW, SoundSource.PLAYERS, 1.0F, 1.0F);
                    
                    // Add wind particles around the player
                    if (pLevel instanceof net.minecraft.server.level.ServerLevel serverLevel) {
                        for (int particleCount = 0; particleCount < 20; particleCount++) {
                            double offsetX = (player.getRandom().nextDouble() - 0.5) * 2.0;
                            double offsetZ = (player.getRandom().nextDouble() - 0.5) * 2.0;
                            serverLevel.sendParticles(
                                net.minecraft.core.particles.ParticleTypes.CLOUD,
                                player.getX() + offsetX, player.getY(), player.getZ() + offsetZ,
                                1, 0.1, 0.1, 0.1, 0.01
                            );
                        }
                        
                        // 25% chance to create a wind gust that affects nearby entities
                        if (player.getRandom().nextFloat() < 0.25f) {
                            createWindGust(serverLevel, player);
                        }
                    }
                    
                    // Mark the trident as used
                    if (!player.hasInfiniteMaterials()) {
                        pStack.hurtAndBreak(1, player, LivingEntity.getSlotForHand(pEntityLiving.getUsedItemHand()));
                    }
                    
                    player.awardStat(Stats.ITEM_USED.get(this));
                    return;
                }
                
                // Default trident behavior (in water or without riptide)
                if (!(f > 0.0F) || player.isInWaterOrRain()) {
                    if (!isTooDamagedToUse(pStack)) {
                        Holder<SoundEvent> holder = EnchantmentHelper.pickHighestLevel(pStack, EnchantmentEffectComponents.TRIDENT_SOUND).orElse(SoundEvents.TRIDENT_THROW);
                        if (!pLevel.isClientSide) {
                            pStack.hurtAndBreak(1, player, LivingEntity.getSlotForHand(pEntityLiving.getUsedItemHand()));
                            if (f == 0.0F) {
                                WindTridentEntity windTridentEntity = new WindTridentEntity(pLevel, player, pStack);
                                windTridentEntity.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 3.2F, 0.5F);
                                if (player.hasInfiniteMaterials()) {
                                    windTridentEntity.pickup = AbstractArrow.Pickup.CREATIVE_ONLY;
                                }

                                pLevel.addFreshEntity(windTridentEntity);
                                pLevel.playSound(null, windTridentEntity, holder.value(), SoundSource.PLAYERS, 1.0F, 1.0F);
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
    
    @Override
    public net.minecraft.world.InteractionResultHolder<ItemStack> use(net.minecraft.world.level.Level pLevel, net.minecraft.world.entity.player.Player pPlayer, net.minecraft.world.InteractionHand pHand) {
        ItemStack itemstack = pPlayer.getItemInHand(pHand);
        if (isTooDamagedToUse(itemstack)) {
            return net.minecraft.world.InteractionResultHolder.fail(itemstack);
        } else {
            // Allow riptide on land for wind trident (override vanilla behavior)
            pPlayer.startUsingItem(pHand);
            return net.minecraft.world.InteractionResultHolder.consume(itemstack);
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
        WindTridentEntity windTridentEntity = new WindTridentEntity(pLevel, pPos.x(), pPos.y(), pPos.z(), pStack.copyWithCount(1));
        windTridentEntity.pickup = AbstractArrow.Pickup.ALLOWED;
        return windTridentEntity;
    }
    
    private void createWindGust(net.minecraft.server.level.ServerLevel level, Player player) {
        // Create a wind gust that affects nearby entities
        double gustRadius = 3.0;
        net.minecraft.world.phys.AABB gustArea = new net.minecraft.world.phys.AABB(
            player.getX() - gustRadius, player.getY() - 1, player.getZ() - gustRadius,
            player.getX() + gustRadius, player.getY() + 3, player.getZ() + gustRadius
        );
        
        for (net.minecraft.world.entity.Entity entity : level.getEntities(player, gustArea)) {
            if (entity instanceof net.minecraft.world.entity.LivingEntity livingEntity && entity != player) {
                // Apply small knockback
                net.minecraft.world.phys.Vec3 gustDirection = entity.position().subtract(player.position()).normalize();
                double gustStrength = 0.5;
                
                net.minecraft.world.phys.Vec3 gustVector = new net.minecraft.world.phys.Vec3(
                    gustDirection.x * gustStrength,
                    0.2, // Small upward push
                    gustDirection.z * gustStrength
                );
                
                livingEntity.setDeltaMovement(livingEntity.getDeltaMovement().add(gustVector));
                
                // Add brief slow falling effect
                livingEntity.addEffect(new net.minecraft.world.effect.MobEffectInstance(
                    net.minecraft.world.effect.MobEffects.SLOW_FALLING, 40, 0, false, false, true
                ));
            }
        }
        
        // Play wind gust sound
        level.playSound(null, player, net.minecraft.sounds.SoundEvents.WEATHER_RAIN, 
            net.minecraft.sounds.SoundSource.AMBIENT, 0.3F, 1.2F);
    }
}

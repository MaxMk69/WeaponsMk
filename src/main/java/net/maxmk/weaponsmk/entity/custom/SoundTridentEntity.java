package net.maxmk.weaponsmk.entity.custom;

import net.maxmk.weaponsmk.entity.ModEntities;
import net.maxmk.weaponsmk.item.ModItems;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.AABB;

import javax.annotation.Nullable;

public class SoundTridentEntity extends AbstractArrow {
    private static final EntityDataAccessor<Byte> ID_LOYALTY = SynchedEntityData.defineId(SoundTridentEntity.class, EntityDataSerializers.BYTE);
    private static final EntityDataAccessor<Boolean> ID_FOIL = SynchedEntityData.defineId(SoundTridentEntity.class, EntityDataSerializers.BOOLEAN);
    private boolean dealtDamage;
    public int clientSideReturnTridentTickCount;
    private static final int MAX_PIERCE = 2;
    private int piercedEntitiesCount = 0;

    public SoundTridentEntity(EntityType<? extends SoundTridentEntity> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public SoundTridentEntity(Level pLevel, LivingEntity pShooter, ItemStack pPickupItemStack) {
        super(ModEntities.SOUND_TRIDENT.get(), pShooter, pLevel, pPickupItemStack, null);
        this.entityData.set(ID_LOYALTY, this.getLoyaltyFromItem(pPickupItemStack));
        this.entityData.set(ID_FOIL, pPickupItemStack.hasFoil());
    }

    public SoundTridentEntity(Level pLevel, double pX, double pY, double pZ, ItemStack pPickupItemStack) {
        super(ModEntities.SOUND_TRIDENT.get(), pX, pY, pZ, pLevel, pPickupItemStack, pPickupItemStack);
        this.entityData.set(ID_LOYALTY, this.getLoyaltyFromItem(pPickupItemStack));
        this.entityData.set(ID_FOIL, pPickupItemStack.hasFoil());
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder pBuilder) {
        super.defineSynchedData(pBuilder);
        pBuilder.define(ID_LOYALTY, (byte)0);
        pBuilder.define(ID_FOIL, false);
    }

    @Override
    public void tick() {
        // Set dealtDamage if we've been in ground for 4+ ticks AND pierced MORE than max enemies
        if (this.inGroundTime > 4 && this.piercedEntitiesCount > MAX_PIERCE) {
            this.dealtDamage = true;
        }

        // Additional entity detection for closely spaced enemies (only if vanilla system hasn't found anything)
        if (!this.dealtDamage && this.piercedEntitiesCount < MAX_PIERCE) {
            this.checkForCloseEntities();
        }

        Entity entity = this.getOwner();
        int i = this.entityData.get(ID_LOYALTY);
        if (i > 0 && (this.dealtDamage || this.isNoPhysics()) && entity != null) {
            if (!this.isAcceptibleReturnOwner()) {
                if (!this.level().isClientSide && this.pickup == Pickup.ALLOWED) {
                    this.spawnAtLocation(this.getPickupItem(), 0.1F);
                }

                this.discard();
            } else {
                this.setNoPhysics(true);
                Vec3 vec3 = entity.getEyePosition().subtract(this.position());
                this.setPosRaw(this.getX(), this.getY() + vec3.y * 0.015 * (double)i, this.getZ());
                if (this.level().isClientSide) {
                    this.yOld = this.getY();
                }

                double d0 = 0.05 * (double)i;
                this.setDeltaMovement(this.getDeltaMovement().scale(0.95).add(vec3.normalize().scale(d0)));
                if (this.clientSideReturnTridentTickCount == 0) {
                    this.playSound(SoundEvents.TRIDENT_RETURN, 10.0F, 1.0F);
                }

                this.clientSideReturnTridentTickCount++;
            }
        }

        super.tick();
    }

    private boolean isAcceptibleReturnOwner() {
        Entity entity = this.getOwner();
        return entity == null || !entity.isAlive() ? false : !(entity instanceof ServerPlayer) || !entity.isSpectator();
    }

    /**
     * Checks for entities that might have been missed by the vanilla collision system
     * when enemies are very close together.
     */
    private void checkForCloseEntities() {
        if (this.level().isClientSide) return;
        
        Vec3 currentPos = this.position();
        Vec3 movement = this.getDeltaMovement();
        
        // Check more frequently and with larger radius for closely spaced enemies
        double searchRadius = 1.5; // Increased to 1.5 block radius
        
        for (Entity entity : this.level().getEntities(this, 
                new AABB(currentPos.x - searchRadius, currentPos.y - searchRadius, currentPos.z - searchRadius,
                         currentPos.x + searchRadius, currentPos.y + searchRadius, currentPos.z + searchRadius))) {
            
            // Skip if we've already hit this entity or if it's not a valid target
            if (entity == this.getOwner() || !entity.isAlive() || entity.isRemoved() || 
                !(entity instanceof LivingEntity) || entity.getType() == EntityType.ENDERMAN) {
                continue;
            }
            
            // More aggressive distance checking - check if entity is within 1.5 blocks
            if (this.canHitEntity(entity) && entity.distanceToSqr(this) < 2.25) { // 1.5^2 = 2.25
                // Create a fake EntityHitResult and call onHitEntity
                EntityHitResult hitResult = new EntityHitResult(entity);
                this.onHitEntity(hitResult);
                
                if (this.dealtDamage || this.piercedEntitiesCount >= MAX_PIERCE) {
                    break; // Stop checking if we've hit our limit
                }
            }
        }
    }



    public boolean isFoil() {
        return this.entityData.get(ID_FOIL);
    }

    @Nullable
    @Override
    protected EntityHitResult findHitEntity(Vec3 pStartVec, Vec3 pEndVec) {
        // Stop finding entities if we've already dealt damage
        if (this.dealtDamage) {
            return null;
        }
        
        return super.findHitEntity(pStartVec, pEndVec);
    }



    @Override
    protected void onHitEntity(EntityHitResult pResult) {
        Entity entity = pResult.getEntity();
        float f = 11.0F;
        Entity owner = this.getOwner();
        
        // Use sonic boom damage type that bypasses armor and shields (like Warden's sonic boom)
        DamageSource customDamageSource = this.damageSources().sonicBoom(this);

        if (this.level() instanceof ServerLevel serverlevel) {
            f = EnchantmentHelper.modifyDamage(serverlevel, this.getWeaponItem(), entity, customDamageSource, f);
        }

        int powerLevel = EnchantmentHelper.getItemEnchantmentLevel(
                this.level().registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.POWER),
                this.getWeaponItem()
        );
        if (powerLevel > 0) {
            f += 0.5F * (powerLevel + 1);
        }

        int riptide = EnchantmentHelper.getItemEnchantmentLevel(
                this.level().registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.RIPTIDE),
                this.getWeaponItem());

        int sharpnessLevel = EnchantmentHelper.getItemEnchantmentLevel(
                this.level().registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.SHARPNESS),
                this.getWeaponItem()
        );
        if (sharpnessLevel > 0 && riptide == 0) {
            f -= 0.5F * (sharpnessLevel + 1);
        } else {
            f += 0F;
        }
        
        if (entity.hurt(customDamageSource, f)) {
            if (entity.getType() == EntityType.ENDERMAN) {
                return;
            }

            if (this.level() instanceof ServerLevel serverlevel1) {
                EnchantmentHelper.doPostAttackEffectsWithItemSource(serverlevel1, entity, customDamageSource, this.getWeaponItem());
            }

            if (entity instanceof LivingEntity livingentity) {
                this.doKnockback(livingentity, customDamageSource);
                this.doPostHurtEffects(livingentity);
            }
        }

        this.piercedEntitiesCount++;
        
        if (this.piercedEntitiesCount > MAX_PIERCE) {
            // We've hit MORE than MAX_PIERCE enemies - behave like vanilla trident (bounce and stick to ground)
            this.setDeltaMovement(this.getDeltaMovement().multiply(-0.01, -0.1, -0.01));
            this.playSound(SoundEvents.TRIDENT_HIT, 1.0F, 1.0F);
            // Set dealtDamage = true after hitting the 4th enemy
            this.dealtDamage = true;
        } else {
            // Speed reduction for realism after each pierce
            double speedReduction = 0.80; // Reduce by 20% each time for noticeable realism
            this.setDeltaMovement(this.getDeltaMovement().multiply(speedReduction, speedReduction, speedReduction));
        }
    }

    @Override
    protected void hitBlockEnchantmentEffects(ServerLevel pLevel, BlockHitResult pHitResult, ItemStack pStack) {
        Vec3 vec3 = pHitResult.getBlockPos().clampLocationWithin(pHitResult.getLocation());
        EnchantmentHelper.onHitBlock(
                pLevel,
                pStack,
                this.getOwner() instanceof LivingEntity livingentity ? livingentity : null,
                this,
                null,
                vec3,
                pLevel.getBlockState(pHitResult.getBlockPos()),
                p_343806_ -> this.kill()
        );
    }

    @Override
    public ItemStack getWeaponItem() {
        return this.getPickupItemStackOrigin();
    }

    @Override
    protected boolean tryPickup(Player pPlayer) {
        return super.tryPickup(pPlayer) || this.isNoPhysics() && this.ownedBy(pPlayer) && pPlayer.getInventory().add(this.getPickupItem());
    }

    @Override
    protected ItemStack getDefaultPickupItem() {
        return new ItemStack(ModItems.SOUND_TRIDENT.get());
    }

    @Override
    protected SoundEvent getDefaultHitGroundSoundEvent() {
        return SoundEvents.TRIDENT_HIT_GROUND;
    }

    @Override
    public void playerTouch(Player pEntity) {
        if (this.ownedBy(pEntity) || this.getOwner() == null) {
            super.playerTouch(pEntity);
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        this.dealtDamage = pCompound.getBoolean("DealtDamage");
        this.entityData.set(ID_LOYALTY, this.getLoyaltyFromItem(this.getPickupItemStackOrigin()));
    }

    @Override
    public void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        pCompound.putBoolean("DealtDamage", this.dealtDamage);
    }

    private byte getLoyaltyFromItem(ItemStack pStack) {
        return this.level() instanceof ServerLevel serverlevel ? (byte) Mth.clamp(EnchantmentHelper
                .getTridentReturnToOwnerAcceleration(serverlevel, pStack, this), 0, 127) : 0;
    }

    @Override
    public void tickDespawn() {
        int i = this.entityData.get(ID_LOYALTY);
        if (this.pickup != Pickup.ALLOWED || i <= 0) {
            super.tickDespawn();
        }
    }

    @Override
    protected float getWaterInertia() {
        return 1F;
    }

    @Override
    public boolean shouldRender(double pX, double pY, double pZ) {
        return true;
    }
}

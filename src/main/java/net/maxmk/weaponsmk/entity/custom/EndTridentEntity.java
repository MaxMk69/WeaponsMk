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

import javax.annotation.Nullable;
import net.minecraft.sounds.SoundSource;

public class EndTridentEntity extends AbstractArrow {
    private static final EntityDataAccessor<Byte> ID_LOYALTY = SynchedEntityData.defineId(EndTridentEntity.class, EntityDataSerializers.BYTE);
    private static final EntityDataAccessor<Boolean> ID_FOIL = SynchedEntityData.defineId(EndTridentEntity.class, EntityDataSerializers.BOOLEAN);
    private boolean dealtDamage;
    public int clientSideReturnTridentTickCount;

    public EndTridentEntity(EntityType<? extends EndTridentEntity> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public EndTridentEntity(Level pLevel, LivingEntity pShooter, ItemStack pPickupItemStack) {
        super(ModEntities.END_TRIDENT.get(), pShooter, pLevel, pPickupItemStack, null);
        this.entityData.set(ID_LOYALTY, this.getLoyaltyFromItem(pPickupItemStack));
        this.entityData.set(ID_FOIL, pPickupItemStack.hasFoil());
    }

    public EndTridentEntity(Level pLevel, double pX, double pY, double pZ, ItemStack pPickupItemStack) {
        super(ModEntities.END_TRIDENT.get(), pX, pY, pZ, pLevel, pPickupItemStack, pPickupItemStack);
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
        if (this.inGroundTime > 4) {
            this.dealtDamage = true;
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

    public boolean isFoil() {
        return this.entityData.get(ID_FOIL);
    }

    @Nullable
    @Override
    protected EntityHitResult findHitEntity(Vec3 pStartVec, Vec3 pEndVec) {
        return this.dealtDamage ? null : super.findHitEntity(pStartVec, pEndVec);
    }

    @Override
    protected void onHitEntity(EntityHitResult pResult) {
        Entity entity = pResult.getEntity();
        super.onHitEntity(pResult);
        float f = 11.0F;
        Entity owner = this.getOwner();
        DamageSource damagesource = this.damageSources().trident(this, owner == null ? this : owner);

        if (this.level() instanceof ServerLevel serverlevel) {
            f = EnchantmentHelper.modifyDamage(serverlevel, this.getWeaponItem(), entity, damagesource, f);
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

        this.dealtDamage = true;

                 if (entity.hurt(damagesource, f)) {
             if (entity.getType() == EntityType.ENDERMAN) {
                 return;
             }
 
             ServerLevel serverLevel = null;
             if (this.level() instanceof ServerLevel) {
                 serverLevel = (ServerLevel) this.level();
                 EnchantmentHelper.doPostAttackEffectsWithItemSource(serverLevel, entity, damagesource, this.getWeaponItem());
             }
 
             if (entity instanceof LivingEntity livingentity) {
                 this.doKnockback(livingentity, damagesource);
                 this.doPostHurtEffects(livingentity);
                 
                 // End Trident Special Abilities
                 if (serverLevel != null) {
                     this.applyEndTridentEffects(livingentity, serverLevel);
                 }
             }
         }
 
         // Don't mark as dealt damage immediately for End Trident to allow multiple hits
         // this.dealtDamage = true; // Commented out to prevent trident from disappearing
         
         this.setDeltaMovement(this.getDeltaMovement().multiply(-0.01, -0.1, -0.01));
         this.playSound(SoundEvents.TRIDENT_HIT, 1.0F, 1.0F);
         
         // Check if this trident should teleport the player
         if (this.level() instanceof ServerLevel serverLevel) {
             this.checkAndExecuteTeleport(serverLevel, entity.position());
         }
    }
    
    private void applyEndTridentEffects(LivingEntity target, ServerLevel level) {
        // 1. Create Dragon's Breath Cloud Effect (enemies stay in place for full visual impact)
        this.createDragonsBreathCloud(target.position(), level);
        
        // 2. Play end-themed sound
        level.playSound(null, target, SoundEvents.ENDERMAN_TELEPORT, SoundSource.NEUTRAL, 0.5F, 1.2F);
    }
    
    private void createDragonsBreathCloud(Vec3 position, ServerLevel level) {
        // Create a persistent damaging cloud that deals damage over time (like Ender Dragon)
        // The visual effects are now handled entirely by the cloud entity for perfect sync
        this.createPersistentDamagingCloud(position, level);
    }
    
         private void createPersistentDamagingCloud(Vec3 position, ServerLevel level) {
         // Create a persistent damaging cloud entity that deals damage over time
         DragonBreathCloud cloud = new DragonBreathCloud(level, position, this.getOwner());
         boolean success = level.addFreshEntity(cloud);
         if (!success) {
             System.out.println("Failed to spawn DragonBreathCloud entity");
         } else {
             System.out.println("Successfully spawned DragonBreathCloud at " + position);
         }
     }
    
         // Custom entity class for the dragon's breath cloud
     private static class DragonBreathCloud extends net.minecraft.world.entity.Entity {
         private final Vec3 cloudPosition;
         private final Entity owner;
         private int age = 0;
         private final int maxAge = 60; // 3 seconds (60 ticks)
         
         public DragonBreathCloud(Level level, Vec3 position, Entity owner) {
             super(EntityType.ARROW, level); // Use a valid entity type
             this.cloudPosition = position;
             this.owner = owner;
             this.setPos(position.x, position.y, position.z);
             this.setNoGravity(true);
             this.setInvulnerable(true);
             this.setInvisible(false); // Make visible so we can see the particles
             this.setSilent(true); // No sounds
         }
        
        @Override
        protected void defineSynchedData(net.minecraft.network.syncher.SynchedEntityData.Builder pBuilder) {}
        
        @Override
        protected void readAdditionalSaveData(net.minecraft.nbt.CompoundTag pCompound) {}
        
        @Override
        protected void addAdditionalSaveData(net.minecraft.nbt.CompoundTag pCompound) {}
        
                 @Override
         public void tick() {
             super.tick();
             age++;
             
             if (age >= maxAge) {
                 System.out.println("DragonBreathCloud expired after " + maxAge + " ticks");
                 this.discard();
                 return;
             }
             
             // Debug output every 20 ticks
             if (age % 20 == 0) {
                 System.out.println("DragonBreathCloud tick " + age + "/" + maxAge + " at " + cloudPosition);
             }
            
                         // Deal damage every 20 ticks (1 second) to entities in the cloud
             if (age % 20 == 0) {
                 double damageRadius = 2.5;
                 net.minecraft.world.phys.AABB damageArea = new net.minecraft.world.phys.AABB(
                     cloudPosition.x - damageRadius, cloudPosition.y - 1, cloudPosition.z - damageRadius,
                     cloudPosition.x + damageRadius, cloudPosition.y + 2, cloudPosition.z + damageRadius
                 );
                 
                 for (net.minecraft.world.entity.Entity entity : this.level().getEntities(null, damageArea)) {
                     if (entity instanceof net.minecraft.world.entity.LivingEntity livingEntity && 
                         entity != owner && entity != this) { // Don't damage owner or self
                         
                         // Deal persistent dragon's breath damage over time
                         livingEntity.hurt(this.level().damageSources().dragonBreath(), 1.5F); // 0.75 hearts per second
                         
                         // Add poison effect every 2 seconds
                         if (age % 40 == 0) {
                             livingEntity.addEffect(new net.minecraft.world.effect.MobEffectInstance(
                                 net.minecraft.world.effect.MobEffects.POISON, 60, 0, false, false, true
                             ));
                         }
                     }
                 }
             }
            
                         // Spawn particles every 3 ticks for a more consistent visual effect
             if (age % 3 == 0) {
                 // Main dragon's breath particles - more frequent and visible
                 for (int i = 0; i < 15; i++) { // Increased from 12 to 15
                     double offsetX = (this.level().getRandom().nextDouble() - 0.5) * 2.5; // Reduced spread for better visibility
                     double offsetY = this.level().getRandom().nextDouble() * 1.5; // Reduced height spread
                     double offsetZ = (this.level().getRandom().nextDouble() - 0.5) * 2.5; // Reduced spread
                     
                     // Use sendParticles for better server-side particle spawning
                     if (this.level() instanceof net.minecraft.server.level.ServerLevel serverLevel) {
                         serverLevel.sendParticles(
                             net.minecraft.core.particles.ParticleTypes.DRAGON_BREATH,
                             cloudPosition.x + offsetX, cloudPosition.y + offsetY, cloudPosition.z + offsetZ,
                             1, 0.0, 0.0, 0.0, 0.01
                         );
                     }
                 }
                 
                 // End portal particles for extra effect - also more frequent
                 for (int i = 0; i < 8; i++) { // Increased from 6 to 8
                     double offsetX = (this.level().getRandom().nextDouble() - 0.5) * 2.0; // Reduced spread
                     double offsetY = this.level().getRandom().nextDouble() * 1.0; // Reduced height spread
                     double offsetZ = (this.level().getRandom().nextDouble() - 0.5) * 2.0; // Reduced spread
                     
                     // Use sendParticles for better server-side particle spawning
                     if (this.level() instanceof net.minecraft.server.level.ServerLevel serverLevel) {
                         serverLevel.sendParticles(
                             net.minecraft.core.particles.ParticleTypes.PORTAL,
                             cloudPosition.x + offsetX, cloudPosition.y + offsetY, cloudPosition.z + offsetZ,
                             1, 0.0, 0.0, 0.0, 0.01
                         );
                     }
                 }
                 
                 // Debug: Log particle spawning
                 if (age % 30 == 0) { // Every 1.5 seconds
                     System.out.println("DragonBreathCloud spawned particles at tick " + age);
                 }
             }
            
                         // Create initial burst of particles when the cloud first appears
             if (age == 1) {
                 if (this.level() instanceof net.minecraft.server.level.ServerLevel serverLevel) {
                     // Initial burst of dragon's breath particles
                     for (int i = 0; i < 30; i++) { // Increased from 25 to 30
                         double offsetX = (this.level().getRandom().nextDouble() - 0.5) * 3.0; // Reduced spread
                         double offsetY = this.level().getRandom().nextDouble() * 2.0; // Reduced height spread
                         double offsetZ = (this.level().getRandom().nextDouble() - 0.5) * 3.0; // Reduced spread
                         
                         serverLevel.sendParticles(
                             net.minecraft.core.particles.ParticleTypes.DRAGON_BREATH,
                             cloudPosition.x + offsetX, cloudPosition.y + offsetY, cloudPosition.z + offsetZ,
                             1, 0.0, 0.0, 0.0, 0.01
                         );
                     }
                     
                     // Initial burst of portal particles
                     for (int i = 0; i < 20; i++) { // Increased from 15 to 20
                         double offsetX = (this.level().getRandom().nextDouble() - 0.5) * 2.5; // Reduced spread
                         double offsetY = this.level().getRandom().nextDouble() * 1.5; // Reduced height spread
                         double offsetZ = (this.level().getRandom().nextDouble() - 0.5) * 2.5; // Reduced spread
                         
                         serverLevel.sendParticles(
                             net.minecraft.core.particles.ParticleTypes.PORTAL,
                             cloudPosition.x + offsetX, cloudPosition.y + offsetY, cloudPosition.z + offsetZ,
                             1, 0.0, 0.0, 0.0, 0.01
                         );
                     }
                     
                     System.out.println("DragonBreathCloud created initial burst of particles");
                 }
             }
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
        
        // Check if this trident should teleport the player
        this.checkAndExecuteTeleport(pLevel, vec3);
    }
    
    private void checkAndExecuteTeleport(ServerLevel level, Vec3 hitPosition) {
        // Check if this trident was thrown while sneaking
        if (this.getPersistentData().getBoolean("EndTridentTeleport")) {
            // Get the player who should be teleported
            java.util.UUID playerUUID = this.getPersistentData().getUUID("TeleportPlayer");
            Player player = level.getPlayerByUUID(playerUUID);
            
            if (player != null && player.isAlive()) {
                // Calculate safe teleport position (slightly above the hit location)
                Vec3 teleportPos = new Vec3(
                    hitPosition.x,
                    hitPosition.y + 1.0, // Teleport 1 block above to avoid suffocation
                    hitPosition.z
                );
                
                // Teleport the player
                player.teleportTo(teleportPos.x, teleportPos.y, teleportPos.z);
                
                // Add end teleport particles at both locations
                this.createEndTeleportParticles(level, player.position());
                this.createEndTeleportParticles(level, teleportPos);
                
                // Play end teleport sound
                level.playSound(null, teleportPos.x, teleportPos.y, teleportPos.z, 
                    SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 1.0F, 1.0F);
                
                // Clear the teleport flag
                this.getPersistentData().remove("EndTridentTeleport");
                this.getPersistentData().remove("TeleportPlayer");
                
                System.out.println("End Trident: Player teleported to " + teleportPos);
            }
        }
    }
    
    private void createEndTeleportParticles(ServerLevel level, Vec3 position) {
        // Create end teleport particles
        for (int i = 0; i < 30; i++) {
            double offsetX = (level.getRandom().nextDouble() - 0.5) * 2.0;
            double offsetY = level.getRandom().nextDouble() * 2.0;
            double offsetZ = (level.getRandom().nextDouble() - 0.5) * 2.0;
            
            level.sendParticles(
                net.minecraft.core.particles.ParticleTypes.PORTAL,
                position.x + offsetX, position.y + offsetY, position.z + offsetZ,
                1, 0.0, 0.0, 0.0, 0.01
            );
        }
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
        return new ItemStack(ModItems.END_TRIDENT.get());
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

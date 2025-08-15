package net.maxmk.weaponsmk.event;

import net.maxmk.weaponsmk.item.ModItems;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.maxmk.weaponsmk.WeaponsMk;

@Mod.EventBusSubscriber(modid = WeaponsMk.MOD_ID)
public class PlayerEvents {
    
    @SubscribeEvent
    public static void onPlayerTick(net.minecraftforge.event.TickEvent.PlayerTickEvent event) {
        Player player = event.player;
        
        // Only run on server side
        if (player.level().isClientSide) {
            return;
        }
        
        // Check if player has Holy Trident in main hand
        ItemStack mainHand = player.getMainHandItem();
        boolean hasHolyTrident = mainHand.is(ModItems.HOLY_TRIDENT.get());
        
        // Check if player currently has resistance effect from us
        boolean hasResistanceEffect = player.hasEffect(MobEffects.DAMAGE_RESISTANCE);
        
        if (hasHolyTrident && !hasResistanceEffect) {
            // Apply resistance 1 effect when holding Holy Trident (20% damage reduction)
            player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, -1, 0, false, false, true));
            System.out.println("Holy Trident: Applied resistance effect");
        } else if (!hasHolyTrident && hasResistanceEffect) {
            // Remove resistance effect when not holding Holy Trident
            player.removeEffect(MobEffects.DAMAGE_RESISTANCE);
            System.out.println("Holy Trident: Removed resistance effect");
        }
        
        // Wind Trident ground slam effect
        boolean hasWindTrident = mainHand.is(ModItems.WIND_TRIDENT.get());
        if (hasWindTrident) {
            boolean wasLeaping = player.getPersistentData().getBoolean("WindTridentLeaping");
            boolean isOnGround = player.onGround();
            
            // Check if player just landed after wind leap
            if (wasLeaping && isOnGround) {
                // Clear the leaping flag
                player.getPersistentData().remove("WindTridentLeaping");
                
                // Perform ground slam effect
                performWindGroundSlam(player);
            }
        }
    }
    
    @SubscribeEvent
    public static void onLivingFall(net.minecraftforge.event.entity.living.LivingFallEvent event) {
        if (event.getEntity() instanceof Player player) {
            // Check if player has Wind Trident in main hand
            ItemStack mainHand = player.getMainHandItem();
            if (mainHand.is(ModItems.WIND_TRIDENT.get())) {
                // Get the fall distance
                float fallDistance = event.getDistance();
                
                // Wind Trident reduces fall damage threshold from 4 to 5 blocks
                // This stacks with Feather Falling enchantment
                float windTridentBonus = 1.0f; // +1 block threshold
                
                // Get Feather Falling level from boots
                ItemStack boots = player.getItemBySlot(net.minecraft.world.entity.EquipmentSlot.FEET);
                int featherFallingLevel = EnchantmentHelper.getItemEnchantmentLevel(
                    player.level().registryAccess().lookupOrThrow(net.minecraft.core.registries.Registries.ENCHANTMENT).getOrThrow(Enchantments.FEATHER_FALLING),
                    boots);
                
                // Feather Falling provides additional protection
                // Level 1: +1 block, Level 2: +2 blocks, Level 3: +3 blocks, Level 4: +4 blocks
                float featherFallingBonus = featherFallingLevel;
                
                // Total fall damage threshold bonus
                float totalBonus = windTridentBonus + featherFallingBonus;
                
                // Apply the bonus to fall distance
                float adjustedFallDistance = fallDistance - totalBonus;
                
                // If adjusted fall distance is below 4, no damage
                if (adjustedFallDistance < 4.0f) {
                    event.setCanceled(true);
                    System.out.println("Wind Trident: Fall damage prevented! Fall distance: " + fallDistance + 
                        ", Adjusted: " + adjustedFallDistance + ", Total bonus: " + totalBonus);
                } else {
                    // Reduce the fall distance for damage calculation
                    event.setDistance(adjustedFallDistance);
                    System.out.println("Wind Trident: Fall damage reduced! Fall distance: " + fallDistance + 
                        ", Adjusted: " + adjustedFallDistance + ", Total bonus: " + totalBonus);
                }
            }
        }
    }
    
    private static void performWindGroundSlam(Player player) {
        // Ground slam radius (2 blocks)
        double radius = 2.0;
        
        // Get all entities in radius
        AABB area = new AABB(
            player.getX() - radius, player.getY() - 1, player.getZ() - radius,
            player.getX() + radius, player.getY() + 2, player.getZ() + radius
        );
        
        // Find nearby living entities (excluding the player)
        for (Entity entity : player.level().getEntities(player, area)) {
            if (entity instanceof LivingEntity livingEntity && entity != player) {
                // Apply damage (8 HP = 4 hearts)
                livingEntity.hurt(player.damageSources().playerAttack(player), 8.0F);
                
                // Apply knockback and upward launch
                Vec3 knockbackDirection = entity.position().subtract(player.position()).normalize();
                double knockbackStrength = 0.8; // Reduced from 1.5 to 0.8 for less aggressive knockback
                double upwardForce = 0.6; // Reduced from 1.0 to 0.6 for gentler upward movement
                
                Vec3 knockbackVector = new Vec3(
                    knockbackDirection.x * knockbackStrength,
                    upwardForce,
                    knockbackDirection.z * knockbackStrength
                );
                
                livingEntity.setDeltaMovement(livingEntity.getDeltaMovement().add(knockbackVector));
                
                // Add brief levitation effect for smoother knockback
                livingEntity.addEffect(new MobEffectInstance(MobEffects.LEVITATION, 15, 0, false, false, true)); // Reduced duration from 20 to 15 ticks
            }
        }
        
        // Add ground slam particles
        if (player.level() instanceof net.minecraft.server.level.ServerLevel serverLevel) {
            for (int i = 0; i < 30; i++) {
                double angle = (i / 30.0) * 2 * Math.PI;
                double distance = player.getRandom().nextDouble() * radius;
                double x = player.getX() + Math.cos(angle) * distance;
                double z = player.getZ() + Math.sin(angle) * distance;
                serverLevel.sendParticles(
                    net.minecraft.core.particles.ParticleTypes.CLOUD,
                    x, player.getY() + 0.1, z,
                    1, 0.0, 0.1, 0.0, 0.05
                );
            }
        }
        
        System.out.println("Wind Trident: Ground slam effect triggered!");
    }
}


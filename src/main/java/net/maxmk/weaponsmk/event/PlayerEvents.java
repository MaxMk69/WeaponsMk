package net.maxmk.weaponsmk.event;

import net.maxmk.weaponsmk.item.ModItems;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
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
    }
}


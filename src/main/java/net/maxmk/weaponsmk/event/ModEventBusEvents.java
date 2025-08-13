package net.maxmk.weaponsmk.event;

import net.maxmk.weaponsmk.WeaponsMk;
import net.maxmk.weaponsmk.entity.client.model.UnifiedTridentModel;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = WeaponsMk.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModEventBusEvents {
    @SubscribeEvent
    public static void registerLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {
        // Register only the unified trident model layer
        event.registerLayerDefinition(UnifiedTridentModel.LAYER_LOCATION, UnifiedTridentModel::createBodyLayer);
    }
}

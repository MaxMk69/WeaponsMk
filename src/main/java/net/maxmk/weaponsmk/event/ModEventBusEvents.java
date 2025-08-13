package net.maxmk.weaponsmk.event;

import net.maxmk.weaponsmk.WeaponsMk;
import net.maxmk.weaponsmk.entity.client.model.*;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = WeaponsMk.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModEventBusEvents {
    @SubscribeEvent
    public static void registerLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(NetheriteTridentModel.LAYER_LOCATION, NetheriteTridentModel::createBodyLayer);
        event.registerLayerDefinition(NetherTridentModel.LAYER_LOCATION, NetherTridentModel::createBodyLayer);
        event.registerLayerDefinition(NatureTridentModel.LAYER_LOCATION, NatureTridentModel::createBodyLayer);
        event.registerLayerDefinition(FrostTridentModel.LAYER_LOCATION, FrostTridentModel::createBodyLayer);
        event.registerLayerDefinition(ShadowTridentModel.LAYER_LOCATION, ShadowTridentModel::createBodyLayer);
        event.registerLayerDefinition(HolyTridentModel.LAYER_LOCATION, HolyTridentModel::createBodyLayer);
        event.registerLayerDefinition(SoundTridentModel.LAYER_LOCATION, SoundTridentModel::createBodyLayer);
    }
}

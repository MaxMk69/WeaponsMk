package net.maxmk.weaponsmk;

import com.mojang.logging.LogUtils;
import net.maxmk.weaponsmk.entity.ModEntities;
import net.maxmk.weaponsmk.entity.client.renderer.*;
import net.maxmk.weaponsmk.item.ModItems;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(WeaponsMk.MOD_ID)
public class WeaponsMk {
    public static final String MOD_ID = "weaponsmk";

    private static final Logger LOGGER = LogUtils.getLogger();

    public WeaponsMk(FMLJavaModLoadingContext context) {
        IEventBus modEventBus = context.getModEventBus();
        MinecraftForge.EVENT_BUS.register(this);

        ModItems.register(modEventBus);
        ModEntities.register(modEventBus);

        modEventBus.addListener(this::addCreative);

        context.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if(event.getTabKey() == CreativeModeTabs.COMBAT) {
            event.accept(ModItems.NETHERITE_TRIDENT);
            event.accept(ModItems.NETHER_TRIDENT);
            event.accept(ModItems.NATURE_TRIDENT);
            event.accept(ModItems.FROST_TRIDENT);
            event.accept(ModItems.SHADOW_TRIDENT);
            event.accept(ModItems.HOLY_TRIDENT);
        }
    }

    @Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            EntityRenderers.register(ModEntities.HOLY_TRIDENT.get(), HolyTridentRenderer::new);
            EntityRenderers.register(ModEntities.SHADOW_TRIDENT.get(), ShadowTridentRenderer::new);
            EntityRenderers.register(ModEntities.NETHERITE_TRIDENT.get(), NetheriteTridentRenderer::new);
            EntityRenderers.register(ModEntities.NETHER_TRIDENT.get(), NetherTridentRenderer::new);
            EntityRenderers.register(ModEntities.NATURE_TRIDENT.get(), NatureTridentRenderer::new);
            EntityRenderers.register(ModEntities.FROST_TRIDENT.get(), FrostTridentRenderer::new);
            EntityRenderers.register(ModEntities.SOUND_TRIDENT.get(), SoundTridentRenderer::new);
        }
    }
}

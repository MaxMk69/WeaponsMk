package net.maxmk.weaponsmk.item;

import net.maxmk.weaponsmk.WeaponsMk;
import net.maxmk.weaponsmk.item.custom.*;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TridentItem;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, WeaponsMk.MOD_ID);

    public static final RegistryObject<Item> NETHERITE_TRIDENT =
            ITEMS.register("netherite_trident",
            () -> new NetheriteTrident(new Item.Properties()
                    .rarity(Rarity.EPIC)
                    .durability(500)
                    .fireResistant()
                    .attributes(NetheriteTrident.createAttributes())
                    .component(DataComponents.TOOL, NetheriteTrident.createToolProperties()))
            );

    public static final RegistryObject<Item> NETHER_TRIDENT =
            ITEMS.register("nether_trident",
                    () -> new NetherTrident(new Item.Properties()
                            .rarity(Rarity.EPIC)
                            .durability(500)
                            .fireResistant()
                            .attributes(NetherTrident.createAttributes())
                            .component(DataComponents.TOOL, NetherTrident.createToolProperties()))
            );
    public static final RegistryObject<Item> NATURE_TRIDENT =
            ITEMS.register("nature_trident",
                    () -> new NatureTrident(new Item.Properties()
                            .rarity(Rarity.EPIC)
                            .durability(500)
                            .fireResistant()
                            .attributes(NatureTrident.createAttributes())
                            .component(DataComponents.TOOL, NatureTrident.createToolProperties()))
            );

    public static final RegistryObject<Item> FROST_TRIDENT =
            ITEMS.register("frost_trident",
                    () -> new FrostTrident(new Item.Properties()
                            .rarity(Rarity.EPIC)
                            .durability(500)
                            .fireResistant()
                            .attributes(FrostTrident.createAttributes())
                            .component(DataComponents.TOOL, FrostTrident.createToolProperties()))
            );

    public static final RegistryObject<Item> SHADOW_TRIDENT =
            ITEMS.register("shadow_trident",
                    () -> new ShadowTrident(new Item.Properties()
                            .rarity(Rarity.EPIC)
                            .durability(500)
                            .fireResistant()
                            .attributes(ShadowTrident.createAttributes())
                            .component(DataComponents.TOOL, ShadowTrident.createToolProperties()))
            );

    public static final RegistryObject<Item> HOLY_TRIDENT =
            ITEMS.register("holy_trident",
                    () -> new HolyTrident(new Item.Properties()
                            .rarity(Rarity.EPIC)
                            .durability(500)
                            .fireResistant()
                            .attributes(HolyTrident.createAttributes())
                            .component(DataComponents.TOOL, HolyTrident.createToolProperties()))
            );

    public static final RegistryObject<Item> SOUND_TRIDENT =
            ITEMS.register("sound_trident",
                    () -> new SoundTrident(new Item.Properties()
                            .rarity(Rarity.EPIC)
                            .durability(500)
                            .fireResistant()
                            .attributes(SoundTrident.createAttributes())
                            .component(DataComponents.TOOL, SoundTrident.createToolProperties()))
            );

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }

}

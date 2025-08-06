package net.maxmk.weaponsmk.item;

import net.maxmk.weaponsmk.WeaponsMk;
import net.maxmk.weaponsmk.item.custom.NetheriteTrident;
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

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }

}

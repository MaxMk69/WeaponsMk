package net.maxmk.weaponsmk.entity;

import net.maxmk.weaponsmk.WeaponsMk;
import net.maxmk.weaponsmk.entity.custom.*;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, WeaponsMk.MOD_ID);

    public static final RegistryObject<EntityType<NetheriteTridentEntity>> NETHERITE_TRIDENT =
            ENTITY_TYPES.register("netherite_trident", () -> EntityType.Builder
                    .<NetheriteTridentEntity>of(NetheriteTridentEntity::new,
                    MobCategory.MISC)
                    .sized(0.5F, 0.5F)
                    .eyeHeight(0.13F)
                    .clientTrackingRange(4)
                    .updateInterval(20)
                    .fireImmune()
                    .build("netherite_trident"));

    public static final RegistryObject<EntityType<NetherTridentEntity>> NETHER_TRIDENT =
            ENTITY_TYPES.register("nether_trident", () -> EntityType.Builder
                    .<NetherTridentEntity>of(NetherTridentEntity::new,
                            MobCategory.MISC)
                    .sized(0.5F, 0.5F)
                    .eyeHeight(0.13F)
                    .clientTrackingRange(4)
                    .updateInterval(20)
                    .fireImmune()
                    .build("nether_trident"));

    public static final RegistryObject<EntityType<NatureTridentEntity>> NATURE_TRIDENT =
            ENTITY_TYPES.register("nature_trident", () -> EntityType.Builder
                    .<NatureTridentEntity>of(NatureTridentEntity::new,
                            MobCategory.MISC)
                    .sized(0.5F, 0.5F)
                    .eyeHeight(0.13F)
                    .clientTrackingRange(4)
                    .updateInterval(20)
                    .fireImmune()
                    .build("nature_trident"));

    public static final RegistryObject<EntityType<FrostTridentEntity>> FROST_TRIDENT =
            ENTITY_TYPES.register("frost_trident", () -> EntityType.Builder
                    .<FrostTridentEntity>of(FrostTridentEntity::new,
                            MobCategory.MISC)
                    .sized(0.5F, 0.5F)
                    .eyeHeight(0.13F)
                    .clientTrackingRange(4)
                    .updateInterval(20)
                    .fireImmune()
                    .build("frost_trident"));

    public static final RegistryObject<EntityType<ShadowTridentEntity>> SHADOW_TRIDENT =
            ENTITY_TYPES.register("shadow_trident", () -> EntityType.Builder
                    .<ShadowTridentEntity>of(ShadowTridentEntity::new,
                            MobCategory.MISC)
                    .sized(0.5F, 0.5F)
                    .eyeHeight(0.13F)
                    .clientTrackingRange(4)
                    .updateInterval(20)
                    .fireImmune()
                    .build("shadow_trident"));

    public static final RegistryObject<EntityType<HolyTridentEntity>> HOLY_TRIDENT =
            ENTITY_TYPES.register("holy_trident", () -> EntityType.Builder
                    .<HolyTridentEntity>of(HolyTridentEntity::new,
                            MobCategory.MISC)
                    .sized(0.5F, 0.5F)
                    .eyeHeight(0.13F)
                    .clientTrackingRange(4)
                    .updateInterval(20)
                    .fireImmune()
                    .build("holy_trident"));

    public static final RegistryObject<EntityType<SoundTridentEntity>> SOUND_TRIDENT =
            ENTITY_TYPES.register("sound_trident", () -> EntityType.Builder
                    .<SoundTridentEntity>of(SoundTridentEntity::new,
                            MobCategory.MISC)
                    .sized(0.5F, 0.5F)
                    .eyeHeight(0.13F)
                    .clientTrackingRange(4)
                    .updateInterval(20)
                    .fireImmune()
                    .build("sound_trident"));


    public static void register(IEventBus eventBus) {
        ENTITY_TYPES.register(eventBus);
    }
}

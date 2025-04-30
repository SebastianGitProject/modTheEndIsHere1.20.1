package net.yastral.theendisheremod.item;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.RecordItem;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.yastral.theendisheremod.TheEndIsHereMod;
import net.yastral.theendisheremod.entity.ModEntities;
import net.yastral.theendisheremod.item.custom.MetalDetectorItem;
import net.yastral.theendisheremod.item.custom.ShadowSummonerItem;
import net.yastral.theendisheremod.sound.ModSounds;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, TheEndIsHereMod.MOD_ID);


    public static final RegistryObject<Item> Sapphire = ITEMS.register("sapphire", () -> new Item(new Item.Properties()));


    public static final RegistryObject<Item> Raw_Sapphire = ITEMS.register("raw_sapphire", () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> Metal_Detector = ITEMS.register("metal_detector", () -> new MetalDetectorItem(new Item.Properties().durability(100)));

    public static final RegistryObject<Item> Strawberry = ITEMS.register("strawberry", () -> new Item(new Item.Properties().food(ModFoods.STRAWBERRY)));
    public static final RegistryObject<Item> SAPPHIRE_STAFF = ITEMS.register("sapphire_staff", () -> new Item(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> Rhino_Spawn_egg = ITEMS.register("rhino_spawn_egg", () -> new ForgeSpawnEggItem(ModEntities.RHINO, 0x7e9680, 0xc5d1c5, new Item.Properties()));

    //public static final RegistryObject<Item> BAR_BRAWL_MUSIC_DISC = ITEMS.register("bar_brawl_music_disc",
    //        () -> new RecordItem(6, ModSounds.BAR_BRAWL, new Item.Properties().stacksTo(1), 2440));
    public static final RegistryObject<Item> Shadow_Summoner = ITEMS.register("shadow_summoner",
            () -> new ShadowSummonerItem(new Item.Properties().stacksTo(1).durability(50)));


    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}

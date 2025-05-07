package net.yastral.theendisheremod.item;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegisterEvent;
import net.minecraftforge.registries.RegistryObject;
import net.yastral.theendisheremod.TheEndIsHereMod;
import net.yastral.theendisheremod.block.ModBlocks;

public class ModCreativeModTabs {

    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, TheEndIsHereMod.MOD_ID);


    public static final RegistryObject<CreativeModeTab> THEENDISHERE_TAB = CREATIVE_MODE_TABS.register("theendishere_tab", () -> CreativeModeTab.builder().icon(() -> new ItemStack(ModItems.Sapphire.get()))
            .title(Component.translatable("creative.theendishere_tab"))
            .displayItems((pParameters, pOutput) -> {
                pOutput.accept(ModItems.Sapphire.get());
                pOutput.accept(ModItems.Raw_Sapphire.get());
                pOutput.accept(ModBlocks.SAPPHIRE_BLOCK.get());
                pOutput.accept(ModBlocks.RAW_SAPPHIRE_BLOCK.get());


                pOutput.accept(ModItems.Rhino_Spawn_egg.get());
                //pOutput.accept(ModItems.BAR_BRAWL_MUSIC_DISC.get());
                pOutput.accept(ModItems.SAPPHIRE_STAFF.get());
                pOutput.accept(ModItems.Strawberry.get());
                pOutput.accept(ModItems.Metal_Detector.get());
                pOutput.accept(ModBlocks.SOUND_BLOCK.get());
                pOutput.accept(ModItems.Shadow_Summoner.get());

                pOutput.accept(ModBlocks.SAPPHIRE_ORE.get());
                pOutput.accept(ModBlocks.DEEPSLATE_SAPPHIRE_ORE.get());
                pOutput.accept(ModBlocks.NETHER_SAPPHIRE_ORE.get());
                pOutput.accept(ModBlocks.END_STONE_SAPPHIRE_ORE.get());
                pOutput.accept(ModBlocks.MOD_PORTAL_VOID.get());
                pOutput.accept(Items.DIAMOND); //per gli oggetti di minecraft vanilla non c'è bisogno di .get()
            })
            .build());

    public static void register(IEventBus eventBus){
         CREATIVE_MODE_TABS.register(eventBus);

            }
}

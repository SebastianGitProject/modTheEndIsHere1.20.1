package net.yastral.theendisheremod;

import com.mojang.logging.LogUtils;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.yastral.theendisheremod.block.ModBlocks;
import net.yastral.theendisheremod.entity.ModEntities;
import net.yastral.theendisheremod.entity.client.RhinoRender;
import net.yastral.theendisheremod.event.FakeServerSimulator;
import net.yastral.theendisheremod.event.MenuModifier;
import net.yastral.theendisheremod.item.ModCreativeModTabs;
import net.yastral.theendisheremod.item.ModItems;
import net.yastral.theendisheremod.sound.ModSounds;
//import net.yastral.theendisheremod.particle.ModParticles;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(TheEndIsHereMod.MOD_ID)
public class TheEndIsHereMod
{
    // Define mod id in a common place for everything to reference
    public static final String MOD_ID = "theendisheremod";
    // Directly reference a slf4j logger
    private static final Logger LOGGER = LogUtils.getLogger();


    public TheEndIsHereMod(FMLJavaModLoadingContext context)
    {
        IEventBus modEventBus = context.getModEventBus();


        ModCreativeModTabs.register(modEventBus);
        ModItems.register(modEventBus);
        ModBlocks.register(modEventBus);

        ModSounds.register(modEventBus);
        ModEntities.register(modEventBus);
        //ModParticles.register(modEventBus);


        modEventBus.addListener(this::commonSetup);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);

        // Register the item to a creative tab
        modEventBus.addListener(this::addCreative);

        // Register our mod's ForgeConfigSpec so that Forge can create and load the config file for us
        context.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(final FMLCommonSetupEvent event)
    {
    }

    // Add the example block item to the building blocks tab
    private void addCreative(BuildCreativeModeTabContentsEvent event)  //aggiunge gli oggetti nella tab creativa
    {
    if(event.getTabKey() == CreativeModeTabs.INGREDIENTS){
        event.accept(ModItems.Sapphire);
        event.accept(ModItems.Raw_Sapphire);
    }
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event)
    {
    }

    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents
    {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event)
        {
            FakeServerSimulator.getInstance();
            EntityRenderers.register(ModEntities.RHINO.get(), RhinoRender::new);
        }
    }
}

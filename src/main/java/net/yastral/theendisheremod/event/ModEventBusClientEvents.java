package net.yastral.theendisheremod.event;


import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.yastral.theendisheremod.TheEndIsHereMod;
import net.yastral.theendisheremod.entity.client.ModModelLayers;
import net.yastral.theendisheremod.entity.client.RhinoModel;

@Mod.EventBusSubscriber(modid = TheEndIsHereMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ModEventBusClientEvents {

    @SubscribeEvent
    public static void registerLyaer(EntityRenderersEvent.RegisterLayerDefinitions event){
        event.registerLayerDefinition(ModModelLayers.RHINO_LAYER, RhinoModel::createBodyLayer);
    }
}

package net.yastral.theendisheremod.event;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.yastral.theendisheremod.TheEndIsHereMod;
import net.yastral.theendisheremod.entity.ModEntities;
import net.yastral.theendisheremod.entity.custom.RhinoEntity;

@Mod.EventBusSubscriber(modid = TheEndIsHereMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModEventBusEvents {

    @SubscribeEvent
    public static void registerAttributes(EntityAttributeCreationEvent event){
        event.put(ModEntities.RHINO.get(), RhinoEntity.createAttributes().build());
    }
}

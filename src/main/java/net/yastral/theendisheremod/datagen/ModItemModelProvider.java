package net.yastral.theendisheremod.datagen;

import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.RegistryObject;
import net.yastral.theendisheremod.TheEndIsHereMod;
import net.yastral.theendisheremod.item.ModItems;

public class ModItemModelProvider extends ItemModelProvider {
    public ModItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, TheEndIsHereMod.MOD_ID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        simpleItem(ModItems.Sapphire);
        simpleItem(ModItems.Raw_Sapphire);
        simpleItem(ModItems.Metal_Detector);
        simpleItem(ModItems.Strawberry);
        simpleItem(ModItems.Shadow_Summoner);
        simpleItem(ModItems.Rhino_Spawn_egg);
        //simpleItem(ModItems.BAR_BRAWL_MUSIC_DISC);
        //withExistingParent(ModItems.RHINO_SPAWN_EGG.getId().getPath(), mcLoc("item/template_spawn_egg"));
    }

    private ItemModelBuilder simpleItem(RegistryObject<Item> item){
        return withExistingParent(item.getId().getPath(),
                new ResourceLocation("item/generated")).texture("layer0",
                new ResourceLocation(TheEndIsHereMod.MOD_ID, "item/" + item.getId().getPath()));
    }
}

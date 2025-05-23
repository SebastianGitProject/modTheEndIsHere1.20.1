package net.yastral.theendisheremod.util;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.yastral.theendisheremod.TheEndIsHereMod;

public class ModTags {

    public static class Blocks{
        public static final TagKey<Block> METAL_DETECTOR_VALUABLES = tag("metal_detector_valuables"); //i tags si trovano in data/theendisheremod/tags/blocks/metal_detector_valuables.json

        private static TagKey<Block> tag(String name) {
            return BlockTags.create(new ResourceLocation(TheEndIsHereMod.MOD_ID, name));
        }
    }

    public static class Items{

        private static TagKey<Item> tag(String name) {
            return ItemTags.create(new ResourceLocation(TheEndIsHereMod.MOD_ID, name));
        }
    }
}

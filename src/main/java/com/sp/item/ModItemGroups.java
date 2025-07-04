package com.sp.item;

import com.sp.SPBRevamped;
import com.sp.init.ModBlocks;
import com.sp.init.ModItems;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ModItemGroups {
    public static final ItemGroup BACKROOMS_GROUP = Registry.register(Registries.ITEM_GROUP,
            new Identifier(SPBRevamped.MOD_ID, "spbrevamped"),
            FabricItemGroup.builder().displayName(Text.translatable("itemgroup.spbrevamped"))
                    .icon(() -> new ItemStack(ModBlocks.WALL_BLOCK)).entries((displayContext, entries) -> {
                        entries.add(ModBlocks.VOID_BLOCK);
                        entries.add(ModBlocks.CEILINGLIGHT);
                        entries.add(ModBlocks.EMERGENCY_LIGHT);
                        entries.add(ModItems.BACKSHROOM);
                        entries.add(ModItems.CANNED_FOOD);
                        entries.add(ModBlocks.WALL_BLOCK);
                        entries.add(ModBlocks.WALL_BLOCK_2);
                        entries.add(ModBlocks.CEILING_TILE);
                        entries.add(ModBlocks.GHOST_CEILING_TILE);
                        entries.add(ModBlocks.CARPET_BLOCK);

                        entries.add(ModBlocks.FLUORESCENT_LIGHT);
                        entries.add(ModBlocks.THIN_FLUORESCENT_LIGHT);

                        entries.add(ModBlocks.WOODEN_CRATE);
                        entries.add(ModBlocks.CHAINFENCE);
                        entries.add(ModBlocks.NEWSTAIRS);
                        entries.add(ModBlocks.BOTTOM_TRIM);
                        entries.add(ModBlocks.CONCRETE_BLOCK_1);
                        entries.add(ModBlocks.CONCRETE_BLOCK_2);
                        entries.add(ModBlocks.CONCRETE_BLOCK_5);
                        entries.add(ModBlocks.CONCRETE_BLOCK_6);
                        entries.add(ModBlocks.CONCRETE_BLOCK_7);
                        entries.add(ModBlocks.CONCRETE_BLOCK_9);
                        entries.add(ModBlocks.CONCRETE_BLOCK_9_SLAB);
                        entries.add(ModBlocks.CONCRETE_BLOCK_10);
                        entries.add(ModBlocks.CONCRETE_BLOCK_11);
                        entries.add(ModBlocks.CONCRETE_BLOCK_12);
                        entries.add(ModBlocks.BRICKS);

                        entries.add(ModBlocks.THIN_PIPE);
                        entries.add(ModBlocks.THIN_PIPE_CORNER);
                        entries.add(ModBlocks.PIPE);
                        entries.add(ModBlocks.PIPE_MIDDLE);
                        entries.add(ModBlocks.BIG_PIPE);
                        entries.add(ModBlocks.BIG_PIPE_MIDDLE);
                        entries.add(ModBlocks.SMALL_PIPE_SET);
                        entries.add(ModBlocks.PIPE_CORNER);

                        entries.add(ModBlocks.WALL_TEXT_1);
                        entries.add(ModBlocks.WALL_TEXT_2);
                        entries.add(ModBlocks.WALL_TEXT_3);
                        entries.add(ModBlocks.WALL_TEXT_4);
                        entries.add(ModBlocks.WALL_TEXT_5);
                        entries.add(ModBlocks.WALL_TEXT_6);
                        entries.add(ModBlocks.WALL_TEXT_7);
                        entries.add(ModBlocks.WALL_TEXT_8);
                        entries.add(ModBlocks.WALL_TEXT_99);

                        entries.add(ModBlocks.WALL_ARROW_1);
                        entries.add(ModBlocks.WALL_ARROW_2);
                        entries.add(ModBlocks.WALL_ARROW_3);
                        entries.add(ModBlocks.WALL_ARROW_4);
                        entries.add(ModBlocks.WALL_SMALL_1);
                        entries.add(ModBlocks.WALL_SMALL_2);
                        entries.add(ModBlocks.WALL_DRAWING_DOOR);
                        entries.add(ModBlocks.WALL_DRAWING_WINDOW);

                        entries.add(ModBlocks.RUG_1);
                        entries.add(ModBlocks.RUG_2);

                        entries.add(ModBlocks.POOLROOMS_SKY_BLOCK);
                        entries.add(ModBlocks.POOL_TILES);
                        entries.add(ModBlocks.POOL_TILE_WALL);
                        entries.add(ModBlocks.POOL_TILE_SLOPE);

                        entries.add(ModBlocks.POWER_POLE_TOP);
                        entries.add(ModBlocks.POWER_POLE);
                        entries.add(ModBlocks.DIRT);

                        entries.add(ModBlocks.ROAD);
                        entries.add(ModBlocks.RED_DIRT);
                        entries.add(ModBlocks.PLASTIC);
                        entries.add(ModBlocks.NONE_REFLECTIVE_PLASTIC);
                        entries.add(ModBlocks.RED_METAL_CASING);
                        entries.add(ModBlocks.PILLAR);
                        entries.add(ModBlocks.POLE);
                        entries.add(ModBlocks.LAMP);
                        entries.add(ModBlocks.WINDOW);
                        entries.add(ModBlocks.SCHLEUSE);
                        entries.add(ModBlocks.TINY_FLUORESCENT_LIGHT);
                        entries.add(ModBlocks.FLOOR_TILING);
                        entries.add(ModBlocks.DOUBLE_SIDED_SHELF);
                        entries.add(ModBlocks.ONE_SIDED_SHELF);
                        entries.add(ModBlocks.PAVEMENT);


                    }).build());




    public static void registerItemGroups() {
        SPBRevamped.LOGGER.info("Registering Item Groups");
    }
}

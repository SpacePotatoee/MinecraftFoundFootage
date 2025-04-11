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
                    .icon(() -> new ItemStack(ModBlocks.WallBlock)).entries((displayContext, entries) -> {
                        entries.add(ModBlocks.VoidBlock);
                        entries.add(ModBlocks.CEILINGLIGHT);
                        entries.add(ModBlocks.EmergencyLight);
                        entries.add(ModItems.BACKSHROOM);
                        entries.add(ModItems.CANNED_FOOD);
                        entries.add(ModBlocks.WallBlock);
                        entries.add(ModBlocks.WallBlock2);
                        entries.add(ModBlocks.CeilingTile);
                        entries.add(ModBlocks.GhostCeilingTile);
                        entries.add(ModBlocks.CarpetBlock);

                        entries.add(ModBlocks.FluorescentLight);
                        entries.add(ModBlocks.ThinFluorescentLight);

                        entries.add(ModBlocks.WOODEN_CRATE);
                        entries.add(ModBlocks.CHAINFENCE);
                        entries.add(ModBlocks.NEWSTAIRS);
                        entries.add(ModBlocks.BottomTrim);
                        entries.add(ModBlocks.ConcreteBlock1);
                        entries.add(ModBlocks.ConcreteBlock2);
                        entries.add(ModBlocks.ConcreteBlock5);
                        entries.add(ModBlocks.ConcreteBlock6);
                        entries.add(ModBlocks.ConcreteBlock7);
                        entries.add(ModBlocks.ConcreteBlock9);
                        entries.add(ModBlocks.ConcreteBlock9Slab);
                        entries.add(ModBlocks.ConcreteBlock10);
                        entries.add(ModBlocks.ConcreteBlock11);
                        entries.add(ModBlocks.ConcreteBlock12);
                        entries.add(ModBlocks.Bricks);

                        entries.add(ModBlocks.ThinPipe);
                        entries.add(ModBlocks.ThinPipeCorner);
                        entries.add(ModBlocks.Pipe);
                        entries.add(ModBlocks.PipeMiddle);
                        entries.add(ModBlocks.BigPipe);
                        entries.add(ModBlocks.BigPipeMiddle);
                        entries.add(ModBlocks.SmallPipeSet);
                        entries.add(ModBlocks.PipeCorner);

                        entries.add(ModBlocks.WallText1);
                        entries.add(ModBlocks.WallText2);
                        entries.add(ModBlocks.WallText3);
                        entries.add(ModBlocks.WallText4);
                        entries.add(ModBlocks.WallText5);
                        entries.add(ModBlocks.WallText6);
                        entries.add(ModBlocks.WallText7);
                        entries.add(ModBlocks.WallText8);
                        entries.add(ModBlocks.WallText99);

                        entries.add(ModBlocks.WallArrow1);
                        entries.add(ModBlocks.WallArrow2);
                        entries.add(ModBlocks.WallArrow3);
                        entries.add(ModBlocks.WallArrow4);
                        entries.add(ModBlocks.WallSmall1);
                        entries.add(ModBlocks.WallSmall2);
                        entries.add(ModBlocks.WallDrawingDoor);
                        entries.add(ModBlocks.WallDrawingWindow);

                        entries.add(ModBlocks.Rug1);
                        entries.add(ModBlocks.Rug2);

                        entries.add(ModBlocks.PoolroomsSkyBlock);
                        entries.add(ModBlocks.PoolTiles);
                        entries.add(ModBlocks.PoolTileWall);
                        entries.add(ModBlocks.PoolTileSlope);

                        entries.add(ModBlocks.POWER_POLE_TOP);
                        entries.add(ModBlocks.POWER_POLE);
                        entries.add(ModBlocks.DIRT);





                    }).build());




    public static void registerItemGroups() {
        SPBRevamped.LOGGER.info("Registering Item Groups");
    }
}

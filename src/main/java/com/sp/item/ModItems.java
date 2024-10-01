package com.sp.item;

import com.sp.SPBRevamped;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModItems {

//    public static final Item FLASHLIGHTWAND = registerItem("flashlight_wand",
//            new FlashLightWand(new FabricItemSettings()));


    private static Item registerItem(String name, Item item){
        return Registry.register(Registries.ITEM, new Identifier(SPBRevamped.MOD_ID, name), item);
    }

    public static void registerModItems() {
        SPBRevamped.LOGGER.info("Registering Mod Items for " + SPBRevamped.MOD_ID);
    }
}

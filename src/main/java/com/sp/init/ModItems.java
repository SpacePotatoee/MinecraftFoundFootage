package com.sp.init;

import com.sp.SPBRevamped;
import com.sp.item.custom.Backshroom;
import com.sp.item.custom.CannedFood;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModItems {

    public static final Item BACKSHROOM = registerItem("backshroom",
            new Backshroom(new FabricItemSettings().food(ModFoodComponents.BACKSHROOM)));

    public static final Item CANNED_FOOD = registerItem("canned_food",
            new CannedFood(new FabricItemSettings().food(ModFoodComponents.CANNED_FOOD)));

    private static Item registerItem(String name, Item item){
        return Registry.register(Registries.ITEM, new Identifier(SPBRevamped.MOD_ID, name), item);
    }

    public static void registerModItems() {
        SPBRevamped.LOGGER.info("Registering Mod Items for " + SPBRevamped.MOD_ID);
    }
}

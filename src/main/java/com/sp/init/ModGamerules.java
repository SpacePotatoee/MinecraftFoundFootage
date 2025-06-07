package com.sp.init;

import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry;
import net.minecraft.world.GameRules;

public class ModGamerules {
    public static final GameRules.Key<GameRules.BooleanRule> STUCK_IN_BACKROOMS =
        GameRuleRegistry.register("stuck_in_the_backrooms", GameRules.Category.PLAYER, GameRuleFactory.createBooleanRule(false));

    public static void registerGamerules() {
        
    }
}

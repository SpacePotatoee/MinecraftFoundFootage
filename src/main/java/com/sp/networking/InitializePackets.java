package com.sp.networking;

import com.sp.SPBRevamped;
import com.sp.networking.C2S.CutsceneSync;
import com.sp.networking.C2S.FlashLightSync;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.util.Identifier;

public class InitializePackets {
    public static final Identifier FL_SYNC = new Identifier(SPBRevamped.MOD_ID, "fl_sync");
    public static final Identifier CUTSCENE_SYNC = new Identifier(SPBRevamped.MOD_ID, "cut_sync");

    public static void registerC2SPackets() {
        ServerPlayNetworking.registerGlobalReceiver(FL_SYNC, FlashLightSync::receive);
        ServerPlayNetworking.registerGlobalReceiver(CUTSCENE_SYNC, CutsceneSync::receive);
    }

    public static void registerS2CPackets() {

    }
}

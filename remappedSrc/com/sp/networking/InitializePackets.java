package com.sp.networking;

import com.sp.SPBRevamped;
import com.sp.networking.C2S.CutsceneSync;
import com.sp.networking.C2S.FlashLightSync;
import com.sp.networking.C2S.ShouldDoStaticPacket;
import com.sp.networking.S2C.InvokeBlackScreenPacket;
import com.sp.networking.S2C.InvokeScreenShakePacket;
import com.sp.networking.S2C.ReloadLightsPacket;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.util.Identifier;

public class InitializePackets {
    public static final Identifier FL_SYNC = new Identifier(SPBRevamped.MOD_ID, "fl_sync");
    public static final Identifier CUTSCENE_SYNC = new Identifier(SPBRevamped.MOD_ID, "cut_sync");
    public static final Identifier STATIC_PACKET = new Identifier(SPBRevamped.MOD_ID, "stat_pack");

    public static final Identifier SCREEN_SHAKE = new Identifier(SPBRevamped.MOD_ID, "scr_shake");
    public static final Identifier BLACK_SCREEN = new Identifier(SPBRevamped.MOD_ID, "blk_screen");
    public static final Identifier RELOAD_LIGHTS = new Identifier(SPBRevamped.MOD_ID, "rl_lights");

    public static void registerC2SPackets() {
        ServerPlayNetworking.registerGlobalReceiver(FL_SYNC, FlashLightSync::receive);
        ServerPlayNetworking.registerGlobalReceiver(CUTSCENE_SYNC, CutsceneSync::receive);
        ServerPlayNetworking.registerGlobalReceiver(STATIC_PACKET, ShouldDoStaticPacket::receive);
    }

    public static void registerS2CPackets() {
        ClientPlayNetworking.registerGlobalReceiver(SCREEN_SHAKE, InvokeScreenShakePacket::receive);
        ClientPlayNetworking.registerGlobalReceiver(BLACK_SCREEN, InvokeBlackScreenPacket::receive);
        ClientPlayNetworking.registerGlobalReceiver(RELOAD_LIGHTS, ReloadLightsPacket::receive);
    }
}

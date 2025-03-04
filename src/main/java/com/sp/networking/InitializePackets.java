package com.sp.networking;

import com.sp.SPBRevamped;
import com.sp.networking.C2S.*;
import com.sp.networking.S2C.*;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.util.Identifier;

public class InitializePackets {
    public static final Identifier FL_SYNC = new Identifier(SPBRevamped.MOD_ID, "fl_sync");
    public static final Identifier CUTSCENE_SYNC = new Identifier(SPBRevamped.MOD_ID, "cut_sync");
    public static final Identifier STATIC_PACKET = new Identifier(SPBRevamped.MOD_ID, "stat_pack");
    public static final Identifier TARGET_ENTITY_SYNC = new Identifier(SPBRevamped.MOD_ID, "targ_ent");
    public static final Identifier SEE_SKINWALKER_SYNC = new Identifier(SPBRevamped.MOD_ID, "see_skin");
    public static final Identifier BEING_CAPTURED_SYNC = new Identifier(SPBRevamped.MOD_ID, "cap_sync");
    public static final Identifier GLITCH_DAMAGE_SYNC = new Identifier(SPBRevamped.MOD_ID, "gl_dam");

    public static final Identifier SCREEN_SHAKE = new Identifier(SPBRevamped.MOD_ID, "scr_shake");
    public static final Identifier BLACK_SCREEN = new Identifier(SPBRevamped.MOD_ID, "blk_screen");
    public static final Identifier RELOAD_LIGHTS = new Identifier(SPBRevamped.MOD_ID, "rl_lights");
    public static final Identifier SOUND = new Identifier(SPBRevamped.MOD_ID, "snd");

    public static void registerC2SPackets() {
        ServerPlayNetworking.registerGlobalReceiver(FL_SYNC, FlashLightSync::receive);
        ServerPlayNetworking.registerGlobalReceiver(CUTSCENE_SYNC, CutsceneSync::receive);
        ServerPlayNetworking.registerGlobalReceiver(STATIC_PACKET, ShouldDoStaticPacket::receive);
        ServerPlayNetworking.registerGlobalReceiver(TARGET_ENTITY_SYNC, TargetEntitySync::receive);
        ServerPlayNetworking.registerGlobalReceiver(SEE_SKINWALKER_SYNC, SeeActiveSkinwalkerSync::receive);
        ServerPlayNetworking.registerGlobalReceiver(BEING_CAPTURED_SYNC, BeingCapturedSync::receive);
        ServerPlayNetworking.registerGlobalReceiver(GLITCH_DAMAGE_SYNC, GlitchDamageSync::receive);
    }

    public static void registerS2CPackets() {
        ClientPlayNetworking.registerGlobalReceiver(SCREEN_SHAKE, InvokeScreenShakePacket::receive);
        ClientPlayNetworking.registerGlobalReceiver(BLACK_SCREEN, InvokeBlackScreenPacket::receive);
        ClientPlayNetworking.registerGlobalReceiver(RELOAD_LIGHTS, ReloadLightsPacket::receive);
        ClientPlayNetworking.registerGlobalReceiver(SOUND, SoundPacket::receive);
    }
}

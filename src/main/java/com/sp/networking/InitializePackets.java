package com.sp.networking;

import com.sp.SPBRevamped;
import com.sp.networking.C2S.*;
import com.sp.networking.S2C.*;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.util.Identifier;

public class InitializePackets {
    public static final Identifier TARGET_ENTITY_SYNC = new Identifier(SPBRevamped.MOD_ID, "targ_ent");
    public static final Identifier SEE_SKINWALKER_SYNC = new Identifier(SPBRevamped.MOD_ID, "see_skin");
    public static final Identifier COMPONENT_SYNC = new Identifier(SPBRevamped.MOD_ID, "comp_sync");

    public static final Identifier SCREEN_SHAKE = new Identifier(SPBRevamped.MOD_ID, "scr_shake");
    public static final Identifier BLACK_SCREEN = new Identifier(SPBRevamped.MOD_ID, "blk_screen");
    public static final Identifier RELOAD_LIGHTS = new Identifier(SPBRevamped.MOD_ID, "rl_lights");
    public static final Identifier SOUND = new Identifier(SPBRevamped.MOD_ID, "snd");
    public static final Identifier LEVEL_TRANSITION_LIGHTSOUT = new Identifier(SPBRevamped.MOD_ID, "ltos");

    public static void registerC2SPackets() {
        ServerPlayNetworking.registerGlobalReceiver(TARGET_ENTITY_SYNC, TargetEntitySync::receive);
        ServerPlayNetworking.registerGlobalReceiver(SEE_SKINWALKER_SYNC, SeeActiveSkinwalkerSync::receive);
        ServerPlayNetworking.registerGlobalReceiver(COMPONENT_SYNC, SyncServerComponent::receive);
    }

    public static void registerS2CPackets() {
        ClientPlayNetworking.registerGlobalReceiver(SCREEN_SHAKE, InvokeScreenShakePacket::receive);
        ClientPlayNetworking.registerGlobalReceiver(BLACK_SCREEN, InvokeBlackScreenPacket::receive);
        ClientPlayNetworking.registerGlobalReceiver(RELOAD_LIGHTS, ReloadLightsPacket::receive);
        ClientPlayNetworking.registerGlobalReceiver(SOUND, SoundPacket::receive);
        ClientPlayNetworking.registerGlobalReceiver(LEVEL_TRANSITION_LIGHTSOUT, LevelTransitionLightsOut::receive);
    }
}

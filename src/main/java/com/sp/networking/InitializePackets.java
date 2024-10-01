package com.sp.networking;

import com.sp.SPBRevamped;
import com.sp.networking.C2S.FlashLightSync;
import com.sp.networking.S2C.*;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.util.Identifier;

public class InitializePackets {
    public static final Identifier FL_SYNC = new Identifier(SPBRevamped.MOD_ID, "fl_sync");
    //public static final Identifier OTHER_PLAYER_UUID = new Identifier(SPBRevamped.MOD_ID, "other_player_uuid");

    public static final Identifier CAMERA_BLOCK_UUID = new Identifier(SPBRevamped.MOD_ID, "camera_block_uuid");
    public static final Identifier CAMERA_BLOCK_POS = new Identifier(SPBRevamped.MOD_ID, "camera_block_pos");
    public static final Identifier CAMERA_BLOCK_DOWN = new Identifier(SPBRevamped.MOD_ID, "camera_block_down");
    public static final Identifier OTHER_PLAYER_UUID = new Identifier(SPBRevamped.MOD_ID, "other_player_uuid");
    public static final Identifier IN_OTHER_INVENTORY = new Identifier(SPBRevamped.MOD_ID, "in_other_inventory");
    public static final Identifier HAS_OTHER_CAMERA = new Identifier(SPBRevamped.MOD_ID, "has_other_camera");


    public static void registerC2SPackets() {
        ServerPlayNetworking.registerGlobalReceiver(FL_SYNC, FlashLightSync::receive);
        //ServerPlayNetworking.registerGlobalReceiver(OTHER_PLAYER_UUID, OtherPlayerUUIDSync::receive);
    }

    public static void registerS2CPackets() {
        ClientPlayNetworking.registerGlobalReceiver(CAMERA_BLOCK_UUID, CameraBlockUUIDSyncS2C::receive);
        ClientPlayNetworking.registerGlobalReceiver(CAMERA_BLOCK_POS, CameraBlockPosSyncS2C::receive);
        ClientPlayNetworking.registerGlobalReceiver(CAMERA_BLOCK_DOWN, CameraBlockDownSyncS2C::receive);
        ClientPlayNetworking.registerGlobalReceiver(OTHER_PLAYER_UUID, OtherPlayerUUIDSyncS2C::receive);
        ClientPlayNetworking.registerGlobalReceiver(IN_OTHER_INVENTORY, CameraInOtherInventorySyncS2C::receive);
        ClientPlayNetworking.registerGlobalReceiver(HAS_OTHER_CAMERA, HasOtherCameraSyncS2C::receive);

    }
}

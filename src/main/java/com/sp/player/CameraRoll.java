package com.sp.player;

import com.sp.ConfigStuff;
import com.sp.cca_stuff.InitializeComponents;
import com.sp.cca_stuff.PlayerComponent;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;

import static net.minecraft.util.math.MathHelper.lerp;

public class CameraRoll {
    static float prevYaw = -1000;
    static float prevPitch = -1000;
    static float rotAmount;
    static float spinRoll;

    static float prevYaw2 = -1000;
    static float prevPitch2 = -1000;
    static float rotAmount2;
    static float spinRoll2;
    static float lookRollMultiplier2 = 4.0f;
    static float MaxLookRollAngle2 = 10.0f;

    static float strafeLeftRoll;
    static float strafeRightRoll;
    static int AtimePressed;
    static int DtimePressed;

    public static void doCameraRoll(PlayerEntity player){
        if (player != null) {
            PlayerComponent playerComponent = InitializeComponents.PLAYER.get(player);
            float yaw = player.getYaw();
            float pitch = player.getPitch();



            if (prevYaw != -1000 && prevPitch != -1000){
                rotAmount = yaw - prevYaw;

                spinRoll += lerp(0.1f, rotAmount/(2 * ConfigStuff.lookRollMultiplier), 0.0f);
                spinRoll = spinRoll * 0.8f;
                spinRoll = MathHelper.clamp(spinRoll, -ConfigStuff.MaxLookRollAngle, ConfigStuff.MaxLookRollAngle);

            }


            prevYaw = yaw;
            prevPitch = pitch;


            if (InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow().getHandle(), 65) && (Math.abs(player.getVelocity().x) > 0.05f || Math.abs(player.getVelocity().z) > 0.05f)){
                AtimePressed++;
                strafeLeftRoll -= (float) lerp(0.9, AtimePressed, 0);
                strafeLeftRoll = strafeLeftRoll * 0.9f;
                strafeLeftRoll = MathHelper.clamp(-Math.abs(strafeLeftRoll), -3, 0);
            }else{
                strafeLeftRoll = strafeLeftRoll * 0.9f;
                strafeLeftRoll = MathHelper.clamp(-Math.abs(strafeLeftRoll), -3, 0);
                AtimePressed = 0;
            }

            if (InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow().getHandle(), 68) && (Math.abs(player.getVelocity().x) > 0.05f || Math.abs(player.getVelocity().z) > 0.05f)){
                DtimePressed++;
                strafeRightRoll += (float) lerp(0.9, DtimePressed, 0);
                strafeRightRoll = strafeRightRoll * 0.9f;
                strafeRightRoll = MathHelper.clamp(Math.abs(strafeRightRoll), 0, 3);
            }else{
                strafeRightRoll = strafeRightRoll * 0.9f;
                strafeRightRoll = MathHelper.clamp(Math.abs(strafeRightRoll), 0, 3);
                DtimePressed = 0;
            }

            playerComponent.setCameraRoll(spinRoll + strafeRightRoll + strafeLeftRoll);


        }
    }

    public static void doCameraRollForOther(PlayerEntity player, PlayerEntity you){
        if (player != null && you != null) {
            PlayerComponent playerComponent = InitializeComponents.PLAYER.get(player);
            PlayerComponent youComponent = InitializeComponents.PLAYER.get(you);
            float yaw = player.getYaw();
            float pitch = player.getPitch();



            if (prevYaw2 != -1000 && prevPitch2 != -1000){
                rotAmount2 = yaw - prevYaw2;

                spinRoll2 += lerp(0.01f, rotAmount2/(2 * lookRollMultiplier2), 0.0f);
                spinRoll2 = spinRoll2 *0.9f;
                spinRoll2 = MathHelper.clamp(spinRoll2, -MaxLookRollAngle2, MaxLookRollAngle2);

            }


            prevYaw2 = yaw;
            prevPitch2 = pitch;
//
//
//            if (InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow().getHandle(), 65) && (Math.abs(player.getVelocity().x) > 0.05f || Math.abs(player.getVelocity().z) > 0.05f)){
//                AtimePressed++;
//                strafeLeftRoll += (float) lerp(0.001, AtimePressed, 0);
//                strafeLeftRoll = MathHelper.clamp(-Math.abs(strafeLeftRoll), -5, 0);
//            }else{
//                strafeLeftRoll = strafeLeftRoll * 0.95f;
//                strafeLeftRoll = MathHelper.clamp(strafeLeftRoll, -5, 0);
//                AtimePressed = 0;
//            }
//
//            if (InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow().getHandle(), 68) && (Math.abs(player.getVelocity().x) > 0.05f || Math.abs(player.getVelocity().z) > 0.05f)){
//                DtimePressed++;
//                strafeRightRoll -= (float) lerp(0.001, DtimePressed, 0);
//                strafeRightRoll = MathHelper.clamp(Math.abs(strafeRightRoll), 0, 5);
//            }else{
//                strafeRightRoll = strafeRightRoll * 0.95f;
//                strafeRightRoll = MathHelper.clamp(strafeRightRoll, 0, 5);
//                DtimePressed = 0;
//            }

            youComponent.setOtherCameraRoll(spinRoll2);


        }
    }
}

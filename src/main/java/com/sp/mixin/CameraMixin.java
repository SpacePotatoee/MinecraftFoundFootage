package com.sp.mixin;

import com.sp.ConfigStuff;
import com.sp.cca_stuff.InitializeComponents;
import com.sp.cca_stuff.PlayerComponent;
import net.minecraft.block.Block;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockView;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Camera.class)
public abstract class CameraMixin {
    int ticks = 0;
    @Shadow
    abstract void setRotation(float yaw, float pitch);

    @Shadow
    abstract void setPos(double x, double y, double z);

    @Shadow private Vec3d pos;

    @Inject(method = "update", at = @At("RETURN"))
    private void UpdateCamera(BlockView area, Entity focusedEntity, boolean thirdPerson, boolean inverseView, float tickDelta, CallbackInfo ci){
        if (focusedEntity instanceof PlayerEntity) {
            PlayerComponent playerComponent = InitializeComponents.PLAYER.get(focusedEntity);

//            if(ConfigStuff.enableBetterCamera) {
//                Vec3d normalPos = focusedEntity.getClientCameraPosVec(tickDelta);
//                float xOffset = (0.20f * MathHelper.sin((float) Math.toRadians(focusedEntity.getYaw(tickDelta)))) * (MathHelper.cos(0.55f * ((float) Math.toRadians(focusedEntity.getPitch(tickDelta) - 0.6f))) + 0.5f);
//                float yOffset = 0.3f * MathHelper.sin(0.8f * ((float) Math.toRadians(focusedEntity.getPitch(tickDelta))) - 0.5f) + 0.26f;
//                float zOffset = (0.20f * MathHelper.cos((float) Math.toRadians(focusedEntity.getYaw(tickDelta)))) * (MathHelper.cos(0.55f * ((float) Math.toRadians(focusedEntity.getPitch(tickDelta) - 0.6f))) + 0.5f);
//
//
//                setPos(normalPos.getX() - xOffset, normalPos.getY() - yOffset, normalPos.getZ() + zOffset);
//            }

            if(playerComponent.isCameraItem()){
                Vec3d cameraItemPos = playerComponent.getCameraItemPos();
                float yaw = playerComponent.getCameraItemYaw();

                setPos(cameraItemPos.x, cameraItemPos.y, cameraItemPos.z);
                setRotation(yaw, 0);

            }


            if(playerComponent.getCameraPos() != null) {
                BlockPos position = playerComponent.getCameraPos();

                if (playerComponent.isCameraDown()) {
                    setPos(position.getX() + 0.5, position.getY() + 0.125, position.getZ() + 0.3125);
                    setRotation(-180.0f, 0.0f);
                }
            }
            if (playerComponent.getOtherPlayerPos() != null){
                PlayerEntity otherPlayer = playerComponent.getOtherPlayerPos();
                Vec3d position = otherPlayer.getClientCameraPosVec(tickDelta);

                if (playerComponent.isCameraInOtherInventory()){
                    setPos(position.getX() - (0.25 * MathHelper.sin((float) Math.toRadians(otherPlayer.getYaw(tickDelta)))), position.getY() - (0.5 * MathHelper.sin((float) Math.toRadians(otherPlayer.getPitch(tickDelta))) + 0.07), position.getZ() + (0.25 * MathHelper.cos((float) Math.toRadians(otherPlayer.getYaw(tickDelta)))));
                    setRotation(otherPlayer.getYaw(tickDelta), otherPlayer.getPitch(tickDelta));
                }
            }

        }
    }
}

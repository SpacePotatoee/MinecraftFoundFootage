package com.sp.mixin.respawnsystem;

import com.sp.init.BackroomsLevels;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import java.util.Optional;

@Mixin(PlayerManager.class)
public class PlayerManagerMixin {

    @Shadow @Final private MinecraftServer server;

    @Unique ServerPlayerEntity targetPlayer;

    @Inject(method = "respawnPlayer", at = @At("HEAD"))
    private void setTargetPlayer(ServerPlayerEntity player, boolean alive, CallbackInfoReturnable<ServerPlayerEntity> cir){
        this.targetPlayer = player;
    }

    @Redirect(method = "respawnPlayer", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayerEntity;getSpawnPointPosition()Lnet/minecraft/util/math/BlockPos;"))
    private BlockPos setSpawnPointPos(ServerPlayerEntity instance){
        if(BackroomsLevels.isInBackrooms(targetPlayer.getWorld().getRegistryKey())) {
            return targetPlayer.getLastDeathPos().isPresent() ? targetPlayer.getLastDeathPos().get().getPos() : instance.getSpawnPointPosition();
        }
        return instance.getSpawnPointPosition();
    }


    @Redirect(method = "respawnPlayer", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/MinecraftServer;getWorld(Lnet/minecraft/registry/RegistryKey;)Lnet/minecraft/server/world/ServerWorld;"))
    private @Nullable ServerWorld getCurrentWorld(MinecraftServer instance, RegistryKey<World> key){
        if(BackroomsLevels.isInBackrooms(targetPlayer.getWorld().getRegistryKey())) {
            return instance.getWorld(targetPlayer.getWorld().getRegistryKey());
        }

        return instance.getWorld(key);
    }



    @Redirect(method = "respawnPlayer", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;findRespawnPosition(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/util/math/BlockPos;FZZ)Ljava/util/Optional;"))
    private Optional<Vec3d> respawn(ServerWorld world, BlockPos pos, float angle, boolean forced, boolean alive){
        if(BackroomsLevels.isInBackrooms(targetPlayer.getWorld().getRegistryKey())) {
            if (targetPlayer.getLastDeathPos().isPresent()) {
                return Optional.of(targetPlayer.getLastDeathPos().get().getPos().toCenterPos());
            }
        }
        return PlayerEntity.findRespawnPosition(world, pos, angle, forced, alive);
    }


    @ModifyArgs(method = "respawnPlayer", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayerEntity;refreshPositionAndAngles(DDDFF)V"))
    private void setSpawnAngle(Args args){
        if(BackroomsLevels.isInBackrooms(targetPlayer.getWorld().getRegistryKey())) {
            args.set(3, targetPlayer.getYaw());
            args.set(4, targetPlayer.getPitch());
        }
    }


    @ModifyArgs(method = "respawnPlayer", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayerEntity;setSpawnPoint(Lnet/minecraft/registry/RegistryKey;Lnet/minecraft/util/math/BlockPos;FZZ)V"))
    private void respawn2(Args args){
        if(BackroomsLevels.isInBackrooms(targetPlayer.getWorld().getRegistryKey())) {
            ServerWorld currentWorld = this.server.getWorld(targetPlayer.getWorld().getRegistryKey());
            Optional<GlobalPos> lastDeathPos = targetPlayer.getLastDeathPos();

            if (currentWorld != null) {
                if (lastDeathPos.isPresent()) {
                    BlockPos pos = lastDeathPos.get().getPos();

                    if (pos.getY() < 0){
                        pos = BackroomsLevels.getCurrentLevelsOrigin(currentWorld.getRegistryKey());
                    }

                    args.set(0, currentWorld.getRegistryKey());
                    args.set(1, pos);
                    args.set(2, args.get(2));
                    args.set(3, args.get(3));
                    args.set(4, args.get(4));
                }
            }
        }
    }

}

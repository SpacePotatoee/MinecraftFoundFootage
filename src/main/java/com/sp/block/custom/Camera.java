package com.sp.block.custom;

import com.sp.block.entity.CameraBlockEntity;
import com.sp.block.entity.ModBlockEntities;
import com.sp.cca_stuff.InitializeComponents;
import com.sp.cca_stuff.PlayerComponent;
import com.sp.networking.InitializePackets;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class Camera extends BlockWithEntity {

    public Camera(Settings settings) {
        super(settings);
    }

    @Override
    public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        PacketByteBuf buffer1 = PacketByteBufs.create();
        PacketByteBuf buffer2 = PacketByteBufs.create();
        ItemStack itemStack = new ItemStack(this);

        if(!world.isClient) {
            if (blockEntity instanceof CameraBlockEntity) {
                if(((CameraBlockEntity) blockEntity).attatchedPlayer != null) {
                    PlayerEntity AttachedPlayer = world.getPlayerByUuid(((CameraBlockEntity) blockEntity).attatchedPlayer);
                    NbtCompound nbt = new NbtCompound();
                    nbt.putUuid("attachedPlayer", ((CameraBlockEntity) blockEntity).attatchedPlayer);
                    itemStack.setSubNbt("attachedPlayer", nbt);
                    itemStack.setCount(1);

                    if (AttachedPlayer != null) {
                        PlayerComponent playerComponent = InitializeComponents.PLAYER.get(AttachedPlayer);

                        playerComponent.setCameraPos(pos);
                        playerComponent.SetCameraDown(false);

                        buffer1.writeBlockPos(playerComponent.getCameraPos());
                        buffer2.writeBoolean(playerComponent.isCameraDown());
                        ServerPlayNetworking.send((ServerPlayerEntity) AttachedPlayer, InitializePackets.CAMERA_BLOCK_POS, buffer1);
                        ServerPlayNetworking.send((ServerPlayerEntity) AttachedPlayer, InitializePackets.CAMERA_BLOCK_DOWN, buffer2);
                    }

                    if (((CameraBlockEntity) blockEntity).customName != null) {
                        itemStack.setCustomName(((CameraBlockEntity) blockEntity).customName);
                    }
                }
            }
        }
        ItemEntity itemEntity = new ItemEntity(world, (double) pos.getX() + 0.5, (double) pos.getY() + 0.5, (double) pos.getZ() + 0.5, itemStack);
        itemEntity.setToDefaultPickupDelay();
        world.spawnEntity(itemEntity);

        super.onBreak(world, pos, state, player);
    }


    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        PacketByteBuf buffer1 = PacketByteBufs.create();
        PacketByteBuf buffer2 = PacketByteBufs.create();
        PacketByteBuf buffer3 = PacketByteBufs.create();
        NbtCompound nbt = new NbtCompound();
        if (placer != null) {
            if (!world.isClient) {
                if (blockEntity instanceof CameraBlockEntity) {

                    if (itemStack.getSubNbt("attachedPlayer") == null) {
                        ((CameraBlockEntity) blockEntity).attatchedPlayer = placer.getUuid();

                        ((CameraBlockEntity) blockEntity).customName = Text.literal(placer.getEntityName() + "'s Camera");

                        nbt.putUuid("attachedPlayer", ((CameraBlockEntity) blockEntity).attatchedPlayer);
                        itemStack.setSubNbt("attachedPlayer", nbt);
                        itemStack.setCount(1);

                        buffer1.writeUuid(((CameraBlockEntity) blockEntity).attatchedPlayer);
                        ServerPlayNetworking.send((ServerPlayerEntity) placer, InitializePackets.CAMERA_BLOCK_UUID, buffer1);

                    }

                    if(itemStack.hasCustomName()){
                        ((CameraBlockEntity) blockEntity).customName = itemStack.getName();
                    }

                    UUID AttachedPlayerUUID = itemStack.getSubNbt("attachedPlayer").getUuid("attachedPlayer");
                    PlayerEntity AttachedPlayer = world.getPlayerByUuid(AttachedPlayerUUID);

                    if(AttachedPlayer != null) {

                        PlayerComponent playerComponent = InitializeComponents.PLAYER.get(AttachedPlayer);
                        ((CameraBlockEntity) blockEntity).attatchedPlayer = AttachedPlayerUUID;

                        playerComponent.setCameraPos(pos);
                        playerComponent.SetCameraDown(true);
                        playerComponent.setCameraInOtherInventory(false);
                        playerComponent.setCameraItem(false);
                        InitializeComponents.PLAYER.sync(AttachedPlayer);

                        buffer2.writeBlockPos(playerComponent.getCameraPos());
                        ServerPlayNetworking.send((ServerPlayerEntity) AttachedPlayer, InitializePackets.CAMERA_BLOCK_POS, buffer2);

                        buffer1.writeUuid(AttachedPlayerUUID);
                        ServerPlayNetworking.send((ServerPlayerEntity) AttachedPlayer, InitializePackets.CAMERA_BLOCK_UUID, buffer1);

                        buffer3.writeBoolean(playerComponent.isCameraDown());
                        ServerPlayNetworking.send((ServerPlayerEntity) AttachedPlayer, InitializePackets.CAMERA_BLOCK_DOWN, buffer3);
                    }
                }
            }
        }

        super.onPlaced(world, pos, state, placer, itemStack);
    }







    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return BlockWithEntity.createCuboidShape(6.0f, 0.0f, 4.0f, 10.0f, 4.0f, 12.0f);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new CameraBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return checkType(type, ModBlockEntities.CAMERA_BLOCK_ENTITY, (world1, pos, state1, blockEntity) -> blockEntity.tick(world1, pos, state1));
    }
}

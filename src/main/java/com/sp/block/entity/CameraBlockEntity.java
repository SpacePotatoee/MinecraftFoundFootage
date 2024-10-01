package com.sp.block.entity;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.UUID;

public class CameraBlockEntity extends BlockEntity {
    public UUID attatchedPlayer;
    public Text customName;
    public Vec3d Position;

    public CameraBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.CAMERA_BLOCK_ENTITY, pos, state);
        this.Position = pos.toCenterPos();
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        if (this.attatchedPlayer != null) nbt.putUuid("attachedPlayer", this.attatchedPlayer);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        this.attatchedPlayer = nbt.getUuid("attachedPlayer");
    }

    public void tick(World world, BlockPos pos, BlockState state) {
//        if(!world.isClient) {
//            System.out.println("Server: " + Position);
//        } else{
//            System.out.println("Client: " + Position);
//        }
    }
}

package com.sp.mixin;

import net.minecraft.block.entity.StructureBlockBlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.Vec3i;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(StructureBlockBlockEntity.class)
public class StructureBlockEntityMixin {
    @Shadow
    private Vec3i size;

    @Inject(method = "readNbt", at = @At("RETURN"))
    public void readNbt(NbtCompound nbt, CallbackInfo ci) {
        int l = nbt.getInt("sizeX");
        int m = nbt.getInt("sizeY");
        int n = nbt.getInt("sizeZ");
        this.size = new Vec3i(l, m, n);
    }
}

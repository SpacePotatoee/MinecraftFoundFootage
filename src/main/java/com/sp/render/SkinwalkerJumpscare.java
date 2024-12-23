package com.sp.render;

import com.sp.cca_stuff.PlayerComponent;
import com.sp.init.ModSounds;
import com.sp.networking.InitializePackets;
import com.sp.util.ExtraUtils;
import foundry.veil.api.client.render.shader.program.ShaderProgram;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.random.Random;

public class SkinwalkerJumpscare {
    private static long startTime;
    private static boolean started = false;
    private static Random random = Random.create(13);
    private static Random random2 = Random.create(8767);

    private static PositionedSoundInstance jumpScareSound;

    public static void doJumpscare(ShaderProgram program, MinecraftClient client, PlayerComponent component){
        if(!started){
            startTime = System.currentTimeMillis();
            started = true;
            if(client.player != null) {
                jumpScareSound = new PositionedSoundInstance(ModSounds.JUMPSCARE, SoundCategory.HOSTILE, 1.0f, 1.0f, client.player.getRandom(), client.player.getBlockPos());
                client.getSoundManager().play(jumpScareSound);
            }
        }

        ExtraUtils.stopAllOtherSounds(jumpScareSound.getId(), client.getSoundManager().soundSystem);
        client.options.hudHidden = true;
        program.setInt("Jumpscare", 1);

        long currentTime = (System.currentTimeMillis() - startTime);

        if(currentTime >= 2000 && currentTime < 12000) {
            program.setInt("CreepyFace1", 1);
        }

        if(currentTime >= 12000) {
            program.setInt("CreepyFace2", 1);
            program.setVector("Rand",  random.nextFloat(), random2.nextFloat());
        }

        if(currentTime >= 14000) {
            component.setBeingCaptured(false);
            client.options.hudHidden = false;
            started = false;
            startTime = 0L;

            PacketByteBuf buffer = PacketByteBufs.create();
            buffer.writeBoolean(component.isBeingCaptured());
            ClientPlayNetworking.send(InitializePackets.BEING_CAPTURED_SYNC, buffer);

            program.setInt("Jumpscare", 0);
            program.setInt("CreepyFace1", 0);
            program.setInt("CreepyFace2", 0);
            program.setVector("Rand", 0, 0);
        }

    }

}

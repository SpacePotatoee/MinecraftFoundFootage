package com.sp.render;

import com.sp.util.MathStuff;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import org.joml.Vector3f;

/**
 * Generates the samples for the SSAO Shader<p>
 * This saves A LOT of processing power
 */
public class SSAOSamples {
    private static final int MaxSamples = 50;
    private static final float radius = 0.5f;
    private static Vector3f[] Samples;

    public static Vector3f[] getSSAOSamples(){
        if(Samples == null){
            Samples = generateSSAOSamples();
        }

        return Samples;
    }

    public static Vector3f[] generateSSAOSamples(){
        Random random = Random.create();
        Vector3f[] list = new Vector3f[MaxSamples];

        for(int i = 0; i < MaxSamples; i++){
            list[i] = new Vector3f(
                    MathStuff.randomFloat(-1f,1f, random),
                    MathStuff.randomFloat(-1f,1f, random),
                    MathStuff.randomFloat(0f,1f, random)
            );

            list[i].normalize();
            list[i].mul(MathStuff.randomFloat(0f, radius, random));

//            float scale = (float) i / MaxSamples;
//            list[i].mul(MathHelper.lerp(scale, 0.0f, radius));
        }
        return list;
    }

}

package com.sp.render;

import com.google.common.collect.ImmutableMap;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormatElement;
import static net.minecraft.client.render.VertexFormats.*;

public class VertexFormats {
    public static final VertexFormat BLOCKS;
    public static final VertexFormat PBR;

    //For some reason setting the Component Type to INT breaks everything
    private static final VertexFormatElement FLOAT = new VertexFormatElement(
            0,
            VertexFormatElement.ComponentType.FLOAT,
            VertexFormatElement.Type.GENERIC,
            1
    );


    static {
        ImmutableMap.Builder<String, VertexFormatElement> blockElements = ImmutableMap.builder();
        blockElements.put("Position", POSITION_ELEMENT);
        blockElements.put("Color", COLOR_ELEMENT);
        blockElements.put("UV0", TEXTURE_ELEMENT);
        blockElements.put("UV2", LIGHT_ELEMENT);
        blockElements.put("Normal", NORMAL_ELEMENT);
        blockElements.put("Padding", PADDING_ELEMENT);
        blockElements.put("Material", FLOAT);

        BLOCKS = new VertexFormat(blockElements.build());


        ImmutableMap.Builder<String, VertexFormatElement> blockElements2 = ImmutableMap.builder();
        blockElements2.put("Position", POSITION_ELEMENT);
        blockElements2.put("Color", COLOR_ELEMENT);
        blockElements2.put("UV0", TEXTURE_ELEMENT);
        blockElements2.put("UV2", LIGHT_ELEMENT);
        blockElements2.put("Normal", NORMAL_ELEMENT);
        blockElements2.put("Zoom", FLOAT);
        blockElements2.put("Resolution", FLOAT);
        blockElements2.put("EnableHeight", FLOAT);
        blockElements2.put("DepthMultiplier", FLOAT);

        PBR = new VertexFormat(blockElements2.build());
    }
}

package com.sp.render;

import com.google.common.collect.ImmutableMap;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormatElement;

public class VertexFormats {
    public static final VertexFormat BLOCKS;
    private static final VertexFormatElement MATERIAL = new VertexFormatElement(
            0, VertexFormatElement.ComponentType.FLOAT, VertexFormatElement.Type.GENERIC, 1
    );


    static {
        ImmutableMap.Builder<String, VertexFormatElement> blockElements = ImmutableMap.builder();

        blockElements.put("Position", net.minecraft.client.render.VertexFormats.POSITION_ELEMENT);
        blockElements.put("Color", net.minecraft.client.render.VertexFormats.COLOR_ELEMENT);
        blockElements.put("UV0", net.minecraft.client.render.VertexFormats.TEXTURE_ELEMENT);
        blockElements.put("UV2", net.minecraft.client.render.VertexFormats.LIGHT_ELEMENT);
        blockElements.put("Normal", net.minecraft.client.render.VertexFormats.NORMAL_ELEMENT);
        blockElements.put("Padding", net.minecraft.client.render.VertexFormats.PADDING_ELEMENT);
        blockElements.put("Material", MATERIAL);

        BLOCKS = new VertexFormat(blockElements.build());
    }
}

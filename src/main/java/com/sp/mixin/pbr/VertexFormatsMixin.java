package com.sp.mixin.pbr;

import com.google.common.collect.ImmutableMap;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormatElement;
import net.minecraft.client.render.VertexFormats;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(VertexFormats.class)
public class VertexFormatsMixin {
//    @Shadow @Final public static VertexFormatElement POSITION_ELEMENT;
//    @Shadow @Final public static VertexFormatElement COLOR_ELEMENT;
//    @Shadow @Final public static VertexFormatElement TEXTURE_ELEMENT;
//    @Shadow @Final public static VertexFormatElement LIGHT_ELEMENT;
//    @Shadow @Final public static VertexFormatElement NORMAL_ELEMENT;
//    @Shadow @Final public static VertexFormatElement PADDING_ELEMENT;
//
//
//    @Unique
//
//
//
//    @Shadow public static final VertexFormat POSITION_COLOR_TEXTURE_LIGHT_NORMAL = new VertexFormat(
//            ImmutableMap.<String, VertexFormatElement>builder()
//                    .put("Position", POSITION_ELEMENT)
//                    .put("Color", COLOR_ELEMENT)
//                    .put("UV0", TEXTURE_ELEMENT)
//                    .put("UV2", LIGHT_ELEMENT)
//                    .put("Normal", NORMAL_ELEMENT)
//                    .put("Padding", PADDING_ELEMENT)
//                    .put("Material", MATERIAL)
//                    .build()
//    );

}

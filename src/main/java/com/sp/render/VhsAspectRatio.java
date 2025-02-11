package com.sp.render;

import com.google.common.collect.Lists;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.client.util.VideoMode;

import java.util.List;
import java.util.Optional;

public class VhsAspectRatio {

    public static List<VideoMode> vhsAspectRatiosList = Lists.<VideoMode>newArrayList();
    public static List<VideoMode> normalVideoModesList = Lists.<VideoMode>newArrayList();

    public static SimpleOption<Integer> normalVideoMode;
    public static SimpleOption<Integer> vhsVideoMode;

    public static Optional<VideoMode> currentNormalVideoMode;
    public static Optional<VideoMode> currentVhsVideoMode;

}

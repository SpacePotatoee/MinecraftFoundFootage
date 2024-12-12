package com.sp;

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class Keybinds {
    public static KeyBinding toggleFlashlight;
    public static KeyBinding Zoom;
    public static KeyBinding toggleEvent;

    public static void inizializeKeyBinds() {
        toggleFlashlight = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.spb-revamped.toggle_flashlight", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_R, "spb-revamped.keybinds"));
        Zoom = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.spb-revamped.zoom", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_Z, "spb-revamped.keybinds"));
        toggleEvent = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.spb-revamped.toggle_event", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_SEMICOLON, "spb-revamped.keybinds"));
    }

}

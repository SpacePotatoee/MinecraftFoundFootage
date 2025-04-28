package com.sp.init;

import com.sp.compat.modmenu.ConfigStuff;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class HelpfulHintManager {
    private static final Text flashlightHint = Text.translatable("flashlight.hint", com.sp.Keybinds.toggleFlashlight.getBoundKeyLocalizedText().copyContentOnly().formatted(Formatting.BOLD, Formatting.UNDERLINE));
    private static final Text suffocateHint = Text.translatable("noclip.hint");

    public static boolean turnedOnFlashlight;
    public static boolean wentToBackrooms;

    //Not persistant after quitting the game but its good enough
    public static void sendMessages(ClientPlayerEntity player){
        if (ConfigStuff.enableHint) {
            if (!turnedOnFlashlight) {
                player.sendMessage(flashlightHint);
            }

            if (!wentToBackrooms) {
                player.sendMessage(suffocateHint);
            }
        }
    }

    public static void disableFlashlightHint() {
        turnedOnFlashlight = true;
    }

    public static void disableSuffocateHint() {
        wentToBackrooms = true;
    }

}

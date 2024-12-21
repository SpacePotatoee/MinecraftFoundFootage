package com.sp.entity.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.GameOptions;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class SkinWalkerCapturedFlavorText {
    private static final MinecraftClient client = MinecraftClient.getInstance();
    private static final GameOptions options = client.options;
    private static boolean shownMovementText = false;
    public static boolean triedToOpenInventory = false;
    private static boolean shownInventoryText = false;
    public static boolean triedToLeave = false;
    private static boolean shownTriedToLeaveText = false;
    public static boolean triedToChat = false;
    private static boolean shownTriedToChatText = false;
    private static int textCount = 0;
    private static boolean shownTextTaunt = false;
    private static int tick = 0;


    public static void tickFlavorText(PlayerEntity player) {
        if(isPressingMoveKeys()){
            if(!shownMovementText){
                player.sendMessage(Text.literal("You can't move anything. ").append(Text.literal("This isn't your body anymore").formatted(Formatting.RED)));
                 shownMovementText = true;
                textCount++;
            }
        }

        if(triedToOpenInventory) {
            if(!shownInventoryText) {
                player.sendMessage(Text.literal("That isn't your inventory anymore").formatted(Formatting.RED));
                shownInventoryText = true;
                textCount++;
            }
        }

        if(triedToLeave) {
            if(!shownTriedToLeaveText) {
                player.sendMessage(Text.literal("There is no escape").formatted(Formatting.RED));
                shownTriedToLeaveText = true;
                textCount++;
            }
        }

        if(triedToChat) {
            if(!shownTriedToChatText) {
                player.sendMessage(Text.literal("No one can hear you scream").formatted(Formatting.RED));
                shownTriedToChatText = true;
                textCount++;
            }
        }

        if(!shownTextTaunt) {
            if(textCount >= 2) {
                tick++;

                if(tick == 100){
                    player.sendMessage(Text.literal("Go ahead").formatted(Formatting.RED));
                }

                if(tick == 120){
                    player.sendMessage(Text.literal("Try to call out to your friends (T)").formatted(Formatting.GOLD));
                    shownTextTaunt = true;
                }
            }
        }


    }

    private static boolean isPressingMoveKeys() {
        return options.forwardKey.isPressed() || options.backKey.isPressed() || options.leftKey.isPressed() || options.rightKey.isPressed() || options.jumpKey.isPressed() || options.sneakKey.isPressed();
    }


}

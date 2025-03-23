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
                player.sendMessage(Text.translatable("skinwalker.flavor-text.move1").append(Text.translatable("skinwalker.flavor-text.move2").formatted(Formatting.RED)));
                 shownMovementText = true;
                textCount++;
            }
        }

        if(triedToOpenInventory) {
            if(!shownInventoryText) {
                player.sendMessage(Text.translatable("skinwalker.flavor-text.inventory").formatted(Formatting.RED));
                shownInventoryText = true;
                textCount++;
            }
        }

        if(triedToLeave) {
            if(!shownTriedToLeaveText) {
                player.sendMessage(Text.translatable("skinwalker.flavor-text.leave").formatted(Formatting.RED));
                shownTriedToLeaveText = true;
                textCount++;
            }
        }

        if(triedToChat) {
            if(!shownTriedToChatText) {
                player.sendMessage(Text.translatable("skinwalker.flavor-text.chat").formatted(Formatting.RED));
                shownTriedToChatText = true;
                textCount++;
            }
        }

        if(!shownTextTaunt && !shownTriedToChatText) {
            if(textCount >= 2) {
                tick++;

                if(tick == 100){
                    player.sendMessage(Text.translatable("skinwalker.flavor-text.taunt1").formatted(Formatting.RED));
                }

                if(tick == 135){
                    player.sendMessage(Text.translatable("skinwalker.flavor-text.taunt2").formatted(Formatting.GOLD));

                }

                if(tick == 300){
                    player.sendMessage(Text.translatable("skinwalker.flavor-text.taunt3").append(Text.translatable("skinwalker.flavor-text.taunt4")).formatted(Formatting.RED));
                    shownTextTaunt = true;
                }
            }
        }


    }

    private static boolean isPressingMoveKeys() {
        return options.forwardKey.isPressed() || options.backKey.isPressed() || options.leftKey.isPressed() || options.rightKey.isPressed() || options.jumpKey.isPressed() || options.sneakKey.isPressed();
    }


}

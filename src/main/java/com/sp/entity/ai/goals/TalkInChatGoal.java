package com.sp.entity.ai.goals;

import com.sp.cca_stuff.InitializeComponents;
import com.sp.cca_stuff.SkinWalkerComponent;
import com.sp.entity.custom.SkinWalkerEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.random.Random;

import java.util.List;

public class TalkInChatGoal extends Goal {
    private final Random random = Random.create();
    private final SkinWalkerEntity entity;
    private final SkinWalkerComponent component;
    private final float initialDelay;
    private int sendThreadCooldown;
    private int sendMessageCooldown;
    private int currentThread;
    private int currentMessage;

    public TalkInChatGoal(SkinWalkerEntity entity, float delayInSeconds){
        this.entity = entity;
        this.component = InitializeComponents.SKIN_WALKER.get(entity);
        this.initialDelay = delayInSeconds * 10;
        this.currentThread = 1;
        this.currentMessage = 0;
    }

    @Override
    public boolean canStart() {
        if(this.entity.age > this.initialDelay){
            if(!this.component.shouldActNatural()) {
                if(this.sendThreadCooldown <= 0){
                    return true;
                } else {
                    this.sendThreadCooldown--;
                    return false;
                }
            }
        }
        return false;
    }
//
//    @Override
//    public boolean shouldContinue() {
//        return component.isTypingInChat();
//    }
//
//    @Override
//    public void start() {
//        this.component.setTypingInChat(true);
//        this.sendMessageCooldown = 70;
//        this.currentMessage = 0;
//    }
//
//    @Override
//    public void tick() {
//        if(!this.entity.getWorld().isClient) {
//            System.out.println("TICKING");
//            switch (this.currentThread) {
//                case 1: getThread1(); break;
//                case 2: getThread2(); break;
//                case 3: getThread3(); break;
//            }
//        }
//    }
//
//    private void getThread1() {
//        if(this.sendMessageCooldown <= 0) {
//            if(this.currentMessage == 0) {
//                this.sendMessage("My mic is broken");
//                this.sendMessageCooldown = 40;
//                this.currentMessage++;
//
//            } else if (this.currentMessage == 1) {
//                this.sendMessage("I can't hear anything either");
//                this.currentMessage++;
//
//            } else {
//                this.currentThread++;
//                this.sendMessageCooldown = 0;
//                this.currentMessage = 0;
//                this.sendThreadCooldown = random.nextBetween(100, 101);
//                this.component.setTypingInChat(false);
//            }
//
//        } else {
//            this.sendMessageCooldown--;
//        }
//    }
//
//    private void getThread2() {
//        if(this.sendMessageCooldown <= 0){
//            if(this.currentMessage == 0){
//                this.sendMessage("my mic is broken :( part 2");
//                this.sendMessageCooldown = 40;
//                this.currentMessage++;
//
//            } else if (this.currentMessage == 1) {
//                this.sendMessage("can't hear anything either part 2");
//                this.currentMessage++;
//
//            } else {
//                this.currentThread++;
//                this.sendMessageCooldown = 0;
//                this.currentMessage = 0;
//                this.sendThreadCooldown = random.nextBetween(100, 101);
//                this.component.setTypingInChat(false);
//            }
//
//        } else {
//            this.sendMessageCooldown--;
//        }
//    }
//
//    private void getThread3() {
//        if(this.sendMessageCooldown <= 0){
//            if(this.currentMessage == 0){
//                this.sendMessage("my mic is broken :( part 3");
//                this.sendMessageCooldown = 40;
//                this.currentMessage++;
//
//            } else if (this.currentMessage == 1) {
//                this.sendMessage("can't hear anything either part 3");
//                this.currentMessage++;
//
//            } else {
//                this.currentThread++;
//                this.sendMessageCooldown = 0;
//                this.currentMessage = 0;
//                this.sendThreadCooldown = random.nextBetween(100, 101);
//                this.component.setTypingInChat(false);
//            }
//
//        } else {
//            this.sendMessageCooldown--;
//        }
//    }
//
//    private void sendMessage(String string){
//        PlayerEntity target = this.entity.getWorld().getPlayerByUuid(component.getTargetPlayerUUID());
//        if(target != null) {
//            List<? extends PlayerEntity> players = this.entity.getWorld().getPlayers();
//            for (PlayerEntity player : players) {
//                player.sendMessage(Text.literal("<" + target.getName().getString() +"> " + string));
//            }
//        }
//    }
}

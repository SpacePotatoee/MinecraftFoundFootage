package com.sp.cca_stuff;

import com.sp.SPBRevamped;
import com.sp.entity.custom.SkinWalkerEntity;
import com.sp.init.BackroomsLevels;
import com.sp.init.ModEntities;
import com.sp.init.ModSounds;
import com.sp.sounds.voicechat.BackroomsVoicechatPlugin;
import com.sp.world.events.AbstractEvent;
import com.sp.world.levels.BackroomsLevel;
import com.sp.world.levels.custom.Level0BackroomsLevel;
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import dev.onyxstudios.cca.api.v3.component.tick.ServerTickingComponent;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.GameMode;
import net.minecraft.world.World;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class WorldEvents implements AutoSyncedComponent, ServerTickingComponent {
    private World world;

    private AbstractEvent activeEvent;
    public int ticks;
    private int delay;

    private UUID nullUUID = UUID.randomUUID();
    private UUID activeSkinwalkerTarget;
    public SkinWalkerEntity activeSkinWalkerEntity;

    public boolean done;
    private int tick;

    public WorldEvents(World world) {
        this.world = world;
        this.ticks = 0;
        this.delay = 1800;
        this.activeSkinwalkerTarget = nullUUID;

        this.done = false;
    }

    public void setActiveEvent(AbstractEvent activeEvent) {
        this.activeEvent = activeEvent;
    }
    public AbstractEvent getActiveEvent() {
        return this.activeEvent;
    }

    public PlayerEntity getActiveSkinwalkerTarget() {
        if(this.activeSkinwalkerTarget == null || this.activeSkinwalkerTarget.equals(nullUUID)){
            return null;
        }
        return this.world.getPlayerByUuid(this.activeSkinwalkerTarget);
    }
    public void setActiveSkinwalkerTarget(UUID uuid) {
        this.activeSkinwalkerTarget = uuid;
        this.sync();
    }

    public void sync() {
        InitializeComponents.EVENTS.sync(this.world);
    }

    @Override
    public void readFromNbt(NbtCompound tag) {
        for (BackroomsLevel level: BackroomsLevels.BACKROOMS_LEVELS) {
            if (this.world.getRegistryKey() == level.getWorldKey()) {
                level.readFromNbt(tag);
            }
        }

        this.activeSkinwalkerTarget = tag.getUuid("activeSkinwalkerTarget");
        this.done = tag.getBoolean("skinwalkerDone");
    }

    @Override
    public void writeToNbt(NbtCompound tag) {
        for (BackroomsLevel level: BackroomsLevels.BACKROOMS_LEVELS) {
            if (this.world.getRegistryKey() == level.getWorldKey()) {
                level.writeToNbt(tag);
            }
        }

        tag.putUuid("activeSkinwalkerTarget", this.activeSkinwalkerTarget);
        tag.putBoolean("skinwalkerDone", this.done);
    }

    @Override
    public void serverTick() {
        if (world != null && !world.getPlayers().isEmpty() && BackroomsLevels.isInBackrooms(world.getRegistryKey())) {
            ticks++;

            tickWorldEvents();
            //Start Looking for a player to take and take them when they're not talking and can't be seen
            tickSkinWalkerCapturing();

            shouldReleasePlayer();
        }

        shouldSync();
    }

    private void shouldReleasePlayer() {
        if (!(BackroomsLevels.getLevel(world) instanceof Level0BackroomsLevel level0BackroomsLevel)) {
            return;
        }

        if (this.getActiveSkinwalkerTarget() != null && this.activeSkinWalkerEntity == null) {
            ServerPlayerEntity target = (ServerPlayerEntity) this.getActiveSkinwalkerTarget();
            PlayerComponent targetComponent = InitializeComponents.PLAYER.get(target);

            if (targetComponent.hasBeenCaptured() || targetComponent.isBeingCaptured()) {
                target.changeGameMode(GameMode.SURVIVAL);
                targetComponent.setHasBeenCaptured(false);
                targetComponent.setShouldBeMuted(false);
                targetComponent.sync();
                SPBRevamped.sendPersonalPlaySoundPacket(target, ModSounds.SKINWALKER_RELEASE, 1.0f, 1.0f);
            }

            this.activeSkinwalkerTarget = nullUUID;

            return;
        }

        if (activeSkinWalkerEntity == null) {
            return;
        }

        SkinWalkerComponent component = InitializeComponents.SKIN_WALKER.get(this.activeSkinWalkerEntity);

        if (!component.shouldBeginRelease()) {
            if (this.getActiveSkinwalkerTarget() != null) {
                ((ServerPlayerEntity) this.getActiveSkinwalkerTarget()).changeGameMode(GameMode.SPECTATOR);
                ((ServerPlayerEntity) this.getActiveSkinwalkerTarget()).setCameraEntity(this.activeSkinWalkerEntity);
            }

            return;
        }

        PlayerComponent targetComponent = InitializeComponents.PLAYER.get(this.getActiveSkinwalkerTarget());
        ServerPlayerEntity target = (ServerPlayerEntity) this.getActiveSkinwalkerTarget();
        tick++;

        if (this.tick == 1) {
            level0BackroomsLevel.setLightState(Level0BackroomsLevel.LightState.FLICKER);
        }

        if (this.tick == 80) {
            level0BackroomsLevel.setLightState(Level0BackroomsLevel.LightState.OFF);

            targetComponent.setBeingReleased(true);
            targetComponent.sync();

            SPBRevamped.sendPersonalPlaySoundPacket(target, ModSounds.SKINWALKER_RELEASE, 1.0f, 1.0f);

            target.changeGameMode(targetComponent.getPrevGameMode() != null ? targetComponent.getPrevGameMode() : GameMode.SURVIVAL);
            target.setCameraEntity(target);

            for (PlayerEntity player : this.world.getPlayers()) {
                PlayerComponent playerComponent = InitializeComponents.PLAYER.get(player);
                playerComponent.setFlashLightOn(false);
                playerComponent.sync();
            }
            this.activeSkinWalkerEntity.discard();
        }

        if (this.tick >= 105) {
            level0BackroomsLevel.setLightState(Level0BackroomsLevel.LightState.ON);
            targetComponent.setBeingReleased(false);
            targetComponent.setHasBeenCaptured(false);
            targetComponent.setShouldBeMuted(false);
            targetComponent.sync();
            this.activeSkinWalkerEntity = null;
        }
    }

    private void tickSkinWalkerCapturing() {
        if (!(BackroomsLevels.getLevel(world) instanceof Level0BackroomsLevel level0BackroomsLevel)) {
            return;
        }

        if (level0BackroomsLevel.getIntercomCount() < 2 || world.getPlayers().size() <= 1) {
            return;
        }

        if (done || this.world.getRegistryKey() != BackroomsLevels.LEVEL0_WORLD_KEY) {
            return;
        }

        if(this.activeSkinWalkerEntity != null){
            return;
        }
        //Thank goodness for https://stackoverflow.com/questions/2776176/get-minvalue-of-a-mapkey-double
        Map.Entry<UUID, Float> min = null;
        for (Map.Entry<UUID, Float> entry : BackroomsVoicechatPlugin.speakingTime.entrySet()) {
            if (min == null || min.getValue() > entry.getValue()) {
                min = entry;

            }
        }

        if (min != null) {
            PlayerEntity target = this.world.getPlayerByUuid(min.getKey());
            if (target != null && target.isAlive()) {
                this.setActiveSkinwalkerTarget(target.getUuid());
            }
        }

        if (this.getActiveSkinwalkerTarget() == null) {
            return;
        }

        PlayerEntity target = this.getActiveSkinwalkerTarget();
        PlayerComponent targetComponent = InitializeComponents.PLAYER.get(target);

        if (targetComponent.isSpeaking()) {
            return;
        }

        List<PlayerEntity> playerEntityList = target.getWorld().getPlayers(
                TargetPredicate.DEFAULT
                        .ignoreDistanceScalingFactor()
                        .ignoreVisibility()
                        .setBaseMaxDistance(50)
                        .setPredicate(EntityPredicates.EXCEPT_CREATIVE_OR_SPECTATOR::test),
                target,
                target.getBoundingBox().expand(50));

        boolean seen = false;
        for (PlayerEntity player : playerEntityList) {
            if (player != target) {
                PlayerComponent playerComponent = InitializeComponents.PLAYER.get(player);
                if (playerComponent.canSeeActiveSkinWalkerTarget()) {
                    seen = true;
                    break;
                }
            }
        }

        if (seen) {
            return;
        }
        //Take em
        SkinWalkerEntity skinWalkerEntity = ModEntities.SKIN_WALKER_ENTITY.create(this.world);
        if (skinWalkerEntity == null) {
            return;
        }

        skinWalkerEntity.refreshPositionAndAngles(target.getX(), target.getY(), target.getZ(), target.getYaw(), target.getPitch());
        skinWalkerEntity.setVelocity(target.getVelocity());
        this.world.spawnEntity(skinWalkerEntity);
        this.activeSkinWalkerEntity = skinWalkerEntity;

        targetComponent.setPrevGameMode(((ServerPlayerEntity) target).interactionManager.getGameMode());
        targetComponent.setBeingCaptured(true);
        targetComponent.setHasBeenCaptured(true);
        targetComponent.setShouldBeMuted(true);
        targetComponent.sync();

        ((ServerPlayerEntity) target).changeGameMode(GameMode.SPECTATOR);
        ((ServerPlayerEntity) target).setCameraEntity(skinWalkerEntity);
        this.done = true;
        this.sync();
    }

    private void tickWorldEvents() {
        if (activeEvent == null) {
            this.delay--;
            if (this.delay > 0) {
                return;
            }

            this.delay = 0;

            BackroomsLevel currentDimension = BackroomsLevels.getLevel(world);

            if (currentDimension == null) {
                return;
            }

            this.activeEvent = currentDimension.getRandomEvent(world);
            activeEvent.init(this.world);
            ticks = 0;
            this.delay = currentDimension.nextEventDelay();

            return;
        }

        if (activeEvent.duration() <= ticks) {
            activeEvent.reset(this.world);
            if (activeEvent.isDone()) activeEvent = null;
        } else {
            activeEvent.ticks(ticks, this.world);
        }
    }

    private void shouldSync() {
        boolean sync = false;

        for (BackroomsLevel backroomsLevel : BackroomsLevels.BACKROOMS_LEVELS) {
            if (backroomsLevel.shouldSync()) {
                sync = true;
                break;
            }
        }

        if (sync){
            this.sync();
        }
    }
}

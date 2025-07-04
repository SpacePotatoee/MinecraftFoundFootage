package com.sp.cca_stuff;

import com.sp.SPBRevamped;
import com.sp.clientWrapper.ClientWrapper;
import com.sp.entity.custom.SmilerEntity;
import com.sp.init.*;
import com.sp.mixininterfaces.ServerPlayNetworkSprint;
import com.sp.sounds.voicechat.BackroomsVoicechatPlugin;
import com.sp.world.levels.BackroomsLevel;
import com.sp.world.levels.custom.Level2BackroomsLevel;
import dev.onyxstudios.cca.api.v3.component.ComponentProvider;
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import dev.onyxstudios.cca.api.v3.component.tick.ClientTickingComponent;
import dev.onyxstudios.cca.api.v3.component.tick.ServerTickingComponent;
import net.fabricmc.fabric.api.dimension.v1.FabricDimensions;
import net.minecraft.block.Blocks;
import net.minecraft.client.sound.MovingSoundInstance;
import net.minecraft.entity.Entity;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.network.packet.s2c.play.StopSoundS2CPacket;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameMode;
import net.minecraft.world.TeleportTarget;
import net.minecraft.world.World;

import java.util.List;
import java.util.Optional;
import java.util.Random;

import static com.sp.SPBRevamped.SLOW_SPEED_MODIFIER;

@SuppressWarnings("DataFlowIssue")
public class PlayerComponent implements AutoSyncedComponent, ClientTickingComponent, ServerTickingComponent {
    public final PlayerEntity player;
    private final SimpleInventory playerSavedMainInventory = new SimpleInventory(36);
    private final SimpleInventory playerSavedArmorInventory = new SimpleInventory(4);
    private final SimpleInventory playerSavedOffhandInventory = new SimpleInventory(1);
    private final Random random = new Random();

    private int smilerSpawnDelay = 80;

    private int stamina;
    private boolean tired;

    private int scrollingInInventoryTime;

    private boolean flashLightOn;
    private boolean shouldRender;
    private boolean isDoingCutscene;
    private boolean playingGlitchSound;
    private boolean shouldNoClip;
    private int teleportingTimer;
    private boolean isTeleporting;

    public int suffocationTimer;
    private boolean shouldDoStatic;

    private boolean isBeingCaptured;
    public boolean hasBeenCaptured;
    private boolean isBeingReleased;
    private Entity targetEntity;
    private int skinWalkerLookDelay;
    private boolean shouldBeMuted;
    private boolean isSpeaking;
    private int speakingBuffer;
    private float prevSpeakingTime;
    private boolean visibleToEntity;
    private int visibilityTimer;
    private int visibilityTimerCooldown;
    private boolean talkingTooLoud;
    private int talkingTooLoudTimer;
    private GameMode prevGameMode;

    public MovingSoundInstance DeepAmbience;
    public MovingSoundInstance GasPipeAmbience;
    public MovingSoundInstance WaterPipeAmbience;
    public MovingSoundInstance WarpAmbience;
    public MovingSoundInstance PoolroomsNoonAmbience;
    public MovingSoundInstance PoolroomsSunsetAmbience;
    public MovingSoundInstance GlitchAmbience;
    public MovingSoundInstance SmilerAmbience;
    public MovingSoundInstance WindAmbience;
    public MovingSoundInstance WindTunnelAmbience;

    private boolean canSeeActiveSkinWalker;
    private boolean prevFlashLightOn;

    public float glitchTimer;
    private boolean shouldGlitch;
    public int glitchTick;
    public boolean shouldInflictGlitchDamage;

    public PlayerComponent(PlayerEntity player){
        this.stamina = 300;
        this.tired = false;
        this.scrollingInInventoryTime = 0;
        this.player = player;
        this.flashLightOn = false;
        this.shouldRender = true;
        this.shouldNoClip = false;
        this.shouldDoStatic = false;

        this.isDoingCutscene = false;

        this.isBeingCaptured = false;
        this.hasBeenCaptured = false;
        this.isBeingReleased = false;
        this.skinWalkerLookDelay = 60;
        this.shouldBeMuted = false;
        this.isSpeaking = false;
        this.speakingBuffer = 80;
        this.prevSpeakingTime = 0;
        this.visibleToEntity = false;
        this.visibilityTimer = 15;
        this.visibilityTimerCooldown = 0;

        this.talkingTooLoud = false;
        this.talkingTooLoudTimer = 20;

        this.suffocationTimer = 0;

        this.canSeeActiveSkinWalker = false;

        this.glitchTimer = 0.0f;
        this.shouldGlitch = false;
        this.glitchTick = 0;

        this.teleportingTimer = -1;
    }

    public void savePlayerInventory() {
        PlayerInventory inventory = this.player.getInventory();
        DefaultedList<ItemStack> mainInventory = inventory.main;
        DefaultedList<ItemStack> armorInventory = inventory.armor;
        DefaultedList<ItemStack> offHand = inventory.offHand;

        this.saveInventory(mainInventory, this.playerSavedMainInventory);
        this.saveInventory(armorInventory, this.playerSavedArmorInventory);
        this.saveInventory(offHand, this.playerSavedOffhandInventory);
    }

    public void loadPlayerSavedInventory() {
        PlayerInventory inventory = this.player.getInventory();
        inventory.clear();
        DefaultedList<ItemStack> mainInventory = inventory.main;
        DefaultedList<ItemStack> armorInventory = inventory.armor;
        DefaultedList<ItemStack> offHand = inventory.offHand;

        this.loadInventory(mainInventory, this.playerSavedMainInventory);
        this.loadInventory(armorInventory, this.playerSavedArmorInventory);
        this.loadInventory(offHand, this.playerSavedOffhandInventory);

        this.playerSavedMainInventory.clear();
    }

    private void saveInventory(DefaultedList<ItemStack> inventory1, Inventory inventory2){
        for (int i = 0; i < inventory1.size(); i++) {
            ItemStack itemStack = inventory1.get(i);
            if (!itemStack.isEmpty()) {
                inventory2.setStack(i, itemStack);
            }
        }
    }

    private void loadInventory(DefaultedList<ItemStack> inventory1, Inventory inventory2){
        for (int i = 0; i < inventory2.size(); i++) {
            ItemStack itemStack = inventory2.getStack(i);
            if (!itemStack.isEmpty()) {
                inventory1.set(i, itemStack);
            }
        }
    }

    public int getTeleportingTimer() {
        return teleportingTimer;
    }

    public void setTeleportingTimer(int teleportingTimer) {
        this.teleportingTimer = teleportingTimer;
        this.justChanged();
    }

    public boolean isTeleporting() {
        return isTeleporting;
    }
    public void setTeleporting(boolean teleporting) {
        isTeleporting = teleporting;
    }

    public int getStamina() {
        return stamina;
    }
    public void setStamina(int stamina) {
        this.stamina = stamina;
    }

    public boolean isTired() {
        return tired;
    }
    public void setTired(boolean tired) {
        this.tired = tired;
    }

    public int getScrollingInInventoryTime() {
        return scrollingInInventoryTime;
    }
    public void setScrollingInInventoryTime(int scrollingInInventoryTime) {
        this.scrollingInInventoryTime = scrollingInInventoryTime;
    }

    public boolean isShouldRender() {
        return shouldRender;
    }
    public void setShouldRender(boolean shouldRender) {
        this.shouldRender = shouldRender;
    }

    public void setFlashLightOn(boolean set){
        this.flashLightOn = set;
    }
    public boolean isFlashLightOn() {
        return flashLightOn;
    }

    public boolean isDoingCutscene() {
        return isDoingCutscene;
    }
    public void setDoingCutscene(boolean doingCutscene) {
        isDoingCutscene = doingCutscene;
    }

    public boolean isTeleportingToPoolrooms() {
        return BackroomsLevels.getLevel(this.player.getWorld()).orElse(BackroomsLevels.OVERWORLD_REPRESENTING_BACKROOMS_LEVEL) instanceof Level2BackroomsLevel && this.teleportingTimer > 0;
    }

    public boolean shouldNoClip() {
        return shouldNoClip;
    }
    public void setShouldNoClip(boolean shouldNoClip) {
        this.shouldNoClip = shouldNoClip;
    }

    public boolean isShouldDoStatic() {
        return shouldDoStatic;
    }
    public void setShouldDoStatic(boolean shouldDoStatic) {
        this.shouldDoStatic = shouldDoStatic;
    }

    public boolean isBeingCaptured() {return isBeingCaptured;}
    public void setBeingCaptured(boolean beingCaptured) {isBeingCaptured = beingCaptured;}

    public boolean hasBeenCaptured() {return hasBeenCaptured;}
    public void setHasBeenCaptured(boolean hasBeenCaptured) {this.hasBeenCaptured = hasBeenCaptured;}

    public boolean isBeingReleased() {
        return isBeingReleased;
    }
    public void setBeingReleased(boolean beingReleased) {
        isBeingReleased = beingReleased;
    }

    public Entity getTargetEntity() {return targetEntity;}
    public void setTargetEntity(Entity targetEntity) {
        this.targetEntity = targetEntity;
    }

    public int getSkinWalkerLookDelay() {
        return skinWalkerLookDelay;
    }
    public void setSkinWalkerLookDelay(int skinWalkerLookDelay) {
        this.skinWalkerLookDelay = skinWalkerLookDelay;
    }
    public void subtractSkinWalkerLookDelay() {
        this.skinWalkerLookDelay -= 1;
    }

    public boolean shouldBeMuted() {return shouldBeMuted;}
    public void setShouldBeMuted(boolean shouldStayUnmuted) {this.shouldBeMuted = shouldStayUnmuted;}

    public boolean isSpeaking() {
        return isSpeaking;
    }
    public void setSpeaking(boolean speaking) {
        isSpeaking = speaking;
    }

    public boolean isVisibleToEntity() {return visibleToEntity;}
    public void setVisibleToEntity(boolean visibleToEntity) {this.visibleToEntity = visibleToEntity;}

    public boolean isTalkingTooLoud() {
        return talkingTooLoud;
    }
    public void setTalkingTooLoud(boolean talkingTooLoud) {
        this.talkingTooLoud = talkingTooLoud;
    }

    public void resetTalkingTooLoudTimer(){
        this.talkingTooLoudTimer = 20;
    }

    public GameMode getPrevGameMode() {
        return prevGameMode;
    }
    public void setPrevGameMode(GameMode prevGameMode) {
        this.prevGameMode = prevGameMode;
    }

    public boolean canSeeActiveSkinWalkerTarget() {return canSeeActiveSkinWalker;}
    public void setCanSeeActiveSkinWalkerTarget(boolean canSeeActiveSkinWalker) {this.canSeeActiveSkinWalker = canSeeActiveSkinWalker;}

    public float getGlitchTimer() {
        return glitchTimer;
    }

    public boolean shouldGlitch() {
        return shouldGlitch;
    }
    public void setShouldGlitch(boolean shouldGlitch) {
        this.shouldGlitch = shouldGlitch;
    }

    public void setShouldInflictGlitchDamage(boolean shouldInflictGlitchDamage) {
        this.shouldInflictGlitchDamage = shouldInflictGlitchDamage;
    }

    @Override
    public void readFromNbt(NbtCompound tag) {
        this.stamina = tag.getInt("stamina");
        this.flashLightOn = tag.getBoolean("flashLightOn");
        this.shouldRender = tag.getBoolean("shouldRender");
        this.isDoingCutscene = tag.getBoolean("isDoingCutscene");
        this.playingGlitchSound = tag.getBoolean("playingGlitchSound");
        this.shouldNoClip = tag.getBoolean("shouldNoClip");
        this.shouldDoStatic = tag.getBoolean("shouldDoStatic");
        this.isBeingCaptured = tag.getBoolean("isBeingCaptured");
        this.hasBeenCaptured = tag.getBoolean("hasBeenCaptured");
        this.isBeingReleased = tag.getBoolean("isBeingReleased");
        this.shouldBeMuted = tag.getBoolean("shouldBeMuted");
        this.shouldGlitch = tag.getBoolean("shouldGlitch");
        this.shouldInflictGlitchDamage = tag.getBoolean("shouldInflictGlitchDamage");
        this.teleportingTimer = tag.getInt("teleportingTimer");

        this.playerSavedMainInventory.readNbtList(tag.getList("inventory", NbtElement.COMPOUND_TYPE));
        this.playerSavedOffhandInventory.readNbtList(tag.getList("inventoryOffHand", NbtElement.COMPOUND_TYPE));
        this.playerSavedArmorInventory.readNbtList(tag.getList("inventoryArmor", NbtElement.COMPOUND_TYPE));
    }

    @Override
    public void writeToNbt(NbtCompound tag) {
        tag.putInt("stamina", this.stamina);
        tag.putBoolean("flashLightOn", this.flashLightOn);
        tag.putBoolean("shouldRender", this.shouldRender);
        tag.putBoolean("isDoingCutscene", this.isDoingCutscene);
        tag.putBoolean("playingGlitchSound", this.playingGlitchSound);
        tag.putBoolean("shouldNoClip", this.shouldNoClip);
        tag.putBoolean("shouldDoStatic", this.shouldDoStatic);
        tag.putBoolean("isBeingCaptured", this.isBeingCaptured);
        tag.putBoolean("hasBeenCaptured", this.hasBeenCaptured);
        tag.putBoolean("isBeingReleased", this.isBeingReleased);
        tag.putBoolean("shouldBeMuted", this.shouldBeMuted);
        tag.putBoolean("shouldGlitch", this.shouldGlitch);
        tag.putBoolean("shouldInflictGlitchDamage", this.shouldInflictGlitchDamage);
        tag.putInt("teleportingTimer", this.teleportingTimer);

        if (BackroomsLevels.isInBackrooms(this.player.getWorld().getRegistryKey())) {
            tag.put("inventory", this.playerSavedMainInventory.toNbtList());
            tag.put("inventoryOffHand", this.playerSavedOffhandInventory.toNbtList());
            tag.put("inventoryOffHand", this.playerSavedArmorInventory.toNbtList());
        }
    }

    public void sync() {
        InitializeComponents.PLAYER.sync(this.player);
    }

    @Override
    public void clientTick() {
        ClientWrapper.tickClientPlayerComponent(this);
    }

    @Override
    public void serverTick() {
        getPrevSettings();

        //*Update Stamina
        updateStamina();

        //*Damage if glitched enough from smilers
        if(this.shouldInflictGlitchDamage){
            this.player.damage(ModDamageTypes.of(this.player.getWorld(), ModDamageTypes.SMILER), 1.0f);
        }

        //*Is speaking
        if(BackroomsVoicechatPlugin.speakingTime.containsKey(this.player.getUuid()) && BackroomsVoicechatPlugin.speakingTime.get(this.player.getUuid()) == this.prevSpeakingTime) {
            if(this.isSpeaking()) {
                this.speakingBuffer--;
                if (this.speakingBuffer <= 0) {
                    this.setSpeaking(false);
                    this.speakingBuffer = 80;
                }
            }
        } else {
            this.speakingBuffer = 80;
        }

        //*Cast him to the Backrooms
        if (checkBackroomsTeleport()) return;

        Optional<BackroomsLevel> backroomsLevel = BackroomsLevels.getLevel(this.player.getWorld());

        if (backroomsLevel.isPresent()) {
            BackroomsLevel level = backroomsLevel.get();

            List<BackroomsLevel.LevelTransition> teleports = level.checkForTransition(this, this.player.getWorld());

            if (!teleports.isEmpty()) {
                for (BackroomsLevel.CrossDimensionTeleport crossDimensionTeleport : teleports.get(0).callback().predicate(this.player.getWorld(), this, level)) {
                    if (crossDimensionTeleport.from().transitionOut(crossDimensionTeleport)) {
                        if (teleportingTimer == -1) {
                            this.setTeleportingTimer(teleports.get(0).duration());
                        }

                        if (teleportingTimer == 0) {
                            ServerWorld destination = crossDimensionTeleport.world().getServer().getWorld(crossDimensionTeleport.to().getWorldKey());
                            TeleportTarget target = new TeleportTarget(crossDimensionTeleport.pos(), crossDimensionTeleport.playerComponent().player.getVelocity(), crossDimensionTeleport.playerComponent().player.getYaw(), crossDimensionTeleport.playerComponent().player.getPitch());

                            if (crossDimensionTeleport.to() == BackroomsLevels.OVERWORLD_REPRESENTING_BACKROOMS_LEVEL) {
                                crossDimensionTeleport.playerComponent().sync();

                                if (this.player.getWorld().getGameRules().getBoolean(ModGamerules.STUCK_IN_BACKROOMS)) {
                                    destination = crossDimensionTeleport.world().getServer().getWorld(BackroomsLevels.LEVEL0_BACKROOMS_LEVEL.getWorldKey());
                                    target = new TeleportTarget(BackroomsLevels.LEVEL0_BACKROOMS_LEVEL.getSpawnPos(), crossDimensionTeleport.playerComponent().player.getVelocity(), crossDimensionTeleport.playerComponent().player.getYaw(), crossDimensionTeleport.playerComponent().player.getPitch());
                                }
                            }

                            FabricDimensions.teleport(crossDimensionTeleport.playerComponent().player, destination, target);
                            crossDimensionTeleport.to().transitionIn(crossDimensionTeleport);
                        }
                    }
                }
            }

            if (level == BackroomsLevels.LEVEL324_BACKROOMS_LEVEL && this.player.getWorld().getBlockState(this.player.getBlockPos().offset(Direction.DOWN, 3)).isOf(Blocks.RED_WOOL)) {
                this.player.teleport(this.player.getX(), this.player.getY() - 64, this.player.getZ());
            }

            if (level == BackroomsLevels.LEVEL324_BACKROOMS_LEVEL && this.player.getWorld().getBlockState(this.player.getBlockPos().offset(Direction.DOWN, 3)).isOf(Blocks.YELLOW_WOOL)) {
                this.player.teleport(this.player.getX(), this.player.getY() + 64, this.player.getZ());
            }
        }


        if (teleportingTimer >= 0) {
            this.setTeleportingTimer(teleportingTimer - 1);
        }

        if (BackroomsLevels.isInBackroomsLevel(player.getWorld(), BackroomsLevels.LEVEL324_BACKROOMS_LEVEL) && player.getPos().subtract(0, 64, 0).lengthSquared() > 10000 && player.getPos().y > 60) {
            summonSmilers();
        }

        //*Update Entity Visibility
        updateEntityVisibility();

        if(BackroomsVoicechatPlugin.speakingTime.containsKey(this.player.getUuid())) {
            this.prevSpeakingTime = BackroomsVoicechatPlugin.speakingTime.get(this.player.getUuid());
        }
        
        shouldSync();
    }

    private void summonSmilers() {
        if (this.smilerSpawnDelay < 0) {
            SmilerEntity smiler = ModEntities.SMILER_ENTITY.create(this.player.getWorld());

            BlockPos.Mutable mutable = new BlockPos.Mutable();
            float randomAngle = random.nextFloat() * 360.0f;
            Vec3d spawnPos = new Vec3d(0, 0, 15).rotateY(randomAngle).add(player.getPos());
            if (!this.player.getWorld().getBlockState(mutable.set(spawnPos.x, spawnPos.y, spawnPos.z)).blocksMovement()) {
                smiler.refreshPositionAndAngles(Math.floor(spawnPos.x) + 0.5f, spawnPos.y, Math.floor(spawnPos.z) + 0.5f, 0.0f, 0.0f);
                this.player.getWorld().spawnEntity(smiler);
                smilerSpawnDelay = 80;
            }
        }

        smilerSpawnDelay--;
    }

    private void updateStamina() {
        EntityAttributeInstance attributeInstance = this.player.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED);
        if(attributeInstance != null) {
            int prevStamina = this.stamina;
            if(!this.player.isCreative() && !this.player.isSpectator()){
                if(this.player.isSprinting()) {
                    this.stamina--;
                } else {
                    this.stamina = Math.min(this.stamina + 1, 300);
                }
            } else {
                this.stamina = 300;
            }

            if(this.stamina <= 0){
                this.stamina = 0;
                this.setTired(true);
            }

            if(this.isTired()){
                this.player.setSprinting(false);
                this.player.addExhaustion(0.05f);
                if(this.stamina > 200){
                    this.setTired(false);
                }
            } else if(!((ServerPlayNetworkSprint)((ServerPlayerEntity)this.player).networkHandler).getShouldStopSprinting()){
                this.player.setSprinting(true);
            }

            if ((!player.isSneaking() && !player.isSprinting()) || this.isTired()) {
                if(!attributeInstance.hasModifier(SLOW_SPEED_MODIFIER)) {
                    attributeInstance.addTemporaryModifier(SLOW_SPEED_MODIFIER);
                }
            } else if(attributeInstance.hasModifier(SLOW_SPEED_MODIFIER)) {
                attributeInstance.removeModifier(SLOW_SPEED_MODIFIER);
            }
            //*Mod by 20 to reduce packet count
            if(prevStamina != this.stamina && this.stamina % 20 == 0){
                //*Only sync with the specific player since other players don't need to know your stamina
                InitializeComponents.PLAYER.syncWith((ServerPlayerEntity) this.player, (ComponentProvider) this.player);
            }

        }
    }

    private boolean checkBackroomsTeleport() {
        if (this.player.isInsideWall()) {
            if (this.player.getWorld().getRegistryKey() == World.OVERWORLD && !this.isDoingCutscene()) {
                suffocationTimer++;
                if (suffocationTimer == 1) {
                    SPBRevamped.sendPersonalPlaySoundPacket((ServerPlayerEntity) this.player, ModSounds.GLITCH, 1.0f, 1.0f);
                    this.playingGlitchSound = true;
                }

                if (suffocationTimer == 40) {
                    RegistryKey<World> registryKey = BackroomsLevels.LEVEL0_WORLD_KEY;
                    ServerWorld backrooms = this.player.getWorld().getServer().getWorld(registryKey);
                    if (backrooms == null) {
                        return true;
                    }

                    this.savePlayerInventory();
                    this.sync();
                    this.player.getInventory().clear();
                    TeleportTarget target = new TeleportTarget(new Vec3d(1.5, 22, 1.5), Vec3d.ZERO, this.player.getYaw(), this.player.getPitch());
                    FabricDimensions.teleport(this.player, backrooms, target);
                    this.setDoingCutscene(true);
                    this.sync();
                    suffocationTimer = 0;
                }
            }
        } else {
            if (this.playingGlitchSound) {
                StopSoundS2CPacket stopSoundS2CPacket = new StopSoundS2CPacket(new Identifier(SPBRevamped.MOD_ID, "glitch"), null);
                ((ServerPlayerEntity) this.player).networkHandler.sendPacket(stopSoundS2CPacket);
            }
            this.playingGlitchSound = false;
            suffocationTimer = 0;
        }
        return false;
    }

    private void updateEntityVisibility() {
        if(this.isTalkingTooLoud() && this.talkingTooLoudTimer >= 0){
            this.setVisibleToEntity(true);
            this.talkingTooLoudTimer--;
        } else if(this.talkingTooLoudTimer <= 0){
            this.setVisibleToEntity(false);
            this.setTalkingTooLoud(false);
            this.resetTalkingTooLoudTimer();
        }

        if(!this.isVisibleToEntity()) {
            float speed = this.player.horizontalSpeed - this.player.prevHorizontalSpeed;

            if (!this.player.isSneaking() && speed != 0) {
                this.visibilityTimer--;
            }
            if (this.player.isSprinting()) {
                this.visibilityTimer--;
            }

            //reset timer if the player is not moving
            if(speed == 0){
                this.visibilityTimer = 15;
            }

            if (this.visibilityTimer <= 0) {
                this.visibilityTimerCooldown = 20;
                this.setVisibleToEntity(true);
            }
        } else {
            if(this.visibilityTimerCooldown > 0){
                this.visibilityTimerCooldown--;
            } else {
                this.visibilityTimer = 15;
                if(!this.isTalkingTooLoud()) {
                    this.setVisibleToEntity(false);
                }
            }
        }
    }

    public void justChanged() {
        this.sync();
    }

    private void shouldSync() {
        boolean sync = this.prevFlashLightOn != this.flashLightOn;

        if (sync) {
            this.sync();
        }
    }

    private void getPrevSettings() {
        this.prevFlashLightOn = this.flashLightOn;
    }
}

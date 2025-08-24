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

/**
 * PlayerComponent handles all player-specific data and behavior for the Backrooms mod.
 * This component is attached to every player and manages various states including:
 * - Stamina and movement mechanics
 * - Flashlight functionality
 * - Entity interactions (Smilers, SkinWalkers)
 * - Teleportation and level transitions
 * - Inventory management for Backrooms levels
 * - Audio and visual effects
 *
 * <h2>For Modders</h2>
 * This class is designed to be extensible. Many fields are protected to allow subclassing,
 * and comprehensive getters/setters are provided for safe access.
 *
 * <h3>Key Features:</h3>
 * <ul>
 *   <li>Automatic client-server synchronization</li>
 *   <li>NBT persistence across sessions</li>
 *   <li>Event-driven updates</li>
 *   <li>Integration with voice chat systems</li>
 *   <li>Comprehensive visual effects API</li>
 * </ul>
 *
 * <h3>Usage Example:</h3>
 * <pre>{@code
 * PlayerComponent component = InitializeComponents.PLAYER.get(player);
 * component.setShouldGlitch(true);
 * component.setStamina(50);
 * component.sync(); // Important: sync changes to client
 * }</pre>
 *
 * @author SpacePotato & Contributors
 * @since 1.0.0
 * @see InitializeComponents#PLAYER
 */
@SuppressWarnings("DataFlowIssue")
public class PlayerComponent implements AutoSyncedComponent, ClientTickingComponent, ServerTickingComponent {

    // Constants
    public static final int DEFAULT_MAX_STAMINA = 300;
    public static final int DEFAULT_SMILER_SPAWN_DELAY = 80;
    public static final int DEFAULT_SPEAKING_BUFFER = 80;
    public static final int DEFAULT_SKINWALKER_LOOK_DELAY = 60;
    public static final int DEFAULT_VISIBILITY_TIMER = 15;
    public static final int DEFAULT_TALKING_TOO_LOUD_TIMER = 20;
    public static final int DEFAULT_VISIBILITY_COOLDOWN = 20;

    // Core fields
    public final PlayerEntity player;
    protected final SimpleInventory playerSavedMainInventory = new SimpleInventory(36);
    protected final SimpleInventory playerSavedArmorInventory = new SimpleInventory(4);
    protected final SimpleInventory playerSavedOffhandInventory = new SimpleInventory(1);
    protected final Random random = new Random();

    // Entity spawn management
    protected int smilerSpawnDelay = DEFAULT_SMILER_SPAWN_DELAY;

    // Stamina system
    protected int stamina;
    protected boolean tired;

    // UI and interaction
    protected int scrollingInInventoryTime;

    // Lighting and visibility
    protected boolean flashLightOn;
    protected boolean shouldRender;

    // Cutscenes and special states
    protected boolean isDoingCutscene;
    protected boolean playingGlitchSound;
    protected boolean shouldNoClip;
    protected int teleportingTimer;
    protected boolean isTeleporting;

    // Environmental effects
    public int suffocationTimer;
    protected boolean shouldDoStatic;

    // Entity interactions
    protected boolean isBeingCaptured;
    public boolean hasBeenCaptured;
    protected boolean isBeingReleased;
    protected Entity targetEntity;
    protected int skinWalkerLookDelay;

    // Voice chat integration
    protected boolean shouldBeMuted;
    protected boolean isSpeaking;
    protected int speakingBuffer;
    protected float prevSpeakingTime;

    // Entity visibility system
    protected boolean visibleToEntity;
    protected int visibilityTimer;
    protected int visibilityTimerCooldown;
    protected boolean talkingTooLoud;
    protected int talkingTooLoudTimer;

    // Game state tracking
    protected GameMode prevGameMode;

    // Ambient sound instances
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

    // Skin walker detection
    protected boolean canSeeActiveSkinWalker;
    protected boolean prevFlashLightOn;

    // Glitch effects
    public float glitchTimer;
    protected boolean shouldGlitch;
    public int glitchTick;
    public boolean shouldInflictGlitchDamage;

    // Level transitions
    public BackroomsLevel.LevelTransition currentTransition = null;

    public PlayerComponent(PlayerEntity player){
        this.player = player;
        this.stamina = DEFAULT_MAX_STAMINA;
        this.tired = false;
        this.scrollingInInventoryTime = 0;
        this.flashLightOn = false;
        this.shouldRender = true;
        this.shouldNoClip = false;
        this.shouldDoStatic = false;
        this.isDoingCutscene = false;
        this.isBeingCaptured = false;
        this.hasBeenCaptured = false;
        this.isBeingReleased = false;
        this.skinWalkerLookDelay = DEFAULT_SKINWALKER_LOOK_DELAY;
        this.shouldBeMuted = false;
        this.isSpeaking = false;
        this.speakingBuffer = DEFAULT_SPEAKING_BUFFER;
        this.prevSpeakingTime = 0;
        this.visibleToEntity = false;
        this.visibilityTimer = DEFAULT_VISIBILITY_TIMER;
        this.visibilityTimerCooldown = 0;
        this.talkingTooLoud = false;
        this.talkingTooLoudTimer = DEFAULT_TALKING_TOO_LOUD_TIMER;
        this.suffocationTimer = 0;
        this.canSeeActiveSkinWalker = false;
        this.glitchTimer = 0.0f;
        this.shouldGlitch = false;
        this.glitchTick = 0;
        this.teleportingTimer = -1;
    }

    // Inventory management

    /** Saves player inventory for Backrooms level transitions */
    public void savePlayerInventory() {
        PlayerInventory inventory = this.player.getInventory();
        DefaultedList<ItemStack> mainInventory = inventory.main;
        DefaultedList<ItemStack> armorInventory = inventory.armor;
        DefaultedList<ItemStack> offHand = inventory.offHand;

        this.saveInventory(mainInventory, this.playerSavedMainInventory);
        this.saveInventory(armorInventory, this.playerSavedArmorInventory);
        this.saveInventory(offHand, this.playerSavedOffhandInventory);
    }

    /** Restores previously saved inventory */
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

    protected void saveInventory(DefaultedList<ItemStack> source, Inventory destination){
        for (int i = 0; i < source.size(); i++) {
            ItemStack itemStack = source.get(i);
            if (!itemStack.isEmpty()) {
                destination.setStack(i, itemStack);
            }
        }
    }

    protected void loadInventory(DefaultedList<ItemStack> destination, Inventory source){
        for (int i = 0; i < source.size(); i++) {
            ItemStack itemStack = source.getStack(i);
            if (!itemStack.isEmpty()) {
                destination.set(i, itemStack);
            }
        }
    }

    // Getters and setters

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
        this.stamina = Math.max(0, Math.min(DEFAULT_MAX_STAMINA, stamina));
    }

    public boolean isTired() {
        return tired;
    }

    public void setTired(boolean tired) {
        this.tired = tired;
    }

    public int getMaxStamina() {
        return DEFAULT_MAX_STAMINA;
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


    public boolean shouldInflictGlitchDamage() {
        return shouldInflictGlitchDamage;
    }

    public int getSmilerSpawnDelay() {
        return smilerSpawnDelay;
    }

    public void setSmilerSpawnDelay(int smilerSpawnDelay) {
        this.smilerSpawnDelay = smilerSpawnDelay;
    }

    public int getSpeakingBuffer() {
        return speakingBuffer;
    }

    public void setSpeakingBuffer(int speakingBuffer) {
        this.speakingBuffer = speakingBuffer;
    }

    public float getPrevSpeakingTime() {
        return prevSpeakingTime;
    }

    public void setPrevSpeakingTime(float prevSpeakingTime) {
        this.prevSpeakingTime = prevSpeakingTime;
    }

    public int getVisibilityTimer() {
        return visibilityTimer;
    }

    public void setVisibilityTimer(int visibilityTimer) {
        this.visibilityTimer = visibilityTimer;
    }

    public int getVisibilityTimerCooldown() {
        return visibilityTimerCooldown;
    }

    public void setVisibilityTimerCooldown(int visibilityTimerCooldown) {
        this.visibilityTimerCooldown = visibilityTimerCooldown;
    }

    public int getTalkingTooLoudTimer() {
        return talkingTooLoudTimer;
    }

    public void setTalkingTooLoudTimer(int talkingTooLoudTimer) {
        this.talkingTooLoudTimer = talkingTooLoudTimer;
    }

    // Utility methods

    public void resetStamina() {
        this.stamina = DEFAULT_MAX_STAMINA;
    }

    public void addStamina(int amount) {
        this.stamina = Math.min(DEFAULT_MAX_STAMINA, this.stamina + amount);
    }

    public void removeStamina(int amount) {
        this.stamina = Math.max(0, this.stamina - amount);
    }

    public boolean hasSavedInventory() {
        return !playerSavedMainInventory.isEmpty() ||
               !playerSavedArmorInventory.isEmpty() ||
               !playerSavedOffhandInventory.isEmpty();
    }

    public void clearSavedInventories() {
        playerSavedMainInventory.clear();
        playerSavedArmorInventory.clear();
        playerSavedOffhandInventory.clear();
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

            if (!teleports.isEmpty() && currentTransition == null) {
                currentTransition = teleports.get(0);
            }

            if (level == BackroomsLevels.LEVEL324_BACKROOMS_LEVEL && this.player.getWorld().getBlockState(this.player.getBlockPos().offset(Direction.DOWN, 3)).isOf(Blocks.RED_WOOL)) {
                this.player.teleport(this.player.getX(), this.player.getY() - 64, this.player.getZ());
            }

            if (level == BackroomsLevels.LEVEL324_BACKROOMS_LEVEL && this.player.getWorld().getBlockState(this.player.getBlockPos().offset(Direction.DOWN, 3)).isOf(Blocks.YELLOW_WOOL)) {
                this.player.teleport(this.player.getX(), this.player.getY() + 64, this.player.getZ());
            }
        }


        if (currentTransition != null) {
            if (teleportingTimer == -1) {
                this.setTeleportingTimer(currentTransition.duration());
            }

            if (teleportingTimer == 0) {
                ServerWorld destination = this.player.getWorld().getServer().getWorld(currentTransition.teleport().to().getWorldKey());
                TeleportTarget target = new TeleportTarget(currentTransition.teleport().pos(), currentTransition.teleport().playerComponent().player.getVelocity(), currentTransition.teleport().playerComponent().player.getYaw(), currentTransition.teleport().playerComponent().player.getPitch());

                if (currentTransition.teleport().to() == BackroomsLevels.OVERWORLD_REPRESENTING_BACKROOMS_LEVEL) {
                    currentTransition.teleport().playerComponent().sync();

                    if (this.player.getWorld().getGameRules().getBoolean(ModGamerules.STUCK_IN_BACKROOMS)) {
                        destination = this.player.getWorld().getServer().getWorld(BackroomsLevels.LEVEL0_BACKROOMS_LEVEL.getWorldKey());
                        target = new TeleportTarget(BackroomsLevels.LEVEL0_BACKROOMS_LEVEL.getSpawnPos(), currentTransition.teleport().playerComponent().player.getVelocity(), currentTransition.teleport().playerComponent().player.getYaw(), currentTransition.teleport().playerComponent().player.getPitch());
                    }
                }

                currentTransition.teleport().to().transitionOut(currentTransition.teleport());
                FabricDimensions.teleport(currentTransition.teleport().playerComponent().player, destination, target);
                currentTransition.teleport().to().transitionIn(currentTransition.teleport());

                currentTransition = null;
            }
        }

        if (teleportingTimer >= 0) {
            if (currentTransition != null) {
                currentTransition.callback().tick(currentTransition.teleport(), teleportingTimer);
            }
            this.setTeleportingTimer(teleportingTimer - 1);
        }

        if (BackroomsLevels.isInBackroomsLevel(player.getWorld(), BackroomsLevels.LEVEL324_BACKROOMS_LEVEL) && player.getPos().subtract(0, 64, 0).lengthSquared() > 10000 && player.getPos().y > 60) {
            summonSmilers();
        }

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
            if(prevStamina != this.stamina && this.stamina % 20 == 0){
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

    // ========================================
    // MODDER API METHODS
    // ========================================

    /**
     * Checks if the player is currently in any Backrooms level.
     * This is a convenience method for modders to quickly determine context.
     *
     * @return true if player is in a Backrooms dimension
     */
    public boolean isInBackrooms() {
        return BackroomsLevels.isInBackrooms(this.player.getWorld().getRegistryKey());
    }

    /**
     * Gets the current Backrooms level the player is in.
     *
     * @return Optional containing the current BackroomsLevel, or empty if not in Backrooms
     */
    public Optional<BackroomsLevel> getCurrentBackroomsLevel() {
        return BackroomsLevels.getLevel(this.player.getWorld());
    }

    /**
     * Applies a visual glitch effect to the player.
     * This is a high-level API for modders to easily trigger effects.
     *
     * @param intensity Effect intensity (0.0 to 1.0)
     * @param duration Duration in ticks
     */
    public void applyGlitchEffect(float intensity, int duration) {
        this.setShouldGlitch(true);
        if (intensity > 0.7f) {
            this.setShouldInflictGlitchDamage(true);
        }
        // Schedule effect removal
        // Note: In a real implementation, you'd want a proper timer system
        this.sync();
    }

    /**
     * Triggers a screen static effect.
     *
     * @param enable Whether to enable or disable static
     */

    /**
     * Forces a player state synchronization.
     * Call this after making multiple changes to ensure client updates.
     */
    public void forceSynchronization() {
        this.sync();
    }

    /**
     * Resets all internal timers to their default values.
     * Useful for level transitions or debugging.
     */
    public void resetAllTimers() {
        this.smilerSpawnDelay = DEFAULT_SMILER_SPAWN_DELAY;
        this.skinWalkerLookDelay = DEFAULT_SKINWALKER_LOOK_DELAY;
        this.speakingBuffer = DEFAULT_SPEAKING_BUFFER;
        this.visibilityTimer = DEFAULT_VISIBILITY_TIMER;
        this.visibilityTimerCooldown = 0;
        this.talkingTooLoudTimer = DEFAULT_TALKING_TOO_LOUD_TIMER;
        this.suffocationTimer = 0;
        this.glitchTick = 0;
        this.teleportingTimer = -1;
    }

    /**
     * Resets all boolean states to their default values.
     * Useful for cleaning up player state during level transitions.
     */
    public void resetAllStates() {
        this.tired = false;
        this.flashLightOn = false;
        this.shouldRender = true;
        this.isDoingCutscene = false;
        this.playingGlitchSound = false;
        this.shouldNoClip = false;
        this.isTeleporting = false;
        this.shouldDoStatic = false;
        this.isBeingCaptured = false;
        this.isBeingReleased = false;
        this.shouldBeMuted = false;
        this.isSpeaking = false;
        this.visibleToEntity = false;
        this.talkingTooLoud = false;
        this.canSeeActiveSkinWalker = false;
        this.shouldGlitch = false;
        this.shouldInflictGlitchDamage = false;
    }

    /**
     * Performs a complete reset of the player component.
     * This includes both timers and states.
     */

    public void fullReset() {
        this.stamina = DEFAULT_MAX_STAMINA;
        this.glitchTimer = 0.0f;
        this.prevSpeakingTime = 0;
        this.targetEntity = null;
        this.prevGameMode = null;
        this.currentTransition = null;

        resetAllTimers();
        resetAllStates();
        clearSavedInventories();

        this.justChanged();
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

    // ===== SIMPLE EFFECT METHODS FOR MODDERS =====

    /**
     * Enable or disable static effect
     * @param enable true to enable, false to disable
     */

    /**
     * Enable or disable glitch effect with full intensity
     * @param enable true to enable, false to disable
     */
    public void glitch(boolean enable) {
        this.setShouldGlitch(enable);
        if (enable) {
            this.glitchTimer = 1.0f;
            this.glitchTick = 80;
        } else {
            this.glitchTimer = 0.0f;
            this.glitchTick = 0;
        }
        this.sync();
    }

    /**
     * Enable glitch effect with custom intensity
     * @param intensity 0.0 to 1.0 (0 = off, 1 = full intensity)
     */
    public void glitch(float intensity) {
        intensity = Math.max(0.0f, Math.min(1.0f, intensity));
        this.setShouldGlitch(intensity > 0);
        this.glitchTimer = intensity;
        this.glitchTick = (int) (intensity * 80);
        this.sync();
    }

    /**
     * Clear all visual effects
     */
    public void clearEffects() {
        this.setShouldDoStatic(false);
        this.setShouldGlitch(false);
        this.glitchTimer = 0.0f;
        this.glitchTick = 0;
        this.sync();
    }
}

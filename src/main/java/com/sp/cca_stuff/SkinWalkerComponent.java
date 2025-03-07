package com.sp.cca_stuff;

import com.sp.entity.custom.SkinWalkerEntity;
import com.sp.entity.ik.components.IKAnimatable;
import com.sp.entity.ik.components.IKLegComponent;
import com.sp.entity.ik.parts.Segment;
import com.sp.entity.ik.parts.ik_chains.IKChain;
import com.sp.entity.ik.parts.ik_chains.TargetReachingIKChain;
import com.sp.entity.ik.parts.sever_limbs.ServerLimb;
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;

import java.util.List;
import java.util.UUID;

public class SkinWalkerComponent implements AutoSyncedComponent {
    private final SkinWalkerEntity entity;
    private PlayerEntity followTarget;
    private boolean isChasing;
    private UUID targetPlayerUUID;
    private boolean shouldLookAtTarget;
    private boolean active;
    private boolean trueForm;
    private boolean idle;
    private boolean shouldActNatural;
    private boolean currentlyActingNatural;
    private boolean isSneaking;
    private boolean beginReveal;
    private boolean beginRelease;
    private boolean isNoticing;
    private int suspicion;
    private BlockPos lastKnownTargetLocation;

    private final IKLegComponent<? extends IKChain, ? extends IKAnimatable<?>> IKComponent = new IKLegComponent<>(
            new IKLegComponent.LegSetting.Builder()
                    .maxDistance(1.5)
                    .stepInFront(1)
                    .movementSpeed(0.7)
                    .maxStandingStillDistance(0.1)
                    .standStillCounter(20).build(),
            List.of(new ServerLimb(1.5, 0, 2),
                    new ServerLimb(-1.5, 0, 2),
                    new ServerLimb(1.5, 0, -2),
                    new ServerLimb(-1.5, 0, -2)),
            new TargetReachingIKChain(new Segment.Builder().length(0.65).build(), new Segment.Builder().length(1).build(), new Segment.Builder().length(1.3).build(), new Segment.Builder().length(0.85).build()),
            new TargetReachingIKChain(new Segment.Builder().length(0.65).build(), new Segment.Builder().length(1).build(), new Segment.Builder().length(1.3).build(), new Segment.Builder().length(0.85).build()),
            new TargetReachingIKChain(new Segment.Builder().length(0.65).build(), new Segment.Builder().length(1).build(), new Segment.Builder().length(1.3).build(), new Segment.Builder().length(0.85).build()),
            new TargetReachingIKChain(new Segment.Builder().length(0.65).build(), new Segment.Builder().length(1).build(), new Segment.Builder().length(1.3).build(), new Segment.Builder().length(0.85).build())
    );

    public SkinWalkerComponent(SkinWalkerEntity entity){
        this.entity = entity;
        this.isSneaking = false;
        this.active = false;
        this.trueForm = false;
        this.shouldActNatural = false;
        this.currentlyActingNatural = false;
        this.shouldLookAtTarget = true;
        this.beginReveal = false;
        this.beginRelease = false;
        this.isNoticing = false;
        this.followTarget = null;
        this.isChasing = false;
        this.suspicion = 0;
    }

    public boolean isChasing() {return isChasing;}
    public void setChasing(boolean chasing) {
        isChasing = chasing;
        this.sync();
    }

    public BlockPos getLastKnownTargetLocation() {return lastKnownTargetLocation;}
    public void setLastKnownTargetLocation(BlockPos lastKnownTargetLocation) {this.lastKnownTargetLocation = lastKnownTargetLocation;}

    public IKLegComponent getIKComponent() {
        return IKComponent;
    }

    public int getSuspicion() {return suspicion;}
    public void addSuspicion(int suspicion) {this.suspicion += suspicion;}
    public void addSuspicion() {this.suspicion += 1;}

    public boolean shouldLookAtTarget() {return shouldLookAtTarget;}
    public void setShouldLookAtTarget(boolean shouldLookAtTarget) {this.shouldLookAtTarget = shouldLookAtTarget;}

    public PlayerEntity getFollowTarget() {return this.followTarget;}
    public void setFollowTarget(PlayerEntity followTarget) {this.followTarget = followTarget;}

    public boolean shouldActNatural() {return this.shouldActNatural;}
    public void setShouldActNatural(boolean shouldActNatural) {this.shouldActNatural = shouldActNatural;}

    public boolean isCurrentlyActingNatural() {return currentlyActingNatural;}
    public void setCurrentlyActingNatural(boolean currentlyActingNatural) {this.currentlyActingNatural = currentlyActingNatural;}

    public boolean isInTrueForm() {return this.trueForm;}
    public void setTrueForm(boolean trueForm) {this.trueForm = trueForm; this.sync();}

    public boolean isActive() {return this.active;}
    public void setActive(boolean active) {this.active = active;}

    public boolean isIdle() {
        return idle;
    }
    public void setIdle(boolean idle) {
        this.idle = idle;
    }

    public boolean isSneaking() {return this.isSneaking;}
    public void setSneaking(boolean sneaking) {this.isSneaking = sneaking; sync();}

    public boolean isNoticing() {return isNoticing;}
    public void setNoticing(boolean noticing) {isNoticing = noticing;}

    public boolean shouldBeginReveal() {return beginReveal;}
    public void setBeginReveal(boolean beginReveal) {this.beginReveal = beginReveal; this.sync();}

    public boolean shouldBeginRelease() {return beginRelease;}
    public void setShouldBeginRelease(boolean beginRelease) {this.beginRelease = beginRelease; this.sync();}

    public UUID getTargetPlayerUUID() {return this.targetPlayerUUID;}
    public void setTargetPlayerUUID(UUID targetPlayerUUID) {this.targetPlayerUUID = targetPlayerUUID; sync();}

    public void sync(){InitializeComponents.SKIN_WALKER.sync(this.entity);}


    @Override
    public void readFromNbt(NbtCompound tag) {
        this.isSneaking = tag.getBoolean("isSneaking");
        this.targetPlayerUUID = tag.getUuid("targetPlayerUUID");
        this.isChasing = tag.getBoolean("isChasing");
        this.trueForm = tag.getBoolean("trueForm");
        this.beginReveal = tag.getBoolean("beginReveal");
    }

    @Override
    public void writeToNbt(NbtCompound tag) {
        tag.putBoolean("isSneaking", this.isSneaking);
        tag.putUuid("targetPlayerUUID", this.targetPlayerUUID);
        tag.putBoolean("isChasing", this.isChasing);
        tag.putBoolean("trueForm", this.trueForm);
        tag.putBoolean("beginReveal", this.beginReveal);
    }


}

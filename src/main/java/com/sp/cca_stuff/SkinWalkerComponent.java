package com.sp.cca_stuff;

import com.sp.entity.custom.SkinWalkerEntity;
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import dev.onyxstudios.cca.api.v3.component.tick.ServerTickingComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;

import java.util.UUID;

public class SkinWalkerComponent implements AutoSyncedComponent, ServerTickingComponent {
    private final SkinWalkerEntity entity;
    private PlayerEntity nearestTarget;
    private UUID targetPlayerUUID;
    private boolean shouldLookAtTarget;
    private boolean active;
    private boolean trueForm;
    private boolean shouldActNatural;
    private boolean currentlyActingNatural;
    private boolean isSneaking;
    private boolean beginReveal;
    private int suspicion;

    public SkinWalkerComponent(SkinWalkerEntity entity){
        this.entity = entity;
        this.isSneaking = false;
        this.active = false;
        this.trueForm = false;
        this.shouldActNatural = false;
        this.currentlyActingNatural = false;
        this.shouldLookAtTarget = true;
        this.beginReveal = false;
        this.nearestTarget = null;
        this.suspicion = 0;
    }

    public int getSuspicion() {return suspicion;}
    public void addSuspicion(int suspicion) {this.suspicion += suspicion;}
    public void addSuspicion() {this.suspicion += 1;}

    public boolean shouldLookAtTarget() {return shouldLookAtTarget;}
    public void setShouldLookAtTarget(boolean shouldLookAtTarget) {this.shouldLookAtTarget = shouldLookAtTarget;}

    public PlayerEntity getNearestTarget() {return this.nearestTarget;}
    public void setNearestTarget(PlayerEntity nearestTarget) {this.nearestTarget = nearestTarget;}

    public boolean shouldActNatural() {return this.shouldActNatural;}
    public void setShouldActNatural(boolean shouldActNatural) {this.shouldActNatural = shouldActNatural;}

    public boolean isCurrentlyActingNatural() {return currentlyActingNatural;}
    public void setCurrentlyActingNatural(boolean currentlyActingNatural) {this.currentlyActingNatural = currentlyActingNatural;}

    public boolean isInTrueForm() {return this.trueForm;}
    public void setTrueForm(boolean trueForm) {this.trueForm = trueForm;}

    public boolean isActive() {return this.active;}
    public void setActive(boolean active) {this.active = active;}

    public boolean isSneaking() {return this.isSneaking;}
    public void setSneaking(boolean sneaking) {this.isSneaking = sneaking; sync();}

    public boolean shouldBeginReveal() {return beginReveal;}
    public void setBeginReveal(boolean typingInChat) {this.beginReveal = typingInChat;}

    public UUID getTargetPlayerUUID() {return this.targetPlayerUUID;}
    public void setTargetPlayerUUID(UUID targetPlayerUUID) {this.targetPlayerUUID = targetPlayerUUID; sync();}

     public void sync(){InitializeComponents.SKIN_WALKER.sync(this.entity);}


    @Override
    public void readFromNbt(NbtCompound tag) {
        this.isSneaking = tag.getBoolean("isSneaking");
        this.targetPlayerUUID = tag.getUuid("targetPlayerUUID");
    }

    @Override
    public void writeToNbt(NbtCompound tag) {
        tag.putBoolean("isSneaking", this.isSneaking);
        tag.putUuid("targetPlayerUUID", this.targetPlayerUUID);
    }

    @Override
    public void serverTick() {
        if(this.getNearestTarget() == null){

        }
    }
}

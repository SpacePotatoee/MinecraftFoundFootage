package com.sp.entity.custom;

import com.sp.cca_stuff.InitializeComponents;
import com.sp.cca_stuff.PlayerComponent;
import com.sp.cca_stuff.SmilerComponent;
import com.sp.init.BackroomsLevels;
import com.sp.world.levels.custom.Level0BackroomsLevel;
import com.sp.world.levels.custom.Level1BackroomsLevel;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

import java.util.List;

public class SmilerEntity extends MobEntity {
    private final SmilerComponent component;
    private int finalTicks;
    private float liveTime;

    public SmilerEntity(EntityType<? extends MobEntity> entityType, World world) {
        super(entityType, world);
        this.component = InitializeComponents.SMILER.get(this);

        if(!world.isClient){
            Random random = Random.create();
            this.component.setRandomTexture(random.nextBetween(1,3));
            this.component.sync();
        }
        this.finalTicks = 20;
        this.liveTime = 100;
    }

    @Override
    protected void initGoals() {
        this.targetSelector.add(1, new ActiveTargetGoal<>(this, PlayerEntity.class, 0, true, false, null));
    }

    @Override
    public void tick() {
        if(!this.getWorld().isClient) {
            if(!this.component.shouldDisappear()) {
                if (this.getWorld().getClosestPlayer(this, 15) != null) {
                    List<? extends PlayerEntity> playerList = this.getWorld().getPlayers(TargetPredicate.createNonAttackable().setBaseMaxDistance(15), this, this.getBoundingBox().expand(15, 1, 15));

                    for (PlayerEntity player : playerList) {
                        if (this.shouldDisappear(player)) {
                            this.component.setShouldDisappear(true);
                            this.component.sync();
                            break;
                        }
                    }
                } else {
                    this.component.setShouldDisappear(true);
                    this.component.sync();
                }

                if (this.liveTime > 0) {
                    this.liveTime--;
                } else {
                    this.component.setShouldDisappear(true);
                    this.component.sync();
                }
            }



            if(this.component.shouldDisappear()) {
                this.finalTicks--;
                if(this.finalTicks <= 0){
                    this.discard();
                }
            }

            if (!((BackroomsLevels.getLevel(this.getWorld()).orElse(BackroomsLevels.OVERWORLD_REPRESENTING_BACKROOMS_LEVEL)) instanceof Level1BackroomsLevel level)) {
                return;
            }

            if(level.getLightState() != Level0BackroomsLevel.LightState.BLACKOUT) {
                this.discard();
            }
        }


        super.tick();
    }

    private boolean shouldDisappear(PlayerEntity player){
        PlayerComponent playerComponent = InitializeComponents.PLAYER.get(player);
        return playerComponent.isFlashLightOn() &&
                this.isPlayerStaring(player);
    }

    //From Enderman. Don't need anything too fancy
    private boolean isPlayerStaring(PlayerEntity player) {
        Vec3d vec3d = player.getRotationVec(1.0F).normalize();
        Vec3d vec3d2 = new Vec3d(this.getX() - player.getX(), this.getEyeY() - player.getEyeY(), this.getZ() - player.getZ());
        double d = vec3d2.length();
        vec3d2 = vec3d2.normalize();
        double e = vec3d.dotProduct(vec3d2);
        return e > 1.0 - 0.35 / d && player.canSee(this);
    }

    public static DefaultAttributeContainer.Builder createSmilerAttributes(){
        return MobEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 1000)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0)
                .add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 1000);
    }


}

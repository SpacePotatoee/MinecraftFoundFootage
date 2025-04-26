package com.sp.entity.ai.goals;

import com.sp.cca_stuff.InitializeComponents;
import com.sp.cca_stuff.SkinWalkerComponent;
import com.sp.entity.custom.SkinWalkerEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;

public class ActNaturalGoal extends Goal {
    private final Random random = Random.create(7585889L);
    private final java.util.Random rand = new java.util.Random();
    private final SkinWalkerEntity entity;
    private final SkinWalkerComponent component;
    private Integer randomAction;
    private int actCooldown;

    private int currentActionCount = 0;
    private int currentActionCooldown = 0;
    private boolean currentActionSwitch = false;
    private boolean isIdling = false;
    private int idleTicks = 0;

    private Vec3d randLookDir;
    private Vec3d lastLookDir;
    private float lookBlendFactor = 0.0f;
    
    // Additional behavioral variables
    private int scratchHeadCooldown = 0;
    private int lookAtGroundCooldown = 0;
    private boolean isLookingAtGround = false;
    private int fidgetCounter = 0;

    public ActNaturalGoal(SkinWalkerEntity entity) {
        this.entity = entity;
        this.component = InitializeComponents.SKIN_WALKER.get(entity);
    }

    @Override
    public boolean canStart() {
        return this.component.shouldActNatural() && !this.component.isInTrueForm() && !this.component.shouldBeginReveal();
    }

    @Override
    public void start() {
        this.actCooldown = getTickCount(random.nextBetween(20, 40));
    }

    @Override
    public void stop() {
        this.randomAction = null;
        this.component.setSneaking(false);
        this.currentActionCooldown = 0;
        this.currentActionCount = 0;
        this.actCooldown = 0;
        this.isIdling = false;
        this.isLookingAtGround = false;
    }

    @Override
    public void tick() {
        if(!this.entity.getWorld().isClient) {
            // Handle occasional idle state
            if (isIdling) {
                idleTick();
                return;
            }
            
            ////Cooldown Checks////
            if (actCooldown > 0) {
                actCooldown--;
                
                // Less frequent head movements but more natural when they happen
                if (actCooldown % 30 == 0 && random.nextFloat() < 0.25f) {
                    performSmallHeadMovement();
                }
                
                return;
            }
            ///////////////////////
            
            // Small chance to enter idle state between actions
            if (this.randomAction == null && random.nextFloat() < 0.15f) {
                this.isIdling = true;
                this.idleTicks = random.nextBetween(40, 100);
                return;
            }
            
            if (this.randomAction == null) {
                this.randomAction = random.nextBetween(1, 7);
            }
            
            this.component.setCurrentlyActingNatural(true);
            switch (this.randomAction) {
                case 1: this.sneakTick(); break;
                case 2: this.strafeTick(); break;
                case 3: this.lookAndPunchTick(); break;
                case 4: this.lookMultTick(); break;
                case 5: this.scratchHeadTick(); break;
                case 6: this.fidgetTick(); break;
                case 7: this.randomPlayerPunchTick(); break;
            }
        }
    }

    private void idleTick() {
        // Just stand still, occasionally look around subtly
        if (idleTicks <= 0) {
            isIdling = false;
            setRandomActCoolDown();
            return;
        }
        
        idleTicks--;
        
        // Occasionally look slightly in a random direction
        if (idleTicks % 15 == 0 && random.nextFloat() < 0.4f) {
            float randX = rand.nextFloat(-15, 15);
            float randY = rand.nextFloat(-30, 30);
            ((SkinWalkerEntity.SkinWalkerLookControl)this.entity.getLookControl()).lookAt(
                eulerToVector(randX, randY), 
                random.nextBetween(8, 15)
            );
        }
        
        // Rarely look at the ground
        if (!isLookingAtGround && lookAtGroundCooldown <= 0 && random.nextFloat() < 0.01f) {
            isLookingAtGround = true;
            lookAtGroundCooldown = random.nextBetween(30, 50);
            float downPitch = rand.nextFloat(30, 45);
            float randYaw = rand.nextFloat(-20, 20);
            ((SkinWalkerEntity.SkinWalkerLookControl)this.entity.getLookControl()).lookAt(
                eulerToVector(downPitch, randYaw), 
                random.nextBetween(6, 10)
            );
        } else if (isLookingAtGround) {
            lookAtGroundCooldown--;
            if (lookAtGroundCooldown <= 0) {
                isLookingAtGround = false;
            }
        }
    }

    private void sneakTick() {
        if (this.currentActionCooldown <= 0) {
            if (this.currentActionCount < 2 && !this.component.isSneaking()) {
                this.component.setSneaking(true);
                this.currentActionCount++;
                this.currentActionCooldown = random.nextBetween(10, 25);
            } else if (this.component.isSneaking()) {
                this.component.setSneaking(false);
                this.currentActionCooldown = random.nextBetween(5, 15);
            } else {
                this.component.setCurrentlyActingNatural(false);
                this.randomAction = null;
                this.currentActionCount = 0;
                this.setRandomActCoolDown();
            }
        } else {
            this.currentActionCooldown--;
            
            // While sneaking, occasionally look around
            if (this.component.isSneaking() && this.currentActionCooldown % 10 == 0 && random.nextFloat() < 0.4f) {
                float randX = rand.nextFloat(-20, 20);
                float randY = rand.nextFloat(-45, 45);
                ((SkinWalkerEntity.SkinWalkerLookControl)this.entity.getLookControl()).lookAt(
                    eulerToVector(randX, randY), 
                    random.nextBetween(4, 7)
                );
            }
        }
    }

    private void strafeTick() {
        if (this.currentActionCooldown <= 0) {
            if (this.currentActionCount < 6) {
                // Varied movement speeds make it look more realistic
                float strafeSpeed = random.nextFloat() < 0.3f ? 0.3f : 0.2f;
                
                if (!this.currentActionSwitch) {
                    this.entity.sidewaysSpeed = strafeSpeed;
                    this.currentActionSwitch = true;
                } else {
                    this.entity.sidewaysSpeed = -strafeSpeed;
                    this.currentActionSwitch = false;
                }
                
                this.currentActionCount++;
                this.currentActionCooldown = random.nextBetween(2, 5);
                
                // Sometimes pause briefly between strafes
                if (random.nextFloat() < 0.2f) {
                    this.entity.sidewaysSpeed = 0;
                    this.currentActionCooldown += random.nextBetween(3, 8);
                }
            } else {
                this.component.setCurrentlyActingNatural(false);
                this.entity.sidewaysSpeed = 0;
                this.currentActionSwitch = false;
                this.randomAction = null;
                this.currentActionCount = 0;
                this.setRandomActCoolDown();
            }
        } else {
            this.currentActionCooldown--;
            
            // occasionally look in strafe direction
            if (this.currentActionCooldown % 8 == 0 && random.nextFloat() < 0.4f) {
                float lookDir = this.currentActionSwitch ? 60 : -60;
                ((SkinWalkerEntity.SkinWalkerLookControl)this.entity.getLookControl()).lookAt(
                    eulerToVector(0, lookDir), 
                    random.nextBetween(3, 6)
                );
            }
        }
    }

    private void lookAndPunchTick() {
        this.component.setShouldLookAtTarget(false);
        if(this.randLookDir == null){
            float randX = rand.nextFloat(-45, 45);
            float randY = rand.nextFloat(-180, 180);
            this.randLookDir = eulerToVector(randX, randY);
            ((SkinWalkerEntity.SkinWalkerLookControl)this.entity.getLookControl()).lookAt(this.randLookDir, 5);
        }

        if(this.currentActionCooldown <= 0){
            if (this.currentActionCount < 4) {
                if (random.nextFloat() < 0.3f) {
                    this.currentActionCooldown = random.nextBetween(4, 8);
                } else {
                    this.entity.swingHand(Hand.MAIN_HAND);
                    this.currentActionCount++;
                    this.currentActionCooldown = random.nextBetween(2, 5);
                }
            } else {
                this.component.setCurrentlyActingNatural(false);
                this.component.setShouldLookAtTarget(true);
                this.randomAction = null;
                this.randLookDir = null;
                this.currentActionCount = 0;
                this.setRandomActCoolDown();
            }
        } else {
            this.currentActionCooldown--;
        }
    }

    private void lookMultTick() {
        this.component.setShouldLookAtTarget(false);

        if(this.currentActionCooldown <= 0){
            if (this.currentActionCount < 5) {
                // we do smoother head movement by blending between look directions
                float randX = rand.nextFloat(-45, 45);
                float randY = rand.nextFloat(-180, 180);
                
                // create a new random direction
                Vec3d newLookDir = eulerToVector(randX, randY);
                
                // if we have a previous direction, blend between them
                if (lastLookDir != null) {
                    int lookSpeed = random.nextBetween(4, 9);
                    if (this.currentActionCount > 2 && random.nextFloat() < 0.4f) {
                        lookSpeed += 3;
                    }
                    
                    ((SkinWalkerEntity.SkinWalkerLookControl)this.entity.getLookControl()).lookAt(newLookDir, lookSpeed);
                } else {
                    ((SkinWalkerEntity.SkinWalkerLookControl)this.entity.getLookControl()).lookAt(newLookDir, 5);
                }
                
                lastLookDir = newLookDir;

                // make it occasionally punch while looking around
                if(random.nextFloat() < 0.15f){
                    this.entity.swingHand(Hand.MAIN_HAND);
                }

                this.currentActionCount++;
                this.currentActionCooldown = random.nextBetween(5, 15);
                
                if (random.nextFloat() < 0.2f) {
                    this.currentActionCooldown += random.nextBetween(5, 15);
                }
            } else {
                this.component.setCurrentlyActingNatural(false);
                this.component.setShouldLookAtTarget(true);
                this.randomAction = null;
                this.currentActionCount = 0;
                this.lastLookDir = null;
                this.setRandomActCoolDown();
            }
        } else {
            this.currentActionCooldown--;
        }
    }
    
    // "confused" head movement type shit
    private void scratchHeadTick() {
        if (this.currentActionCooldown <= 0) {
            if (this.currentActionCount == 0) {
                // first, look slightly upward
                float upPitch = rand.nextFloat(-30, -15);
                float randYaw = rand.nextFloat(-20, 20);
                ((SkinWalkerEntity.SkinWalkerLookControl)this.entity.getLookControl()).lookAt(
                    eulerToVector(upPitch, randYaw), 6
                );
                this.currentActionCount++;
                this.currentActionCooldown = random.nextBetween(5, 8);
            } else if (this.currentActionCount < 4) {
                // then do a few small hand movements. idk why i but like, i think it looks better
                this.entity.swingHand(random.nextBoolean() ? Hand.MAIN_HAND : Hand.OFF_HAND);
                this.currentActionCount++;
                this.currentActionCooldown = random.nextBetween(4, 8);
                
                if (random.nextFloat() < 0.5f) {
                    float randX = rand.nextFloat(-15, 15);
                    float randY = rand.nextFloat(-20, 20);
                    ((SkinWalkerEntity.SkinWalkerLookControl)this.entity.getLookControl()).lookAt(
                        eulerToVector(randX, randY), 5
                    );
                }
            } else { 
                this.component.setCurrentlyActingNatural(false);
                this.component.setShouldLookAtTarget(true);
                this.randomAction = null; 
                this.currentActionCount = 0;
                this.setRandomActCoolDown();
            }
        } else {
            this.currentActionCooldown--;
        }
    }
    
    // fidget and adjust posture stuff
    private void fidgetTick() {
        if (this.currentActionCooldown <= 0) {
            if (this.currentActionCount < 3) {
                // alternating between like short sneaks and stands because people dont always do the same thing exactly
                this.component.setSneaking(!this.component.isSneaking());
                
                if (random.nextFloat() < 0.4f) {
                    float randX = rand.nextFloat(-20, 20);
                    float randY = rand.nextFloat(-40, 40);
                    ((SkinWalkerEntity.SkinWalkerLookControl)this.entity.getLookControl()).lookAt(
                        eulerToVector(randX, randY), random.nextBetween(3, 6)
                    );
                }
                
                this.currentActionCount++;
                this.currentActionCooldown = random.nextBetween(3, 7);
            } else {
                // super duper important: we make sure we end standing up
                if (this.component.isSneaking()) {
                    this.component.setSneaking(false);
                }
                
                this.component.setCurrentlyActingNatural(false);
                this.randomAction = null;
                this.currentActionCount = 0;
                this.setRandomActCoolDown();
            }
        } else {
            this.currentActionCooldown--;
        }
    }
    
    // lil helper function for small subtle head movements
    private void performSmallHeadMovement() {
        float randX = rand.nextFloat(-5, 5);
        float randY = rand.nextFloat(-10, 10);
        
        int lookSpeed = random.nextBetween(15, 25);
        
        Vec3d targetLook = eulerToVector(randX, randY);
        
        ((SkinWalkerEntity.SkinWalkerLookControl)this.entity.getLookControl()).lookAt(
            targetLook, 
            lookSpeed
        );
    }

    private Vec3d eulerToVector(float pitch, float yaw){
        float x = MathHelper.cos(yaw)*MathHelper.cos(pitch);
        float y = MathHelper.sin(yaw)*MathHelper.cos(pitch);
        float z = MathHelper.sin(pitch);

        return new Vec3d(x, y, z).multiply(180/Math.PI);
    }

    private void setRandomActCoolDown(){
        this.actCooldown = getTickCount(random.nextBetween(40, 200));
    }

    // randomly punch at player for no fucking reason
    private void randomPlayerPunchTick() {
        if (this.currentActionCooldown <= 0) {
            if (this.component.getFollowTarget() != null) {
                // check if player is within attack range
                double distanceSq = this.entity.squaredDistanceTo(this.component.getFollowTarget());
                double attackRange = Math.pow(this.entity.getWidth() + 1.0, 2);
                
                if (distanceSq <= attackRange) {
                    // look at player before punching
                    this.entity.getLookControl().lookAt(
                        this.component.getFollowTarget().getX(),
                        this.component.getFollowTarget().getEyeY(),
                        this.component.getFollowTarget().getZ()
                    );
                    
                    // guarantee an attack happens
                    this.entity.swingHand(Hand.MAIN_HAND);
                    boolean attacked = this.entity.tryAttack(this.component.getFollowTarget());
                    
                    // and then try a second attack if we're in range for good measure :)
                    if (attacked && random.nextFloat() < 0.4f) {
                        this.entity.swingHand(Hand.MAIN_HAND);
                        this.entity.tryAttack(this.component.getFollowTarget());
                    }
                }
            }
            
            this.component.setCurrentlyActingNatural(false);
            this.randomAction = null;
            this.currentActionCount = 0;
            this.setRandomActCoolDown();
        } else {
            this.currentActionCooldown--;
        }
    }
}

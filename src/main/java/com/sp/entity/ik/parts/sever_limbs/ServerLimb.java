package com.sp.entity.ik.parts.sever_limbs;

import com.sp.entity.ik.components.IKLegComponent;
import com.sp.entity.ik.util.ArrayUtil;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;

import java.util.ArrayList;
import java.util.List;

public class ServerLimb {
    public Vec3d target = Vec3d.ZERO;
    public Vec3d oldTarget = Vec3d.ZERO;
    public Vec3d pos = Vec3d.ZERO;
    public Vec3d baseOffset;
    public boolean hasToBeSet = true;
    public final Random random = Random.create();
    public boolean playedStepSound;
    public StepCallback stepCallback;

    public ServerLimb(Vec3d baseOffset) {
        this.baseOffset = baseOffset;
    }

    public ServerLimb(Vec3d baseOffset, StepCallback stepCallback) {
        this.baseOffset = baseOffset;
        this.stepCallback = stepCallback;
    }

    public ServerLimb(double x, double y, double z) {
        this.baseOffset = new Vec3d(x, y, z);
    }

    public ServerLimb(double x, double y, double z, StepCallback stepCallback) {
        this.baseOffset = new Vec3d(x, y, z);
        this.stepCallback = stepCallback;
    }

    public void set(Vec3d newPos) {
        this.pos = newPos;
        this.oldTarget = newPos;
        this.target = newPos;
        this.hasToBeSet = false;
    }

    public void tick(IKLegComponent legComponent, int i, double movementSpeed) {
        if (!this.pos.isInRange(this.target, 5 * legComponent.scale)) {
            this.pos = this.target;
            this.oldTarget = this.target;
        }

        if (!adjacentEndPointGrounded(legComponent.getEndPoints(), i)) {
            return;
        }

        Vec3d flatTarget = new Vec3d(this.target.x, 0, this.target.z);
        Vec3d flatPos = new Vec3d(this.pos.x, 0, this.pos.z);

        double flatDistanceToEndPos = flatTarget.distanceTo(flatPos);
        Vec3d raisedTarget = this.target.add(0, flatDistanceToEndPos, 0);

        Vec3d targetDirection = raisedTarget.subtract(this.pos).normalize();

        this.pos = this.pos.add(targetDirection.multiply((this.target.distanceTo(this.pos)) * movementSpeed));

        if (this.pos.isInRange(this.target, 0.3)) {
            if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
                if (!this.playedStepSound || this.stepCallback != null) {
                    this.stepCallback.onStep(this, legComponent, i, movementSpeed);
                }
            }

            this.pos = this.target;
            this.oldTarget = this.target;
        } else {
            this.playedStepSound = false;
        }
    }

    public interface StepCallback {
        default void onStep(ServerLimb limb, IKLegComponent legComponent, int i, double movementSpeed) {}
    }

    private boolean adjacentEndPointGrounded(List<ServerLimb> limbs, int index) {
        boolean areAllGrounded = true;

        for (int legIndex : adjacent(index)) {
            ServerLimb leg = ArrayUtil.getOrNull(limbs, legIndex);
            if (leg == null) continue;

            if (leg.isGrounded()) continue;

            areAllGrounded = false;
            break;
        }

        return areAllGrounded;
    }


    public Vec3d getPos() {
        return this.pos;
    }

    public void setPos(Vec3d pos) {
        this.pos = pos;
    }

    public void setTarget(Vec3d target) {
        this.target = target;
    }

    public boolean isGrounded() {
        return this.pos == this.oldTarget;
    }
    /**
     * THIS CODE FOLLOWING IS NOT MINE!!!! <p>
     * It was politely stolen form Cymaera, with their consent, on <a href="https://github.com/TheCymaera/minecraft-spider/blob/main/src/main/java/com/heledron/spideranimation/spider/LegLookUp.kt">GitHub</a> and then translated!
     **/
    public static List<List<Integer>> diagonalPairs(List<Integer> legs) {
        List<List<Integer>> result = new ArrayList<>();
        for (int leg : legs) {
            List<Integer> diagonal = new ArrayList<>(diagonal(leg));
            diagonal.add(leg);
            result.add(diagonal);
        }
        return result;
    }

    public static boolean isLeftLeg(int leg) {
        return leg % 2 == 0;
    }

    public static boolean isRightLeg(int leg) {
        return !isLeftLeg(leg);
    }

    public static int getPairIndex(int leg) {
        return leg / 2;
    }

    public static boolean isDiagonal1(int leg) {
        return getPairIndex(leg) % 2 == 0 ? isLeftLeg(leg) : isRightLeg(leg);
    }

    public static boolean isDiagonal2(int leg) {
        return !isDiagonal1(leg);
    }

    public static int diagonalFront(int leg) {
        return isLeftLeg(leg) ? leg - 1 : leg - 3;
    }

    public static int diagonalBack(int leg) {
        return isLeftLeg(leg) ? leg + 3 : leg + 1;
    }

    public static int front(int leg) {
        return leg - 2;
    }

    public static int back(int leg) {
        return leg + 2;
    }

    public static int horizontal(int leg) {
        return isLeftLeg(leg) ? leg + 1 : leg - 1;
    }

    public static List<Integer> diagonal(int leg) {
        return List.of(diagonalFront(leg), diagonalBack(leg));
    }

    public static List<Integer> adjacent(int leg) {
        return List.of(front(leg), back(leg), horizontal(leg));
    }
}

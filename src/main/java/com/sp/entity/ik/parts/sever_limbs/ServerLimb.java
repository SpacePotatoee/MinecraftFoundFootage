package com.sp.entity.ik.parts.sever_limbs;

import net.dumbcode.projectnublar.entity.ik.components.IKLegComponent;
import net.dumbcode.projectnublar.entity.ik.util.ArrayUtil;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

public class ServerLimb {
    public Vec3 target = Vec3.ZERO;
    public Vec3 oldTarget = Vec3.ZERO;
    public Vec3 pos = Vec3.ZERO;
    public Vec3 baseOffset;
    public boolean hasToBeSet = true;

    public ServerLimb(Vec3 baseOffset) {
        this.baseOffset = baseOffset;
    }

    public ServerLimb(double x, double y, double z) {
        this.baseOffset = new Vec3(x, y, z);
    }

    public void set(Vec3 newPos) {
        this.pos = newPos;
        this.oldTarget = newPos;
        this.target = newPos;
        this.hasToBeSet = false;
    }

    public void tick(IKLegComponent legComponent, int i, double movementSpeed) {
        if (!this.pos.closerThan(this.target, 5 * legComponent.scale)) {
            this.pos = this.target;
            this.oldTarget = this.target;
        }

        if (!adjacentEndPointGrounded(legComponent.getEndPoints(), i)) {
            return;
        }

        Vec3 flatTarget = new Vec3(this.target.x(), 0, this.target.z());
        Vec3 flatPos = new Vec3(this.pos.x(), 0, this.pos.z());

        double flatDistanceToEndPos = flatTarget.distanceTo(flatPos);
        Vec3 raisedTarget = this.target.add(0, flatDistanceToEndPos, 0);

        Vec3 targetDirection = raisedTarget.subtract(this.pos).normalize();

        this.pos = this.pos.add(targetDirection.scale((this.target.distanceTo(this.pos)) * movementSpeed));

        if (this.pos.closerThan(this.target, 0.3)) {
            this.pos = this.target;
            this.oldTarget = this.target;
        }
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


    public Vec3 getPos() {
        return this.pos;
    }

    public void setPos(Vec3 pos) {
        this.pos = pos;
    }

    public void setTarget(Vec3 target) {
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

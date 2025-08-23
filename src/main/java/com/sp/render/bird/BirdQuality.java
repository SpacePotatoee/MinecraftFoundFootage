package com.sp.render.bird;

public enum BirdQuality {
    LOW(1000, 1),
    MEDIUM(2500, 2),
    HIGH(5000, 4),
    ULTRA(8000, 6);

    private final int count;
    private final int flocks;

    BirdQuality(int birdCount, int flockCount){
        this.count = birdCount;
        this.flocks = flockCount;
    }

    public int getBirdCount() {
        return count;
    }

    public int getFlockCount() {
        return flocks;
    }
}

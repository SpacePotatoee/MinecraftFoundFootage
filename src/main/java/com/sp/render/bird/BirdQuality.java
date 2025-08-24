package com.sp.render.bird;

public enum BirdQuality {
    DISABLED(0, 0),
    LOW(2000, 2),
    MEDIUM(5000, 4),
    HIGH(8000, 7),
    ULTRA(16000, 13);

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

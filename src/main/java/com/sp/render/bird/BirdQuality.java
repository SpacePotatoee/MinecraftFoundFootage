package com.sp.render.bird;

public enum BirdQuality {
    LOW(200),
    MEDIUM(500),
    HIGH(1000),
    ULTRA(2500);

    private final int count;

    BirdQuality(int count){
        this.count = count;
    }

    public int getCount() {
        return count;
    }
}

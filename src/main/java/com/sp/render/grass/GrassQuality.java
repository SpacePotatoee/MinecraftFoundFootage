package com.sp.render.grass;

public enum GrassQuality {
    LOW(3.0f, 1, 200000),
    MEDIUM(5.0f, 2, 500000),
    HIGH(8.0f, 3, 1000000),
    ULTRA(10.0f, 5, 2500000);

    private final float density;
    private final int resolution;
    private final int count;

    GrassQuality(float density, int resolution, int count){
        this.density = density;
        this.resolution = resolution;
        this.count = count;
    }

    public float getDensity() {
        return density;
    }
    public int getResolution() {
        return resolution;
    }
    public int getCount() {
        return count;
    }
}

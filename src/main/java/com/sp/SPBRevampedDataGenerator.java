package com.sp;

import com.sp.datagen.ModModelProvider;
import com.sp.datagen.ModWorldGenerator;
import com.sp.world.levels.BackroomsLevels;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.minecraft.registry.RegistryBuilder;
import net.minecraft.registry.RegistryKeys;

public class SPBRevampedDataGenerator implements DataGeneratorEntrypoint {
	@Override
	public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
		FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();
		pack.addProvider(ModModelProvider::new);
	}

	@Override
	public void buildRegistry(RegistryBuilder registryBuilder) {
	}
}

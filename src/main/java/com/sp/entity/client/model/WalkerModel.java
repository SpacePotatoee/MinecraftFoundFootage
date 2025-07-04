package com.sp.entity.client.model;

import com.sp.SPBRevamped;
import com.sp.entity.custom.WalkerEntity;
import com.sp.entity.ik.model.GeckoLib.GeoModelAccessor;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
/**
 * Original model base by ShadowZecro <br>
 * Colored by VoidAtomicX <br>
 * Head reworked with the help of Jarton
 */
public class WalkerModel extends GeoModel<WalkerEntity> {
	private final Identifier MODEL = new Identifier(SPBRevamped.MOD_ID, "geo/entity/walker.geo.json");
	private final Identifier TEXTURES = new Identifier(SPBRevamped.MOD_ID, "textures/entity/walker/walker.png");


	@Override
	public void setCustomAnimations(WalkerEntity animatable, long instanceId, AnimationState<WalkerEntity> animationState) {
		super.setCustomAnimations(animatable, instanceId, animationState);

		animatable.tickComponentsClient(animatable, new GeoModelAccessor(this));
	}

	@Override
	public Identifier getModelResource(WalkerEntity animatable) {
		return MODEL;
	}

	@Override
	public Identifier getTextureResource(WalkerEntity animatable) {
		return TEXTURES;
	}

	@Override
	public Identifier getAnimationResource(WalkerEntity animatable) {
		return null;
	}

}
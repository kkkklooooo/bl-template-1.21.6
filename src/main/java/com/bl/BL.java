package com.bl;

import com.bl.entity.ModEntities;
import com.bl.entity.custom.ExpandingSphereEntity;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.bl.entity.ModEntities.EXPANDING_SPHERE;


public class BL implements ModInitializer {
	public static final String MOD_ID = "bl";
	public void createExpandingSphere(float max, Vec3d pos, World world) {
		// 假设ExpandingSphereEntity已经定义
		ExpandingSphereEntity sphere = new ExpandingSphereEntity(EXPANDING_SPHERE, world);
		sphere.maxRadius=max;
		sphere.expansionRate=max/50;
		sphere.setPosition(pos);
		world.spawnEntity(sphere);
	}

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

	/*
		FabricDefaultAttributeRegistry.register(
				entities.blentity, BLEntity.createMobAttributes().build()
		);*/
		//Registry.register(Registries.ENTITY_TYPE, Identifier.of(MOD_ID,"blentity"), blentity);
		ModEntities.register();
		FabricDefaultAttributeRegistry.register(EXPANDING_SPHERE, ExpandingSphereEntity.createAttributes());
		LOGGER.info("Hello Fabric world!");
	}
}
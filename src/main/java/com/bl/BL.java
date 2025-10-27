package com.bl;

import com.bl.entity.ModEntities;
import com.bl.entity.custom.ExpandingSphereEntity;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.WindChargeEntity;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.bl.entity.ModEntities.EXPANDING_SPHERE;


public class BL implements ModInitializer {
	public static final String MOD_ID = "bl";
	 public static void createExpandingSphere(double max, Vec3d pos, ServerWorld world) {
		// 假设ExpandingSphereEntity已经定义
		ExpandingSphereEntity sphere = new ExpandingSphereEntity(EXPANDING_SPHERE, world);
		sphere.SetMax(max);
		sphere.setPosition(pos);
		world.spawnEntity(sphere);
	}

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		/*Registry.register(Registries.SOUND_EVENT, Identifier.of(MOD_ID, "ppp"),
				SoundEvent.of(Identifier.of(MOD_ID, "ppp")));*/
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

	/*
		FabricDefaultAttributeRegistry.register(
				entities.blentity, BLEntity.createMobAttributes().build()
		);*/
		//Registry.register(Registries.ENTITY_TYPE, Identifier.of(MOD_ID,"blentity"), blentity);
		ModEntities.register();
		//FabricDefaultAttributeRegistry.register(EXPANDING_SPHERE, ExpandingSphereEntity.createAttributes());
		LOGGER.info("Hello Fabric world!");
	}
}
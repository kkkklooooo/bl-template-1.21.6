package com.bl;

import com.bl.entity.custom.BLEntity;
import com.bl.entity.custom.QuantumEntityy;
import com.bl.entity.entities;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.bl.entity.entities.blentity;

public class BL implements ModInitializer {
	public static final String MOD_ID = "bl";

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
		Registry.register(Registries.ENTITY_TYPE, Identifier.of(MOD_ID,"blentity"), blentity);
		//FabricDefaultAttributeRegistry.register(blentity, QuantumEntityy.createAttributes());
		LOGGER.info("Hello Fabric world!");
	}
}
package com.bl.entity;

import com.bl.entity.custom.BLEntity;
import com.bl.entity.custom.QuantumEntity;
import com.bl.entity.custom.QuantumEntityy;
import net.minecraft.entity.EntityType;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

import static com.bl.BL.MOD_ID;

public class entities {



    public static final RegistryKey<EntityType<?>> registryKey = RegistryKey.of(RegistryKeys.ENTITY_TYPE, Identifier.of(MOD_ID, "blentity"));
    public static final EntityType<QuantumEntityy> blentity = EntityType.Builder.create(QuantumEntityy::new, net.minecraft.entity.SpawnGroup.CREATURE).dimensions(1f, 2f).build(registryKey);
}

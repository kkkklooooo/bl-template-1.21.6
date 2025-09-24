package com.bl.entity;

import com.bl.entity.custom.*;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

import static com.bl.BL.MOD_ID;

public class ModEntities {



    public static final RegistryKey<EntityType<?>> registryKey = RegistryKey.of(RegistryKeys.ENTITY_TYPE, Identifier.of(MOD_ID, "blentity"));
    public static final RegistryKey<EntityType<?>> registryKey2 = RegistryKey.of(RegistryKeys.ENTITY_TYPE, Identifier.of(MOD_ID, "quantum_entity"));
    public static final RegistryKey<EntityType<?>> registryKey3 = RegistryKey.of(RegistryKeys.ENTITY_TYPE, Identifier.of(MOD_ID, "expanding_sphere"));
    public static final RegistryKey<EntityType<?>> registryKey4 = RegistryKey.of(RegistryKeys.ENTITY_TYPE, Identifier.of(MOD_ID, "quantum_block"));
    public static final EntityType<QuantumEntityy> QUANTUM_ENTITY = EntityType.Builder.create(QuantumEntityy::new, SpawnGroup.MISC).dimensions(1f, 2f).maxTrackingRange(64).trackingTickInterval(10).build(registryKey2);
    public static final EntityType<ExpandingSphereEntity> EXPANDING_SPHERE = EntityType.Builder.create(ExpandingSphereEntity::new, SpawnGroup.MISC).dimensions(1f, 2f).maxTrackingRange(64).trackingTickInterval(10).build(registryKey3);
    public static final EntityType<QuantumBlockEntity> QUANTUM_BLOCK = EntityType.Builder.create(QuantumBlockEntity::new, SpawnGroup.MISC).dimensions(1f, 2f).maxTrackingRange(64).trackingTickInterval(10).build(registryKey4);
    public static final EntityType<BLEntity> blentity = EntityType.Builder.create(BLEntity::new, SpawnGroup.MISC).dimensions(1f, 2f).maxTrackingRange(64).trackingTickInterval(10).build(registryKey);
    public static void register() {
        Registry.register(Registries.ENTITY_TYPE,
                Identifier.of(MOD_ID, "blentity"),
                blentity
        );
        Registry.register(Registries.ENTITY_TYPE,
                Identifier.of(MOD_ID, "quantum_entity"),
                QUANTUM_ENTITY
        );
        Registry.register(Registries.ENTITY_TYPE,
                Identifier.of(MOD_ID, "quantum_block"),
                QUANTUM_BLOCK
        );
        Registry.register(Registries.ENTITY_TYPE,
                Identifier.of(MOD_ID, "expanding_sphere"),
                EXPANDING_SPHERE
        );
    }

}

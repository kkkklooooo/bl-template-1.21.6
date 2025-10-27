package com.bl.entity.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

import static com.bl.BL.MOD_ID;
import static com.bl.entity.ModEntities.*;


@Environment(EnvType.CLIENT)
public class EntityClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {


        /*
        EntityRendererRegistry.register(
                entities.blentity,(ctx -> new BLRenderer(ctx))
        );*/
        EntityRendererRegistry.register(EXPANDING_SPHERE,(ctx -> new ESRenderer(ctx)));
        //EntityRendererRegistry.register(QUANTUM_BLOCK,(ctx -> new QBRenderer(ctx)));
        EntityModelLayerRegistry.registerModelLayer(ESRenderer.MODEL_CUBE_LAYER,ExpandingSphere::getTexturedModelData);
        //EntityModelLayerRegistry.registerModelLayer(BLRenderer.MODEL_CUBE_LAYER, BLmd::getTexturedModelData);
//        Registry.register(Registries.SOUND_EVENT, Identifier.of(MOD_ID, "metal_whistle"),
//                SoundEvent.of(Identifier.of(MOD_ID, "metal_whistle")));
    }
}

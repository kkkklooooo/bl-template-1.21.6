package com.bl.entity.client;

import com.bl.entity.custom.QuantumEntityRenderer;
import com.bl.entity.entities;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;


@Environment(EnvType.CLIENT)
public class BLclient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {

        /*
        EntityRendererRegistry.register(
                entities.blentity,(ctx -> new BLRenderer(ctx))
        );*/
        EntityRendererRegistry.register(
                entities.blentity,(ctx -> new QuantumEntityRenderer(ctx))
        );
        //EntityModelLayerRegistry.registerModelLayer(BLRenderer.MODEL_CUBE_LAYER, BLmd::getTexturedModelData);

    }
}

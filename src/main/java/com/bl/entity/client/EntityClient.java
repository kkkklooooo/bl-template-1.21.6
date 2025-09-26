package com.bl.entity.client;

import com.bl.entity.custom.QuantumBlockEntity;
import com.bl.entity.custom.QuantumEntityRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;

import static com.bl.entity.ModEntities.*;


@Environment(EnvType.CLIENT)
public class EntityClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {


        /*
        EntityRendererRegistry.register(
                entities.blentity,(ctx -> new BLRenderer(ctx))
        );*/
        EntityRendererRegistry.register(
                blentity,(ctx -> new BLRenderer(ctx))
        );
        EntityRendererRegistry.register(EXPANDING_SPHERE,(ctx -> new ESRenderer(ctx)));
        //EntityRendererRegistry.register(QUANTUM_BLOCK,(ctx -> new QBRenderer(ctx)));
        EntityModelLayerRegistry.registerModelLayer(BLRenderer.MODEL_CUBE_LAYER, com.bl.entity.client.blentity::getTexturedModelData);
        EntityModelLayerRegistry.registerModelLayer(ESRenderer.MODEL_CUBE_LAYER,ExpandingSphere::getTexturedModelData);
        //EntityModelLayerRegistry.registerModelLayer(BLRenderer.MODEL_CUBE_LAYER, BLmd::getTexturedModelData);

    }
}

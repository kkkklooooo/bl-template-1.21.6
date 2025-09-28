package com.bl.entity.client;

import com.bl.entity.custom.ExpandingSphereEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

import static com.bl.BL.MOD_ID;

public class ESRenderer extends EntityRenderer<ExpandingSphereEntity, ESrds> {
    float j=0.0f;
    private static final Identifier TEXTURE = Identifier.of(MOD_ID,"textures/entity/expanding_sphere/texture.png");
    public static final EntityModelLayer MODEL_CUBE_LAYER = new EntityModelLayer(Identifier.of(MOD_ID, "expanding_sphere"), "bone");
    public ESRenderer(EntityRendererFactory.Context context) {
        super(context);
    }

    @Override
    public ESrds createRenderState() {
        return new ESrds();
    }



}

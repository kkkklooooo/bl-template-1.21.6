package com.bl.entity.client;

import com.bl.entity.custom.ExpandingSphereEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

import static com.bl.BL.MOD_ID;

public class ESRenderer extends MobEntityRenderer<ExpandingSphereEntity, ESrds,ExpandingSphere> {
    float j=0.0f;
    private static final Identifier TEXTURE = Identifier.of(MOD_ID,"textures/entity/expanding_sphere/texture.png");
    public static final EntityModelLayer MODEL_CUBE_LAYER = new EntityModelLayer(Identifier.of(MOD_ID, "expanding_sphere"), "bone");
    public ESRenderer(EntityRendererFactory.Context context) {
        super(context,new ExpandingSphere(context.getPart(MODEL_CUBE_LAYER)), 0.5f);
    }

    @Override
    public ESrds createRenderState() {
        return new ESrds();
    }

    @Override
    public Identifier getTexture(ESrds state) {
        return TEXTURE;
    }

    @Override
    public void render(ESrds livingEntityRenderState, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
        matrixStack.scale(10.0F+j,10.0f+j,10.0f+j);
        j+=5.0f;
        super.render(livingEntityRenderState, matrixStack, vertexConsumerProvider, i);
    }
    /*
    @Override
    public Identifier getTexture(BLrds state) {
        return TEXTURE;
    }*/

}

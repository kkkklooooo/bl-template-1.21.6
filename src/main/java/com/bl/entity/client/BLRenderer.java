package com.bl.entity.client;

import com.bl.entity.custom.BLEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.util.Identifier;

import static com.bl.BL.MOD_ID;

public class BLRenderer extends MobEntityRenderer<BLEntity, BLrds, blentity> {
    private static final Identifier TEXTURE = Identifier.of(MOD_ID,"textures/entity/blentity/blentity.png");
    public static final EntityModelLayer MODEL_CUBE_LAYER = new EntityModelLayer(Identifier.of(MOD_ID, "blentity"), "bone");
    public BLRenderer(EntityRendererFactory.Context context) {
        super(context,new blentity(context.getPart(MODEL_CUBE_LAYER)), 0.5f);
    }

    @Override
    public BLrds createRenderState() {
        return new BLrds();
    }


    @Override
    public Identifier getTexture(BLrds state) {
        return TEXTURE;
    }
}

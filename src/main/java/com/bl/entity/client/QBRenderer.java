package com.bl.entity.client;

import com.bl.entity.custom.ExpandingSphereEntity;
import com.bl.entity.custom.QuantumBlockEntity;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.util.Identifier;

import static com.bl.BL.MOD_ID;

public class QBRenderer extends EntityRenderer<QuantumBlockEntity, BLrds> {
    private static final Identifier TEXTURE = Identifier.of(MOD_ID,"textures/entity/blentity/blentity.png");
    public static final EntityModelLayer MODEL_CUBE_LAYER = new EntityModelLayer(Identifier.of(MOD_ID, "blentity"), "bone");
    public QBRenderer(EntityRendererFactory.Context context) {
        super(context);
    }

    @Override
    public BLrds createRenderState() {
        return new BLrds();
    }

    /*
    @Override
    public Identifier getTexture(BLrds state) {
        return TEXTURE;
    }*/

}

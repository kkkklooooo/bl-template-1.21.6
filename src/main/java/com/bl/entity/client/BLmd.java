package com.bl.entity.client;

import net.minecraft.client.model.*;
import net.minecraft.client.render.entity.model.EntityModel;

public class BLmd extends EntityModel<BLrds> {
    private final ModelPart all;
    public BLmd(ModelPart root) {
        super(root);
        this.all = root.getChild("all");
    }


    public static TexturedModelData getTexturedModelData()  {
        ModelData md= new ModelData();
        ModelPartData modelPartData = md.getRoot();
        ModelPartData all = modelPartData.addChild("all", ModelPartBuilder.create().uv(0, 12).cuboid(-1.0F, -2.0F, -6.0F, 2.0F, 2.0F, 10.0F, new Dilation(0.0F))
                .uv(14, 12).cuboid(-1.5F, -4.0F, 3.0F, 3.0F, 3.0F, 3.0F, new Dilation(0.0F))
                .uv(0, 4).cuboid(-2.3F, -3.0F, 4.5F, 1.5F, 1.5F, 1.5F, new Dilation(0.0F))
                .uv(0, 0).cuboid(0.7F, -3.0F, 4.5F, 1.5F, 1.5F, 1.5F, new Dilation(0.0F)), ModelTransform.of(0.0F, 24.0F, 0.0F, 0.0F, 3.1416F, 0.0F));
        return TexturedModelData.of(md, 64, 32); // Adjust texture size as needed
    }
}

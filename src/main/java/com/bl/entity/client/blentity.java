package com.bl.entity.client;

import com.bl.entity.client.BLrds;
import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;

// Made with Blockbench 4.12.6
// Exported for Minecraft version 1.17+ for Yarn
// Paste this class into your mod and generate all required imports
public class blentity extends EntityModel<BLrds> {
	private final ModelPart bone;
	public blentity(ModelPart root) {
        super(root);
        this.bone = root.getChild("bone");
	}
	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		ModelPartData bone = modelPartData.addChild("bone", ModelPartBuilder.create().uv(0, 0).cuboid(-1.0F, -2.0F, -1.0F, 2.0F, 2.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 24.0F, 0.0F,0.0F,3.1416F,0.0F));
		return TexturedModelData.of(modelData, 16, 16);
	}

	public void setAngles(BLrds b) {
		super.setAngles(b);
	}
	public void render(MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay, float red, float green, float blue, float alpha) {
		bone.render(matrices, vertexConsumer, light, overlay);
	}
}
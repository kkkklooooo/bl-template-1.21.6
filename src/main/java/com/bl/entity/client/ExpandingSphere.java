package com.bl.entity.client;

import net.minecraft.client.model.*;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.entity.Entity;

// Made with Blockbench 4.12.6
// Exported for Minecraft version 1.17+ for Yarn
// Paste this class into your mod and generate all required imports
public class ExpandingSphere extends EntityModel<ESrds> {
	private final ModelPart hexadecagon;
	private final ModelPart hexadecagon2;
	private final ModelPart hexadecagon3;
	private final ModelPart hexadecagon4;
	private final ModelPart hexadecagon5;
	private final ModelPart hexadecagon6;
	private  final ModelPart bone;
	public ExpandingSphere(ModelPart root) {
        super(root);
		this.bone = root.getChild("bone");
        this.hexadecagon = bone.getChild("hexadecagon");
		this.hexadecagon2 = bone.getChild("hexadecagon2");
		this.hexadecagon3 = bone.getChild("hexadecagon3");
		this.hexadecagon4 = bone.getChild("hexadecagon4");
		this.hexadecagon5 = bone.getChild("hexadecagon5");
		this.hexadecagon6 = bone.getChild("hexadecagon6");
	}
	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		ModelPartData bone = modelPartData.addChild("bone", ModelPartBuilder.create(), ModelTransform.of(0.0F, 24.0F, 0.0F,0.0F,3.1416F,0.0F));

		ModelPartData hexadecagon = bone.addChild("hexadecagon", ModelPartBuilder.create().uv(0, 4).cuboid(-0.5F, -0.3978F, -2.0F, 1.0F, 0.7956F, 1.0F, new Dilation(0.0F))
				.uv(4, 6).cuboid(-0.5F, -0.3978F, 1.0F, 1.0F, 0.7956F, 1.0F, new Dilation(0.0F))
				.uv(20, 14).cuboid(-0.5F, 1.0F, -0.3978F, 1.0F, 1.0F, 0.7956F, new Dilation(0.0F))
				.uv(20, 18).cuboid(-0.5F, -2.0F, -0.3978F, 1.0F, 1.0F, 0.7956F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F,0.0F,3.1416F,0.0F));

		ModelPartData hexadecagon_r1 = hexadecagon.addChild("hexadecagon_r1", ModelPartBuilder.create().uv(20, 20).cuboid(-0.5F, -2.0F, -0.3978F, 1.0F, 1.0F, 0.7956F, new Dilation(0.0F))
				.uv(16, 20).cuboid(-0.5F, 1.0F, -0.3978F, 1.0F, 1.0F, 0.7956F, new Dilation(0.0F))
				.uv(0, 8).cuboid(-0.5F, -0.3978F, 1.0F, 1.0F, 0.7956F, 1.0F, new Dilation(0.0F))
				.uv(4, 0).cuboid(-0.5F, -0.3978F, -2.0F, 1.0F, 0.7956F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, -0.3927F, 0.0F, 0.0F));

		ModelPartData hexadecagon_r2 = hexadecagon.addChild("hexadecagon_r2", ModelPartBuilder.create().uv(20, 16).cuboid(-0.5F, -2.0F, -0.3978F, 1.0F, 1.0F, 0.7956F, new Dilation(0.0F))
				.uv(20, 12).cuboid(-0.5F, 1.0F, -0.3978F, 1.0F, 1.0F, 0.7956F, new Dilation(0.0F))
				.uv(0, 6).cuboid(-0.5F, -0.3978F, 1.0F, 1.0F, 0.7956F, 1.0F, new Dilation(0.0F))
				.uv(0, 2).cuboid(-0.5F, -0.3978F, -2.0F, 1.0F, 0.7956F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.3927F, 0.0F, 0.0F));

		ModelPartData hexadecagon_r3 = hexadecagon.addChild("hexadecagon_r3", ModelPartBuilder.create().uv(8, 0).cuboid(-0.5F, -0.3978F, 1.0F, 1.0F, 0.7956F, 1.0F, new Dilation(0.0F))
				.uv(4, 2).cuboid(-0.5F, -0.3978F, -2.0F, 1.0F, 0.7956F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, -0.7854F, 0.0F, 0.0F));

		ModelPartData hexadecagon_r4 = hexadecagon.addChild("hexadecagon_r4", ModelPartBuilder.create().uv(4, 4).cuboid(-0.5F, -0.3978F, 1.0F, 1.0F, 0.7956F, 1.0F, new Dilation(0.0F))
				.uv(0, 0).cuboid(-0.5F, -0.3978F, -2.0F, 1.0F, 0.7956F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.7854F, 0.0F, 0.0F));

		ModelPartData hexadecagon2 = bone.addChild("hexadecagon2", ModelPartBuilder.create().uv(8, 4).cuboid(-0.5F, -0.3978F, -2.0F, 1.0F, 0.7956F, 1.0F, new Dilation(0.0F))
				.uv(8, 10).cuboid(-0.5F, -0.3978F, 1.0F, 1.0F, 0.7956F, 1.0F, new Dilation(0.0F))
				.uv(4, 22).cuboid(-0.5F, 1.0F, -0.3978F, 1.0F, 1.0F, 0.7956F, new Dilation(0.0F))
				.uv(16, 22).cuboid(-0.5F, -2.0F, -0.3978F, 1.0F, 1.0F, 0.7956F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -0.5236F, 0.0F));

		ModelPartData hexadecagon_r5 = hexadecagon2.addChild("hexadecagon_r5", ModelPartBuilder.create().uv(20, 22).cuboid(-0.5F, -2.0F, -0.3978F, 1.0F, 1.0F, 0.7956F, new Dilation(0.0F))
				.uv(8, 22).cuboid(-0.5F, 1.0F, -0.3978F, 1.0F, 1.0F, 0.7956F, new Dilation(0.0F))
				.uv(0, 12).cuboid(-0.5F, -0.3978F, 1.0F, 1.0F, 0.7956F, 1.0F, new Dilation(0.0F))
				.uv(8, 6).cuboid(-0.5F, -0.3978F, -2.0F, 1.0F, 0.7956F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, -0.3927F, 0.0F, 0.0F));

		ModelPartData hexadecagon_r6 = hexadecagon2.addChild("hexadecagon_r6", ModelPartBuilder.create().uv(12, 22).cuboid(-0.5F, -2.0F, -0.3978F, 1.0F, 1.0F, 0.7956F, new Dilation(0.0F))
				.uv(0, 22).cuboid(-0.5F, 1.0F, -0.3978F, 1.0F, 1.0F, 0.7956F, new Dilation(0.0F))
				.uv(4, 10).cuboid(-0.5F, -0.3978F, 1.0F, 1.0F, 0.7956F, 1.0F, new Dilation(0.0F))
				.uv(4, 8).cuboid(-0.5F, -0.3978F, -2.0F, 1.0F, 0.7956F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.3927F, 0.0F, 0.0F));

		ModelPartData hexadecagon_r7 = hexadecagon2.addChild("hexadecagon_r7", ModelPartBuilder.create().uv(12, 0).cuboid(-0.5F, -0.3978F, 1.0F, 1.0F, 0.7956F, 1.0F, new Dilation(0.0F))
				.uv(8, 8).cuboid(-0.5F, -0.3978F, -2.0F, 1.0F, 0.7956F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, -0.7854F, 0.0F, 0.0F));

		ModelPartData hexadecagon_r8 = hexadecagon2.addChild("hexadecagon_r8", ModelPartBuilder.create().uv(0, 10).cuboid(-0.5F, -0.3978F, 1.0F, 1.0F, 0.7956F, 1.0F, new Dilation(0.0F))
				.uv(8, 2).cuboid(-0.5F, -0.3978F, -2.0F, 1.0F, 0.7956F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.7854F, 0.0F, 0.0F));

		ModelPartData hexadecagon3 = bone.addChild("hexadecagon3", ModelPartBuilder.create().uv(12, 4).cuboid(-0.5F, -0.3978F, -2.0F, 1.0F, 0.7956F, 1.0F, new Dilation(0.0F))
				.uv(12, 12).cuboid(-0.5F, -0.3978F, 1.0F, 1.0F, 0.7956F, 1.0F, new Dilation(0.0F))
				.uv(24, 0).cuboid(-0.5F, 1.0F, -0.3978F, 1.0F, 1.0F, 0.7956F, new Dilation(0.0F))
				.uv(24, 4).cuboid(-0.5F, -2.0F, -0.3978F, 1.0F, 1.0F, 0.7956F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

		ModelPartData hexadecagon_r9 = hexadecagon3.addChild("hexadecagon_r9", ModelPartBuilder.create().uv(24, 6).cuboid(-0.5F, -2.0F, -0.3978F, 1.0F, 1.0F, 0.7956F, new Dilation(0.0F))
				.uv(24, 2).cuboid(-0.5F, 1.0F, -0.3978F, 1.0F, 1.0F, 0.7956F, new Dilation(0.0F))
				.uv(0, 14).cuboid(-0.5F, -0.3978F, 1.0F, 1.0F, 0.7956F, 1.0F, new Dilation(0.0F))
				.uv(12, 6).cuboid(-0.5F, -0.3978F, -2.0F, 1.0F, 0.7956F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, -0.3927F, 0.0F, 0.0F));

		ModelPartData hexadecagon_r10 = hexadecagon3.addChild("hexadecagon_r10", ModelPartBuilder.create().uv(4, 24).cuboid(-0.5F, -2.0F, -0.3978F, 1.0F, 1.0F, 0.7956F, new Dilation(0.0F))
				.uv(0, 24).cuboid(-0.5F, 1.0F, -0.3978F, 1.0F, 1.0F, 0.7956F, new Dilation(0.0F))
				.uv(12, 10).cuboid(-0.5F, -0.3978F, 1.0F, 1.0F, 0.7956F, 1.0F, new Dilation(0.0F))
				.uv(4, 12).cuboid(-0.5F, -0.3978F, -2.0F, 1.0F, 0.7956F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.3927F, 0.0F, 0.0F));

		ModelPartData hexadecagon_r11 = hexadecagon3.addChild("hexadecagon_r11", ModelPartBuilder.create().uv(4, 14).cuboid(-0.5F, -0.3978F, 1.0F, 1.0F, 0.7956F, 1.0F, new Dilation(0.0F))
				.uv(8, 12).cuboid(-0.5F, -0.3978F, -2.0F, 1.0F, 0.7956F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, -0.7854F, 0.0F, 0.0F));

		ModelPartData hexadecagon_r12 = hexadecagon3.addChild("hexadecagon_r12", ModelPartBuilder.create().uv(12, 8).cuboid(-0.5F, -0.3978F, 1.0F, 1.0F, 0.7956F, 1.0F, new Dilation(0.0F))
				.uv(12, 2).cuboid(-0.5F, -0.3978F, -2.0F, 1.0F, 0.7956F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.7854F, 0.0F, 0.0F));

		ModelPartData hexadecagon4 = bone.addChild("hexadecagon4", ModelPartBuilder.create().uv(0, 16).cuboid(-0.5F, -0.3978F, -2.0F, 1.0F, 0.7956F, 1.0F, new Dilation(0.0F))
				.uv(16, 6).cuboid(-0.5F, -0.3978F, 1.0F, 1.0F, 0.7956F, 1.0F, new Dilation(0.0F))
				.uv(24, 8).cuboid(-0.5F, 1.0F, -0.3978F, 1.0F, 1.0F, 0.7956F, new Dilation(0.0F))
				.uv(24, 12).cuboid(-0.5F, -2.0F, -0.3978F, 1.0F, 1.0F, 0.7956F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.5708F, 0.0F));

		ModelPartData hexadecagon_r13 = hexadecagon4.addChild("hexadecagon_r13", ModelPartBuilder.create().uv(24, 14).cuboid(-0.5F, -2.0F, -0.3978F, 1.0F, 1.0F, 0.7956F, new Dilation(0.0F))
				.uv(24, 10).cuboid(-0.5F, 1.0F, -0.3978F, 1.0F, 1.0F, 0.7956F, new Dilation(0.0F))
				.uv(8, 16).cuboid(-0.5F, -0.3978F, 1.0F, 1.0F, 0.7956F, 1.0F, new Dilation(0.0F))
				.uv(16, 0).cuboid(-0.5F, -0.3978F, -2.0F, 1.0F, 0.7956F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, -0.3927F, 0.0F, 0.0F));

		ModelPartData hexadecagon_r14 = hexadecagon4.addChild("hexadecagon_r14", ModelPartBuilder.create().uv(12, 24).cuboid(-0.5F, -2.0F, -0.3978F, 1.0F, 1.0F, 0.7956F, new Dilation(0.0F))
				.uv(8, 24).cuboid(-0.5F, 1.0F, -0.3978F, 1.0F, 1.0F, 0.7956F, new Dilation(0.0F))
				.uv(16, 4).cuboid(-0.5F, -0.3978F, 1.0F, 1.0F, 0.7956F, 1.0F, new Dilation(0.0F))
				.uv(12, 14).cuboid(-0.5F, -0.3978F, -2.0F, 1.0F, 0.7956F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.3927F, 0.0F, 0.0F));

		ModelPartData hexadecagon_r15 = hexadecagon4.addChild("hexadecagon_r15", ModelPartBuilder.create().uv(16, 8).cuboid(-0.5F, -0.3978F, 1.0F, 1.0F, 0.7956F, 1.0F, new Dilation(0.0F))
				.uv(16, 2).cuboid(-0.5F, -0.3978F, -2.0F, 1.0F, 0.7956F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, -0.7854F, 0.0F, 0.0F));

		ModelPartData hexadecagon_r16 = hexadecagon4.addChild("hexadecagon_r16", ModelPartBuilder.create().uv(4, 16).cuboid(-0.5F, -0.3978F, 1.0F, 1.0F, 0.7956F, 1.0F, new Dilation(0.0F))
				.uv(8, 14).cuboid(-0.5F, -0.3978F, -2.0F, 1.0F, 0.7956F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.7854F, 0.0F, 0.0F));

		ModelPartData hexadecagon5 = bone.addChild("hexadecagon5", ModelPartBuilder.create().uv(16, 12).cuboid(-0.5F, -0.3978F, -2.0F, 1.0F, 0.7956F, 1.0F, new Dilation(0.0F))
				.uv(8, 18).cuboid(-0.5F, -0.3978F, 1.0F, 1.0F, 0.7956F, 1.0F, new Dilation(0.0F))
				.uv(24, 16).cuboid(-0.5F, 1.0F, -0.3978F, 1.0F, 1.0F, 0.7956F, new Dilation(0.0F))
				.uv(24, 20).cuboid(-0.5F, -2.0F, -0.3978F, 1.0F, 1.0F, 0.7956F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -2.0944F, 0.0F));

		ModelPartData hexadecagon_r17 = hexadecagon5.addChild("hexadecagon_r17", ModelPartBuilder.create().uv(24, 22).cuboid(-0.5F, -2.0F, -0.3978F, 1.0F, 1.0F, 0.7956F, new Dilation(0.0F))
				.uv(24, 18).cuboid(-0.5F, 1.0F, -0.3978F, 1.0F, 1.0F, 0.7956F, new Dilation(0.0F))
				.uv(12, 18).cuboid(-0.5F, -0.3978F, 1.0F, 1.0F, 0.7956F, 1.0F, new Dilation(0.0F))
				.uv(16, 14).cuboid(-0.5F, -0.3978F, -2.0F, 1.0F, 0.7956F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, -0.3927F, 0.0F, 0.0F));

		ModelPartData hexadecagon_r18 = hexadecagon5.addChild("hexadecagon_r18", ModelPartBuilder.create().uv(20, 24).cuboid(-0.5F, -2.0F, -0.3978F, 1.0F, 1.0F, 0.7956F, new Dilation(0.0F))
				.uv(16, 24).cuboid(-0.5F, 1.0F, -0.3978F, 1.0F, 1.0F, 0.7956F, new Dilation(0.0F))
				.uv(4, 18).cuboid(-0.5F, -0.3978F, 1.0F, 1.0F, 0.7956F, 1.0F, new Dilation(0.0F))
				.uv(12, 16).cuboid(-0.5F, -0.3978F, -2.0F, 1.0F, 0.7956F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.3927F, 0.0F, 0.0F));

		ModelPartData hexadecagon_r19 = hexadecagon5.addChild("hexadecagon_r19", ModelPartBuilder.create().uv(16, 18).cuboid(-0.5F, -0.3978F, 1.0F, 1.0F, 0.7956F, 1.0F, new Dilation(0.0F))
				.uv(16, 16).cuboid(-0.5F, -0.3978F, -2.0F, 1.0F, 0.7956F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, -0.7854F, 0.0F, 0.0F));

		ModelPartData hexadecagon_r20 = hexadecagon5.addChild("hexadecagon_r20", ModelPartBuilder.create().uv(0, 18).cuboid(-0.5F, -0.3978F, 1.0F, 1.0F, 0.7956F, 1.0F, new Dilation(0.0F))
				.uv(16, 10).cuboid(-0.5F, -0.3978F, -2.0F, 1.0F, 0.7956F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.7854F, 0.0F, 0.0F));

		ModelPartData hexadecagon6 = bone.addChild("hexadecagon6", ModelPartBuilder.create().uv(20, 2).cuboid(-0.5F, -0.3978F, -2.0F, 1.0F, 0.7956F, 1.0F, new Dilation(0.0F))
				.uv(20, 8).cuboid(-0.5F, -0.3978F, 1.0F, 1.0F, 0.7956F, 1.0F, new Dilation(0.0F))
				.uv(0, 26).cuboid(-0.5F, 1.0F, -0.3978F, 1.0F, 1.0F, 0.7956F, new Dilation(0.0F))
				.uv(12, 26).cuboid(-0.5F, -2.0F, -0.3978F, 1.0F, 1.0F, 0.7956F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -2.618F, 0.0F));

		ModelPartData hexadecagon_r21 = hexadecagon6.addChild("hexadecagon_r21", ModelPartBuilder.create().uv(16, 26).cuboid(-0.5F, -2.0F, -0.3978F, 1.0F, 1.0F, 0.7956F, new Dilation(0.0F))
				.uv(4, 26).cuboid(-0.5F, 1.0F, -0.3978F, 1.0F, 1.0F, 0.7956F, new Dilation(0.0F))
				.uv(20, 10).cuboid(-0.5F, -0.3978F, 1.0F, 1.0F, 0.7956F, 1.0F, new Dilation(0.0F))
				.uv(4, 20).cuboid(-0.5F, -0.3978F, -2.0F, 1.0F, 0.7956F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, -0.3927F, 0.0F, 0.0F));

		ModelPartData hexadecagon_r22 = hexadecagon6.addChild("hexadecagon_r22", ModelPartBuilder.create().uv(8, 26).cuboid(-0.5F, -2.0F, -0.3978F, 1.0F, 1.0F, 0.7956F, new Dilation(0.0F))
				.uv(24, 24).cuboid(-0.5F, 1.0F, -0.3978F, 1.0F, 1.0F, 0.7956F, new Dilation(0.0F))
				.uv(8, 20).cuboid(-0.5F, -0.3978F, 1.0F, 1.0F, 0.7956F, 1.0F, new Dilation(0.0F))
				.uv(20, 0).cuboid(-0.5F, -0.3978F, -2.0F, 1.0F, 0.7956F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.3927F, 0.0F, 0.0F));

		ModelPartData hexadecagon_r23 = hexadecagon6.addChild("hexadecagon_r23", ModelPartBuilder.create().uv(12, 20).cuboid(-0.5F, -0.3978F, 1.0F, 1.0F, 0.7956F, 1.0F, new Dilation(0.0F))
				.uv(20, 4).cuboid(-0.5F, -0.3978F, -2.0F, 1.0F, 0.7956F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, -0.7854F, 0.0F, 0.0F));

		ModelPartData hexadecagon_r24 = hexadecagon6.addChild("hexadecagon_r24", ModelPartBuilder.create().uv(20, 6).cuboid(-0.5F, -0.3978F, 1.0F, 1.0F, 0.7956F, 1.0F, new Dilation(0.0F))
				.uv(0, 20).cuboid(-0.5F, -0.3978F, -2.0F, 1.0F, 0.7956F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.7854F, 0.0F, 0.0F));
		return TexturedModelData.of(modelData, 32, 32);
	}
	@Override
	public void setAngles(ESrds b) {
		super.setAngles(b);
	}
	/*
	public void render(MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay, float red, float green, float blue, float alpha) {
		hexadecagon.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
		hexadecagon2.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
		hexadecagon3.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
		hexadecagon4.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
		hexadecagon5.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
		hexadecagon6.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
	}*/
}
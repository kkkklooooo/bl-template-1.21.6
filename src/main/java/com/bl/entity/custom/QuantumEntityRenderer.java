package com.bl.entity.custom;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.util.math.MatrixStack;

public class QuantumEntityRenderer extends EntityRenderer<QuantumEntity,QuantumEntityRD> {
    public QuantumEntityRenderer(EntityRendererFactory.Context ctx) {
        super(ctx);
    }

    @Override
    public QuantumEntityRD createRenderState() {
        return null;
    }

    /*
    @Override
    public void render(QuantumEntity entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        float observedProgress = entity.getObservedProgress();

        // 根据观察进度调整透明度
        float alpha = 1.0F - observedProgress;

        // 渲染实体的代码，使用alpha值控制透明度
        // ...

        super.render(entity, yaw, tickDelta, matrices, vertexConsumers, light);
    }*/

    @Override
    public void render(QuantumEntityRD state, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        //float observedProgress = entity.getObservedProgress();

        // 根据观察进度调整透明度
        //float alpha = 1.0F - observedProgress;

        // 渲染实体的代码，使用alpha值控制透明度
        // ...
        super.render(state, matrices, vertexConsumers, light);
    }


    // 其他渲染方法...
}
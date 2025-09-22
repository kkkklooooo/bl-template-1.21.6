package com.bl.entity.custom;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.world.World;

public class QuantumEntityy extends QuantumEntity {
    private EntityType<?> modelEntity;

    public QuantumEntityy(EntityType<?> type, World world) {
        super(type, world);
    }

    @Override
    public boolean damage(ServerWorld world, DamageSource source, float amount) {
        return false;
    }

    @Override
    protected void readCustomData(ReadView view) {

    }

    @Override
    protected void writeCustomData(WriteView view) {

    }

    public void setModelEntity(EntityType<?> modelEntity) {
        this.modelEntity = modelEntity;
    }

    @Override
    protected void onFullyObserved() {
        // 生成消失的粒子效果
        spawnDisappearanceParticles();

        // 移除实体
        this.discard();
    }

    private void spawnDisappearanceParticles() {
        World world = this.getWorld();
        for (int i = 0; i < 10; i++) {
            double offsetX = (random.nextDouble() - 0.5) * 0.5;
            double offsetY = (random.nextDouble() - 0.5) * 0.5;
            double offsetZ = (random.nextDouble() - 0.5) * 0.5;


            world.addParticleClient(ParticleTypes.ELECTRIC_SPARK,
                    this.getX() + offsetX,
                    this.getY() + offsetY,
                    this.getZ() + offsetZ,
                    0, 0, 0);
        }
    }

    // 其他方法和渲染相关代码...
}

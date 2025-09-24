package com.bl.entity.custom;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.world.World;

import java.util.List;

import static com.bl.entity.ModEntities.EXPANDING_SPHERE;

public class BLEntity extends MobEntity {

        // 生命周期（Tick数）
        private int lifeTime;
        // 目标实体（如果有，例如被引导的球状闪电）
        private Entity target;

        public BLEntity(EntityType<?> type, World world) {
            super((EntityType<? extends MobEntity>) type, world);
            //this.noClip = true; // 允许穿墙
            this.lifeTime = 200; // 设置一个默认生命周期（10秒，20tick/秒）
        }

        @Override
        public void tick() {
            super.tick();
            this.lifeTime--;

            // 生命周期结束，移除实体
            if (this.lifeTime <= 0) {
                this.discard();
                return;
            }

            // 这里可以添加一些粒子效果
            spawnParticles();

            // 简单的移动逻辑（例如向目标移动或随机飘动）
            if (target != null) {
                // 向目标移动的逻辑
            } else {
                // 随机移动或其它行为
            }

            // 检测与其他球状闪电的碰撞
            checkCollisions();
        }

        private void spawnParticles() {
            // 生成粒子效果，例如白色或蓝色的球体
            this.getWorld().addParticleClient(ParticleTypes.ELECTRIC_SPARK,
                    this.getX(), this.getY(), this.getZ(),
                    0, 0, 0);
        }

        private void checkCollisions() {
            // 获取周围其他球状闪电实体
            List<BLEntity> otherLightnings =
                    this.getWorld().getEntitiesByClass(BLEntity.class,
                            this.getBoundingBox().expand(0.5), Entity::isAlive);

            for (BLEntity other : otherLightnings) {
                if (other != this && this.distanceTo(other) < 0.5f) {
                    // 碰撞发生，创建膨胀光球
                    createExpandingSphere();
                    this.discard();
                    other.discard();
                    break;
                }
            }
        }

        private void createExpandingSphere() {
            ExpandingSphereEntity sphere = new ExpandingSphereEntity(
                    EXPANDING_SPHERE, this.getWorld());
            sphere.setPosition(this.getPos());
            this.getWorld().spawnEntity(sphere);
        }


    public static DefaultAttributeContainer createAttributes(){
        return MobEntity.createMobAttributes()
                .add(EntityAttributes.MAX_HEALTH,33550336.0)
                .add(EntityAttributes.MOVEMENT_SPEED,1f)
                .add(EntityAttributes.FLYING_SPEED,1f)
                .add(EntityAttributes.ATTACK_DAMAGE,0.5f)
                .add(EntityAttributes.FOLLOW_RANGE,10)
                .build();
    }
}

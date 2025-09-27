package com.bl.entity.custom;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

import java.util.List;

import static com.bl.entity.ModEntities.EXPANDING_SPHERE;
//import org.lambdajh.lambdynamiclights.DynamicLights;

public class BLEntity extends MobEntity {

    // 生命周期（Tick数）
    private int lifeTime;
    // 目标实体（如果有，例如被引导的球状闪电）
    private Entity target;
    // 随机移动相关变量
    private int movementCooldown = 0;
    private Vec3d currentMovement = Vec3d.ZERO;

    public BLEntity(EntityType<?> type, World world) {
        super((EntityType<? extends MobEntity>) type, world);
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

        /*if (this.isAlive()) {
            // 设置光源亮度 (0 - 15)，15为最大，如火把
            DynamicLights.setLuminance(this, 10);
        } else {
            // 当不需要发光时，移除光源
            DynamicLights.setLuminance(this, 0);
        }*/

        // 简单的移动逻辑（例如向目标移动或随机飘动）
        if (target != null) {
            // 向目标移动的逻辑
            // 这里可以添加追踪目标的代码
        } else {
            // 随机移动行为
            performRandomMovement();
        }

        // 检测与其他球状闪电的碰撞
        checkCollisions();
    }

    private void spawnParticles() {
        // 随机向四周发射粒子效果
        Random random = this.getWorld().random;

        // 增加白色粒子的数量（每次tick生成8-12个）
        int whiteParticleCount = 8 + random.nextInt(5);

        for (int i = 0; i < whiteParticleCount; i++) {
            // 更分散的方向向量 - 增加随机范围使粒子更分散
            double speed = 0.15 + random.nextDouble() * 0.25; // 增加速度范围
            double dx = (random.nextDouble() - 0.5) * 2 * speed;
            double dy = (random.nextDouble() - 0.7) * 2 * speed;
            double dz = (random.nextDouble() - 0.5) * 2 * speed;

            // 添加白色电火花粒子（主要粒子效果）
            this.getWorld().addParticleClient(ParticleTypes.ELECTRIC_SPARK,
                    this.getX() + (random.nextDouble() - 0.5) * 0.5, // 增加发射点随机偏移
                    this.getY() + 0.5 + (random.nextDouble() - 0.5) * 0.5,
                    this.getZ() + (random.nextDouble() - 0.5) * 0.5,
                    dx, dy, dz);
        }

        // 减少灵魂粒子的比例（从20%降到10%）
        if (random.nextFloat() < 0.1f) {
            // 每次只生成1-2个灵魂粒子
            int soulParticleCount = 1 + random.nextInt(2);

            for (int i = 0; i < soulParticleCount; i++) {
                // 灵魂粒子速度较慢
                double speed = 0.05 + random.nextDouble() * 0.1;
                double dx = (random.nextDouble() - 0.5) * 2 * speed;
                double dy = (random.nextDouble() - 0.5) * 2 * speed;
                double dz = (random.nextDouble() - 0.5) * 2 * speed;

                this.getWorld().addParticleClient(ParticleTypes.SOUL_FIRE_FLAME,
                        this.getX(), this.getY() + 0.5, this.getZ(),
                        dx, dy, dz);
            }
        }

        // 偶尔添加一些额外的特效粒子（例如在生命周期最后阶段）
        if (random.nextFloat() < 0.3f) {
            this.getWorld().addParticleClient(ParticleTypes.FLASH,
                    this.getX(), this.getY() + 0.5, this.getZ(),
                    0, 0, 0);
        }
    }

    private void performRandomMovement() {
        Random random = this.getWorld().random;

        // 减少移动冷却
        if (movementCooldown > 0) {
            movementCooldown--;

            // 应用当前移动向量
            if (!currentMovement.equals(Vec3d.ZERO)) {
                this.setVelocity(currentMovement);
            }
        } else {
            // 冷却结束，生成新的随机移动方向
            movementCooldown = 20 + random.nextInt(40); // 1-3秒的移动持续时间

            // 生成随机方向向量（稍微偏上，模拟漂浮）
            double dx = (random.nextDouble() - 0.5) * 0.2;
            double dy = random.nextDouble() * 0.1; // 轻微向上偏斜
            double dz = (random.nextDouble() - 0.5) * 0.2;

            currentMovement = new Vec3d(dx, dy, dz);
            this.setVelocity(currentMovement);
        }

        // 确保实体不会沉入地下
        if (this.isOnGround() && currentMovement.y < 0) {
            currentMovement = new Vec3d(currentMovement.x, 0.1, currentMovement.z);
        }

        // 添加轻微的漂浮效果
        this.setVelocity(this.getVelocity().add(0, 0.01, 0));
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
        // 假设ExpandingSphereEntity已经定义
         ExpandingSphereEntity sphere = new ExpandingSphereEntity(EXPANDING_SPHERE, this.getWorld());
         sphere.setPosition(this.getPos());
         this.getWorld().spawnEntity(sphere);
    }

    public static DefaultAttributeContainer createAttributes(){
        return MobEntity.createMobAttributes()
                .add(EntityAttributes.MAX_HEALTH, 33550336.0)
                .add(EntityAttributes.MOVEMENT_SPEED, 1f)
                .add(EntityAttributes.FLYING_SPEED, 1f)
                .add(EntityAttributes.ATTACK_DAMAGE, 0.5f)
                .add(EntityAttributes.FOLLOW_RANGE, 10)
                .build();
    }

    // 设置目标的方法
    public void setTarget(Entity target) {
        this.target = target;
    }

    // 获取目标的方法
}
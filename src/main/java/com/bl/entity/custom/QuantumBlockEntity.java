package com.bl.entity.custom;

import com.bl.BL;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class QuantumBlockEntity extends FallingBlockEntity {
    private BlockState blockState;
    private static final TrackedData<Integer> OBSERVED_TIME = DataTracker.registerData(QuantumEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private final Map<UUID, Integer> observerTimers = new HashMap<>();

    public QuantumBlockEntity(EntityType<?> type, World world) {
        super((EntityType<? extends FallingBlockEntity>) type, world);
    }

    public void setBlockState(BlockState blockState) {
        this.blockState = blockState;
    }

    public BlockState getBlockState() {
        return blockState;
    }

    protected void onFullyObserved() {
        // 生成消失的粒子效果
        spawnDisappearanceParticles();

        // 移除实体
        this.discard();
    }

    private void spawnDisappearanceParticles() {
        World world = this.getWorld();
        for (int i = 0; i < 15; i++) {
            double offsetX = (random.nextDouble() - 0.5) * 0.7;
            double offsetY = random.nextDouble() * 0.7;
            double offsetZ = (random.nextDouble() - 0.5) * 0.7;

            world.addParticleClient(ParticleTypes.ELECTRIC_SPARK,
                    this.getX() + offsetX,
                    this.getY() + offsetY,
                    this.getZ() + offsetZ,
                    0, 0, 0);
        }
    }

    /*@Override
    public boolean damage(ServerWorld world, DamageSource source, float amount) {
        return false;
    }*/

    @Override
    protected void readCustomData(ReadView view) {

    }

    @Override
    protected void writeCustomData(WriteView view) {

    }
    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        //this.dataTracker.startTracking(OBSERVED_TIME, 0);
        builder.add(OBSERVED_TIME,0).build();
        BL.LOGGER.info("dzfjkhhjdfas");
        //this.getDataTracker().;
        //this.dataTracker

    }

    @Override
    public void tick() {
        super.tick();

        if (!this.getWorld().isClient) {
            // 服务器端处理观察检测
            checkObservers();
            updateObservationState();
        }
    }

    private void checkObservers() {
        // 清空当前观察状态
        observerTimers.keySet().removeIf(uuid -> {
            PlayerEntity player = getWorld().getPlayerByUuid(uuid);
            return player == null || !isBeingObservedBy(player);
        });

        // 检查所有玩家
        for (PlayerEntity player : getWorld().getPlayers()) {
            if (isBeingObservedBy(player)) {
                UUID playerId = player.getUuid();
                int currentTime = observerTimers.getOrDefault(playerId, 0);
                observerTimers.put(playerId, currentTime + 1);
            }
        }

        // 更新观察时间数据（用于客户端同步）
        int maxObservedTime = observerTimers.values().stream().max(Integer::compare).orElse(0);
        this.getDataTracker().set(OBSERVED_TIME, maxObservedTime);
    }

    private boolean isBeingObservedBy(PlayerEntity player) {
        // 检查玩家是否正在看着这个实体
        if (player.squaredDistanceTo(this) > 256.0D) { // 16格距离限制
            return false;
        }

        // 使用射线检测确定玩家是否真的能看到这个实体
        Vec3d playerEyePos = player.getEyePos();
        Vec3d entityPos = this.getPos();
        Vec3d direction = entityPos.subtract(playerEyePos).normalize();

        // 简单的视线检测（可以优化为精确的视线检测）
        double dotProduct = player.getRotationVec(1.0F).dotProduct(direction);
        return dotProduct > 0.95D; // 玩家视线方向与实体方向基本一致
    }

    private void updateObservationState() {
        // 检查是否有玩家观察时间达到20ticks（1秒）
        for (Integer observedTime : observerTimers.values()) {
            if (observedTime >= 20) {
                onFullyObserved();
                break;
            }
        }
    }


    public float getObservedProgress() {
        // 返回被观察的进度（0.0-1.0），可用于渲染效果
        int observedTime = this.getDataTracker().get(OBSERVED_TIME);
        return Math.min(1.0F, observedTime / 20.0F);
    }

    // 其他方法和渲染相关代码...
}
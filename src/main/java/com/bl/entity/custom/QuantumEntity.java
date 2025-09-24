package com.bl.entity.custom;

import com.bl.BL;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;

public abstract class QuantumEntity extends Entity {
    private static final TrackedData<Integer> OBSERVED_TIME = DataTracker.registerData(QuantumEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private final Map<UUID, Integer> observerTimers = new HashMap<>();

    public QuantumEntity(EntityType<?> type, World world) {
        super(type, world);
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

    protected abstract void onFullyObserved();

    public float getObservedProgress() {
        // 返回被观察的进度（0.0-1.0），可用于渲染效果
        int observedTime = this.getDataTracker().get(OBSERVED_TIME);
        return Math.min(1.0F, observedTime / 20.0F);
    }
}

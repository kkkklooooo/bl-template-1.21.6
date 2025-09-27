package com.bl.mixin;// package com.bl.mixin;

import com.bl.BL;
import com.bl.entity.client.FallingBlockEntityMixinAccessor;
import com.bl.entity.custom.ExpandingSphereEntity;
import com.mojang.datafixers.types.templates.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.logging.Logger;

@Mixin(FallingBlockEntity.class)
public abstract class FallingBlockEntityMixin extends Entity implements FallingBlockEntityMixinAccessor {

	@Unique
	private boolean bl$isQuantum = false;

	@Unique
	private int bl$viewTime = 0;


	public FallingBlockEntityMixin(EntityType<?> type, World world) {
		super(type, world);
	}

	@Inject(method = "<init>(Lnet/minecraft/entity/EntityType;Lnet/minecraft/world/World;)V", at = @At("RETURN"))
	private void init(EntityType<?> type, World world, CallbackInfo ci) {
		// 初始化量子标记为false
		//this.bl$isQuantum = false;
		//BL.LOGGER.info("fuuuuuuuuuuck");
	}

	@Inject(method = "tick", at = @At("HEAD"))
	private void onTick(CallbackInfo ci) {
		if (!this.bl$isQuantum) return;

		World world = this.getWorld();

		// 生成量子粒子效果
		if (world instanceof ServerWorld serverWorld) {
			spawnQuantumParticles(serverWorld);
		}

		// 检查是否落地
		if (this.isOnGround()) {
			BL.LOGGER.info("groundfuck");
			this.discard();
			return;

		}

		// 检查玩家观看
		checkPlayerViewing();
	}

	@Unique
	private void spawnQuantumParticles(ServerWorld world) {
		Vec3d pos = this.getPos();
		// 生成量子效果的粒子
		for (int i = 0; i < 2; i++) {
			double offsetX = (this.random.nextDouble() - 0.5) * 0.5;
			double offsetY = (this.random.nextDouble() - 0.5) * 0.5;
			double offsetZ = (this.random.nextDouble() - 0.5) * 0.5;

			world.spawnParticles(ParticleTypes.ELECTRIC_SPARK,
					pos.x + offsetX, pos.y + 0.5 + offsetY, pos.z + offsetZ,
					1, 0, 0, 0, 0.1);
			world.spawnParticles(ParticleTypes.END_ROD,
					pos.x + offsetX, pos.y + 0.5 + offsetY, pos.z + offsetZ,
					1, 0, 0, 0, 0.05);
		}
	}

	@Unique
	private void checkPlayerViewing() {
		// 获取周围10格内的玩家
		Box box = new Box(this.getBlockPos()).expand(10);
		for (PlayerEntity player : this.getWorld().getNonSpectatingEntities(PlayerEntity.class, box)) {
			if (isPlayerLookingAt(player)) {
				bl$viewTime++;
				// 被观看2秒后消失
				if (bl$viewTime >= 10) {
					BL.LOGGER.info("lookfuck");
					this.discard();
					return;
				}
				break; // 只要有一个玩家在看就计数
			}
		}
	}

	@Unique
	private boolean isPlayerLookingAt(PlayerEntity player) {
		// 计算玩家视线方向与到实体方向的夹角
		Vec3d playerLookVec = player.getRotationVec(1.0F).normalize();
		Vec3d playerToEntity = this.getPos().subtract(player.getEyePos()).normalize();

		double dotProduct = playerLookVec.dotProduct(playerToEntity);

		// 如果夹角很小（cos值接近1），说明玩家正在看这个实体
		return dotProduct > 0; // 调整这个值来改变灵敏度
	}

	// 添加设置量子状态的方法
	@Unique
	public void bl$setQuantum() {
		this.bl$isQuantum = true;
		this.bl$viewTime = 0;
		this.setGlowing(true);
	}

	// 添加获取量子状态的方法
	@Unique
	public boolean bl$isQuantum() {
		return this.bl$isQuantum;
	}
}
package com.bl.mixin;// package com.bl.mixin;

import com.bl.entity.client.FallingBlockEntityMixinAccessor;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.World;
import org.apache.logging.log4j.core.jmx.Server;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;

import java.util.ArrayList;
import java.util.List;

@Mixin(FallingBlockEntity.class)
public abstract class FallingBlockEntityMixin extends Entity implements FallingBlockEntityMixinAccessor {
	@Unique
	boolean isq=false;
	private static List<BlockPos> getAllOnGroundBlocks(Entity entity) {
		List<BlockPos> groundBlocks = new ArrayList<>();
		World world = entity.getWorld();
		Box entityBoundingBox = entity.getBoundingBox();

		// 创建更精确的检测区域（仅实体底部）
		Box groundCheckBox = new Box(
				entityBoundingBox.minX-0.05, entityBoundingBox.minY-0.05, entityBoundingBox.minZ-0.05,
				entityBoundingBox.maxX+0.05, entityBoundingBox.maxY+0.05, entityBoundingBox.maxZ+0.05
		);

		// 遍历检测区域内的方块
		for (BlockPos pos : BlockPos.iterate(
				MathHelper.floor(groundCheckBox.minX),
				MathHelper.floor(groundCheckBox.minY),
				MathHelper.floor(groundCheckBox.minZ),
				MathHelper.floor(groundCheckBox.maxX),
				MathHelper.floor(groundCheckBox.maxY),
				MathHelper.floor(groundCheckBox.maxZ)
		)) {
			BlockState blockState = world.getBlockState(pos);

			// 快速跳过空气和没有碰撞的形状
			if (blockState.isAir() || blockState.getCollisionShape(world, pos).isEmpty()) {
				continue;
			}

			// 精确碰撞检测
			if (checkBlockCollision(blockState, world, pos, groundCheckBox)) {
				groundBlocks.add(pos.toImmutable());
			}
		}

		return groundBlocks;
	}

	private static boolean checkBlockCollision(BlockState blockState, World world, BlockPos pos, Box checkBox) {
		VoxelShape collisionShape = blockState.getCollisionShape(world, pos);

		// 使用VoxelShape的简单相交检测（性能更好）
		return VoxelShapes.matchesAnywhere(collisionShape.offset(pos.getX(), pos.getY(), pos.getZ()),
				VoxelShapes.cuboid(checkBox),
				BooleanBiFunction.AND);
	}


	public FallingBlockEntityMixin(EntityType<?> type, World world) {
		super(type, world);
	}
	public boolean isOnGround()
	{
		if(!this.isq)
		{
			return super.isOnGround();
		}
		if(super.isOnGround())
		{
			PlayerEntity player=this.getWorld().getClosestPlayer(this,100);
			if(player!=null)
			{
				ItemStack stack =player.getOffHandStack();
				if(stack==ItemStack.EMPTY)
				{
					return super.isOnGround();
				}
				List<BlockPos> poses=getAllOnGroundBlocks(this);
				if(poses.isEmpty())
				{
					return super.isOnGround();
				}


				stack.useOnBlock(new ItemUsageContext(player.getWorld(),player,Hand.OFF_HAND,stack,new BlockHitResult(this.getPos(), Direction.UP,poses.getFirst(),false)));
			}
			this.discard();
		}
		return false;
	}
	@Unique
	public void bl$setQuantum() {
		this.setGlowing(true);
		this.isq=true;
	}


}
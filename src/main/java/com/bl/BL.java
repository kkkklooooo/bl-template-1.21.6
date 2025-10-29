package com.bl;

import com.bl.entity.ModEntities;
import com.bl.entity.client.TerrainTransformationTask;
import com.bl.entity.custom.ExpandingSphereEntity;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.WindChargeEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Heightmap;
import net.minecraft.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;

import static com.bl.entity.ModEntities.EXPANDING_SPHERE;


public class BL implements ModInitializer {
	public static int a=0;
	public static final String MOD_ID = "bl";
	 public static void createExpandingSphere(double max, Vec3d pos, ServerWorld world) {
		// 假设ExpandingSphereEntity已经定义
		ExpandingSphereEntity sphere = new ExpandingSphereEntity(EXPANDING_SPHERE, world);
		sphere.SetMax(max);
		sphere.setPosition(pos);
		world.spawnEntity(sphere);
	}

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		/*Registry.register(Registries.SOUND_EVENT, Identifier.of(MOD_ID, "ppp"),
				SoundEvent.of(Identifier.of(MOD_ID, "ppp")));*/
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

	/*
		FabricDefaultAttributeRegistry.register(
				entities.blentity, BLEntity.createMobAttributes().build()
		);*/
		//Registry.register(Registries.ENTITY_TYPE, Identifier.of(MOD_ID,"blentity"), blentity);
		LOGGER.info("Terrain Transformation Mod Initialized!");

		UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
			if (world.isClient()) return ActionResult.PASS;

			ItemStack itemStack = player.getStackInHand(hand);
			BlockPos pos = hitResult.getBlockPos();

			if (world.getBlockState(pos).getBlock() == net.minecraft.block.Blocks.BEACON &&
					itemStack.getItem() == Items.NETHER_STAR) {

				// 消耗下界之星
				if (!player.isCreative()) {
					itemStack.decrement(1);
				}

				startTerrainTransformation((ServerWorld) world, pos, player);
				return ActionResult.SUCCESS;
			}

			return ActionResult.PASS;
		});
		ModEntities.register();
		//FabricDefaultAttributeRegistry.register(EXPANDING_SPHERE, ExpandingSphereEntity.createAttributes());
		LOGGER.info("Hello Fabric world!");
	}
	private void startTerrainTransformation(ServerWorld world, BlockPos beaconPos, net.minecraft.entity.player.PlayerEntity player) {
		// 寻找有良好地表结构的参考位置
		BlockPos referencePos = findSuitableReferencePosition(world, beaconPos);

		if (referencePos != null) {
			TerrainTransformationTask task = new TerrainTransformationTask(world, beaconPos, referencePos, player);
			task.start();
			player.sendMessage(net.minecraft.text.Text.literal("§a地形改造已启动！将先清除区域再复制远处的地表结构。"), false);
		} else {
			player.sendMessage(net.minecraft.text.Text.literal("§c未找到合适的参考地形！请尝试在其他位置使用。"), false);
		}
	}

	private BlockPos findSuitableReferencePosition(ServerWorld world, BlockPos center) {
		int minDistance = 10000;
		int maxAttempts = 20;
		int forceLoadChunkRadius = 2;
		Set<ChunkPos> chunksToForceLoad = new HashSet<>();

		try {
			for (int i = 0; i < maxAttempts; i++) {
				double angle = world.random.nextDouble() * 2 * Math.PI;
				int distance = minDistance + world.random.nextInt(5000);

				int refX = center.getX() + (int)(Math.cos(angle) * distance);
				int refZ = center.getZ() + (int)(Math.sin(angle) * distance);

				ChunkPos chunkPos = new ChunkPos(refX >> 4, refZ >> 4);

				// 强制加载目标区块及其周边区块
				chunksToForceLoad.clear();
				for (int cx = chunkPos.x - forceLoadChunkRadius; cx <= chunkPos.x + forceLoadChunkRadius; cx++) {
					for (int cz = chunkPos.z - forceLoadChunkRadius; cz <= chunkPos.z + forceLoadChunkRadius; cz++) {
						ChunkPos currentPos = new ChunkPos(cx, cz);
						//if (world.getChunkManager().isOutsideBuildHeight(currentPos.getStartY())) continue;
						world.setChunkForced(currentPos.x, currentPos.z, true);
						chunksToForceLoad.add(currentPos);
					}
				}

				// 等待区块加载
				try {
					Thread.sleep(50);
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
					break;
				}

				BlockPos refPos = new BlockPos(refX, 64, refZ);
				if (world.isChunkLoaded(chunkPos.x, chunkPos.z)) {
					int surfaceY = world.getChunk(chunkPos.x, chunkPos.z)
							.getHeightmap(net.minecraft.world.Heightmap.Type.WORLD_SURFACE)
							.get(refX & 15, refZ & 15);

					// 确保地表高度有效且不是液体
					if (surfaceY > 64 && surfaceY < world.getHeight() - 10) {
						BlockPos surfacePos = new BlockPos(refX, surfaceY, refZ);
						net.minecraft.block.BlockState surfaceBlock = world.getBlockState(surfacePos);
						if (!(surfaceBlock.isOf(Blocks.WATER)||surfaceBlock.isOf(Blocks.LAVA))) {
							return surfacePos;
						}
					}
				}

				// 清理强制加载的区块
				for (ChunkPos pos : chunksToForceLoad) {
					world.setChunkForced(pos.x, pos.z, false);
				}
			}
		} catch (Exception e) {
			// 发生异常时，清理所有已强制加载的区块
			for (ChunkPos pos : chunksToForceLoad) {
				world.setChunkForced(pos.x, pos.z, false);
			}
		}
		return null;
	}
}
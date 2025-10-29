package com.bl;

import com.bl.entity.ModEntities;
import com.bl.entity.client.TerrainTransformationTask;
import com.bl.entity.custom.ExpandingSphereEntity;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Heightmap;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import static com.bl.entity.ModEntities.EXPANDING_SPHERE;

public class BL implements ModInitializer {
	public static int a=0;
	public static final String MOD_ID = "bl";

	// 定义方块到生物群系的映射（只包含主世界地表群系）
	private static final BlockToBiomeMapping[] BLOCK_TO_BIOME_MAPPINGS = {
			new BlockToBiomeMapping(Blocks.GRASS_BLOCK, BiomeKeys.PLAINS, "平原"),
			new BlockToBiomeMapping(Blocks.JUNGLE_LOG, BiomeKeys.JUNGLE, "丛林"),
			new BlockToBiomeMapping(Blocks.SAND, BiomeKeys.DESERT, "沙漠"),
			new BlockToBiomeMapping(Blocks.SNOW_BLOCK, BiomeKeys.SNOWY_PLAINS, "雪原"),
			new BlockToBiomeMapping(Blocks.DARK_OAK_LOG, BiomeKeys.DARK_FOREST, "黑森林"),
			new BlockToBiomeMapping(Blocks.OAK_LOG, BiomeKeys.FOREST, "森林"),
			new BlockToBiomeMapping(Blocks.AMETHYST_BLOCK, BiomeKeys.FLOWER_FOREST, "繁花森林"),
			new BlockToBiomeMapping(Blocks.HAY_BLOCK, BiomeKeys.SUNFLOWER_PLAINS, "向日葵平原"),
			new BlockToBiomeMapping(Blocks.MOSS_BLOCK, BiomeKeys.SWAMP, "沼泽"),
			new BlockToBiomeMapping(Blocks.PODZOL, BiomeKeys.OLD_GROWTH_PINE_TAIGA, "原始松木针叶林"),
			new BlockToBiomeMapping(Blocks.MUD, BiomeKeys.MANGROVE_SWAMP, "红树林沼泽"),
			new BlockToBiomeMapping(Blocks.SANDSTONE, BiomeKeys.BADLANDS, "恶地"),
			new BlockToBiomeMapping(Blocks.RED_SANDSTONE, BiomeKeys.ERODED_BADLANDS, "被风蚀的恶地"),
			new BlockToBiomeMapping(Blocks.ICE, BiomeKeys.ICE_SPIKES, "冰刺之地"),
			new BlockToBiomeMapping(Blocks.PACKED_ICE, BiomeKeys.FROZEN_PEAKS, "冰封山峰"),
			new BlockToBiomeMapping(Blocks.BIRCH_LOG, BiomeKeys.BIRCH_FOREST, "桦木森林"),
			new BlockToBiomeMapping(Blocks.SPRUCE_LOG, BiomeKeys.TAIGA, "针叶林"),
			new BlockToBiomeMapping(Blocks.ACACIA_LOG, BiomeKeys.SAVANNA, "热带草原"),
			new BlockToBiomeMapping(Blocks.CHERRY_LOG, BiomeKeys.CHERRY_GROVE, "樱花树林")
	};

	// 定义方块到结构的映射
	private static final BlockToStructureMapping[] BLOCK_TO_STRUCTURE_MAPPINGS = {
			new BlockToStructureMapping(Blocks.TARGET, "village", "村庄"),
			new BlockToStructureMapping(Blocks.COBBLESTONE, "pillager_outpost", "掠夺者前哨站"),
			new BlockToStructureMapping(Blocks.MOSSY_COBBLESTONE, "jungle_pyramid", "丛林神庙"),
			new BlockToStructureMapping(Blocks.SMOOTH_SANDSTONE, "desert_pyramid", "沙漠神殿"),
			new BlockToStructureMapping(Blocks.BOOKSHELF, "mansion", "林地府邸"),
	};

	public static void createExpandingSphere(double max, Vec3d pos, ServerWorld world) {
		ExpandingSphereEntity sphere = new ExpandingSphereEntity(EXPANDING_SPHERE, world);
		sphere.SetMax(max);
		sphere.setPosition(pos);
		world.spawnEntity(sphere);
	}

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		LOGGER.info("Terrain Transformation Mod Initialized!");

		UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
			if (world.isClient()) {
				return ActionResult.PASS;
			}

			ItemStack itemStack = player.getStackInHand(hand);
			BlockPos pos = hitResult.getBlockPos();

			if (world.getBlockState(pos).getBlock() == net.minecraft.block.Blocks.BEACON &&
					itemStack.getItem() == Items.NETHER_STAR) {

				if (!player.isCreative()) {
					itemStack.decrement(1);
				}
				BL.LOGGER.warn("激活地形改造");

				world.getServer().execute(()->{
					startTerrainTransformation((ServerWorld) world, pos, player);
				});

				return ActionResult.SUCCESS;
			}

			return ActionResult.PASS;
		});
		ModEntities.register();
		LOGGER.info("Hello Fabric world!");
	}

	private void startTerrainTransformation(ServerWorld world, BlockPos beaconPos, net.minecraft.entity.player.PlayerEntity player) {
		// 检查信标东侧 (x+1, y, z) 的方块
		RegistryKey<Biome> targetBiome = checkBeaconSideBlock(world, beaconPos, player);
		String targetStructure = checkBeaconSideBlockForStructure(world, beaconPos, player);

		BlockPos referencePos;

		if (targetStructure != null) {
			// 使用结构定位
			referencePos = findStructureReferencePosition(world, beaconPos, targetStructure, player);
		} else if (targetBiome != null) {
			// 使用生物群系定位
			referencePos = findBiomeReferencePosition(world, beaconPos, targetBiome, player);
		} else {
			// 使用原有的随机查找方法
			referencePos = findSuitableReferencePosition(world, beaconPos);
		}

		if (referencePos != null) {
			TerrainTransformationTask task = new TerrainTransformationTask(world, beaconPos, referencePos, player);
			task.start();
			player.sendMessage(net.minecraft.text.Text.literal("§a地形改造已启动！将先清除区域再复制远处的地表结构。"), false);
		} else {
			if (targetBiome != null) {
				player.sendMessage(net.minecraft.text.Text.literal("§c未找到指定的生物群系！请尝试在其他位置使用。"), false);
			} else if (targetStructure != null) {
				player.sendMessage(net.minecraft.text.Text.literal("§c未找到指定的结构！请尝试在其他位置使用。"), false);
			} else {
				player.sendMessage(net.minecraft.text.Text.literal("§c未找到合适的参考地形！请尝试在其他位置使用。"), false);
			}
		}
	}

	/**
	 * 检查信标东侧 (x+1, y, z) 的方块并返回对应的生物群系
	 */
	private RegistryKey<Biome> checkBeaconSideBlock(ServerWorld world, BlockPos beaconPos, net.minecraft.entity.player.PlayerEntity player) {
		// 检查信标东侧 (x+1, y, z) 的方块
		BlockPos sidePos = beaconPos.east(); // east() 方法就是 (x+1, y, z)
		Block sideBlock = world.getBlockState(sidePos).getBlock();

		// 查找对应的生物群系
		for (BlockToBiomeMapping mapping : BLOCK_TO_BIOME_MAPPINGS) {
			if (mapping.block == sideBlock) {
				player.sendMessage(Text.literal("§6检测到东侧方块: " + sideBlock.getName().getString() + "，将寻找" + mapping.biomeName + "生物群系"), false);
				return mapping.biome;
			}
		}

		return null;
	}

	/**
	 * 检查信标东侧 (x+1, y, z) 的方块并返回对应的结构
	 */
	private String checkBeaconSideBlockForStructure(ServerWorld world, BlockPos beaconPos, net.minecraft.entity.player.PlayerEntity player) {
		// 检查信标东侧 (x+1, y, z) 的方块
		BlockPos sidePos = beaconPos.east();
		Block sideBlock = world.getBlockState(sidePos).getBlock();

		// 查找对应的结构
		for (BlockToStructureMapping mapping : BLOCK_TO_STRUCTURE_MAPPINGS) {
			if (mapping.block == sideBlock) {
				player.sendMessage(Text.literal("§6检测到东侧方块: " + sideBlock.getName().getString() + "，将寻找" + mapping.structureName + "结构"), false);
				return mapping.structureId;
			}
		}

		return null;
	}

	/**
	 * 查找特定生物群系的参考位置
	 */
	private BlockPos findBiomeReferencePosition(ServerWorld world, BlockPos center, RegistryKey<Biome> targetBiome, net.minecraft.entity.player.PlayerEntity player) {
		try {
			// 使用世界的locateBiome方法查找最近的生物群系
			BlockPos biomePos = Objects.requireNonNull(world.locateBiome(
					b -> b.matchesKey(targetBiome),
					center,
					10000, // 搜索半径增加到10000格
					8,    // 区块检查步长
					64    // 分组大小
			)).getFirst();

			if (biomePos != null) {
				// 获取该位置的地表高度
				int surfaceY = world.getTopY(Heightmap.Type.WORLD_SURFACE, biomePos.getX(), biomePos.getZ());
				BlockPos surfacePos = new BlockPos(biomePos.getX(), surfaceY, biomePos.getZ());

				// 验证地表方块是否合适
				BlockState surfaceBlock = world.getBlockState(surfacePos);
				if (!surfaceBlock.isOf(Blocks.WATER) && !surfaceBlock.isOf(Blocks.LAVA)) {
					double distance = Math.sqrt(center.getSquaredDistance(surfacePos));
					player.sendMessage(Text.literal("§a成功找到目标生物群系，距离: " + String.format("%.1f", distance) + " 格"), false);
					return surfacePos;
				} else {
					// 如果地表是液体，尝试在周围寻找合适的区块
					return findAlternativePositionInBiome(world, biomePos, targetBiome, player);
				}
			}
		} catch (Exception e) {
			LOGGER.error("查找生物群系时发生错误", e);
			player.sendMessage(Text.literal("§c查找生物群系时发生错误: " + e.getMessage()), false);
		}

		return null;
	}

	/**
	 * 查找特定结构的参考位置
	 */
	private BlockPos findStructureReferencePosition(ServerWorld world, BlockPos center, String structureId, net.minecraft.entity.player.PlayerEntity player) {
		try {
			// 使用世界的locateStructure方法查找最近的结构
			BlockPos structurePos = world.locateStructure(
					TagKey.of(net.minecraft.registry.RegistryKeys.STRUCTURE, net.minecraft.util.Identifier.of(structureId)),
					center,
					10000, // 搜索半径
					false   // 是否只搜索已探索区域
			);

			if (structurePos != null) {
				// 获取该位置的地表高度
				int surfaceY = world.getTopY(Heightmap.Type.WORLD_SURFACE, structurePos.getX(), structurePos.getZ());
				BlockPos surfacePos = new BlockPos(structurePos.getX(), surfaceY, structurePos.getZ());

				double distance = Math.sqrt(center.getSquaredDistance(surfacePos));
				player.sendMessage(Text.literal("§a成功找到目标结构，距离: " + String.format("%.1f", distance) + " 格"), false);

				// 对于结构，我们返回结构本身的位置，而不是地表位置
				// 这样可以确保复制整个结构区域
				return structurePos;
			}
		} catch (Exception e) {
			LOGGER.error("查找结构时发生错误", e);
			player.sendMessage(Text.literal("§c查找结构时发生错误: " + e.getMessage()), false);
		}

		return null;
	}

	/**
	 * 在目标生物群系内寻找替代位置（避免液体表面）
	 */
	private BlockPos findAlternativePositionInBiome(ServerWorld world, BlockPos center, RegistryKey<Biome> targetBiome, net.minecraft.entity.player.PlayerEntity player) {
		int attempts = 10;
		int range = 100; // 在中心点周围100格范围内寻找

		for (int i = 0; i < attempts; i++) {
			int offsetX = world.random.nextInt(range * 2) - range;
			int offsetZ = world.random.nextInt(range * 2) - range;

			BlockPos testPos = center.add(offsetX, 0, offsetZ);

			// 验证该位置是否仍在目标生物群系内
			RegistryEntry<Biome> biomeAtPos = world.getBiome(testPos);
			if (biomeAtPos.matchesKey(targetBiome)) {
				int surfaceY = world.getTopY(Heightmap.Type.WORLD_SURFACE, testPos.getX(), testPos.getZ());
				BlockPos surfacePos = new BlockPos(testPos.getX(), surfaceY, testPos.getZ());

				BlockState surfaceBlock = world.getBlockState(surfacePos);
				if (!surfaceBlock.isOf(Blocks.WATER) && !surfaceBlock.isOf(Blocks.LAVA)) {
					return surfacePos;
				}
			}
		}

		return null;
	}

	/**
	 * 原有的随机查找方法（作为备选）
	 */
	private BlockPos findSuitableReferencePosition(ServerWorld world, BlockPos center) {
		int minDistance = 2000;
		int maxAttempts = 20;
		int forceLoadChunkRadius = 2;
		Set<ChunkPos> chunksToForceLoad = new HashSet<>();

		try {
			for (int i = 0; i < maxAttempts; i++) {
				double angle = world.random.nextDouble() * 2 * Math.PI;
				int distance = minDistance + world.random.nextInt(500);

				int refX = center.getX() + (int)(Math.cos(angle) * distance);
				int refZ = center.getZ() + (int)(Math.sin(angle) * distance);

				ChunkPos chunkPos = new ChunkPos(refX >> 4, refZ >> 4);

				// 强制加载目标区块及其周边区块
				chunksToForceLoad.clear();
				for (int cx = chunkPos.x - forceLoadChunkRadius; cx <= chunkPos.x + forceLoadChunkRadius; cx++) {
					for (int cz = chunkPos.z - forceLoadChunkRadius; cz <= chunkPos.z + forceLoadChunkRadius; cz++) {
						ChunkPos currentPos = new ChunkPos(cx, cz);
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

				if (world.isChunkLoaded(chunkPos.x, chunkPos.z)) {
					int surfaceY = world.getChunk(chunkPos.x, chunkPos.z)
							.getHeightmap(Heightmap.Type.WORLD_SURFACE)
							.get(refX & 15, refZ & 15);

					if (surfaceY > 64 && surfaceY < world.getHeight() - 10) {
						BlockPos surfacePos = new BlockPos(refX, surfaceY, refZ);
						BlockState surfaceBlock = world.getBlockState(surfacePos);
						if (!surfaceBlock.isOf(Blocks.WATER) && !surfaceBlock.isOf(Blocks.LAVA)) {
							// 清理强制加载的区块
							for (ChunkPos pos : chunksToForceLoad) {
								world.setChunkForced(pos.x, pos.z, false);
							}
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

	/**
	 * 方块到生物群系的映射类
	 */
	private record BlockToBiomeMapping(Block block, RegistryKey<Biome> biome, String biomeName) {
	}

	/**
	 * 方块到结构的映射类
	 */
		private record BlockToStructureMapping(Block block, String structureId, String structureName) {
	}
}
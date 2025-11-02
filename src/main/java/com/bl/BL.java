package com.bl;

import com.bl.entity.client.TerrainTransformationTask;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class BL implements ModInitializer {
	public static final String MOD_ID = "bl";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	private static final BlockToBiomeMapping[] BLOCK_TO_BIOME_MAPPINGS = {
			new BlockToBiomeMapping(Blocks.GRASS_BLOCK, BiomeKeys.PLAINS, "平原"),
			new BlockToBiomeMapping(Blocks.JUNGLE_LOG, BiomeKeys.JUNGLE, "丛林"),
			new BlockToBiomeMapping(Blocks.SAND, BiomeKeys.DESERT, "沙漠"),
			new BlockToBiomeMapping(Blocks.SNOW_BLOCK, BiomeKeys.SNOWY_PLAINS, "雪原"),
			new BlockToBiomeMapping(Blocks.DARK_OAK_LOG, BiomeKeys.DARK_FOREST, "黑森林"),
			new BlockToBiomeMapping(Blocks.MYCELIUM, BiomeKeys.MUSHROOM_FIELDS, "蘑菇岛"),
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

	private static final BlockToStructureMapping[] BLOCK_TO_STRUCTURE_MAPPINGS = {
			new BlockToStructureMapping(Blocks.TARGET, "village", "村庄"),
			new BlockToStructureMapping(Blocks.COBBLESTONE, "pillager_outpost", "掠夺者前哨站"),
			new BlockToStructureMapping(Blocks.MOSSY_COBBLESTONE, "jungle_pyramid", "丛林神庙"),
			new BlockToStructureMapping(Blocks.SMOOTH_SANDSTONE, "desert_pyramid", "沙漠神殿"),
			new BlockToStructureMapping(Blocks.BOOKSHELF, "mansion", "林地府邸"),
	};

	@Override
	public void onInitialize() {
		LOGGER.info("Terrain Transformation Mod Initialized!");

		UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
			if (world.isClient()) return ActionResult.PASS;

			ItemStack itemStack = player.getStackInHand(hand);
			BlockPos pos = hitResult.getBlockPos();

			if (world.getBlockState(pos).getBlock() == Blocks.BEACON &&
					itemStack.getItem() == Items.NETHER_STAR) {

				if (!player.isCreative()) itemStack.decrement(1);

				LOGGER.info("激活地形改造");
				Objects.requireNonNull(world.getServer()).execute(() -> {
					startTerrainTransformation((ServerWorld) world, pos, player);
				});

				return ActionResult.SUCCESS;
			}

			return ActionResult.PASS;
		});
	}

	private void startTerrainTransformation(ServerWorld world, BlockPos beaconPos, net.minecraft.entity.player.PlayerEntity player) {
		LOGGER.info("开始地形改造过程 - 信标位置: {}", beaconPos);

		RegistryKey<Biome> targetBiome = detectTargetBiome(world, beaconPos, player);
		String targetStructure = detectTargetStructure(world, beaconPos, player);

		BlockPos referencePos = findReferencePosition(world, beaconPos, targetBiome, targetStructure, player);

		if (referencePos != null) {
			new TerrainTransformationTask(world, beaconPos, referencePos, player).start();
			player.sendMessage(Text.literal("§a地形改造已启动！将先清除区域再复制远处的地表结构。"), false);
			LOGGER.info("地形改造任务已启动 - 参考位置: {}", referencePos);
		} else {
			sendErrorMessage(player, targetBiome, targetStructure);
		}
	}

	private RegistryKey<Biome> detectTargetBiome(ServerWorld world, BlockPos beaconPos, net.minecraft.entity.player.PlayerEntity player) {
		BlockPos sidePos = beaconPos.east();
		Block sideBlock = world.getBlockState(sidePos).getBlock();

		for (BlockToBiomeMapping mapping : BLOCK_TO_BIOME_MAPPINGS) {
			if (mapping.block == sideBlock) {
				player.sendMessage(Text.literal("§6检测到东侧方块: " + sideBlock.getName().getString() + "，将寻找" + mapping.biomeName + "生物群系"), false);
				return mapping.biome;
			}
		}
		return null;
	}

	private String detectTargetStructure(ServerWorld world, BlockPos beaconPos, net.minecraft.entity.player.PlayerEntity player) {
		BlockPos sidePos = beaconPos.east();
		Block sideBlock = world.getBlockState(sidePos).getBlock();

		for (BlockToStructureMapping mapping : BLOCK_TO_STRUCTURE_MAPPINGS) {
			if (mapping.block == sideBlock) {
				player.sendMessage(Text.literal("§6检测到东侧方块: " + sideBlock.getName().getString() + "，将寻找" + mapping.structureName + "结构"), false);
				return mapping.structureId;
			}
		}
		return null;
	}

	private BlockPos findReferencePosition(ServerWorld world, BlockPos center, RegistryKey<Biome> targetBiome,
										   String targetStructure, net.minecraft.entity.player.PlayerEntity player) {
		if (targetStructure != null) {
			return findStructurePosition(world, center, targetStructure, player);
		} else if (targetBiome != null) {
			return findBiomePosition(world, center, targetBiome, player);
		} else {
			return findRandomPosition(world, center);
		}
	}

	private BlockPos findBiomePosition(ServerWorld world, BlockPos center, RegistryKey<Biome> targetBiome,
									   net.minecraft.entity.player.PlayerEntity player) {
		try {
			BlockPos biomePos = Objects.requireNonNull(world.locateBiome(
					b -> b.matchesKey(targetBiome),
					center, 10000, 8, 64
			)).getFirst();

			if (biomePos != null) {
				BlockPos surfacePos = getValidSurfacePosition(world, biomePos);
				if (surfacePos != null) {
					double distance = Math.sqrt(center.getSquaredDistance(surfacePos));
					player.sendMessage(Text.literal("§a成功找到目标生物群系，距离: " + String.format("%.1f", distance) + " 格"), false);
					return surfacePos;
				}
				return findAlternativeBiomePosition(world, biomePos, targetBiome);
			}
		} catch (Exception e) {
			LOGGER.error("查找生物群系时发生错误", e);
			player.sendMessage(Text.literal("§c查找生物群系时发生错误: " + e.getMessage()), false);
		}
		return null;
	}

	private BlockPos findStructurePosition(ServerWorld world, BlockPos center, String structureId,
										   net.minecraft.entity.player.PlayerEntity player) {
		try {
			BlockPos structurePos = world.locateStructure(
					net.minecraft.registry.tag.TagKey.of(net.minecraft.registry.RegistryKeys.STRUCTURE,
							net.minecraft.util.Identifier.of(structureId)),
					center, 10000, false
			);

			if (structurePos != null) {
				BlockPos surfacePos = getValidSurfacePosition(world, structurePos);
				if (surfacePos != null) {
					double distance = Math.sqrt(center.getSquaredDistance(surfacePos));
					player.sendMessage(Text.literal("§a成功找到目标结构，距离: " + String.format("%.1f", distance) + " 格"), false);
					return surfacePos;
				}
			}
		} catch (Exception e) {
			LOGGER.error("查找结构时发生错误", e);
			player.sendMessage(Text.literal("§c查找结构时发生错误: " + e.getMessage()), false);
		}
		return null;
	}

	private BlockPos findRandomPosition(ServerWorld world, BlockPos center) {
		LOGGER.info("开始随机查找参考位置 - 中心: {}", center);

		for (int i = 0; i < 20; i++) {
			double angle = world.random.nextDouble() * 2 * Math.PI;
			int distance = 2000 + world.random.nextInt(500);

			int refX = center.getX() + (int)(Math.cos(angle) * distance);
			int refZ = center.getZ() + (int)(Math.sin(angle) * distance);
			BlockPos testPos = new BlockPos(refX, 0, refZ);

			BlockPos surfacePos = getValidSurfacePosition(world, testPos);
			if (surfacePos != null) {
				LOGGER.info("成功找到随机位置: {}", surfacePos);
				return surfacePos;
			}
		}

		LOGGER.info("随机查找失败");
		return null;
	}

	private BlockPos getValidSurfacePosition(ServerWorld world, BlockPos pos) {
		// 只在必要时强制加载单个区块
		ChunkPos chunkPos = new ChunkPos(pos.getX() >> 4, pos.getZ() >> 4);

		if (!world.isChunkLoaded(chunkPos.x, chunkPos.z)) {
			world.setChunkForced(chunkPos.x, chunkPos.z, true);
			try {
				Thread.sleep(10); // 短暂等待区块加载
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				return null;
			}
		}

		try {
			int surfaceY = world.getTopY(Heightmap.Type.WORLD_SURFACE, pos.getX(), pos.getZ());

			if (surfaceY >= 64 && surfaceY < world.getHeight() - 10) {
				BlockPos surfacePos = new BlockPos(pos.getX(), surfaceY, pos.getZ());
				BlockState surfaceBlock = world.getBlockState(surfacePos);

				if (!surfaceBlock.isOf(Blocks.WATER) && !surfaceBlock.isOf(Blocks.LAVA)) {
					return surfacePos;
				}
			}
		} finally {
			// 取消强制加载
			world.setChunkForced(chunkPos.x, chunkPos.z, false);
		}

		return null;
	}

	private BlockPos findAlternativeBiomePosition(ServerWorld world, BlockPos center, RegistryKey<Biome> targetBiome) {
		for (int i = 0; i < 10; i++) {
			int offsetX = world.random.nextInt(400) - 200;
			int offsetZ = world.random.nextInt(400) - 200;
			BlockPos testPos = center.add(offsetX, 0, offsetZ);

			if (world.getBiome(testPos).matchesKey(targetBiome)) {
				BlockPos surfacePos = getValidSurfacePosition(world, testPos);
				if (surfacePos != null) return surfacePos;
			}
		}
		return null;
	}

	private void sendErrorMessage(net.minecraft.entity.player.PlayerEntity player,
								  RegistryKey<Biome> targetBiome, String targetStructure) {
		if (targetBiome != null) {
			player.sendMessage(Text.literal("§c未找到指定的生物群系！请尝试在其他位置使用。"), false);
		} else if (targetStructure != null) {
			player.sendMessage(Text.literal("§c未找到指定的结构！请尝试在其他位置使用。"), false);
		} else {
			player.sendMessage(Text.literal("§c未找到合适的参考地形！请尝试在其他位置使用。"), false);
		}
	}

	private record BlockToBiomeMapping(Block block, RegistryKey<Biome> biome, String biomeName) {}
	private record BlockToStructureMapping(Block block, String structureId, String structureName) {}
}
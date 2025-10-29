package com.bl.entity.client;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.Heightmap;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TerrainTransformationTask {
    private final ServerWorld world;
    private final BlockPos center;
    private final BlockPos referenceCenter;
    private final net.minecraft.entity.player.PlayerEntity player;
    private Set<ChunkPos> forcedChunks = new HashSet<>();

    private int currentRadius = 0;
    private final int maxRadius = 64;
    private boolean isActive = false;
    private boolean isinit=false;

    //private int tickInterval = 1;

    // 新增：控制改造速度的间隔变量，每 interval 次才改造圆环地形
    private final int interval = 1; // 例如设置为3，表示每3个半径才改造一次
    private int radiusCounter = 0; // 用于计数当前累计的半径数

    public TerrainTransformationTask(ServerWorld world, BlockPos center, BlockPos referenceCenter, net.minecraft.entity.player.PlayerEntity player) {
        this.world = world;
        this.center = center;
        this.referenceCenter = referenceCenter;
        this.player = player;
        RegisterToTick();
    }

    public void start() {
        //player.addVelocity(0,10,0);
        this.isActive = true;

        //scheduleNextTick();
    }

    public void stop() {
        this.isActive = false;
    }


    private void RegisterToTick(){

        ServerTickEvents.END_SERVER_TICK.register((MinecraftServer server)->{
            if(this.isActive){
                processNextStep();
                //这段代码精妙绝伦，活跃但是没有初始化则初始化，而不活跃了却初始化为true了说明没有进行结尾处理，结尾处理后改成false
                if(!isinit)
                {
                    int chunkRadius = (maxRadius + 15) >> 4; // 向上取整
                    for (int x = -chunkRadius; x <= chunkRadius; x++) {
                        for (int z = -chunkRadius; z <= chunkRadius; z++) {
                            ChunkPos chunkPos = new ChunkPos((referenceCenter.getX() >> 4) + x, (referenceCenter.getZ() >> 4) + z);
                            if (!forcedChunks.contains(chunkPos)) {
                                world.setChunkForced(chunkPos.x, chunkPos.z, true);
                                forcedChunks.add(chunkPos);
                            }
                        }
                    }
                    isinit=true;
                }
                } else if (isinit) {
                for (ChunkPos chunkPos : forcedChunks) {
                    world.setChunkForced(chunkPos.x, chunkPos.z, false);
                }
                forcedChunks.clear();
                isinit=false;
            }
        });
    }


    private void scheduleNextTick() {
        world.getServer().execute(() -> {
            if (isActive) {
                processNextStep();
            }
        });
    }

    private void processNextStep() {
        if (currentRadius > maxRadius) {
            player.sendMessage(net.minecraft.text.Text.literal("§6地形改造完成！"), false);
            stop();
            return;
        }

        // 新增：间隔控制逻辑
        radiusCounter++;
        if (radiusCounter < interval) {
            // 未达到间隔，只发送消息但不进行实际改造
            player.sendMessage(net.minecraft.text.Text.literal("§7准备改造半径: " + currentRadius + "格 (等待中 " + radiusCounter + "/" + interval + ")"), false);
            //currentRadius++;
            /*if (isActive) {
                //scheduleNextTick();
            }*/
            return;
        }

        // 达到间隔，重置计数器并进行实际改造
        radiusCounter = 0;

        // 生成当前半径的所有位置（完整的圆环区域）
        List<BlockPos> circlePositions = generateCirclePositions(currentRadius);

        // 处理当前圆环的所有位置
        for (BlockPos circlePos : circlePositions) {
            processPosition(circlePos);
        }

        player.sendMessage(net.minecraft.text.Text.literal("§e改造半径: " + currentRadius + "格"), false);

        currentRadius++;

        /*if (isActive) {
            //scheduleNextTick();
        }*/
    }

    /**
     * 生成圆形区域内的所有位置（不仅仅是圆环轮廓）
     */
    private List<BlockPos> generateCirclePositions(int radius) {
        List<BlockPos> positions = new ArrayList<>();

        if (radius == 0) {
            // 中心点
            positions.add(new BlockPos(center.getX(), 0, center.getZ()));
            return positions;
        }

        // 生成圆环内的所有点，不仅仅是轮廓
        int radiusSquared = radius * radius;

        for (int dx = -radius; dx <= radius; dx++) {
            for (int dz = -radius; dz <= radius; dz++) {
                // 检查是否在圆环内（当前半径的环状区域）
                int distanceSquared = dx * dx + dz * dz;
                if (distanceSquared <= radiusSquared && distanceSquared > (radius - 1) * (radius - 1)) {
                    int x = center.getX() + dx;
                    int z = center.getZ() + dz;
                    positions.add(new BlockPos(x, 0, z));
                }
            }
        }

        return positions;
    }

    /**
     * 处理单个位置：先破坏再重建
     */
    private void processPosition(BlockPos circlePos) {
        int targetX = circlePos.getX();
        int targetZ = circlePos.getZ();

        if (!world.isChunkLoaded(targetX >> 4, targetZ >> 4)) {
            return;
        }

        // 计算偏移量
        int offsetX = targetX - center.getX();
        int offsetZ = targetZ - center.getZ();
        int referenceX = referenceCenter.getX() + offsetX;
        int referenceZ = referenceCenter.getZ() + offsetZ;

        if (!world.isChunkLoaded(referenceX >> 4, referenceZ >> 4)) {
            return;
        }

        // 先破坏目标位置
        destroyAtPosition(targetX,targetZ);

        // 然后从参考位置复制到目标位置
        copyFromReference(targetX, targetZ, referenceX, referenceZ);
    }

    /**
     * 破坏目标位置的方块（Y > 60 的区域）
     */
    private void destroyAtPosition(int targetX, int targetZ) {
        // 获取目标位置的地表高度
        int surfaceY = world.getChunk(targetX >> 4, targetZ >> 4)
                .getHeightmap(Heightmap.Type.WORLD_SURFACE)
                .get(targetX & 15, targetZ & 15);

        if (surfaceY < 54) {
            return;
        }

        // 从Y=61开始到地表上方清除方块
        if(currentRadius<=3) {
            for (int y = 56; y <= surfaceY + 10; y++) {
                BlockPos targetPos = new BlockPos(targetX, y, targetZ);
                if (targetPos.getX() == center.getX() && targetPos.getY() == center.getY() && targetPos.getZ() == center.getZ()) {
                    continue;
                }
                if (targetPos.getY() == center.getY() - 1 && Math.abs(targetPos.getX() - center.getX()) <= 1 && Math.abs(targetPos.getZ() - center.getZ()) <= 1) {
                    continue;
                }
                BlockState currentState = world.getBlockState(targetPos);
                if (!currentState.isAir()) {
                    world.setBlockState(targetPos, Blocks.AIR.getDefaultState(), 3);
                }
            }
        }
        else
        {
            for (int y = 56; y <= surfaceY + 10; y++) {
                BlockPos targetPos = new BlockPos(targetX, y, targetZ);
                BlockState currentState = world.getBlockState(targetPos);
                if (!currentState.isAir()) {
                    world.setBlockState(targetPos, Blocks.AIR.getDefaultState(), 3);
                }
            }
        }
    }

    /**
     * 从参考位置复制到目标位置
     */
    private void copyFromReference(int targetX, int targetZ, int referenceX, int referenceZ) {
        // 获取参考位置的地表信息
        ReferenceTerrainInfo referenceInfo = getReferenceTerrainInfo(referenceX, referenceZ);
        if (referenceInfo == null) {
            return;
        }

        // 复制整个Y轴范围的方块
        copyTerrainStructure(targetX, targetZ, referenceInfo);
    }

    /**
     * 获取参考位置的完整地形信息
     */
    private ReferenceTerrainInfo getReferenceTerrainInfo(int referenceX, int referenceZ) {
        if (!world.isChunkLoaded(referenceX >> 4, referenceZ >> 4)) {
            return null;
        }

        // 获取参考位置的地表高度
        int referenceSurfaceY = world.getChunk(referenceX >> 4, referenceZ >> 4)
                .getHeightmap(Heightmap.Type.WORLD_SURFACE)
                .get(referenceX & 15, referenceZ & 15);

        if (referenceSurfaceY < 60) {
            return null;
        }

        return analyzeReferenceTerrain(referenceX, referenceZ, referenceSurfaceY);
    }

    /**
     * 分析参考地形的完整结构
     */
    private ReferenceTerrainInfo analyzeReferenceTerrain(int x, int z, int surfaceY) {
        ReferenceTerrainInfo info = new ReferenceTerrainInfo();
        info.surfaceY = surfaceY;

        // 记录从Y=61到地表的所有方块
        List<BlockState> blocks = new ArrayList<>();
        List<Integer> heights = new ArrayList<>();

        for (int y = 56; y <= surfaceY + 10; y++) {
            BlockPos pos = new BlockPos(x, y, z);
            BlockState state = world.getBlockState(pos);

            if (!state.isAir() || y <= surfaceY) { // 包括地表以下的非空气方块
                blocks.add(state);
                heights.add(y);
            }
        }

        info.blocks = blocks.toArray(new BlockState[0]);
        info.heights = heights.stream().mapToInt(Integer::intValue).toArray();

        // 记录地表以上的方块（如树木、草等）
        BlockPos abovePos = new BlockPos(x, surfaceY + 1, z);
        BlockState aboveState = world.getBlockState(abovePos);
        if (!aboveState.isAir()) {
            info.aboveSurfaceBlocks = new BlockState[]{aboveState};
            info.aboveSurfaceHeights = new int[]{surfaceY + 1};
        }

        return info;
    }

    /**
     * 复制地形结构到目标位置
     */
    private void copyTerrainStructure(int targetX, int targetZ, ReferenceTerrainInfo reference) {
        // 复制主要地形方块
        if (reference.blocks != null && reference.heights != null) {
            if(currentRadius<=3) {


                for (int i = 0; i < reference.blocks.length; i++) {
                    int targetY = reference.heights[i] + center.getY() - this.referenceCenter.getY();
                    //targetY = this.center.getY();
                    BlockPos targetPos = new BlockPos(targetX, targetY, targetZ);
                    BlockState referenceState = reference.blocks[i];
                    if (targetPos.getX() == center.getX() && targetPos.getY() == center.getY() && targetPos.getZ() == center.getZ()) {
                        continue;
                    }
                    if (targetPos.getY() == center.getY() - 1 && Math.abs(targetPos.getX() - center.getX()) <= 1 && Math.abs(targetPos.getZ() - center.getZ()) <= 1) {
                        continue;
                    }
                    // 只设置非空气方块
                    if (!referenceState.isAir()) {
                        BlockState currentState = world.getBlockState(targetPos);
                        if (!currentState.equals(referenceState)) {
                            world.setBlockState(targetPos, referenceState, 3);
                        }
                    }
                }
            }
            else {
                for (int i = 0; i < reference.blocks.length; i++) {
                    int targetY = reference.heights[i] + center.getY() - this.referenceCenter.getY();
                    //targetY = this.center.getY();
                    BlockPos targetPos = new BlockPos(targetX, targetY, targetZ);
                    BlockState referenceState = reference.blocks[i];
                    // 只设置非空气方块
                    if (!referenceState.isAir()) {
                        BlockState currentState = world.getBlockState(targetPos);
                        if (!currentState.equals(referenceState)) {
                            world.setBlockState(targetPos, referenceState, 3);
                        }
                    }
                }
            }
        }

        // 复制地表以上的装饰方块
        if (reference.aboveSurfaceBlocks != null && reference.aboveSurfaceHeights != null) {
            for (int i = 0; i < reference.aboveSurfaceBlocks.length; i++) {
                int targetY = reference.heights[i]+ center.getY()-this.referenceCenter.getY();
                //targetY = this.center.getY();
                BlockPos targetPos = new BlockPos(targetX, targetY, targetZ);
                BlockState referenceState = reference.aboveSurfaceBlocks[i];

                BlockState currentState = world.getBlockState(targetPos);
                if (currentState.isAir() || currentState.isReplaceable()) {
                    world.setBlockState(targetPos, referenceState, 3);
                }
            }
        }
    }

    /**
     * 参考地形信息类
     */
    private static class ReferenceTerrainInfo {
        int surfaceY;
        BlockState[] blocks;           // 主要地形方块
        int[] heights;                 // 对应的高度
        BlockState[] aboveSurfaceBlocks; // 地表以上的装饰方块
        int[] aboveSurfaceHeights;     // 装饰方块的高度
    }
}
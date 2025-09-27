package com.bl.entity.custom;
import com.bl.BL;
import com.bl.entity.client.FallingBlockEntityMixinAccessor;
import net.minecraft.block.BlockState;
import net.minecraft.entity.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.List;
import java.util.Random;

//import static com.bl.entity.ModEntities.QUANTUM_BLOCK;

public class ExpandingSphereEntity extends MobEntity {
    private float radius = 0.02f; // 初始半径
    public float maxRadius = 20.0f; // 最大半径
    public float expansionRate = 0.5f; // 每Tick扩张的半径

    public ExpandingSphereEntity(EntityType<?> type, World world) {
        super((EntityType<? extends MobEntity>) type, world);
        this.noClip = true;
    }
    public void SetMax(float max)
    {
        this.maxRadius=max;
        this.expansionRate=max/60;
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

    @Override
    public void tick() {
        super.tick();
        this.addStatusEffect(new StatusEffectInstance(StatusEffects.GLOWING,99999,2,false,false));
        this.setVelocity(0F,0F,0F);

        // 扩大半径
        radius += expansionRate;

        // 摧毁范围内的实体和方块
        destroyInRadius();

        // 生成膨胀效果的粒子
        spawnExpansionParticles();

        // 达到最大半径后消失
        if (radius >= maxRadius) {
            this.discard();
        }
    }

    @Override
    public boolean damage(ServerWorld world, DamageSource source, float amount) {
        return false;
    }

    private void destroyInRadius() {
        // 获取范围内的所有实体
        /*Box box = new Box(
                this.getX() - radius, this.getY() - radius, this.getZ() - radius,
                this.getX() + radius, this.getY() + radius, this.getZ() + radius
        );

        List<Entity> entities = this.getWorld().getOtherEntities(this, box);
        for (Entity entity : entities) {
            if (entity instanceof LivingEntity || entity instanceof ItemEntity) {
                // 对实体造成"量子化"效果
                quantumizeEntity(entity);
            }
        }*/

        // 摧毁范围内的方块（如果是客户端，需要跳过）
        if (!this.getWorld().isClient) {
            // 这是一个性能敏感的操作，需要优化
            destroyBlocksInRadius();
        }
    }


    private void destroyBlocksInRadius() {
        // 这是一个简化的实现，实际应用中需要考虑性能优化
        int iRadius = (int) Math.ceil(radius);
        BlockPos center = this.getBlockPos();

        for (int x = -iRadius; x <= iRadius; x++) {
            for (int y = -iRadius; y <= iRadius; y++) {
                for (int z = -iRadius; z <= iRadius; z++) {
                    BlockPos pos = center.add(x, y, z);
                    if (center.getSquaredDistance(pos) <= radius * radius) {
                        // 创建方块的"量子态"掉落物
                        quantumizeBlock(pos);
                    }
                }
            }
        }
    }

    private void quantumizeBlock(BlockPos pos) {
        BlockState blockState = this.getWorld().getBlockState(pos);
        if (blockState.isAir() || blockState.getHardness(this.getWorld(), pos) < 0) {
            return; // 跳过空气和不可破坏的方块（如基岩）
        }

        // 移除原方块
        this.getWorld().removeBlock(pos, false);
        if(random.nextDouble()<0.95)
        {
            return;
        }

            FallingBlockEntity quantumBlock = FallingBlockEntity.spawnFromBlock(this.getWorld(),pos,blockState);
            /*QuantumBlockEntity quantumBlock = new QuantumBlockEntity(QUANTUM_BLOCK,this.getWorld());
            this.getWorld().setBlockState(pos, blockState.getFluidState().getBlockState(), 3);
            this.getWorld().spawnEntity(quantumBlock);*/
            quantumBlock.setPosition(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
        if (quantumBlock instanceof FallingBlockEntityMixinAccessor a) {
            a.bl$setQuantum();
            BL.LOGGER.info("66666");
        }

            // 随机抛射向量
            Vec3d velocity = new Vec3d(
                    (random.nextDouble() - 0.5) * 4.0,
                    random.nextDouble() * 2.0,
                    (random.nextDouble() - 0.5) * 4.0
            );
            quantumBlock.setVelocity(velocity);

            this.getWorld().spawnEntity(quantumBlock);
            // 创建量子态方块实体（类似掉落物形式）
    }

    private void spawnExpansionParticles() {
        // 生成表示膨胀边缘的粒子效果
        for (int i = 0; i < (int)radius*100; i++) {
            double theta = random.nextDouble() * Math.PI * 2;
            double phi = random.nextDouble() * Math.PI;
            double x = this.getX() + radius * Math.cos(theta) * Math.sin(phi);
            double y = this.getY() + radius * Math.cos(phi);
            double z = this.getZ() + radius * Math.sin(theta) * Math.sin(phi);

            this.getWorld().addParticleClient(ParticleTypes.ELECTRIC_SPARK,
                    x, y, z, 0, 0, 0);
        }
    }
    protected void initDataTracker() {
        // 初始化数据跟踪器（如果需要同步数据到客户端）
    }
    protected void readCustomDataFromNbt(NbtCompound nbt) {
        // 从NBT读取数据
        this.radius = nbt.getFloat("Radius").orElse(0f);
    }
    protected void writeCustomDataToNbt(NbtCompound nbt) {
        // 写入数据到NBT
        nbt.putFloat("Radius", this.radius);
    }
}

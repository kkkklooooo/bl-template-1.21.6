package com.bl.entity.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ConsumableComponent;
import net.minecraft.component.type.FoodComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.WindChargeEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Unique;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

public class FoodEnchantmentHelper {

    private static final Map<UUID, Integer> thornsDamageTimers = new HashMap<>();

    private static final int THORNS_DAMAGE_INTERVAL = 10;
    /**
     * 检查物品是否是食物
     */
    public static boolean isFood(ItemStack stack) {
        if (stack == null || stack.isEmpty()) return false;

        ConsumableComponent consumable = stack.get(DataComponentTypes.CONSUMABLE);
        if (consumable == null) return false;

        FoodComponent food = stack.get(DataComponentTypes.FOOD);
        return food != null;
    }
    public  static void applyThornsDuringEating(ItemStack stack, World world, PlayerEntity player) {
        RegistryEntry<Enchantment> thornsEntry = world.getRegistryManager().getEntryOrThrow(Enchantments.THORNS);
        int thornsLevel = EnchantmentHelper.getLevel(thornsEntry, stack);

        if (thornsLevel > 0) {
            UUID playerId = player.getUuid();

            // 获取或初始化玩家的计时器
            int timer = thornsDamageTimers.getOrDefault(playerId, 0);
            timer++;

            // 每间隔一定时间造成伤害
            if (timer >= THORNS_DAMAGE_INTERVAL) {
                // 根据荆棘等级计算伤害量，每级造成0.5点伤害
                float damageAmount = thornsLevel * 0.5F;

                // 对玩家造成伤害
                if (damageAmount > 0) {
                    player.damage((ServerWorld) player.getWorld(),player.getDamageSources().generic(), damageAmount);

                    // 播放受伤音效
                    if (thornsLevel >= 2) {
                        player.playSound(SoundEvents.ENTITY_PLAYER_HURT, 0.4F, 1.0F);
                    }
                }

                // 重置计时器
                timer = 0;
            }

            // 更新计时器
            thornsDamageTimers.put(playerId, timer);
        } else {
            // 如果没有荆棘附魔，移除计时器以节省内存
            thornsDamageTimers.remove(player.getUuid());
        }
    }

    /**
     * 应用所有附魔效果
     */
    public static void applyAllEnchantmentEffects(ItemStack stack, World world, LivingEntity entity) {
        if (!isFood(stack)) return;
        //SoundPlayer.playCustomSoundAtPosition(entity.getWorld(), entity.getPos(), SoundPlayer.Sounds.p, 100.0f, 1);

        // 只在服务器端执行效果
        if (world.isClient) return;

        // 风弹爆炸效果
        applyWindBurstEffect(stack, world, entity);

        // 引雷附魔效果 - 实体被雷劈
        applyChannelingEffect(stack, world, entity);

        // 火焰附加附魔效果 - 实体着火
        applyFireAspectEffect(stack, world, entity);

        if(entity instanceof PlayerEntity player)
        {
            applyThornsDuringEating(stack,world,player);
        }
    }

    /**
     * 应用引雷附魔效果 - 直接生成闪电实体
     */
    public static void applyChannelingEffect(ItemStack stack, World world, LivingEntity entity) {
        RegistryEntry<Enchantment> channelingEntry = world.getRegistryManager().getEntryOrThrow(Enchantments.CHANNELING);
        int channelingLevel = EnchantmentHelper.getLevel(channelingEntry, stack);

        if (channelingLevel > 0) {
            // 直接创建闪电实体
            LightningEntity lightning = new LightningEntity(
                    net.minecraft.entity.EntityType.LIGHTNING_BOLT,
                    world
            );

            // 设置闪电位置为实体位置
            lightning.setPosition(entity.getPos());

            // 将闪电添加到世界
            world.spawnEntity(lightning);

            // 播放雷声音效
            entity.playSound(SoundEvents.ENTITY_LIGHTNING_BOLT_THUNDER, 1.0F, 1.0F);
        }
    }

    /**
     * 应用火焰附加附魔效果 - 实体着火
     */
    public static void applyFireAspectEffect(ItemStack stack, World world, LivingEntity entity) {
        RegistryEntry<Enchantment> fireAspectEntry = world.getRegistryManager().getEntryOrThrow(Enchantments.FIRE_ASPECT);
        int fireAspectLevel = EnchantmentHelper.getLevel(fireAspectEntry, stack);

        if (fireAspectLevel > 0) {
            // 根据火焰附加等级设置着火时间，每级3秒（60游戏刻）
            int fireTicks = fireAspectLevel * 60;

            // 设置实体着火
            entity.setOnFireFor(fireTicks);

            // 播放着火音效
            entity.playSound(SoundEvents.ENTITY_BLAZE_SHOOT, 0.8F, 1.0F);
        }
    }

    /**
     * 应用风弹爆炸效果
     */
    public static void applyWindBurstEffect(ItemStack stack, World world, LivingEntity entity) {
        RegistryEntry<Enchantment> wbEntry = world.getRegistryManager().getEntryOrThrow(Enchantments.WIND_BURST);
        int windBurstLevel = EnchantmentHelper.getLevel(wbEntry, stack);

        if (windBurstLevel > 0) {
            // 使用实体作为所有者创建风弹
            SoundPlayer.playCustomSoundAtPosition(entity.getWorld(), entity.getPos(), SoundPlayer.Sounds.p, 100.0f, 1);
            WindChargeEntity windCharge = new WindChargeEntity(MinecraftClient.getInstance().player, world, entity.getX(), entity.getY(), entity.getZ());

            try {
                // 使用反射调用createExplosion方法
                Method createExplosionMethod = WindChargeEntity.class.getDeclaredMethod("createExplosion", Vec3d.class);
                createExplosionMethod.setAccessible(true);
                createExplosionMethod.invoke(windCharge, windCharge.getPos());
            } catch (Exception e) {
                System.err.println("Failed to invoke createExplosion via reflection: " + e.getMessage());

                // 备用方案：直接在世界中创建爆炸
                if (world instanceof ServerWorld) {
                    world.createExplosion(
                            entity,
                            entity.getX(), entity.getY(), entity.getZ(),
                            windBurstLevel * 0.5F,
                            false,
                            World.ExplosionSourceType.MOB
                    );
                }
            }

            windCharge.discard();
        }
    }

    /**
     * 计算食物新的使用时间
     */
    public static int calculateNewUseTime(ItemStack stack, LivingEntity user, int baseTime) {
        int newTime = baseTime;

        RegistryEntry<Enchantment> wbEntry = user.getWorld().getRegistryManager().getEntryOrThrow(Enchantments.EFFICIENCY);
        int efficiencyLevel = EnchantmentHelper.getLevel(wbEntry, stack);
        if (efficiencyLevel > 0) {
            float reductionFactor = 1.0F - (efficiencyLevel * 0.2F);

            newTime = Math.max(1, (int)(baseTime * reductionFactor));
        }

        return newTime;
    }
}

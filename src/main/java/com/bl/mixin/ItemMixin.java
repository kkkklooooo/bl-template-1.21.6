package com.bl.mixin;

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
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.lang.reflect.Method;

@Mixin(Item.class)
public class ItemMixin {

    // 简单的静态变量存储荆棘伤害计时器（假设只有一个玩家）
    @Unique
    private static int thornsDamageTimer = 0;

    @Unique
    private static final int THORNS_DAMAGE_INTERVAL = 10; // 每10游戏刻造成一次伤害

    @Inject(method = "getMaxUseTime", at = @At("HEAD"), cancellable = true)
    private void modifyFoodUseTime(ItemStack stack, LivingEntity user, CallbackInfoReturnable<Integer> cir) {
        ConsumableComponent consumable = stack.get(DataComponentTypes.CONSUMABLE);
        if (consumable == null) return;

        FoodComponent food = stack.get(DataComponentTypes.FOOD);
        if (food == null) return;

        int baseUseTime = 32;
        int newUseTime = calculateNewUseTime(stack, user, baseUseTime);

        if (newUseTime != baseUseTime) {
            cir.setReturnValue(newUseTime);
        }
    }

    @Inject(method = "finishUsing", at = @At("RETURN"))
    private void onFinishUsing(ItemStack stack, World world, LivingEntity user, CallbackInfoReturnable<ItemStack> cir) {
        if (!(user instanceof PlayerEntity player)) return;

        ConsumableComponent consumable = stack.get(DataComponentTypes.CONSUMABLE);
        if (consumable == null) return;

        FoodComponent food = stack.get(DataComponentTypes.FOOD);
        if (food == null) return;

        // 只在服务器端执行效果
        if (!world.isClient) {
            // 重置荆棘伤害计时器
            thornsDamageTimer = 0;

            // 风弹爆炸效果
            applyWindBurstEffect(stack, world, player);

            // 引雷附魔效果 - 玩家被雷劈
            applyChannelingEffect(stack, world, player);

            // 火焰附加附魔效果 - 玩家着火
            applyFireAspectEffect(stack, world, player);
        }
    }

    /**
     * 在食物使用过程中持续调用（每游戏刻调用一次）
     */
    @Inject(method = "usageTick", at = @At("HEAD"))
    private void onUsingTick(World world, LivingEntity user, ItemStack stack, int remainingUseTicks, CallbackInfo ci) {
        if (!(user instanceof PlayerEntity player)) return;

        ConsumableComponent consumable = stack.get(DataComponentTypes.CONSUMABLE);
        if (consumable == null) return;

        FoodComponent food = stack.get(DataComponentTypes.FOOD);
        if (food == null) return;

        // 只在服务器端执行
        if (!world.isClient) {
            // 应用食用过程中的荆棘效果
            applyThornsDuringEating(stack, world, player);
        }
    }

    /**
     * 计算食物新的使用时间
     */
    @Unique
    private int calculateNewUseTime(ItemStack stack, LivingEntity user, int baseTime) {
        int newTime = baseTime;

        RegistryEntry<Enchantment> wbEntry = user.getWorld().getRegistryManager().getEntryOrThrow(Enchantments.EFFICIENCY);
        int efficiencyLevel = EnchantmentHelper.getLevel(wbEntry, stack);
        if (efficiencyLevel > 0) {
            float reductionFactor = 1.0F - (efficiencyLevel * 0.2F);
            newTime = Math.max(1, (int)(baseTime * reductionFactor));
        }

        return newTime;
    }

    /**
     * 在食用过程中应用荆棘效果 - 持续掉血
     */
    @Unique
    private void applyThornsDuringEating(ItemStack stack, World world, PlayerEntity player) {
        RegistryEntry<Enchantment> thornsEntry = world.getRegistryManager().getEntryOrThrow(Enchantments.THORNS);
        int thornsLevel = EnchantmentHelper.getLevel(thornsEntry, stack);

        if (thornsLevel > 0) {
            // 更新计时器
            thornsDamageTimer++;

            // 每间隔一定时间造成伤害
            if (thornsDamageTimer >= THORNS_DAMAGE_INTERVAL) {
                // 根据荆棘等级计算伤害量，每级造成0.5点伤害（四分之一颗心）
                float damageAmount = thornsLevel * 0.5F;

                // 对玩家造成伤害
                if (damageAmount > 0) {
                    player.damage((ServerWorld) player.getWorld(),player.getDamageSources().generic(), damageAmount);

                    // 可选：播放受伤音效
                    if (thornsLevel >= 2) {
                        player.playSound(SoundEvents.ENTITY_PLAYER_HURT, 0.4F, 1.0F);
                    }
                }

                // 重置计时器
                thornsDamageTimer = 0;
            }
        } else {
            // 如果没有荆棘附魔，重置计时器
            thornsDamageTimer = 0;
        }
    }

    /**
     * 应用引雷附魔效果 - 直接生成闪电实体
     */
    @Unique
    private void applyChannelingEffect(ItemStack stack, World world, PlayerEntity player) {
        RegistryEntry<Enchantment> channelingEntry = world.getRegistryManager().getEntryOrThrow(Enchantments.CHANNELING);
        int channelingLevel = EnchantmentHelper.getLevel(channelingEntry, stack);

        if (channelingLevel > 0) {
            // 直接创建闪电实体
            LightningEntity lightning = new LightningEntity(
                    net.minecraft.entity.EntityType.LIGHTNING_BOLT,
                    world
            );

            // 设置闪电位置为玩家位置
            lightning.setPosition(player.getPos());

            // 设置闪电为无害模式（可选，避免直接伤害玩家）
            // lightning.setCosmetic(true);

            // 将闪电添加到世界
            world.spawnEntity(lightning);

            // 播放雷声音效
            player.playSound(SoundEvents.ENTITY_LIGHTNING_BOLT_THUNDER, 1.0F, 1.0F);
        }
    }

    /**
     * 应用火焰附加附魔效果 - 玩家着火
     */
    @Unique
    private void applyFireAspectEffect(ItemStack stack, World world, PlayerEntity player) {
        RegistryEntry<Enchantment> fireAspectEntry = world.getRegistryManager().getEntryOrThrow(Enchantments.FIRE_ASPECT);
        int fireAspectLevel = EnchantmentHelper.getLevel(fireAspectEntry, stack);

        if (fireAspectLevel > 0) {
            // 根据火焰附加等级设置着火时间，每级3秒（60游戏刻）
            int fireTicks = fireAspectLevel * 60;

            // 设置玩家着火
            player.setOnFireFor(fireTicks);

            // 播放着火音效
            player.playSound(SoundEvents.ENTITY_BLAZE_SHOOT, 0.8F, 1.0F);
        }
    }

    /**
     * 应用风弹爆炸效果
     */
    @Unique
    private void applyWindBurstEffect(ItemStack stack, World world, PlayerEntity player) {
        RegistryEntry<Enchantment> wbEntry = world.getRegistryManager().getEntryOrThrow(Enchantments.WIND_BURST);
        int windBurstLevel = EnchantmentHelper.getLevel(wbEntry, stack);

        if (windBurstLevel > 0) {
            WindChargeEntity windCharge = new WindChargeEntity(player, world, player.getX(), player.getY(), player.getZ());

            try {
                Method createExplosionMethod = WindChargeEntity.class.getDeclaredMethod("createExplosion", Vec3d.class);
                createExplosionMethod.setAccessible(true);
                createExplosionMethod.invoke(windCharge, windCharge.getPos());
            } catch (Exception e) {
                System.err.println("Failed to invoke createExplosion via reflection: " + e.getMessage());
            }

            windCharge.discard();
        }
    }
}
package com.bl.mixin;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ConsumableComponent;
import net.minecraft.component.type.FoodComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.WindChargeEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;

@Mixin(Item.class)
public class ItemMixin {

    @Inject(method = "getMaxUseTime", at = @At("HEAD"), cancellable = true)
    private void modifyFoodUseTime(ItemStack stack, LivingEntity user, CallbackInfoReturnable<Integer> cir) {
        // 获取物品的ConsumableComponent，确认物品可被食用
        ConsumableComponent consumable = stack.get(DataComponentTypes.CONSUMABLE);
        if (consumable == null) return;

        // 获取物品的FoodComponent，确认是食物
        FoodComponent food = stack.get(DataComponentTypes.FOOD);
        if (food == null) return;

        // 获取基础使用时间（默认为1.6秒，即32游戏刻）
        int baseUseTime = 32; // 对应原版默认食物使用时间

        // 计算新的使用时间（例如，根据附魔调整）
        int newUseTime = calculateNewUseTime(stack, user, baseUseTime);

        // 如果计算出的新时间与默认时间不同，则设置返回值
        if (newUseTime != baseUseTime) {
            cir.setReturnValue(newUseTime);
        }
    }

    @Inject(method = "finishUsing", at = @At("RETURN"))
    private void onFinishUsing(ItemStack stack, World world, LivingEntity user, CallbackInfoReturnable<ItemStack> cir) {
        // 确保使用者是玩家，避免其他生物食用时触发
        if (!(user instanceof PlayerEntity player)) return;

        // 检查物品是否具有ConsumableComponent和FoodComponent，确认它是可食用的食物
        ConsumableComponent consumable = stack.get(DataComponentTypes.CONSUMABLE);
        if (consumable == null) return;

        FoodComponent food = stack.get(DataComponentTypes.FOOD);
        if (food == null) return;

        RegistryEntry<Enchantment> wbEntry =user.getWorld().getRegistryManager().getEntryOrThrow(Enchantments.WIND_BURST);
        int fireAspectLevel = EnchantmentHelper.getLevel(wbEntry, stack);
        if (fireAspectLevel > 0) {
            // 在服务器端执行
            if (!world.isClient) {
                // 创建风弹实体，所有者是玩家，位置为玩家位置
                WindChargeEntity windCharge = new WindChargeEntity(player, world, player.getX(), player.getY(), player.getZ());
                // 调用风弹的爆炸方法
                try {
                    // 获取 createExplosion 方法，参数为 Vec3d
                    Method createExplosionMethod = WindChargeEntity.class.getDeclaredMethod("createExplosion", Vec3d.class);
                    // 设置方法为可访问（突破 protected 限制）
                    createExplosionMethod.setAccessible(true);
                    // 调用方法，传入风弹当前位置
                    createExplosionMethod.invoke(windCharge, windCharge.getPos());
                } catch (Exception e) {
                    // 处理反射可能抛出的异常
                    System.err.println("Failed to invoke createExplosion via reflection: " + e.getMessage());
                    //e.printStackTrace();
                    // 作为备选方案，可以直接在世界中创建爆炸
                    // world.createExplosion(player, windCharge.getX(), windCharge.getY(), windCharge.getZ(), 1.2F, World.ExplosionSourceType.TRIGGER);
                }

                windCharge.discard();
                // 注意：我们并不需要将风弹实体添加到世界，因为爆炸已经产生，我们直接丢弃它（或者不添加即可，由垃圾回收处理）
                // 由于我们没有将风弹加入世界，所以不需要丢弃。
            }
        }
    }

    /**
     * 计算食物新的使用时间
     */
    @Unique
    private int calculateNewUseTime(ItemStack stack, LivingEntity user, int baseTime) {
        int newTime = baseTime;

        // 示例1：如果物品拥有"效率"附魔，则减少使用时间
        RegistryEntry<Enchantment> wbEntry =user.getWorld().getRegistryManager().getEntryOrThrow(Enchantments.EFFICIENCY);
        int efficiencyLevel = EnchantmentHelper.getLevel(wbEntry, stack);
        if (efficiencyLevel > 0) {
            float reductionFactor = 1.0F - (efficiencyLevel * 0.2F);
            newTime = Math.max(1, (int)(baseTime * reductionFactor));
        }

        // 示例2：根据食用者特定状态调整（此处为示例，实际状态效果需根据情况获取）
        // if (user.hasStatusEffect(StatusEffects.HASTE)) {
        //     newTime = (int)(newTime * 0.7F); // 急迫效果减少30%使用时间
        // }

        return newTime;
    }
}

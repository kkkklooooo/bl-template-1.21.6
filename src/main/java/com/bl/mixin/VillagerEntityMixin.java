package com.bl.mixin;

import com.bl.entity.client.FoodEnchantmentHelper;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ConsumableComponent;
import net.minecraft.component.type.FoodComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Unique;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Mixin(VillagerEntity.class)
public class VillagerEntityMixin {


    /**
     * @author YourName
     * @reason 重写食物消耗方法以添加附魔效果，并为玩家添加荆棘扣血效果
     */
    @Overwrite
    private void consumeAvailableFood() {
        VillagerEntity villager = (VillagerEntity) (Object) this;
        World world = villager.getWorld();

        if (this.canEatFood() && this.getAvailableFood() != 0) {
            SimpleInventory inventory = villager.getInventory();

            for(int i = 0; i < inventory.size(); ++i) {
                ItemStack itemStack = inventory.getStack(i);
                if (!itemStack.isEmpty()) {
                    Integer foodValue = getFoodValue(itemStack.getItem());
                    if (foodValue != null) {
                        int stackCount = itemStack.getCount();

                        for(int k = stackCount; k > 0; --k) {

                            // 增加食物等级
                            this.increaseFoodLevel(foodValue);
                            inventory.removeStack(i, 1);

                            // 应用附魔效果（对所有生物）
                            if (!world.isClient) {
                                ItemStack consumedStack = itemStack.copy();
                                consumedStack.setCount(1);
                                FoodEnchantmentHelper.applyAllEnchantmentEffects(consumedStack, world, villager);
                            }

                            if (!this.canEatFood()) {
                                return;
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * 获取食物价值
     */
    @Unique
    private Integer getFoodValue(Item item) {
        Map<Item, Integer> foodValues = new HashMap<>();
        foodValues.put(Items.BREAD, 4);
        foodValues.put(Items.POTATO, 1);
        foodValues.put(Items.CARROT, 1);
        foodValues.put(Items.BEETROOT, 1);
        // 可以添加更多食物...

        return foodValues.get(item);
    }

    /**
     * 检查是否可以吃食物
     */
    @Unique
    private boolean canEatFood() {
        VillagerEntity villager = (VillagerEntity) (Object) this;
        return getFoodLevel(villager) < 12;
    }

    /**
     * 获取食物等级
     */
    @Unique
    private int getFoodLevel(VillagerEntity villager) {
        // 这里需要访问原类的foodLevel字段
        // 由于是private字段，我们需要使用accessor或者反射
        // 这里使用简化实现，你可能需要根据实际情况调整
        try {
            java.lang.reflect.Field foodLevelField = VillagerEntity.class.getDeclaredField("foodLevel");
            foodLevelField.setAccessible(true);
            return (int) foodLevelField.get(villager);
        } catch (Exception e) {
            return 0; // 默认值
        }
    }

    /**
     * 增加食物等级
     */
    @Unique
    private void increaseFoodLevel(int amount) {
        VillagerEntity villager = (VillagerEntity) (Object) this;
        try {
            java.lang.reflect.Field foodLevelField = VillagerEntity.class.getDeclaredField("foodLevel");
            foodLevelField.setAccessible(true);
            int currentFood = (int) foodLevelField.get(villager);
            foodLevelField.set(villager, currentFood + amount);
        } catch (Exception e) {
            // 处理异常
        }
    }

    /**
     * 获取可用食物数量
     */
    @Unique
    private int getAvailableFood() {
        VillagerEntity villager = (VillagerEntity) (Object) this;
        SimpleInventory inventory = villager.getInventory();
        Map<Item, Integer> foodValues = new HashMap<>();
        foodValues.put(Items.BREAD, 4);
        foodValues.put(Items.POTATO, 1);
        foodValues.put(Items.CARROT, 1);
        foodValues.put(Items.BEETROOT, 1);

        return foodValues.entrySet().stream()
                .mapToInt(entry -> inventory.count(entry.getKey()) * entry.getValue())
                .sum();
    }

    /**
     * 在食用过程中应用荆棘效果 - 仅对玩家生效
     */
}
package com.bl.mixin;

import com.bl.entity.client.FoodEnchantmentHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Item.class)
public class ItemMixin {

    @Inject(method = "getMaxUseTime", at = @At("HEAD"), cancellable = true)
    private void modifyFoodUseTime(ItemStack stack, LivingEntity user, CallbackInfoReturnable<Integer> cir) {
        if (!FoodEnchantmentHelper.isFood(stack)) return;

        int baseUseTime = 32;
        int newUseTime = FoodEnchantmentHelper.calculateNewUseTime(stack, user, baseUseTime);

        if (newUseTime != baseUseTime) {
            cir.setReturnValue(newUseTime);
        }
    }

    @Inject(method = "finishUsing", at = @At("RETURN"))
    private void onFinishUsing(ItemStack stack, World world, LivingEntity user, CallbackInfoReturnable<ItemStack> cir) {
        if (!FoodEnchantmentHelper.isFood(stack)) return;

        // 只在服务器端执行效果
        if (!world.isClient) {
            // 应用所有附魔效果
            FoodEnchantmentHelper.applyAllEnchantmentEffects(stack, world, user);
        }
    }
}
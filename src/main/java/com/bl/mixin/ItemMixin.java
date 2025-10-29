package com.bl.mixin;

import com.bl.BL;
import com.bl.entity.client.FoodEnchantmentHelper;
import com.bl.entity.client.SoundPlayer;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Random;

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
        RegistryEntry<Enchantment> wbEntry1 = world.getRegistryManager().getEntryOrThrow(Enchantments.WIND_BURST);
        int windBurstLevel = EnchantmentHelper.getLevel(wbEntry1, stack);

        if (windBurstLevel > 0) {
            SoundPlayer.playCustomSoundAtPosition(user.getWorld(), user.getPos(), SoundPlayer.Sounds.p, 100.0f, 1);
        }
        RegistryEntry<Enchantment> wbEntry = user.getWorld().getRegistryManager().getEntryOrThrow(Enchantments.EFFICIENCY);
        int efficiencyLevel = EnchantmentHelper.getLevel(wbEntry, stack);
        if (efficiencyLevel > 0) {
            if(BL.a>=5) {
                float a = new Random().nextFloat();
                if (a < 0.33333) {
                    SoundPlayer.playCustomSoundAtPosition(user.getWorld(), user.getPos(), SoundPlayer.Sounds.a, 100.0f, 1);
                } else if (a < 0.667) {
                    SoundPlayer.playCustomSoundAtPosition(user.getWorld(), user.getPos(), SoundPlayer.Sounds.b, 100.0f, 1);
                } else {
                    SoundPlayer.playCustomSoundAtPosition(user.getWorld(), user.getPos(), SoundPlayer.Sounds.c, 100.0f, 1);
                }
                BL.a=0;
            }
            else
            {
                BL.a++;
            }

        }
    }
}
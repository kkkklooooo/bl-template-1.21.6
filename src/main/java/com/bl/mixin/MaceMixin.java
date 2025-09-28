package com.bl.mixin;

import com.bl.BL;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.item.MaceItem;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.minecraft.item.MaceItem.shouldDealAdditionalDamage;

@Mixin(MaceItem.class)
public abstract class MaceMixin {

    @Inject(method="postHit",at=@At("TAIL"))
    public void postHit(ItemStack stack, LivingEntity target, LivingEntity attacker, CallbackInfo ci)
    {
        if(shouldDealAdditionalDamage(attacker))
        {
            RegistryEntry<Enchantment> wbEntry=attacker.getWorld().getRegistryManager().getEntryOrThrow(Enchantments.WIND_BURST);
            int level= EnchantmentHelper.getLevel(wbEntry,stack);
            if(level>0)
            {
                double s;
                switch (level)
                {
                    case 1:
                        s=3;
                        break;
                    case 2:
                        s=4;
                        break;
                    case 3:
                        s=5;
                        break;
                    default:
                        s=(4+level/3.0);
                        break;
                }
                BL.createExpandingSphere(s,target.getPos(),(ServerWorld) target.getWorld());
                BL.LOGGER.info(String.valueOf(attacker));
                attacker.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOW_FALLING, level+100,0,false,true));

            }
        }
    }
}

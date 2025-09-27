package com.bl.mixin;

import com.bl.BL;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.AbstractWindChargeEntity;
import net.minecraft.entity.projectile.WindChargeEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.apache.commons.lang3.ObjectUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.lang.reflect.Method;

@Mixin(AbstractWindChargeEntity.class)
public class WindChargeEntityMixin {

    @Inject(method="onEntityHit",at=@At("HEAD"))
    protected void onEntityHit(EntityHitResult result,CallbackInfo ci)
    {
        //BL.createExpandingSphere(1.0f,result.getPos(),result.getEntity().getWorld().isClient?null :result.getEntity().getWorld());
    }
    @Inject(method = "onBlockHit",at=@At("HEAD"))
    protected void onBlockHit(BlockHitResult blockHitResult, CallbackInfo ci)
    {
        //BL.createExpandingSphere(1.0f,blockHitResult.getPos(),((AbstractWindChargeEntity)(Object)this).getWorld().isClient?null:((AbstractWindChargeEntity)(Object)this).getWorld());
    }


}

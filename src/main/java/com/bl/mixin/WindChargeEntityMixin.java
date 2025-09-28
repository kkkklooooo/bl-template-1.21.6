package com.bl.mixin;

import com.bl.BL;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.AbstractWindChargeEntity;
import net.minecraft.entity.projectile.WindChargeEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.lang.reflect.Method;

@Mixin(WindChargeEntity.class)
public class WindChargeEntityMixin {

    @Inject(method="createExplosion",at=@At("HEAD"))
    protected void onEntityHit(Vec3d pos, CallbackInfo ci)
    {
        BL.createExpandingSphere(3.0f,pos,(ServerWorld)((WindChargeEntity)(Object)this).getWorld());
    }
    /*@Inject(method = "onBlockHit",at=@At("HEAD"))
    protected void onBlockHit(BlockHitResult blockHitResult, CallbackInfo ci)
    {
        BL.createExpandingSphere(1.0f,blockHitResult.getPos(),((AbstractWindChargeEntity)(Object)this).getWorld());
    }*/


}

package com.bl.mixin;


import com.bl.BL;
import net.minecraft.entity.projectile.AbstractWindChargeEntity;
import net.minecraft.entity.projectile.WindChargeEntity;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WindChargeEntity.class)
public class FuckMixin {
    @Inject(method="createExplosion",at=@At("HEAD"))
    protected void createExplosion(Vec3d pos, CallbackInfo ci) {
        BL.createExpandingSphere(10.0f,((WindChargeEntity)(Object)this).getPos(),((WindChargeEntity)(Object)this).getWorld().isClient?null: (net.minecraft.server.world.ServerWorld) ((AbstractWindChargeEntity)(Object)this).getWorld());
    }
}

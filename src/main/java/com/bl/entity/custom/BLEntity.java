package com.bl.entity.custom;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.world.World;

public class BLEntity extends MobEntity {
    public BLEntity(EntityType<? extends MobEntity> entityType, World world) {
        super(entityType, world);
    }


    public static DefaultAttributeContainer createAttributes(){
        return MobEntity.createMobAttributes()
                .add(EntityAttributes.MAX_HEALTH,33550336.0)
                .add(EntityAttributes.MOVEMENT_SPEED,1f)
                .add(EntityAttributes.FLYING_SPEED,1f)
                .add(EntityAttributes.ATTACK_DAMAGE,0.5f)
                .add(EntityAttributes.FOLLOW_RANGE,10)
                .build();
    }
}

package com.bl.entity.client;

import com.bl.BL;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class SoundPlayer {

    public enum Sounds{
        p,
        a,
        b,
        c,
    }


    public static void playCustomSoundAtPosition(World world, BlockPos pos,
                                                 Sounds Type, float volume, float pitch) {
        if (!world.isClient()) return;

        Identifier i = switch (Type){
            case p -> Identifier.of("bl","p");
            case a -> Identifier.of("bl","1");
            case b -> Identifier.of("bl","2");
            case c -> Identifier.of("bl","3");
        };

        // 获取客户端世界和玩家
        ClientWorld clientWorld = (ClientWorld) world;
        MinecraftClient client = MinecraftClient.getInstance();

        // 创建音效实例
        PositionedSoundInstance soundInstance =PositionedSoundInstance.master(
                SoundEvent.of(i),pitch,volume
        );

//        PositionedSoundInstance soundInstance = new PositionedSoundInstance(
//                soundId, // 例如: new Identifier("modid", "custom_sound")
//                SoundCategory.MASTER,
//                volume,
//                pitch,
//                false, // 是否循环
//                0, // 延迟
//                SoundInstance.AttenuationType.LINEAR,
//                pos.getX() + 0.5, // 音效位置 X
//                pos.getY() + 0.5, // 音效位置 Y
//                pos.getZ() + 0.5, // 音效位置 Z
//                false // 是否相对位置
//        );

        // 播放音效
        client.getSoundManager().play(soundInstance);
        BL.LOGGER.warn("Play");
    }
}
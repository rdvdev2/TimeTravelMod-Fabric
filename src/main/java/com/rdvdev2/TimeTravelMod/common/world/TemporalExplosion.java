package com.rdvdev2.TimeTravelMod.common.world;

import com.rdvdev2.TimeTravelMod.ModBlocks;
import com.rdvdev2.TimeTravelMod.ModTriggers;
import com.rdvdev2.TimeTravelMod.common.block.TemporalExplosionBlock;
import net.minecraft.entity.Entity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;

public class TemporalExplosion {

    private World world;
    private Entity entity;
    private BlockPos pos;
    private float strength;

    public TemporalExplosion(World world, Entity entity, BlockPos pos, float strength) {
        this.world = world;
        this.entity = entity;
        this.pos = pos;
        this.strength = strength;
    }

    public void explode() {
        Explosion explosion = new Explosion(world, entity, pos.getX(), pos.getY(), pos.getZ(), strength, false, Explosion.DestructionType.NONE);
        explosion.collectBlocksAndDamageEntities();
        world.playSound(null, pos, SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.BLOCKS, 6.0F, (1.0F + (this.world.random.nextFloat() - this.world.random.nextFloat()) * 0.2F) * 0.7F);
        for(BlockPos block:explosion.getAffectedBlocks()) {
            for (Entity entity:world.getEntities(null, new Box(block))) {
                entity.damage(TemporalExplosionBlock.damage, 1000000);
                if (entity instanceof ServerPlayerEntity) ModTriggers.TEMPORAL_EXPLOSION.trigger((ServerPlayerEntity) entity);
            }
            world.setBlockState(block, ModBlocks.TEMPORAL_EXPLOSION.getDefaultState());
        }
    }
}

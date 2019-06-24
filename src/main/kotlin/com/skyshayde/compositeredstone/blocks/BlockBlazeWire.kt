package com.skyshayde.compositeredstone.blocks

import net.minecraft.block.state.IBlockState
import net.minecraft.enchantment.EnchantmentHelper
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityLivingBase
import net.minecraft.util.DamageSource
import net.minecraft.util.EnumParticleTypes
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess
import net.minecraft.world.World
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import java.util.*

class BlockBlazeWire : BlockCompositeWire("blaze_wire") {
    override fun getColor(world: IBlockAccess?, state: IBlockState?, pos: BlockPos?, tint: Int): Int {
        val power = state?.getValue(POWER) ?: 0
        return BlockCompositeWire.toRGB(33f, 100f, 30f + power * 2f, 1f).rgb
    }

    @SideOnly(Side.CLIENT)
    override fun randomDisplayTick(stateIn: IBlockState, worldIn: World, pos: BlockPos, rand: Random) {
        val i = (stateIn.getValue(POWER) as Int).toInt()

        if (i != 0 && rand.nextInt(3) == 0) {
            val d0 = pos.x.toDouble() + 0.5 + (rand.nextFloat().toDouble() - 0.5) * 0.2
            val d1 = (pos.y.toFloat() + 0.0625f).toDouble()
            val d2 = pos.z.toDouble() + 0.5 + (rand.nextFloat().toDouble() - 0.5) * 0.2
            val f = i.toFloat() / 15.0f
            val f1 = f * 0.6f + 0.4f
            val f2 = Math.max(0.0f, f * f * 0.7f - 0.5f)
            val f3 = Math.max(0.0f, f * f * 0.6f - 0.7f)
            val particle = if (rand.nextBoolean()) EnumParticleTypes.LAVA else EnumParticleTypes.REDSTONE
            worldIn.spawnParticle(particle, d0, d1, d2, f1.toDouble(), f2.toDouble(), f3.toDouble())
        }
    }

    override fun onEntityCollidedWithBlock(worldIn: World, pos: BlockPos, state: IBlockState, entityIn: Entity) {
        if (!entityIn.isImmuneToFire && entityIn is EntityLivingBase && !EnchantmentHelper.hasFrostWalkerEnchantment(entityIn)) {
            entityIn.setFire(state.getValue(POWER)/5)
        }
        super.onEntityCollidedWithBlock(worldIn, pos, state, entityIn)
    }
}
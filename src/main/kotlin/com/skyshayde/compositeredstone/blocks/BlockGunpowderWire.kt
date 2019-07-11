package com.skyshayde.compositeredstone.blocks

import net.minecraft.block.material.Material
import net.minecraft.block.state.IBlockState
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess
import net.minecraft.world.World
import java.util.*

class BlockGunpowderWire : BlockCompositeWire("gunpowder_wire", Material.CIRCUITS) {

    override fun getColor(world: IBlockAccess?, state: IBlockState?, pos: BlockPos?, tint: Int): Int {
        val power = state?.getValue(POWER) ?: 0
        return BlockCompositeWire.toRGB(0f, 0f, 40f + power * 2f, 1f).rgb
    }

    override fun updateTick(worldIn: World, pos: BlockPos, state: IBlockState, rand: Random) {
        if (!worldIn.isRemote && state.getValue(POWER) > 0) {
            worldIn.createExplosion(null, pos.x.toDouble(), pos.y.toDouble(), pos.z.toDouble(), 1f, false)
            worldIn.destroyBlock(pos, true);
        }
    }
}

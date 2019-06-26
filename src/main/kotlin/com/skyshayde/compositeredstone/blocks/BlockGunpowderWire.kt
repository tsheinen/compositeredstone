package com.skyshayde.compositeredstone.blocks

import net.minecraft.block.material.Material
import net.minecraft.block.state.IBlockState
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess

class BlockGunpowderWire : BlockCompositeWire("gunpowder_wire", Material.CIRCUITS) {

    override var powerDecreasedPerBlock = 0

    override fun getColor(world: IBlockAccess?, state: IBlockState?, pos: BlockPos?, tint: Int): Int {
        val power = state?.getValue(POWER) ?: 0
        return BlockCompositeWire.toRGB(0f, 0f, 40f + power * 2f, 1f).rgb
    }

}

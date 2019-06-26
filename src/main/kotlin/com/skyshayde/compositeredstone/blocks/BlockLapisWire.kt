package com.skyshayde.compositeredstone.blocks

import net.minecraft.block.material.Material
import net.minecraft.block.state.IBlockState
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess

class BlockLapisWire : BlockCompositeWire("lapis_wire", Material.CIRCUITS) {
    override fun getColor(world: IBlockAccess?, state: IBlockState?, pos: BlockPos?, tint: Int): Int {
        val power = state?.getValue(POWER) ?: 0
        return BlockCompositeWire.toRGB(240f, 100f, 30f + power * 3f, 1f).rgb
    }
    override var powerDecreasedPerBlock = 2
}
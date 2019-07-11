package com.skyshayde.compositeredstone.blocks

import net.minecraft.block.material.Material
import net.minecraft.block.state.IBlockState
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess

class BlockSugarWire : BlockCompositeWire("sugar_wire", Material.CIRCUITS) {

    override fun getColor(world: IBlockAccess?, state: IBlockState?, pos: BlockPos?, tint: Int): Int {
        val power = state?.getValue(POWER) ?: 0
        return BlockCompositeWire.toRGB(0f, 0f, 55f + power * 3f, 1f).rgb
    }
}
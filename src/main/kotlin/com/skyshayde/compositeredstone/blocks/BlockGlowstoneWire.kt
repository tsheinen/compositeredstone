package com.skyshayde.compositeredstone.blocks

import net.minecraft.block.material.Material
import net.minecraft.block.state.IBlockState
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess

class BlockGlowstoneWire : BlockCompositeWire("glowstone_wire", Material.CIRCUITS) {
    override fun getColor(world: IBlockAccess?, state: IBlockState?, pos: BlockPos?, tint: Int): Int {
        val power = state?.getValue(POWER) ?: 0
        return BlockCompositeWire.toRGB(72f, 100f, 30f + power * 2f, 1f).rgb
    }

    override fun getLightValue(state: IBlockState): Int {
        return if(state.getValue(POWER) > 0) 15 else 0
    }


}

package com.skyshayde.compositeredstone.blocks

import net.minecraft.block.Block
import net.minecraft.block.BlockFalling
import net.minecraft.block.BlockFalling.canFallThrough
import net.minecraft.block.material.Material
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.item.EntityFallingBlock
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess
import net.minecraft.world.World
import java.util.*

class BlockSandWire : BlockCompositeWire("sand_wire", Material.SAND) {

    override fun getColor(world: IBlockAccess?, state: IBlockState?, pos: BlockPos?, tint: Int): Int {
        val power = state?.getValue(POWER) ?: 0
        return BlockCompositeWire.toRGB(52f, 72f, 30f + power * 3f, 1f).rgb
    }

    override fun neighborChanged(state: IBlockState, worldIn: World, pos: BlockPos, blockIn: Block, fromPos: BlockPos) {
        if (!worldIn.isRemote) {
            if (this.canPlaceBlockAt(worldIn, pos)) {
                this.updateSurroundingRedstone(worldIn, pos, state)
            }
        }
        worldIn.scheduleUpdate(pos, this, this.tickRate(worldIn))
    }

    override fun onBlockAdded(worldIn: World, pos: BlockPos, state: IBlockState) {
        super.onBlockAdded(worldIn, pos, state)
        worldIn.scheduleUpdate(pos, this, this.tickRate(worldIn))
    }

    override fun updateTick(worldIn: World, pos: BlockPos, state: IBlockState, rand: Random) {
        if (!worldIn.isRemote) {
            this.checkFallable(worldIn, pos)
        }
    }

    private fun checkFallable(worldIn: World, pos: BlockPos) {
        if ((worldIn.isAirBlock(pos.down()) || BlockFalling.canFallThrough(worldIn.getBlockState(pos.down()))) && pos.y >= 0) {
            val i = 32

            if (worldIn.isAreaLoaded(pos.add(-32, -32, -32), pos.add(32, 32, 32))) {
                if (!worldIn.isRemote) {
                    val entityfallingblock = EntityFallingBlock(worldIn, pos.x.toDouble() + 0.5, pos.y.toDouble(), pos.z.toDouble() + 0.5, worldIn.getBlockState(pos))
                    worldIn.spawnEntity(entityfallingblock)
                }
            } else {
                val state = worldIn.getBlockState(pos)
                worldIn.setBlockToAir(pos)
                var blockpos: BlockPos

                blockpos = pos.down()
                while ((worldIn.isAirBlock(blockpos) || canFallThrough(worldIn.getBlockState(blockpos))) && blockpos.y > 0) {
                    blockpos = blockpos.down()
                }

                if (blockpos.y > 0) {
                    worldIn.setBlockState(blockpos.up(), state)
                }
            }
        }
    }
}
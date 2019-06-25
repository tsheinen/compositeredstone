package com.skyshayde.compositeredstone.blocks

import net.minecraft.block.Block
import net.minecraft.block.BlockFalling
import net.minecraft.block.BlockFalling.canFallThrough
import net.minecraft.block.material.MapColor
import net.minecraft.block.material.Material
import net.minecraft.block.material.MaterialLogic
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.item.EntityFallingBlock
import net.minecraft.init.Blocks
import net.minecraft.util.EnumParticleTypes
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess
import net.minecraft.world.World
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import java.util.*
import kotlin.math.max

class BlockSandWire : BlockCompositeWire("sand_wire", Material.SAND) {

    override fun getColor(world: IBlockAccess?, state: IBlockState?, pos: BlockPos?, tint: Int): Int {
        val power = state?.getValue(POWER) ?: 0
        return BlockCompositeWire.toRGB(52f, 72f, 40f + power * 3f, 1f).rgb
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
            val f2 = max(0.0f, f * f * 0.7f - 0.5f)
            val f3 = max(0.0f, f * f * 0.6f - 0.7f)
            worldIn.spawnParticle(EnumParticleTypes.REDSTONE, d0, d1, d2, f1.toDouble(), f2.toDouble(), f3.toDouble())
        }
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
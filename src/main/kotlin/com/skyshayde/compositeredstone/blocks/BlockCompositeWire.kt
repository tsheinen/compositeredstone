package com.skyshayde.compositeredstone.blocks

import com.google.common.collect.Lists
import com.google.common.collect.Sets
import net.minecraft.block.Block
import net.minecraft.block.BlockObserver
import net.minecraft.block.BlockRedstoneDiode
import net.minecraft.block.BlockRedstoneRepeater
import net.minecraft.block.properties.IProperty
import net.minecraft.block.properties.PropertyInteger
import net.minecraft.block.state.BlockStateContainer
import net.minecraft.block.state.IBlockState
import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.init.Blocks
import net.minecraft.item.Item
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumParticleTypes
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess
import net.minecraft.world.World
import net.minecraftforge.client.model.ModelLoader
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import vazkii.arl.block.BlockModDust
import java.awt.Color
import java.util.*


abstract class BlockCompositeWire(name: String) : BlockModDust(name) {
    private var canProvidePower = true
    /** List of blocks to update with redstone.  */
    private val blocksNeedingUpdate = Sets.newHashSet<BlockPos>()

    companion object {
        val POWER: PropertyInteger = PropertyInteger.create("power", 0, 15)

        fun toRGB(h: Float, s: Float, l: Float, alpha: Float): Color {
            var h = h
            var s = s
            var l = l
            if (s < 0.0f || s > 100.0f) {
                val message = "Color parameter outside of expected range - Saturation"
                throw IllegalArgumentException(message)
            }

            if (l < 0.0f || l > 100.0f) {
                val message = "Color parameter outside of expected range - Luminance"
                throw IllegalArgumentException(message)
            }

            if (alpha < 0.0f || alpha > 1.0f) {
                val message = "Color parameter outside of expected range - Alpha"
                throw IllegalArgumentException(message)
            }

            //  Formula needs all values between 0 - 1.

            h = h % 360.0f
            h /= 360f
            s /= 100f
            l /= 100f

            var q = 0f

            if (l < 0.5)
                q = l * (1 + s)
            else
                q = l + s - s * l

            val p = 2 * l - q

            var r = Math.max(0f, HueToRGB(p, q, h + 1.0f / 3.0f))
            var g = Math.max(0f, HueToRGB(p, q, h))
            var b = Math.max(0f, HueToRGB(p, q, h - 1.0f / 3.0f))

            r = Math.min(r, 1.0f)
            g = Math.min(g, 1.0f)
            b = Math.min(b, 1.0f)

            return Color(r, g, b, alpha)
        }

        private fun HueToRGB(p: Float, q: Float, h: Float): Float {
            var h = h
            if (h < 0) h += 1f

            if (h > 1) h -= 1f

            if (6 * h < 1) {
                return p + (q - p) * 6f * h
            }

            if (2 * h < 1) {
                return q
            }

            return if (3 * h < 2) {
                p + (q - p) * 6f * (2.0f / 3.0f - h)
            } else p

        }
    }

    init {
        defaultState = defaultState.withProperty(POWER, Integer.valueOf(0))
        setCreativeTab(CreativeTabs.MISC)
    }

    abstract override fun getColor(world: IBlockAccess?, state: IBlockState?, pos: BlockPos?, tint: Int): Int


    override fun getModNamespace(): String {
        return "compositeredstone"
    }

    override fun createBlockState(): BlockStateContainer {
        return BlockStateContainer(this, *arrayOf<IProperty<*>>(NORTH, EAST, SOUTH, WEST, POWER))
    }

    override fun canConnectTo(blockState: IBlockState, side: EnumFacing?, world: IBlockAccess, pos: BlockPos): Boolean {
        val block = blockState.block
        if (block == this) {
            return true
        } else if (block == Blocks.REDSTONE_WIRE || block is BlockCompositeWire) {
            return false
        } else if (Blocks.UNPOWERED_REPEATER.isSameDiode(blockState)) {
            val enumfacing = blockState.getValue(BlockRedstoneRepeater.FACING) as EnumFacing
            return enumfacing == side || enumfacing.opposite == side
        } else return if (Blocks.OBSERVER === blockState.block) {
            side == blockState.getValue(BlockObserver.FACING)
        } else {
            blockState.block.canConnectRedstone(blockState, world, pos, side)
        }
    }

    private fun updateSurroundingRedstone(worldIn: World, pos: BlockPos, state: IBlockState): IBlockState {
        var state = state
        state = this.calculateCurrentChanges(worldIn, pos, pos, state)
        val list = Lists.newArrayList(this.blocksNeedingUpdate)
        this.blocksNeedingUpdate.clear()

        for (blockpos in list) {
            worldIn.notifyNeighborsOfStateChange(blockpos, this, false)
        }

        return state
    }

    override fun canConnectRedstone(state: IBlockState, world: IBlockAccess, pos: BlockPos, side: EnumFacing?): Boolean {
        return false
    }

    private fun notifyWireNeighborsOfStateChange(worldIn: World, pos: BlockPos) {
        if (worldIn.getBlockState(pos).block === this) {
            worldIn.notifyNeighborsOfStateChange(pos, this, false)

            for (enumfacing in EnumFacing.values()) {
                worldIn.notifyNeighborsOfStateChange(pos.offset(enumfacing), this, false)
            }
        }
    }

    override fun onBlockAdded(worldIn: World, pos: BlockPos, state: IBlockState) {
        if (!worldIn.isRemote) {
            this.updateSurroundingRedstone(worldIn, pos, state)

            for (enumfacing in EnumFacing.Plane.VERTICAL) {
                worldIn.notifyNeighborsOfStateChange(pos.offset(enumfacing), this, false)
            }

            for (enumfacing1 in EnumFacing.Plane.HORIZONTAL) {
                this.notifyWireNeighborsOfStateChange(worldIn, pos.offset(enumfacing1))
            }

            for (enumfacing2 in EnumFacing.Plane.HORIZONTAL) {
                val blockpos = pos.offset(enumfacing2)

                if (worldIn.getBlockState(blockpos).isNormalCube) {
                    this.notifyWireNeighborsOfStateChange(worldIn, blockpos.up())
                } else {
                    this.notifyWireNeighborsOfStateChange(worldIn, blockpos.down())
                }
            }
        }
    }



    override fun breakBlock(worldIn: World, pos: BlockPos, state: IBlockState) {
        super.breakBlock(worldIn, pos, state)

        if (!worldIn.isRemote) {
            for (enumfacing in EnumFacing.values()) {
                worldIn.notifyNeighborsOfStateChange(pos.offset(enumfacing), this, false)
            }

            this.updateSurroundingRedstone(worldIn, pos, state)

            for (enumfacing1 in EnumFacing.Plane.HORIZONTAL) {
                this.notifyWireNeighborsOfStateChange(worldIn, pos.offset(enumfacing1))
            }

            for (enumfacing2 in EnumFacing.Plane.HORIZONTAL) {
                val blockpos = pos.offset(enumfacing2)

                if (worldIn.getBlockState(blockpos).isNormalCube) {
                    this.notifyWireNeighborsOfStateChange(worldIn, blockpos.up())
                } else {
                    this.notifyWireNeighborsOfStateChange(worldIn, blockpos.down())
                }
            }
        }
    }

    private fun calculateCurrentChanges(worldIn: World, pos1: BlockPos, pos2: BlockPos, state: IBlockState): IBlockState {
        var state = state
        val iblockstate = state
        val i = (state.getValue(POWER) as Int).toInt()
        var j = 0
        j = this.getMaxCurrentStrength(worldIn, pos2, j)
        this.canProvidePower = false
        val k = worldIn.isBlockIndirectlyGettingPowered(pos1)
        this.canProvidePower = true

        if (k > 0 && k > j - 1) {
            j = k
        }

        var l = 0

        for (enumfacing in EnumFacing.Plane.HORIZONTAL) {
            val blockpos = pos1.offset(enumfacing)
            val flag = blockpos.x != pos2.x || blockpos.z != pos2.z

            if (flag) {
                l = this.getMaxCurrentStrength(worldIn, blockpos, l)
            }

            if (worldIn.getBlockState(blockpos).isNormalCube && !worldIn.getBlockState(pos1.up()).isNormalCube) {
                if (flag && pos1.y >= pos2.y) {
                    l = this.getMaxCurrentStrength(worldIn, blockpos.up(), l)
                }
            } else if (!worldIn.getBlockState(blockpos).isNormalCube && flag && pos1.y <= pos2.y) {
                l = this.getMaxCurrentStrength(worldIn, blockpos.down(), l)
            }
        }

        if (l > j) {
            j = l - 1
        } else if (j > 0) {
            --j
        } else {
            j = 0
        }

        if (k > j - 1) {
            j = k
        }

        if (i != j) {
            state = state.withProperty(POWER, Integer.valueOf(j)!!)

            if (worldIn.getBlockState(pos1) === iblockstate) {
                worldIn.setBlockState(pos1, state, 2)
            }

            this.blocksNeedingUpdate.add(pos1)

            for (enumfacing1 in EnumFacing.values()) {
                this.blocksNeedingUpdate.add(pos1.offset(enumfacing1))
            }
        }

        return state
    }

    private fun getMaxCurrentStrength(worldIn: World, pos: BlockPos, strength: Int): Int {
        if (worldIn.getBlockState(pos).block !== this) {
            return strength
        } else {
            val i = (worldIn.getBlockState(pos).getValue(POWER) as Int).toInt()
            return if (i > strength) i else strength
        }
    }

    override fun canProvidePower(state: IBlockState): Boolean {
        return this.canProvidePower
    }

    override fun getStateFromMeta(meta: Int): IBlockState {
        return this.defaultState.withProperty(POWER, Integer.valueOf(meta)!!)
    }

    override fun getStrongPower(blockState: IBlockState, blockAccess: IBlockAccess, pos: BlockPos, side: EnumFacing): Int {
        return if (!this.canProvidePower) 0 else blockState.getWeakPower(blockAccess, pos, side)
    }

    override fun getWeakPower(blockState: IBlockState, blockAccess: IBlockAccess, pos: BlockPos, side: EnumFacing): Int {
        if (!this.canProvidePower) {
            return 0
        } else {
            val i = (blockState.getValue(POWER) as Int).toInt()

            if (i == 0) {
                return 0
            } else if (side == EnumFacing.UP) {
                return i
            } else {
                val enumset = EnumSet.noneOf(EnumFacing::class.java)

                for (enumfacing in EnumFacing.Plane.HORIZONTAL) {
                    if (this.isPowerSourceAt(blockAccess, pos, enumfacing)) {
                        enumset.add(enumfacing)
                    }
                }

                return if (side.axis.isHorizontal && enumset.isEmpty()) {
                    i
                } else if (enumset.contains(side) && !enumset.contains(side.rotateYCCW()) && !enumset.contains(side.rotateY())) {
                    i
                } else {
                    0
                }
            }
        }
    }

    private fun isPowerSourceAt(worldIn: IBlockAccess, pos: BlockPos, side: EnumFacing): Boolean {
        val blockpos = pos.offset(side)
        val iblockstate = worldIn.getBlockState(blockpos)
        val flag = iblockstate.isNormalCube
        val flag1 = worldIn.getBlockState(pos.up()).isNormalCube

        return if (!flag1 && flag && canConnectUpwardsTo(worldIn, blockpos.up())) {
            true
        } else if (canConnectTo(iblockstate, side, worldIn, pos)) {
            true
        } else if (iblockstate.block === Blocks.POWERED_REPEATER && iblockstate.getValue(BlockRedstoneDiode.FACING) == side) {
            true
        } else {
            !flag && canConnectUpwardsTo(worldIn, blockpos.down())
        }
    }

    override fun neighborChanged(state: IBlockState, worldIn: World, pos: BlockPos, blockIn: Block, fromPos: BlockPos) {
        if (!worldIn.isRemote) {
            if (this.canPlaceBlockAt(worldIn, pos)) {
                this.updateSurroundingRedstone(worldIn, pos, state)
            } else {
                this.dropBlockAsItem(worldIn, pos, state, 0)
                worldIn.setBlockToAir(pos)
            }
        }
    }
    @SideOnly(Side.CLIENT)
    fun initModel() {
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), 0, ModelResourceLocation(registryName, "inventory"));
    }
}
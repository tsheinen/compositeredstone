package com.skyshayde.compositeredstone.blocks

import net.minecraft.block.Block
import net.minecraftforge.fml.common.Mod

@Mod.EventBusSubscriber
object ModBlocks {
    val blazeWire: Block
    val sandWire: Block
    val lapisWire: Block
    val glowstoneWire: Block
    init {
        blazeWire = BlockBlazeWire()
        sandWire = BlockSandWire()
        lapisWire = BlockLapisWire()
        glowstoneWire = BlockGlowstoneWire()
    }
}


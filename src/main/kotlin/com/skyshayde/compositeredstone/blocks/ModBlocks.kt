package com.skyshayde.compositeredstone.blocks

import net.minecraft.block.Block
import net.minecraft.item.Item
import net.minecraft.item.ItemBlock
import net.minecraftforge.event.RegistryEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

@Mod.EventBusSubscriber
object ModBlocks {
    val blazeWire: Block
    val sandWire: Block
    val lapisWire: Block

    init {
        blazeWire = BlockBlazeWire()
        sandWire = BlockSandWire()
        lapisWire = BlockLapisWire()

    }

    @JvmStatic
    @SubscribeEvent
    fun registerItems(event: RegistryEvent.Register<Item>) {
        event.registry.register(ItemBlock(blazeWire).setRegistryName(blazeWire.registryName))
        event.registry.register(ItemBlock(sandWire).setRegistryName(sandWire.registryName))
        event.registry.register(ItemBlock(lapisWire).setRegistryName(lapisWire.registryName))
    }
}


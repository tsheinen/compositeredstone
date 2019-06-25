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

    init {
        blazeWire = BlockBlazeWire()
        sandWire = BlockSandWire()

    }

    @JvmStatic
    @SubscribeEvent
    fun registerBlocks(event: RegistryEvent.Register<Block>) {
        event.registry.register(blazeWire)
        event.registry.register(sandWire)
    }


    @JvmStatic
    @SubscribeEvent
    fun registerItems(event: RegistryEvent.Register<Item>) {
        event.registry.register(ItemBlock(blazeWire).setRegistryName(blazeWire.registryName))
        event.registry.register(ItemBlock(sandWire).setRegistryName(sandWire.registryName))
    }
}


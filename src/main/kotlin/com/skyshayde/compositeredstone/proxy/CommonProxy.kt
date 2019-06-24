package com.skyshayde.compositeredstone.proxy

import com.skyshayde.compositeredstone.blocks.ModBlocks
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent

open class CommonProxy {
    open fun preInit(e: FMLPreInitializationEvent) {
        ModBlocks
    }

    open fun init(e: FMLInitializationEvent) {
    }

    open fun postInit(e: FMLPostInitializationEvent) {}
}
package com.skyshayde.compositeredstone

import com.skyshayde.compositeredstone.proxy.CommonProxy
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.SidedProxy
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent

@Mod(modid = CompositeRedstone.MODID, version = CompositeRedstone.VERSION, name = CompositeRedstone.MODNAME, dependencies = CompositeRedstone.DEPENDENCIES, modLanguageAdapter = CompositeRedstone.ADAPTER, acceptedMinecraftVersions = CompositeRedstone.ALLOWED)
object CompositeRedstone {

    @SidedProxy(serverSide = "com.skyshayde.compositeredstone.proxy.ServerProxy",
            clientSide = "com.skyshayde.compositeredstone.proxy.ClientProxy")
    lateinit var proxy: CommonProxy

    @Mod.Instance
    lateinit var instance: CompositeRedstone

    const val MODID = "compositeredstone"
    const val MODNAME = "CompositeRedstone"
    const val MAJOR = "GRADLE:VERSION"
    const val MINOR = "GRADLE:BUILD"
    const val VERSION = "$MAJOR.$MINOR"
    const val ALLOWED = "[1.12,)"
    const val DEPENDENCIES = "required-after:forgelin@[1.8.0,);required-after:forge@[14.23.5.2768,)"
    const val ADAPTER = "net.shadowfacts.forgelin.KotlinAdapter"

    @Mod.EventHandler
    fun preInit(event: FMLPreInitializationEvent) {

        MinecraftForge.EVENT_BUS.register(this)
        proxy.preInit(event)
    }

    @Mod.EventHandler
    fun init(event: FMLInitializationEvent) {
        proxy.init(event)
    }

}
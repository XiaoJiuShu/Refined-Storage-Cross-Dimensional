package com.rscrossdimensional;

import net.minecraft.world.World;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppingEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod(
    modid = RSCrossDimensional.MODID,
    name = RSCrossDimensional.NAME,
    version = RSCrossDimensional.VERSION,
    dependencies = "required-after:mixinbooter;required-after:refinedstorage"
)
public class RSCrossDimensional {
    public static final String MODID = "rs cross dimensional";
    public static final String NAME = "RS Cross Dimensional";
    public static final String VERSION = "1.0";

    @Mod.Instance(MODID)
    public static RSCrossDimensional instance;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        ForgeChunkManager.setForcedChunkLoadingCallback(this, TransmitterChunkManager::releaseLoadedTickets);
        MinecraftForge.EVENT_BUS.register(this);
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
    }

    @EventHandler
    public void serverStopping(FMLServerStoppingEvent event) {
        TransmitterChunkManager.releaseAll();
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onBlockBreak(BlockEvent.BreakEvent event) {
        TransmitterChunkManager.release(event.getWorld(), event.getPos());
    }

    @SubscribeEvent
    public void onWorldUnload(WorldEvent.Unload event) {
        World world = event.getWorld();
        if (!world.isRemote) {
            TransmitterChunkManager.releaseForWorld(world);
        }
    }
}

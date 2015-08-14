package net.fusionlord.cabinets3;

import net.fusionlord.cabinets3.config.Config;
import net.fusionlord.cabinets3.handlers.GuiHandler;
import net.fusionlord.cabinets3.tileentity.CabinetTileEntity;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;

@Mod(modid = Reference.MOD_ID, name = "Cabinets Reloaded", version = Reference.MOD_VERSION, guiFactory = "net.fusionlord.cabinets3.config.GuiFactory", dependencies = "required-after:fusionutil")
public class CabinetsReloaded
{

	@Mod.Instance(Reference.MOD_ID)
	public static CabinetsReloaded instance;

	@SidedProxy(clientSide = "net.fusionlord.cabinets3.client.ClientProxy",
			           serverSide = "net.fusionlord.cabinets3.CommonProxy")
	public static CommonProxy proxy;

	@EventHandler
	public void preInit(FMLPreInitializationEvent event)
	{
		Reference.config = new Config(event.getSuggestedConfigurationFile());
		Reference.init();
	}

	@EventHandler
	public void load(FMLInitializationEvent event)
	{
		proxy.registerRenderers();
		GameRegistry.registerTileEntity(CabinetTileEntity.class, "cabinets3_cabinet");
		NetworkRegistry.INSTANCE.registerGuiHandler(this, new GuiHandler());
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event)
	{
		Reference.addCabinetRecipe();
	}
}
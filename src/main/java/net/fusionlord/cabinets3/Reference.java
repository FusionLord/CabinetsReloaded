package net.fusionlord.cabinets3;

import net.fusionlord.fusionutil.network.PacketHandler;
import net.fusionlord.cabinets3.block.CabinetBlock;
import net.fusionlord.cabinets3.config.Config;
import net.fusionlord.cabinets3.handlers.EventHandler;
import net.fusionlord.cabinets3.packets.CabinetGuiPacket;
import net.fusionlord.cabinets3.packets.CabinetNullifyOwnerPacket;
import net.fusionlord.cabinets3.packets.CabinetSyncPacket;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class Reference
{

	public static final String MOD_ID      = "cabinets3";
	public static final String MOD_VERSION = "version";
	public static PacketHandler packetHandler;
	public static CabinetBlock  cabinet;
	public static Config        config;

	public static boolean showItemsTileEntity = true;
	public static boolean showItemsItem       = true;
	public static int     cabinetYield        = 8;
	public static int     oldCabinetYield     = cabinetYield;

	public static IRecipe currentCabinetRecipe;

	public static void init()
	{
		config.load();
		cabinet = new CabinetBlock();
		packetHandler = new PacketHandler("CabinetsReloaded");
		packetHandler.initialise();
		packetHandler.registerPackets(CabinetGuiPacket.class,
		                             CabinetNullifyOwnerPacket.class,
		                             CabinetSyncPacket.class
		);
		FMLCommonHandler.instance().bus().register(new EventHandler());
	}

	public static void addRecipes()
	{
		addCabinetRecipe();
		GameRegistry.addShapelessRecipe(new ItemStack(cabinet, 1, 1), new ItemStack(cabinet, 1, 0));
		GameRegistry.addShapelessRecipe(new ItemStack(cabinet, 1, 2), new ItemStack(cabinet, 1, 1));
		GameRegistry.addShapelessRecipe(new ItemStack(cabinet, 1), new ItemStack(cabinet, 1, 2));
	}

	public static ResourceLocation getResource(String resource)
	{
		return new ResourceLocation(MOD_ID, resource);
	}

	public static void addCabinetRecipe()
	{
		currentCabinetRecipe = GameRegistry.addShapedRecipe(new ItemStack(cabinet, cabinetYield), "ppp", "pgp", "ppp", 'p',
				new ItemStack(Blocks.planks, 0, 0), 'g', new ItemStack(Blocks.glass));
		oldCabinetYield = cabinetYield;
	}
}

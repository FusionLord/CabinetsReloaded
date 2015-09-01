package net.fusionlord.cabinets3;

import net.fusionlord.cabinets3.block.CabinetBlock;
import net.fusionlord.cabinets3.config.Config;
import net.fusionlord.cabinets3.handlers.EventHandler;
import net.fusionlord.cabinets3.packets.*;
import net.fusionlord.fusionutil.network.PacketHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ForgeModContainer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.ShapedOreRecipe;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Reference
{
	public static final String MOD_ID = "cabinets3";
	public static final String MOD_VERSION = "version";
	public static final Logger logger = LogManager.getLogger(MOD_ID);
	public static PacketHandler packetHandler;
	public static CabinetBlock cabinet;
	public static Config config;
	public static boolean showItemsTileEntity = true;
	//	public static boolean showItemsItem = true;
	public static int cabinetYield = 8;
	public static int oldCabinetYield = cabinetYield;
	public static List<TextureAtlasSprite> SKINS = new ArrayList<>();
	public static IRecipe currentCabinetRecipe;
	public static String[] BLACKLIST;
	public static String[] COLORABLE;
	public static String[] DOUBLERENDER;

	public static void init()
	{
		config.load();
		cabinet = new CabinetBlock();
		packetHandler = new PacketHandler("CabinetsReloaded");
		packetHandler.getHandler().registerMessage(CabinetGuiPacket.Handler.class, CabinetGuiPacket.class, 0, Side.SERVER);
		packetHandler.getHandler().registerMessage(CabinetTextureSyncPacket.Handler.class, CabinetTextureSyncPacket.class, 1, Side.SERVER);
		packetHandler.getHandler().registerMessage(CabinetNullifyOwnerPacket.Handler.class, CabinetNullifyOwnerPacket.class, 2, Side.SERVER);
		packetHandler.getHandler().registerMessage(CabinetSyncPacket.Handler.class, CabinetSyncPacket.class, 3, Side.CLIENT);
		packetHandler.getHandler().registerMessage(CabinetSettingsSyncPacket.Handler.class, CabinetSettingsSyncPacket.class, 4, Side.SERVER);
		packetHandler.getHandler().registerMessage(CabinetAbilityDoActionPacket.Handler.class, CabinetAbilityDoActionPacket.class, 5, Side.SERVER);
		FMLCommonHandler.instance().bus().register(new EventHandler());
		ForgeModContainer.fullBoundingBoxLadders = true;
	}

	@SideOnly(Side.CLIENT)
	public static void loadSkins()
	{
		SKINS.clear();

		try
		{
			TextureMap tm = Minecraft.getMinecraft().getTextureMapBlocks();

			Class clazz = Class.forName(TextureMap.class.getName());
			Field f = ReflectionHelper.findField(clazz, "mapRegisteredSprites", "field_110574_e");
			f.setAccessible(true);
			Field modifiers = Field.class.getDeclaredField("modifiers");
			modifiers.setAccessible(true);
			modifiers.setInt(f, f.getModifiers() & ~Modifier.FINAL);
			Map mapRegisteredSprites = (Map) f.get(tm);

			Iterator iterator = mapRegisteredSprites.entrySet().iterator();
			Map.Entry entry;
			whileloop:
			while (iterator.hasNext())
			{
				entry = (Map.Entry) iterator.next();
				if (entry.getValue() instanceof TextureAtlasSprite)
				{
					TextureAtlasSprite tex = (TextureAtlasSprite) entry.getValue();
					for (String s : BLACKLIST)
					{
						if (tex.getIconName().contains(s))
						{
							continue whileloop;
						}
					}
					if (!SKINS.contains(tex))
					{
						SKINS.add(tex);
					}
				}
			}
			f.setAccessible(false);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		SKINS.sort((o1, o2) -> o1.getIconName().substring(o1.getIconName().lastIndexOf("/") != -1 ? o1.getIconName().lastIndexOf("/") : o1.getIconName().lastIndexOf(":")).toLowerCase().compareTo(o2.getIconName().substring(o2.getIconName().lastIndexOf("/") != -1 ? o2.getIconName().lastIndexOf("/") : o2.getIconName().lastIndexOf(":")).toLowerCase()));
	}

	public static ResourceLocation getResource(String resource)
	{
		return new ResourceLocation(MOD_ID, resource);
	}

	public static void addCabinetRecipe()
	{
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(cabinet, cabinetYield), "ppp", "pgp", "ppp", 'p', Blocks.planks, 'g', Blocks.glass));
	}

	public static boolean isTextureDoubleRendered(String texture)
	{
		for (String s : DOUBLERENDER)
		{
			if (texture.toLowerCase().contains(s))
			{
				return true;
			}
		}
		return false;
	}

	public static List<TextureAtlasSprite> getSkinsForSearch(String text)
	{
		return text.isEmpty() ? SKINS : SKINS.stream().filter(texture -> texture.getIconName().toLowerCase().contains(text.toLowerCase())).collect(Collectors.toList());
	}

	public static void sortTextures(int sortType)
	{
		switch (sortType)
		{
			case 1:
				System.out.println("Sorting by mod!");
				SKINS.sort((o1, o2) -> o1.getIconName().substring(0, o1.getIconName().indexOf(":")).toUpperCase().compareTo(o2.getIconName().substring(0, o2.getIconName().indexOf(":")).toUpperCase()));
				break;
			default:
				SKINS.sort((o1, o2) -> o1.getIconName().substring(o1.getIconName().lastIndexOf("/") != -1 ? o1.getIconName().lastIndexOf("/") : o1.getIconName().lastIndexOf(":")).toLowerCase().compareTo(o2.getIconName().substring(o2.getIconName().lastIndexOf("/") != -1 ? o2.getIconName().lastIndexOf("/") : o2.getIconName().lastIndexOf(":")).toLowerCase()));
				break;
		}
	}

	public static boolean isTextureColorable(String texture)
	{
		for (String s : Reference.COLORABLE)
		{
			if (texture.contains(s))
			{
				return true;
			}
		}
		return false;
	}
}

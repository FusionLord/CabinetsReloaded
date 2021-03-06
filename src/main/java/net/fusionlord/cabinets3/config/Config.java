package net.fusionlord.cabinets3.config;

import net.fusionlord.cabinets3.Reference;
import net.minecraftforge.common.config.Configuration;

import java.io.File;

/**
 * Author: FusionLord
 * Email: FusionLord@gmail.com
 */
public class Config
{
	Configuration config;

	public Config(File configFile)
	{
		config = new Configuration(configFile);
	}

	public void load()
	{
//		Reference.showItemsItem = config.getBoolean("Show contents In Item", Configuration.CATEGORY_GENERAL, Reference.showItemsItem, "Whether to show contents in the item.");
		Reference.showItemsTileEntity = config.getBoolean("Show contents In Cabinet", Configuration.CATEGORY_GENERAL, Reference.showItemsTileEntity, "Whether to show contents in the cabinet.");
		Reference.cabinetYield = config.getInt("Number of cabinet a recipe yields", Configuration.CATEGORY_GENERAL, Reference.cabinetYield, 1, 64, "How many cabinets should the recipe  give you");
		Reference.BLACKLIST = config.getStringList("Terms to blacklist.", Configuration.CATEGORY_GENERAL, new String[] {"_flow", "pane_top", "pane_side", "_stem"}, "Any textures that should be blacklisted.");
		Reference.COLORABLE = config.getStringList("Colorable textures.", Configuration.CATEGORY_GENERAL, new String[] {"vine", "leaves", "lily", "destroy_stage", "reeds"}, "Any textures that need coloring per biome.");
		Reference.DOUBLERENDER = config.getStringList("Render Background.", Configuration.CATEGORY_GENERAL, new String[] {"destroy_stage", "reeds"}, "Any textures that should render over default skin.");

		save();
	}

	public void save()
	{
		if (config.hasChanged())
		{
			config.save();
		}
	}

	public Configuration getConfig()
	{
		return config;
	}
}

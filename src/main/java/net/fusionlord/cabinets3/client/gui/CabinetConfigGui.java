package net.fusionlord.cabinets3.client.gui;

import net.fusionlord.cabinets3.Reference;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.config.GuiConfig;

/**
 * Author: FusionLord
 * Email: FusionLord@gmail.com
 */
public class CabinetConfigGui extends GuiConfig
{
	public CabinetConfigGui(GuiScreen parent)
	{
		super(parent, new ConfigElement(Reference.config.getConfig().getCategory(Configuration.CATEGORY_GENERAL)).getChildElements(), Reference.MOD_ID, false, false, GuiConfig.getAbridgedConfigPath(Reference.config.getConfig().toString()));
	}
}

package net.fusionlord.cabinets3.handlers;

import net.fusionlord.cabinets3.Reference;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.ListIterator;

/**
 * Author: FusionLord
 * Email: FusionLord@gmail.com
 */
public class EventHandler
{
	@SubscribeEvent
	public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent eventArgs)
	{
		if (eventArgs.modID.equals(Reference.MOD_ID))
		{
			Reference.config.load();
			if (Reference.cabinetYield != Reference.oldCabinetYield)
			{
				ListIterator<IRecipe> iterator = CraftingManager.getInstance().getRecipeList().listIterator();
				while (iterator.hasNext())
				{
					IRecipe r = iterator.next();
					if (Reference.currentCabinetRecipe.equals(r))
					{
						iterator.remove();
					}
				}
				Reference.addCabinetRecipe();
			}
		}
	}
}

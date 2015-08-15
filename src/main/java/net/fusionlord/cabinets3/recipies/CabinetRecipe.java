package net.fusionlord.cabinets3.recipies;

import net.fusionlord.cabinets3.Reference;
import net.fusionlord.cabinets3.item.CabinetItem;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;

/**
 * Created by FusionLord on 8/13/2015.
 */
public class CabinetRecipe implements IRecipe
{
	@Override
	public boolean matches(InventoryCrafting inv, World worldIn)
	{
		if (inv.getSizeInventory() == 9)
		{
			boolean matches = true;
			for (int i = 0; i < inv.getSizeInventory(); i++)
			{
				if (!matches)
				{
					return false;
				}
				matches = inv.getStackInSlot(i) != null && inv.getStackInSlot(i).isItemEqual(i == 4 ? new ItemStack(Blocks.glass, 1, 0) : new ItemStack(Blocks.planks, 1, 0));
			}
			return true;
		}
		else
		{
			for (int i = 0; i < inv.getSizeInventory(); i++)
			{
				if (inv.getStackInSlot(i) != null && inv.getStackInSlot(i).getItem() instanceof CabinetItem)
				{
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public ItemStack getCraftingResult(InventoryCrafting inv)
	{
		if (inv.getSizeInventory() == 9)
		{
			boolean matches = true;
			for (int i = 0; i < inv.getSizeInventory(); i++)
			{
				if (!matches)
				{
					break;
				}
				matches = inv.getStackInSlot(i) != null && inv.getStackInSlot(i).isItemEqual(i == 4 ? new ItemStack(Blocks.glass, 1, 0) : new ItemStack(Blocks.planks, 1, 0));
			}
			return new ItemStack(Reference.cabinet, Reference.cabinetYield);
		}

		ItemStack old = null;
		for (int i = 0; i < inv.getSizeInventory(); i++)
		{
			if (inv.getStackInSlot(i) != null && inv.getStackInSlot(i).getItem() instanceof CabinetItem)
			{
				old = inv.getStackInSlot(i);
			}
		}
		if (old != null)
		{
			ItemStack notOld = new ItemStack(Reference.cabinet, 1, Math.abs(old.getMetadata() - 3) - 1);
			notOld.setTagCompound(old.getTagCompound());
			return notOld;
		}

		return null;
	}

	@Override
	public int getRecipeSize()
	{
		return 1;
	}

	@Override
	public ItemStack getRecipeOutput()
	{
		return new ItemStack(Reference.cabinet);
	}

	@Override
	public ItemStack[] getRemainingItems(InventoryCrafting inv)
	{
		ItemStack[] old = new ItemStack[inv.getSizeInventory()];

		if (inv.getSizeInventory() == 9)
		{
			boolean matches = true;
			for (int i = 0; i < inv.getSizeInventory(); i++)
			{
				if (!matches)
				{
					break;
				}
				matches = inv.getStackInSlot(i) != null && inv.getStackInSlot(i).isItemEqual(i == 4 ? new ItemStack(Blocks.glass, 1, 0) : new ItemStack(Blocks.planks, 1, 0));
			}
			if (matches)
			{
				for (int i = 0; i < inv.getSizeInventory(); i++)
				{
					old[i] = inv.getStackInSlot(i);
					old[i].stackSize--;
					if (old[i].stackSize < 1)
					{
						old[i] = null;
					}
				}
			}

		}

		for (int i = 0; i < inv.getSizeInventory(); i++)
		{
			if (inv.getStackInSlot(i) != null && inv.getStackInSlot(i).getItem() instanceof CabinetItem)
			{
				old[i] = inv.getStackInSlot(i);
				old[i].stackSize--;
				if (old[i].stackSize < 1)
				{
					old[i] = null;
				}
			}
		}

		return old;
	}
}

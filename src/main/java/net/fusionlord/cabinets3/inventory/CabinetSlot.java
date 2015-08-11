package net.fusionlord.cabinets3.inventory;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

/**
 * Author: FusionLord
 * Email: FusionLord@gmail.com
 */
public class CabinetSlot extends Slot
{
	public CabinetSlot(IInventory iInventory, int slotIdx, int xPos, int yPos)
	{
		super(iInventory, slotIdx, xPos, yPos);
	}

	@Override
	public boolean isItemValid(ItemStack itemStack)
	{
		return inventory.isItemValidForSlot(slotNumber, itemStack);
	}
}

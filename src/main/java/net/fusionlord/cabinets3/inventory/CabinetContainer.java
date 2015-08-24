package net.fusionlord.cabinets3.inventory;

import net.fusionlord.cabinets3.item.CabinetItem;
import net.fusionlord.cabinets3.tileentity.CabinetTileEntity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class CabinetContainer extends Container
{

	protected CabinetTileEntity cabinet;

	public CabinetContainer(CabinetTileEntity cabinet, InventoryPlayer inventory)
	{
		this.cabinet = cabinet;
		this.cabinet.openInventory(inventory.player);

		int i = 0;
		while (i < cabinet.getSizeInventory() - 2)
		{
			addSlotToContainer(new CabinetSlot(cabinet, i, 8 + ((i % 3) * 18), 18 + (i / 3) * 18));
			i++;
		}
		addSlotToContainer(new CabinetSlot(cabinet, i++, 96, 60));
		addSlotToContainer(new CabinetSlot(cabinet, i, 120, 60));

		bindPlayerInventory(inventory);

	}

	protected void bindPlayerInventory(InventoryPlayer player_inventory)
	{
		for (int var6 = 0; var6 < 3; ++var6)
		{
			for (int var7 = 0; var7 < 9; ++var7)
			{
				this.addSlotToContainer(new Slot(player_inventory, var7 + var6 * 9 + 9, 8 + var7 * 18, 84 + var6 * 18));
			}
		}

		for (int var6 = 0; var6 < 9; ++var6)
		{
			this.addSlotToContainer(new Slot(player_inventory, var6, 8 + var6 * 18, 142));
		}

	}

	public CabinetTileEntity getCabinet()
	{
		return cabinet;
	}

	@Override
	public void onContainerClosed(EntityPlayer player)
	{
		super.onContainerClosed(player);
		this.cabinet.closeInventory(player);
	}

	@Override
	public boolean canInteractWith(EntityPlayer player)
	{
		return cabinet.isUseableByPlayer(player);
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int slotID)
	{
		ItemStack itemStack = null;
		Slot slot = (Slot) this.inventorySlots.get(slotID);

		if (slot != null && slot.getHasStack())
		{
			ItemStack itemStack1 = slot.getStack().copy();
			itemStack = itemStack1.copy();

			if (itemStack.getItem() instanceof CabinetItem)
			{
				return null;
			}

			if (slotID < cabinet.getSizeInventory())
			{
				if (!this.mergeItemStack(itemStack1, cabinet.getSizeInventory(), 45, true))
				{
					return null;
				}
			}
			else if (!this.mergeItemStack(itemStack1, 0, cabinet.getSizeInventory(), false))
			{
				return null;
			}

			if (itemStack1.stackSize == 0)
			{
				slot.putStack(null);
			}
			else
			{
				slot.onSlotChanged();
			}

			if (itemStack1.stackSize == itemStack.stackSize)
			{
				return null;
			}

			slot.onPickupFromSlot(player, itemStack1);
		}

		return itemStack;
	}
}
package net.fusionlord.cabinets3.tileentity.wrappers;

import net.fusionlord.cabinets3.abilities.ChestAbility;
import net.fusionlord.cabinets3.tileentity.CabinetTileEntity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.EnumFacing;

public class TileEntityChestWrapper extends TileEntityChest
{
	/**
	 * Determines if the check for adjacent chests has taken place.
	 */
	public boolean adjacentChestChecked;
	private ItemStack[] chestContents = new ItemStack[27];

	public TileEntityChestWrapper() {}

	/**
	 * Returns the number of slots in the inventory.
	 */
	public int getSizeInventory()
	{
		return 27;
	}

	/**
	 * Returns the stack in slot i
	 */
	public ItemStack getStackInSlot(int index)
	{
		return this.chestContents[index];
	}

	/**
	 * Removes from an inventory slot (first arg) up to a specified number (second arg) of items and returns them in a
	 * new stack.
	 */
	public ItemStack decrStackSize(int index, int count)
	{
		if (this.chestContents[index] != null)
		{
			ItemStack itemstack;

			if (this.chestContents[index].stackSize <= count)
			{
				itemstack = this.chestContents[index];
				this.chestContents[index] = null;
				this.markDirty();
				return itemstack;
			}
			else
			{
				itemstack = this.chestContents[index].splitStack(count);

				if (this.chestContents[index].stackSize == 0)
				{
					this.chestContents[index] = null;
				}

				this.markDirty();
				return itemstack;
			}
		}
		else
		{
			return null;
		}
	}

	/**
	 * When some containers are closed they call this on each slot, then drop whatever it returns as an EntityItem -
	 * like when you close a workbench GUI.
	 */
	public ItemStack getStackInSlotOnClosing(int index)
	{
		if (this.chestContents[index] != null)
		{
			ItemStack itemstack = this.chestContents[index];
			this.chestContents[index] = null;
			return itemstack;
		}
		else
		{
			return null;
		}
	}

	/**
	 * Sets the given item stack to the specified slot in the inventory (can be crafting or armor sections).
	 */
	public void setInventorySlotContents(int index, ItemStack stack)
	{
		this.chestContents[index] = stack;

		if (stack != null && stack.stackSize > this.getInventoryStackLimit())
		{
			stack.stackSize = this.getInventoryStackLimit();
		}

		this.markDirty();
	}

	public void readFromNBT(NBTTagCompound tag)
	{
		NBTTagCompound itemsTag = tag.getCompoundTag("Items");
		chestContents = new ItemStack[this.getSizeInventory()];

		for (int i = 0; i < chestContents.length; ++i)
		{
			NBTTagCompound itemTag = itemsTag.getCompoundTag("slot:".concat(String.valueOf(i)));
			chestContents[i] = ItemStack.loadItemStackFromNBT(itemTag);
		}
	}

	public void writeToNBT(NBTTagCompound tag)
	{
		NBTTagCompound itemsTag = new NBTTagCompound();

		for (int i = 0; i < this.chestContents.length; ++i)
		{
			if (chestContents[i] != null)
			{
				NBTTagCompound itemTag = new NBTTagCompound();
				chestContents[i].writeToNBT(itemTag);
				itemsTag.setTag("slot:".concat(String.valueOf(i)), itemTag);
			}
		}

		tag.setTag("Items", itemsTag);
	}

	/**
	 * Returns the maximum stack size for a inventory slot. Seems to always be 64, possibly will be extended. *Isn't
	 * this more of a set than a get?*
	 */
	public int getInventoryStackLimit()
	{
		return 64;
	}

	/**
	 * Do not make give this method the name canInteractWith because it clashes with Container
	 */
	public boolean isUseableByPlayer(EntityPlayer player)
	{
		return true;
	}

	public void updateContainingBlockInfo()
	{
		this.adjacentChestChecked = false;
	}

	/**
	 * Performs the check for adjacent chests to determine if this chest is double or not.
	 */
	public void checkForAdjacentChests() {}

	protected TileEntityChest getAdjacentChest(EnumFacing side)
	{
		CabinetTileEntity cabinet = (CabinetTileEntity) worldObj.getTileEntity(getPos());
		if (cabinet.getSecAbilityStack() != null && (cabinet.getSecAbilityStack().getItem() == Item.getItemFromBlock(Blocks.chest) || cabinet.getSecAbilityStack().getItem() == Item.getItemFromBlock(Blocks.trapped_chest)))
		{
			return ((ChestAbility) cabinet.getAbility()).getChest2();
		}

		return null;
	}

	/**
	 * Updates the JList with a new model.
	 */
	public void update() {}

	public boolean receiveClientEvent(int id, int type) { return true; }

	public void openInventory(EntityPlayer player) {}

	public void closeInventory(EntityPlayer player) {}

	/**
	 * Returns true if automation is allowed to insert the given stack (ignoring stack size) into the given slot.
	 */
	public boolean isItemValidForSlot(int index, ItemStack stack)
	{
		return true;
	}

	/**
	 * invalidates a tile entity
	 */
	public void invalidate()
	{
		super.invalidate();
		this.checkForAdjacentChests();
	}

	public int getChestType()
	{
		return -1;
	}

	public String getGuiID()
	{
		return "minecraft:chest";
	}

	public Container createContainer(InventoryPlayer playerInventory, EntityPlayer playerIn)
	{
		return new ContainerChest(playerInventory, this, playerIn);
	}

	public int getField(int id)
	{
		return 0;
	}

	public void setField(int id, int value) {}

	public int getFieldCount()
	{
		return 0;
	}

	public void clear()
	{
		for (int i = 0; i < this.chestContents.length; ++i)
		{
			this.chestContents[i] = null;
		}
	}
}
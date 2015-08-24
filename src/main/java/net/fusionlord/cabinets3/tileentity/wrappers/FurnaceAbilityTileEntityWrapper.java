package net.fusionlord.cabinets3.tileentity.wrappers;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerFurnace;
import net.minecraft.inventory.SlotFurnaceFuel;
import net.minecraft.item.*;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;

public class FurnaceAbilityTileEntityWrapper extends TileEntityFurnace
{
	/**
	 * The ItemStacks that hold the items currently being used in the furnace
	 */
	private ItemStack[] furnaceItems = new ItemStack[3];
	/**
	 * The number of ticks that the furnace will keep burning
	 */
	private int burnTime;
	/**
	 * The number of ticks that a fresh copy of the currently-burning item would keep the furnace burning for
	 */
	private int currentBurnTime;
	private int timeCooked;
	private int timeToCook;

	/**
	 * Returns the number of ticks that the supplied fuel item will keep the furnace burning, or 0 if the item isn't
	 * fuel
	 */
	public static int getItemBurnTime(ItemStack p_145952_0_)
	{
		if (p_145952_0_ == null)
		{
			return 0;
		}
		else
		{
			Item item = p_145952_0_.getItem();

			if (item instanceof ItemBlock && Block.getBlockFromItem(item) != Blocks.air)
			{
				Block block = Block.getBlockFromItem(item);

				if (block == Blocks.wooden_slab)
				{
					return 150;
				}

				if (block.getMaterial() == Material.wood)
				{
					return 300;
				}

				if (block == Blocks.coal_block)
				{
					return 16000;
				}
			}

			if (item instanceof ItemTool && ((ItemTool) item).getToolMaterialName().equals("WOOD"))
			{
				return 200;
			}
			if (item instanceof ItemSword && ((ItemSword) item).getToolMaterialName().equals("WOOD"))
			{
				return 200;
			}
			if (item instanceof ItemHoe && ((ItemHoe) item).getMaterialName().equals("WOOD"))
			{
				return 200;
			}
			if (item == Items.stick)
			{
				return 100;
			}
			if (item == Items.coal)
			{
				return 1600;
			}
			if (item == Items.lava_bucket)
			{
				return 20000;
			}
			if (item == Item.getItemFromBlock(Blocks.sapling))
			{
				return 100;
			}
			if (item == Items.blaze_rod)
			{
				return 2400;
			}
			return net.minecraftforge.fml.common.registry.GameRegistry.getFuelValue(p_145952_0_);
		}
	}

	public static boolean isItemFuel(ItemStack p_145954_0_)
	{
		/**
		 * Returns the number of ticks that the supplied fuel item will keep the furnace burning, or 0 if the item isn't
		 * fuel
		 */
		return getItemBurnTime(p_145954_0_) > 0;
	}

	/**
	 * Returns the number of slots in the inventory.
	 */
	@Override
	public int getSizeInventory()
	{
		return this.furnaceItems.length;
	}

	/**
	 * Returns the stack in slot i
	 */
	@Override
	public ItemStack getStackInSlot(int index)
	{
		return this.furnaceItems[index];
	}

	/**
	 * Removes from an inventory slot (first arg) up to a specified number (second arg) of items and returns them in a
	 * new stack.
	 */
	@Override
	public ItemStack decrStackSize(int index, int count)
	{
		if (this.furnaceItems[index] != null)
		{
			ItemStack itemstack;

			if (this.furnaceItems[index].stackSize <= count)
			{
				itemstack = this.furnaceItems[index];
				this.furnaceItems[index] = null;
				return itemstack;
			}
			else
			{
				itemstack = this.furnaceItems[index].splitStack(count);

				if (this.furnaceItems[index].stackSize == 0)
				{
					this.furnaceItems[index] = null;
				}

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
	@Override
	public ItemStack getStackInSlotOnClosing(int index)
	{
		if (this.furnaceItems[index] != null)
		{
			ItemStack itemstack = this.furnaceItems[index];
			this.furnaceItems[index] = null;
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
	@Override
	public void setInventorySlotContents(int index, ItemStack stack)
	{
		boolean flag = stack != null && stack.isItemEqual(this.furnaceItems[index]) && ItemStack.areItemStackTagsEqual(stack, this.furnaceItems[index]);
		this.furnaceItems[index] = stack;

		if (stack != null && stack.stackSize > this.getInventoryStackLimit())
		{
			stack.stackSize = this.getInventoryStackLimit();
		}

		if (index == 0 && !flag)
		{
			this.timeToCook = this.getCookTime(stack);
			this.timeCooked = 0;
			this.markDirty();
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound compound)
	{
		NBTTagList nbttaglist = compound.getTagList("Items", 10);
		this.furnaceItems = new ItemStack[this.getSizeInventory()];

		for (int i = 0; i < nbttaglist.tagCount(); ++i)
		{
			NBTTagCompound nbttagcompound1 = nbttaglist.getCompoundTagAt(i);
			byte b0 = nbttagcompound1.getByte("Slot");

			if (b0 >= 0 && b0 < this.furnaceItems.length)
			{
				this.furnaceItems[b0] = ItemStack.loadItemStackFromNBT(nbttagcompound1);
			}
		}

		this.burnTime = compound.getShort("BurnTime");
		this.timeCooked = compound.getShort("CookTime");
		this.timeToCook = compound.getShort("CookTimeTotal");
		this.currentBurnTime = getItemBurnTime(this.furnaceItems[1]);
	}

	@Override
	public void writeToNBT(NBTTagCompound compound)
	{
		compound.setShort("BurnTime", (short) this.burnTime);
		compound.setShort("CookTime", (short) this.timeCooked);
		compound.setShort("CookTimeTotal", (short) this.timeToCook);
		NBTTagList nbttaglist = new NBTTagList();

		for (int i = 0; i < this.furnaceItems.length; ++i)
		{
			if (this.furnaceItems[i] != null)
			{
				NBTTagCompound nbttagcompound1 = new NBTTagCompound();
				nbttagcompound1.setByte("Slot", (byte) i);
				this.furnaceItems[i].writeToNBT(nbttagcompound1);
				nbttaglist.appendTag(nbttagcompound1);
			}
		}

		compound.setTag("Items", nbttaglist);
	}

	/**
	 * Returns the maximum stack size for a inventory slot. Seems to always be 64, possibly will be extended. *Isn't
	 * this more of a set than a get?*
	 */
	@Override
	public int getInventoryStackLimit()
	{
		return 64;
	}

	/**
	 * Furnace isBurning
	 */
	@Override
	public boolean isBurning()
	{
		return this.burnTime > 0;
	}

	/**
	 * Updates the JList with a new model.
	 */
	@Override
	public void update()
	{
		boolean flag = this.isBurning();
		boolean flag1 = false;

		if (this.isBurning())
		{
			--this.burnTime;
		}

		if (!this.worldObj.isRemote)
		{
			if (!this.isBurning() && (this.furnaceItems[1] == null || this.furnaceItems[0] == null))
			{
				if (!this.isBurning() && this.timeCooked > 0)
				{
					this.timeCooked = MathHelper.clamp_int(this.timeCooked - 2, 0, this.timeToCook);
				}
			}
			else
			{
				if (!this.isBurning() && this.canSmelt())
				{
					this.currentBurnTime = this.burnTime = getItemBurnTime(this.furnaceItems[1]);

					if (this.isBurning())
					{
						flag1 = true;

						if (this.furnaceItems[1] != null)
						{
							--this.furnaceItems[1].stackSize;

							if (this.furnaceItems[1].stackSize == 0)
							{
								this.furnaceItems[1] = furnaceItems[1].getItem().getContainerItem(furnaceItems[1]);
							}
						}
					}
				}

				if (this.isBurning() && this.canSmelt())
				{
					++this.timeCooked;

					if (this.timeCooked == this.timeToCook)
					{
						this.timeCooked = 0;
						this.timeToCook = this.getCookTime(this.furnaceItems[0]);
						this.smeltItem();
						flag1 = true;
					}
				}
				else
				{
					this.timeCooked = 0;
				}
			}

			if (flag != this.isBurning())
			{
				flag1 = true;
			}
		}

		if (flag1)
		{
			this.markDirty();
		}
	}

	@Override
	public int getCookTime(ItemStack stack)
	{
		return 200;
	}

	/**
	 * Returns true if the furnace can smelt an item, i.e. has a source item, destination stack isn't full, etc.
	 */
	private boolean canSmelt()
	{
		if (this.furnaceItems[0] == null)
		{
			return false;
		}
		else
		{
			ItemStack itemstack = FurnaceRecipes.instance().getSmeltingResult(this.furnaceItems[0]);
			if (itemstack == null)
			{
				return false;
			}
			if (this.furnaceItems[2] == null)
			{
				return true;
			}
			if (!this.furnaceItems[2].isItemEqual(itemstack))
			{
				return false;
			}
			int result = furnaceItems[2].stackSize + itemstack.stackSize;
			return result <= getInventoryStackLimit() && result <= this.furnaceItems[2].getMaxStackSize(); //Forge BugFix: Make it respect stack sizes properly.
		}
	}

	/**
	 * Turn one item from the furnace source stack into the appropriate smelted item in the furnace result stack
	 */
	@Override
	public void smeltItem()
	{
		if (this.canSmelt())
		{
			ItemStack itemstack = FurnaceRecipes.instance().getSmeltingResult(this.furnaceItems[0]);

			if (this.furnaceItems[2] == null)
			{
				this.furnaceItems[2] = itemstack.copy();
			}
			else if (this.furnaceItems[2].getItem() == itemstack.getItem())
			{
				this.furnaceItems[2].stackSize += itemstack.stackSize; // Forge BugFix: Results may have multiple items
			}

			if (this.furnaceItems[0].getItem() == Item.getItemFromBlock(Blocks.sponge) && this.furnaceItems[0].getMetadata() == 1 && this.furnaceItems[1] != null && this.furnaceItems[1].getItem() == Items.bucket)
			{
				this.furnaceItems[1] = new ItemStack(Items.water_bucket);
			}

			--this.furnaceItems[0].stackSize;

			if (this.furnaceItems[0].stackSize <= 0)
			{
				this.furnaceItems[0] = null;
			}
		}
	}

	/**
	 * Do not make give this method the name canInteractWith because it clashes with Container
	 */
	@Override
	public boolean isUseableByPlayer(EntityPlayer player)
	{
		return true;
	}

	@Override
	public void openInventory(EntityPlayer player) {}

	@Override
	public void closeInventory(EntityPlayer player) {}

	/**
	 * Returns true if automation is allowed to insert the given stack (ignoring stack size) into the given slot.
	 */
	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack)
	{
		return index != 2 && (index != 1 || isItemFuel(stack) || SlotFurnaceFuel.isBucket(stack));
	}

	/**
	 * Returns true if automation can insert the given item in the given slot from the given side. Args: slot, item,
	 * side
	 */
	@Override
	public boolean canInsertItem(int index, ItemStack itemStackIn, EnumFacing direction)
	{
		return this.isItemValidForSlot(index, itemStackIn);
	}

	/**
	 * Returns true if automation can extract the given item in the given slot from the given side. Args: slot, item,
	 * side
	 */
	@Override
	public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction)
	{
		if (direction == EnumFacing.DOWN && index == 1)
		{
			Item item = stack.getItem();

			if (item != Items.water_bucket && item != Items.bucket)
			{
				return false;
			}
		}

		return true;
	}

	@Override
	public String getGuiID()
	{
		return "minecraft:furnace";
	}

	@Override
	public Container createContainer(InventoryPlayer playerInventory, EntityPlayer playerIn)
	{
		return new ContainerFurnace(playerInventory, this);
	}

	@Override
	public int getField(int id)
	{
		switch (id)
		{
			case 0:
				return this.burnTime;
			case 1:
				return this.currentBurnTime;
			case 2:
				return this.timeCooked;
			case 3:
				return this.timeToCook;
			default:
				return 0;
		}
	}

	@Override
	public void setField(int id, int value)
	{
		switch (id)
		{
			case 0:
				this.burnTime = value;
				break;
			case 1:
				this.currentBurnTime = value;
				break;
			case 2:
				this.timeCooked = value;
				break;
			case 3:
				this.timeToCook = value;
		}
	}

	@Override
	public int getFieldCount()
	{
		return 4;
	}

	@Override
	public void clear()
	{
		for (int i = 0; i < this.furnaceItems.length; ++i)
		{
			this.furnaceItems[i] = null;
		}
	}
}
package net.fusionlord.cabinets3.item;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.StatCollector;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

public class CabinetItem extends ItemBlock
{
	public CabinetItem(Block block)
	{
		super(block);
		setMaxDamage(0);
		setHasSubtypes(true);
		setCreativeTab(CreativeTabs.tabDecorations);
	}

	@Override
	public void addInformation(ItemStack itemStack, EntityPlayer player, List list, boolean bool)
	{
		NBTTagCompound silk;

		if (itemStack.hasTagCompound())
		{
			if (itemStack.getTagCompound().hasKey("silktouch"))
			{
				silk = itemStack.getTagCompound().getCompoundTag("silktouch");
				ItemStack displayStack = ItemStack.loadItemStackFromNBT(silk.getCompoundTag("displayStack"));
				if (displayStack == null)
				{
					displayStack = new ItemStack(Blocks.planks);
				}
				add(list, "Owner: %s", silk.getString("ownerName"));
				add(list, "Public: %s", !silk.getBoolean("locked"));
				add(list, "Hidden: %s", silk.getBoolean("hidden"));
				add(list, "Skin: %s", displayStack.getDisplayName());
				if (silk.hasKey("inv"))
				{
					NBTTagCompound inv = silk.getCompoundTag("inv");
					add(list, "Contains:");
					for (int i = 0; i < 9; i++)
					{
						ItemStack stack = ItemStack.loadItemStackFromNBT(inv.getCompoundTag("slot".concat(String.valueOf(i))));
						if (stack != null)
						{
							add(list, "  %sx %s", stack.stackSize, stack.getDisplayName());
						}
					}
				}
			}
		}
	}

	private void add(List list, String s, Object... objects)
	{
		list.add(String.format(s, objects));
	}

	@Override
	public int getMetadata(int damage)
	{
		return damage;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public String getUnlocalizedName(ItemStack itemStack)
	{
		int meta = itemStack.getItemDamage();
		String name = "";
		switch (meta)
		{
			case 0:
				name = "left";
				break;
			case 1:
				name = "right";
				break;
			case 2:
				name = "double";
				break;
		}
		return String.format("%s.cabinet", name);
	}

	@Override
	public void getSubItems(Item item, CreativeTabs creativeTabs, List list)
	{
		list.add(new ItemStack(this, 1, 0));
		list.add(new ItemStack(this, 1, 1));
		list.add(new ItemStack(this, 1, 2));
	}

	@Override
	public String getItemStackDisplayName(ItemStack itemStack)
	{
		return StatCollector.translateToLocal(String.format("item.%s.name", getUnlocalizedName(itemStack)));
	}
}

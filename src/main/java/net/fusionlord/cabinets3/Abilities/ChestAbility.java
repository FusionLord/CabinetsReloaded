package net.fusionlord.cabinets3.abilities;

import net.fusionlord.cabinets3.tileentity.CabinetTileEntity;
import net.fusionlord.cabinets3.tileentity.wrappers.TileEntityChestWrapper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.world.World;

/**
 * Created by FusionLord on 8/20/2015.
 */
public class ChestAbility extends Ability
{
	TileEntityChestWrapper chest, chest2;

	public ChestAbility()
	{
		super("Storage", true);
		this.chest = new TileEntityChestWrapper();
		this.chest2 = new TileEntityChestWrapper();
	}

	public TileEntityChest getChest2()
	{
		return chest2;
	}

	@Override
	public void doAction(CabinetTileEntity cabinet, EntityPlayer player)
	{
		player.displayGUIChest(chest);
	}

	@Override
	public void update() {}

	@Override
	public void writeNBT(NBTTagCompound tag)
	{
		NBTTagCompound chestTag = new NBTTagCompound();
		chest.writeToNBT(chestTag);
		tag.setTag("chest1", chestTag);
		chestTag = new NBTTagCompound();
		chest2.writeToNBT(chestTag);
		tag.setTag("chest2", chestTag);
	}

	@Override
	public void readNBT(NBTTagCompound tag)
	{
		NBTTagCompound chestTag = tag.getCompoundTag("chest1");
		chest.readFromNBT(chestTag);
		chestTag = tag.getCompoundTag("chest2");
		chest2.readFromNBT(chestTag);
	}

	@Override
	public World getWorld() {return null;}

	@Override
	public void setWorld(World world) {}

	@Override
	public String getTagName() {return "storage";}
}

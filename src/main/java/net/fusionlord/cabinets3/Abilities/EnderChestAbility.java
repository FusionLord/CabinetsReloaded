package net.fusionlord.cabinets3.abilities;

import net.fusionlord.cabinets3.tileentity.CabinetTileEntity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

/**
 * Created by FusionLord on 8/18/2015.
 */
public class EnderChestAbility extends Ability
{
	public EnderChestAbility()
	{
		super("EnderChest", true);
	}

	@Override
	public void doAction(CabinetTileEntity cabinet, EntityPlayer player)
	{
		player.displayGUIChest(player.getInventoryEnderChest());
	}

	@Override
	public void update() {}

	@Override
	public void writeNBT(NBTTagCompound tag) {}

	@Override
	public void readNBT(NBTTagCompound tag) {}

	@Override
	public World getWorld() {return null;}

	@Override
	public void setWorld(World world) {}

	@Override
	public String getTagName()
	{
		return null;
	}
}

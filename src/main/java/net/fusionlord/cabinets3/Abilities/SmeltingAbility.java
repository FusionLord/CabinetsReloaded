package net.fusionlord.cabinets3.abilities;

import net.fusionlord.cabinets3.CabinetsReloaded;
import net.fusionlord.cabinets3.tileentity.CabinetTileEntity;
import net.fusionlord.cabinets3.tileentity.wrappers.FurnaceAbilityTileEntityWrapper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

/**
 * Created by FusionLord on 8/18/2015.
 */
public class SmeltingAbility extends Ability
{
	FurnaceAbilityTileEntityWrapper furnace = new FurnaceAbilityTileEntityWrapper();

	public SmeltingAbility()
	{
		super("Smelt", true, true);
		furnace = new FurnaceAbilityTileEntityWrapper();
	}

	@Override
	public void doAction(CabinetTileEntity cabinet, EntityPlayer player)
	{
		player.openGui(CabinetsReloaded.instance, 4, cabinet.getWorld(), cabinet.getPos().getX(), cabinet.getPos().getY(), cabinet.getPos().getZ());
	}

	@Override
	public void update()
	{
		furnace.update();
	}

	@Override
	public void writeNBT(NBTTagCompound tag)
	{
		furnace.writeToNBT(tag);
	}

	@Override
	public void readNBT(NBTTagCompound tag)
	{
		furnace.readFromNBT(tag);
	}

	@Override
	public World getWorld()
	{
		return furnace.getWorld();
	}

	@Override
	public void setWorld(World world)
	{
		furnace.setWorldObj(world);
	}

	@Override
	public String getTagName()
	{
		return "furnace";
	}

	public FurnaceAbilityTileEntityWrapper getFurnace()
	{
		return furnace;
	}
}

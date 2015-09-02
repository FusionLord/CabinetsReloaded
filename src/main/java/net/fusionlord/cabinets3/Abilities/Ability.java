package net.fusionlord.cabinets3.abilities;

import net.fusionlord.cabinets3.tileentity.CabinetTileEntity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

/**
 * Created by FusionLord on 8/18/2015.
 */
public abstract class Ability
{

	boolean needsWorld;
	String abilityName = "No Ability";
	boolean hasAction;

	public Ability() {}

	public Ability(String abilityName) { this(abilityName, true); }

	public Ability(String abilityName, boolean hasAction)
	{
		this(abilityName, hasAction, false);
	}

	public Ability(String abilityName, boolean hasAction, boolean needsWorld)
	{
		this.abilityName = abilityName;
		this.hasAction = hasAction;
		this.needsWorld = needsWorld;
	}

	public abstract void doAction(CabinetTileEntity cabinet, EntityPlayer player);

	public abstract void update();

	public abstract void writeNBT(NBTTagCompound tag);

	public abstract void readNBT(NBTTagCompound tag);

	public abstract World getWorld();

	public abstract void setWorld(World world);

	public abstract String getTagName();

	public String getAbilityName()
	{
		return abilityName;
	}

	public boolean hasAction()
	{
		return hasAction;
	}

	public boolean needsWorld()
	{
		return needsWorld;
	}
}

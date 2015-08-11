package net.fusionlord.cabinets3.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ContainerWorkbench;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

/**
 * Author: FusionLord
 * Email: FusionLord@gmail.com
 */
public class ContainerWorkbenchWrapper extends ContainerWorkbench
{
	BlockPos pos;

	public ContainerWorkbenchWrapper(InventoryPlayer inventory, World world, BlockPos pos)
	{
		super(inventory, world, pos);
		this.pos = pos;
	}

	@Override
	public boolean canInteractWith(EntityPlayer player)
	{
		return player.getDistanceSq(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D) <= 64.0D;
	}
}

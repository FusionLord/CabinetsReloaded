package net.fusionlord.cabinets3.handlers;

import net.fusionlord.cabinets3.abilities.SmeltingAbility;
import net.fusionlord.cabinets3.client.gui.CabinetGui;
import net.fusionlord.cabinets3.client.gui.CabinetSettingsGui;
import net.fusionlord.cabinets3.client.gui.CabinetSkinSelectionGui;
import net.fusionlord.cabinets3.inventory.CabinetContainer;
import net.fusionlord.cabinets3.inventory.ContainerWorkbenchWrapper;
import net.fusionlord.cabinets3.tileentity.CabinetTileEntity;
import net.minecraft.client.gui.inventory.GuiCrafting;
import net.minecraft.client.gui.inventory.GuiFurnace;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

public class GuiHandler implements IGuiHandler
{
	@Override
	public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z)
	{
		CabinetTileEntity cabinet = (CabinetTileEntity) world.getTileEntity(new BlockPos(x, y, z));

		switch (id)
		{
			case 0:
				return new CabinetContainer(cabinet, player.inventory);
			case 1:
				return new ContainerWorkbenchWrapper(player.inventory, world, new BlockPos(x, y, z));
			case 2:
			case 3:
				break;
			case 4:
				return ((SmeltingAbility) cabinet.getAbility()).getFurnace().createContainer(player.inventory, player);
		}
		return null;
	}

	@Override
	public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z)
	{
		CabinetTileEntity cabinet = (CabinetTileEntity) world.getTileEntity(new BlockPos(x, y, z));
		switch (id)
		{
			case 0:
				return new CabinetGui(cabinet, player);
			case 1:
				return new GuiCrafting(player.inventory, world, new BlockPos(x, y, z));
			case 2:
				return new CabinetSkinSelectionGui(player, cabinet);
			case 3:
				return new CabinetSettingsGui(player, cabinet);
			case 4:
				return new GuiFurnace(player.inventory, ((SmeltingAbility) cabinet.getAbility()).getFurnace());
		}
		return null;
	}
}

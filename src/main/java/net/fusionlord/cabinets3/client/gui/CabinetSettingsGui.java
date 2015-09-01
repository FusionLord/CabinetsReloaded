package net.fusionlord.cabinets3.client.gui;

import net.fusionlord.cabinets3.CabinetsReloaded;
import net.fusionlord.cabinets3.Reference;
import net.fusionlord.cabinets3.client.renderer.RenderingReference;
import net.fusionlord.cabinets3.packets.CabinetNullifyOwnerPacket;
import net.fusionlord.cabinets3.packets.CabinetSettingsSyncPacket;
import net.fusionlord.cabinets3.tileentity.CabinetTileEntity;
import net.fusionlord.fusionutil.client.dynamics.DynGUIScreen;
import net.fusionlord.fusionutil.client.dynamics.elements.ButtonGuiElement;
import net.fusionlord.fusionutil.client.dynamics.elements.IGuiElement;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumFacing;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by FusionLord on 8/16/2015.
 */
public class CabinetSettingsGui extends DynGUIScreen
{
	private ButtonGuiElement unclaim, show_hide, public_private, facing, vertFacing, publicSkin, doorType, back;
	private CabinetTileEntity cabinet;

	public CabinetSettingsGui(EntityPlayer player, CabinetTileEntity cabinet)
	{
		super(player, 10, 10);
		this.cabinet = cabinet;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void addInitialElements(List<IGuiElement> elements)
	{
		int bWidth = 110;
		int bHeight = 20;
		int posX = 5;
		int posY = 5;

		elements.add(unclaim = new ButtonGuiElement(0, posX, posY, bWidth, bHeight, "Unclaim", true, true));
		elements.add(show_hide = new ButtonGuiElement(0, posX, posY += bHeight, bWidth, bHeight, String.format("Items: %s", !cabinet.isHidden() ? "Shown" : "Hidden"), true, true));
		elements.add(public_private = new ButtonGuiElement(0, posX, posY += bHeight, bWidth, bHeight, String.format("Access: %s", cabinet.isLocked() ? "Public" : "Private"), true, true));
		elements.add(facing = new ButtonGuiElement(0, posX, posY += bHeight, bWidth, bHeight, String.format("Yaw: %s", cabinet.getFacing().getOpposite().name()), true, true));
		elements.add(vertFacing = new ButtonGuiElement(0, posX, posY += bHeight, bWidth, bHeight, String.format("Pitch: %s", cabinet.getVerticalFacing() == EnumFacing.NORTH ? "flat" : cabinet.getVerticalFacing().name()), true, true));
		elements.add(publicSkin = new ButtonGuiElement(0, posX, posY += bHeight, bWidth, bHeight, String.format("Skinning: %s", cabinet.isSkinningPublic() ? "Public" : "Private"), true, true));
		elements.add(doorType = new ButtonGuiElement(0, posX, posY += bHeight, bWidth, bHeight, String.format("Door type: %s", cabinet.getDoorType().name()), true, true));
		elements.add(back = new ButtonGuiElement(0, posX, posY + bHeight, bWidth, bHeight, "Back", true, true));

		buttonList.addAll(elements.stream().filter(element -> element instanceof ButtonGuiElement).map(element -> ((ButtonGuiElement) element).getButton()).collect(Collectors.toList()));
	}

	@Override
	public void updateScreen()
	{
		super.updateScreen();

		show_hide.getButton().displayString = String.format("Items: %s", !cabinet.isHidden() ? "Shown" : "Hidden");
		public_private.getButton().displayString = String.format("Access: %s", !cabinet.isLocked() ? "Public" : "Private");
		facing.getButton().displayString = String.format("Yaw: %s", RenderingReference.capitalize(cabinet.getFacing().getOpposite().name()));
		vertFacing.getButton().displayString = String.format("Pitch: %s", cabinet.getVerticalFacing() == EnumFacing.NORTH ? "Flat" : RenderingReference.capitalize(cabinet.getVerticalFacing().name()));
		publicSkin.getButton().displayString = String.format("Skinning: %s", cabinet.isSkinningPublic() ? "Public" : "Private");
		doorType.getButton().displayString = String.format("Door type: %s", RenderingReference.capitalize(cabinet.getDoorType().name()));
	}

	@Override
	protected void actionPerformed(GuiButton button) throws IOException
	{

		if (button == unclaim.getButton())
		{
			if (cabinet.getBlocked())
			{
				player.addChatComponentMessage(new ChatComponentText("You're cabinet is in-accessible, please rotate it."));
			}
			else
			{
				Reference.packetHandler.sendToServer(new CabinetNullifyOwnerPacket(cabinet));
				player.closeScreen();
			}
		}
		if (button == show_hide.getButton())
		{
			cabinet.setHidden(!cabinet.isHidden());
		}
		if (button == public_private.getButton())
		{
			cabinet.setLocked(!cabinet.isLocked());
		}
		if (button == facing.getButton())
		{
			cabinet.setFacing(cabinet.getFacing().rotateY());
		}
		if (button == vertFacing.getButton())
		{
			if (cabinet.getVerticalFacing() == EnumFacing.DOWN)
			{
				cabinet.setVerticalFacing(EnumFacing.NORTH);
			}
			else if (cabinet.getVerticalFacing() == EnumFacing.NORTH)
			{
				cabinet.setVerticalFacing(EnumFacing.UP);
			}
			else if (cabinet.getVerticalFacing() == EnumFacing.UP)
			{
				cabinet.setVerticalFacing(EnumFacing.DOWN);
			}
		}
		if (button == publicSkin.getButton())
		{
			cabinet.togglePublicSkin();
		}
		if (button == doorType.getButton())
		{
			cabinet.setDoorType(cabinet.getDoorType().next());
		}
		if (button == back.getButton())
		{
			if (cabinet.getBlocked())
			{
				player.addChatComponentMessage(new ChatComponentText("You're cabinet is in-accessible, please rotate it."));
			}
			else
			{
				player.openGui(CabinetsReloaded.instance, 0, cabinet.getWorld(), cabinet.getPos().getX(), cabinet.getPos().getY(), cabinet.getPos().getZ());
			}

		}
		Reference.packetHandler.sendToServer(new CabinetSettingsSyncPacket(cabinet));
	}

	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException
	{
		if (cabinet.getBlocked() && keyCode == 1)
		{
			player.addChatComponentMessage(new ChatComponentText("You're cabinet is in-accessible, please rotate it."));
		}
		else
		{
			super.keyTyped(typedChar, keyCode);
		}
	}

	@Override
	public boolean doesGuiPauseGame()
	{
		return false;
	}

}

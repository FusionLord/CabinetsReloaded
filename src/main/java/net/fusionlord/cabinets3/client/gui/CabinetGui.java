package net.fusionlord.cabinets3.client.gui;

import net.fusionlord.cabinets3.CabinetsReloaded;
import net.fusionlord.cabinets3.Reference;
import net.fusionlord.cabinets3.inventory.CabinetContainer;
import net.fusionlord.cabinets3.packets.CabinetGuiPacket;
import net.fusionlord.cabinets3.tileentity.CabinetTileEntity;
import net.fusionlord.fusionutil.client.dynamics.DynGUIContainer;
import net.fusionlord.fusionutil.client.dynamics.elements.ButtonGuiElement;
import net.fusionlord.fusionutil.client.dynamics.elements.IGuiElement;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.StatCollector;

import java.util.List;

public class CabinetGui extends DynGUIContainer<CabinetContainer>
{
	private GuiButton button0;
	private GuiButton button1;
	private GuiButton button2;
	private GuiButton button3;
	private EntityPlayer player;
	private CabinetTileEntity cabinet;

	public CabinetGui(CabinetTileEntity cabinet, EntityPlayer player)
	{
		super(new CabinetContainer(cabinet, player.inventory));

		if (cabinet == null)
		{
			player.closeScreen();
			return;
		}

		this.cabinet = cabinet;
		this.player = player;
		sync();
	}

	@Override
	public void addInitialElements(List<IGuiElement> elements)
	{
		int posy = (height - ySize) / 2 - 13;

		button0 = new ButtonGuiElement(0, width / 2 + 24, posy + 28, 55, 14, "Unclaim", elements, buttonList);
		button1 = new ButtonGuiElement(1, width / 2 + 24, posy + 42, 55, 14, cabinet.isHidden() ? "Show" : "Hide", elements, buttonList);
		button2 = new ButtonGuiElement(2, width / 2 + 24, posy + 56, 55, 14, cabinet.isLocked() ? "Public" : "Private", elements, buttonList);
		button3 = new ButtonGuiElement(3, width / 2 + 24, posy + 70, 55, 14, "Skin", elements, buttonList);

		boolean enabled = cabinet.getOwner() != null && cabinet.getOwner().equals(player.getPersistentID());
		button0.enabled = enabled;
		button1.enabled = enabled;
		button2.enabled = enabled;
		button3.enabled = enabled;
	}


	@Override
	public void onGuiClosed()
	{
		sync();
		super.onGuiClosed();
	}

	protected void actionPerformed(GuiButton button)
	{
		if (button == button0)
		{
			cabinet.setOwner(null);
			player.closeScreen();
		}
		else if (button == button1)
		{
			cabinet.setHidden(!cabinet.isHidden());
		}
		else if (button == button2)
		{
			cabinet.setLocked(!cabinet.isLocked());
		}
		else if (button == button3)
		{
			player.openGui(CabinetsReloaded.instance, 2, player.worldObj, cabinet.getPos().getX(), cabinet.getPos().getY(), cabinet.getPos().getZ());
		}
		sync();
	}

	private void sync()
	{
		Reference.packetHandler.sendToServer(new CabinetGuiPacket(cabinet));
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int i, int j)
	{
		super.drawGuiContainerForegroundLayer(i, j);
		String name = String.format("%s's Cabinet", cabinet.getOwnerName());
		fontRendererObj.drawString(name, 6, 5, 0x000000);
		fontRendererObj.drawString(StatCollector.translateToLocal("container.inventory"), 6, ySize - 94, 0x000000);
	}

	@Override
	public void updateScreen()
	{
		super.updateScreen();

		button1.displayString = cabinet.isHidden() ? "Show" : "Hide";
		button2.displayString = cabinet.isLocked() ? "Public" : "Private";

		boolean enabled = cabinet.getOwner() != null && cabinet.getOwner().equals(player.getPersistentID());
		if (button0.enabled != enabled)
		{
			button0.enabled = enabled;
		}
		if (button1.enabled != enabled)
		{
			button1.enabled = enabled;
		}
		if (button2.enabled != enabled)
		{
			button2.enabled = enabled;
		}
		if (button3.enabled != enabled)
		{
			button3.enabled = enabled;
		}
	}
}
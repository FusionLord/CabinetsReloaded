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

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class CabinetGui extends DynGUIContainer<CabinetContainer>
{
	private ButtonGuiElement unclaim, show_hide, public_private, skins;
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

	@SuppressWarnings("unchecked")
	@Override
	public void addInitialElements(List<IGuiElement> elements)
	{
		int bwidth = 60;
		int bheight = 60 / 4;
		int posX = 167 - 60;
		int posy = 15;

		buttonList.add(unclaim = new ButtonGuiElement(buttonList.size(), posX, posy, bwidth, bheight, "Unclaim", true, true));
		buttonList.add(show_hide = new ButtonGuiElement(buttonList.size(), posX, posy += bheight, bwidth, bheight, cabinet.isHidden() ? "Show" : "Hide", true, true));
		buttonList.add(public_private = new ButtonGuiElement(buttonList.size(), posX, posy += bheight, bwidth, bheight, cabinet.isLocked() ? "Public" : "Private", true, true));
		buttonList.add(skins = new ButtonGuiElement(buttonList.size(), posX, posy + bheight, bwidth, bheight, "Skin", true, true));

		elements.addAll((Collection<? extends IGuiElement>) buttonList.stream().filter(o -> o instanceof IGuiElement).map(o -> o).collect(Collectors.toList()));

		boolean enabled = cabinet.getOwner() != null && cabinet.getOwner().equals(player.getPersistentID());
		unclaim.enabled = enabled;
		show_hide.enabled = enabled;
		public_private.enabled = enabled;
		skins.enabled = enabled;
	}


	@Override
	public void onGuiClosed()
	{
		sync();
		super.onGuiClosed();
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

		show_hide.displayString = cabinet.isHidden() ? "Show" : "Hide";
		public_private.displayString = cabinet.isLocked() ? "Public" : "Private";

		boolean enabled = cabinet.getOwner() != null && cabinet.getOwner().equals(player.getPersistentID());
		if (unclaim.enabled != enabled)
		{
			unclaim.enabled = enabled;
		}
		if (show_hide.enabled != enabled)
		{
			show_hide.enabled = enabled;
		}
		if (public_private.enabled != enabled)
		{
			public_private.enabled = enabled;
		}
		if (skins.enabled != enabled)
		{
			skins.enabled = enabled;
		}
	}

	@Override
	public void actionPerformed(GuiButton button)
	{
		if (button == unclaim)
		{
			cabinet.setOwner(null);
			player.closeScreen();
		}
		else if (button == show_hide)
		{
			cabinet.setHidden(!cabinet.isHidden());
		}
		else if (button == public_private)
		{
			cabinet.setLocked(!cabinet.isLocked());
		}
		else if (button == skins)
		{
			player.openGui(CabinetsReloaded.instance, 2, player.worldObj, cabinet.getPos().getX(), cabinet.getPos().getY(), cabinet.getPos().getZ());
		}
		sync();
	}
}
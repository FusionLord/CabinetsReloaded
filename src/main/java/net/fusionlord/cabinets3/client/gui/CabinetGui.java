package net.fusionlord.cabinets3.client.gui;

import net.fusionlord.cabinets3.CabinetsReloaded;
import net.fusionlord.cabinets3.Reference;
import net.fusionlord.cabinets3.inventory.CabinetContainer;
import net.fusionlord.cabinets3.packets.CabinetAbilityDoActionPacket;
import net.fusionlord.cabinets3.tileentity.CabinetTileEntity;
import net.fusionlord.fusionutil.client.dynamics.DynGUIContainer;
import net.fusionlord.fusionutil.client.dynamics.elements.ButtonGuiElement;
import net.fusionlord.fusionutil.client.dynamics.elements.IGuiElement;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.StatCollector;

import java.util.List;
import java.util.stream.Collectors;

public class CabinetGui extends DynGUIContainer<CabinetContainer>
{
	private ButtonGuiElement skins, settings, ability;
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
	}

	@SuppressWarnings("unchecked")
	@Override
	public void addInitialElements(List<IGuiElement> elements)
	{
		int bwidth = 50;
		int bheight = 20;
		int posX = 66;
		int posy = 17;
		elements.add(skins = new ButtonGuiElement(buttonList.size(), posX, posy, bwidth, bheight, "Skin", true, true));
		elements.add(settings = new ButtonGuiElement(buttonList.size(), posX + bwidth, posy, bwidth, bheight, "Settings", true, true));
		elements.add(ability = new ButtonGuiElement(buttonList.size(), posX, posy + bheight, bwidth + bwidth, bheight, "", false, true));

		buttonList.addAll(elements.stream().filter(element -> element instanceof ButtonGuiElement).map(element -> ((ButtonGuiElement) element).getButton()).collect(Collectors.toList()));

		boolean enabled = cabinet.getOwner() == null || cabinet.getOwner().equals(player.getPersistentID());
		settings.getButton().enabled = enabled;
		ability.getButton().enabled = enabled;
		skins.getButton().enabled = enabled || cabinet.isSkinningPublic();
	}

	@Override
	public void updateScreen()
	{
		super.updateScreen();
		if (cabinet != null && ability != null && cabinet.getAbility() != null)
		{
			ability.getButton().enabled = cabinet.getAbility().hasAction();
			ability.getButton().displayString = cabinet.getAbility().getAbilityName();
		}
	}

	@Override
	public void onGuiClosed()
	{
		super.onGuiClosed();
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
	public void actionPerformed(GuiButton button)
	{
		if (button == skins.getButton())
		{
			player.openGui(CabinetsReloaded.instance, 2, player.worldObj, cabinet.getPos().getX(), cabinet.getPos().getY(), cabinet.getPos().getZ());
		}
		else if (button == settings.getButton())
		{
			player.openGui(CabinetsReloaded.instance, 3, player.worldObj, cabinet.getPos().getX(), cabinet.getPos().getY(), cabinet.getPos().getZ());
		}
		else if (button == ability.getButton() && cabinet.getAbility().hasAction())
		{
			Reference.packetHandler.sendToServer(new CabinetAbilityDoActionPacket(cabinet));
		}
	}
}
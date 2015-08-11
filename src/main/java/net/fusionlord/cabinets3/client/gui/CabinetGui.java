package net.fusionlord.cabinets3.client.gui;

import net.fusionlord.cabinets3.Reference;
import net.fusionlord.cabinets3.inventory.CabinetContainer;
import net.fusionlord.cabinets3.packets.CabinetGuiPacket;
import net.fusionlord.cabinets3.packets.CabinetNullifyOwnerPacket;
import net.fusionlord.cabinets3.tileentity.CabinetTileEntity;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import org.lwjgl.opengl.GL11;

public class CabinetGui extends GuiContainer
{
	private GuiButton button0;
	private GuiButton button1;
	private GuiButton button2;
	private EntityPlayer player;
	private ResourceLocation texture;
	private CabinetTileEntity cabinet;

	public CabinetGui(CabinetTileEntity cabinet, EntityPlayer player)
	{
		super(new CabinetContainer(cabinet, player.inventory));

		this.cabinet = cabinet;
		this.player = player;
		sync();
	}

	@Override
	public void initGui()
	{
		if (cabinet == null)
		{
			player.closeScreen();
			return;
		}
		super.initGui();
		texture = Reference.getResource("textures/gui/cabinetgui.png");
		int posy = (height - ySize) / 2 - 13;

		button0 = new GuiButton(0, width / 2 + 24, posy + 28, 55, 18, "Unclaim");
		button1 = new GuiButton(1, width / 2 + 24, posy + 46, 55, 18, cabinet.isHidden() ? "Show" : "Hide");
		button2 = new GuiButton(2, width / 2 + 24, posy + 64, 55, 18, cabinet.isLocked() ? "Public" : "Private");

		boolean enabled = cabinet.getOwner() != null && cabinet.getOwner().equals(player.getPersistentID());
		button0.enabled = enabled;
		button1.enabled = enabled;
		button2.enabled = enabled;

		buttonList.add(button0);
		buttonList.add(button1);
		buttonList.add(button2);
	}

	@Override
	public void onGuiClosed()
	{
		sync();
		super.onGuiClosed();
	}

	protected void actionPerformed(GuiButton guibutton)
	{
		switch (guibutton.id)
		{
			case 0:
				cabinet.setOwner(null);
				Reference.packetHandler.sendToServer(new CabinetNullifyOwnerPacket(cabinet));
				player.closeScreen();
				break;
			case 1:
				cabinet.setHidden(!cabinet.isHidden());
				break;
			case 2:
				cabinet.setLocked(!cabinet.isLocked());
				break;
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
		String name = String.format("%s's Cabinet", cabinet.getOwnerName());
		fontRendererObj.drawString(name, 6, 5, 0x000000);
		fontRendererObj.drawString(StatCollector.translateToLocal("container.inventory"), 6, ySize - 94, 0x000000);
		fontRendererObj.drawString("Skin", 76, 25, 0x000000);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int i, int j)
	{

		GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);

		mc.renderEngine.bindTexture(texture);

		int x = (width - xSize) / 2;

		int y = (height - ySize) / 2;

		drawTexturedModalRect(x, y, 0, 0, xSize, ySize);
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
		if (this.button2.enabled != enabled)
		{
			button2.enabled = enabled;
		}
	}
}
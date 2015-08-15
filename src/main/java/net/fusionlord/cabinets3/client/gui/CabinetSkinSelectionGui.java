package net.fusionlord.cabinets3.client.gui;

import net.fusionlord.cabinets3.Reference;
import net.fusionlord.cabinets3.client.renderer.CabinetParts;
import net.fusionlord.cabinets3.packets.CabinetTextureSyncPacket;
import net.fusionlord.cabinets3.tileentity.CabinetTileEntity;
import net.fusionlord.fusionutil.client.dynamics.DynGUIScreen;
import net.fusionlord.fusionutil.client.dynamics.elements.ButtonGuiElement;
import net.fusionlord.fusionutil.client.dynamics.elements.IGuiElement;
import net.fusionlord.fusionutil.client.dynamics.elements.TextFieldGuiElement;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.StatCollector;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import org.lwjgl.input.Mouse;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by FusionLord on 8/11/2015.
 */

@SuppressWarnings("unchecked")
public class CabinetSkinSelectionGui extends DynGUIScreen
{
	int buttonScaleX = 110, buttonScaleY = 20, tabOffset = 25;
	private CabinetTileEntity cabinet;
	private String selectedTexture;
	private Map<GuiButton, List<IGuiElement>> tabContents = new HashMap<>();
	private int page = 0, sortType = 0;
	private int tileX = 6, tileY = 6, scale = 20;
	private GuiButton door;
	private GuiButton all;
	private GuiButton tab1;
	private GuiButton tab2;
	private GuiButton tab3;
	private GuiButton done;
	private GuiButton next;
	private GuiButton prev;
	private GuiButton sort;
	private GuiTextField search;
	private GuiButton currentTab;

	public CabinetSkinSelectionGui(CabinetTileEntity cabinetTE, EntityPlayer player)
	{
		super(player);
		cabinet = cabinetTE;
	}

	@Override
	protected void addInitialElements(List<IGuiElement> elements)
	{
		int x = guiLeft + 5;
		int y = guiTop + 5 + tabOffset;

		List<IGuiElement> tab1contents = new ArrayList<>();
		List<IGuiElement> tab2contents = new ArrayList<>();
		List<IGuiElement> tab3contents = new ArrayList<>();
		for (CabinetParts part : CabinetParts.values())
		{
			if (!(part.name().toLowerCase().contains("door")))
			{
				if (part.name().toLowerCase().contains("inner"))
				{
					tab3contents.add(new ButtonGuiElement(part.ordinal(), x, y + ((part.ordinal() % 6) * buttonScaleY), buttonScaleX, buttonScaleY, StatCollector.translateToLocal("cabinet.skin_gui.button.".concat(part.name().toLowerCase())), true, false, elements, buttonList));
				}
				else if (part.name().toLowerCase().contains("shelf"))
				{
					tab2contents.add(new ButtonGuiElement(part.ordinal(), x, y + ((part.ordinal() % 6) * buttonScaleY), buttonScaleX, buttonScaleY, StatCollector.translateToLocal("cabinet.skin_gui.button.".concat(part.name().toLowerCase())), true, false, elements, buttonList));
				}
				else
				{
					tab1contents.add(new ButtonGuiElement(part.ordinal(), x, y + ((part.ordinal() % 6) * buttonScaleY), buttonScaleX, buttonScaleY, StatCollector.translateToLocal("cabinet.skin_gui.button.".concat(part.name().toLowerCase())), true, true, elements, buttonList));
				}
			}
		}

		y += 6 * buttonScaleY;

		door = new ButtonGuiElement(buttonList.size(), x, y + buttonScaleY, buttonScaleX, buttonScaleY, StatCollector.translateToLocal("cabinet.skin_gui.button.door"), elements, buttonList);

		all = new ButtonGuiElement(buttonList.size(), x, y, buttonScaleX, buttonScaleY, StatCollector.translateToLocal("cabinet.skin_gui.button.all"), elements, buttonList);

		done = new ButtonGuiElement(buttonList.size(), x, y + buttonScaleY * 2 + 5, buttonScaleX, buttonScaleY, StatCollector.translateToLocal("cabinet.skin_gui.button.finished"), elements, buttonList);

		next = new ButtonGuiElement(buttonList.size(), x + buttonScaleX * 3 - 20, y + buttonScaleY * 2 + 5, 30, buttonScaleY, StatCollector.translateToLocal("cabinet.skin_gui.button.next_page"), elements, buttonList);
		prev = new ButtonGuiElement(buttonList.size(), next.xPosition - 90, y + buttonScaleY * 2 + 5, 30, buttonScaleY, StatCollector.translateToLocal("cabinet.skin_gui.button.prev_page"), elements, buttonList);

		sort = new ButtonGuiElement(buttonList.size(), done.xPosition + done.width + 5, done.yPosition, done.height, done.height, "Aa", elements, buttonList);
		search = new TextFieldGuiElement(buttonList.size(), fontRendererObj, sort.xPosition + sort.width + 5, sort.yPosition, (prev.xPosition - 5) - (sort.xPosition + sort.width + 5), done.height, elements);
		search.setCanLoseFocus(false);
		search.setFocused(true);

		//Tabs
		tab1 = new ButtonGuiElement(buttonList.size(), x, 5, buttonScaleX, buttonScaleY, StatCollector.translateToLocal("cabinet.skin_gui.button.tab_outside"), false, true, elements, buttonList);
		tab2 = new ButtonGuiElement(buttonList.size(), x + buttonScaleX + 5, 5, buttonScaleX, buttonScaleY, StatCollector.translateToLocal("cabinet.skin_gui.button.tab_shelves"), elements, buttonList);
		tab3 = new ButtonGuiElement(buttonList.size(), x + buttonScaleX * 2 + 10, 5, buttonScaleX, buttonScaleY, StatCollector.translateToLocal("cabinet.skin_gui.button.tab_inside"), elements, buttonList);

		currentTab = tab1;
		tabContents.put(tab1, tab1contents);
		tabContents.put(tab2, tab2contents);
		tabContents.put(tab3, tab3contents);

	}

	@Override
	protected void actionPerformed(GuiButton button) throws IOException
	{
		if (button == all)
		{
			List<IGuiElement> textures = tabContents.get(currentTab);
			for (IGuiElement texture : textures)
			{
				cabinet.setTexture(((GuiButton) texture).id, selectedTexture);
			}
			Reference.packetHandler.sendToServer(new CabinetTextureSyncPacket(cabinet));
		}
		else if (button == done)
		{
			player.closeScreen();
		}
		else if (button == next)
		{

			if (page + 1 <= Reference.getSkinsForSearch(search.getText()).size() / (tileX * tileY))
			{
				page++;
			}
		}
		else if (button == prev)
		{

			if (page - 1 >= 0)
			{
				page--;
			}
		}
		else if (button == tab1)
		{
			currentTab.enabled = true;
			List<IGuiElement> contents = tabContents.get(currentTab);
			for (IGuiElement element : contents)
			{
				((GuiButton) element).visible = false;
			}
			currentTab = button;
			currentTab.enabled = false;
			contents = tabContents.get(currentTab);
			for (IGuiElement element : contents)
			{
				((GuiButton) element).visible = true;
			}
		}
		else if (button == tab2)
		{
			currentTab.enabled = true;
			List<IGuiElement> contents = tabContents.get(currentTab);
			for (IGuiElement element : contents)
			{
				((GuiButton) element).visible = false;
			}
			currentTab = button;
			currentTab.enabled = false;
			contents = tabContents.get(currentTab);
			for (IGuiElement element : contents)
			{
				((GuiButton) element).visible = true;
			}
		}
		else if (button == tab3)
		{
			currentTab.enabled = true;
			List<IGuiElement> contents = tabContents.get(currentTab);
			for (IGuiElement element : contents)
			{
				((GuiButton) element).visible = false;
			}
			currentTab = button;
			currentTab.enabled = false;
			contents = tabContents.get(currentTab);
			for (IGuiElement element : contents)
			{
				((GuiButton) element).visible = true;
			}
		}
		else if (button == door)
		{
			cabinet.setTexture(CabinetParts.values().length - 3 + cabinet.getBlockMetadata(), selectedTexture);
			Reference.packetHandler.sendToServer(new CabinetTextureSyncPacket(cabinet));
		}
		else if (button == sort)
		{
			if (sortType == 0)
			{
				sort.displayString = "M";
				sortType = 1;
			}
			else
			{
				sort.displayString = "Aa";
				sortType = 0;
			}
			Reference.sortTextures(sortType);
		}
		else
		{
			List<IGuiElement> buttons = tabContents.get(currentTab);
			buttons.stream().filter(cbutton -> button == cbutton).forEach(current -> {
				cabinet.setTexture(button.id, selectedTexture);
				Reference.packetHandler.sendToServer(new CabinetTextureSyncPacket(cabinet));
			});
		}

	}

	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException
	{
		super.keyTyped(typedChar, keyCode);

		if (this.search.isFocused())
		{
			this.search.textboxKeyTyped(typedChar, keyCode);
		}
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException
	{
		super.mouseClicked(mouseX, mouseY, mouseButton);
		search.mouseClicked(mouseX, mouseY, mouseButton);
	}

	@Override
	public void updateScreen()
	{
		super.updateScreen();

		search.updateCursorCounter();

		tileX = (guiWidth - 40 - buttonScaleX) / scale;
		tileY = (guiHeight - 20 - buttonScaleY * 2) / scale;

		int x = getXOffset();
		int y = getYOffset();
		int mouseX = Mouse.getX() * width / mc.displayWidth;
		int mouseY = height - Mouse.getY() * height / mc.displayHeight;
		if (mouseX > x && mouseX < x + tileX * scale && mouseY > y && mouseY < y + tileY * scale)
		{
			if (Mouse.isButtonDown(0))
			{
				selectedTexture = getTextureAt(mouseX, mouseY);
			}
			int mouseDWheel = Mouse.getDWheel();
			if (mouseDWheel > 0 && page - 1 > -1)
			{
				page--;
			}
			if (mouseDWheel < 0 && page + 1 < Reference.SKINS.size() / (tileX * tileY) + 1)
			{
				page++;

			}
		}
	}

	private String getTextureAt(int mouseX, int mouseY)
	{
		int x = getXOffset();
		int y = getYOffset();
		int x2, y2, idx = 0;
		for (int i = page * (tileX * tileY); i < Reference.SKINS.size(); i++)
		{
			if (idx / tileX == tileY)
			{
				break;
			}
			x2 = x + ((idx % tileX) * scale);
			y2 = y + ((idx / tileX) * scale);
			if (mouseX > x2 && mouseX < x2 + scale && mouseY > y2 && mouseY < y2 + scale)
			{
				return Reference.SKINS.get(i).getIconName();
			}
			idx++;
		}
		return null;
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks)
	{
		super.drawScreen(mouseX, mouseY, partialTicks);
		int x = getXOffset();
		int y = getYOffset();
		Color c;

		drawCenteredStringNoShadow(fontRendererObj, String.format("%s of %s", page + 1, Reference.SKINS.size() / (tileX * tileY) + 1), next.xPosition - 30, y + guiHeight - 50, 0x000000);
		GlStateManager.resetColor();

		mc.getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
		TextureAtlasSprite texture;

		//Draw current cabinet textures.
		List<IGuiElement> elements = tabContents.get(currentTab);
		for (IGuiElement element : elements)
		{
			texture = mc.getTextureMapBlocks().getTextureExtry(cabinet.getTexture(((GuiButton) element).id));

			for (String s : Reference.COLORABLE)
			{
				if (texture.getIconName().contains(s))
				{
					if (texture.getIconName().contains("grass"))
					{
						c = new Color(cabinet.getWorld().getBiomeGenForCoords(cabinet.getPos()).getGrassColorAtPos(cabinet.getPos()));
					}
					else
					{
						c = new Color(cabinet.getWorld().getBiomeGenForCoords(cabinet.getPos()).getGrassColorAtPos(cabinet.getPos()));
					}
					GlStateManager.color(c.getRed() / 255F, c.getGreen() / 255F, c.getBlue() / 255F);
					break;
				}
			}
			drawTexturedModalRect(x + 1 - 25, y + 1 + (scale * elements.indexOf(element)), texture, 18, 18);
			GlStateManager.color(1F, 1F, 1F);
		}
		texture = mc.getTextureMapBlocks().getTextureExtry(cabinet.getTexture(CabinetParts.values().length - 3 + cabinet.getBlockMetadata()));

		for (String s : Reference.COLORABLE)
		{

			if (texture.getIconName().contains(s))
			{

				if (texture.getIconName().contains("grass"))
				{
					c = new Color(cabinet.getWorld().getBiomeGenForCoords(cabinet.getPos()).getGrassColorAtPos(cabinet.getPos()));
				}
				else
				{
					c = new Color(cabinet.getWorld().getBiomeGenForCoords(cabinet.getPos()).getGrassColorAtPos(cabinet.getPos()));
				}
				GlStateManager.color(c.getRed() / 255F, c.getGreen() / 255F, c.getBlue() / 255F);
				break;
			}
		}
		drawTexturedModalRect(x - 24, guiTop + 5 + tabOffset + (scale * 7) + 1, texture, scale - 2, scale - 2);
		GlStateManager.color(1F, 1F, 1F);

		//Draw texture list.
		int x2, y2, idx = 0;

		List<TextureAtlasSprite> textures = Reference.getSkinsForSearch(search.getText());
		for (int i = page * (tileX * tileY); i < textures.size(); i++)
		{
			if (idx / tileX == tileY)
			{
				break;
			}
			x2 = x + ((idx % tileX) * scale);
			y2 = y + ((idx / tileX) * scale);
			texture = textures.get(i);
			for (String s : Reference.COLORABLE)
			{
				if (texture.getIconName().contains(s))
				{
					if (texture.getIconName().contains("grass"))
					{
						c = new Color(cabinet.getWorld().getBiomeGenForCoords(cabinet.getPos()).getGrassColorAtPos(cabinet.getPos()));
					}
					else
					{
						c = new Color(cabinet.getWorld().getBiomeGenForCoords(cabinet.getPos()).getGrassColorAtPos(cabinet.getPos()));
					}
					GlStateManager.color(c.getRed() / 255F, c.getGreen() / 255F, c.getBlue() / 255F);
					break;
				}
			}
			drawTexturedModalRect(x2 + 1, y2 + 1, texture, scale - 2, scale - 2);
			GlStateManager.color(1F, 1F, 1F);
			idx++;
		}

		//Draw texture tooltip.
		idx = 0;
		for (int i = page * (tileX * tileY); i < textures.size(); i++)
		{
			if (idx / tileX == tileY)
			{
				break;
			}
			x2 = x + ((idx % tileX) * scale);
			y2 = y + ((idx / tileX) * scale);
			if (mouseX > x2 && mouseX < x2 + scale && mouseY > y2 && mouseY < y2 + scale)
			{
				texture = textures.get(i);
				List<String> lines = new ArrayList<>();
				String s = texture.getIconName();
				String modid = s.substring(0, s.indexOf(":"));
				ModContainer mod = modid.equals("minecraft") ? Loader.instance().getMinecraftModContainer() : Loader.instance().getIndexedModList().get(modid);
				lines.add(mod == null ? modid : mod.getName());
				lines.add(capitalize(s.substring(s.lastIndexOf("/") + 1).replace("_", " ")));
				drawHoveringText(lines, mouseX, mouseY);
			}
			idx++;
		}
	}

	@Override
	protected void drawGuiBackgroundLayer(int mouseX, int mouseY)
	{
		super.drawGuiBackgroundLayer(mouseX, mouseY);

		int x = getXOffset();
		int y = getYOffset();

		drawRect(x - 25, y, x + scale - 25, y + tabContents.get(currentTab).size() * scale, Color.BLACK.getRGB());
		drawRect(x - 25, guiTop + 5 + tabOffset + (scale * 7), x - 25 + scale, guiTop + 5 + tabOffset + (scale * 8), Color.BLACK.getRGB());
		drawRect(x, y, x + (tileX * scale), y + tileY * scale, Color.BLACK.getRGB());
		int x2, y2, idx = 0;
		TextureAtlasSprite current;
		for (int i = page * (tileX * tileY); i < Reference.getSkinsForSearch(search.getText()).size(); i++)
		{
			if (idx / tileX == tileY)
			{
				break;
			}
			x2 = x + ((idx % tileX) * scale);
			y2 = y + ((idx / tileX) * scale);
			current = Reference.SKINS.get(i);
			if (current.getIconName().equals(selectedTexture))
			{
				drawRect(x2, y2, x2 + scale, y2 + scale, Color.GREEN.getRGB());
			}
			idx++;
		}
		GlStateManager.color(1F, 1F, 1F);
	}

	public String capitalize(String name)
	{
		if (name != null && name.length() != 0)
		{
			char[] chars = name.toCharArray();
			chars[0] = Character.toUpperCase(chars[0]);
			return new String(chars);
		}
		else
		{
			return name;
		}
	}

	@Override
	public void onGuiClosed()
	{
		Reference.packetHandler.sendToServer(new CabinetTextureSyncPacket(cabinet));
		super.onGuiClosed();
	}

	@Override
	public boolean doesGuiPauseGame()
	{
		return false;
	}

	public int getXOffset()
	{
		return guiLeft + buttonScaleX + 35;
	}

	public int getYOffset() { return guiTop + 5 + tabOffset; }


	public void drawCenteredStringNoShadow(FontRenderer fontRendererIn, String text, int x, int y, int color)
	{
		fontRendererIn.drawString(text, x - fontRendererIn.getStringWidth(text) / 2, y, color);
	}
}

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
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by FusionLord on 8/11/2015.
 */
@SuppressWarnings("unchecked")
public class CabinetSkinSelectionGui extends DynGUIScreen
{
	int buttonScaleX = 110, buttonScaleY = 20, tabOffset = 25;
	private CabinetTileEntity cabinet;
	private TextureAtlasSprite selectedTexture;
	private Map<ButtonGuiElement, List<ButtonGuiElement>> tabContents = new HashMap<>();
	private int page = 0, sortType = 0;
	private int tileX = 10, tileY = 8, scale = 20;
	private ButtonGuiElement door, all, tab1, tab2, tab3, done, next, prev, sort, currentTab;
	private TextFieldGuiElement search;
	private List<TextureAtlasSprite> textures = new ArrayList<>();

	public CabinetSkinSelectionGui(CabinetTileEntity cabinetTE, EntityPlayer player)
	{
		super(player);
		cabinet = cabinetTE;
	}

	@Override
	public void addInitialElements(List<IGuiElement> elements)
	{
		int x = 5;
		int y = tabOffset + 5;

		//Tabs
		buttonList.add(tab1 = new ButtonGuiElement(buttonList.size(), x, y - tabOffset, buttonScaleX, buttonScaleY, StatCollector.translateToLocal("cabinet.skin_gui.button.tab_outside"), false, true));
		buttonList.add(tab2 = new ButtonGuiElement(buttonList.size(), x + buttonScaleX + 5, y - tabOffset, buttonScaleX, buttonScaleY, StatCollector.translateToLocal("cabinet.skin_gui.button.tab_shelves"), true, true));
		buttonList.add(tab3 = new ButtonGuiElement(buttonList.size(), x + buttonScaleX * 2 + 10, y - tabOffset, buttonScaleX, buttonScaleY, StatCollector.translateToLocal("cabinet.skin_gui.button.tab_inside"), true, true));

		List<ButtonGuiElement> tab1contents = new ArrayList<>();
		List<ButtonGuiElement> tab2contents = new ArrayList<>();
		List<ButtonGuiElement> tab3contents = new ArrayList<>();
		ButtonGuiElement button;
		int y1 = y, y2 = y, y3 = y;
		for (CabinetParts part : CabinetParts.values())
		{
			if (!(part.name().toLowerCase().contains("door")))
			{
				if (part.name().toLowerCase().contains("inner"))
				{
					button = new ButtonGuiElement(buttonList.size(), x, y1, buttonScaleX, buttonScaleY, StatCollector.translateToLocal("cabinet.skin_gui.button.".concat(part.name().toLowerCase())), true, false);

					buttonList.add(button);
					tab3contents.add(button);
					y1 += buttonScaleY;
				}
				else if (part.name().toLowerCase().contains("shelf"))
				{
					button = new ButtonGuiElement(buttonList.size(), x, y2, buttonScaleX, buttonScaleY, StatCollector.translateToLocal("cabinet.skin_gui.button.".concat(part.name().toLowerCase())), true, false);
					buttonList.add(button);
					tab2contents.add(button);

					y2 += buttonScaleY;
				}
				else
				{
					button = new ButtonGuiElement(buttonList.size(), x, y3, buttonScaleX, buttonScaleY, StatCollector.translateToLocal("cabinet.skin_gui.button.".concat(part.name().toLowerCase())), true, true);
					buttonList.add(button);
					tab1contents.add(button);
					y3 += buttonScaleY;
				}
			}
		}
		y += buttonScaleY * 6;

		buttonList.add(all = new ButtonGuiElement(buttonList.size(), x, y, buttonScaleX, buttonScaleY, StatCollector.translateToLocal("cabinet.skin_gui.button.all"), true, true));
		y += buttonScaleY;
		buttonList.add(door = new ButtonGuiElement(buttonList.size(), x, y, buttonScaleX, buttonScaleY, StatCollector.translateToLocal("cabinet.skin_gui.button.door"), true, true));
		y += 5 + buttonScaleY;
		buttonList.add(done = new ButtonGuiElement(buttonList.size(), x, y, buttonScaleX, buttonScaleY, StatCollector.translateToLocal("cabinet.skin_gui.button.finished"), true, true));
		buttonList.add(sort = new ButtonGuiElement(buttonList.size(), done.xPosition + done.width + 5, y, scale, buttonScaleY, "Aa", true, true));
		search = new TextFieldGuiElement(0, fontRendererObj, sort.xPosition + sort.width + 5, y, 75, buttonScaleY);
		search.setCanLoseFocus(false);
		search.setFocused(true);
		buttonList.add(prev = new ButtonGuiElement(buttonList.size(), search.xPosition + search.width + 5, y, 30, buttonScaleY, StatCollector.translateToLocal("cabinet.skin_gui.button.prev_page"), true, true));
		buttonList.add(next = new ButtonGuiElement(buttonList.size(), prev.xPosition + prev.width + 60, y, 30, buttonScaleY, StatCollector.translateToLocal("cabinet.skin_gui.button.next_page"), true, true));

		elements.addAll((Collection<? extends IGuiElement>) buttonList.stream().filter(o -> o instanceof IGuiElement).map(o -> o).collect(Collectors.toList()));

		elements.add(search);

		currentTab = tab1;
		tabContents.put(tab1, tab1contents);
		tabContents.put(tab2, tab2contents);
		tabContents.put(tab3, tab3contents);

		textures = Reference.getSkinsForSearch(search.getText());
	}

	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException
	{
		super.keyTyped(typedChar, keyCode);

		if (this.search.isFocused())
		{
			this.search.textboxKeyTyped(typedChar, keyCode);
		}
		textures = Reference.getSkinsForSearch(search.getText());
		if (page > textures.size() / (tileX * tileY))
		{
			page = textures.size() / (tileX * tileY);
		}
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException
	{
		super.mouseClicked(mouseX, mouseY, mouseButton);
		search.mouseClicked(mouseX, mouseY, mouseButton);

		textures = Reference.getSkinsForSearch(search.getText());
		if (page > textures.size() / (tileX * tileY))
		{
			page = textures.size() / (tileX * tileY);
		}

		int x = sort.getElementX() + sort.getElementWidth() + 5;
		int y = tab1.getElementY() + tab1.getElementHeight() + 5;
		if (mouseX > x && mouseX < x + tileX * scale && mouseY > y && mouseY < y + tileY * scale)
		{
			selectedTexture = getTextureAt(mouseX, mouseY);
		}
	}

	private TextureAtlasSprite getTextureAt(int mouseX, int mouseY)
	{
		//Draw texture tooltip.
		int x, y, idx = 0;
		for (int i = page * (tileX * tileY); i < textures.size(); i++)
		{
			if (idx / tileX == tileY)
			{
				break;
			}
			x = sort.getElementX() + sort.getElementWidth() + 5 + (idx % tileX) * scale;
			y = tab1.getElementY() + tab1.getElementHeight() + 5 + (idx / tileX) * scale;
			if (mouseX > x && mouseX < x + scale && mouseY > y && mouseY < y + scale)
			{
				return textures.get(i);
			}
			idx++;
		}
		return mc.getTextureMapBlocks().getAtlasSprite(cabinet.getDefaultTexture());
	}

	@Override
	public void updateScreen()
	{
		super.updateScreen();
		search.updateCursorCounter();
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks)
	{
		super.drawScreen(mouseX, mouseY, partialTicks);

		//mouse input
		int x = sort.getElementX() + sort.getElementWidth() + 5;
		int y = tab1.getElementY() + tab1.getElementHeight() + 5;
		if (mouseX > x && mouseX < x + tileX * scale && mouseY > y && mouseY < y + tileY * scale)
		{
			int mouseDWheel = Mouse.getDWheel();
			if (mouseDWheel > 0 && page - 1 > -1)
			{
				page--;
			}
			if (mouseDWheel < 0 && page + 1 < textures.size() / (tileX * tileY) + 1)
			{
				page++;

			}
		}

		Color c;
		drawCenteredStringNoShadow(fontRendererObj, String.format("%s of %s", page + 1, textures.size() / ((tileX * tileY) + 1)), prev.xPosition + prev.width + 30, prev.yPosition + 7, 0x000000);
		GlStateManager.color(1F, 1F, 1F);

		mc.getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
		TextureAtlasSprite texture;

		//Draw current tab textures.
		for (IGuiElement element : tabContents.get(currentTab))
		{
			texture = mc.getTextureMapBlocks().getTextureExtry(cabinet.getTexture(elements.indexOf(element) - 3));

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
			drawTexturedModalRect(element.getElementX() + element.getElementWidth() + 6, element.getElementY() + 1, texture, 18, 18);
			GlStateManager.color(1F, 1F, 1F);
		}

		//Door
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
		drawTexturedModalRect(door.getElementX() + door.getElementWidth() + 6, door.getElementY() + 1, texture, scale - 2, scale - 2);
		GlStateManager.color(1F, 1F, 1F);

		//Draw texture list.
		int idx = 0;
		for (int i = page * (tileX * tileY); i < textures.size(); i++)
		{
			if (idx / tileX == tileY)
			{
				break;
			}
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
			drawTexturedModalRect(sort.getElementX() + sort.getElementWidth() + 6 + (idx % tileX) * scale, tab1.getElementY() + tab1.getElementHeight() + 6 + (idx / tileX) * scale, texture, scale - 2, scale - 2);
			GlStateManager.color(1F, 1F, 1F);
			idx++;
		}

		//Draw texture tooltip.
		texture = getTextureAt(mouseX, mouseY);
		List<String> lines = new ArrayList<>();
		String s = texture.getIconName();
		String modid = s.substring(0, s.indexOf(":"));
		ModContainer mod = modid.equals("minecraft") ? Loader.instance().getMinecraftModContainer() : Loader.instance().getIndexedModList().get(modid);
		lines.add(mod == null ? modid : mod.getName());
		lines.add(capitalize(s.substring(s.lastIndexOf("/") + 1).replace("_", " ")));
		drawHoveringText(lines, mouseX, mouseY);
	}

	@Override
	protected void drawGuiBackgroundLayer(int mouseX, int mouseY)
	{
		super.drawGuiBackgroundLayer(mouseX, mouseY);

		//door
		drawRect(sort.getElementX(), door.getElementY(), sort.getElementX() + scale, door.getElementY() + scale, Color.BLACK.getRGB());
		//current
		drawRect(sort.getElementX(), tab1.getElementY() + tab1.getElementHeight() + 5, sort.getElementX() + scale, tab1.getElementY() + tab1.getElementHeight() + 5 + (tabContents.get(currentTab).size() * scale), Color.BLACK.getRGB());

		int x, y, idx = 0;
		TextureAtlasSprite current;
		for (int i = page * (tileX * tileY); i < textures.size(); i++)
		{
			if (idx / tileX == tileY)
			{
				break;
			}
			x = sort.getElementX() + sort.getElementWidth() + 5 + (idx % tileX) * scale;
			y = tab1.getElementY() + tab1.getElementHeight() + 5 + (idx / tileX) * scale;
			current = textures.get(i);
			if (selectedTexture != null && current.equals(selectedTexture))
			{
				drawRect(x, y, x + scale, y + scale, Color.GREEN.getRGB());
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

	public void drawCenteredStringNoShadow(FontRenderer fontRendererIn, String text, int x, int y, int color)
	{
		fontRendererIn.drawString(text, x - fontRendererIn.getStringWidth(text) / 2, y, color);
	}

	@Override
	public void actionPerformed(GuiButton button)
	{
		if (button == all)
		{
			for (ButtonGuiElement current : tabContents.get(currentTab))
			{
				cabinet.setTexture(current.id - 3, selectedTexture.getIconName());
			}
			Reference.packetHandler.sendToServer(new CabinetTextureSyncPacket(cabinet));
		}
		else if (button == done)
		{
			player.closeScreen();
		}
		else if (button == next)
		{
			if (page + 1 <= textures.size() / (tileX * tileY))
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
			List<ButtonGuiElement> contents = tabContents.get(currentTab);
			for (IGuiElement element : contents)
			{
				((ButtonGuiElement) element).visible = false;
			}
			currentTab = (ButtonGuiElement) button;
			currentTab.enabled = false;
			contents = tabContents.get(currentTab);
			for (IGuiElement element : contents)
			{
				((ButtonGuiElement) element).visible = true;
			}
		}
		else if (button == tab2)
		{
			currentTab.enabled = true;
			List<ButtonGuiElement> contents = tabContents.get(currentTab);
			for (IGuiElement element : contents)
			{
				((ButtonGuiElement) element).visible = false;
			}
			currentTab = (ButtonGuiElement) button;
			currentTab.enabled = false;
			contents = tabContents.get(currentTab);
			for (IGuiElement element : contents)
			{
				((ButtonGuiElement) element).visible = true;
			}
		}
		else if (button == tab3)
		{
			currentTab.enabled = true;
			List<ButtonGuiElement> contents = tabContents.get(currentTab);
			for (IGuiElement element : contents)
			{
				((ButtonGuiElement) element).visible = false;
			}
			currentTab = (ButtonGuiElement) button;
			currentTab.enabled = false;
			contents = tabContents.get(currentTab);
			for (IGuiElement element : contents)
			{
				((ButtonGuiElement) element).visible = true;
			}
		}
		else if (button == door)
		{
			cabinet.setTexture(CabinetParts.values().length - 3 + cabinet.getBlockMetadata(), selectedTexture.getIconName());
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
			List<ButtonGuiElement> buttons = tabContents.get(currentTab);
			for (int i = 0; i < buttons.size(); i++)
			{
				if (button == buttons.get(i))
				{
					cabinet.setTexture(i, selectedTexture.getIconName());
					Reference.packetHandler.sendToServer(new CabinetTextureSyncPacket(cabinet));
					break;
				}
			}
		}
	}
}

package net.fusionlord.cabinets3.client.gui;

import net.fusionlord.cabinets3.CabinetsReloaded;
import net.fusionlord.cabinets3.Reference;
import net.fusionlord.cabinets3.client.renderer.CabinetParts;
import net.fusionlord.cabinets3.client.renderer.RenderingReference;
import net.fusionlord.cabinets3.packets.CabinetTextureSyncPacket;
import net.fusionlord.cabinets3.tileentity.CabinetTileEntity;
import net.fusionlord.fusionutil.client.dynamics.DynGUIScreen;
import net.fusionlord.fusionutil.client.dynamics.elements.ButtonGuiElement;
import net.fusionlord.fusionutil.client.dynamics.elements.IGuiElement;
import net.fusionlord.fusionutil.client.dynamics.elements.TextFieldGuiElement;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

	public CabinetSkinSelectionGui(EntityPlayer player, CabinetTileEntity cabinetTE)
	{
		super(player);
		cabinet = cabinetTE;
	}

	@Override
	public void addInitialElements(List<IGuiElement> elements)
	{
		int x = 5;
		int y = tabOffset + 5;
		int id = 0;
		//Tabs
		elements.add(tab1 = new ButtonGuiElement(id++, x, y - tabOffset, buttonScaleX, buttonScaleY, StatCollector.translateToLocal("cabinet.skin_gui.button.tab_outside"), false, true));
		elements.add(tab2 = new ButtonGuiElement(id++, x + buttonScaleX + 5, y - tabOffset, buttonScaleX, buttonScaleY, StatCollector.translateToLocal("cabinet.skin_gui.button.tab_shelves"), true, true));
		elements.add(tab3 = new ButtonGuiElement(id++, x + buttonScaleX * 2 + 10, y - tabOffset, buttonScaleX, buttonScaleY, StatCollector.translateToLocal("cabinet.skin_gui.button.tab_inside"), true, true));

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
					elements.add(button = new ButtonGuiElement(id++, x, y1, buttonScaleX, buttonScaleY, StatCollector.translateToLocal("cabinet.skin_gui.button.".concat(part.name().toLowerCase())), true, false));
					tab3contents.add(button);
					y1 += buttonScaleY;
				}
				else if (part.name().toLowerCase().contains("shelf"))
				{
					elements.add(button = new ButtonGuiElement(id++, x, y2, buttonScaleX, buttonScaleY, StatCollector.translateToLocal("cabinet.skin_gui.button.".concat(part.name().toLowerCase())), true, false));
					tab2contents.add(button);
					y2 += buttonScaleY;
				}
				else
				{
					elements.add(button = new ButtonGuiElement(id++, x, y3, buttonScaleX, buttonScaleY, StatCollector.translateToLocal("cabinet.skin_gui.button.".concat(part.name().toLowerCase())), true, true));
					tab1contents.add(button);
					y3 += buttonScaleY;
				}
			}
		}
		y += buttonScaleY * 6;

		elements.add(all = new ButtonGuiElement(id++, x, y, buttonScaleX, buttonScaleY, StatCollector.translateToLocal("cabinet.skin_gui.button.all"), true, true));
		y += buttonScaleY;
		elements.add(door = new ButtonGuiElement(id++, x, y, buttonScaleX, buttonScaleY, StatCollector.translateToLocal("cabinet.skin_gui.button.door"), true, true));
		y += 5 + buttonScaleY;
		elements.add(done = new ButtonGuiElement(id++, x, y, buttonScaleX, buttonScaleY, StatCollector.translateToLocal("cabinet.skin_gui.button.finished"), true, true));
		elements.add(sort = new ButtonGuiElement(id++, done.getElementX() + done.getElementWidth() + 5, y, scale, buttonScaleY, "Aa", true, true));
		elements.add(search = new TextFieldGuiElement(0, fontRendererObj, sort.getElementX() + sort.getElementWidth() + 5, y, 75, buttonScaleY));
		search.getTextField().setCanLoseFocus(false);
		search.getTextField().setFocused(true);
		elements.add(prev = new ButtonGuiElement(id++, search.getTextField().xPosition + search.getTextField().width + 5, y, 30, buttonScaleY, StatCollector.translateToLocal("cabinet.skin_gui.button.prev_page"), true, true));
		elements.add(next = new ButtonGuiElement(id, prev.getElementX() + prev.getElementWidth() + 60, y, 30, buttonScaleY, StatCollector.translateToLocal("cabinet.skin_gui.button.next_page"), true, true));

		buttonList.addAll(elements.stream().filter(element -> element instanceof ButtonGuiElement).map(element -> ((ButtonGuiElement) element).getButton()).collect(Collectors.toList()));

		elements.add(search);

		currentTab = tab1;
		tabContents.put(tab1, tab1contents);
		tabContents.put(tab2, tab2contents);
		tabContents.put(tab3, tab3contents);

		textures = Reference.getSkinsForSearch(search.getTextField().getText());
	}

	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException
	{
		super.keyTyped(typedChar, keyCode);

		if (search.getTextField().isFocused())
		{
			search.getTextField().textboxKeyTyped(typedChar, keyCode);
		}
		textures = Reference.getSkinsForSearch(search.getTextField().getText());
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

		textures = Reference.getSkinsForSearch(search.getTextField().getText());
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
		return null;
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
		RenderingReference.drawCenteredStringNoShadow(fontRendererObj, String.format("%s of %s", page + 1, textures.size() / ((tileX * tileY) + 1)), prev.getElementX() + prev.getElementWidth() + 30, prev.getElementY() + 7, 0x000000);
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
		texture = mc.getTextureMapBlocks().getTextureExtry(cabinet.getDoorTexture());
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

		if (mouseX > x && mouseX < x + tileX * scale && mouseY > y && mouseY < y + tileY * scale)
		{
			//Draw texture tooltip.
			texture = getTextureAt(mouseX, mouseY);
			if (texture != null)
			{
				List<String> lines = new ArrayList<>();
				String s = texture.getIconName();
				String modid = s.substring(0, s.indexOf(":"));
				ModContainer mod = modid.equals("minecraft") ? Loader.instance().getMinecraftModContainer() : Loader.instance().getIndexedModList().get(modid);
				lines.add(mod == null ? modid : mod.getName());
				lines.add(RenderingReference.capitalize(s.substring(s.lastIndexOf("/") + 1).replace("_", " ")));
				drawHoveringText(lines, mouseX, mouseY);
			}
		}
	}

	@Override
	protected void drawGuiBackgroundLayer(int mouseX, int mouseY)
	{
		drawDefaultBackground();
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

	@Override
	public void actionPerformed(GuiButton button)
	{
		if (button == tab1.getButton() || button == tab2.getButton() || button == tab3.getButton())
		{
			currentTab.getButton().enabled = true;
			List<ButtonGuiElement> contents = tabContents.get(currentTab);
			for (IGuiElement element : contents)
			{
				((ButtonGuiElement) element).getButton().visible = false;
				((ButtonGuiElement) element).getButton().enabled = false;
			}
			currentTab = (ButtonGuiElement) elements.get(button.id);
			currentTab.getButton().enabled = false;
			contents = tabContents.get(currentTab);
			for (IGuiElement element : contents)
			{
				((ButtonGuiElement) element).getButton().visible = true;
				((ButtonGuiElement) element).getButton().enabled = true;
			}
		}
		else if (button == next.getButton())
		{
			if (page + 1 <= textures.size() / (tileX * tileY))
			{
				page++;
			}
		}
		else if (button == prev.getButton())
		{

			if (page - 1 >= 0)
			{
				page--;
			}
		}
		else if (button == all.getButton() && selectedTexture != null)
		{
			for (ButtonGuiElement current : tabContents.get(currentTab))
			{
				cabinet.setTexture(current.getButton().id - 3, selectedTexture.getIconName());
			}
			Reference.packetHandler.sendToServer(new CabinetTextureSyncPacket(cabinet));
		}
		else if (button == door.getButton())
		{
			if (selectedTexture != null)
			{
				cabinet.setDoorTexture(selectedTexture.getIconName());
				Reference.packetHandler.sendToServer(new CabinetTextureSyncPacket(cabinet));
			}
		}
		else if (button == done.getButton())
		{
			player.openGui(CabinetsReloaded.instance, 0, cabinet.getWorld(), cabinet.getPos().getX(), cabinet.getPos().getY(), cabinet.getPos().getZ());
		}
		else if (button == sort.getButton())
		{
			if (sortType == 0)
			{
				sort.getButton().displayString = "M";
				sortType = 1;
			}
			else
			{
				sort.getButton().displayString = "Aa";
				sortType = 0;
			}
			Reference.sortTextures(sortType);
		}
		else if (selectedTexture != null)
		{
			cabinet.setTexture(button.id - 3, selectedTexture.getIconName());
			Reference.packetHandler.sendToServer(new CabinetTextureSyncPacket(cabinet));
		}
	}
}

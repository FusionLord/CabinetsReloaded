package net.fusionlord.cabinets3.client.renderer;

import net.fusionlord.cabinets3.Reference;
import net.fusionlord.cabinets3.tileentity.CabinetTileEntity;
import net.fusionlord.fusionutil.client.RenderingUtil;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemAnvilBlock;
import net.minecraft.item.ItemBanner;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.IPlantable;
import org.lwjgl.opengl.GL11;

public class CabinetTileEntityRenderer extends TileEntitySpecialRenderer
{


	public CabinetTileEntityRenderer()
	{
		super();
	}

	@Override
	public void renderTileEntityAt(TileEntity tileEntity, double x, double y, double z, float f, int i)
	{
		GlStateManager.pushMatrix();
		CabinetTileEntity cabinet = (CabinetTileEntity) tileEntity;
		GlStateManager.translate((float) x + .5, (float) y + .5, (float) z + .5);
		renderCabinet(cabinet);
		GlStateManager.popMatrix();
	}

	public void renderCabinet(CabinetTileEntity cabinet)
	{
		bindTexture(TextureMap.locationBlocksTexture);

		WorldRenderer worldRenderer = Tessellator.getInstance().getWorldRenderer();

		GlStateManager.enableBlend();
		GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

		switch (cabinet.getFacing())
		{
			case DOWN:
				break;
			case UP:
				break;
			case NORTH:
				break;
			case SOUTH:
				GlStateManager.rotate(180, 0, 1, 0);
				break;
			case WEST:
				GlStateManager.rotate(90, 0, 1, 0);
				break;
			case EAST:
				GlStateManager.rotate(-90, 0, 1, 0);
				break;
		}

		switch (cabinet.getVerticalFacing())
		{
			case DOWN:
				GlStateManager.rotate(90, 1, 0, 0);
				break;
			case UP:
				GlStateManager.rotate(-90, 1, 0, 0);
				break;
		}

		TextureAtlasSprite texture = Minecraft.getMinecraft().getTextureMapBlocks().getTextureExtry(cabinet.getDoorTexture());

		int color = -1;
		for (String s : Reference.COLORABLE)
		{
			if (texture.getIconName().contains(s))
			{
				if (texture.getIconName().contains("grass"))
				{
					color = cabinet.getWorld().getBiomeGenForCoords(cabinet.getPos()).getGrassColorAtPos(cabinet.getPos());
				}
				else
				{
					color = cabinet.getWorld().getBiomeGenForCoords(cabinet.getPos()).getFoliageColorAtPos(cabinet.getPos());
				}
			}
		}

		boolean doubleRender = texture.getIconName().toLowerCase().contains("items");
		float a = 90 * cabinet.getDoorAngle();
		float m = 0.0625F;
		switch (cabinet.getDoorType())
		{
			case LEFT:
				GlStateManager.translate(m * -7.5f, m * 1f, m * 7.5f);
				GlStateManager.rotate(a, 0f, -1f, 0f);
				GlStateManager.rotate(180, 0F, 1F, 0F);
				if (texture.getIconName().contains("grass_side_overlay") || texture.getIconName().contains("grass_top"))
				{
					GlStateManager.disableLighting();
				}
				if (doubleRender)
				{
					RenderingUtil.renderPartWithIcon(RenderingReference.model, "leftdoor", Minecraft.getMinecraft().getTextureMapBlocks().getTextureExtry(cabinet.getDoorTexture()), worldRenderer, -1);
				}
				RenderingUtil.renderPartWithIcon(RenderingReference.model, "leftdoor", texture, worldRenderer, color);
				if (texture.getIconName().contains("grass_side_overlay") || texture.getIconName().contains("grass_top"))
				{
					GlStateManager.enableLighting();
				}
				GlStateManager.rotate(180, 0F, -1F, 0F);
				GlStateManager.rotate(a, 0f, 1f, 0f);
				GlStateManager.translate(m * 7.5f, m * -1f, m * -7.5f);
				break;
			case RIGHT:
				GlStateManager.translate(m * 7.5f, m * 1f, m * 7.5f);
				GlStateManager.rotate(a, 0f, 1f, 0f);
				GlStateManager.rotate(180, 0F, 1F, 0F);
				if (texture.getIconName().contains("grass_side_overlay") || texture.getIconName().contains("grass_top"))
				{
					GlStateManager.disableLighting();
				}
				if (doubleRender)
				{
					RenderingUtil.renderPartWithIcon(RenderingReference.model, "rightdoor", Minecraft.getMinecraft().getTextureMapBlocks().getTextureExtry(cabinet.getDoorTexture()), worldRenderer, -1);
				}
				RenderingUtil.renderPartWithIcon(RenderingReference.model, "rightdoor", texture, worldRenderer, color);
				if (texture.getIconName().contains("grass_side_overlay") || texture.getIconName().contains("grass_top"))
				{
					GlStateManager.enableLighting();
				}
				GlStateManager.rotate(180, 0F, -1F, 0F);
				GlStateManager.rotate(a, 0f, -1F, 0f);
				GlStateManager.translate(m * -7.5f, m * -1f, m * -7.5f);
				break;
			case DOUBLE:
				GlStateManager.translate(m * 7.5f, m * 1f, m * 7.5f);
				GlStateManager.rotate(a, 0f, 1f, 0f);
				if (texture.getIconName().contains("grass_side_overlay") || texture.getIconName().contains("grass_top"))
				{
					GlStateManager.disableLighting();
				}
				if (doubleRender)
				{
					RenderingUtil.renderPartWithIcon(RenderingReference.model, "leftdoubledoor", Minecraft.getMinecraft().getTextureMapBlocks().getTextureExtry(cabinet.getDoorTexture()), worldRenderer, -1);
				}
				RenderingUtil.renderPartWithIcon(RenderingReference.model, "leftdoubledoor", texture, worldRenderer, color);
				if (texture.getIconName().contains("grass_side_overlay") || texture.getIconName().contains("grass_top"))
				{
					GlStateManager.enableLighting();
				}
				GlStateManager.rotate(a, 0f, -1f, 0f);
				GlStateManager.translate(m * -7.5f, m * -1f, m * -7.5f);
				GlStateManager.translate(m * -7.5f, m * 1f, m * 7.5f);
				GlStateManager.rotate(a, 0f, -1f, 0f);
				if (texture.getIconName().contains("grass_side_overlay") || texture.getIconName().contains("grass_top"))
				{
					GlStateManager.disableLighting();
				}
				if (doubleRender)
				{
					RenderingUtil.renderPartWithIcon(RenderingReference.model, "rightdoubledoor", Minecraft.getMinecraft().getTextureMapBlocks().getTextureExtry(cabinet.getDoorTexture()), worldRenderer, -1);
				}
				RenderingUtil.renderPartWithIcon(RenderingReference.model, "rightdoubledoor", texture, worldRenderer, color);
				if (texture.getIconName().contains("grass_side_overlay") || texture.getIconName().contains("grass_top"))
				{
					GlStateManager.enableLighting();
				}
				GlStateManager.rotate(a, 0f, 1f, 0f);
				GlStateManager.translate(m * 7.5f, m * -1f, m * -7.5f);
				break;
		}

		for (CabinetParts part : CabinetParts.values())
		{
			texture = Minecraft.getMinecraft().getTextureMapBlocks().getTextureExtry(cabinet.getTexture(part.ordinal()));
			doubleRender = texture.getIconName().toLowerCase().contains("items");
			color = -1;
			if (Reference.isTextureColorable(texture.getIconName()))
			{
				if (texture.getIconName().contains("grass"))
				{
					color = cabinet.getWorld().getBiomeGenForCoords(cabinet.getPos()).getGrassColorAtPos(cabinet.getPos());
				}
				else
				{
					color = cabinet.getWorld().getBiomeGenForCoords(cabinet.getPos()).getFoliageColorAtPos(cabinet.getPos());
				}
			}
			for (int i = 0; i < part.count; i++)
			{
				if (texture.getIconName().contains("grass_side_overlay") || texture.getIconName().contains("grass_top"))
				{
					GlStateManager.disableLighting();
				}
				if (doubleRender)
				{
					RenderingUtil.renderPartWithIcon(RenderingReference.model, part.name().concat(String.valueOf(i)), Minecraft.getMinecraft().getTextureMapBlocks().getTextureExtry(cabinet.getDefaultTexture()), worldRenderer, -1);
				}
				RenderingUtil.renderPartWithIcon(RenderingReference.model, part.name().concat(String.valueOf(i)), texture, worldRenderer, color);
				if (texture.getIconName().contains("grass_side_overlay") || texture.getIconName().contains("grass_top"))
				{
					GlStateManager.enableLighting();
				}
			}
		}
		renderContents(cabinet);
		GlStateManager.disableBlend();
	}

	private void renderContents(CabinetTileEntity cabinet)
	{
		if (!cabinet.isHidden() && Reference.showItemsTileEntity)
		{
			float scale = .4f;
			GlStateManager.scale(scale, scale, scale);
			ItemStack[] contents = cabinet.getContents();
			GlStateManager.translate(0f, 1.23f, -.05f);
			for (int c = 0; c < contents.length - 2; c++)
			{
				ItemStack stack = contents[c];
				if (contents[c] == null)
				{
					continue;
				}
				for (int e = 0; e < c / 3 + 1; e++)
				{
					float i, j, k;
					i = ((c % 3) * .65f) - .8f * scale - .325f;
					j = ((-c / 3) * .65f) - .54f - (.1f * (c / 3));
					k = ((c / 3) * .5f) - .56f * scale;
					GlStateManager.pushMatrix();
					GlStateManager.translate(i, j, k + (-.5f * e));
					if (!(stack.getItem() instanceof ItemBlock))
					{
						GlStateManager.scale(0.5f, 0.5f, 0.5f);
					}
					else
					{
						Block block = Block.getBlockFromItem(stack.getItem());
						if (block instanceof IPlantable)
						{
							GlStateManager.scale(0.5f, 0.5f, 0.5f);
						}
					}
					if (stack.getItem() instanceof ItemBanner)
					{
						GlStateManager.rotate(180, 0, 1, 0);
					}
					if (stack.getItem() instanceof ItemAnvilBlock)
					{
						GlStateManager.rotate(90, 0, 1, 0);
					}
					if (stack.stackSize >= e + 1)
					{
						Minecraft.getMinecraft().getRenderItem().renderItemModel(stack);
					}
					GlStateManager.popMatrix();
				}
			}
		}
	}
}
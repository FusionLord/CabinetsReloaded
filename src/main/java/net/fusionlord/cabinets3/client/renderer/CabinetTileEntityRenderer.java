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
		GlStateManager.translate((float) x + .5f, (float) y, (float) z + .5f);
		CabinetTileEntity te = (CabinetTileEntity) tileEntity;
		renderCabinet(te);
		GlStateManager.popMatrix();
	}

	public void renderCabinet(CabinetTileEntity cabinet)
	{
		WorldRenderer worldRenderer = Tessellator.getInstance().getWorldRenderer();

		GlStateManager.enableBlend();
		GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GlStateManager.disableLighting();

		int facing = cabinet.getFacing();
		int face = 0;
		switch (facing)
		{
			case 0:
				face = 180;
				break;
			case 1:
				face = 90;
				break;
			case 2:
				face = 0;
				break;
			case 3:
				face = 270;
				break;
		}

		bindTexture(TextureMap.locationBlocksTexture);
		for (CabinetParts part : CabinetParts.values())
		{
			TextureAtlasSprite texture = Minecraft.getMinecraft().getTextureMapBlocks().getTextureExtry(cabinet.getTexture(part.ordinal()));
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
			for (int i = 0; i < part.count; i++)
			{
				if (part == CabinetParts.Bottom)
				{
					GlStateManager.rotate(180, 0F, 1F, 0F);
				}
				if (!((part == CabinetParts.Bottom || part == CabinetParts.Top)))
				{
					GlStateManager.rotate(face, 0F, 1F, 0F);
				}

				float a = 90 * cabinet.getDoorAngle();
				float m = 0.0625F;
				if (part == CabinetParts.Half_Door && cabinet.getBlockMetadata() == 2)
				{
					GlStateManager.translate(m * 7.5f, m * 1f, m * 7.5f);
					GlStateManager.rotate(a, 0f, 1f, 0f);
					RenderingUtil.renderPartWithIcon(RenderingReference.model, part.name().concat(String.valueOf(0)), texture, worldRenderer, color);
					GlStateManager.rotate(a, 0f, -1f, 0f);
					GlStateManager.translate(m * -7.5f, m * -1f, m * -7.5f);
					GlStateManager.translate(m * -7.5f, m * 1f, m * 7.5f);
					GlStateManager.rotate(a, 0f, -1f, 0f);
					RenderingUtil.renderPartWithIcon(RenderingReference.model, part.name().concat(String.valueOf(1)), texture, worldRenderer, color);
					GlStateManager.rotate(a, 0f, 1f, 0f);
					GlStateManager.translate(m * 7.5f, m * -1f, m * -7.5f);
				}
				else if (part == CabinetParts.Right_Door && cabinet.getBlockMetadata() == 1)
				{
					GlStateManager.translate(m * 7.5f, m * 1f, m * 7.5f);
					GlStateManager.rotate(a, 0f, 1f, 0f);
					GlStateManager.rotate(180, 0F, 1F, 0F);
					RenderingUtil.renderPartWithIcon(RenderingReference.model, part.name().concat(String.valueOf(0)), texture, worldRenderer, color);
					GlStateManager.rotate(180, 0F, -1F, 0F);
					GlStateManager.rotate(a, 0f, -1F, 0f);
					GlStateManager.translate(m * -7.5f, m * -1f, m * -7.5f);
				}
				else if (part == CabinetParts.Left_Door && cabinet.getBlockMetadata() == 0)
				{
					GlStateManager.translate(m * -7.5f, m * 1f, m * 7.5f);
					GlStateManager.rotate(a, 0f, -1f, 0f);
					GlStateManager.rotate(180, 0F, 1F, 0F);
					RenderingUtil.renderPartWithIcon(RenderingReference.model, part.name().concat(String.valueOf(0)), texture, worldRenderer, color);
					GlStateManager.rotate(180, 0F, -1F, 0F);
					GlStateManager.rotate(a, 0f, 1f, 0f);
					GlStateManager.translate(m * 7.5f, m * -1f, m * -7.5f);
				}
				else if (!(part == CabinetParts.Left_Door || part == CabinetParts.Right_Door || part == CabinetParts.Half_Door))
				{
					if (Reference.isTextureClimbable(texture.getIconName()) || Reference.isTextureDoubleRendered(texture.getIconName()))
					{
						GlStateManager.translate(0.001f, 0f, 0f);
						RenderingUtil.renderPartWithIcon(RenderingReference.model, part.name().concat(String.valueOf(i)), Minecraft.getMinecraft().getTextureMapBlocks().getTextureExtry(cabinet.getDefaultTexture()), worldRenderer, -1);
					}
					RenderingUtil.renderPartWithIcon(RenderingReference.model, part.name().concat(String.valueOf(i)), texture, worldRenderer, color);
				}

				if (!((part == CabinetParts.Bottom || part == CabinetParts.Top)))
				{
					GlStateManager.rotate(face, 0F, -1F, 0F);
				}
				if ((part == CabinetParts.Bottom))
				{
					GlStateManager.rotate(180, 0F, -1F, 0F);
				}
			}
		}
		GlStateManager.rotate(face, 0F, 1F, 0F);

		if (!cabinet.isHidden() && Reference.showItemsTileEntity)
		{
			float scale = .4f;
			GlStateManager.scale(scale, scale, scale);
			ItemStack[] contents = cabinet.getContents();
			GlStateManager.translate(0f, 2.5f, 0f);
			for (int c = 0; c < contents.length; c++)
			{
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
					if (!(contents[c].getItem() instanceof ItemBlock))
					{
						GlStateManager.scale(0.5f, 0.5f, 0.5f);
					}
					else
					{
						Block block = Block.getBlockFromItem(contents[c].getItem());
						if (block instanceof IPlantable)
						{
							GlStateManager.scale(0.5f, 0.5f, 0.5f);
						}
					}
					if (contents[c].getItem() instanceof ItemBanner)
					{
						GlStateManager.rotate(180, 0, 1, 0);
					}
					if (contents[c].stackSize >= e + 1)
					{
						Minecraft.getMinecraft().getRenderItem().renderItemModel(contents[c]);
					}
					GlStateManager.popMatrix();
				}
			}
		}
		GlStateManager.rotate(face, 0F, -1F, 0F);
		GlStateManager.enableLighting();
		GlStateManager.disableBlend();
	}
}
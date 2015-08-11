package net.fusionlord.cabinets3.client.renderer;

import net.fusionlord.fusionutil.client.RenderingUtil;
import net.fusionlord.cabinets3.Reference;
import net.fusionlord.cabinets3.tileentity.CabinetTileEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBanner;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
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
		renderCabinet(te, tileEntity.getWorld(), te.getPos());
		GlStateManager.popMatrix();
	}

	public void renderCabinet(CabinetTileEntity cabinet, World world, BlockPos blockPos)
	{
		WorldRenderer worldRenderer = Tessellator.getInstance().getWorldRenderer();
		float lightValue = Reference.cabinet.getLightValue(world, blockPos);
		worldRenderer.setBrightness((int) lightValue);

		GlStateManager.enableBlend();
		GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

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

		ItemStack displayStack = cabinet.getDisplayStack();
		if (displayStack == null)
		{
			displayStack = new ItemStack(Blocks.planks, 1, 0);
		}

		bindTexture(TextureMap.locationBlocksTexture);
		for (int side = 0; side < 6; side++)
		{
			CabinetParts part = CabinetParts.values()[side];
			for (int i = 1; i <= part.count; i++)
			{
				if ((side == EnumFaceDirection.DOWN.ordinal() && i == 2))
				{
					GlStateManager.rotate(180, 0F, 1F, 0F);
				}
				if (!((side == EnumFaceDirection.DOWN.ordinal() && i == 2) || (side == EnumFaceDirection.UP.ordinal()
						&& i == 1)))
				{
					GlStateManager.rotate(face, 0F, 1F, 0F);
				}
				Minecraft mc = Minecraft.getMinecraft();
				BlockModelShapes blockModelRenderer = mc.getBlockRendererDispatcher().getBlockModelShapes();
				TextureAtlasSprite texture = blockModelRenderer.getTexture(((ItemBlock) displayStack.getItem()).getBlock().getStateFromMeta(displayStack.getItemDamage()));
				bindTexture(TextureMap.locationBlocksTexture);
				RenderingUtil.renderPartWithIcon(
						                                RenderingReference.model,
						                                part.name().concat(Integer.toString(i)),
						                                texture,
						                                worldRenderer,
						                                -1
				);
				if (!((side == EnumFaceDirection.DOWN.ordinal() && i == 2) || (side == EnumFaceDirection.UP.ordinal()
						&& i == 1)))
				{
					GlStateManager.rotate(face, 0F, -1F, 0F);
				}
				if ((side == EnumFaceDirection.DOWN.ordinal() && i == 2))
				{
					GlStateManager.rotate(180, 0F, -1F, 0F);
				}
			}
		}

		GlStateManager.rotate(face - 90, 0F, 1F, 0F);

		float a = 90 * cabinet.getDoorAngle();
		float m = 0.0625F;
		switch (cabinet.getBlockMetadata())
		{
			case 0:
				bindTexture(RenderingReference.door);
				GlStateManager.translate(m * 7f, m * 1f, m * 8f - ((a / 90) * m));
				GlStateManager.rotate(a, 0f, -1f, 0f);
				RenderingReference.doorModel.renderPart("LeftDoor");
				GlStateManager.rotate(a, 0f, 1f, 0f);
				GlStateManager.translate(-(m * 7f), -(m * 1f), -(m * 8f) + ((a / 90) * m));
				break;

			case 1:
				bindTexture(RenderingReference.door);
				GlStateManager.translate(m * 7f, m * 1f, -(m * 8f) + ((a / 90) * m));
				GlStateManager.rotate(a, 0f, 1f, 0f);
				RenderingReference.doorModel.renderPart("RightDoor");
				GlStateManager.rotate(a, 0f, -1f, 0f);
				GlStateManager.translate(-(m * 7f), -(m * 1f), m * 8f - ((a / 90) * m));
				break;

			case 2:
				bindTexture(RenderingReference.doubleDoor);

				GlStateManager.translate(m * 7f, m * 1f, m * 8f - ((a / 90) * m));
				GlStateManager.rotate(a, 0f, -1f, 0f);
				RenderingReference.doorModel.renderPart("DLeftDoor");
				GlStateManager.rotate(a, 0f, 1f, 0f);
				GlStateManager.translate(-(m * 7f), -(m * 1f), -(m * 8f) + ((a / 90) * m));
				GlStateManager.translate(m * 7f, m * 1f, -(m * 8f) + ((a / 90) * m));
				GlStateManager.rotate(a, 0f, 1f, 0f);
				RenderingReference.doorModel.renderPart("DRightDoor");
				GlStateManager.rotate(a, 0f, -1f, 0f);
				GlStateManager.translate(-(m * 7f), -(m * 1f), m * 8f - ((a / 90) * m));
				break;
		}

		GlStateManager.rotate(90, 0F, 1F, 0F);

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
		GlStateManager.disableBlend();
	}
}
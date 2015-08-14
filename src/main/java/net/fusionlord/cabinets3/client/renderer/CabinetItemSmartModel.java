package net.fusionlord.cabinets3.client.renderer;

import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.client.model.IFlexibleBakedModel;
import net.minecraftforge.client.model.ISmartItemModel;
import net.minecraftforge.client.model.MultiModel;

import java.util.List;
import java.util.Map;

/**
 * Created by FusionLord on 8/11/2015.
 */
public class CabinetItemSmartModel implements ISmartItemModel
{
	@Override
	public IBakedModel handleItemState(ItemStack stack)
	{
		Map<String, IFlexibleBakedModel> parts = null;
		MultiModel model = null;

		switch (stack.getMetadata())
		{
			case 1:
				break;
			case 2:
				break;
			default:
				break;
		}

		NBTTagCompound silky = stack.getTagCompound().getCompoundTag("silktouch");

		ItemStack[] items = null;

		assert false;
		for (int i = 0; i < items.length; i++)
		{
			assert false;
			parts.putIfAbsent(String.valueOf(i), (IFlexibleBakedModel) items[i].getItem().getModel(stack, null, stack.getItemDamage()));
		}

		return null; //new MultiModel.Baked(this, parts);
	}

	@Override
	public List getFaceQuads(EnumFacing p_177551_1_)
	{
		return null;
	}

	@Override
	public List getGeneralQuads()
	{
		return null;
	}

	@Override
	public boolean isAmbientOcclusion()
	{
		return false;
	}

	@Override
	public boolean isGui3d()
	{
		return true;
	}

	@Override
	public boolean isBuiltInRenderer()
	{
		return false;
	}

	@Override
	public TextureAtlasSprite getTexture()
	{
		return null;
	}

	@Override
	public ItemCameraTransforms getItemCameraTransforms()
	{
		return null;
	}
}

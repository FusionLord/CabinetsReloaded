package net.fusionlord.cabinets3.block;

import net.fusionlord.cabinets3.CabinetsReloaded;
import net.fusionlord.cabinets3.Reference;
import net.fusionlord.cabinets3.item.CabinetItem;
import net.fusionlord.cabinets3.tileentity.CabinetTileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.client.particle.EntityDiggingFX;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeModContainer;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;

public class CabinetBlock extends BlockContainer
{
	public CabinetBlock()
	{
		super(Material.wood);
		setHardness(1F);
		setCreativeTab(CreativeTabs.tabDecorations);
		GameRegistry.registerBlock(this, CabinetItem.class, "cabinet");
		useNeighborBrightness = true;
	}

	public static boolean canPlace(World world, BlockPos pos, EntityPlayer player)
	{
		int x = pos.getX();
		int y = pos.getY();
		int z = pos.getZ();
		for (int x2 = -1; x2 < 2; x2++)
		{
			for (int y2 = -1; y2 < 2; y2++)
			{
				for (int z2 = -1; z2 < 2; z2++)
				{
					TileEntity tileEntity = world.getTileEntity(new BlockPos(x + x2, y + y2, z + z2));
					if (tileEntity != null && tileEntity instanceof CabinetTileEntity)
					{
						CabinetTileEntity cabinet2 = (CabinetTileEntity) tileEntity;

						if (!cabinet2.isOwner(player))
						{
							return false;
						}
					}
				}
			}
		}
		return true;
	}

	@Override
	public ItemStack getPickBlock(MovingObjectPosition target, World world, BlockPos pos, EntityPlayer player)
	{
		ItemStack itemStack = new ItemStack(this, 1);
		if (player.isSneaking())
		{
			NBTTagCompound silky = new NBTTagCompound();
			CabinetTileEntity cabinet = (CabinetTileEntity) world.getTileEntity(pos);
			cabinet.writeExtraNBT(silky);
			silky.removeTag("yaw");
			silky.removeTag("pitch");
			itemStack.setTagCompound(new NBTTagCompound());
			itemStack.getTagCompound().setTag("silktouch", silky);
		}
		return itemStack;
	}

	@Override
	public boolean isLadder(IBlockAccess world, BlockPos blockPos, EntityLivingBase entity)
	{
		CabinetTileEntity cabinet = (CabinetTileEntity) world.getTileEntity(blockPos);
		ItemStack stack = cabinet.getSecAbilityStack();
		if (stack != null && stack.getItem() instanceof ItemBlock)
		{
			Block block = Block.getBlockFromItem(stack.getItem());
			return block != null && block.isLadder(world, blockPos, entity);
		}
		return false;
	}

	@Override
	public void setBlockBoundsBasedOnState(IBlockAccess worldIn, BlockPos pos)
	{
		if (!ForgeModContainer.fullBoundingBoxLadders)
		{
			ForgeModContainer.fullBoundingBoxLadders = true;
		}
		float offset = 0.005F;
		this.setBlockBounds(0.0F + offset, 0F, 0.0F + offset, 1.0F - offset, 1.0F, 1.0F - offset);
	}

	@Override
	public boolean removedByPlayer(World world, BlockPos pos, EntityPlayer player, boolean willHarvest)
	{
		CabinetTileEntity cabinet = (CabinetTileEntity) world.getTileEntity(pos);
		if (cabinet.isOwner(player))
		{
			IBlockState state = world.getBlockState(pos);
			ItemStack tool = player.getCurrentEquippedItem();
			if (tool != null && (tool.getItem() instanceof ItemAxe || tool.getItem() instanceof ItemPickaxe) && EnchantmentHelper.getSilkTouchModifier(player))
			{
				ItemStack itemStack = new ItemStack(this, 1);
				NBTTagCompound silky = new NBTTagCompound();
				cabinet.writeExtraNBT(silky);
				silky.removeTag("yaw");
				silky.removeTag("pitch");
				itemStack.setTagCompound(new NBTTagCompound());
				itemStack.getTagCompound().setTag("silktouch", silky);
				if (!player.inventory.addItemStackToInventory(itemStack))
				{
					player.addChatComponentMessage(new ChatComponentText("Cannot break, you don't have room for it."));
					return false;
				}
			}
			else if (!player.capabilities.isCreativeMode)
			{
				getDrops(world, pos, state, 0).stream().filter(stack -> stack != null).forEach(stack -> Block.spawnAsEntity(world, pos, stack));
			}
			world.setBlockToAir(pos);
			world.removeTileEntity(pos);
			return true;
		}
		return false;
	}

	@Override
	public int getRenderType()
	{
		return 0;
	}

	@Override
	public boolean isOpaqueCube()
	{
		return false;
	}

	@Override
	public boolean isFullCube()
	{
		return false;
	}

	@Override
	public ArrayList<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune)
	{
		ArrayList<ItemStack> ret = new ArrayList<>();

		TileEntity te = world.getTileEntity(pos);
		if (te instanceof CabinetTileEntity)
		{
			CabinetTileEntity cabinet = (CabinetTileEntity) te;
			ret.add(new ItemStack(Reference.cabinet, 1));
			for (ItemStack stack : cabinet.getContents())
			{
				if (stack != null)
				{
					ret.add(stack);
				}
			}
		}
		return ret;
	}

	@Override
	public float getExplosionResistance(World world, BlockPos pos, Entity entity, Explosion explosion)
	{
		CabinetTileEntity cabinet = (CabinetTileEntity) world.getTileEntity(pos);
		if (cabinet != null && !cabinet.isLocked())
		{
			return Blocks.stone.getExplosionResistance(entity);
		}
		return Blocks.bedrock.getExplosionResistance(entity);
	}

	@Override
	public TileEntity createNewTileEntity(World world, int metadata)
	{
		return new CabinetTileEntity();
	}

	@Override
	public int getComparatorInputOverride(World world, BlockPos pos)
	{
		return Container.calcRedstoneFromInventory((CabinetTileEntity) world.getTileEntity(pos));
	}

	@Override
	public boolean hasComparatorInputOverride()
	{
		return true;
	}

	@Override
	public void onNeighborBlockChange(World world, BlockPos pos, IBlockState state, Block block)
	{
		int powered = world.isBlockIndirectlyGettingPowered(pos);
		CabinetTileEntity cabinet = (CabinetTileEntity) world.getTileEntity(pos);
		cabinet.setPowered(powered != 0);
		super.onNeighborBlockChange(world, pos, state, block);
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumFacing side, float hitX, float hitY, float hitZ)
	{
		int x = pos.getX();
		int y = pos.getY();
		int z = pos.getZ();
		TileEntity te = world.getTileEntity(pos);
		if (te == null || !(te instanceof CabinetTileEntity) || world.isRemote)
		{
			return true;
		}
		CabinetTileEntity cabinet = (CabinetTileEntity) te;

		if (cabinet.getOwner() == null)
		{
			cabinet.setOwner(player);
		}

		if (cabinet.isUseableByPlayer(player) && !cabinet.getBlocked())
		{
			player.openGui(CabinetsReloaded.instance, 0, world, x, y, z);
			return true;
		}
		return true;
	}

	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase entity, ItemStack stack)
	{
		if (entity instanceof EntityPlayer)
		{
			CabinetTileEntity cabinet = (CabinetTileEntity) world.getTileEntity(pos);
			EntityPlayer player = (EntityPlayer) entity;

			cabinet.setFacing(player.getHorizontalFacing());

			cabinet.setVerticalFacing(EnumFacing.NORTH);
			if (player.rotationPitch < -50)
			{
				cabinet.setVerticalFacing(EnumFacing.DOWN);
			}
			if (player.rotationPitch > 50)
			{
				cabinet.setVerticalFacing(EnumFacing.UP);
			}


			if (!player.getHeldItem().hasTagCompound())
			{
				cabinet.setOwner(player);
			}
			else
			{
				cabinet.readExtraNBT(player.getHeldItem().getTagCompound().getCompoundTag("silktouch"));
			}
			cabinet.markForUpdate();
		}
	}

	@Override
	public boolean shouldSideBeRendered(IBlockAccess worldIn, BlockPos pos, EnumFacing side)
	{
		return false;
	}

	@Override
	public int getLightValue(IBlockAccess world, BlockPos blockPos)
	{
		CabinetTileEntity cabinet = (CabinetTileEntity) world.getTileEntity(blockPos);
		if (cabinet != null)
		{
			ItemStack stack = cabinet.getSecAbilityStack();
			if (stack != null && stack.getItem() instanceof ItemBlock)
			{
				Block block = Block.getBlockFromItem(stack.getItem());
				return block != null ? block.getLightValue() : 0;
			}
		}
		return 0;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public boolean addDestroyEffects(World world, BlockPos pos, EffectRenderer effectRenderer)
	{
		CabinetTileEntity cabinet = (CabinetTileEntity) world.getTileEntity(pos);
		if (cabinet != null)
		{
			int x = pos.getX();
			int y = pos.getY();
			int z = pos.getZ();
			byte b0 = 4;

			for (int i1 = 0; i1 < b0; ++i1)
			{
				for (int j1 = 0; j1 < b0; ++j1)
				{
					for (int k1 = 0; k1 < b0; ++k1)
					{
						double d0 = (double) x + ((double) i1 + 0.5D) / (double) b0;
						double d1 = (double) y + ((double) j1 + 0.5D) / (double) b0;
						double d2 = (double) z + ((double) k1 + 0.5D) / (double) b0;
						EntityDiggingFX fx = (EntityDiggingFX) new EntityDiggingFX.Factory().getEntityFX(0, world, d0, d1, d2, 0.0D, 0.0D, 0.0D, Block.getIdFromBlock(world.getBlockState(pos).getBlock()));
						fx.multipleParticleScaleBy(0.6F);
						fx.setParticleIcon(Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(cabinet.getTexture(1)));

						effectRenderer.addEffect(fx);
					}
				}
			}
		}
		return true;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public boolean addHitEffects(World world, MovingObjectPosition target, EffectRenderer effectRenderer)
	{
		CabinetTileEntity cabinet = (CabinetTileEntity) world.getTileEntity(target.getBlockPos());
		if (cabinet != null)
		{
			int x = target.getBlockPos().getX();
			int y = target.getBlockPos().getY();
			int z = target.getBlockPos().getZ();
			byte b0 = 4;

			for (int i1 = 0; i1 < b0; ++i1)
			{
				for (int j1 = 0; j1 < b0; ++j1)
				{
					for (int k1 = 0; k1 < b0; ++k1)
					{
						double d0 = (double) x + ((double) i1 + 0.5D) / (double) b0;
						double d1 = (double) y + ((double) j1 + 0.5D) / (double) b0;
						double d2 = (double) z + ((double) k1 + 0.5D) / (double) b0;
						EntityDiggingFX fx = (EntityDiggingFX) new EntityDiggingFX.Factory().getEntityFX(0, world, d0, d1, d2, 0.0D, 0.0D, 0.0D, Block.getIdFromBlock(world.getBlockState(target.getBlockPos()).getBlock()));
						fx.multipleParticleScaleBy(0.6F);
						fx.setParticleIcon(Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(cabinet.getTexture(cabinet.getFacing().ordinal())));

						effectRenderer.addEffect(fx);
					}
				}
			}
		}
		return true;
	}
}
package net.fusionlord.cabinets3.block;

import net.fusionlord.cabinets3.CabinetsReloaded;
import net.fusionlord.cabinets3.Reference;
import net.fusionlord.cabinets3.item.CabinetItem;
import net.fusionlord.cabinets3.tileentity.CabinetTileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.BlockWorkbench;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.client.particle.EntityDiggingFX;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.Container;
import net.minecraft.item.*;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.List;

public class CabinetBlock extends BlockContainer
{
	public static final PropertyEnum TYPE = PropertyEnum.create("cabinet_type", Types.class);

	public enum Types implements IStringSerializable
	{
		LEFT,
		RIGHT,
		DOUBLE
		;

		@Override
		public String getName()
		{
			return name().toLowerCase();
		}

		public int getID()
		{
			return ordinal();
		}

		@Override
		public String toString()
		{
			return name().toLowerCase();
		}
	}

	public CabinetBlock()
	{
		super(Material.wood);
		setHardness(1.5f);
		setCreativeTab(CreativeTabs.tabDecorations);
		setDefaultState(this.blockState.getBaseState().withProperty(TYPE, Types.LEFT));
		GameRegistry.registerBlock(this, CabinetItem.class, "cabinet");
	}

	@Override
	protected BlockState createBlockState()
	{
		return new BlockState(this, new IProperty[] {TYPE});
	}

	@Override
	public IBlockState getStateFromMeta(int meta)
	{
		if (meta < 0 || meta > 2) meta = 0;
		return getDefaultState().withProperty(TYPE, Types.values()[meta]);
	}

	@Override
	public int getMetaFromState(IBlockState state)
	{
		return ((Types) state.getValue(TYPE)).getID();
	}

	@Override
	public ItemStack getPickBlock(MovingObjectPosition target, World world, BlockPos pos)
	{
		return new ItemStack(Item.getItemFromBlock(this), 1, getMetaFromState(world.getBlockState(pos)));
	}

	public static boolean getBlocked(World world, BlockPos pos, int facing)
	{
		int x = pos.getX();
		int y = pos.getY();
		int z = pos.getZ();
		boolean blocked = false;
		switch (facing)
		{
			case 0:
				blocked = !world.isAirBlock(new BlockPos(x, y, z - 1));
				break;
			case 1:
				blocked = !world.isAirBlock(new BlockPos(x + 1, y, z));
				break;
			case 2:
				blocked = !world.isAirBlock(new BlockPos(x, y, z + 1));
				break;
			case 3:
				blocked = !world.isAirBlock(new BlockPos(x - 1, y, z));
				break;
		}
		return blocked;
	}

	@SideOnly(Side.CLIENT)
	public void getSubBlocks(Item itemIn, CreativeTabs tab, List list)
	{
		for (int j = 0; j < Types.values().length; ++j)
		{
			list.add(new ItemStack(itemIn, 1, j));
		}
	}

	@Override
	public boolean removedByPlayer(World world, BlockPos pos, EntityPlayer player, boolean willHarvest)
	{
		TileEntity te = world.getTileEntity(pos);
		if (te != null && te instanceof CabinetTileEntity)
		{
			CabinetTileEntity cabinet = (CabinetTileEntity) te;
			if (cabinet.isOwner(player))
			{
				IBlockState state = world.getBlockState(pos);
				int meta = state.getBlock().getMetaFromState(state) << 12;
				boolean silky = false;
				ItemStack tool = player.getCurrentEquippedItem();
				if (tool != null && (tool.getItem() instanceof ItemAxe || tool.getItem() instanceof ItemPickaxe))
				{
					silky = EnchantmentHelper.getSilkTouchModifier(player);
				}
				if (player.capabilities.isCreativeMode || silky)
				{
					ItemStack stack = new ItemStack(this, 1, meta);
					boolean storeData = false;
					for (ItemStack itemStack : cabinet.getContents())
					{
						if (storeData)
						{
							break;
						}
						if (itemStack != null)
						{
							storeData = true;
						}
					}
					if (storeData || cabinet.getDisplayStack() != null)
					{
						NBTTagCompound cabinetTag = new NBTTagCompound();
						cabinet.writeExtraNBT(cabinetTag);
						stack.setTagCompound(new NBTTagCompound());
						stack.setTagInfo("silktouch", cabinetTag);
					}
					dropBlockAsItem(world, pos, world.getBlockState(pos), 0);
				}
				else
				{
					getDrops(world, pos, state, 0).stream().filter(stack -> stack != null).forEach(stack -> Block.spawnAsEntity(world, pos, stack));
				}
				world.setBlockToAir(pos);
				world.removeTileEntity(pos);
			}
		}
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
			ret.add(new ItemStack(Reference.cabinet, 1, ((Types)state.getValue(TYPE)).getID()));
			for (ItemStack stack : cabinet.getContents())
			{
				if (stack != null)
				{
					ret.add(stack);
				}
			}
			if (cabinet.getDisplayStack() != null)
			{
				ret.add(cabinet.getDisplayStack());
			}
		}
		return ret;
	}

	@Override
	public float getExplosionResistance(World world, BlockPos pos, Entity entity, Explosion explosion)
	{
		TileEntity tileEntity = world.getTileEntity(pos);
		if (tileEntity instanceof CabinetTileEntity)
		{
			CabinetTileEntity cabinet = (CabinetTileEntity) tileEntity;
			if (!cabinet.isLocked())
			{
				return Blocks.stone.getExplosionResistance(entity);
			}
		}
		return Blocks.bedrock.getExplosionResistance(entity);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean addHitEffects(World worldObj, MovingObjectPosition target, EffectRenderer effectRenderer)
	{
		Block block = worldObj.getBlockState(target.getBlockPos()).getBlock();

		if (worldObj.isAirBlock(target.getBlockPos()))
		{
			float f = 0.1F;

			int side = target.sideHit.getIndex();

			double d1 = target.getBlockPos().getY() + block.getBlockBoundsMinY() - (double) f * side == 0 ? 1 : -1;
			double d2 = target.getBlockPos().getZ() + block.getBlockBoundsMinZ() - (double) f * side == 2 ? 1 : -1;
			double d0 = target.getBlockPos().getX() + block.getBlockBoundsMinX() - (double) f * side == 4 ? 1 : -1;

			TileEntity te = worldObj.getTileEntity(target.getBlockPos());
			if (te instanceof CabinetTileEntity)
			{
				CabinetTileEntity cabinet = (CabinetTileEntity) te;
				block = cabinet.getDisplayStack() != null ? Block.getBlockFromItem(cabinet.getDisplayStack().getItem()) : Blocks.planks;
			}
			effectRenderer.addEffect(new EntityDiggingFX.Factory().getEntityFX(0,
											 worldObj,
											 d0,
											 d1,
											 d2,
											 0.0D,
											 0.0D,
											 0.0D,
											 Block.getIdFromBlock(block)
									 )
					/*.applyColourMultiplier(target.getBlockPos().getX(), target.getBlockPos().getY(), target.getBlockPos().getZ()).multiplyVelocity(0.2F)*/
					.multipleParticleScaleBy(0.6F));
		}
		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean addDestroyEffects(World world, BlockPos pos, EffectRenderer effectRenderer)
	{
		int x = pos.getX();
		int y = pos.getY();
		int z = pos.getZ();
		Block block = world.getBlockState(pos).getBlock();

		TileEntity te = world.getTileEntity(pos);
		if (te instanceof CabinetTileEntity)
		{
			CabinetTileEntity cabinet = (CabinetTileEntity) te;
			if (cabinet.getDisplayStack() != null)
			{
				block = Block.getBlockFromItem(cabinet.getDisplayStack().getItem());
			}
			else
			{
				block = Blocks.planks;
			}
		}

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

					effectRenderer.addEffect(new EntityDiggingFX.Factory().getEntityFX(0,
																					   world,
																					   d0,
																					   d1,
																					   d2,
																					   0.0D,
																					   0.0D,
																					   0.0D,
																					   Block.getIdFromBlock(block)
											 )
					/*.applyColourMultiplier(target.getBlockPos().getX(), target.getBlockPos().getY(), target.getBlockPos().getZ()).multiplyVelocity(0.2F)*/
													 .multipleParticleScaleBy(0.6F));
				}
			}
		}
		return true;
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
	public int getRenderType()
	{
		return 0;
	}

	@Override
	public boolean isFullCube()
	{
		return false;
	}

	@Override
	public boolean isSideSolid(IBlockAccess world, BlockPos pos, EnumFacing side)
	{
		EnumFacing face = EnumFacing.DOWN;
		TileEntity te = world.getTileEntity(pos);
		if (te != null && te instanceof CabinetTileEntity)
		{
			switch (((CabinetTileEntity) te).getFacing())
			{
				case 0:
					face = EnumFacing.NORTH;
					break;
				case 1:
					face = EnumFacing.EAST;
					break;
				case 2:
					face = EnumFacing.SOUTH;
					break;
				case 3:
					face = EnumFacing.WEST;
					break;
			}
		}
		return side != face;
	}

	@Override
	public boolean isOpaqueCube()
	{
		return false;
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
		if (cabinet.getDisplayStack() != null)
		{
			Block baseBlock = Block.getBlockFromItem(cabinet.getDisplayStack().getItem());
			if (baseBlock != null && baseBlock instanceof BlockWorkbench && side == EnumFacing.UP)
			{
				player.openGui(CabinetsReloaded.instance, 1, world, x, y, z);
				return true;
			}
		}

		if (cabinet.getOwner() == null)
		{
			cabinet.setOwner(player);
		}

		boolean blocked = getBlocked(world, pos, cabinet.getFacing());
		if (cabinet.isUseableByPlayer(player) && !blocked)
		{
			player.openGui(CabinetsReloaded.instance, 0, world, x, y, z);
			return true;
		}
		return true;
	}

	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase entity, ItemStack stack)
	{
		if (world.isRemote)
		{
			return;
		}
		int facing = MathHelper.floor_double(entity.rotationYaw * 4F / 360F + 0.5D) & 3;
		TileEntity te = world.getTileEntity(pos);
		if (te != null && te instanceof CabinetTileEntity && entity instanceof EntityPlayer)
		{
			CabinetTileEntity cabinet = (CabinetTileEntity) te;
			EntityPlayer player = (EntityPlayer) entity;
			if (!canPlace(world, pos, player, cabinet))
			{
				world.setBlockToAir(pos);
				world.removeTileEntity(pos);
				ItemStack newStack = stack.copy();
				newStack.stackSize = 1;
				player.inventory.addItemStackToInventory(newStack);
				return;
			}
			if (!stack.hasTagCompound())
			{
				cabinet.setOwner(player);
				cabinet.setFacing(facing);
			}
			else
			{
				cabinet.readExtraNBT(stack.getTagCompound().getCompoundTag("silktouch"));
				cabinet.setFacing(facing);
				cabinet.sync();
			}
		}
	}

	private boolean canPlace(World world, BlockPos pos, EntityPlayer player, CabinetTileEntity cabinet)
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
						if (cabinet != cabinet2)
						{
							if (!cabinet2.isOwner(player))
							{
								return false;
							}
						}
					}
				}
			}
		}
		return true;
	}

	@Override
	public int getLightValue(IBlockAccess world, BlockPos pos)
	{
		TileEntity te = world.getTileEntity(pos);
		if (te instanceof CabinetTileEntity)
		{
			CabinetTileEntity cabinet = (CabinetTileEntity) te;
			if (cabinet.getDisplayStack() != null)
			{
				Block baseBlock = Block.getBlockFromItem(cabinet.getDisplayStack().getItem());
				if (baseBlock != null)
				{
					return baseBlock.getLightValue();
				}
			}
		}
		return getLightValue();
	}

	@Override
	public float getBlockHardness(World world, BlockPos pos)
	{
		TileEntity te = world.getTileEntity(pos);
		if (te instanceof CabinetTileEntity)
		{
			CabinetTileEntity cabinet = (CabinetTileEntity) te;
			if (cabinet.getDisplayStack() != null)
			{
				Block baseBlock = Block.getBlockFromItem(cabinet.getDisplayStack().getItem());
				if (baseBlock != null)
				{
					return baseBlock.getBlockHardness(world, pos);
				}
			}
		}
		return blockHardness;
	}

	@Override
	public int damageDropped(IBlockState state)
	{
		return ((Types)state.getValue(TYPE)).getID();
	}

	@Override
	public boolean shouldSideBeRendered(IBlockAccess worldIn, BlockPos pos, EnumFacing side)
	{
		return false;
	}
}
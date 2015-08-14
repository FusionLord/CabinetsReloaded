package net.fusionlord.cabinets3.block;

import net.fusionlord.cabinets3.CabinetsReloaded;
import net.fusionlord.cabinets3.Reference;
import net.fusionlord.cabinets3.item.CabinetItem;
import net.fusionlord.cabinets3.tileentity.CabinetTileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockState;
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
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.item.Item;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeModContainer;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.List;

public class CabinetBlock extends BlockContainer
{
	public static final PropertyEnum TYPE = PropertyEnum.create("cabinet_type", Types.class);

	public CabinetBlock()
	{
		super(Material.wood);
		setHardness(1.5f);
		setCreativeTab(CreativeTabs.tabDecorations);
		setDefaultState(this.blockState.getBaseState().withProperty(TYPE, Types.LEFT));
		GameRegistry.registerBlock(this, CabinetItem.class, "cabinet");
		useNeighborBrightness = true;
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

	public int getFixedID(EnumFacing sideHit, int facing)
	{
		if (sideHit == EnumFacing.DOWN)
		{
			return 0;
		}
		if (sideHit == EnumFacing.UP)
		{
			return 1;
		}
		switch (sideHit)
		{
			case NORTH:
				switch (facing)
				{
					case 0:
						return 3;
					case 1:
						return 5;
					case 2:
						return 4;
					case 3:
						return 2;
				}
				break;
			case SOUTH:
				switch (facing)
				{
					case 0:
						return 4;
					case 1:
						return 2;
					case 2:
						return 3;
					case 3:
						return 5;
				}
				break;
			case WEST:
				switch (facing)
				{
					case 0:
						return 5;
					case 1:
						return 4;
					case 2:
						return 2;
					case 3:
						return 3;
				}
				break;
			case EAST:
				switch (facing)
				{
					case 0:
						return 2;
					case 1:
						return 3;
					case 2:
						return 5;
					case 3:
						return 4;
				}
				break;
		}
		return 0;
	}

	@Override
	protected BlockState createBlockState()
	{
		return new BlockState(this, TYPE);
	}

	@Override
	public IBlockState getStateFromMeta(int meta)
	{
		if (meta < 0 || meta > 2)
		{
			meta = 0;
		}
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

	@SideOnly(Side.CLIENT)
	public void getSubBlocks(Item itemIn, CreativeTabs tab, List list)
	{
		for (int j = 0; j < Types.values().length; ++j)
		{
			list.add(new ItemStack(itemIn, 1, j));
		}
	}

	@Override
	public boolean isLadder(IBlockAccess world, BlockPos pos, EntityLivingBase entity)
	{
		CabinetTileEntity cabinet = (CabinetTileEntity) world.getTileEntity(pos);
		Vec3 entityPos = new Vec3(entity.posX, entity.posY, entity.posZ);
		Vec3 pos1 = entityPos.subtract((double) pos.getX(), (double) pos.getY(), (double) pos.getZ());
		EnumFacing face = EnumFacing.getFacingFromVector((float) pos1.xCoord, (float) pos1.yCoord, (float) pos1.zCoord);

		if (pos1.xCoord > pos1.zCoord)
		{
			face = EnumFacing.getFacingFromVector((float) pos1.xCoord, (float) pos1.yCoord, (float) Math.floor(pos1.zCoord));
		}
		if (pos1.xCoord < pos1.zCoord)
		{
			face = EnumFacing.getFacingFromVector((float) Math.floor(pos1.xCoord), (float) pos1.yCoord, (float) pos1.zCoord);
		}

		if (face == EnumFacing.UP || face == EnumFacing.DOWN)
		{
			return false;
		}
		String texture = cabinet.getTexture(getFixedID(face, cabinet.getFacing()));
		for (String s : Reference.CLIMBABLE)
		{
			if (s.equals(texture))
			{
				return true;
			}
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
//		super.setBlockBoundsBasedOnState(worldIn, pos);
		float offset = 0.005F;
		this.setBlockBounds(0.0F + offset, 0F - offset, 0.0F + offset, 1.0F - offset, 1.0F - offset, 1.0F - offset);
	}

	@Override
	public void onEntityCollidedWithBlock(World worldIn, BlockPos pos, Entity entityIn)
	{
		boolean state = ForgeModContainer.fullBoundingBoxLadders;
		ForgeModContainer.fullBoundingBoxLadders = true;
		super.onEntityCollidedWithBlock(worldIn, pos, entityIn);
		ForgeModContainer.fullBoundingBoxLadders = state;
	}

	@Override
	public void onLanded(World worldIn, Entity entityIn)
	{
//		super.onLanded(worldIn, entityIn);
	}

	@Override
	public boolean removedByPlayer(World world, BlockPos pos, EntityPlayer player, boolean willHarvest)
	{
		TileEntity te = world.getTileEntity(pos);
		if (te != null && te instanceof CabinetTileEntity)
		{
			CabinetTileEntity cabinet = (CabinetTileEntity) te;
			if (player.capabilities.isCreativeMode || cabinet.isOwner(player))
			{
				IBlockState state = world.getBlockState(pos);
				int meta = state.getBlock().getMetaFromState(state) << 12;
				boolean silky = false;
				ItemStack tool = player.getCurrentEquippedItem();
				if (tool != null && (tool.getItem() instanceof ItemAxe || tool.getItem() instanceof ItemPickaxe))
				{
					silky = EnchantmentHelper.getSilkTouchModifier(player);
				}
				if ((player.isSneaking() && player.capabilities.isCreativeMode) || silky)
				{
					ItemStack stack = new ItemStack(this, 1, meta);
					NBTTagCompound cabinetTag = new NBTTagCompound();
					cabinet.writeExtraNBT(cabinetTag);
					stack.setTagCompound(new NBTTagCompound());
					stack.setTagInfo("silktouch", cabinetTag);
					if (!player.inventory.addItemStackToInventory(stack))
					{
						player.addChatComponentMessage(new ChatComponentText("Cannot break, you don't have room for it."));
						return false;
					}
				}
				else
				{
					getDrops(world, pos, state, 0).stream().filter(stack -> stack != null).forEach(stack -> Block.spawnAsEntity(world, pos, stack));
				}
				world.setBlockToAir(pos);
				world.removeTileEntity(pos);
				return true;
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
			ret.add(new ItemStack(Reference.cabinet, 1, ((Types) state.getValue(TYPE)).getID()));
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
		String topTexture = cabinet.getTexture(getFixedID(side, cabinet.getFacing()));
		if (topTexture.equals("minecraft:blocks/crafting_table_top"))
		{
			player.openGui(CabinetsReloaded.instance, 1, world, x, y, z);
			return true;
		}

		if (topTexture.equals("minecraft:blocks/water_still") && player.getHeldItem() != null && player.getHeldItem().getItem() == Items.bucket)
		{
			player.inventory.consumeInventoryItem(Items.bucket);
			player.inventory.addItemStackToInventory(new ItemStack(Items.water_bucket));
			return true;
		}

		if (topTexture.equals("minecraft:blocks/lava_still") && player.getHeldItem() != null && player.getHeldItem().getItem() == Items.bucket)
		{
			player.inventory.consumeInventoryItem(Items.bucket);
			player.inventory.addItemStackToInventory(new ItemStack(Items.lava_bucket));
			return true;
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
	public int damageDropped(IBlockState state)
	{
		return ((Types) state.getValue(TYPE)).getID();
	}

	@Override
	public boolean shouldSideBeRendered(IBlockAccess worldIn, BlockPos pos, EnumFacing side)
	{
		return false;
	}

	@Override
	public int getLightValue(IBlockAccess world, BlockPos pos)
	{
		CabinetTileEntity cabinet = (CabinetTileEntity) world.getTileEntity(pos);
		if (cabinet != null)
		{
			for (String s : Reference.LIGHTS)
			{
				if (cabinet.getTexture(0).contains(s)
						    && cabinet.getTexture(1).contains(s)
						    && cabinet.getTexture(2).contains(s)
						    && cabinet.getTexture(3).contains(s)
						    && cabinet.getTexture(4).contains(s)
						    && cabinet.getTexture(5).contains(s))
				{
					return 15;
				}
			}
		}
		return 0;
	}

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
						int texID = getFixedID(target.sideHit, cabinet.getFacing());
						fx.setParticleIcon(Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(cabinet.getTexture(texID)));

						effectRenderer.addEffect(fx);
					}
				}
			}
		}
		return true;
	}

	public enum Types implements IStringSerializable
	{
		LEFT,
		RIGHT,
		DOUBLE;

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
}
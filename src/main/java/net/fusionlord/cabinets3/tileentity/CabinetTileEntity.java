package net.fusionlord.cabinets3.tileentity;

import net.fusionlord.cabinets3.Reference;
import net.fusionlord.cabinets3.abilities.*;
import net.fusionlord.cabinets3.client.renderer.CabinetParts;
import net.fusionlord.cabinets3.item.CabinetItem;
import net.fusionlord.cabinets3.packets.CabinetSyncPacket;
import net.fusionlord.cabinets3.util.DoorType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.server.gui.IUpdatePlayerListBox;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.fml.common.network.NetworkRegistry;

import java.util.*;

public class CabinetTileEntity extends TileEntity implements IUpdatePlayerListBox, IInventory, ISidedInventory
{
	private static final Map<Item, Class<? extends Ability>> abilityMap = new HashMap<>();

	static
	{
		abilityMap.put(Item.getItemFromBlock(Blocks.crafting_table), CraftingAbility.class);
		abilityMap.put(Item.getItemFromBlock(Blocks.furnace), SmeltingAbility.class);
		abilityMap.put(Item.getItemFromBlock(Blocks.ender_chest), EnderChestAbility.class);
//		abilityMap.put(Item.getItemFromBlock(Blocks.chest), ChestAbility.class);
//		abilityMap.put(Item.getItemFromBlock(Blocks.trapped_chest), ChestAbility.class);
	}

	private List<Ability> abilities = new ArrayList<>();
	private Ability ability;
	private ItemStack[] contents = new ItemStack[getSizeInventory()];
	private String[] textures;
	private String doorTexture;
	private DoorType doorType = DoorType.LEFT;
	private float doorAngle = 0F;
	private UUID owner;
	private String ownerName = "";
	private int numUsingPlayers = 0;
	private int sync = 1;
	private boolean needsUpdate = false;
	private boolean skinningPublic = false;
	private boolean hidden = false;
	private boolean locked = true;
	private boolean powered = false;
	private EnumFacing facing = EnumFacing.NORTH;
	private EnumFacing verticalFacing = EnumFacing.NORTH;

	public CabinetTileEntity()
	{
		super();
		textures = new String[CabinetParts.values().length + 1];
		for (CabinetParts part : CabinetParts.values())
		{
			textures[part.ordinal()] = getDefaultTexture();
		}
		doorTexture = doorType.getTexture();
	}

	@Override
	public int getSizeInventory()
	{
		return 11;
	}

	@Override
	public ItemStack getStackInSlot(int slot)
	{
		return contents[slot];
	}

	@Override
	public ItemStack decrStackSize(int slot, int amount)
	{
		ItemStack itemStack = getStackInSlot(slot);
		if (itemStack != null)
		{
			if (itemStack.stackSize <= amount)
			{
				setInventorySlotContents(slot, null);
			}
			else
			{
				itemStack = itemStack.splitStack(amount);
				if (itemStack.stackSize == 0)
				{
					setInventorySlotContents(slot, null);
				}
			}
		}

		return itemStack;
	}


	@Override
	public void setInventorySlotContents(int slot, ItemStack itemStack)
	{
		if (itemStack != null)
		{
			if (slot == contents.length - 2)
			{
				if (abilityMap.containsKey(itemStack.getItem()))
				{
					ability = getAbilityForItem(itemStack.getItem());
					contents[slot] = itemStack; //itemStack.splitStack(getInventoryStackLimit(slot));
				}
				else
				{
					if (isItemValidForSlot(slot + 1, itemStack) && getStackInSlot(slot + 1) != null)
					{
						setInventorySlotContents(slot + 1, itemStack);
					}
				}
			}
			else
			{
				contents[slot] = itemStack;
			}
			if (contents[slot] != null && contents[slot].stackSize < 1)
			{
				contents[slot] = null;
			}
		}
		else
		{
			contents[slot] = null;
			if (slot == contents.length - 2)
			{
				ability = new NoActionAbility();
			}
		}
		markDirty();
	}

	public int getInventoryStackLimit(int slot)
	{
		if (slot >= getSizeInventory() - 2)
		{
			return 1;
		}
		return getInventoryStackLimit();
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int slot)
	{
		return getStackInSlot(slot);
	}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack itemStack)
	{
		return (slot == contents.length - 2 && abilityMap.containsKey(itemStack.getItem())) || (slot != contents.length - 2 && !(itemStack.getItem() instanceof CabinetItem));
	}

	@Override
	public int getField(int id)
	{
		return 0;
	}

	@Override
	public void setField(int id, int value) {}

	@Override
	public int getFieldCount()
	{
		return 0;
	}

	@Override
	public void markDirty()
	{
//		super.markDirty();
		worldObj.checkLight(pos);
	}

	@Override
	public void clear()
	{
		contents = null;
		contents = new ItemStack[getSizeInventory()];
	}

	@Override
	public void readFromNBT(NBTTagCompound tag)
	{
		super.readFromNBT(tag);
		readExtraNBT(tag);
	}

	public void readExtraNBT(NBTTagCompound tag)
	{
		readGeneralNBT(tag);
		readAbilitiesNBT(tag);
		readInventoryNBT(tag);
		readTextureNBT(tag);
	}

	private void readAbilitiesNBT(NBTTagCompound tag)
	{
		NBTTagCompound abilitiesTag = tag.getCompoundTag("abilities");
		for (Class<? extends Ability> tempClass : abilityMap.values())
		{
			try
			{
				Ability temp = tempClass.getConstructor().newInstance();
				if (abilitiesTag.hasKey(temp.getTagName()))
				{
					temp.readNBT(abilitiesTag.getCompoundTag(temp.getTagName()));
					abilities.add(temp);
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}

	public void readGeneralNBT(NBTTagCompound tag)
	{
		if (tag.getBoolean("hasOwner"))
		{
			owner = new UUID(tag.getLong("UUID1"), tag.getLong("UUID2"));
			ownerName = tag.getString("ownerName");
		}
		else
		{
			owner = null;
			ownerName = "Unclaimed";
		}
		powered = tag.getBoolean("powered");
		readSettingsNBT(tag);
	}

	public void readInventoryNBT(NBTTagCompound tag)
	{
		NBTTagCompound inv = tag.getCompoundTag("inv");
		for (int i = 0; i < contents.length; i++)
		{
			contents[i] = ItemStack.loadItemStackFromNBT(inv.getCompoundTag("slot".concat(String.valueOf(i))));
		}
		if (ability == null && contents[contents.length - 2] != null)
		{
			ability = getAbilityForItem(contents[contents.length - 2].getItem());
		}
	}

	public void readTextureNBT(NBTTagCompound tag)
	{
		NBTTagCompound texTag = tag.getCompoundTag("texTag");
		for (CabinetParts part : CabinetParts.values())
		{
			if (texTag.hasKey("texture:".concat(part.name().toLowerCase())))
			{
				textures[part.ordinal()] = texTag.getString("texture:".concat(part.name().toLowerCase()));
			}
		}
		doorTexture = texTag.getString("texture:door");
		if (worldObj != null && worldObj.isRemote)
		{
			worldObj.checkLight(pos);
		}
	}

	public void readSettingsNBT(NBTTagCompound tag)
	{
		if (tag.hasKey("locked"))
		{
			this.locked = tag.getBoolean("locked");
		}
		if (tag.hasKey("hidden"))
		{
			this.hidden = tag.getBoolean("hidden");
		}
		if (tag.hasKey("yaw"))
		{
			this.facing = EnumFacing.values()[tag.getInteger("yaw")];
		}
		if (tag.hasKey("facing"))
		{
			this.facing = EnumFacing.values()[tag.getInteger("facing") + 2];
		}
		if (tag.hasKey("pitch"))
		{
			this.verticalFacing = EnumFacing.values()[tag.getInteger("pitch")];
		}
		if (tag.hasKey("skinning"))
		{
			this.skinningPublic = tag.getBoolean("skinning");
		}
		if (tag.hasKey("doorType"))
		{
			this.doorType = DoorType.values()[tag.getInteger("doorType")];
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound tag)
	{
		super.writeToNBT(tag);
		writeExtraNBT(tag);
	}

	public void writeExtraNBT(NBTTagCompound tag)
	{
		writeGeneralNBT(tag);
		writeAbilitiesNBT(tag);
		writeInventoryNBT(tag);
		writeTextureNBT(tag);
	}

	private void writeAbilitiesNBT(NBTTagCompound tag)
	{
		NBTTagCompound abilitiesTag = new NBTTagCompound();
		for (Ability a : abilities)
		{
			NBTTagCompound abilityTag = new NBTTagCompound();
			a.writeNBT(abilityTag);
			abilitiesTag.setTag(a.getTagName(), abilityTag);
		}
		tag.setTag("abilities", abilitiesTag);
	}

	public void writeGeneralNBT(NBTTagCompound tag)
	{
		tag.setBoolean("hasOwner", owner != null);
		if (owner != null)
		{
			tag.setLong("UUID1", owner.getMostSignificantBits());
			tag.setLong("UUID2", owner.getLeastSignificantBits());
			tag.setString("ownerName", ownerName);
		}
		tag.setBoolean("powered", powered);
		writeSettingsNBT(tag);
	}

	public void writeInventoryNBT(NBTTagCompound tagCompound)
	{
		NBTTagCompound inv = new NBTTagCompound();
		for (int i = 0; i < contents.length; i++)
		{
			if (contents[i] == null)
			{
				continue;
			}
			NBTTagCompound itemTag = new NBTTagCompound();
			contents[i].writeToNBT(itemTag);
			inv.setTag("slot".concat(String.valueOf(i)), itemTag);
		}
		tagCompound.setTag("inv", inv);
	}

	public void writeTextureNBT(NBTTagCompound tagCompound)
	{
		NBTTagCompound texTag = new NBTTagCompound();
		for (CabinetParts part : CabinetParts.values())
		{
			texTag.setString("texture:".concat(part.name().toLowerCase()), textures[part.ordinal()]);
		}
		texTag.setString("texture:door", doorTexture);
		tagCompound.setTag("texTag", texTag);
	}

	public NBTTagCompound writeSettingsNBT(NBTTagCompound tag)
	{
		tag.setBoolean("locked", this.locked);
		tag.setBoolean("hidden", this.hidden);
		tag.setInteger("yaw", this.facing.ordinal());
		tag.setInteger("pitch", this.verticalFacing.ordinal());
		tag.setBoolean("skinning", this.skinningPublic);
		tag.setInteger("doorType", this.doorType.ordinal());
		return tag;
	}

	@Override
	public int getInventoryStackLimit()
	{
		return 64;
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer player)
	{
		return worldObj != null && worldObj.getTileEntity(pos) == this && (this.isOwner(player) || !locked);
	}

	@Override
	public void update()
	{
		if (ability != null)
		{
			if (ability.needsWorld() && ability.getWorld() == null)
			{
				ability.setWorld(worldObj);
			}
			ability.update();
		}

		double x = (double) pos.getX() + 0.5D;
		double y = (double) pos.getY() + 0.5D;
		double z = (double) pos.getZ() + 0.5D;
		if (!worldObj.isRemote)
		{
			if (sync++ % 2400 == 1 || needsUpdate)
			{
				Reference.packetHandler.sendToAllAround(new CabinetSyncPacket(this, CabinetSyncPacket.GENERAL), new NetworkRegistry.TargetPoint(worldObj.provider.getDimensionId(), pos.getX(), pos.getY(), pos.getZ(), 64D));
				Reference.packetHandler.sendToAllAround(new CabinetSyncPacket(this, CabinetSyncPacket.INVENTORY), new NetworkRegistry.TargetPoint(worldObj.provider.getDimensionId(), pos.getX(), pos.getY(), pos.getZ(), 64D));
				Reference.packetHandler.sendToAllAround(new CabinetSyncPacket(this, CabinetSyncPacket.TEXTURES), new NetworkRegistry.TargetPoint(worldObj.provider.getDimensionId(), pos.getX(), pos.getY(), pos.getZ(), 64D));
				needsUpdate = false;
			}
		}

		if (!getBlocked())
		{
			float anglePerTick = 0.1F;
			float lastAngle = doorAngle;
			if (doorAngle < 1f && (numUsingPlayers > 0 || powered))
			{
				doorAngle += anglePerTick;
				if (lastAngle == 0F)
				{
					worldObj.playSoundEffect(x, y, z, "random.chestopen", 0.5F,
							                        worldObj.rand.nextFloat() * 0.1F + 0.9F);
				}
			}

			if (doorAngle > 0F && numUsingPlayers < 1 && !powered)
			{
				doorAngle -= anglePerTick;
				if (lastAngle == 1F)
				{
					worldObj.playSoundEffect(x, y, z, "random.chestclosed", 0.5F,
							                        worldObj.rand.nextFloat() * 0.1F + 0.9F);
				}
			}
		}

		if (doorAngle > 1F)
		{
			doorAngle = 1F;
		}
		if (doorAngle < 0F || (getBlocked() && doorAngle > 0F))
		{
			doorAngle = 0F;
		}
	}

	public boolean getBlocked()
	{
		if (verticalFacing == EnumFacing.DOWN)
		{
			return worldObj.getBlockState(pos.offset(EnumFacing.DOWN)).getBlock().isFullBlock();
		}
		else if (verticalFacing == EnumFacing.UP)
		{
			return worldObj.getBlockState(pos.offset(EnumFacing.UP)).getBlock().isFullBlock();
		}

		return worldObj.getBlockState(pos.offset(facing.getOpposite())).getBlock().isFullBlock();
	}

	@Override
	public boolean receiveClientEvent(int id, int value)
	{
		switch (id)
		{
			case 0:
				numUsingPlayers = value;
				break;
			case 1:
				powered = value == 0;
				break;
		}
		return true;
	}

	@Override
	public void openInventory(EntityPlayer player)
	{
		numUsingPlayers++;
		worldObj.addBlockEvent(pos, Reference.cabinet, 0, numUsingPlayers);
	}

	@Override
	public void closeInventory(EntityPlayer player)
	{
		numUsingPlayers--;
		worldObj.addBlockEvent(pos, Reference.cabinet, 0, numUsingPlayers);
	}

	@Override
	public Packet getDescriptionPacket()
	{
		NBTTagCompound tag = new NBTTagCompound();
		writeToNBT(tag);
		return new S35PacketUpdateTileEntity(pos, 0, tag);
	}

	@Override
	public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt)
	{
		readFromNBT(pkt.getNbtCompound());
	}

	@Override
	public String getCommandSenderName()
	{
		return null;
	}

	@Override
	public boolean hasCustomName()
	{
		return true;
	}

	@Override
	public IChatComponent getDisplayName()
	{
		if (ownerName == null || ownerName.isEmpty())
		{
			return new ChatComponentText("Unclaimed");
		}
		return new ChatComponentText(ownerName.concat("\'s"));
	}

	public UUID getOwner()
	{
		return owner;
	}

	public void setOwner(EntityPlayer player)
	{
		if (player == null)
		{
			this.owner = null;
			this.ownerName = "";
		}
		else
		{
			this.owner = player.getPersistentID();
			this.ownerName = player.getDisplayNameString();
		}
		markForUpdate();
	}

	public boolean isOwner(EntityPlayer player)
	{
		return player.capabilities.isCreativeMode || owner == null || owner.equals(player.getPersistentID());
	}

	public boolean isHidden()
	{
		return hidden;
	}

	public void setHidden(boolean hidden)
	{
		this.hidden = hidden;
	}

	public boolean isLocked()
	{
		return locked;
	}

	public void setLocked(boolean locked)
	{
		this.locked = locked;
	}

	public EnumFacing getFacing()
	{
		return facing;
	}

	public void setFacing(EnumFacing facing)
	{
		this.facing = facing;
	}

	public float getDoorAngle()
	{
		return doorAngle;
	}

	public void setPowered(boolean powered)
	{
		this.powered = powered;
		worldObj.addBlockEvent(pos, Reference.cabinet, 1, (powered ? 0 : 1));
	}

	public ItemStack[] getContents()
	{
		return contents;
	}

	@Override
	public int[] getSlotsForFace(EnumFacing side)
	{
		int[] slots = new int[getSizeInventory()];
		for (int i = 0; i < slots.length; i++)
		{
			slots[i] = i;
		}
		return slots;
	}

	@Override
	public boolean canInsertItem(int p_102007_1_, ItemStack p_102007_2_, EnumFacing face)
	{
		return false;
	}

	@Override
	public boolean canExtractItem(int p_102008_1_, ItemStack p_102008_2_, EnumFacing face)
	{
		return false;
	}
	
	public String getOwnerName()
	{
		return ownerName;
	}

	public void setTexture(int id, String selectedTexture)
	{
		textures[id] = selectedTexture;
	}

	public String getTexture(int id)
	{
		return textures[id];
	}

	public String[] getTextures()
	{
		return textures;
	}

	public void setTextures(String[] newTextures)
	{
		textures = newTextures;
	}

	public String getDefaultTexture()
	{
		return "minecraft:blocks/planks_oak";
	}

	public void markForUpdate()
	{
		needsUpdate = true;
	}

	public void clearOwner()
	{
		this.owner = null;
		this.ownerName = "";
	}

	public DoorType getDoorType()
	{
		return doorType;
	}

	public void setDoorType(DoorType doorType)
	{
		this.doorType = doorType;
	}

	public String getDoorTexture()
	{
		return doorTexture == null || doorTexture.isEmpty() ? doorType.getTexture() : doorTexture;
	}

	public void setDoorTexture(String doorTexture)
	{
		this.doorTexture = doorTexture;
	}

	public EnumFacing getVerticalFacing()
	{
		return verticalFacing;
	}

	public void setVerticalFacing(EnumFacing verticalFacing)
	{
		this.verticalFacing = verticalFacing;
	}

	public boolean isSkinningPublic()
	{
		return skinningPublic;
	}

	public void togglePublicSkin()
	{
		skinningPublic = !skinningPublic;
	}

	public Ability getAbility()
	{
		return ability;
	}

	public ItemStack getSecAbilityStack()
	{
		return contents[contents.length - 1];
	}

	public Ability getAbilityForItem(Item item)
	{
		Class<? extends Ability> tempClass = abilityMap.get(item);
		for (Ability ability : abilities)
		{
			if (ability.getClass() == tempClass)
			{
				if (ability.needsWorld())
				{
					ability.setWorld(worldObj);
				}
				return ability;
			}
		}

		if (tempClass != null)
		{
			try
			{
				Ability a = tempClass.getConstructor().newInstance();
				abilities.add(a);
				if (a.needsWorld())
				{
					a.setWorld(worldObj);
				}
				return a;
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		return new NoActionAbility();
	}
}
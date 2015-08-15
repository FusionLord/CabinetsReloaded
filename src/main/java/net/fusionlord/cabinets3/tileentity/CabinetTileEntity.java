package net.fusionlord.cabinets3.tileentity;

import net.fusionlord.cabinets3.Reference;
import net.fusionlord.cabinets3.block.CabinetBlock;
import net.fusionlord.cabinets3.client.renderer.CabinetParts;
import net.fusionlord.cabinets3.item.CabinetItem;
import net.fusionlord.cabinets3.packets.CabinetSyncPacket;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemBlock;
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

import java.util.UUID;

public class CabinetTileEntity extends TileEntity implements IUpdatePlayerListBox, IInventory, ISidedInventory
{
	private ItemStack[] contents = new ItemStack[getSizeInventory()];
	private String[] textures;
	private UUID owner;
	private boolean hidden;
	private boolean locked;
	private int facing;
	private float doorAngle;
	private boolean powered;
	private int numUsingPlayers;
	private int sync = 1;
	private String ownerName = "";
	private boolean needsUpdate = false;

	public CabinetTileEntity()
	{
		super();
		locked = true;
		textures = new String[CabinetParts.values().length];
		for (CabinetParts part : CabinetParts.values())
		{
			String tex;
			if (part.name().toLowerCase().contains("half_door"))
			{
				tex = "cabinets3:blocks/halfdoor";
			}
			else if (part.name().toLowerCase().contains("door"))
			{
				tex = "cabinets3:blocks/door";
			}
			else
			{
				tex = getDefaultTexture();
			}
			textures[part.ordinal()] = tex;
		}
	}

	@Override
	public int getSizeInventory()
	{
		return 9;
	}

	@Override
	public ItemStack getStackInSlot(int slot)
	{
		return contents[slot - 1];
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
		contents[slot - 1] = itemStack;
		if (itemStack != null && itemStack.stackSize > this.getInventoryStackLimit())
		{
			itemStack.stackSize = this.getInventoryStackLimit();
		}

		markDirty();
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int slot)
	{
		return getStackInSlot(slot);
	}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack itemStack)
	{
		return slot == 0 && itemStack.getItem() instanceof ItemBlock || !(itemStack.getItem() instanceof CabinetItem);
	}

	@Override
	public int getField(int id)
	{
		return 0;
	}

	@Override
	public void setField(int id, int value)
	{

	}

	@Override
	public int getFieldCount()
	{
		return 0;
	}

	@Override
	public void markDirty()
	{
		super.markDirty();
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
		readInventoryNBT(tag);
		readTextureNBT(tag);
	}

	public void readGeneralNBT(NBTTagCompound tag)
	{
		this.facing = tag.getInteger("facing");
		if (tag.getBoolean("hasOwner"))
		{
			this.owner = new UUID(tag.getLong("UUID1"), tag.getLong("UUID2"));
			this.ownerName = tag.getString("ownerName");
		}
		this.hidden = tag.getBoolean("hidden");
		this.locked = tag.getBoolean("locked");
		this.powered = tag.getBoolean("Powered");
	}

	public void readInventoryNBT(NBTTagCompound tag)
	{
		NBTTagCompound inv = tag.getCompoundTag("inv");
		for (int i = 0; i < contents.length; i++)
		{
			contents[i] = ItemStack.loadItemStackFromNBT(inv.getCompoundTag("slot".concat(String.valueOf(i))));
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
		writeInventoryNBT(tag);
		writeTextureNBT(tag);
	}

	public void writeGeneralNBT(NBTTagCompound tag)
	{
		tag.setInteger("facing", this.facing);
		tag.setBoolean("hasOwner", this.owner != null);
		if (this.owner != null)
		{
			tag.setLong("UUID1", this.owner.getMostSignificantBits());
			tag.setLong("UUID2", this.owner.getLeastSignificantBits());
			tag.setString("ownerName", this.ownerName);
		}
		tag.setBoolean("hidden", this.hidden);
		tag.setBoolean("locked", this.locked);
		tag.setBoolean("Powered", this.powered);
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
		tagCompound.setTag("texTag", texTag);
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
		double x = (double) pos.getX() + 0.5D;
		double y = (double) pos.getY() + 0.5D;
		double z = (double) pos.getZ() + 0.5D;
		if (!worldObj.isRemote)
		{
			if (sync++ % 2400 == 1 || needsUpdate)
			{
				sync();
				needsUpdate = false;
			}
		}

		if (!CabinetBlock.getBlocked(worldObj, pos, facing))
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
		if (doorAngle < 0F || (CabinetBlock.getBlocked(worldObj, pos, facing)
				                       && doorAngle > 0F))
		{
			doorAngle = 0F;
		}
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

	protected void sync()
	{
		Reference.packetHandler.sendToAllAround(new CabinetSyncPacket(this, CabinetSyncPacket.GENERAL), new NetworkRegistry.TargetPoint(worldObj.provider.getDimensionId(), pos.getX(), pos.getY(), pos.getZ(), 64D));
		Reference.packetHandler.sendToAllAround(new CabinetSyncPacket(this, CabinetSyncPacket.INVENTORY), new NetworkRegistry.TargetPoint(worldObj.provider.getDimensionId(), pos.getX(), pos.getY(), pos.getZ(), 64D));
		Reference.packetHandler.sendToAllAround(new CabinetSyncPacket(this, CabinetSyncPacket.TEXTURES), new NetworkRegistry.TargetPoint(worldObj.provider.getDimensionId(), pos.getX(), pos.getY(), pos.getZ(), 64D));
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

	public int getFacing()
	{
		return facing;
	}

	public void setFacing(int facing)
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
		worldObj.checkLight(getPos());
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
		this.ownerName = null;
	}
}
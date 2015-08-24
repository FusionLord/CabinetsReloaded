package net.fusionlord.cabinets3.packets;

import io.netty.buffer.ByteBuf;
import net.fusionlord.cabinets3.tileentity.CabinetTileEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class CabinetSyncPacket implements IMessage
{
	public static final byte GENERAL = 0, INVENTORY = 1, TEXTURES = 2;
	BlockPos pos;
	NBTTagCompound tagCompound;
	byte part;

	public CabinetSyncPacket() {}

	public CabinetSyncPacket(CabinetTileEntity cabinet, byte part)
	{
		pos = cabinet.getPos();
		this.part = part;
		tagCompound = new NBTTagCompound();
		switch (part)
		{
			case GENERAL:
				cabinet.writeGeneralNBT(tagCompound);
				break;
			case INVENTORY:
				cabinet.writeInventoryNBT(tagCompound);
				break;
			case TEXTURES:
				cabinet.writeTextureNBT(tagCompound);
		}
	}

	@Override
	public void toBytes(ByteBuf buffer)
	{
		buffer.writeLong(pos.toLong());
		buffer.writeByte(part);
		ByteBufUtils.writeTag(buffer, tagCompound);
	}

	@Override
	public void fromBytes(ByteBuf buffer)
	{
		Long posl = buffer.readLong();
		pos = BlockPos.fromLong(posl);
		part = buffer.readByte();
		tagCompound = ByteBufUtils.readTag(buffer);
	}

	public static class Handler implements IMessageHandler<CabinetSyncPacket, IMessage>
	{
		@Override
		public IMessage onMessage(CabinetSyncPacket message, MessageContext ctx)
		{
			CabinetTileEntity cabinet = (CabinetTileEntity) Minecraft.getMinecraft().theWorld.getTileEntity(message.pos);
			if (cabinet != null)
			{
				switch (message.part)
				{
					case GENERAL:
						cabinet.readGeneralNBT(message.tagCompound);
						break;
					case INVENTORY:
						cabinet.readInventoryNBT(message.tagCompound);
						break;
					case TEXTURES:
						cabinet.readTextureNBT(message.tagCompound);
				}
			}
			return null;
		}
	}
}

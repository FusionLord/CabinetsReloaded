package net.fusionlord.cabinets3.packets;

import io.netty.buffer.ByteBuf;
import net.fusionlord.cabinets3.tileentity.CabinetTileEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class CabinetSyncPacket implements IMessage
{
	BlockPos pos;
	NBTTagCompound tagCompound;
	byte part;
	public static final byte GENERAL = 0, INVENTORY = 1, TEXTURES = 2;

	public CabinetSyncPacket() {}

	public CabinetSyncPacket(CabinetTileEntity cabinet, byte part)
	{
		pos = cabinet.getPos();
		tagCompound = new NBTTagCompound();
		this.part = part;
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
		System.out.println(tagCompound);
	}

	@Override
	public void fromBytes(ByteBuf buffer)
	{
		pos = BlockPos.fromLong(buffer.readLong());
		part = buffer.readByte();
		tagCompound = ByteBufUtils.readTag(buffer);
		System.out.println(tagCompound);
	}

	public static class Handler implements IMessageHandler<CabinetSyncPacket, IMessage>
	{
		@Override
		public IMessage onMessage(CabinetSyncPacket message, MessageContext ctx)
		{
			World world = Minecraft.getMinecraft().theWorld;
			CabinetTileEntity cabinet = (CabinetTileEntity) world.getTileEntity(message.pos);
			System.out.println("Cabinet = " + cabinet);
			System.out.println("part = " + message.part);
			System.out.println("TAG = " + message.tagCompound);

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
			return null;
		}
	}
}

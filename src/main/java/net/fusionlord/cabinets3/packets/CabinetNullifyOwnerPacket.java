package net.fusionlord.cabinets3.packets;

import io.netty.buffer.ByteBuf;
import net.fusionlord.cabinets3.tileentity.CabinetTileEntity;
import net.minecraft.util.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * Author: FusionLord
 * Email: FusionLord@gmail.com
 */
public class CabinetNullifyOwnerPacket implements IMessage
{
	BlockPos pos;

	public CabinetNullifyOwnerPacket() {}

	public CabinetNullifyOwnerPacket(CabinetTileEntity cabinet)
	{
		pos = cabinet.getPos();
	}

	@Override
	public void toBytes(ByteBuf buffer)
	{
		buffer.writeLong(pos.toLong());
	}

	@Override
	public void fromBytes(ByteBuf buffer)
	{
		pos = BlockPos.fromLong(buffer.readLong());
	}

	public static class Handler implements IMessageHandler<CabinetNullifyOwnerPacket, IMessage>
	{
		@Override
		public IMessage onMessage(CabinetNullifyOwnerPacket message, MessageContext ctx)
		{
			CabinetTileEntity cabinet = (CabinetTileEntity) ctx.getServerHandler().playerEntity.worldObj.getTileEntity(message.pos);
			if (cabinet != null)
			{
				cabinet.clearOwner();
				cabinet.markForUpdate();
			}
			return null;
		}
	}
}

package net.fusionlord.cabinets3.packets;

import io.netty.buffer.ByteBuf;
import net.fusionlord.cabinets3.tileentity.CabinetTileEntity;
import net.minecraft.util.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class CabinetGuiPacket implements IMessage
{
	BlockPos pos;
	boolean isLocked, hideItems;

	public CabinetGuiPacket() {}

	public CabinetGuiPacket(CabinetTileEntity cabinet)
	{
		pos = cabinet.getPos();

		isLocked = cabinet.isLocked();
		hideItems = cabinet.isHidden();
	}

	@Override
	public void toBytes(ByteBuf buffer)
	{
		buffer.writeLong(pos.toLong());
		buffer.writeBoolean(isLocked);
		buffer.writeBoolean(hideItems);
	}

	@Override
	public void fromBytes(ByteBuf buffer)
	{
		Long posl = buffer.readLong();
		pos = BlockPos.fromLong(posl);
		isLocked = buffer.readBoolean();
		hideItems = buffer.readBoolean();
	}


	public static class Handler implements IMessageHandler<CabinetGuiPacket, IMessage>
	{
		@Override
		public IMessage onMessage(CabinetGuiPacket message, MessageContext ctx)
		{
			CabinetTileEntity cabinet = (CabinetTileEntity) ctx.getServerHandler().playerEntity.worldObj.getTileEntity(message.pos);
			if (cabinet != null)
			{
				cabinet.setLocked(message.isLocked);
				cabinet.setHidden(message.hideItems);
				cabinet.markForUpdate();
			}
			return null;
		}
	}
}

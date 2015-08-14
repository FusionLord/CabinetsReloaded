package net.fusionlord.cabinets3.packets;

import io.netty.buffer.ByteBuf;
import net.fusionlord.cabinets3.tileentity.CabinetTileEntity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

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
	public void fromBytes(ByteBuf buffer)
	{
		pos = BlockPos.fromLong(buffer.readLong());
		isLocked = buffer.readBoolean();
		hideItems = buffer.readBoolean();
	}

	@Override
	public void toBytes(ByteBuf buffer)
	{
		buffer.writeLong(pos.toLong());

		buffer.writeBoolean(isLocked);
		buffer.writeBoolean(hideItems);
	}

	public static class Handler implements IMessageHandler<CabinetGuiPacket, IMessage>
	{
		@Override
		public IMessage onMessage(CabinetGuiPacket message, MessageContext ctx)
		{
			if (ctx.side == Side.SERVER)
			{
				EntityPlayer player = ctx.getServerHandler().playerEntity;
				TileEntity te = player.worldObj.getTileEntity(message.pos);
				if (te instanceof CabinetTileEntity)
				{
					CabinetTileEntity cabinet = (CabinetTileEntity) te;

					cabinet.setLocked(message.isLocked);
					cabinet.setHidden(message.hideItems);
					cabinet.sync();
				}
			}

			return null;
		}
	}
}

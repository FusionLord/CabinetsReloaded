package net.fusionlord.cabinets3.packets;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.fusionlord.fusionutil.network.packets.AbstractPacket;
import net.fusionlord.cabinets3.tileentity.CabinetTileEntity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;

public class CabinetGuiPacket extends AbstractPacket
{
	int x, y, z;
	boolean l, h;

	public CabinetGuiPacket()
	{
	}

	public CabinetGuiPacket(CabinetTileEntity cabinet)
	{
		x = cabinet.getPos().getX();
		y = cabinet.getPos().getY();
		z = cabinet.getPos().getZ();

		l = cabinet.isLocked();
		h = cabinet.isHidden();
	}

	@Override
	public void encodeInto(ChannelHandlerContext ctx, ByteBuf buffer)
	{
		buffer.writeInt(x);
		buffer.writeInt(y);
		buffer.writeInt(z);

		buffer.writeBoolean(l);
		buffer.writeBoolean(h);
	}

	@Override
	public void decodeInto(ChannelHandlerContext ctx, ByteBuf buffer)
	{
		x = buffer.readInt();
		y = buffer.readInt();
		z = buffer.readInt();

		l = buffer.readBoolean();
		h = buffer.readBoolean();
	}

	@Override
	public void handleClientSide(EntityPlayer player)
	{
	}

	@Override
	public void handleServerSide(EntityPlayer player)
	{
		TileEntity te = player.worldObj.getTileEntity(new BlockPos(x, y, z));
		if (te instanceof CabinetTileEntity)
		{
			CabinetTileEntity cabinet = (CabinetTileEntity) te;

			cabinet.setLocked(l);
			cabinet.setHidden(h);
			cabinet.sync();
		}
	}
}

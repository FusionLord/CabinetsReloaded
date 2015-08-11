package net.fusionlord.cabinets3.packets;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.fusionlord.fusionutil.network.packets.AbstractPacket;
import net.fusionlord.cabinets3.tileentity.CabinetTileEntity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;

/**
 * Author: FusionLord
 * Email: FusionLord@gmail.com
 */
public class CabinetNullifyOwnerPacket extends AbstractPacket
{
	int x, y, z;

	public CabinetNullifyOwnerPacket()
	{
	}

	public CabinetNullifyOwnerPacket(CabinetTileEntity cabinet)
	{
		x = cabinet.getPos().getX();
		y = cabinet.getPos().getY();
		z = cabinet.getPos().getZ();
	}

	@Override
	public void encodeInto(ChannelHandlerContext ctx, ByteBuf buffer)
	{
		buffer.writeInt(x);
		buffer.writeInt(y);
		buffer.writeInt(z);
	}

	@Override
	public void decodeInto(ChannelHandlerContext ctx, ByteBuf buffer)
	{
		this.x = buffer.readInt();
		this.y = buffer.readInt();
		this.z = buffer.readInt();
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
			((CabinetTileEntity) te).setOwner(null);
		}
	}
}

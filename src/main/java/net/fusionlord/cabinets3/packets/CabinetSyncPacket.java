package net.fusionlord.cabinets3.packets;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.fusionlord.fusionutil.network.packets.AbstractPacket;
import net.fusionlord.cabinets3.tileentity.CabinetTileEntity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraftforge.fml.common.network.ByteBufUtils;

public class CabinetSyncPacket extends AbstractPacket
{
	int x, y, z;
	NBTTagCompound tagCompound;

	public CabinetSyncPacket() {}

	public CabinetSyncPacket(CabinetTileEntity cabinet)
	{
		x = cabinet.getPos().getX();
		y = cabinet.getPos().getY();
		z = cabinet.getPos().getZ();
		tagCompound = new NBTTagCompound();
		cabinet.writeToNBT(tagCompound);
	}

	@Override
	public void encodeInto(ChannelHandlerContext ctx, ByteBuf buffer)
	{
		buffer.writeInt(x);
		buffer.writeInt(y);
		buffer.writeInt(z);
		ByteBufUtils.writeTag(buffer, tagCompound);
	}

	@Override
	public void decodeInto(ChannelHandlerContext ctx, ByteBuf buffer)
	{
		x = buffer.readInt();
		y = buffer.readInt();
		z = buffer.readInt();
		tagCompound = ByteBufUtils.readTag(buffer);
	}

	@Override
	public void handleClientSide(EntityPlayer player)
	{
		TileEntity te = player.worldObj.getTileEntity(new BlockPos(x, y, z));
		if (te != null && te instanceof CabinetTileEntity)
		{
			((CabinetTileEntity)te).readExtraNBT(tagCompound);
		}
	}

	@Override
	public void handleServerSide(EntityPlayer player)
	{
	}
}

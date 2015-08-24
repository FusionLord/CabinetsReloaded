package net.fusionlord.cabinets3.packets;

import io.netty.buffer.ByteBuf;
import net.fusionlord.cabinets3.tileentity.CabinetTileEntity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * Created by FusionLord on 8/12/2015.
 */
public class CabinetTextureSyncPacket implements IMessage
{
	private BlockPos pos;
	private NBTTagCompound textures;

	public CabinetTextureSyncPacket() {}

	public CabinetTextureSyncPacket(CabinetTileEntity cabinet)
	{
		pos = cabinet.getPos();
		textures = new NBTTagCompound();
		cabinet.writeTextureNBT(textures);
	}

	@Override
	public void toBytes(ByteBuf buffer)
	{
		buffer.writeLong(pos.toLong());
		ByteBufUtils.writeTag(buffer, textures);
	}

	@Override
	public void fromBytes(ByteBuf buffer)
	{
		Long posl = buffer.readLong();
		pos = BlockPos.fromLong(posl);
		textures = ByteBufUtils.readTag(buffer);
	}

	public static class Handler implements IMessageHandler<CabinetTextureSyncPacket, IMessage>
	{
		@Override
		public IMessage onMessage(CabinetTextureSyncPacket message, MessageContext ctx)
		{

			CabinetTileEntity cabinet = (CabinetTileEntity) ctx.getServerHandler().playerEntity.worldObj.getTileEntity(message.pos);
			if (cabinet != null)
			{
				cabinet.readTextureNBT(message.textures);
				cabinet.markForUpdate();
			}
			return null;
		}
	}
}

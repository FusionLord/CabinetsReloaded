package net.fusionlord.cabinets3.packets;

import io.netty.buffer.ByteBuf;
import net.fusionlord.cabinets3.client.renderer.CabinetParts;
import net.fusionlord.cabinets3.tileentity.CabinetTileEntity;
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
	private BlockPos location;
	private String[] textures;

	public CabinetTextureSyncPacket() {}

	public CabinetTextureSyncPacket(CabinetTileEntity cabinet)
	{
		location = cabinet.getPos();
		textures = cabinet.getTextures();
	}

	@Override
	public void toBytes(ByteBuf buffer)
	{
		buffer.writeLong(location.toLong());
		for (int i = 0; i < CabinetParts.values().length; i++)
		{
			ByteBufUtils.writeUTF8String(buffer, textures[i] != null ? textures[i] : "");
		}
	}

	@Override
	public void fromBytes(ByteBuf buffer)
	{
		location = BlockPos.fromLong(buffer.readLong());

		textures = new String[CabinetParts.values().length];
		for (int i = 0; i < CabinetParts.values().length; i++)
		{
			textures[i] = ByteBufUtils.readUTF8String(buffer);
		}
	}

	public static class Handler implements IMessageHandler<CabinetTextureSyncPacket, IMessage>
	{

		@Override
		public IMessage onMessage(CabinetTextureSyncPacket message, MessageContext ctx)
		{

			CabinetTileEntity cabinet = (CabinetTileEntity) ctx.getServerHandler().playerEntity.worldObj.getTileEntity(message.location);
			cabinet.setTextures(message.textures);
			cabinet.sync();
			return null;
		}
	}
}

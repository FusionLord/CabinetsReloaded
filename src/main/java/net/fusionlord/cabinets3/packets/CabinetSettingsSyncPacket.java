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
 * Created by FusionLord on 8/17/2015.
 */
public class CabinetSettingsSyncPacket implements IMessage
{
	BlockPos pos;
	NBTTagCompound settings;

	public CabinetSettingsSyncPacket() {}

	public CabinetSettingsSyncPacket(CabinetTileEntity cabinet)
	{
		pos = cabinet.getPos();
		settings = new NBTTagCompound();
		settings = cabinet.writeSettingsNBT(settings);
	}

	@Override
	public void fromBytes(ByteBuf buf)
	{
		pos = BlockPos.fromLong(buf.readLong());
		settings = ByteBufUtils.readTag(buf);
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		buf.writeLong(pos.toLong());
		ByteBufUtils.writeTag(buf, settings);
	}

	public static class Handler implements IMessageHandler<CabinetSettingsSyncPacket, IMessage>
	{
		@Override
		public IMessage onMessage(CabinetSettingsSyncPacket message, MessageContext ctx)
		{
			CabinetTileEntity cabinet = (CabinetTileEntity) ctx.getServerHandler().playerEntity.worldObj.getTileEntity(message.pos);
			cabinet.readSettingsNBT(message.settings);
			cabinet.markForUpdate();
			return null;
		}
	}
}

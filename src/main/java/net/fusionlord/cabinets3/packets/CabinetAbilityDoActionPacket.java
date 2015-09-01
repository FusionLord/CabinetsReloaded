package net.fusionlord.cabinets3.packets;

import io.netty.buffer.ByteBuf;
import net.fusionlord.cabinets3.tileentity.CabinetTileEntity;
import net.minecraft.util.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * Created by FusionLord on 8/20/2015.
 */
public class CabinetAbilityDoActionPacket implements IMessage
{
	public BlockPos pos;

	public CabinetAbilityDoActionPacket() {}

	public CabinetAbilityDoActionPacket(CabinetTileEntity cabinet)
	{
		pos = cabinet.getPos();
	}

	@Override
	public void fromBytes(ByteBuf buf)
	{
		pos = BlockPos.fromLong(buf.readLong());
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		buf.writeLong(pos.toLong());
	}

	public static class Handler implements IMessageHandler<CabinetAbilityDoActionPacket, IMessage>
	{

		@Override
		public IMessage onMessage(CabinetAbilityDoActionPacket message, MessageContext ctx)
		{
			CabinetTileEntity cabinet = (CabinetTileEntity) ctx.getServerHandler().playerEntity.worldObj.getTileEntity(message.pos);
			if (cabinet.getAbility().hasAction())
			{
				cabinet.getAbility().doAction(cabinet, ctx.getServerHandler().playerEntity);
			}
			return null;
		}
	}
}

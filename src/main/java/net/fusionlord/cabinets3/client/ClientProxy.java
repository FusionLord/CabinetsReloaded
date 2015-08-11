package net.fusionlord.cabinets3.client;

import net.fusionlord.cabinets3.CommonProxy;
import net.fusionlord.cabinets3.Reference;
import net.fusionlord.cabinets3.block.CabinetBlock;
import net.fusionlord.cabinets3.client.renderer.CabinetTileEntityRenderer;
import net.fusionlord.cabinets3.client.renderer.RenderingReference;
import net.fusionlord.cabinets3.tileentity.CabinetTileEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemModelMesher;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ClientProxy extends CommonProxy
{
	@Override
	@SideOnly(Side.CLIENT)
	public void registerRenderers()
	{
		ClientRegistry.bindTileEntitySpecialRenderer(CabinetTileEntity.class, new CabinetTileEntityRenderer());

		ItemModelMesher modelMesher = Minecraft.getMinecraft().getRenderItem().getItemModelMesher();

		Item cabinetItem = Item.getItemFromBlock(Reference.cabinet);
		modelMesher.register(cabinetItem, CabinetBlock.Types.LEFT.getID(), new ModelResourceLocation(Reference.MOD_ID.concat(":cabinet"), "inventory_left"));
		modelMesher.register(cabinetItem, CabinetBlock.Types.RIGHT.getID(), new ModelResourceLocation(Reference.MOD_ID.concat(":cabinet"), "inventory_right"));
		modelMesher.register(cabinetItem, CabinetBlock.Types.DOUBLE.getID(), new ModelResourceLocation(Reference.MOD_ID.concat(":cabinet"), "inventory_double"));

		RenderingReference.init();
	}
}

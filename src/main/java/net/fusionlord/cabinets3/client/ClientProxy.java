package net.fusionlord.cabinets3.client;

import net.fusionlord.cabinets3.CommonProxy;
import net.fusionlord.cabinets3.Reference;
import net.fusionlord.cabinets3.client.renderer.CabinetTileEntityRenderer;
import net.fusionlord.cabinets3.client.renderer.RenderingReference;
import net.fusionlord.cabinets3.tileentity.CabinetTileEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemModelMesher;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.io.File;

public class ClientProxy extends CommonProxy
{
	@Override
	@SideOnly(Side.CLIENT)
	public void registerRenderers()
	{
		ClientRegistry.bindTileEntitySpecialRenderer(CabinetTileEntity.class, new CabinetTileEntityRenderer());

		ItemModelMesher modelMesher = Minecraft.getMinecraft().getRenderItem().getItemModelMesher();

		modelMesher.register(Item.getItemFromBlock(Reference.cabinet), 0, new ModelResourceLocation(Reference.MOD_ID.concat(":cabinet"), "inventory"));

		RenderingReference.init();

		MinecraftForge.EVENT_BUS.register(new ClientEvents());
	}

	@Override
	public void loadResources(File configDir)
	{
//		File location = null;
//
//		try
//		{
//			location = new File(configDir.getCanonicalPath().concat("/Cabinets Reloaded/"));
//
//			if (!location.exists()) location.mkdirs();
//
//			for (File file : location.listFiles(pathname -> {
//				return pathname.getName().endsWith("..png");
//			}))
//			{
////				Minecraft.getMinecraft().getTextureMapBlocks().registerSprite(file.get);
//			}
//		}
//		catch (IOException e)
//		{
//			e.printStackTrace();
//		}

	}
}

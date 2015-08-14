package net.fusionlord.cabinets3.client;

import net.fusionlord.cabinets3.Reference;
import net.fusionlord.cabinets3.client.renderer.RenderingReference;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * Created by FusionLord on 8/12/2015.
 */
public class ClientEvents
{
	@SubscribeEvent
	public void doneLoadingTextures(TextureStitchEvent.Post event)
	{
		Reference.loadSkins();
	}

	@SubscribeEvent
	public void reloadAssets(TextureStitchEvent.Pre event) { RenderingReference.init(); }
}

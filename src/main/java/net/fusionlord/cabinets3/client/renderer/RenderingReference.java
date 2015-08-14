package net.fusionlord.cabinets3.client.renderer;

import net.fusionlord.cabinets3.Reference;
import net.fusionlord.fusionutil.client.objLoader.AdvancedModelLoader;
import net.fusionlord.fusionutil.client.objLoader.obj.WavefrontObject;

/**
 * Author: FusionLord
 * Email: FusionLord@gmail.com
 */
public class RenderingReference
{

	public static WavefrontObject model;

	public static void init()
	{
		model = (WavefrontObject) AdvancedModelLoader.loadModel(Reference.getResource("models/cabinetwithdoors.obj"));
	}
}

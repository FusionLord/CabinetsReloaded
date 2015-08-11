package net.fusionlord.cabinets3.client.renderer;

import net.fusionlord.fusionutil.client.objLoader.AdvancedModelLoader;
import net.fusionlord.fusionutil.client.objLoader.obj.WavefrontObject;
import net.fusionlord.cabinets3.Reference;
import net.minecraft.util.ResourceLocation;

/**
 * Author: FusionLord
 * Email: FusionLord@gmail.com
 */
public class RenderingReference
{

	public static WavefrontObject model;
	public static WavefrontObject  doorModel;
	public static ResourceLocation door;
	public static ResourceLocation doubleDoor;

	public static void init()
	{
		model = (WavefrontObject) AdvancedModelLoader.loadModel(Reference.getResource("models/cabinet.obj"));
		doorModel = (WavefrontObject) AdvancedModelLoader.loadModel(Reference.getResource("models/doors.obj"));
		door = Reference.getResource("textures/blocks/door.png");
		doubleDoor = Reference.getResource("textures/blocks/doors.png");
	}
}

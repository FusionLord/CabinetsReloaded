package net.fusionlord.cabinets3.client.renderer;

import net.fusionlord.cabinets3.Reference;
import net.fusionlord.fusionutil.client.objLoader.AdvancedModelLoader;
import net.fusionlord.fusionutil.client.objLoader.obj.WavefrontObject;
import net.minecraft.client.gui.FontRenderer;

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

	public static String capitalize(String name)
	{
		if (name != null && name.length() != 0)
		{
			char[] chars = name.toLowerCase().toCharArray();
			chars[0] = Character.toUpperCase(chars[0]);
			return new String(chars);
		}
		else
		{
			return name;
		}
	}

	public static void drawCenteredStringNoShadow(FontRenderer fontRendererIn, String text, int x, int y, int color)
	{
		fontRendererIn.drawString(text, x - fontRendererIn.getStringWidth(text) / 2, y, color);
	}
}

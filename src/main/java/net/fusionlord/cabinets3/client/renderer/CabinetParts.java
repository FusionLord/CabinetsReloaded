package net.fusionlord.cabinets3.client.renderer;

/**
 * Created by FusionLord on 4/5/2015.
 */

public enum CabinetParts
{
	Bottom,
	Top,
	Left,
	Face(4),
	Back,
	Right,
	Top_Shelf_Bottom,
	Top_Shelf_Top,
	Top_Shelf_Face,
	Bottom_Shelf_Bottom,
	Bottom_Shelf_Top,
	Bottom_Shelf_Face,
	Inner_Bottom,
	Inner_Top,
	Inner_Left,
	Inner_Back(3),
	Inner_Right,
	Left_Door,
	Right_Door,
	Half_Door,;
	
	int count;
	
	CabinetParts()
	{
		this(1);
	}
	
	CabinetParts(int i)
	{
		count = i;
	}
}

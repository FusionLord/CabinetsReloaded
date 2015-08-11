package net.fusionlord.cabinets3.client.renderer;

/**
 * Created by FusionLord on 4/5/2015.
 */

public enum CabinetParts
{
	Bottom(4),
	Top(4),
	Left(2),
	Front(9),
	Back(1),
	Right(2);

	int count;

	CabinetParts(int count)
	{
		this.count = count;
	}
}

package net.fusionlord.cabinets3.util;

/**
 * Created by FusionLord on 8/16/2015.
 */
public enum DoorType
{
	LEFT, RIGHT, DOUBLE;

	public String getTexture()
	{
		return this == DOUBLE ? "cabinets3:blocks/halfdoor" : "cabinets3:blocks/door";
	}

	public DoorType next()
	{
		switch (this)
		{
			case LEFT:
				return RIGHT;
			case RIGHT:
				return DOUBLE;
			case DOUBLE:
				return LEFT;
		}
		return this;
	}
}

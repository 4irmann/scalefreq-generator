package de.airmann.scalefreq.renderer.single;

public enum GlissEQColorEnum {

	GREEN (0, 0x93, 0xcc, 0x17, "Green"),
	BROWN (1, 0xee, 0x99, 0x77, "Brown"),
	BLUE (2, 0x58, 0xbd, 0xf7, "Blue"),
	PINK (3, 0xd4, 0x83, 0xff, "Pink"),
	RED (4, 0xff, 0x10, 0x10, "Red"),
	GREEN_LIGHT (5, 0x10, 0xff, 0x10, "Green Light"),
	WHITE (6, 0xf4, 0xf4, 0xf4, "White"),
	YELLOW (7, 0xf0, 0xe8, 0x1b, "Yellow"),
	ORANGE (8, 0xff, 0xa4, 0x43, "Orange"),
	PINK_LIGHT (9, 0xff, 0x83, 0xbd, "Pink Light"),
	VIOLET (10, 0x83, 0x8c, 0xff, "Violet"),
	BLUE_DARK (11, 0x36, 0x79, 0xff, "Blue Dark"),
	YELLOW_NEON (12, 0xd9, 0xf8, 0x26,"Yellow Neon"),
	PINK_NEON (13, 0xf9, 0x28, 0x96, "Pink Neon"),
	GREEN_NEON (14, 0x29, 0xf8, 0xa6, "Green Neon"),
	PURPLE (15, 0x79, 0x40, 0xf6, "Purple");
				
	private final int colorIndex;
	private final int r;
	private final int g;
	private final int b;
	private final String label;	
		
	private GlissEQColorEnum(int colorIndex, int r, int g, int b, String label)
	{
		this.colorIndex = colorIndex;
		this.r = r;
		this.g = g;
		this.b = b;
		this.label = label;		
	}
	
	public int getColorIndex()
	{
		return this.colorIndex;
	}
	
	public int getR()
	{
		return this.r;
	}
	
	public int getG()
	{
		return this.g;		
	}
	
	public int getB()
	{
		return this.b;
	}
	
	public String toString()
	{
		return this.label;		
	}
	
}

package de.airmann.scalefreq.renderer.single;

public enum GlissEQFilterTypeEnum {
	
	PEAKING ("PEAKING"),
	PEAKING_INV ("PEAKING INV"),
	PEAKING_HRM ("PEAKING HRM"),
	PEAKING_PLAIN ("PEAKING PLAIN"),
	LO_SHELF ("LO-SHELF"),
	LO_PASS_12 ("LO-PASS 12"),
	HI_PASS_12 ("HI-PASS 12"),	
	LO_PASS_24 ("LO-PASS 24"),
	HI_PASS_24 ("HI-PASS 24"),
	NOTCH ("NOTCH"),
	NOTCH_4 ("NOTCH 4"),
	NOTCH_8 ("NOTCH 8"),
	PEAKING_4 ("PEAKING 4"),
	PEAKING_8 ("PEAKING 8"),
	BANDPASS ("BANDPASS"),
	LO_PASS_6 ("LO-PASS 6"),
	HI_PASS_6 ("HI-PASS 6");
	
	private final String type;
		
	private GlissEQFilterTypeEnum(String type)
	{
		this.type = type;
	}
	
	public String toString()
	{
		return this.type;		
	}
	
}
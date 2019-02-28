package de.airmann.scalefreq.core;

public enum ScaleEnum {
	                                					//  123456789ABC
														//  C#D#EF#G#A#B
	CHROMATIC 			("Chromatic","Chro"				, 0b111111111111), 
	
	// HEPTATONIC scales (seven tone scales) ///////////////////////////////////////////	
	
	// Western Church Modes
													  			//  123456789ABC
																//  C#D#EF#G#A#B
	IONIAN					("Ionian","Ion"						, 0b101011010101), // See Major !
	DORIAN					("Dorian","Dor"						, 0b101101010110),	
	PHRYGIAN				("Phrygian","Phr"					, 0b110101011010), 			
	LYDIAN					("Lydian","Lyd" 					, 0b101010110101),			
	MIXOLYDIAN				("Mixolydian","Mix"					, 0b101011010110),	
	AEOLIAN					("Aeloian","Aeol"					, 0b101101011010), // See Minor
	LOCRIAN					("Locrian","Loc"					, 0b110101101010),
	
	// other
	PHRYGIANT_DOMINANT		("Phrygian Dominant","PhrD"			, 0b110011011010),
	GREEK_DORIAN_CHROMATIC	("Greek Dorian Chromatic", "DorGC"	, 0b111001011100),	
		
	MAJOR 				("Major","Maj"					, ScaleEnum.IONIAN.getBinValue()), // Alias
	
	// MINOR See http://de.wikipedia.org/wiki/Moll
	MINOR				("Minor","Min"					, ScaleEnum.AEOLIAN.getBinValue()),	// Alias
	NATURAL_MINOR		("Natural Minor","Min"  		, ScaleEnum.AEOLIAN.getBinValue()),	// Alias 
	AEOLIC_MINOR		("Aeolic Minor","Min"			, ScaleEnum.AEOLIAN.getBinValue()),	// Alias
	HARMONIC_MINOR		("Harmonic Minor", "HarMin"		, 0b101101011001),
	MELODIC_MINOR		("Melodic Minor", "MelMin"		, 0b101101010101),	
	GIPSY_MINOR			("Gipsy Minor","GipMin" 		, 0b101100111001),
				
	// German Diatonic Intervals See http://de.wikipedia.org/wiki/Intervall_%28Musik%29
														//	123456789ABC
	PRIME				("Prime","0 HT"					, 0b100000000000),
	VERM_SEKUNDE		("Verm Sekunde", "0 HT"			, 0b100000000000),		
	KLEINE_SEKUNDE		("Kleine Sekunde","1 HT"		, 0b010000000000),
	GROSSE_SEKUNDE		("Grosse Sekunde","2 HT"		, 0b001000000000),
	UEBER_SEKUNDE		("Ueber Sekunde", "3 HT"		, 0b000100000000),		
	VERM_TERZ			("Verm Terz", "2 HT"			, 0b001000000000),
	KLEINE_TERZ			("Kleine Terz","3 HT"			, 0b000100000000),
	GROSSE_TERZ			("Grosse Terz","4 HT"			, 0b000010000000),
	UEBER_TERZ			("Ueber Terz","5 HT"			, 0b000001000000),
	VERM_QUARTE			("Verm Quarte","4 HT"			, 0b000010000000),
	REINE_QUARTE		("Reine Quarte","5 HT"	  		, 0b000001000000),
	UEBER_QUARTE		("Ueber Quarte","6 HT"			, 0b000000100000),
	VERM_QUINTE			("Verm Quinte", "6 HT"			, 0b000000100000),
	REINE_QUINTE		("Reine Quinte", "7 HT"			, 0b000000010000),
	UEBER_QUINTE		("Ueber Quinte", "8 HT"			, 0b000000001000),
	VERM_SEXTE			("Verm Sexte", "7 HT"			, 0b000000010000),
	KLEINE_SEXTE		("Kleine Sexte", "8 HT"			, 0b000000001000),
	GROSSE_SEXTE		("Grosse Sexte", "9 HT"			, 0b000000000100),
	UEBER_SEXTE			("Ueber Sexte", "10 HT"			, 0b000000000010),
	VERM_SEPTIME		("Verm Septime", "9 HT"			, 0b000000000100),
	KLEINE_SEPTIME		("Kleine Septime", "10 HT"		, 0b000000000010),
	GROSSE_SEPTIME		("Grosse Septime", "11 HT"		, 0b000000000001),
	VERM_OKTAVE			("Verm Oktave", "11 HT"			, 0b000000000001),
	
	// OTHER
	WHOLETONE			("Wholetone","Whole"			, 0b101010101010);
	
	private final String label;
	private final String shortLabel;
	private final int binValue;
	
	private ScaleEnum(String label, String shortLabel, int binValue)
	{
		this.label = label;
		this.shortLabel = shortLabel;
		this.binValue = binValue;
	}
	
	public String getLabel()
	{
		return this.label;
	}
	
	public String getLabel(String separator)
	{
		return this.label.replace(" ", separator);
	}
	
	public String getShortLabel()
	{
		return this.shortLabel;
	}
	
	public String getShortLabel(String separator)
	{
		return this.shortLabel.replace(" ", separator);
	}
	
	public int getBinValue()
	{
		return this.binValue;
	}	
	
}

package de.airmann.scalefreq.core;

public enum NoteEnum {	
		
	C ("C",0),
	Cs ("Cs",1),	
	D ("D",2),
	Ds("Ds",3),	
	E ("E",4),
	F ("F",5),
	Fs("Fs",6),
	G ("G",7),
	Gs("Gs",8),
	A ("A",9),
	As("As",10),
	B ("B",11);
	
	private final String name;
	private final int interval;
		
	private NoteEnum(String name, int interval)
	{
		this.name= name;
		this.interval = interval;			
	}	
	
	public String getName()
	{
		return name;
	}
	
	public String getName(String sharpSymbol)
	{
		return this.name.replace("s", sharpSymbol);
	}
	
	public int getInterval()
	{
		return interval;
	}
}

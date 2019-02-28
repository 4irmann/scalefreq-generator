package de.airmann.scalefreq.core;

public class Scale {	
	
	private boolean isInverse = false;			
	private String label = null;
	private String shortLabel;
	private int binValue;	
	
	public Scale(String label, String shortLabel, int binValue, boolean isInverse)
	{
		this.label = label;
		this.shortLabel = shortLabel;
		this.binValue = binValue;
		this.isInverse = isInverse;		
	}		
	
	public Scale(ScaleEnum sc,boolean isInverse)
	{
		this(sc.getLabel(), sc.getShortLabel(), sc.getBinValue(), isInverse);		
	}	
	
	public Scale(ScaleEnum scale)
	{
		this(scale,false);
	}
	
	public boolean isInverse()
	{
		return this.isInverse;
	}
	
	public void setInverse(boolean isInverse)
	{
		this.isInverse = isInverse;
	}	
			
	public String getLabel()
	{
		if (isInverse)		
			return this.label+" "+"Inv";		
		else		
			return this.label;				
	}
	
	public String getLabel(String separator)
	{
		return this.getLabel().replace(" ", separator);
	}
	
	public String getShortLabel()
	{
		if (isInverse)		
			return this.shortLabel+" "+"Inv";		
		else		
			return this.shortLabel;
	}
	
	public String getShortLabel(String separator)
	{
		return this.getShortLabel().replace(" ", separator);
	}	
	
	public int[] getIntValues()
	{
		return bin2IntValues(getBinValue());				
	}
	
	public int getBinValue()
	{
		if (isInverse)		
			return getInvBinValue(this.binValue);		
		else		
			return this.binValue;		
	}	
	
	// public converter methods
	
	// get binary value of inverse scale 
	public static int getInvBinValue(int binValue)
	{
		return binValue ^ 0b111111111111; // xor bitwise inversion
	}		
	
	// convert 12 bit binary scale representation into an 
	// int array containing interval numbers
	public static int[] bin2IntValues(int binValue)
	{
		int size = 0;
		int b = binValue;		
		
		int[] temp = new int[12];
		for (int i=0; i < 12; i++)
		{
			int mask = 0b1;
			if ((mask & b) == 1)
			{
				temp[12-size-1] = 12-i;
				size++;
			}
			b = b >>> 1; // shift one bit position from l to r			
		}
		int[] values = new int[size];
		System.arraycopy(temp,12-size,values,0,size);				
		return values;
	}
		
	public boolean isTransposable() 
	{
		return ((binValue != 0b000000000000) && (binValue != 0b111111111111));
		
	}
	
}

package de.airmann.scalefreq.core;

public class Note {
	
	private double freq;
	private int index;
	private String label;	
	
	public Note(double freq,int index)
	{
		this(freq,index,"");		
	}
		
	public Note(double freq, int index, String label)
	{
		this.freq = freq;
		this.index = index;		
		this.label = label;		
	}
	
	public int getIndex()
	{
		return this.index;
	}	
	
	public double getFreq()
	{
		return freq;	
	}
	
	public void setFreq(double freq)
	{
		this.freq = freq;
	}
	
	public String getLabel()
	{
		return label;	
	}
	
	public String getFreqText()
	{
		String unit = "";
		String freqText = "";
		
		double f =  this.freq;		
		if (f >= 10000d)
		{
			f = f / 1000d;
			f = java.lang.Math.round(f * 100d) / 100d;
			unit = "kHz";
		}
		else {
			if (f >= 1000)
			{
				f = f / 1000d;
				f = java.lang.Math.round(f * 100d) / 100d;
				unit = "kHz";
			}
			else
			{							
				f = java.lang.Math.round(f * 10d) / 10d;
				unit = "Hz";
			}
		}		
		
		double f_round = java.lang.Math.round(f);
		if (f_round == f)
		{
			freqText += (int) f_round; // Nachkomma .0 wegschneiden
		}
		else
		{
			freqText += f;
		}
		
		return freqText+" "+unit;	
	}
	
	public void setLabel(String label)
	{
		this.label = label;
	}
	
	public String toString()
	{
		
		return this.getFreqText()+" "+label;
	}
	
	public Note copy()
	{
		return new Note(this.freq,this.index,this.label);
	}
}

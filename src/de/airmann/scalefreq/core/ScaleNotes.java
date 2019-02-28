package de.airmann.scalefreq.core;

public class ScaleNotes {
	
	// default tuning in Hz for A0 
	// See http://en.wikipedia.org/wiki/Concert_pitch
	public static double default_tuning =  440;
	private double tuning = 0;
	
	// notes / range
	public static int first_octave = -1;
	public static int last_octave = 10;
	private int octave_range = 0;
	private int octave_offset = 0;
	private Note notes[] = null;	
		
	// constructor
	public ScaleNotes() 
	{
		this.computeOctaveRange();
		this.setTuning(default_tuning);
	}
	
	private void computeOctaveRange()
	{		
		int range = 0;
	    if (ScaleNotes.first_octave < 0)
	    {
	    	this.octave_offset = ScaleNotes.first_octave * -1; 
	    	if (ScaleNotes.last_octave < 0)	    	
	    		range = (ScaleNotes.first_octave * -1) - (ScaleNotes.last_octave * -1) + 1;	    	
	    	else	    	
	    		range = ScaleNotes.first_octave * -1 + ScaleNotes.last_octave + 1;    	
	    }
	    else 
	    {
	    	this.octave_offset = ScaleNotes.first_octave * -1;
	    	range = ScaleNotes.last_octave - ScaleNotes.first_octave + 1;
	    
	    }
	    this.octave_range = range+1;  // +1 octave overlap because of tonic shifts   
	}
	
	// Number of octaves+1, starting from 0
	public int getOctaveRange()
	{
		return this.octave_range;
	}
	
	public int getLength()
	{
		return notes.length;
	}
	
	// fill note frequency array 
	public void setTuning(double tuning)
	{
		this.tuning = tuning;
				
		// A4 => 4th octave and ninth halftone
		// we have to subtract this, since we want to start from tonic C 
		int first_halftone = (ScaleNotes.first_octave-4)*12 - 9; // 9 is index of A
	    int last_halftone = (ScaleNotes.last_octave-4+1)*12 + (12-9); // +1 octave overlap because of tonal shifts
	    	    	    	
	    this.notes = new Note[this.octave_range*12];
	    
	    for (int i=first_halftone; i < last_halftone; i++)
	    {
	    	int offset = java.lang.Math.abs((ScaleNotes.first_octave-4)*12 - 9);
	    	int index = i + offset;
	    		    	
	    	// freqency formula based on A4: http://en.wikipedia.org/wiki/Note
	    	this.notes[index] = new Note(
	    			java.lang.Math.pow(2, (double) i/12) * this.tuning,index);
	    	
	    	Integer o = new Integer((index / 12) - 1);  	    		    	
	    	switch (index % 12)
	    	{
	    		case 0 : this.notes[index].setLabel("C"+o); break;
	    		case 1: this.notes[index].setLabel("C#"+o); break;
	    		case 2: this.notes[index].setLabel("D"+o); break;
	    		case 3: this.notes[index].setLabel("D#"+o); break;
	    		case 4: this.notes[index].setLabel("E"+o); break;
	    		case 5: this.notes[index].setLabel("F"+o); break;
	    		case 6: this.notes[index].setLabel("F#"+o); break;
	    		case 7: this.notes[index].setLabel("G"+o); break;
	    		case 8: this.notes[index].setLabel("G#"+o); break;
	    		case 9: this.notes[index].setLabel("A"+o); break;
	    		case 10: this.notes[index].setLabel("A#"+o); break;
	    		case 11: this.notes[index].setLabel("B"+o); break;
	    		default : this.notes[index].setLabel("ERR"); 
	    	}
	    }		    
	}
	
	public double getTuning()
	{
		return tuning;
	}
	
	// interval = halftone steps 1..12
	public Note getNote(int octave, int interval)
	{				
		int index = (octave + this.octave_offset) * 12 + interval-1;
		
		return this.notes[index];
	}	
	
	public Note getNoteByIndex(int index)
	{
		return this.notes[index];
	}
	
	// shift = chromatic shift (tonal)
	public Note[] getScaleNotes(Scale scale, NoteEnum tonic, int first_octave, int last_octave)
	{
		int[] sc = scale.getIntValues();
		if (sc == null || sc.length < 1) return null;
		if (last_octave < first_octave) return null;
		
		Note result[] = 
				new Note[sc.length*(last_octave - first_octave +1)];
		int col = 0;
		for (int o = first_octave; o <= last_octave; o++)
		{
			for (int i=0; i < sc.length; i++)
			{			
				result[col++] = getNote(o,sc[i]+tonic.getInterval()).copy();
			}			  
		}		
		return result;
	}

	// convenience function
	public Note[] getScaleNotes(Scale scale, NoteEnum tonic, int first_octave)
	{
		return this.getScaleNotes(scale, tonic, first_octave, first_octave);
	}

}

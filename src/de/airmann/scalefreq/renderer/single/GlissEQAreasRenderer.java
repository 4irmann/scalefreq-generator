package de.airmann.scalefreq.renderer.single;

import java.util.prefs.Preferences;

import de.airmann.scalefreq.core.Note;
import de.airmann.scalefreq.core.NoteEnum;
import de.airmann.scalefreq.core.RenderException;
import de.airmann.scalefreq.core.Scale;
import de.airmann.scalefreq.core.ScaleNotes;
import de.airmann.scalefreq.renderer.single.table.ScaleTableRenderer;

public class GlissEQAreasRenderer extends ScaleTableRenderer {	
	
	private int freq_range_start;
	private int freq_range_end;	
	public final static int FREQ_RANGE_MIN = 20;
	public final static int FREQ_RANGE_MAX = 20000;
					
	private boolean alternate_colors;
	private int color_index; // 0..15	
	private int note_width; // in cents (1..100)	
	private int height;		  // 0..10 
	private int vertical_pos; // 0..11

	public GlissEQAreasRenderer() {				
		reset();	
	}
	
	@Override
	public void reset() {	
		super.reset();		
		setFreq_range_start(GlissEQAreasRenderer.FREQ_RANGE_MIN);		
		setFreq_range_end(GlissEQAreasRenderer.FREQ_RANGE_MAX);
		setAlternateColors(false);
		setNoteWidth(50);
		setColorIndex(6);
		setHeight(7);
		setVerticalPos(0);		
	}
	
	@Override
	public void render() throws RenderException	{		
		if (freq_range_start > freq_range_end)
		{
			throw new RenderException("invalid frequency range");
		}
		super.render();
	}
	
	@Override
	protected void renderHeader(Scale scale, NoteEnum tonic)	{
		outln("Enabled,Title,Color,FreqLow,FreqHigh,Height,VertPos");
	}
	
	@Override
	protected void renderBody(Scale scale, NoteEnum tonic) 	{
		Note[] notes = sn.getScaleNotes(
				scale, tonic, ScaleNotes.first_octave, ScaleNotes.last_octave);		
		if (notes == null) return;
		
		for (int lineCounter=0, i=0; (i < notes.length) && (lineCounter < 64); i++)
		{
			Note n = notes[i];			
	
			// see http://en.wikipedia.org/wiki/Cent_%28music%29
			Double freqLow = new Double(
					(n.getFreq()*java.lang.Math.pow(2, (double) (-1*(double)this.note_width/1200d))));
			
			Double freqHigh = new Double(
					(n.getFreq()*java.lang.Math.pow(2, (double) ((double)this.note_width/1200d))));
					
			if (freqLow < Math.max(GlissEQAreasRenderer.FREQ_RANGE_MIN,freq_range_start) || 
				freqHigh < Math.max(GlissEQAreasRenderer.FREQ_RANGE_MIN,freq_range_start)) continue;
			
			if (freqHigh > Math.min(GlissEQAreasRenderer.FREQ_RANGE_MAX,freq_range_end) || 
				freqLow > Math.min(GlissEQAreasRenderer.FREQ_RANGE_MAX,freq_range_end)) break;			
						
			out("1,");							// Enabled											
			out(n.getLabel().replace("#","s")+","); // Title (GlissEQ can not handle #)
			if (this.alternate_colors) {
				out((i%16)+",");				// Color (alternating)
			}
			else {
				out(this.color_index+",");		// Color (fixed)
			}
			out(freqLow+",");					// FreqLow			
			out(freqHigh+",");					// FreqHigh
			out(this.height+",");				// Height
			outln(this.vertical_pos+"");		// Vertpos
			lineCounter++;						// max 64 area entries possible, more will crash GlissEQ
		}				
	}

	@Override
	protected void renderFooter() {		
		// not needed
	}
	
	public int getFreq_range_start() {
		return freq_range_start;
	}

	public void setFreq_range_start(int freq_range_start) {		
		this.freq_range_start = clamp(freq_range_start, 
				GlissEQAreasRenderer.FREQ_RANGE_MIN, GlissEQAreasRenderer.FREQ_RANGE_MAX);			
	}

	public int getFreq_range_end() {
		return freq_range_end;
	}

	public void setFreq_range_end(int freq_range_end) {		
		this.freq_range_end = clamp(freq_range_end, 
				GlissEQAreasRenderer.FREQ_RANGE_MIN, GlissEQAreasRenderer.FREQ_RANGE_MAX);		
	}
	
	public boolean isAlternateColors() {
		return alternate_colors;
	}

	public void setAlternateColors(boolean alternate_colors) {
		this.alternate_colors = alternate_colors;
	}

	public int getNoteWidth() {
		return note_width;
	}

	public void setNoteWidth(int note_width) {
		this.note_width = clamp(note_width,1,100);
	}

	public int getColorIndex() {
		return color_index;
	}

	public void setColorIndex(int color_index) {
		this.color_index = clamp(color_index, 0, 15);
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = clamp(height, 0, 10);
	}

	public int getVerticalpos() {
		return vertical_pos;
	}

	public void setVerticalPos(int vertical_pos) {
		this.vertical_pos = clamp(vertical_pos, 0, 11);
	}		
	
	public void storePreferences(Preferences prefs)	{
		prefs.putInt("gear_freq_range_start", this.freq_range_start);
		prefs.putInt("gear_freq_range_end", this.freq_range_end);
		prefs.putBoolean("gear_alternate_colors", this.alternate_colors);
		prefs.putInt("gear_color_index", this.color_index);
		prefs.putInt("gear_note_width", this.note_width);
		prefs.putInt("gear_height", this.height);
		prefs.putInt("gear_vertical_pos", this.vertical_pos);	
		prefs.putBoolean("gear_enabled", this.enabled);
	}
	
	public void loadPreferences(Preferences prefs)	{	
		setFreq_range_start(prefs.getInt("gear_freq_range_start", this.freq_range_start));
		setFreq_range_end(prefs.getInt("gear_freq_range_end", this.freq_range_end));
		setAlternateColors(prefs.getBoolean("gear_alternate_colors", this.alternate_colors));
		setColorIndex(prefs.getInt("gear_color_index", this.color_index));
		setNoteWidth(prefs.getInt("gear_note_width", this.note_width));
		setHeight(prefs.getInt("gear_height", this.height));
		setVerticalPos(prefs.getInt("gear_vertical_pos", this.vertical_pos));
		setEnabled(prefs.getBoolean("gear_enabled", this.enabled));
	}
	
}

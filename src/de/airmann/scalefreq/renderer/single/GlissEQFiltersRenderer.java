package de.airmann.scalefreq.renderer.single;

import de.airmann.scalefreq.core.Note;
import de.airmann.scalefreq.core.RenderException;
import de.airmann.scalefreq.core.ScaleNotes;

import java.util.prefs.*;

public class GlissEQFiltersRenderer extends ScaleRenderer {
			
	private int freq_range_start;
	private int freq_range_end;
	public final static int FREQ_RANGE_MIN = 20;
	public final static int FREQ_RANGE_MAX = 96000;
				
	private double gain;
	public final static double GAIN_MIN = -30d;
	public final static double GAIN_MAX = 30d;
	
	private double q;
	public final static double Q_MIN = 0.01d;
	public final static double Q_MAX = 7d;
	
	private double dyn;
	public final static double DYN_MIN = 0d;
	public final static double DYN_MAX = 250d;
					
	private GlissEQFilterTypeEnum type;	
	
	public GlissEQFiltersRenderer() {
		reset();		
	}
	
	@Override
	public void reset() {	
		super.reset();		
		setFreq_range_start(GlissEQFiltersRenderer.FREQ_RANGE_MIN);		
		setFreq_range_end(20000);
		setGain(0d);
		setDyn(0d);
		setQ(0.01d);
		setType(GlissEQFilterTypeEnum.PEAKING);		
	}
	
	@Override
	public void render() throws RenderException {
		if (this.scale == null || this.tonic == null)
		{
			throw new RenderException("render parameter not set");
		}
		
		if (this.freq_range_start > this.freq_range_end)
		{
			throw new RenderException("invalid frequency range");
		}
		
		Note[] notes = sn.getScaleNotes(
				this.scale, this.tonic, 
					ScaleNotes.first_octave, ScaleNotes.last_octave);
		if (notes == null) return;
		
		for (int i=0; i < notes.length; i++)
		{
			Note n = notes[i];
			Double freq = new Double(n.getFreq());			

			// freq range
			if (freq < freq_range_start) continue;
			if (freq > freq_range_end) break;			
		
			out(freq+",");		// freq
			out(gain+",");		// Gain
			out(q+",");			// Q
			out(dyn+",");		// dyn
			outln(type+"");	  	// filter type				
		}
	}

	public int getFreq_range_start() {
		return freq_range_start;
	}

	public void setFreq_range_start(int freq_range_start) {		
		this.freq_range_start = clamp(freq_range_start, 
				GlissEQFiltersRenderer.FREQ_RANGE_MIN, 
				GlissEQFiltersRenderer.FREQ_RANGE_MAX);			
	}

	public int getFreq_range_end() {
		return freq_range_end;
	}

	public void setFreq_range_end(int freq_range_end) {		
		this.freq_range_end = clamp(freq_range_end, 
				GlissEQFiltersRenderer.FREQ_RANGE_MIN, 
				GlissEQFiltersRenderer.FREQ_RANGE_MAX);		
	}

	public double getGain() {
		return gain;
	}

	public void setGain(double gain) {
		this.gain = clamp(gain,
				GlissEQFiltersRenderer.GAIN_MIN, 
				GlissEQFiltersRenderer.GAIN_MAX);		
	}

	public double getQ() {
		return q;
	}

	public void setQ(double q) {
		this.q = clamp(q,
				GlissEQFiltersRenderer.Q_MIN, 
				GlissEQFiltersRenderer.Q_MAX);		
	}

	public double getDyn() {
		return dyn;
	}

	public void setDyn(double dyn) {
		this.dyn = clamp(dyn, 
				GlissEQFiltersRenderer.DYN_MIN,
				GlissEQFiltersRenderer.DYN_MAX);
	}

	public GlissEQFilterTypeEnum getType() {
		return this.type;
	}

	public void setType(GlissEQFilterTypeEnum type) {							
		this.type = type;
	}
	
	@Override
	public void storePreferences(Preferences prefs) {
		prefs.putInt("gefr_freq_range_start", this.freq_range_start);
		prefs.putInt("gefr_freq_range_end", this.freq_range_end);
		prefs.putDouble("gefr_gain", this.gain);
		prefs.putDouble("gefr_dyn", this.dyn);
		prefs.putDouble("gefr_q", this.q);
		prefs.putInt("gefr_type", this.type.ordinal());
		prefs.putBoolean("gefr_enabled", this.enabled);		
	}		
	
	@Override
	public void loadPreferences(Preferences prefs) {	
		setFreq_range_start(prefs.getInt("gefr_freq_range_start", this.freq_range_start));
		setFreq_range_end(prefs.getInt("gefr_freq_range_end", this.freq_range_end));
		setGain(prefs.getDouble("gefr_gain", this.gain));
		setDyn(prefs.getDouble("gefr_dyn", this.dyn));
		setQ(prefs.getDouble("gefr_q", this.q));
		setType(GlissEQFilterTypeEnum.values()[prefs.getInt("gefr_type", this.type.ordinal())]);
		setEnabled(prefs.getBoolean("gefr_enabled", this.enabled));		
	}
		
}

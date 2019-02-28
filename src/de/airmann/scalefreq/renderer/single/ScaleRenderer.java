package de.airmann.scalefreq.renderer.single;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.prefs.*;

import de.airmann.scalefreq.core.NoteEnum;
import de.airmann.scalefreq.core.RenderException;
import de.airmann.scalefreq.core.RenderOutput;
import de.airmann.scalefreq.core.Renderable;
import de.airmann.scalefreq.core.Plugable;
import de.airmann.scalefreq.core.Scale;
import de.airmann.scalefreq.core.ScaleEnum;
import de.airmann.scalefreq.core.ScaleNotes;

public abstract class ScaleRenderer implements Renderable, RenderOutput, Plugable {
 	
	protected OutputStream os;
	protected PrintStream ps;
	
	protected Scale scale;
	protected NoteEnum tonic;	
	protected ScaleNotes sn;
	
	protected boolean enabled;
	   	
	public ScaleRenderer()
	{
		this.sn = new ScaleNotes();
		reset();
	}
	
	@Override
	public void setRenderOutput(OutputStream os)
	{		
		this.os = os;
		if (os != null)
		{
			this.ps = new PrintStream(os);
		}		
	}
	
	// initial frequency for A1 note in Hz (default 440 Hz)
	public void setTuning(double tuning)
	{		
		this.sn.setTuning(tuning);		
	}
	
	public double getTuning()
	{
		return this.sn.getTuning();
	}
		
	public void setScale(Scale scale, NoteEnum tonic)
	{		
		setScale(scale);
		setTonic(tonic);
	}		
	
	public Scale getScale()
	{
		return this.scale;
	}	
	
	public void setTonic(NoteEnum tonic)
	{
		if (this.scale == null)
		{
			this.scale = new Scale(ScaleEnum.MAJOR);
		}
		this.tonic = tonic;	
	}
	
	public NoteEnum getTonic()
	{
		return this.tonic;
	}
	
	public void setScale(Scale scale)
	{
		if (this.tonic == null)
		{
			this.tonic = NoteEnum.C;
		}
		this.scale = scale;	
	}		
			
	public abstract void render() throws RenderException;			
	
	public void out(String str)
	{
		ps.print(str);		
	}
	
	public void outln(String str)
	{
		this.out(str+"\n");
	}
	
	@Override
	public void setEnabled(boolean enabled)
	{
		this.enabled = enabled;
	}
	
	@Override
	public boolean isEnabled()
	{
		return this.enabled;
	}		
	
	@Override
	public void reset()
	{
		setEnabled(false);
	}
	
	@Override
	public void loadPreferences(Preferences prefs)
	{
		// n/a
	}
	
	@Override
	public void storePreferences(Preferences prefs)
	{
		// n/a
	}	
	
	// helper
	public static double clamp(double value, double min, double max)
	{		
		return Math.max(min, Math.min(max, value));
	}
	
	// helper
	public static int clamp(int value, int min, int max)
	{		
		return Math.max(min, Math.min(max, value));
	}
			
}

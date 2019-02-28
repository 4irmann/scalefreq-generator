package de.airmann.scalefreq.renderer.multi;
import java.io.FileOutputStream;

import de.airmann.scalefreq.core.NoteEnum;
import de.airmann.scalefreq.core.RenderException;
import de.airmann.scalefreq.core.Scale;
import de.airmann.scalefreq.core.ScaleEnum;
import de.airmann.scalefreq.renderer.single.ScaleRenderer;

public class MultiFileScaleRenderer extends MultiFileRenderer 
{		
	protected ScaleRenderer sr;
	protected ScaleEnum[] scales;
	protected NoteEnum[] tonics;
	
	public void setScaleRenderer(ScaleRenderer sr)
	{
		this.sr = sr;
	}
	
	public void setScales(ScaleEnum[] scales)
	{
		this.scales = scales;
	}
	
	// comfort function
	public void setScales(ScaleEnum scale)
	{
		this.scales = new ScaleEnum[1];
		this.scales[0] = scale;
	}
	
	public void setTonics(NoteEnum[] tonics)
	{
		this.tonics = tonics;
	}
	
	// comfort function
	public void setTonalities(NoteEnum tonic)
	{
		this.tonics = new NoteEnum[1];
		this.tonics[0] = tonic;
	}

	@Override
	public void render() throws RenderException
	{
		super.render();		
		
		if (sr == null || scales == null || tonics == null)
		{
			throw new RenderException("render parameter not set");
		}
		
		NoteEnum[] notes = NoteEnum.values();
		
		for (int i=0; i < scales.length; i++)
		{
			Scale s = new Scale(scales[i]);
			
			for (int j=0; j < tonics.length; j++)
			{
				FileOutputStream fos = null;
				try 
				{
					fos = new FileOutputStream(dirPath+
						prefix+s.getShortLabel()
							+"_"+notes[j].getName()+"_"
								+postfix+"."+extension);
				}
				catch(Exception e)
				{
					e.printStackTrace(System.err);
				}
					
				sr.setRenderOutput(fos);
				sr.setScale(s, tonics[j]);
				sr.render();
			}
		}
	}
	
}

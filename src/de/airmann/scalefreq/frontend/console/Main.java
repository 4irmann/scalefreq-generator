package de.airmann.scalefreq.frontend.console;
import java.io.FileOutputStream;

import de.airmann.scalefreq.core.NoteEnum;
import de.airmann.scalefreq.core.Scale;
import de.airmann.scalefreq.core.ScaleEnum;
import de.airmann.scalefreq.renderer.multi.MultiFileScaleRenderer;
import de.airmann.scalefreq.renderer.multi.MultiKeyboardRenderer;
import de.airmann.scalefreq.renderer.single.GlissEQFiltersRenderer;
import de.airmann.scalefreq.renderer.single.GlissEQAreasRenderer;
import de.airmann.scalefreq.renderer.single.KeyboardRenderer;
import de.airmann.scalefreq.renderer.single.table.HTMLRenderer;

public class Main {
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
				
		FileOutputStream fos = null;
		String folder = "i:/data/";		
		
		try {
		
			Scale s = new Scale(ScaleEnum.MAJOR);
			HTMLRenderer r = new HTMLRenderer();
			fos = new FileOutputStream(folder+s.getLabel()+"_scales.html");				
			r.setRenderOutput(fos);
			r.setScale(s);
			r.render();		
			fos.close();
	
			s = new Scale(ScaleEnum.MAJOR,true);
			fos = new FileOutputStream(folder+s.getLabel()+"_scales.html");
			r.setRenderOutput(fos);
			r.setScale(s);
			r.render();
			fos.close();	
			
			s = new Scale(ScaleEnum.MINOR);		
			fos = new FileOutputStream(folder+s.getLabel()+"_scales.html");
			r.setRenderOutput(fos);
			r.setScale(s);
			r.render();		
			fos.close();				
			
			s = new Scale(ScaleEnum.MINOR,true);		
			fos = new FileOutputStream(folder+s.getLabel()+"_scales.html");
			r.setRenderOutput(fos);
			r.setScale(s);
			r.render();		
			fos.close();									
			
			// GlissEQ Ranges
			
			GlissEQAreasRenderer rr = new GlissEQAreasRenderer();
			
			s = new Scale(ScaleEnum.MINOR);
			fos = new FileOutputStream(folder+s.getLabel()+"_scale_ranges.csv");
			rr.setRenderOutput(fos);
			rr.setScale(s, NoteEnum.D);
			rr.render();
			fos.close();	
			
			s = new Scale(ScaleEnum.MINOR,true);
			fos = new FileOutputStream(folder+s.getLabel()+"_scale_ranges.csv");
			rr.setRenderOutput(fos);			
			rr.setScale(s, NoteEnum.D);
			rr.render();
			fos.close();	
			
			// GlissEQ Filter
			
			GlissEQFiltersRenderer fr = new GlissEQFiltersRenderer();
			s = new Scale(ScaleEnum.MINOR);
			fos = new FileOutputStream(folder+s.getLabel()+"_scale_filters.csv");
			fr.setRenderOutput(fos);
			fr.setScale(s,NoteEnum.D);
			fr.render();
			fos.close();
					
			s = new Scale(ScaleEnum.MINOR,true);
			fos = new FileOutputStream(folder+s.getLabel()+"_scale_filters.csv");
			fr.setRenderOutput(fos);
			fr.setScale(s,NoteEnum.D);
			fr.render();
			fos.close();		
			
			// Keyboard
			s = new Scale(ScaleEnum.MINOR);
			KeyboardRenderer kr = new KeyboardRenderer();
			fos = new FileOutputStream(folder+s.getLabel()+"_keyboard.png");
			kr.setRenderOutput(fos);
			kr.setScale(s,NoteEnum.F);
			kr.render();
			fos.close();
			
			// Multi Keyboard (single file)
			MultiFileScaleRenderer mfr = new MultiFileScaleRenderer();
			
			// TODO: create output dir automatically
			
			mfr.setOutputDir(folder+"keyboard_single/");
			mfr.setFileNamePattern("", "", "png");
			mfr.setScales(ScaleEnum.DORIAN);		
			mfr.setTonalities(NoteEnum.C);
			mfr.setScaleRenderer(kr);
			mfr.render();
			
			// Multi Keyboard (composite html)
			MultiKeyboardRenderer mkr = new MultiKeyboardRenderer();
			
			//TODO: create output dir automatically
			
			mkr.setOutputDir(folder+"keyboard_html/");
			fos = new FileOutputStream(mkr.getOutputDir()+"index.html");
			mkr.setRenderOutput(fos);						
			mkr.setScales(ScaleEnum.values());		
			mkr.setTonics(NoteEnum.values());			
			mkr.render();
			
		}
		catch(Exception e)
		{
			e.printStackTrace(System.err);
		}
		
	}

}

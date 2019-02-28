package de.airmann.scalefreq.renderer.single.table;

import java.util.prefs.Preferences;

import de.airmann.scalefreq.core.Note;
import de.airmann.scalefreq.core.NoteEnum;
import de.airmann.scalefreq.core.RenderException;
import de.airmann.scalefreq.core.Scale;
import de.airmann.scalefreq.core.ScaleEnum;
import de.airmann.scalefreq.core.ScaleNotes;

public class HTMLRenderer extends ScaleTableRenderer {
	
	private boolean chromaticMode;
	private boolean allScalesMode;
	
	public HTMLRenderer() {
		reset();		
	}
	
	@Override
	public void reset() {		
		super.reset();
		setChromaticMode(false);
		setAllScalesMode(false);		
	}
	
	public void setChromaticMode(boolean chromaticMode) {		
		this.chromaticMode = chromaticMode;
	}		
	
	public boolean getChromaticMode()  {
		return this.chromaticMode;		
	}
	
	public void setAllScalesMode(boolean allScalesMode) {
		this.allScalesMode = allScalesMode;
	}
	
	public boolean getAllScalesMode() {
		return this.allScalesMode;
	}			
	
	protected void renderDocHeader() {
		// xhtml doctype
		outln("<?xml version=\"1.0\" ?>");
		out("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" ");
		outln("\"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">");
		outln("<HTML xmlns=\"http://www.w3.org/1999/xhtml\"><HEAD/><BODY>");
	}
		
	protected void renderDocFooter() {
		out("</BODY></HTML>");
	}		
	
	@Override
	public void render() throws RenderException
	{			
		if (this.scale == null || this.tonic == null) {
			throw new RenderException("render paramter not set");
		}
	
		renderDocHeader();		
		
		if (!this.allScalesMode) {		
			renderHeader(this.scale, this.tonic);
			renderBody(this.scale, this.tonic);
			renderFooter();
		}
		else {			
			ScaleEnum[] values = ScaleEnum.values();
						
			for (int i=0; i < values.length; i++) {				
				Scale sc = new Scale(values[i]);
				sc.setInverse(this.scale.isInverse());
				renderHeader(sc,this.tonic);
				renderBody(sc,this.tonic);
				renderFooter();
			}										
		}
		
		renderDocFooter();
	}				
	
	@Override
	protected void renderHeader(Scale s, NoteEnum tonic)
	{
		String title="";
		if (!this.chromaticMode) {					
			title = tonic.getName("#")+" ";
		}
		title += s.getLabel();		
		title = title.toUpperCase();								
		outln("<HEAD1>"+title+" SCALE</HEAD1><TABLE>\n<TR><p/></tr>");	
	}	
	
	@Override
	protected void renderBody(Scale s, NoteEnum tonic)
	{	
		if (!this.chromaticMode || (!s.isTransposable())) {
			renderScaleForBody(null,s,tonic);
		}
		else {
			renderScaleChromatically(s.getLabel(), s);
		}			
	}
	
	@Override
	protected void renderFooter()
	{
		out("</TABLE>");
	}
	
	protected void renderScaleChromatically(String title, Scale scale)
	{
		NoteEnum[] values = NoteEnum.values();				
		for (int i=0; i < values.length; i++)
		{
			renderScaleForBody(values[i].getName("#")+" "+title,scale,values[i]);			
		}
	}								
	
	protected void renderScaleForBody(String title, Scale scale, NoteEnum tonic)
	{			
		// Table header
		if (title != null) {			
			outln("<TR><TH  colspan=\"5\" align=\"left\">"+title+"</TH></TR>");			
		}
		
		out("<TR><TH>Interv.__  </TH><TH>1________</TH><TH>2________</TH><TH>3________</TH>");
		out("<TH>4________</TH><TH>5________</TH><TH>6________</TH><TH>7________</TH>");
		out("<TH>8________</TH><TH>9________</TH><TH>10_______</TH><TH>11_______</TH>");
		outln("<TH>12_______</TH></TR>");
		
		Note notes[] = sn.getScaleNotes(
				scale, tonic, ScaleNotes.first_octave, ScaleNotes.last_octave);
		int found;
		
		out("<TR><TD></TD>");		
		NoteEnum[] values = NoteEnum.values();
		String s = null;
		for (int i=tonic.getInterval(); i < 12+tonic.getInterval(); i++)
		{
			s = values[i % 12].getName("#");
			out("<TD>"+s+"</TD>");
		}
		outln("</TR>");
		
		int[] sc = scale.getIntValues();
		for (int oc=0; oc <  sn.getOctaveRange()-1; oc++)
		{
			out("<TR><TD>"+(oc-1)+"</TD>");			
			for (int i=0; i < 12; i++)
			{					
				out("<TD>");
				found = -1;
				
				for (int j=0; j < sc.length; j++)
				{			
					if (sc[j] == i+1) 
					{
						found = j;						
						break;
					}					
				}
				if (found > -1)
				{			
					out(notes[oc*sc.length+found].getFreqText());
				}
				out("</TD>");
			}
			outln("</TR>");
		}
		outln("<TR><TD><P/></TD></TR>");
	}		
	
	@Override
	public void storePreferences(Preferences prefs) {
		prefs.putBoolean("htmr_chromatic_mode", this.chromaticMode);
		prefs.putBoolean("htmr_all_scales_mode", this.allScalesMode);
		prefs.putBoolean("htmr_enabled", this.enabled);
	}
	
	@Override
	public void loadPreferences(Preferences prefs) {			
		setChromaticMode(prefs.getBoolean("htmr_chromatic_mode", this.chromaticMode));
		setAllScalesMode(prefs.getBoolean("htmr_all_scales_mode", this.allScalesMode));
		setEnabled(prefs.getBoolean("htmr_enabled", this.enabled));
	}
	
}

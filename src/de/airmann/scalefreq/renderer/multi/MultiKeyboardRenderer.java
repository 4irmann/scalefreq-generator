package de.airmann.scalefreq.renderer.multi;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;

import de.airmann.scalefreq.core.NoteEnum;
import de.airmann.scalefreq.core.RenderException;
import de.airmann.scalefreq.core.RenderOutput;
import de.airmann.scalefreq.core.Scale;
import de.airmann.scalefreq.renderer.single.KeyboardRenderer;
import de.airmann.scalefreq.renderer.single.ScaleRenderer;

// TODO: shouldn't this be a table renderer, too ?
public class MultiKeyboardRenderer extends MultiFileScaleRenderer implements RenderOutput
{
	protected OutputStream os;
	protected PrintStream ps;
		
	public MultiKeyboardRenderer()
	{
		this.sr = new KeyboardRenderer();
		((KeyboardRenderer)this.sr).setScaleFactor(0.4);
		this.extension = "png";
	}
	
	@Override
	public void setScaleRenderer(ScaleRenderer sr)
	{
		// not implemented / supported
	}
	
	@Override
	public void setFileNamePattern(String s, String s1, String s2)
	{
		// not implemented / supported
	}
	
	@Override
	public void setRenderOutput(OutputStream os)
	{
		this.os = os;
		this.ps = new PrintStream(os);
	}
		
	protected void renderHeader(String title) throws RenderException
	{		
		title = title.toUpperCase();
		
		// xhtml doctype
		outln("<?xml version=\"1.0\" ?>");
		out("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" ");
		outln("\"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">");
		outln("<HTML xmlns=\"http://www.w3.org/1999/xhtml\"><HEAD/><BODY><H1>"
				+title+" SCALES</H1>");		
	}
		
	protected void renderBody() throws RenderException
	{
		if (sr == null || scales == null || tonics == null)
		{
			throw new RenderException("render parameter not set");
		}
		
		
		NoteEnum[] notes = NoteEnum.values();		
		for (int i=0; i < scales.length; i++)
		{
			Scale s = new Scale(scales[i]);
			
			outln("<hr/>");
			outln("<h2>"+s.getLabel()+"</h2>");

			out("<TABLE cellpadding=\"10\">");
			out("<tr>");
			
			for (int j=0; j < tonics.length; j++)
			{
				FileOutputStream fos = null;
				try 
				{
					String fileName = s.getShortLabel()+"_"+notes[j].getName()+"."+extension;					
					fos = new FileOutputStream(dirPath+fileName);
					out("<td><img alt=\"title\" src=\""+fileName+"\"/></td>");
				}
				catch(Exception e)
				{
					e.printStackTrace(System.err);
				}
					
				sr.setRenderOutput(fos);
				sr.setScale(s, tonics[j]);
				sr.render();
				
				// linebreak
				if (j == 6)
				{
					outln("</tr>");
					out("<tr>");
				}
			}						
			outln("</tr></table>");			
		}
	}
	
	protected void renderFooter() throws RenderException
	{
		out("</BODY></HTML>");
	}
	
	@Override
	public void render() throws RenderException
	{		
		renderHeader("Keyboard");
		renderBody();
		renderFooter();		
	}
	
	public void out(String str)
	{
		ps.print(str);		
	}
	
	public void outln(String str)
	{
		this.out(str+"\n");
	}
	
}

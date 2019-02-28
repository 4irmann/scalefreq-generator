package de.airmann.scalefreq.renderer.multi;
import java.io.File;

import de.airmann.scalefreq.core.RenderException;
import de.airmann.scalefreq.core.Renderable;

public abstract class MultiFileRenderer implements Renderable {
	
	protected String dirPath;
	protected String prefix;
	protected String postfix;
	protected String extension;	
	
	public void setOutputDir(String dirPath)
	{
		this.dirPath = dirPath;
	}
	
	public String getOutputDir()
	{
		return this.dirPath;
	}
		
	public void setFileNamePattern(String prefix, String postfix, String extension)
	{
		this.prefix = prefix;
		this.postfix = postfix;
		this.extension = extension;
	}
	
	@Override
	public void render() throws RenderException
	{
		if (dirPath == null || prefix == null || 
			postfix == null || extension == null) 
		{
			throw new RenderException("render parameter not set");				
		}
		
		File dir = new File(dirPath);
		if (!dir.exists())
		{
			dir.mkdirs();
		}				
			
	}
		
}

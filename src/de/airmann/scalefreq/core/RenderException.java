package de.airmann.scalefreq.core;

public class RenderException extends Exception {

	public static final long serialVersionUID = 394879587;
	
	public RenderException()
	{
		super("undefined error");
	}
	
	public RenderException(String msg)
	{
		super(msg);
	}
		
}

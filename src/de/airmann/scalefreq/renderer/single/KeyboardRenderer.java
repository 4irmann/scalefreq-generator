package de.airmann.scalefreq.renderer.single;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

import de.airmann.scalefreq.core.Note;
import de.airmann.scalefreq.core.NoteEnum;
import de.airmann.scalefreq.core.Scale;
import de.airmann.scalefreq.renderer.single.table.ScaleTableRenderer;

import java.io.IOException;
import java.lang.Math;

public class KeyboardRenderer extends ScaleTableRenderer {

	private BufferedImage img = null;
	private Graphics2D g2 = null;
	
	private double sc_factor = 1.0d;
	
	// constants
	
	private final int text_height = 35;
	private final int img_width = 280;
	private final int img_height = 200;		
	private final int kb_width = img_width;
	private final int kb_height = img_height-text_height;
			
	private final double bl_width_factor = 0.666d;
	private final double bl_height_factor = 0.60d;
	private final double yl_width_factor = 0.4333d;
	private final double yl_height_factor = 0.30d;
	private final double yl_black_dist_factor = 0.8666d;
	private final double yl_white_dist_factor = 0.9333d;
	private final Color scaleColor = Color.lightGray;
	
	public KeyboardRenderer()
	{		
		this.setScaleFactor(sc_factor);
	}
	
	public void setScaleFactor(double sc_factor)
	{
		this.sc_factor = sc_factor;					
	}
	
	public BufferedImage getImage()
	{
		return img;
	}
	
	@Override
	protected void renderHeader(Scale scale, NoteEnum tonic) 
	{
		double img_width = this.img_width * sc_factor;
		double img_height = this.img_height * sc_factor;
		double text_height = this.text_height * sc_factor;
		
		this.img = new BufferedImage(rnd(img_width),rnd(img_height),BufferedImage.TYPE_INT_ARGB);
				
		this.g2 = img.createGraphics();
		this.g2.setColor(Color.BLACK);
		this.g2.setFont(new Font("Serif",Font.BOLD, rnd(text_height)));				
		this.g2.drawString(
				tonic.getName("#")+" "+scale.getLabel(), 0, rnd(text_height-2));
	}

	// helper
	private int rnd(double d)
	{
		return (int) Math.round(d);
	}
	
	@Override
	protected void renderBody(Scale scale, NoteEnum tonic) 
	{		
		double kb_width = this.kb_width * sc_factor;
		double kb_height = this.kb_height * sc_factor;
		double text_height = this.text_height * sc_factor;		
		double key_width = kb_width / 7;		
		double key_height = kb_height;
		
		double bl_width = key_width * bl_width_factor;
		double bl_height = key_height * bl_height_factor;
		double yl_width = key_width * yl_width_factor;
		double yl_height = key_height * yl_height_factor;			
		
		// draw white keys		
		this.g2.setBackground(Color.WHITE);			
		for (int i=0; i < 7; i++)
		{
			this.g2.setColor(Color.WHITE);
			this.g2.fillRect(rnd(i*key_width), rnd(text_height), rnd(key_width)-1, rnd(key_height)-1);
			this.g2.setColor(Color.BLACK);
			this.g2.drawRect(rnd(i*key_width), rnd(text_height), rnd(key_width)-1, rnd(key_height)-1);			
		}
		
		// draw black keys
		for (int i=0; i < 7; i++)
		{
			switch(i)
			{
				case 1 :
				case 2 :
				case 4 : 
				case 5 : 
				case 6 :
					this.g2.setColor(Color.BLACK);
					this.g2.fillRect(
						rnd(i*key_width-bl_width*0.5d), rnd(text_height), rnd(bl_width)-1, rnd(bl_height)-1);
				default : ;
			}
		}
		
		// draw scale 
		Note[] notes = 
				sn.getScaleNotes(scale, tonic, 4, 4);
		if (notes != null) {			
			for (int i=0; i < notes.length; i++)
			{
				Note n = notes[i];	
				int index = n.getIndex();			
				int col = 0;
				boolean black = false;
				switch (index % 12)
				{
					case 0 : col = 0; break; 
					case 1 : col = 1; black = true; break;
					case 2 : col = 1; break;
					case 3 : col = 2; black = true; break;
					case 4 : col = 2; break;
					case 5 : col = 3; break;
					case 6 : col = 4; black = true; break;
					case 7 : col = 4; break;
					case 8 : col = 5; black = true; break;
					case 9 : col = 5; break;
					case 10 : col = 6; black = true; break;
					case 11 : col = 6;
				}
				if (black)
				{
					this.g2.setColor(scaleColor);
					this.g2.fillRect(
						rnd(col*key_width-yl_width*0.5d), 
							rnd(text_height+(bl_height-yl_height)*yl_black_dist_factor), 
								rnd(yl_width)-1, rnd(yl_height)-1);			
				}
				else 
				{
					this.g2.setColor(scaleColor);				
					this.g2.fillRect(
						rnd(col*key_width+key_width*0.5d-yl_width*0.5d), 
							rnd(text_height+(key_height-yl_height)*yl_white_dist_factor), 
								rnd(yl_width)-1, rnd(yl_height)-1);							  				
				}
			}
		}
	}

	@Override
	protected void renderFooter() 
	{	
		try
		{
			if (os != null)
			{
				ImageIO.write(this.img, "png", os);
			}
		}
		catch(IOException e)		
		{
			e.printStackTrace(System.err);
			return;
		}
	}		
}

package de.airmann.scalefreq.core;

import java.util.prefs.*;

public interface Plugable 
{	
	public void setEnabled(boolean enabled);
	public boolean isEnabled();
		
	public void loadPreferences(Preferences prefs);
	public void storePreferences(Preferences prefs);
	public void reset();	
}

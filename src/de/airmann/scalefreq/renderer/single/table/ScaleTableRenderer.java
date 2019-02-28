package de.airmann.scalefreq.renderer.single.table;

import de.airmann.scalefreq.core.NoteEnum;
import de.airmann.scalefreq.core.RenderException;
import de.airmann.scalefreq.core.Scale;
import de.airmann.scalefreq.renderer.single.ScaleRenderer;

public abstract class ScaleTableRenderer extends ScaleRenderer {
	
	protected abstract void renderHeader(Scale scale, NoteEnum tonic) throws RenderException;
	protected abstract void renderBody(Scale scale, NoteEnum tonic) throws RenderException;
	protected abstract void renderFooter() throws RenderException;

	@Override
	public void render() throws RenderException
	{		
		if (this.scale == null || this.tonic == null)
		{
			throw new RenderException("render paramter not set");
		}
		
		renderHeader(this.scale, this.tonic);
		renderBody(this.scale, this.tonic);
		renderFooter();
	}

}

package de.airmann.scalefreq.frontend.swt;


import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import de.airmann.scalefreq.core.*;
import de.airmann.scalefreq.renderer.single.*;
import de.airmann.scalefreq.renderer.single.table.*;

import java.awt.image.BufferedImage;
import java.awt.image.DirectColorModel;
import java.awt.image.IndexColorModel;
import java.awt.image.WritableRaster;
import java.io.File;
//import java.io.FileNotFoundException;
import java.io.FileOutputStream;
//import java.io.IOException;
import java.util.ArrayList;
import java.util.prefs.*;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.graphics.Point;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.jface.databinding.swt.SWTObservables;

public class ScaleFreqGUI {
	
	protected Shell shlScalefreqGeneratorGui;

	private final static String nodePath = "/scalefreq/settings";	
	private static boolean resetMode = false;
	
	private ArrayList<ScaleRenderer> sr;
	private KeyboardRenderer kr;	
	private GlissEQFiltersRenderer gefr;
	private GlissEQAreasRenderer gear;
	private HTMLRenderer htmr;
	
	private Text txtOutputFolder;
	private Canvas canvas;		
	 	
	private Preferences prefs;
	
	// global Parameters	
	private boolean glbInverseScale;
	private int glbTuning;
	private int glbScaleOrdinal;
	private int glbTonicOrdinal;
	
	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String[] args) {
				
		Display display = Display.getDefault();		
		do {			
			Realm.runWithDefault(SWTObservables.getRealm(display), new Runnable() {
				public void run() {
					try {										
						ScaleFreqGUI window = new ScaleFreqGUI();
						window.open();					
					} catch (Exception e) {
						e.printStackTrace();
					}				
				}
			});
		} 
		while (resetMode);		
	}
	
	/**
	 * Open the window.
	 */
	public void open() {

		resetGlobal();
		loadPreferences();
		
		// create display and widgets		
		Display display = Display.getDefault();
											
		createContents();
		shlScalefreqGeneratorGui.open();
		shlScalefreqGeneratorGui.layout();							
		
		while (!shlScalefreqGeneratorGui.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		
		storePreferences();
	}
	
	private void resetGlobal()
	{		
		this.glbInverseScale = false;
		this.glbTuning = 440;
		this.glbScaleOrdinal = ScaleEnum.MAJOR.ordinal();
		this.glbTonicOrdinal = NoteEnum.C.ordinal();
	}
	
	private void storePreferences()
	{
		// store global settings		
		prefs.putBoolean("global_inverse_scale", this.glbInverseScale);
		prefs.putInt("global_tuning", this.glbTuning);
		prefs.putInt("global_scale", this.glbScaleOrdinal);
		prefs.putInt("global_tonic", this.glbTonicOrdinal);
				
		// store renderer settings		
		for(ScaleRenderer sc : sr)  {					
			sc.storePreferences(prefs);
		}		
	}		
	
	private void loadPreferences()
	{
		prefs = Preferences.userRoot().node(nodePath);

		// restore global settings		
		this.glbInverseScale = prefs.getBoolean("global_inverse_scale", this.glbInverseScale);
		this.glbTuning = prefs.getInt("global_tuning", this.glbTuning);
		this.glbScaleOrdinal= prefs.getInt("global_scale", this.glbScaleOrdinal);
		this.glbTonicOrdinal = prefs.getInt("global_tonic", this.glbTonicOrdinal);
	}
	
	// reset all gobal and renderer parameters to default values
	private void resetPreferences()
	{
		resetGlobal();				
		for(ScaleRenderer sc : sr)  {					
			sc.reset();
		}				
	}
	
	/**
	 * Create contents of the window.
	 * @wbp.parser.entryPoint
	 */
	protected void createContents() {
		
		resetMode = false;
		
		shlScalefreqGeneratorGui = new Shell(SWT.CLOSE | SWT.MIN | SWT.TITLE);
		shlScalefreqGeneratorGui.setSize(574, 487);
		shlScalefreqGeneratorGui.setText("ScaleFreq Generator GUI v1.01 - \u00A9 4irmann 2014, License: ALv2");
		shlScalefreqGeneratorGui.setImage(SWTResourceManager.getImage(ScaleFreqGUI.class, "/de/airmann/scalefreq/resource/icon_64.png"));

		// center Dialog
		Point size = shlScalefreqGeneratorGui.getSize();
		Rectangle screen = Display.getDefault().getMonitors()[0].getBounds();
		shlScalefreqGeneratorGui.setBounds((screen.width-size.x)/2, (screen.height-size.y)/2, size.x, size.y);
												
		// initialize all renderers
		
		sr = new ArrayList<ScaleRenderer>();
		
		gefr = new GlissEQFiltersRenderer();
		sr.add(gefr);
									
		gear = new GlissEQAreasRenderer();
		sr.add(gear);
		
		htmr = new HTMLRenderer();
		sr.add(htmr);
		
		kr = new KeyboardRenderer();
		sr.add(kr);
		
		for(ScaleRenderer sc : sr)  {			
			sc.setScale(new Scale(ScaleEnum.values()[this.glbScaleOrdinal],this.glbInverseScale));
			sc.setTonic(NoteEnum.values()[this.glbTonicOrdinal]);
			sc.setTuning(this.glbTuning);
			sc.setRenderOutput(null);			
			sc.loadPreferences(prefs);
		}
		
		// initialize keyboard renderer
		kr.setScaleFactor(0.4d);
		kr.setRenderOutput(null);				
		try {
			kr.render();
		}
		catch(RenderException re) {
			re.printStackTrace();			
		}
		
		// global widgets
		
		createGlobalParametersGrp();

		// renderer widgets
		
		TabFolder tabFolder = new TabFolder(shlScalefreqGeneratorGui, SWT.NONE);
		tabFolder.setFont(SWTResourceManager.getFont("Arial", 9, SWT.NORMAL));
		tabFolder.setBounds(10, 202, 548, 193);
		
		TabItem tbtmHtml = new TabItem(tabFolder, SWT.NONE);
		tbtmHtml.setText("HTML");		
		Composite cmpsHTML = new Composite(tabFolder, SWT.NONE);
		tbtmHtml.setControl(cmpsHTML);
		createHTMLTab(cmpsHTML);					
		Button btnInclude_2 = new Button(cmpsHTML, SWT.CHECK);
		btnInclude_2.setFont(SWTResourceManager.getFont("Arial", 9, SWT.NORMAL));
		btnInclude_2.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Button b = (Button) e.getSource();
				htmr.setEnabled(b.getSelection());		
			}
		});
		btnInclude_2.setBounds(10, 10, 93, 16);
		btnInclude_2.setText("Include");
		btnInclude_2.setSelection(htmr.isEnabled());
		
		Button btnRenderChromatically = new Button(cmpsHTML, SWT.CHECK);	
		btnRenderChromatically.setFont(SWTResourceManager.getFont("Arial", 9, SWT.NORMAL));
		btnRenderChromatically.addSelectionListener(new SelectionAdapter() {			
			@Override
			public void widgetSelected(SelectionEvent e) {
				Button b = (Button) e.getSource();
				htmr.setChromaticMode(b.getSelection());								
			}
		});
		btnRenderChromatically.setBounds(10, 53, 171, 16);
		btnRenderChromatically.setText("Render chromatically");
		btnRenderChromatically.setSelection(htmr.getChromaticMode());
		
		Button btnRenderAllScales = new Button(cmpsHTML, SWT.CHECK);
		btnRenderAllScales.setFont(SWTResourceManager.getFont("Arial", 9, SWT.NORMAL));
		btnRenderAllScales.addSelectionListener(new SelectionAdapter() {			
			@Override
			public void widgetSelected(SelectionEvent e) {
				Button b = (Button) e.getSource();
				htmr.setAllScalesMode(b.getSelection());								
			}
		});
		btnRenderAllScales.setBounds(10, 88, 120, 16);
		btnRenderAllScales.setText("Render all scales");
		btnRenderAllScales.setSelection(htmr.getAllScalesMode());
		
		TabItem tbtmGlissEQFilter = new TabItem(tabFolder, SWT.NONE);
		tbtmGlissEQFilter.setText("GlissEQ Filters");				
		Composite cmpsGlissEQFilter = new Composite(tabFolder, SWT.NONE);
		tbtmGlissEQFilter.setControl(cmpsGlissEQFilter);
		createGlissEQFiltersTab(cmpsGlissEQFilter);										
		
		TabItem tbtmGlissEQAreas = new TabItem(tabFolder, SWT.NONE);
		tbtmGlissEQAreas.setText("GlissEQ Areas");		
		Composite cmpsGlissEQAreas = new Composite(tabFolder, SWT.NONE);
		tbtmGlissEQAreas.setControl(cmpsGlissEQAreas);
		createGlissEQAreasTab(cmpsGlissEQAreas);
		
		// render
		
		Button btnRender = new Button(shlScalefreqGeneratorGui, SWT.NONE);
		btnRender.setFont(SWTResourceManager.getFont("Arial", 9, SWT.NORMAL));
		btnRender.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
														
				// Create a directory; all non-existent ancestor 
				// directories are automatically created
				File folder = new File(txtOutputFolder.getText());				
				if (!folder.exists()) {
					folder.mkdirs();
				}
										
				try {
					if (gefr.isEnabled())
					{
						renderGlissEQFilters(folder);
					}
					if (gear.isEnabled())
					{
						renderGlissEQAreas(folder);
					}
					if (htmr.isEnabled())
					{											
						renderHTML(folder);
					}
				}
				catch(Exception e1) {  // TODO: add message dialog
					e1.printStackTrace(); 
				}				
			}
		});
		btnRender.setBounds(10, 412, 75, 25);
		btnRender.setText("Render");		
		
		Button btnReset = new Button(shlScalefreqGeneratorGui, SWT.NONE);
		btnReset.setFont(SWTResourceManager.getFont("Arial", 9, SWT.NORMAL));
		btnReset.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				resetPreferences();
				resetMode = true;
				shlScalefreqGeneratorGui.dispose();
			}
		});
		btnReset.setBounds(476, 412, 75, 25);
		btnReset.setText("Reset");
	}	
	
	private void createGlobalParametersGrp()
	{
		Group grpGobalSettings = new Group(shlScalefreqGeneratorGui, SWT.NONE);
		grpGobalSettings.setFont(SWTResourceManager.getFont("Arial", 9, SWT.NORMAL));
		grpGobalSettings.setText("Gobal Settings");
		grpGobalSettings.setBounds(10, 10, 541, 180);
		
		canvas = new Canvas(grpGobalSettings, SWT.NONE);
		canvas.addPaintListener(new PaintListener() {
			public void paintControl(PaintEvent e) {
				BufferedImage bi = kr.getImage();
				canvas.setSize(bi.getWidth(),bi.getHeight());
				e.gc.drawImage(convertToSWT(kr.getImage()),0,0);
			}
		});
		canvas.setBounds(417, 47, 112, 79);
		
		// Tonic / Tonality
			
		Label lblTonic = new Label(grpGobalSettings, SWT.NONE);
		lblTonic.setFont(SWTResourceManager.getFont("Arial", 9, SWT.NORMAL));
		lblTonic.setBounds(10, 71, 55, 15);
		lblTonic.setText("Tonic");
		
		Combo cmbTonic = new Combo(grpGobalSettings, SWT.NONE);
		cmbTonic.setFont(SWTResourceManager.getFont("Arial", 9, SWT.NORMAL));
		for (NoteEnum ne : NoteEnum.values())
		{
			String label = ne.getName("#"); 
			cmbTonic.add(label);
			cmbTonic.setData(label, ne);		
		}		
		
		cmbTonic.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Combo c = (Combo) e.getSource();
				int index = c.getSelectionIndex();
				String key = c.getItem(index);
				NoteEnum ne = (NoteEnum) c.getData(key);
				glbTonicOrdinal = ne.ordinal();
				for (ScaleRenderer sc : sr) {
					sc.setTonic(ne);	
				}																				
				try {
					kr.render();
				}
				catch(RenderException re) {					
				}				
				canvas.redraw();
			}
		});
		cmbTonic.setBounds(95, 68, 238, 23);
		cmbTonic.select(glbTonicOrdinal);
		
		// Scale
	
		Label lblScale = new Label(grpGobalSettings, SWT.NONE);
		lblScale.setFont(SWTResourceManager.getFont("Arial", 9, SWT.NORMAL));
		lblScale.setBounds(10, 107, 70, 15);
		lblScale.setText("Scale");
		
		Combo cmbScale = new Combo(grpGobalSettings, SWT.NONE);		
		cmbScale.setFont(SWTResourceManager.getFont("Arial", 9, SWT.NORMAL));
		for (ScaleEnum se : ScaleEnum.values())
		{
			String label = se.getLabel(); 
			cmbScale.add(label);
			cmbScale.setData(label, se);		
		}		
		cmbScale.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Combo c = (Combo) e.getSource();
				int index = c.getSelectionIndex();
				String key = c.getItem(index);
				ScaleEnum se = (ScaleEnum) c.getData(key);
				glbScaleOrdinal = se.ordinal();
				Scale s = new Scale(se);								                                 
				s.setInverse(glbInverseScale);						
				
				for (ScaleRenderer sc : sr) {	
					
					sc.setScale(s);										
				}																
				try {
					kr.render();
				}
				catch(RenderException re) {					
				}						
				canvas.redraw();
			}
		});
		cmbScale.setBounds(95, 103, 238, 23);	
		cmbScale.select(glbScaleOrdinal);					
		
		txtOutputFolder = new Text(grpGobalSettings, SWT.BORDER);
		txtOutputFolder.setFont(SWTResourceManager.getFont("Arial", 9, SWT.NORMAL));
		txtOutputFolder.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				Preferences prefs = Preferences.userRoot().node(nodePath);
		    	prefs.put("default_path", ((Text)e.getSource()).getText());
			}
		});		
		txtOutputFolder.setBounds(131, 141, 398, 21);		
		String defaultPath = System.getProperty("user.home");								
		defaultPath = prefs.get("default_path",defaultPath);										
		txtOutputFolder.setText(defaultPath);
		
		Button btnOutputFolder = new Button(grpGobalSettings, SWT.NONE);
		btnOutputFolder.setFont(SWTResourceManager.getFont("Arial", 9, SWT.NORMAL));
		btnOutputFolder.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				
				DirectoryDialog dd = new DirectoryDialog(shlScalefreqGeneratorGui);
				dd.setMessage("Please select a directory and click OK");			        
			    String dir = dd.open();
			    if (dir != null) {			        
			    	txtOutputFolder.setText(dir);			    
			    }												
			}
		});
		btnOutputFolder.setBounds(93, 141, 35, 20);
		btnOutputFolder.setText("...");
		
		Label lblOuputFolder = new Label(grpGobalSettings, SWT.NONE);
		lblOuputFolder.setFont(SWTResourceManager.getFont("Arial", 9, SWT.NORMAL));
		lblOuputFolder.setBounds(10, 144, 77, 15);
		lblOuputFolder.setText("Output Folder");							
		
		Spinner sprTuning = new Spinner(grpGobalSettings, SWT.BORDER);
		sprTuning.setFont(SWTResourceManager.getFont("Arial", 9, SWT.NORMAL));
		sprTuning.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Spinner s = (Spinner) e.getSource();
				int value = s.getSelection();
				glbTuning = value;
				for (ScaleRenderer sc : sr)
				{
					sc.setTuning(value);						
				}						
			}			
		});
		sprTuning.setPageIncrement(1);
		sprTuning.setMaximum(500);
		sprTuning.setMinimum(400);
		sprTuning.setSelection(glbTuning);
		sprTuning.setBounds(96, 34, 55, 22);
		
		Label lblTuning = new Label(grpGobalSettings, SWT.NONE);
		lblTuning.setFont(SWTResourceManager.getFont("Arial", 9, SWT.NORMAL));
		lblTuning.setBounds(10, 37, 85, 15);
		lblTuning.setText("A1 Tuning (Hz)");		
		
		Button btnInverseScale = new Button(grpGobalSettings, SWT.CHECK);
		btnInverseScale.setFont(SWTResourceManager.getFont("Arial", 9, SWT.NORMAL));
		btnInverseScale.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Button b = (Button) e.getSource();				
				boolean old = glbInverseScale;
				glbInverseScale = b.getSelection();												
				if (old != glbInverseScale)
				{					
					for (ScaleRenderer sc : sr) {					
						sc.getScale().setInverse(glbInverseScale);										
					}																
					try {
						kr.render();
					}
					catch(RenderException re) {					
					}						
					canvas.redraw();
				}
			}
		});
		btnInverseScale.setBounds(344, 106, 67, 16);
		btnInverseScale.setText("Inverse");
		btnInverseScale.setSelection(glbInverseScale);
	}
	
	private void createGlissEQFiltersTab(Composite cmpsGlissEQFilter)
	{
		Label lblStart = new Label(cmpsGlissEQFilter, SWT.NONE);
		lblStart.setFont(SWTResourceManager.getFont("Arial", 9, SWT.NORMAL));
		lblStart.setText("Start");
		lblStart.setBounds(115, 46, 33, 15);
		
		Spinner sprFreqRangeStart = new Spinner(cmpsGlissEQFilter, SWT.BORDER);		
		sprFreqRangeStart.setFont(SWTResourceManager.getFont("Arial", 9, SWT.NORMAL));
		sprFreqRangeStart.setIncrement(10);
		sprFreqRangeStart.setPageIncrement(100);
		sprFreqRangeStart.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Spinner s = (Spinner) e.getSource();				
				gefr.setFreq_range_start(s.getSelection());							
			}
		});
		sprFreqRangeStart.setMaximum(GlissEQFiltersRenderer.FREQ_RANGE_MAX);
		sprFreqRangeStart.setMinimum(GlissEQFiltersRenderer.FREQ_RANGE_MIN);
		sprFreqRangeStart.setSelection(gefr.getFreq_range_start());
		sprFreqRangeStart.setBounds(150, 42, 64, 22);				
		
		Label lblHz = new Label(cmpsGlissEQFilter, SWT.NONE);
		lblHz.setText("Hz");
		lblHz.setBounds(218, 45, 22, 15);
		
		Label lblEnd = new Label(cmpsGlissEQFilter, SWT.NONE);
		lblEnd.setFont(SWTResourceManager.getFont("Arial", 9, SWT.NORMAL));
		lblEnd.setText("End");
		lblEnd.setBounds(247, 45, 22, 15);
		
		Spinner sprFreqRangeEnd = new Spinner(cmpsGlissEQFilter, SWT.BORDER);
		sprFreqRangeEnd.setFont(SWTResourceManager.getFont("Arial", 9, SWT.NORMAL));
		sprFreqRangeEnd.setIncrement(10);
		sprFreqRangeEnd.setPageIncrement(100);
		sprFreqRangeEnd.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Spinner s = (Spinner) e.getSource();				
				gefr.setFreq_range_end(s.getSelection());															
			}
		});
		sprFreqRangeEnd.setMaximum(GlissEQFiltersRenderer.FREQ_RANGE_MAX);
		sprFreqRangeEnd.setMinimum(GlissEQFiltersRenderer.FREQ_RANGE_MIN);
		sprFreqRangeEnd.setSelection(gefr.getFreq_range_end());
		sprFreqRangeEnd.setBounds(274, 42, 64, 22);
		
		Label lblHz1 = new Label(cmpsGlissEQFilter, SWT.NONE);
		lblHz1.setFont(SWTResourceManager.getFont("Arial", 9, SWT.NORMAL));
		lblHz1.setText("Hz");
		lblHz1.setBounds(342, 45, 33, 15);
		
		Label lblGain = new Label(cmpsGlissEQFilter, SWT.NONE);
		lblGain.setFont(SWTResourceManager.getFont("Arial", 9, SWT.NORMAL));
		lblGain.setText("Gain");
		lblGain.setBounds(115, 85, 27, 15);		
		Spinner sprGain = new Spinner(cmpsGlissEQFilter, SWT.BORDER);
		sprGain.setFont(SWTResourceManager.getFont("Arial", 9, SWT.NORMAL));
		sprGain.setIncrement(10);
		sprGain.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Spinner s = (Spinner) e.getSource();
				gefr.setGain((double)(s.getSelection()/100d));								
			}
		});
		sprGain.setPageIncrement(100);
		sprGain.setMaximum((int) Math.round(GlissEQFiltersRenderer.GAIN_MAX*100d));
		sprGain.setMinimum((int) Math.round(GlissEQFiltersRenderer.GAIN_MIN*100d));
		sprGain.setSelection((int) Math.round(gefr.getGain()*100d));
		sprGain.setDigits(2);
		sprGain.setBounds(149, 83, 65, 22);
		
		Label lbldB = new Label(cmpsGlissEQFilter, SWT.NONE);
		lbldB.setFont(SWTResourceManager.getFont("Arial", 9, SWT.NORMAL));
		lbldB.setText("dB");
		lbldB.setBounds(218, 86, 27, 15);
		
		Label lblQ = new Label(cmpsGlissEQFilter, SWT.NONE);
		lblQ.setFont(SWTResourceManager.getFont("Arial", 9, SWT.NORMAL));
		lblQ.setText("Q");
		lblQ.setBounds(257, 87, 12, 15);
		
		Spinner sprQ = new Spinner(cmpsGlissEQFilter, SWT.BORDER);
		sprQ.setFont(SWTResourceManager.getFont("Arial", 9, SWT.NORMAL));
		sprQ.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				int sel = ((Spinner)e.getSource()).getSelection();						
				gefr.setQ((double)(sel/100d));
			}
		});
		sprQ.setMaximum((int) Math.round(GlissEQFiltersRenderer.Q_MAX*100d));
		sprQ.setMinimum((int) Math.round(GlissEQFiltersRenderer.Q_MIN*100d));
		sprQ.setSelection((int) Math.round(gefr.getQ()*100d));
		sprQ.setDigits(2);
		sprQ.setBounds(274, 83, 57, 22);
		
		Label lblDyn = new Label(cmpsGlissEQFilter, SWT.NONE);
		lblDyn.setFont(SWTResourceManager.getFont("Arial", 9, SWT.NORMAL));
		lblDyn.setText("Dyn");
		lblDyn.setBounds(365, 86, 21, 15);
		
		Spinner sprDyn = new Spinner(cmpsGlissEQFilter, SWT.BORDER);
		sprDyn.setFont(SWTResourceManager.getFont("Arial", 9, SWT.NORMAL));
		sprDyn.setPageIncrement(1000);
		sprDyn.setIncrement(100);
		sprDyn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				int sel = ((Spinner)e.getSource()).getSelection();						
				gefr.setDyn((double)(sel/100d));
			}
		});		
		sprDyn.setMaximum((int) Math.round(GlissEQFiltersRenderer.DYN_MAX*100d));
		sprDyn.setMinimum((int) Math.round(GlissEQFiltersRenderer.DYN_MIN*100d));
		sprDyn.setSelection((int) Math.round(gefr.getDyn()*100d));
		sprDyn.setDigits(2);
		sprDyn.setBounds(391, 83, 64, 22);
		
		Label lblFrequencyRange = new Label(cmpsGlissEQFilter, SWT.NONE);
		lblFrequencyRange.setFont(SWTResourceManager.getFont("Arial", 9, SWT.NORMAL));
		lblFrequencyRange.setBounds(10, 46, 76, 15);
		lblFrequencyRange.setText("Range:");
		
		Label lblFilterSettings = new Label(cmpsGlissEQFilter, SWT.NONE);
		lblFilterSettings.setFont(SWTResourceManager.getFont("Arial", 9, SWT.NORMAL));
		lblFilterSettings.setBounds(10, 85, 82, 15);
		lblFilterSettings.setText("Filter Settings:");
		
		Button btnInclude = new Button(cmpsGlissEQFilter, SWT.CHECK);
		btnInclude.setFont(SWTResourceManager.getFont("Arial", 9, SWT.NORMAL));
		btnInclude.addSelectionListener(new SelectionAdapter() {			
			@Override
			public void widgetSelected(SelectionEvent e) {
				Button b = (Button) e.getSource();
				gefr.setEnabled(b.getSelection());								
			}
		});
		btnInclude.setBounds(10, 10, 93, 16);
		btnInclude.setSelection(gefr.isEnabled());
		btnInclude.setText("Include");
		
		Combo cmbFilterType = new Combo(cmpsGlissEQFilter, SWT.NONE);
		cmbFilterType.setFont(SWTResourceManager.getFont("Arial", 9, SWT.NORMAL));
		cmbFilterType.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Combo c = (Combo) e.getSource();
				int index = c.getSelectionIndex();
				String key = c.getItem(index);
				GlissEQFilterTypeEnum fte = (GlissEQFilterTypeEnum) c.getData(key);
				gefr.setType(fte);				
			}
		});
		cmbFilterType.setBounds(148, 127, 131, 23);
		for (GlissEQFilterTypeEnum fte : GlissEQFilterTypeEnum.values())
		{
			String label = fte.toString(); 
			cmbFilterType.add(label);
			cmbFilterType.setData(label, fte);		
		}	
		cmbFilterType.select(gefr.getType().ordinal());
		
		Label lblFilterType = new Label(cmpsGlissEQFilter, SWT.NONE);
		lblFilterType.setFont(SWTResourceManager.getFont("Arial", 9, SWT.NORMAL));
		lblFilterType.setBounds(114, 130, 27, 15);
		lblFilterType.setText("Type");				
	}
	
	private void createGlissEQAreasTab(Composite cmpsGlissEQAreas)
	{
		Button btnInclude_1 = new Button(cmpsGlissEQAreas, SWT.CHECK);
		btnInclude_1.setFont(SWTResourceManager.getFont("Arial", 9, SWT.NORMAL));
		btnInclude_1.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Button b = (Button) e.getSource();
				gear.setEnabled(b.getSelection());		
			}
		});
		btnInclude_1.setBounds(10, 10, 93, 16);
		btnInclude_1.setText("Include");
		btnInclude_1.setSelection(gear.isEnabled());
		
		Spinner sprNoteWidth = new Spinner(cmpsGlissEQAreas, SWT.BORDER);
		sprNoteWidth.setFont(SWTResourceManager.getFont("Arial", 9, SWT.NORMAL));
		sprNoteWidth.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				int sel = ((Spinner)e.getSource()).getSelection();						
				gear.setNoteWidth(sel);
			}
		});
		sprNoteWidth.setMinimum(1);
		sprNoteWidth.setMaximum(100);
		sprNoteWidth.setSelection(gear.getNoteWidth());
		sprNoteWidth.setToolTipText("note width in cents");
		sprNoteWidth.setBounds(116, 89, 47, 22);
		
		Label lblNoteWidth = new Label(cmpsGlissEQAreas, SWT.NONE);
		lblNoteWidth.setFont(SWTResourceManager.getFont("Arial", 9, SWT.NORMAL));
		lblNoteWidth.setBounds(10, 92, 63, 15);
		lblNoteWidth.setText("Note width:");
		
		Label lblColor = new Label(cmpsGlissEQAreas, SWT.NONE);
		lblColor.setFont(SWTResourceManager.getFont("Arial", 9, SWT.NORMAL));
		lblColor.setBounds(223, 92, 34, 15);
		lblColor.setText("Color:");
		
		Label lblCent = new Label(cmpsGlissEQAreas, SWT.NONE);
		lblCent.setFont(SWTResourceManager.getFont("Arial", 9, SWT.NORMAL));
		lblCent.setBounds(169, 92, 31, 15);
		lblCent.setText("cent");
		
		Label lblVertPos = new Label(cmpsGlissEQAreas, SWT.NONE);
		lblVertPos.setFont(SWTResourceManager.getFont("Arial", 9, SWT.NORMAL));
		lblVertPos.setBounds(206, 137, 93, 15);
		lblVertPos.setText("Vertical position:");
		
		Spinner sprVertPos = new Spinner(cmpsGlissEQAreas, SWT.BORDER);
		sprVertPos.setFont(SWTResourceManager.getFont("Arial", 9, SWT.NORMAL));
		sprVertPos.setPageIncrement(2);
		sprVertPos.setMaximum(11);
		sprVertPos.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				int sel = ((Spinner)e.getSource()).getSelection();						
				gear.setVerticalPos(sel);
			}
		});
		sprVertPos.setBounds(305, 133, 47, 22);
		sprVertPos.setSelection(gear.getVerticalpos());
		
		Label lblHeight = new Label(cmpsGlissEQAreas, SWT.NONE);
		lblHeight.setFont(SWTResourceManager.getFont("Arial", 9, SWT.NORMAL));
		lblHeight.setBounds(10, 137, 47, 15);
		lblHeight.setText("Height:");
		
		Spinner sprHeight = new Spinner(cmpsGlissEQAreas, SWT.BORDER);
		sprHeight.setFont(SWTResourceManager.getFont("Arial", 9, SWT.NORMAL));
		sprHeight.setPageIncrement(2);
		sprHeight.setMaximum(10);
		sprHeight.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				int sel = ((Spinner)e.getSource()).getSelection();						
				gear.setHeight(sel);
			}
		});
		sprHeight.setBounds(116, 133, 47, 22);
		sprHeight.setSelection(gear.getHeight());		
		
		Button btnAlternateColors = new Button(cmpsGlissEQAreas, SWT.CHECK);
		btnAlternateColors.setFont(SWTResourceManager.getFont("Arial", 9, SWT.NORMAL));
		btnAlternateColors.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Button b = (Button) e.getSource();
				gear.setAlternateColors(b.getSelection());		
			}
		});
		btnAlternateColors.setBounds(404, 91, 112, 16);
		btnAlternateColors.setText("Alternate Colors");
		btnAlternateColors.setSelection(gear.isAlternateColors());
		
		Combo cmbColor = new Combo(cmpsGlissEQAreas, SWT.NONE);
		cmbColor.setFont(SWTResourceManager.getFont("Arial", 9, SWT.NORMAL));
		cmbColor.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Combo c = (Combo) e.getSource();
				int index = c.getSelectionIndex();
				String key = c.getItem(index);
				GlissEQColorEnum gce = (GlissEQColorEnum) c.getData(key);
				int colorIndex = gce.getColorIndex();
				gear.setColorIndex(colorIndex);												
				c.setBackground(
						new Color(Display.getCurrent(), 
								gce.getR(), gce.getG(), gce.getB()));				
			}
		});
		for (GlissEQColorEnum gce : GlissEQColorEnum.values())
		{
			String label = gce.toString(); 
			cmbColor.add(label);
			cmbColor.setData(label, gce);				
		}			
		cmbColor.select(gear.getColorIndex());		
		GlissEQColorEnum gce = GlissEQColorEnum.values()[gear.getColorIndex()];
		cmbColor.setBackground(
				new Color(
						Display.getCurrent(), 
							gce.getR(),
							gce.getG(),
							gce.getB()));
		cmbColor.setBounds(263, 89, 120, 23);
		
		Label lblFrequencyRange1 = new Label(cmpsGlissEQAreas, SWT.NONE);
		lblFrequencyRange1.setFont(SWTResourceManager.getFont("Arial", 9, SWT.NORMAL));
		lblFrequencyRange1.setText("Range:");
		lblFrequencyRange1.setBounds(10, 48, 58, 15);
		
		Label lblStart1 = new Label(cmpsGlissEQAreas, SWT.NONE);
		lblStart1.setFont(SWTResourceManager.getFont("Arial", 9, SWT.NORMAL));
		lblStart1.setText("Start");
		lblStart1.setBounds(118, 48, 27, 15);
		
		Spinner sprFreqRangeStart1 = new Spinner(cmpsGlissEQAreas, SWT.BORDER);
		sprFreqRangeStart1.setFont(SWTResourceManager.getFont("Arial", 9, SWT.NORMAL));
		sprFreqRangeStart1.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Spinner s = (Spinner) e.getSource();				
				gear.setFreq_range_start(s.getSelection());	
			}
		});
		sprFreqRangeStart1.setPageIncrement(100);
		sprFreqRangeStart1.setIncrement(10);
		sprFreqRangeStart1.setMaximum(GlissEQAreasRenderer.FREQ_RANGE_MAX);
		sprFreqRangeStart1.setMinimum(GlissEQAreasRenderer.FREQ_RANGE_MIN);
		sprFreqRangeStart1.setSelection(gear.getFreq_range_start());		
		sprFreqRangeStart1.setBounds(152, 45, 64, 22);
		
		Label lblHz2 = new Label(cmpsGlissEQAreas, SWT.NONE);
		lblHz2.setFont(SWTResourceManager.getFont("Arial", 9, SWT.NORMAL));
		lblHz2.setText("Hz");
		lblHz2.setBounds(220, 48, 27, 15);
		
		Label lblEnd1 = new Label(cmpsGlissEQAreas, SWT.NONE);
		lblEnd1.setFont(SWTResourceManager.getFont("Arial", 9, SWT.NORMAL));
		lblEnd1.setText("End");
		lblEnd1.setBounds(273, 47, 22, 15);
		
		Spinner sprFreqRangeEnd1 = new Spinner(cmpsGlissEQAreas, SWT.BORDER);
		sprFreqRangeEnd1.setFont(SWTResourceManager.getFont("Arial", 9, SWT.NORMAL));
		sprFreqRangeEnd1.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Spinner s = (Spinner) e.getSource();				
				gear.setFreq_range_end(s.getSelection());	
			}
		});
		sprFreqRangeEnd1.setPageIncrement(100);
		sprFreqRangeEnd1.setIncrement(10);
		sprFreqRangeEnd1.setMaximum(GlissEQAreasRenderer.FREQ_RANGE_MAX);
		sprFreqRangeEnd1.setMinimum(GlissEQAreasRenderer.FREQ_RANGE_MIN);
		sprFreqRangeEnd1.setSelection(gear.getFreq_range_end());		
		sprFreqRangeEnd1.setBounds(299, 45, 64, 22);
		
		Label lblHz3 = new Label(cmpsGlissEQAreas, SWT.NONE);
		lblHz3.setFont(SWTResourceManager.getFont("Arial", 9, SWT.NORMAL));
		lblHz3.setText("Hz");
		lblHz3.setBounds(369, 47, 33, 15);
	}	
	
	private void createHTMLTab(Composite cmpsHTML)
	{	
	}
	
	private void renderGlissEQFilters(File folder) throws Exception
	{
		FileOutputStream fos = null;		
		fos = new FileOutputStream(
				folder+File.separator
					+gefr.getTonic().getName()+"_"
					+gefr.getScale().getLabel("_")
					+"_GEQ_Filters.csv");
						
		gefr.setRenderOutput(fos);		
		gefr.render();														
		fos.close();		
	}				
	
	private void renderGlissEQAreas(File folder) throws Exception
	{
		FileOutputStream fos = null;		
		fos = new FileOutputStream(
				folder+File.separator
					+gear.getTonic().getName()+"_"
					+gear.getScale().getLabel("_")
					+"_GEQ_Areas.csv");
						
		gear.setRenderOutput(fos);		
		gear.render();														
		fos.close();		
	}
	
	private void renderHTML(File folder) throws Exception
	{
		FileOutputStream fos = null;
		String fileName = null;
		if (htmr.getAllScalesMode()) {
			fileName = "All_Scales";
			if (htmr.getChromaticMode()) {
				fileName += "_Chromatic";
			}
			if (htmr.getScale().isInverse()) {
				fileName += "_Inv";				
			}
		}
		else {
			if (htmr.getChromaticMode()) {
				fileName = htmr.getScale().getLabel("_")+"_Chromatic";
			}
			else {
				fileName = htmr.getTonic().getName()+"_"
							+htmr.getScale().getLabel("_");	
			}
		}
		
		fos = new FileOutputStream(
				folder+File.separator	
					+fileName
					+".html");
						
		htmr.setRenderOutput(fos);		
		htmr.render();														
		fos.close();		
	}
		
	/*******************************************************************************
	 * Copyright (c) 2004, 2009 IBM Corporation and others.
	 * All rights reserved. This program and the accompanying materials
	 * are made available under the terms of the Eclipse Public License v1.0
	 * which accompanies this distribution, and is available at
	 * http://www.eclipse.org/legal/epl-v10.html
	 *
	 * Contributors:
	 *    IBM Corporation - initial API and implementation 
	 ****************************************************************************/
	public static Image convertToSWT(BufferedImage bufferedImage) {
		
		if (bufferedImage.getColorModel() instanceof DirectColorModel) {
			DirectColorModel colorModel = (DirectColorModel)bufferedImage.getColorModel();
			PaletteData palette = new PaletteData(colorModel.getRedMask(), colorModel.getGreenMask(), colorModel.getBlueMask());
			ImageData data = new ImageData(bufferedImage.getWidth(), bufferedImage.getHeight(), colorModel.getPixelSize(), palette);
			for (int y = 0; y < data.height; y++) {
				for (int x = 0; x < data.width; x++) {
					int rgb = bufferedImage.getRGB(x, y);
					int pixel = palette.getPixel(new RGB((rgb >> 16) & 0xFF, (rgb >> 8) & 0xFF, rgb & 0xFF)); 
					data.setPixel(x, y, pixel);
					if (colorModel.hasAlpha()) {
						data.setAlpha(x, y, (rgb >> 24) & 0xFF);												
					}
				}
			}			
			return new Image(Display.getCurrent(), data);		
		} else if (bufferedImage.getColorModel() instanceof IndexColorModel) {
			IndexColorModel colorModel = (IndexColorModel)bufferedImage.getColorModel();
			int size = colorModel.getMapSize();
			byte[] reds = new byte[size];
			byte[] greens = new byte[size];
			byte[] blues = new byte[size];
			colorModel.getReds(reds);
			colorModel.getGreens(greens);
			colorModel.getBlues(blues);
			RGB[] rgbs = new RGB[size];
			for (int i = 0; i < rgbs.length; i++) {
				rgbs[i] = new RGB(reds[i] & 0xFF, greens[i] & 0xFF, blues[i] & 0xFF);
			}
			PaletteData palette = new PaletteData(rgbs);
			ImageData data = new ImageData(bufferedImage.getWidth(), bufferedImage.getHeight(), colorModel.getPixelSize(), palette);
			data.transparentPixel = colorModel.getTransparentPixel();
			WritableRaster raster = bufferedImage.getRaster();
			int[] pixelArray = new int[1];
			for (int y = 0; y < data.height; y++) {
				for (int x = 0; x < data.width; x++) {
					raster.getPixel(x, y, pixelArray);
					data.setPixel(x, y, pixelArray[0]);
				}
			}
			return new Image(Display.getCurrent(), data);
		}
		return null;
	}		
}

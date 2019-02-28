/*******************************************************************************
 * Copyright (c) 2008, 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Ferenc Hechler - initial API and implementation
 *     Ferenc Hechler, ferenc_hechler@users.sourceforge.net - 219530 [jar application] add Jar-in-Jar ClassLoader option
 *     Ferenc Hechler, ferenc_hechler@users.sourceforge.net - 262746 [jar exporter] Create a builder for jar-in-jar-loader.zip
 *     Ferenc Hechler, ferenc_hechler@users.sourceforge.net - 262748 [jar exporter] extract constants for string literals in JarRsrcLoader et al.
 *******************************************************************************/

package de.airmann.scalefreq.frontend.swt;

import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import org.eclipse.jdt.internal.jarinjarloader.*;

public class SWTClassLoader extends JarRsrcLoader {
		
	final class JIJConstants {	
		static final String REDIRECTED_CLASS_PATH_MANIFEST_NAME  = "Rsrc-Class-Path";  //$NON-NLS-1$
	    static final String REDIRECTED_MAIN_CLASS_MANIFEST_NAME  = "Rsrc-Main-Class";  //$NON-NLS-1$
	    static final String DEFAULT_REDIRECTED_CLASSPATH         = "";  //$NON-NLS-1$
	    static final String MAIN_METHOD_NAME                     = "main";  //$NON-NLS-1$
	    static final String JAR_INTERNAL_URL_PROTOCOL_WITH_COLON = "jar:rsrc:";  //$NON-NLS-1$
	    static final String JAR_INTERNAL_SEPARATOR               = "!/";  //$NON-NLS-1$
	    static final String INTERNAL_URL_PROTOCOL_WITH_COLON     = "rsrc:";  //$NON-NLS-1$
	    static final String INTERNAL_URL_PROTOCOL                = "rsrc";  //$NON-NLS-1$
	    static final String PATH_SEPARATOR                       = "/";  //$NON-NLS-1$
	    static final String CURRENT_DIR                          = "./";  //$NON-NLS-1$
	    static final String UTF8_ENCODING                        = "UTF-8";  //$NON-NLS-1$
	}
		 
	private static class ManifestInfo {
		String rsrcMainClass;
		String[] rsrcClassPath;
	}
	
	public static void main(String[] args) throws ClassNotFoundException, 
		IllegalArgumentException, IllegalAccessException, InvocationTargetException, SecurityException, NoSuchMethodException, IOException, RuntimeException {
		
		ManifestInfo mi = getManifestInfo();
		
		ClassLoader cl = Thread.currentThread().getContextClassLoader();
		URL.setURLStreamHandlerFactory(new RsrcURLStreamHandlerFactory(cl));
		
		// OS DETECTION 
		// See http://stackoverflow.com/questions/2706222/create-cross-platform-java-swt-application
		String osName = System.getProperty("os.name").toLowerCase();		
	    String osArch = System.getProperty("os.arch").toLowerCase();	       
	    String swtFileNameOsPart = 
            osName.contains("win") ? "win32" :
            osName.contains("mac") ? "macosx" :
            osName.contains("linux") || osName.contains("nix") ? "linux_gtk" : "";            	
	    if (swtFileNameOsPart.equals(""))
	    {	    		    	
	    	throw new RuntimeException("Unsupported OS: "+osName+" - Only Win, MacOS, Linux are supported !");	    	    	
	    }
        String swtFileNameArchPart = osArch.contains("64") ? "x64" : "x86";
        String swtFileName = "swt_"+swtFileNameOsPart+"_"+swtFileNameArchPart+".jar";
        System.out.println("Detected OS/VM: "+osName+" ARCH: "+osArch+" Using: " +swtFileName);       								
		
        // find out how many swt wrappers are included in the main jar file
        int nrSWTWrappers = 0;
        for (int i = 0; i < mi.rsrcClassPath.length; i++) {
        	if (mi.rsrcClassPath[i].startsWith("swt_")) {
        		nrSWTWrappers++;
        	}
        }
        
		URL[] rsrcUrls = new URL[mi.rsrcClassPath.length-nrSWTWrappers+1]; // TODO !!!!
		int j = 0;
		for (int i = 0; i < mi.rsrcClassPath.length; i++) {
			String rsrcPath = mi.rsrcClassPath[i];
			
			// ignore swt wrapper that we don't need
			if (rsrcPath.startsWith("swt_") && (!rsrcPath.equals(swtFileName))) {
				continue;
			}
			
			if (rsrcPath.endsWith(JIJConstants.PATH_SEPARATOR)) 
				rsrcUrls[j] = new URL(JIJConstants.INTERNAL_URL_PROTOCOL_WITH_COLON + rsrcPath); 
			else
				rsrcUrls[j] = new URL(JIJConstants.JAR_INTERNAL_URL_PROTOCOL_WITH_COLON 
								+ rsrcPath + JIJConstants.JAR_INTERNAL_SEPARATOR);
			j++;
		}
							
		ClassLoader jceClassLoader = new URLClassLoader(rsrcUrls, null);
		Thread.currentThread().setContextClassLoader(jceClassLoader);
		Class c = Class.forName(mi.rsrcMainClass, true, jceClassLoader);
		Method main = c.getMethod(JIJConstants.MAIN_METHOD_NAME, new Class[]{args.getClass()}); 
		main.invoke((Object)null, new Object[]{args});
	}

	private static ManifestInfo getManifestInfo() throws IOException {
		Enumeration resEnum;
		resEnum = Thread.currentThread().getContextClassLoader().getResources(JarFile.MANIFEST_NAME); 
		while (resEnum.hasMoreElements()) {
			try {
				URL url = (URL)resEnum.nextElement();
				InputStream is = url.openStream();
				if (is != null) {
					ManifestInfo result = new ManifestInfo();
					Manifest manifest = new Manifest(is);
					Attributes mainAttribs = manifest.getMainAttributes();
					result.rsrcMainClass = mainAttribs.getValue(JIJConstants.REDIRECTED_MAIN_CLASS_MANIFEST_NAME); 
					String rsrcCP = mainAttribs.getValue(JIJConstants.REDIRECTED_CLASS_PATH_MANIFEST_NAME); 
					if (rsrcCP == null)
						rsrcCP = JIJConstants.DEFAULT_REDIRECTED_CLASSPATH; 
					result.rsrcClassPath = splitSpaces(rsrcCP);
					if ((result.rsrcMainClass != null) && !result.rsrcMainClass.trim().equals(""))    //$NON-NLS-1$
							return result;
				}
			}
			catch (Exception e) {
				// Silently ignore wrong manifests on classpath?
			}
		}
		System.err.println("Missing attributes for JarRsrcLoader in Manifest ("
				+JIJConstants.REDIRECTED_MAIN_CLASS_MANIFEST_NAME+", "
				+JIJConstants.REDIRECTED_CLASS_PATH_MANIFEST_NAME+")"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		return null;
	}

	/**
	 * JDK 1.3.1 does not support String.split(), so we have to do it manually. Skip all spaces
	 * (tabs are not handled)
	 * 
	 * @param line the line to split
	 * @return array of strings
	 */
	private static String[] splitSpaces(String line) {
		if (line == null) 
			return null;
		List result = new ArrayList();
		int firstPos = 0;
		while (firstPos < line.length()) {
			int lastPos = line.indexOf(' ', firstPos);
			if (lastPos == -1)
				lastPos = line.length();
			if (lastPos > firstPos) {
				result.add(line.substring(firstPos, lastPos));
			}
			firstPos = lastPos+1; 
		}
		return (String[]) result.toArray(new String[result.size()]);
	}
	
}

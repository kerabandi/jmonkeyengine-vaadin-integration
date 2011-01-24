package org.wholebrainprojecttutorials.jmevaadinapplication;


import java.util.concurrent.ConcurrentLinkedQueue;

import org.wholebrainprojecttutorials.jmevaadinapplication.ui.MainLayout;

import com.vaadin.ui.Window;

/**
 * Main class of project. 
 * Responsible for adding 2D Vaadin Components and 3D Components into a single 
 * application. 
 * 
 * @author Jesus Martinez(jrmartin@ncmir.ucsd.edu)
 *
 */
public class ProjectMainClass extends com.vaadin.Application{
	
	private Window mainWindow = null;
	private MainLayout mainLayout;	
	
	/**
	 * Method automatically called once you declared class type 
	 */
	public void init() {		
		setTheme("data");
		mainWindow = new Window("WBC");
		mainWindow.setImmediate(true);
		
		setMainWindow(mainWindow);
		
		mainLayout = new MainLayout();
		
		getMainWindow().setContent(mainLayout);
	}
}

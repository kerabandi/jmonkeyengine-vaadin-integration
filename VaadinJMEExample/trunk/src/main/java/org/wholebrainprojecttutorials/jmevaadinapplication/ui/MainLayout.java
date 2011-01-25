package org.wholebrainprojecttutorials.jmevaadinapplication.ui;

import org.vaadin.artur.icepush.ICEPush;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.terminal.Resource;
import com.vaadin.terminal.Sizeable;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.SplitPanel;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.TabSheet.SelectedTabChangeEvent;

public class MainLayout extends VerticalLayout implements 
						Property.ValueChangeListener{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;	
	
	private SplitPanel mainViewSplitPanel = new SplitPanel(
            SplitPanel.ORIENTATION_HORIZONTAL);

	private JMEApplet jmeApplet;
		    
	private ICEPush pusher = new ICEPush();
		
	private static final String[] shapes = new String[] { "Box",
		"Sphere", "Cone"};

	String colorChange = "Change shape color";
	
	VerticalLayout headBar;
	
	public MainLayout(){
		this.setStyleName("view2d");
		buildMainLayout();		
	}
	
	
	/**
	 * Main Window Layout consists of two components.
	 * First components holds MenuBar plus HeadBar.
	 * Second Component holds SplitPanel with left side bar, and application's
	 * main view.
	 */
	public void buildMainLayout(){
		
		this.setSizeFull();
							
		//Create Layout for top part i.e. HeadBar
		headBar = new HeadBar();
		
		headBar.setVisible(true);
		
		headBar.setSizeFull();
		
		//add headbar to main layout
		this.addComponent(headBar);
				
		//add SplitPanel to main Layout
		this.addComponent(mainViewSplitPanel);
		
		this.setExpandRatio(headBar,1);
		//Gives all extra space to main view split panel
		this.setExpandRatio(mainViewSplitPanel, 5);
				
		mainViewSplitPanel.setFirstComponent(createLeftSideBar());	
		mainViewSplitPanel.setSecondComponent(create3DView());
		
		//Give left side a minimun of 200 pixels
		mainViewSplitPanel.setSplitPosition(300, Sizeable.UNITS_PIXELS);		
				
        this.addComponent(pusher);
	}
	
	/**
	 * Initialize layout to represent the left side bar of application.
	 * Layout will be a combination of two splitpanels.
	 * @return
	 */
	public VerticalLayout createLeftSideBar(){
		
		VerticalLayout leftBarLayout = new VerticalLayout();
				
		leftBarLayout.setWidth("100%");
		leftBarLayout.setHeight("100%");
		leftBarLayout.setMargin(true);
		leftBarLayout.setSpacing(false);
		leftBarLayout.setImmediate(true);
		
		leftBarLayout.setSizeFull();
				
		// Create & set input prompt
        ComboBox l = new ComboBox("Switch Box");

        // configure & load content
        l.setImmediate(true);
        l.addListener(this);
        for (int i = 0; i < shapes.length; i++) {
        	System.out.println("shape " + shapes[i]);
            l.addItem(shapes[i]);
        }
        
        leftBarLayout.addComponent(l);
        
        Button randomColor = new Button(colorChange);
        randomColor.addListener(new Button.ClickListener(){

			public void buttonClick(ClickEvent event) {
				jmeApplet.fireEvent("changeColor", new Object[]{""});
				ajaxPush();
			}
        	
        });
        
        leftBarLayout.addComponent(randomColor);
        
        return leftBarLayout;
	}
	public VerticalLayout create3DView() {	

		jmeApplet = new JMEApplet();
						
		// Tab 1 content
        final VerticalLayout l1 = new VerticalLayout();
        l1.setMargin(true);
        
	    String APPLET_CLASS = "org.jmeappletintegration.example.SimpleJMEApplet";
	    
	    String[] APPLET_ARCHIVES = new String[] {
	            "jmeapplet-1.0.jar",
	            "commons-httpclient-3.1.jar", "commons-codec-1.3.jar",
	            "commons-logging-1.1.1.jar" };
	    
	    jmeApplet.setAppletArchives(APPLET_ARCHIVES);
	    jmeApplet.setAppletClass(APPLET_CLASS);
	    jmeApplet.setAppletWidth("100%");
	    jmeApplet.setAppletHeight("100%");
	    jmeApplet.setSizeFull();
		
	    l1.addComponent(jmeApplet);      
	    l1.setWidth("100%");
	    l1.setHeight("100%");
	    l1.setSizeFull();
        l1.setExpandRatio(jmeApplet, 1);
                
        return l1;
	}
	
	public void ajaxPush(){
		pusher.push();
	}


	public void valueChange(ValueChangeEvent event) {
		
		String command = event.getProperty().toString();
		Object[] parameters = new Object[]{""};
		
		parameters[0] = command;
				
		jmeApplet.fireEvent("changeShape", parameters);

		this.ajaxPush();
	}
}

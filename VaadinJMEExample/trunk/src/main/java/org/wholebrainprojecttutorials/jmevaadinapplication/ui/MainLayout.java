package org.wholebrainprojecttutorials.jmevaadinapplication.ui;

import org.vaadin.artur.icepush.ICEPush;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.terminal.Sizeable;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.SplitPanel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;

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

	String textureChange = "Change geometry texture";
	
	VerticalLayout headBar;

	private Panel panel;
	
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
		
		SplitPanel splitPanel = new SplitPanel(
	            SplitPanel.ORIENTATION_VERTICAL);
		
		VerticalLayout firstPanel = new VerticalLayout();
		firstPanel.setHeight("100%");
		
		firstPanel.setSizeFull();
		
		firstPanel.addComponent(new Label(
                "Drag obj file from desktop  "
                        + "file system to the drop box below (dragging files requires HTML5 capable browser like FF 3.6, Safari or Chrome)"));
        
        CssLayout css = new CssLayout();
        css.setWidth("300px");
        css.setHeight("300px");
        
        DropBox box = new DropBox(css,this);
        box.setSizeUndefined();
        
        panel = new Panel(box);
        panel.setSizeUndefined();
        panel.setWidth("100%");
        firstPanel.addComponent(panel);
        
        splitPanel.setFirstComponent(firstPanel);
                
        VerticalLayout secondPanel = new VerticalLayout();
        secondPanel.setSizeFull();
        
        Label secondPanelTitle = new Label("Switch geometry and geometry's texture" 
        		+ " in applet using option below. Note: The 'Change geometry texture' feature " +
        			" will not apply texture to user uploaded models");
        
        secondPanel.addComponent(secondPanelTitle);
        
     // Create & set input prompt
        ComboBox l = new ComboBox("Switch Box");

        // configure & load content
        l.setImmediate(true);
        l.addListener(this);
        for (int i = 0; i < shapes.length; i++) {
        	System.out.println("shape " + shapes[i]);
            l.addItem(shapes[i]);
        }
        
        secondPanel.addComponent(l);
        
        Button randomColor = new Button(textureChange);
        randomColor.addListener(new Button.ClickListener(){

			public void buttonClick(ClickEvent event) {
				jmeApplet.fireEvent("changeTexture", new Object[]{""});
				ajaxPush();
			}
        	
        });
        
        secondPanel.addComponent(randomColor);
        
        splitPanel.setSecondComponent(secondPanel);
        
        leftBarLayout.addComponent(splitPanel);
        
        return leftBarLayout;
	}

	public VerticalLayout create3DView() {	

		jmeApplet = new JMEApplet();
						
		// Tab 1 content
        final VerticalLayout l1 = new VerticalLayout();
        l1.setMargin(true);
        
	    String APPLET_CLASS = "org.jmeappletintegration.example.SimpleJMEApplet";
	    
	    String[] APPLET_ARCHIVES = new String[] {
	            "jmeapplet-1.0.1.jar"};
	    
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
	
	public JMEApplet getApplet(){
		return this.jmeApplet;
	}
}

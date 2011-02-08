package org.wholebrainprojecttutorials.jmevaadinapplication.ui;

import com.vaadin.terminal.ExternalResource;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;
import com.vaadin.ui.VerticalLayout;

/**
 * Creates GUI components that are part of the head bar of the application.
 * 
 * @author Jesus Martinez (jrmartin@ncmir.ucsd.edu)
 *
 */
public class HeadBar extends VerticalLayout{
				
	public HeadBar(){						
		
		createHeadBarComponents();
	}
	
	private void createHeadBarComponents(){
		
		HorizontalLayout hlayout = new HorizontalLayout();
        hlayout.setMargin(true);
        
        hlayout.setWidth("100%");
        hlayout.setHeight("100%");
        
		//give css style to component layout
        hlayout.setStyleName("headbar");
        
        Link link = new Link("",new ExternalResource("http://www.wholebraincatalog.org/web"));
        link.setIcon(new ThemeResource("images/favicon-blue.png"));
        
        hlayout.addComponent(link);
        hlayout.setExpandRatio(link, 1);
        hlayout.setComponentAlignment(link, Alignment.MIDDLE_LEFT);
        
		Label mydatalabel = new Label("Vaadin JME Applet Integration " +
								" with Drag and Drop Upload Tool");
		mydatalabel.setStyleName("mystyle");
		
		hlayout.addComponent(mydatalabel);	
		//alight to the middle left
		hlayout.setComponentAlignment(mydatalabel, Alignment.MIDDLE_CENTER);
		//Give it all extra space in tool bar
		hlayout.setExpandRatio(mydatalabel, 1);
		        		
        this.addComponent(hlayout);
	}
}

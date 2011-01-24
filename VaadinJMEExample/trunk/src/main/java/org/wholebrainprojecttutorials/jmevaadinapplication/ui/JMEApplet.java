package org.wholebrainprojecttutorials.jmevaadinapplication.ui;


import java.io.ByteArrayOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.vaadin.applet.client.ui.VAppletIntegration;

import com.vaadin.Application;
import com.vaadin.terminal.PaintException;
import com.vaadin.terminal.PaintTarget;
import com.vaadin.terminal.gwt.server.WebApplicationContext;
import com.vaadin.ui.AbstractComponent;

/**
 * The View3DWeb is the 3d panel on the web interface, that is connected
 * via an applet wrapping the wbc-view-3d package.
 * 
 * The client-side implementation is {@link VAppletIntegration}.
 *
 * @author - Jesus Martinez (jrmartin@ncmir.ucsd.edu)
 */
@SuppressWarnings("unchecked")
@com.vaadin.ui.ClientWidget(org.vaadin.applet.client.ui.VAppletIntegration.class)
public class JMEApplet extends AbstractComponent {


    private String appletClass = null;
    private String codebase;
    private String[] appletArchives = null;
    private Map<String, String> appletParams = null;

    private String command = null;

	private String height;

	private String width;

	private boolean fireEvent;
			
	Object[] parameters;
	
    public ByteArrayOutputStream data;

    public boolean ready;
    
    String mouseSet = null;
    
    PaintTarget target = null;    

    /**
     * Creates new View3DWeb component.
     *Listening for remote VM connection failed
Address already in use: JVM_Bind

     */
    public JMEApplet() {  
    }
    
    @Override
    public void paintContent(PaintTarget target) throws PaintException {
        super.paintContent(target);

        // Applet class
        if (appletClass == null) {
            // Do not paint anything of class is missing
            return;
        }
        target.addAttribute(VAppletIntegration.ATTR_APPLET_CLASS, appletClass);

        // Applet HTTP Session id
        String sid = getHttpSessionId();
        if (sid != null) {
            target.addAttribute(VAppletIntegration.ATTR_APPLET_SESSION, sid);
        }

        // Applet archives
        if (appletArchives != null) {
            target.addAttribute(VAppletIntegration.ATTR_APPLET_ARCHIVES,appletArchives);
        }

        // Applet codebase
        if (codebase != null) {
            target.addAttribute(VAppletIntegration.ATTR_APPLET_CODEBASE,codebase);
        }

        // Applet parameters
        if (appletParams != null) {
            target.addAttribute(VAppletIntegration.ATTR_APPLET_PARAM_NAMES,
                    appletParams);
        }
        
        if(height != null) {
        	target.addAttribute("height", height);
        }
        
        if(width != null){
        	target.addAttribute("width", width);
        }
        
        if(fireEvent){
        	target.addAttribute(VAppletIntegration.ATTR_CMD, command);
        	target.addAttribute(VAppletIntegration.ATTR_CMD_PARAMS, parameters);
        	fireEvent = false;
        }
    }
    
	public void setAppletWidth(String w){
		width = w;
	}
	
	public void setAppletHeight(String h){
		height = h;
	}
	
	public String getAppletWidth(){
		return width;
	}
	
	public String getAppletHeight(){
		return height;
	}
	
    /**
     * Read the HTTP session id.
     *
     * This method cannot be called if this component has not been attached to
     * the application.
     *
     * @return
     */
    protected String getHttpSessionId() {
        Application app = getApplication();
        if (app != null) {
            WebApplicationContext ctx = ((WebApplicationContext) app
                    .getContext());
            if (ctx != null) {
                return ctx.getHttpSession().getId();
            }
        }
        return null;
    }
    
    public void fireEvent(String c, Object[] p){
    	command = c;
    	parameters = p;
    	
    	fireEvent = true;
    	requestRepaint();
    	
    }
    
    /**
     * Set the fully qualified class name of the applet.
     *
     * This method is protected so that overriding classes can publish it if
     * needed.
     *
     * @param appletClass
     */
    public void setAppletClass(String appletClass) {
        this.appletClass = appletClass;
    }

    /**
     * Set list of archives needed to run the applet.
     *
     * This method is protected so that overriding classes can publish it if
     * needed.
     *
     * @param appletClass
     */
    public void setAppletArchives(String[] appletArchives) {
        this.appletArchives = appletArchives;
    }

    /**
     * Get an applet paramter. These are name value pairs passed to the applet
     * element as PARAM& elements.
     *
     * This method is protected so that overriding classes can publish it if
     * needed.
     *
     */
    protected String getAppletParams(String paramName) {
        if (appletParams == null) {
            return null;
        }
        return appletParams.get(paramName);
    }

    /**
     * Set an applet paramter. These are name value pairs passed to the applet
     * element as PARAM elements and should therefore be applied before first
     * the applet integration.
     *
     * This method is protected so that overriding classes can publish it if
     * needed.
     *
     */
    protected void setAppletParams(String paramName, String paramValue) {
        if (appletParams == null) {
            appletParams = new HashMap<String, String>();
        }
        appletParams.put(paramName, paramValue);
    }

    /**
     * Get map (name-value pairs) of parameter passed to the applet.
     *
     * This method is protected so that overriding classes can publish it if
     * needed.
     *
     * @param appletClass
     */
    protected Map<String, String> getAppletParams() {
        return Collections.unmodifiableMap(appletParams);
    }

    /**
     * Set the codebase attribute for the applet.
     *
     * By default the codebase points to GWT modulepath, but this can be
     * overrided by setting it explicitly.
     *
     * @param codebase
     */
    public void setCodebase(String codebase) {
        this.codebase = codebase;
    }

    /**
     * Set the codebase attribute for the applet.
     *
     * By default the codebase points to GWT modulepath, but this can be
     * overrided by setting it explicitly.
     *
     * @see #setCodebase(String)
     * @return codebase
     */
    public String getCodebase() {
        return codebase;
    }

    
    public void setString(String s){
    	mouseSet = s;
    }
    
    public String getMouseSet(){
    	return mouseSet;
    }
    
    @Override
    public void changeVariables(Object source, Map variables) {
        super.changeVariables(source, variables);
        if(source != null){
        	System.out.println("change variables " + source.toString());
        }        
    }
    
    /**
     * Thread for executing outgoing JavaScript commands. This thread
     * implementation is used to asynchronously invoke JavaScript commands from
     * applet.
     *
     * @author Sami Ekblad
     *
     */
    public class JSCallThread extends Thread {

        private String command = null;
        private Object result = null;
        private boolean success = false;

        /**
         * Constructor
         *
         * @param command
         *            Complete JavaScript command to be executed including
         */
        public JSCallThread(String command) {
            super();
            // SE: We need to remove all line changes to avoid exceptions
            this.command = command.replaceAll("\n", " ");
        }

        @Override
        public void run() {

            System.out.println("Call JavaScript '" + command + "'");

            String jscmd = command;

            try {
                Method getWindowMethod = null;
                Method evalMethod = null;
                Object jsWin = null;
                Class<?> c = Class.forName("netscape.javascript.JSObject");
                Method ms[] = c.getMethods();
                for (int i = 0; i < ms.length; i++) {
                    if (ms[i].getName().compareTo("getWindow") == 0) {
                        getWindowMethod = ms[i];
                    } else if (ms[i].getName().compareTo("eval") == 0) {
                        evalMethod = ms[i];
                    }

                }

                // Get window of the applet
                jsWin = getWindowMethod.invoke(c,
                        new Object[] { JMEApplet.this });

                // Invoke the command
                result = evalMethod.invoke(jsWin, new Object[] { jscmd });

                if (!(result instanceof String) && result != null) {
                    result = result.toString();
                }
                success = true;
            }

            catch (InvocationTargetException e) {
                success = true;
                result = e;
            } catch (Exception e) {
                success = true;
                result = e;
            }
        }

        /**
         * Get result of the execution.
         *
         * @return
         */
        public Object getResult() {
            return result;
        }

        /**
         * Get the result of execution as string.
         *
         * @return
         */
        public String getResultAsString() {
            if (result == null) {
                return null;
            }
            return (String) (result instanceof String ? result : result
                    .toString());
        }

        /**
         * Get the exception that occurred during JavaScript invocation.
         *
         * @return
         */
        public Exception getException() {
            return (Exception) (result instanceof Exception ? result : null);
        }

        /**
         * Check if the JavaScript invocation was an success.
         *
         * @return
         */
        public boolean isSuccess() {
            return success;
        }

    }
    
    /**
     * Invokes vaadin.forceSync that synchronizes the client-side GWT
     * application with server.
     *
     */
    public void vaadinSync() {
        jsCallAsync("vaadin.forceSync()");
    }
    
    /*
     * TODO: Variable support missing for: String[], Object[], long, float,
     * Map<String,Object>, Paintable
     */

    /**
     * Execute a JavaScript asynchronously.
     *
     * @param command
     */
    public void jsCallAsync(String command) {
        JSCallThread t = new JSCallThread(command);
        t.start();
    }
}

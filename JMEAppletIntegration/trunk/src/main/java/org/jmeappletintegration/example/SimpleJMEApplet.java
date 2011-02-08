/**
 * Copyright (C) 2010 
 *
 * JMonkeyEngine-Vaadin-Integration project is Licensed under the GNU Lesser Public License (LGPL),
 * Version 2.0 (the "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the license at
 *
 * http://www.gnu.org/licenses/lgpl.txt
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package org.jmeappletintegration.example;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Font;
import java.awt.dnd.DropTarget;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelListener;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.vaadin.applet.AbstractVaadinApplet;
import org.jmeappletintegration.util.JavaLibraryPath;
import org.jmeappletintegration.util.LibraryElement;
import org.jmeappletintegration.util.LibraryElement.ARCH;
import org.jmeappletintegration.util.LibraryElement.OS;

import com.jme.bounding.BoundingBox;
import com.jme.image.Texture;
import com.jme.input.FirstPersonHandler;
import com.jme.input.InputHandler;
import com.jme.input.InputSystem;
import com.jme.input.KeyBindingManager;
import com.jme.input.KeyInput;
import com.jme.input.MouseInput;
import com.jme.light.PointLight;
import com.jme.math.FastMath;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.renderer.Camera;
import com.jme.renderer.ColorRGBA;
import com.jme.renderer.Renderer;
import com.jme.scene.Geometry;
import com.jme.scene.Node;
import com.jme.scene.Spatial;
import com.jme.scene.Text;
import com.jme.scene.TriMesh;
import com.jme.scene.VBOInfo;
import com.jme.scene.Spatial.CullHint;
import com.jme.scene.shape.Box;
import com.jme.scene.shape.Cone;
import com.jme.scene.shape.Quad;
import com.jme.scene.shape.Sphere;
import com.jme.scene.state.BlendState;
import com.jme.scene.state.GLSLShaderObjectsState;
import com.jme.scene.state.LightState;
import com.jme.scene.state.TextureState;
import com.jme.scene.state.WireframeState;
import com.jme.scene.state.ZBufferState;
import com.jme.system.DisplaySystem;
import com.jme.system.canvas.JMECanvas;
import com.jme.system.canvas.SimpleCanvasImpl;
import com.jme.util.Debug;
import com.jme.util.GameTaskQueue;
import com.jme.util.GameTaskQueueManager;
import com.jme.util.TextureManager;
import com.jme.util.export.binary.BinaryImporter;
import com.jme.util.geom.Debugger;
import com.jme.util.stat.StatCollector;
import com.jme.util.stat.StatType;
import com.jme.util.stat.graph.GraphFactory;
import com.jme.util.stat.graph.LineGrapher;
import com.jme.util.stat.graph.TabledLabelGrapher;
import com.jmex.awt.input.AWTKeyInput;
import com.jmex.awt.input.AWTMouseInput;
import com.jmex.awt.lwjgl.LWJGLAWTCanvasConstructor;
import com.jmex.font2d.Font2D;
import com.jmex.font2d.Text2D;
import com.jmex.model.converters.FormatConverter;
import com.jmex.model.converters.ObjToJme;

/**
 * Class in charge of creating JME Applet. Uses AbstractVaadinApplet class from
 * appletintegration vaadin add-on in order to render applet inside browser. 
 * 
 * @author Jesus Martinez (jrmartin@ncmir.ucsd.edu)
 *
 */
public class SimpleJMEApplet extends AbstractVaadinApplet {
    private static final Logger logger = Logger.getLogger(SimpleJMEApplet.class
            .getName());
    
    private static final long serialVersionUID = 1L;

    private Canvas glCanvas;
    private SimpleAppletCanvasImplementor impl;

    private static final String INIT_LOCK = "INIT_LOCK";

    protected static final int STATUS_INITING = 0;
    protected static final int STATUS_RUNNING = 1;
    protected static final int STATUS_DESTROYING = 2;
    protected static final int STATUS_DEAD = 3;
    
    private static final String USE_APPLET_CANVAS_SIZE = "useAppletCanvasSize";
    private static final int DEFAULT_JME_CANVAS_WIDTH = 640;
    private static final int DEFAULT_JME_CANVAS_HEIGHT = 480;

    protected int status = STATUS_INITING;

    /**
     * Alpha bits to use for the renderer. Must be set in the constructor.
     */
    protected int alphaBits = 0;

    /**
     * Depth bits to use for the renderer. Must be set in the constructor.
     */
    protected int depthBits = 8;

    /**
     * Stencil bits to use for the renderer. Must be set in the constructor.
     */
    protected int stencilBits = 0;

    /**
     * Number of samples to use for the multisample buffer. Must be set in the constructor.
     */
    protected int samples = 0;
    
    private String[] textures = {"/Monkey.png", "/wbclogo.png", "/penguin.png",
    		                     "/vaadin.png", "/java.jpg"};
    
    private String BOX = "Box";
    private String SPHERE = "Sphere";
    private String CONE = "Cone";

    boolean rotateAll = false;
    
    private String currentShape = BOX;

	private DropTarget dropTarget = null;
    
	private AppletDropTarget appletTarget;
	
    @Override
    public void init() {

    	/*
    	 * Set the lwjgl path in order for Applet Canvas to be rendered.
    	 */
    	try{
    		injectTrayNativLib();
    	}
    	catch(Exception e){
    		e.printStackTrace();
    		throw new RuntimeException(
    				"Can not add tray native library to the java.library.path", e);
    	}    	
    	
    	super.init();
    	
        synchronized (INIT_LOCK) {
            TextureManager.clearCache();
            Text.resetFontTexture();

            DisplaySystem display = DisplaySystem.getDisplaySystem();
            display.registerCanvasConstructor("AWT", LWJGLAWTCanvasConstructor.class);
            display.setMinDepthBits( depthBits );
            display.setMinStencilBits( stencilBits );
            display.setMinAlphaBits( alphaBits );
            display.setMinSamples( samples );

            int canvasWidth;
            int canvasHeight;
            
            /**
             * Check if we're using the applet's specified dimensions or the
             * default.
             */
            if (Boolean.parseBoolean(this.getParameter(USE_APPLET_CANVAS_SIZE))) {
                canvasWidth = getWidth();
                canvasHeight = getHeight();
            } else {
                canvasWidth = DEFAULT_JME_CANVAS_WIDTH;
                canvasHeight = DEFAULT_JME_CANVAS_HEIGHT;
            }
            
            glCanvas = (Canvas)DisplaySystem.getDisplaySystem().createCanvas(canvasWidth, canvasHeight);
            // Important! Here is where we add the guts to the canvas:
            impl = new SimpleAppletCanvasImplementor(getWidth(), getHeight());

            ((JMECanvas) glCanvas).setImplementor(impl);
            setLayout(new BorderLayout());
            add(glCanvas, BorderLayout.CENTER);

            glCanvas.addComponentListener(new ComponentAdapter() {
                public void componentResized(ComponentEvent ce) {
                    if (impl != null) {
                        impl.resizeCanvas(glCanvas.getWidth(), glCanvas
                                .getHeight());
                        if (impl.getCamera() != null) {
                            Callable<?> exe = new Callable<Object>() {
                                public Object call() {
                                    impl.getCamera().setFrustumPerspective(
                                            45.0f,
                                            (float) glCanvas.getWidth()
                                                    / (float) glCanvas
                                                            .getHeight(), 1,
                                            1000);
                                    return null;
                                }
                            };
                            GameTaskQueueManager.getManager().getQueue(GameTaskQueue.RENDER).enqueue(exe);
                        }
                    }
                }
            });

            glCanvas.addFocusListener(new FocusListener() {

                public void focusGained(FocusEvent arg0) {
                    ((AWTKeyInput) KeyInput.get()).setEnabled(true);
                    ((AWTMouseInput) MouseInput.get()).setEnabled(true);
                }

                public void focusLost(FocusEvent arg0) {
                    ((AWTKeyInput) KeyInput.get()).setEnabled(false);
                    ((AWTMouseInput) MouseInput.get()).setEnabled(false);
                }

            });

            // We are going to use jme's Input systems, so enable updating.
            ((JMECanvas) glCanvas).setUpdateInput(true);

            if (!KeyInput.isInited())
                KeyInput.setProvider(InputSystem.INPUT_SYSTEM_AWT);
            ((AWTKeyInput) KeyInput.get()).setEnabled(false);
            KeyListener kl = (KeyListener) KeyInput.get();

            glCanvas.addKeyListener(kl);

            if (!MouseInput.isInited())
                MouseInput.setProvider(InputSystem.INPUT_SYSTEM_AWT);
            ((AWTMouseInput) MouseInput.get()).setEnabled(false);
            ((AWTMouseInput) MouseInput.get()).setDragOnly(true);
            glCanvas.addMouseListener((MouseListener) MouseInput.get());
            glCanvas.addMouseWheelListener((MouseWheelListener) MouseInput
                    .get());
            glCanvas.addMouseMotionListener((MouseMotionListener) MouseInput
                    .get());
            glCanvas.addMouseMotionListener(new MouseMotionAdapter() {
                public void mouseMoved(java.awt.event.MouseEvent e) {
                    if (!glCanvas.hasFocus())
                        glCanvas.requestFocus();
                };
            });
            
            dropTarget = new DropTarget(this, getAppletTarget());
        }
    }
    

    public AppletDropTarget getAppletTarget(){
    	if(appletTarget == null){
    		appletTarget = new AppletDropTarget(this);
    	}
    	
    	return appletTarget;
    }
    /**
     * Override this function from class AbstractVaadinApplet. This method is the one
     * receiving commands from the vaadin code. 
     * 
     * Example: User clicks on UI button, event is send as a string via 
     * ajax push to the applet, the applet integration code handles the event 
     * which is then received by this method.
     */
	@Override
	protected void doExecute(String command, Object[] params) {
		
		if(command.equals("changeTexture")){
			impl.changeShapeTexture();
		}
		
		else if(command.equals("changeShape")){
			String shape = params[0].toString();
			
			impl.changeShape(shape);
		}
		
		//vaadin application told applet to add new object
		else if(command.equals("add_obj")){
			//parse the parameters sent by vaadin
			String data = params[0].toString();
			String name = params[1].toString();
			
			String home = System.getProperty("user.home");
			
			//copy .obj data into user home folder
			File f = new File(home + "/" + name);

			try{
				
				f.createNewFile();
				
				FileOutputStream fop=new FileOutputStream(f);

				if(f.exists()){
					fop.write(data.getBytes());

					fop.flush();
					fop.close();
				}

			}
			catch(Exception e){
				
			}

			addFileTo3D(f);
		}
	}

	/**
	 * Take a Java io File and convert it to JME OBject
	 * @param f
	 */
	public void addFileTo3D(File f){
		
        try {
        	
        	impl.setLoading(true, f.getName());
            
        	//URL of File
            URL objFile= f.toURL();
                        
            InputStream stream = objFile.openStream();
            
			FormatConverter converter = new ObjToJme();

            ByteArrayOutputStream BO=new ByteArrayOutputStream();
            
			// This will read the .jme format and convert it into a scene graph
			BinaryImporter jbr = new BinaryImporter();
					
			
			//convert file obj to jme object
            converter.convert(stream,BO);
                                                
            //cast jme object to spatial
            Spatial r=(Spatial) jbr.load(new ByteArrayInputStream(BO.toByteArray()));
                                                //add spatial to root
            impl.addOBJ(r);            
            
            impl.setLoading(false, f.getName());
            
        } catch (IOException e) {
            logger.logp(Level.SEVERE, this.getClass().toString(),
                    "simpleInitGame()", "Exception", e);
        }
	}
	 
	/**
	 * Extract the tray.dll or libtray.so (depending on the Operating System) 
	 * into a temporary location and add this very location to the
	 * <i>java.library.path</i> path.
	 * 
	 * @throws Exception
	 *             In case of an error.
	 */
	public void injectTrayNativLib() throws Exception {
		/**
		 * Windows
		 */
		LibraryElement winLib1 = new LibraryElement("natives/win/win32", "lwjgl.dll",
				OS.Windows, ARCH.x86);
		LibraryElement win64Lib1 = new LibraryElement("natives/win/win64", "lwjgl64.dll",
				OS.Windows, ARCH.x86_64);
		JavaLibraryPath.injectNativLib(winLib1,	win64Lib1);	

		/**
		 * Linux
		 */
		LibraryElement linuxLibx861 = new LibraryElement("natives/linux/x86", "liblwjgl.so",
				OS.Linux, ARCH.i386);
		LibraryElement linuxLibx862 = new LibraryElement("natives/linux/x86", "libopenal.so",
				OS.Linux, ARCH.i386);
		LibraryElement linuxLibamd641 = new LibraryElement("natives/linux/amd64", "liblwjgl64.so",
				OS.Linux, ARCH.amd64);
		LibraryElement linuxLibamd642 = new LibraryElement("natives/linux/amd64", "libopenal64.so",
				OS.Linux, ARCH.amd64);
		JavaLibraryPath.injectNativLib(linuxLibx861,linuxLibx862,linuxLibamd641, linuxLibamd642);
		
		/**
		 * Mac
		 */
		LibraryElement libMac1 = new LibraryElement("natives/mac", "liblwjgl.jnilib",
				OS.Mac);
		LibraryElement libMac2 = new LibraryElement("natives/mac", "openal.dylib",
				OS.Mac);
		JavaLibraryPath.injectNativLib(libMac1, libMac2);
	}
    @Override
    public void start(){
    }
    
    @Override
    public void stop(){
    }
    
    @Override 
    public void destroy(){
    }
    
    
    public Camera getCamera() {
        return impl.getCamera();
    }

    public Renderer getRenderer() {
        return impl.getRenderer();
    }

    public Node getRootNode() {
        return impl.getRootNode();
    }

    public Node getStatNode() {
        return impl.getStatNode();
    }

    public float getTimePerFrame() {
        return impl.getTimePerFrame();
    }

    public LightState getLightState() {
        return impl.getLightState();
    }

    public WireframeState getWireframeState() {
        return impl.getWireframeState();
    }
    
    public InputHandler getInputHandler() {
        return impl.getInputHandler();
    }
    
    public void setInputHandler(InputHandler input) {
        impl.setInputHandler(input);
    }
    
    public SimpleAppletCanvasImplementor getImpl(){
    	return impl;
    }

    class SimpleAppletCanvasImplementor extends SimpleCanvasImpl {

        /**
         * True if the renderer should display the depth buffer.
         */
        protected boolean showDepth = false;

        /**
         * True if the renderer should display bounds.
         */
        protected boolean showBounds = false;

        /**
         * True if the rnederer should display normals.
         */
        protected boolean showNormals = false;

        protected boolean pause;

        /**
         * A wirestate to turn on and off for the rootNode
         */
        protected WireframeState wireState;

        private InputHandler input;

        /**
         * A lightstate to turn on and off for the rootNode
         */
        protected LightState lightState;

        /**
         * The root node for our stat graphs.
         */
        protected Node statNode;

        private TabledLabelGrapher tgrapher;
        private Quad labGraph;
        private TriMesh box, sphere, cone;
        private Quaternion rotQuat;
        private float angle = 0;
        private Vector3f axis;
        
        Vector3f max = new Vector3f(5, 5, 5);
        Vector3f min = new Vector3f(-5, -5, -5);
        
        private Text2D loadField;

		private Node HUD;
        
        protected SimpleAppletCanvasImplementor(int width, int height) {
            super(width, height);
        }

        public Node getStatNode() {
            return statNode;
        }

        public LightState getLightState() {
            return lightState;
        }

        public WireframeState getWireframeState() {
            return wireState;
        }
        
        public InputHandler getInputHandler() {
            return input;
        }
        
        public void setInputHandler(InputHandler input) {
            this.input = input;
        }

        public void simpleUpdate() {
            
            input.update(tpf);

            if (Debug.stats) {
                StatCollector.update();
                labGraph.setLocalTranslation(.5f*labGraph.getWidth(), (renderer.getHeight()-.5f*labGraph.getHeight()), 0);
            }

        	float tpf = getTimePerFrame();
        	if (tpf < 1) {
        		angle = angle + (tpf * 25);
        		if (angle > 360) {
        			angle -= 360;
        		}
        	}

        	rotQuat.fromAngleNormalAxis(angle * FastMath.DEG_TO_RAD, axis);
        	
        	if(currentShape.equals(BOX)){
        		box.setLocalRotation(rotQuat);
        	}
        	
        	if(currentShape.equals(SPHERE)){
        		sphere.setLocalRotation(rotQuat);
        	}
        	
        	if(currentShape.equals(CONE)){
        		cone.setLocalRotation(rotQuat);
        	}
        	
        	/** If toggle_pause is a valid command (via key p), change pause. */
            if ( KeyBindingManager.getKeyBindingManager().isValidCommand(
                    "toggle_pause", false ) ) {
                pause = !pause;
            }

            /** If toggle_wire is a valid command (via key T), change wirestates. */
            if ( KeyBindingManager.getKeyBindingManager().isValidCommand(
                    "toggle_wire", false ) ) {
                wireState.setEnabled( !wireState.isEnabled() );
                rootNode.updateRenderState();
            }
            /** If toggle_lights is a valid command (via key L), change lightstate. */
            if ( KeyBindingManager.getKeyBindingManager().isValidCommand(
                    "toggle_lights", false ) ) {
                lightState.setEnabled( !lightState.isEnabled() );
                rootNode.updateRenderState();
            }
            /** If toggle_bounds is a valid command (via key B), change bounds. */
            if ( KeyBindingManager.getKeyBindingManager().isValidCommand(
                    "toggle_bounds", false ) ) {
                showBounds = !showBounds;
            }
            /** If toggle_depth is a valid command (via key F3), change depth. */
            if ( KeyBindingManager.getKeyBindingManager().isValidCommand(
                    "toggle_depth", false ) ) {
                showDepth = !showDepth;
            }

            if ( KeyBindingManager.getKeyBindingManager().isValidCommand(
                    "toggle_normals", false ) ) {
                showNormals = !showNormals;
            }
            /** If camera_out is a valid command (via key C), show camera location. */
            if ( KeyBindingManager.getKeyBindingManager().isValidCommand(
                    "camera_out", false ) ) {
                logger.info( "Camera at: "
                        + renderer.getCamera().getLocation() );
            }

            if ( KeyBindingManager.getKeyBindingManager().isValidCommand(
                    "screen_shot", false ) ) {
                renderer.takeScreenShot( "SimpleAppletScreenShot" );
            }

            if ( KeyBindingManager.getKeyBindingManager().isValidCommand(
                    "mem_report", false ) ) {
                long totMem = Runtime.getRuntime().totalMemory();
                long freeMem = Runtime.getRuntime().freeMemory();
                long maxMem = Runtime.getRuntime().maxMemory();
                
                logger.info("|*|*|  Memory Stats  |*|*|");
                logger.info("Total memory: "+(totMem>>10)+" kb");
                logger.info("Free memory: "+(freeMem>>10)+" kb");
                logger.info("Max memory: "+(maxMem>>10)+" kb");
            }
        }

        public void simpleSetup() {
            synchronized (INIT_LOCK) {
                input = new FirstPersonHandler(getCamera(), 50, 1);

                /**
                 * Create a wirestate to toggle on and off. Starts disabled with
                 * default width of 1 pixel.
                 */
                wireState = renderer.createWireframeState();
                wireState.setEnabled(false);
                rootNode.setRenderState(wireState);

                // ---- LIGHTS
                /** Set up a basic, default light. */
                PointLight light = new PointLight();
                light.setDiffuse(new ColorRGBA(0.75f, 0.75f, 0.75f, 0.75f));
                light.setAmbient(new ColorRGBA(0.5f, 0.5f, 0.5f, 1.0f));
                light.setLocation(new Vector3f(100, 100, 100));
                light.setEnabled(true);

                /**
                 * Attach the light to a lightState and the lightState to
                 * rootNode.
                 */
                lightState = renderer.createLightState();
                lightState.setEnabled(true);
                lightState.attach(light);
                rootNode.setRenderState(lightState);

                // Finally, a stand alone node (not attached to root on purpose)
                statNode = new Node("stat node");
                statNode.setCullHint(Spatial.CullHint.Dynamic);

                setupStatGraphs();
                setupStats();

                statNode.updateGeometricState(0, true);
                statNode.updateRenderState();

                simpleAppletSetup();

                /** Assign key P to action "toggle_pause". */
                KeyBindingManager.getKeyBindingManager().set("toggle_pause",
                        KeyInput.KEY_P);
                /** Assign key T to action "toggle_wire". */
                KeyBindingManager.getKeyBindingManager().set("toggle_wire",
                        KeyInput.KEY_T);
                /** Assign key L to action "toggle_lights". */
                KeyBindingManager.getKeyBindingManager().set("toggle_lights",
                        KeyInput.KEY_L);
                /** Assign key B to action "toggle_bounds". */
                KeyBindingManager.getKeyBindingManager().set("toggle_bounds",
                        KeyInput.KEY_B);
                /** Assign key N to action "toggle_normals". */
                KeyBindingManager.getKeyBindingManager().set("toggle_normals",
                        KeyInput.KEY_N);
                /** Assign key C to action "camera_out". */
                KeyBindingManager.getKeyBindingManager().set("camera_out",
                        KeyInput.KEY_C);
                KeyBindingManager.getKeyBindingManager().set("screen_shot",
                        KeyInput.KEY_F1);
                KeyBindingManager.getKeyBindingManager().set("exit",
                        KeyInput.KEY_ESCAPE);
                KeyBindingManager.getKeyBindingManager().set("mem_report",
                        KeyInput.KEY_R);

                status = STATUS_RUNNING;
            }
        }

        /**
         * Add box to JME Scene graph
         */
        private void simpleAppletSetup() {

            try {       
                rotQuat = new Quaternion();
                axis = new Vector3f(1, 1, 0.5f).normalizeLocal();
                      
                createBox();

                HUD = new Node("HUD");
                
        		HUD.setLightCombineMode(Spatial.LightCombineMode.Off);

        		getRootNode().attachChild(HUD);

        		Font2D dfont = new Font2D();
        		ZBufferState zbs = DisplaySystem.getDisplaySystem().getRenderer()
        				.createZBufferState();
        		zbs.setFunction(ZBufferState.TestFunction.Always);
        		
        		loadField = dfont.createText("Loading...\n (Please Wait)", 14,
        				Font.BOLD); // make smaller by scale
        		loadField.setLocalTranslation(new Vector3f(DisplaySystem.getDisplaySystem().getWidth() / 3,
        				DisplaySystem.getDisplaySystem().getHeight() / 2, 3));
        		System.out.println("local trans " + loadField.getLocalTranslation());
        		loadField.setLocalScale(.9f * 1.5f);
        		loadField.setRenderQueueMode(Renderer.QUEUE_ORTHO);
        		loadField.setRenderState(zbs);
        		loadField.setTextColor(new ColorRGBA(1, 1, 1f, 1));
        		loadField.setCullHint(CullHint.Always); // hide by default
        		
        		HUD.attachChild(loadField);        		
        		
            } catch (Exception e) {
                // Had issues setting up. We'll catch it and go on so it
                // doesn't
                // try setting up over and over.
                logger.logp(Level.SEVERE, this.getClass().toString(),
                        "simpleSetup()", "Exception", e);
            }			
		}
        
        public void setLoading(boolean mode, String name){
        	if(mode){
        		System.out.println("adding load field to hud ");
        		loadField.setCullHint(CullHint.Never);
        		loadField.setText("Loading " + name + " please Wait");
        		loadField.updateRenderState();
        		HUD.updateRenderState();
        	}
        	
        	else{
        		System.out.println("hide loadfiled");
        		loadField.setCullHint(CullHint.Always);
        	}
        }

        private void applyRenderState(TriMesh mesh, String image) {
            TextureState ts = getRenderer().createTextureState();
            ts.setEnabled(true);
            ts.setTexture(TextureManager.loadTexture(
                    SimpleJMEApplet.class.getResource(
                            image), Texture.MinificationFilter.Trilinear,
                    Texture.MagnificationFilter.Bilinear));
     
            //Apply Render State to Box 
            mesh.setRenderState(ts);	
            
            mesh.updateRenderState();            
		}

		/**
         * Makes the target node have an XRay (aka Ghost) effect to it
         * @param target
         * @return True if the apply was successful, False is unsuccessful
         */
        public boolean applyEffectGhost(Spatial target)
        {
            try
            {		        
                GLSLShaderObjectsState xray = DisplaySystem.getDisplaySystem()
					.getRenderer().createGLSLShaderObjectsState();
                                    
                //URL frag = target.getClass().getResource("xray.frag");
                
                //We are using the base of the application to find the shaders, which means 
                //they should be located at the same place as the wbc-properties files
                URL frag = SimpleJMEApplet.class.getResource("/xray.frag");
                URL vert = SimpleJMEApplet.class.getResource("/xray.vert");
                
                xray.load(vert,  frag);  
                xray.setEnabled(true);
                xray.setUniform("edgefalloff", 1f);

                target.setRenderState(xray);
                target.updateRenderState();             
            }
	        catch (Exception e) 
	        {
	        		e.printStackTrace();
	        		return false;
	        }
	                
	        return true;
        }
        
        /**
         * Takes a Spatial and adds it to the Root Node of the application
         * @param obj
         */
        public void addOBJ(Spatial obj){   
        	
        	//give the spatial a random color
    		((Geometry) obj).setSolidColor(ColorRGBA.randomColor());
    		
    		obj.setModelBound(new BoundingBox());
    		VBOInfo nfo = new VBOInfo(true);
    		((Geometry) obj).setVBOInfo(nfo);
    		
    		//apply ghost effect to spatial
        	this.applyEffectGhost(obj);
            
        	//Computer point right in front of camera
        	Vector3f location = this.getCamera().getLocation();
    		Vector3f toward = getCamera().getDirection().mult(40f);
    		Vector3f combined = location.add(toward);
    		
        	obj.setLocalTranslation(combined);
        	
        	//attach spatial to root node
        	getRootNode().attachChild(obj);        	
        	
        	//update model bound and render state
    		getRootNode().updateModelBound();
    		getRootNode().updateRenderState();
        }
        
		public void simpleRender() {
            statNode.draw(renderer);
            doDebug();
        }

		/**
		 * Create a JME Box
		 */
        public void createBox(){
            box = new Box("Box", min, max);
            box.setLocalTranslation(new Vector3f(0, 0, -15));
            box.setModelBound(new BoundingBox());
            box.updateModelBound();
            
            
            applyRenderState((TriMesh)box, "/Monkey.png");
            
            getRootNode().attachChild(box);
        }
        
        /**
         * Create a JME Sphere
         */
        public void createSphere(){
            sphere = new Sphere("Sphere",63, 50, 8);
                     
            sphere.setLocalTranslation(new Vector3f(0, 0, -15));
            sphere.setModelBound(new BoundingBox());
            sphere.updateModelBound();
            
            applyRenderState((TriMesh)sphere,"/Monkey.png");
            
            getRootNode().attachChild(sphere);
        }
        
        public void createCone(){
        	
        	int axisSamples = 10; 
        	int radialSamples = 10;
        	float radius = 8;
        	float height = 15;
        	cone = new Cone("Cone", axisSamples, radialSamples, radius, height);
        	        	
            cone.setModelBound(new BoundingBox());
            cone.updateModelBound();
            cone.setLocalTranslation(new Vector3f(0, 0, -15));   

        	applyRenderState((TriMesh)cone, "/Monkey.png");
        	
        	getRootNode().attachChild(cone);
        }
        	
        /**
         * Changes the color of the current shape being rendered in the applet
         */
        public void changeShapeTexture(){
        	
            Random generator = new Random();
            int index = generator.nextInt(5);
            
            if(currentShape.equals(BOX)){
            	applyRenderState((TriMesh)box, textures[index]);
        	}

        	else if(currentShape.equals(CONE)){
            	applyRenderState((TriMesh)cone, textures[index]);
        	}
        	
        	else if(currentShape.equals(SPHERE)){
            	applyRenderState((TriMesh)sphere, textures[index]);
        	}
        	
        	getRootNode().updateRenderState();
        }
        
        /**
         * Replaces current shape in the applet
         * 
         * @param shape
         */
        public void changeShape(String shape){       
        	
        	Vector3f location = this.getCamera().getLocation();
    		Vector3f toward = getCamera().getDirection().mult(40f);
    		Vector3f combined = location.add(toward);
    		
        	if(shape.equals(BOX)){
                box.setCullHint(CullHint.Dynamic);
                box.setLocalTranslation(combined);
                
                currentShape = BOX;
                
                hideOtherShapes(currentShape);
        	}
        	
        	else if(shape.equals(CONE)){
        		if(cone == null){
        			createCone();
        		}
        		
        		cone.setCullHint(CullHint.Dynamic);
        		cone.setLocalTranslation(combined);
        		
                currentShape = CONE;
                hideOtherShapes(currentShape);

        	}
        	
        	else if(shape.equals(SPHERE)){
        		if(sphere == null){
        			createSphere();
        		}
        		
        		sphere.setCullHint(CullHint.Dynamic);
        		sphere.setLocalTranslation(combined);
        		                
                currentShape = SPHERE;
                
                hideOtherShapes(currentShape);                
        	}        	
        	
        	getRootNode().updateRenderState();
        }
        
        private void hideOtherShapes(String currentShape){
        	if(currentShape.equals(BOX)){
        		if(sphere!=null){
        			sphere.setCullHint(CullHint.Always);
        		}
        		
        		if(cone!=null){
        			cone.setCullHint(CullHint.Always);
        		}
        	}
        	
        	if(currentShape.equals(SPHERE)){
        		if(box!=null){
        			box.setCullHint(CullHint.Always);
        		}
        		
        		if(cone!=null){
        			cone.setCullHint(CullHint.Always);
        		}
        	}
        	
        	if(currentShape.equals(CONE)){
        		if(box!=null){
        			box.setCullHint(CullHint.Always);
        		}
        		
        		if(sphere!=null){
        			sphere.setCullHint(CullHint.Always);
        		}
        	}
        }
        protected void doDebug() {
            /**
             * If showing bounds, draw rootNode's bounds, and the bounds of all its
             * children.
             */
            if ( showBounds ) {
                Debugger.drawBounds( rootNode, renderer, true );
            }

            if ( showNormals ) {
                Debugger.drawNormals( rootNode, renderer );
                Debugger.drawTangents( rootNode, renderer );
            }

            if (showDepth) {
                renderer.renderQueue();
                Debugger.drawBuffer(Texture.RenderToTextureType.Depth, Debugger.NORTHEAST, renderer);
            }
        }

        
        /**
         * Set up which stats to graph
         *
         */
        protected void setupStats() {
        	tgrapher.addConfig(StatType.STAT_FRAMES, LineGrapher.ConfigKeys.Color.name(), ColorRGBA.green);
        	tgrapher.addConfig(StatType.STAT_TRIANGLE_COUNT, LineGrapher.ConfigKeys.Color.name(), ColorRGBA.cyan);
        	tgrapher.addConfig(StatType.STAT_QUAD_COUNT, LineGrapher.ConfigKeys.Color.name(), ColorRGBA.lightGray);
        	tgrapher.addConfig(StatType.STAT_LINE_COUNT, LineGrapher.ConfigKeys.Color.name(), ColorRGBA.red);
        	tgrapher.addConfig(StatType.STAT_GEOM_COUNT, LineGrapher.ConfigKeys.Color.name(), ColorRGBA.gray);
        	tgrapher.addConfig(StatType.STAT_TEXTURE_BINDS, LineGrapher.ConfigKeys.Color.name(), ColorRGBA.orange);

            tgrapher.addConfig(StatType.STAT_FRAMES, TabledLabelGrapher.ConfigKeys.Decimals.name(), 0);
            tgrapher.addConfig(StatType.STAT_FRAMES, TabledLabelGrapher.ConfigKeys.Name.name(), "Frames/s:");
            tgrapher.addConfig(StatType.STAT_TRIANGLE_COUNT, TabledLabelGrapher.ConfigKeys.Decimals.name(), 0);
            tgrapher.addConfig(StatType.STAT_TRIANGLE_COUNT, TabledLabelGrapher.ConfigKeys.Name.name(), "Avg.Tris:");
            tgrapher.addConfig(StatType.STAT_TRIANGLE_COUNT, TabledLabelGrapher.ConfigKeys.FrameAverage.name(), true);
            tgrapher.addConfig(StatType.STAT_QUAD_COUNT, TabledLabelGrapher.ConfigKeys.Decimals.name(), 0);
            tgrapher.addConfig(StatType.STAT_QUAD_COUNT, TabledLabelGrapher.ConfigKeys.Name.name(), "Avg.Quads:");
            tgrapher.addConfig(StatType.STAT_QUAD_COUNT, TabledLabelGrapher.ConfigKeys.FrameAverage.name(), true);
            tgrapher.addConfig(StatType.STAT_LINE_COUNT, TabledLabelGrapher.ConfigKeys.Decimals.name(), 0);
            tgrapher.addConfig(StatType.STAT_LINE_COUNT, TabledLabelGrapher.ConfigKeys.Name.name(), "Avg.Lines:");
            tgrapher.addConfig(StatType.STAT_LINE_COUNT, TabledLabelGrapher.ConfigKeys.FrameAverage.name(), true);
            tgrapher.addConfig(StatType.STAT_GEOM_COUNT, TabledLabelGrapher.ConfigKeys.Decimals.name(), 0);
            tgrapher.addConfig(StatType.STAT_GEOM_COUNT, TabledLabelGrapher.ConfigKeys.Name.name(), "Avg.Objs:");
            tgrapher.addConfig(StatType.STAT_GEOM_COUNT, TabledLabelGrapher.ConfigKeys.FrameAverage.name(), true);
            tgrapher.addConfig(StatType.STAT_TEXTURE_BINDS, TabledLabelGrapher.ConfigKeys.Decimals.name(), 0);
            tgrapher.addConfig(StatType.STAT_TEXTURE_BINDS, TabledLabelGrapher.ConfigKeys.Name.name(), "Avg.Tex binds:");
            tgrapher.addConfig(StatType.STAT_TEXTURE_BINDS, TabledLabelGrapher.ConfigKeys.FrameAverage.name(), true);
        }
        
        /**
         * Set up the graphers we will use and the quads we'll show the stats on.
         *
         */
        protected void setupStatGraphs() {
            StatCollector.setSampleRate(1000L);
            StatCollector.setMaxSamples(30);
            labGraph = new Quad("labelGraph", Math.max(renderer.getWidth()/3, 250), Math.max(renderer.getHeight()/3, 250)) {
                private static final long serialVersionUID = 1L;
                @Override
                public void draw(Renderer r) {
                    StatCollector.pause();
                    super.draw(r);
                    StatCollector.resume();
                }
            };
            tgrapher = GraphFactory.makeTabledLabelGraph((int)labGraph.getWidth(), (int)labGraph.getHeight(), labGraph);
            tgrapher.setColumns(1);
            labGraph.setLocalTranslation(0, (renderer.getHeight()*5/6), 0);
            statNode.attachChild(labGraph);
            
        }
    }

}

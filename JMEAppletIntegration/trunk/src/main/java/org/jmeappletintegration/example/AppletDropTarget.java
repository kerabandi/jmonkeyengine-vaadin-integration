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

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.jme.scene.Spatial;
import com.jme.util.export.binary.BinaryImporter;
import com.jmex.model.converters.FormatConverter;
import com.jmex.model.converters.ObjToJme;

/**
 * Listener Class to detect user dropping a file in the applet. 
 * User dropped file is then given to applet to convert JME object and add to 3D Scene.
 * 
 * @author Jesus Martinez (jrmartin@ncmir.ucsd.edu)
 *
 */
public class AppletDropTarget implements DropTargetListener{

	SimpleJMEApplet applet;
	
	public AppletDropTarget(SimpleJMEApplet applet){
		this.applet = applet;
	}
	
	public void dragEnter(DropTargetDragEvent dtde) {
		// TODO Auto-generated method stub
	}

	public void dragExit(DropTargetEvent dte) {
		// TODO Auto-generated method stub
	}

	public void dragOver(DropTargetDragEvent dtde) {
		// TODO Auto-generated method stub
	}

	public void drop(DropTargetDropEvent dtde) {
		
		List<File> files = new ArrayList<File>();
		
		try{
		   Transferable transferable = dtde.getTransferable();
		   DataFlavor flavors[] = transferable.getTransferDataFlavors();
		   
		   for (int i = 0; i < flavors.length; i++) {
			   
			  // Check for file lists specifically
		        if (flavors[i].isFlavorJavaFileListType()) {
		        	
		          // Great! Accept copy drops...
		          dtde.acceptDrop(DnDConstants.ACTION_COPY);

		          // And add the list of file names to our text area
		          java.util.List list = (java.util.List) transferable.getTransferData(flavors[i]);
		          
		          
		          for (int j = 0; j < list.size(); j++) {
		          
		             File f = new File(list.get(j).toString());		
		             
		             ThreadLoad load = new ThreadLoad(f,applet);
		             load.start();
		          }
		        }
		      }
		   
		   dtde.dropComplete(true);
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}

	public void dropActionChanged(DropTargetDragEvent dtde) {
		System.out.println("Drop Action");
	}

	
}

/**
 * Java File to JME object conversion done inside own thread
 * @author jrmartin
 *
 */
class ThreadLoad extends Thread{
	
	private File f;
	private SimpleJMEApplet applet;

	public ThreadLoad(File f, SimpleJMEApplet applet){
		this.f = f;
		this.applet = applet;
	}
	
	public void run(){
		try{
			applet.addFileTo3D(f);
		}
		catch(Exception e){
			e.printStackTrace();
		}

	}
}

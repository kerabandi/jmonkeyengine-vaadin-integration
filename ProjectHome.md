**Integrates JMonkeyEngine inside a Vaadin Application, with the ability to drag and drop OBJ files to browser**

# [Check out the online Demo](http://dev-data-web.wholebraincatalog.org/jme-vaadin-applet) #

<a href='http://dev-data-web.wholebraincatalog.org/jme-vaadin-applet'><img src='http://img827.imageshack.us/img827/5623/jmeappletintegration.png' alt='Whole Brain Catalog interface' height='300' width='480' /></a>

## What is this project about? ##

The JMonkeyEngine-Vaadin-Integration project demonstrates how to do 3D rendering in a web browser using Vaadin, Java Applet and JMonkeyEngine.

The communication between the GUI components in the browser and the applet take place via AJAX push calls.

One of the open source technologies used in the project, Vaadin Framework, provides the feature of compiling your Java code to HTML/CSS/Javascript, which is benefitial for those developers wanting to develop Web applications but
> don't have enough knowledge of web based languages to do so
# Technologies used #

**Vaadin**: Used to create the User Interface.

**Vaadin Add-ons:**

**1.** AppletIntegration  found [here](http://vaadin.com/directory#addon/appletintegration). This add-on was necessary in order to integrate Java Applet in Vaadin Application. The add-on was added to the Vaadin add-on repository by a member of the community.

**2.** ICEPush found [here](http://vaadin.com/directory#addon/icepush). This add-on adds push support to the Vaadin Application. Used in cases where applet needed to receive an event from the UI. The AppletIntegration add-on provides a feature for Applet to send/receive events from other Vaadin components, the ICEPUsh was used in combination with this in order for Applet to receive events inmediately.

**JMonkeyEngine**: Used to render the 3D scene/graphics/objects inside the applet.

## Other Technologies: ##

**Maven**: Used to keep track of dependencies needed in project. Easy way to manage all jars without having to add them manually one by one.

**Java**: Most of the code used to create this application was in Java.

**Jetty**: Used to create a local server to run the project.

# Instructions #
Read the wiki found [here](http://code.google.com/p/jmonkeyengine-vaadin-integration/wiki/Instructions) in order to learn how to check out the code and run it from scratch.
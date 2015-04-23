# Requirements #

## Requirements: ##

1. Java JDK 1.5 or above, I recommend installing the latest JDK available.

2. Maven 2.1 or above.
> - you can check to see if you have this maven version installed in your machine by opening a command prompt and typing "mvn -version".

3. 150 MBspace in hard drive.

4. Supported Web Browsers:
> Windows: Firefox, IE, Chrome, Opera

> Mac: Safari 4, Firefox

> Linux: Firefox

5. (Optional) Text editor, if you are planning on making any changes to the
example I provided. I use Eclipse to edit my text files.

# Steps #

Steps needed in order to run the provided example.

Make sure you meet the requirements explained above.

## Running project using command prompt. ##

1. Check-out the VaadinJMEAppletExample trunk to your workspace.

**2**. Navigate from the command prompt to the parent folder holding the check-out project.
Example: If you checked out your project to C://vaadinproject, navigate
to folder named "vaadinproject".

3. Once inside the folder, type in command "mvn clean install" in command prompt. This will build the project for you, and you should see "Build Successful" at the end of it.

4. After building the project, you will be able to deploy the application to your browser by entering "mvn jetty:run" in your command prompt. This will deploy your application to a local server at port :8080. You will know this task is done when you see "Started Jetty Server" displayed in your command prompt.

5. After deploying the application, go to a supported web browser and type "http://localhost:8080/jme-vaadin-applet". You will be prompted to accept the applet certificate, click "Ok" in order for applet to show.

## Making changes to the Applet ##
If you want to modify the Applet code, you will have to check out the JMEAppletIntegration trunk found in this googlecode project to your workspace. The main class in this project is SimpleJMEApplet.java. You will have to recompile this project into a jar and add it to your Vaadin Application project in order to see the changes.
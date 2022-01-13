# Project-Diagram-Generator
 Program that takes the root folder of a programming project and automatically generates a UML diagram for that entire project; will only process .java files for now as that is what I am familiar with.
 
 Requires Graphviz to work, so grab it here: https://graphviz.org/download/
 
 Just run the "Project Diagram Generator 1.8.jar" to get things started, direct the prompt towards your graphviz/bin/dot.exe file (wherever you downloaded Graphviz to), and you can get started! The default image in there is the UML diagram for the program itself. More thorough description below.

## How to use this
- If you're code-aware and want rapid construction of the same project, download this project and find the Main.java class in the Main package; there's a function called 'runLoose()' that takes a path and output name directly to automatically construct the same project's UML every time you run it, which is faster than doing it through the UI, although you could change the project while the UI and its settings were open and do rapid reconstruction that way as well.

- To use it normally, download the 'Project Diagram Generator 1.8.jar' file; this is a runnable .jar (basically an .exe) that gives you a GUI for viewing and generating the diagrams you generate (they are really pleasing to look at when it's a good design). It uses Java v.1.8 so you don't need the newest jdk to run it (for some reason user-end java releases are only in version 1.8).

- You will need to download Graphviz, as that is the software used to make the graphs after your project has been converted into a generic UML format. The first time you run the program, it will configure itself by asking you to direct it to the graphviz/bin/dot.exe file, which it saves in a folder beside the .jar alongside folders to hold the images you generate and the graphviz source files to replicate or edit them manually.

- Upon configuring the program, you will see a default image that is the UML Diagram I generated of this project, so you can see the class architecture that produced this!

- You need to provide three things for the program to run: the directory of the root path for your project (either manual entry or using the + button next to it that pops open a FileChooser browser to navigate your file system), any packages you want to ignore (please just use the + button to get a popout interface for selecting this, you can click on the packages to open and close them which translate to whether or not the contents of that package will be interpreted for the UML), and a name for the output image.

- You can also filter out whether the UML will contain instance variables, functions, and/or the private instance variables/functions using the three checkboxes on the right of the screen.

That's about it! Let me know if anything doesn't work or if you would recommend some changes; it currently only works on java projects (it has to know how to read the files in the project, which takes a lot of care) but I plan to eventually support more languages once I get to understanding them well enough to process them robustly.

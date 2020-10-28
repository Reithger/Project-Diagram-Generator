# Project-Diagram-Generator
 Program that takes the root folder of a programming project and automatically generates a UML diagram for that entire project; will only process .java files for now as that is what I am familiar with

## How to use this
- To use it as-is, download the 'Project Diagram Generator.jar' file; this is a runnable .jar (basically an .exe) that gives you a GUI for viewing and generating the diagrams you generate (they are really pleasing to look at when it's a good design).

- You will need to download Graphviz, as that is the software used to make the graphs after your project has been converted into a generic UML format. The first time you run the program, it will configure itself by asking you to direct it to the graphviz/bin/dot.exe file, which it saves in a folder beside the .jar alongside folders to hold the images you generate and the graphviz source files to replicate or edit them manually.

- Upon configuring the program, you will see a default image that is the UML Diagram I generated of this project, so you can see the class architecture that produced this!

- You need to provide three things for the program to run: the directory of the root path for your project (either manual entry or using the + button next to it that pops open a FileChooser browser to navigate your file system), any packages you want to ignore (please just use the + button to get a popout interface for selecting this, you can click on the packages to open and close them which translate to whether or not the contents of that package will be interpreted for the UML), and a name for the output image.

- You can also filter out whether the UML will contain instance variables, functions, and/or the private instance variables/functions using the three checkboxes on the right of the screen.

That's about it! Let me know if anything doesn't work or if you would recommend some changes; it currently only works on java projects (it has to know how to read the files in the project, which takes a lot of care) but I plan to eventually support more languages once I get to understanding them well enough to process them robustly.

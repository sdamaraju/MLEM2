EECS 839, Programming project
Sai Krishna Teja Damaraju
#3028488

Implementation of MLEM2 algorithm with concept approximations for complete and incomplete data sets.

### Ways to run the algorithm ###

There are 2 ways to run the algorithm.

-> Using shortcut "run.sh"

1. Copy the EECS839_ProgrammingProject_#3028488 to any system that has Java enabled.

2. Open the EECS839_ProgrammingProject_#3028488 folder.

3. Double-click on the run.sh command.

(Steps mentioned above are tested on MAC as well as Windows systems).

(On Mac : if the double-click doesn't work due to authenticity issues by Mac OS,
 please provide execute permissions to run.sh by running the command :  "chmod a+x run.sh" 
 and then try double clicking the command.	
 if it still fails : please use terminal to change directory to the Project directory
 and run the below command)
 "sh run.sh"

#If still the above fails, please use the below steps to run the algorithm manually.

-> Manual command prompt run

1. Copy the EECS839_ProgrammingProject_#3028488 to any system that has Java enabled.

2. Execute the below commands on a terminal.
	
	a. cd "EECS839_Programming_Project_#3028488"/src
	-Changes directory to Project's Source folder

	b. javac -d ../classes MLEM2Implementation.java
	-Generates all necessary classes.

	c. cd ../classes
	-Changes directory to classes folder where the classes are generated.

	d. java MLEM2Implementation
	-Runs the MLEM2Implementation

### MLEM2 with Concept approximation User inputs ###

1. Once the execution begins, console prompts user to enter a valid input file name.
	
	"Please enter a valid input file name with absolute path..."

	-Upon seeing this message, please provide entire path including the file name in the below pattern. 
	"/Users/sdamaraju/Desktop/EECS839/SimpleTest.txt" (quotes not required)

2. If the provided file is not valid, console reverts to user, asking to enter the appropriate file name.

3. Once file name is successfully entered, console prompts user for approximation details with below message.
	
	"Please type in 'lower' for lower concept approximation..."

	-As the message says, if your data set is incomplete, and you want to calculate "lower approximation" for the same, then type in "lower" (case in-sensitive), any other input, or even pressing just "enter" key, will calculate upper approximation for the same.

Please noter that if the data set is "complete", not missing any attributes and is "consistent", then 
the algorithm is efficient enough to understand that approximation is not required and ignores
any input provided for approximation.

4. Console then prompts for output file path and name, to print the rules, by showing the below message.

	"Please enter an output file (path) name..."
	
	If the file doesn't exist in the path, the algorithm creates the file and writes the result to the file. Please understand that, if file path is not provided, the file will be created in the classes folder.


### Important Note : System requirements ###

System requirements :
 
1. Any system with Java version - JDK greater than 1.5 is required.

2. The two ways explained above to run the algorithm, require "javac/java" commands, to compile the sources and run. Please make sure appropriate java JDK and JRE versions are installed and the PATH variables are set.

3. If already generated classes in classes folder are being used in a manual way other than the 2 ways described above, please note that they are compiled with runtime version 57, if the JRE version in any other system is lesser, program would fail to run. In such case please remove all the classes and recompile the java files in the src directory.

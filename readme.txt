EECS 839, Programming project
Sai krishna Teja Damaraju
#3028488

Implementation of MLEM2 algorithm with concept approximations for complete and incomplete data sets.

System requirements : 
Any system with Java version greater than 1.5 is required.
Classes in bin folder are compiled with runtime version 57, if the JRE version in any other system is lesser, program would fail to run. In such case please remove all the classes and recompile the java files.



Please follow the below steps for execution.

1. Copy the EECS839-ProgrammingProject#3028488 to any system that has Java enabled.

2. Execute the below commands.
	
	a. cd - Change directory to "EECS839-ProgrammingProject #3028488"/src
	-Changes directory to Project's Source folder

	b. javac MLEM2Implementation.java
	-Generates all necessary classes.

	c. java MLEM2Implementation
	-Runs the MLEM2Implementation


3. Once the execution begins, console prompts user to enter a valid input file name.
	
	"Please enter a valid input file name with absolute path..."

	-Upon seeing this message, please provide entire path including the file name in the below pattern. 
	"/Users/sdamaraju/Desktop/EECS839/SimpleTest.txt"

4. If the provided file is not valid, console reverts to user, asking to enter the appropriate file name.

5. Once file name is successfully entered, console prompts user for approximation details with below message.
	
	"Please type in 'lower' for lower concept approximation..."

	-As the message says, if your data set is incomplete, and you want to calculate "lower approximation" for the same, then type in "lower" (case in-sensitive), any other input, or even pressing just "enter" key, will calculate upper approximation for the same.

If your data set is "complete" and not missing any attributes, algorithm is efficient enough to understand that approximation is not required and ignores any input provided for approximation.

6. Console then prompts for output file path and name, to print the rules, by showing the below message.

	"Please enter an output file (path) name..."
	
	If the file doesn't exist in the path, the algorithm creates the file and writes the result to the file.
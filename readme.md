Requirements:
----------------
The below are what we used to run the project with a single click in our own systems. If you are more comfortable with other IDEs and most recent Java versions, then you may also use them.
- IntelliJ ([Install](https://www.jetbrains.com/idea/download/))
- JDK 17 ([Install using OpenJDK](https://www.openlogic.com/openjdk-downloads?field_java_parent_version_target_id=807&field_operating_system_target_id=All&field_architecture_target_id=All&field_java_package_target_id=All))

We have not used any third party libraries for this project, so nothing else is required!

How to run?
----------------
- The main method for this project is in MinCostFlow class.
- So after compiling the project, run the main() method in MinCostFlow class.
- When you run it, you are prompted to either run tests or run simulations
- If you choose to run tests, then tests will be run on all the algorithms and results will be displayed
- If you choose to run simulations, then you are asked whether you want to run simulations 1 or simulations 2
- You can choose any of those 2, to see how Simulations are working
- NOTE: When running Simulations2, it takes a bit more time because we are considering high r values. So, please wait for a minute until you get the complete output.

Outputs:
--------------
- You can see the graph files that are generated in minimum-cost-flow\src\main\resources.
- When you run the tests and simulations, the outputs of them can be seen in the console.
- The outputs that you see in the terminal/console are formatted clearly according to the tables, for everyone to understand what is going on.

Graphs
--------------
All the graphs are present in src/main/resources folder of the project.

For testing:
- Used graphs graph-test-1.txt, graph-test-2.txt, graph-test-3.txt

For Simulations:
- Simulations 1 uses graph1.txt to graph8.txt
- Simulations 2 uses graph9.txt to graph28.txt
- In Simulations 2 there are 5 input sets, each of the input set uses the below graphs:
    - Input Set 1 -- graph9.txt to graph12.txt
    - Input Set 2 -- graph13.txt to graph16.txt
    - Input Set 3 -- graph17.txt to graph20.txt
    - Input Set 4 -- graph21.txt to graph24.txt
    - Input Set 5 -- graph25.txt to graph28.txt

Reports:
-------------
The details of tests and simulations are clearly mentioned in the report. Please see the report for more details.
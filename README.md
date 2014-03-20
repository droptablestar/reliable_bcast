<h2>Compilation and execution</h2>
<h4>To build the project: </h4>
   ```
   ant build
   ```
<h4>To build with jar file: </h4>
   ```
   ant jar
   ```
<h4>To create javadocs: </h4>
   ```
   ant doc
   ```
<h4>To test project with 2 instances:</h4>
   ```
   cat data.txt | java -jar dist/rchannel.jar localhost 0
   cat data.txt | java -jar dist/rchannel.jar localhost 1
   ```
<h2>Data generation and testing</h2>
Data generation and evaluation tools can be found in the tests/ directory. This
directory contains two scripts, genTest.py and checkOutput.py, which can be
used to generate test data and check the results. To generate a test file use:
   ```
   python genTest.py <number_of_messages>
   ```
To check the output use:
   ```
   python checkOutput.py <number_of_messages>
   ```
   

Dev. Environment: macOS Sierra Version 10.12.3

# Libraries & Tools
javaparser-core-2.5.0

yUML web service

# How to run Java2UMLParser.jar file. 

1, download Java2UMLParser.jar
https://github.com/laura310/Java2UMLParser/tree/master/out/artifacts/Java2UMLParser_jar

2, download test cases.
https://github.com/laura310/Java2UMLParser/tree/master/new-test-case
You'll need to provide the path to the test cases ON YOUR LAPTOP as parameters when you run the JAR. 

3, run the JAR file to generate class diagram [TWO parameters needed].
(1) in command line, go to the directory where Java2UMLParser.jar is downloaded.

(2) type in command:
$java -jar Java2UMLParser.jar [path_to_test_case] [path_to_where_graph_is_created]

Example command:
$java -jar Java2UMLParser.jar /Users/laurazhou/Desktop/new-test-case/test1 /Users/laurazhou/Desktop/new-test-case/test1.png

in above example, "/Users/laurazhou/Desktop/new-test-case/test1" is the path to source code. "/Users/laurazhou/Desktop/new-test-case/test1.png" is the path where the class diagram will be generated, and the ending "test1.png" is the graph name.


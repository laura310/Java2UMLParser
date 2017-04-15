# Java2UMLParser

# How to run Java2UMLParser.jar file. 

# 1, download Java2UMLParser.jar

You can download Java2UMLParser.jar from current git repo with path:
Java2UMLParser/out/artifacts/Java2UMLParser_jar/Java2UMLParser.jar

# 2, you need test cases.

You'll need to provide the path to the test cases ON YOUR LAPTOP as parameters when you run the JAR. 
If you don't have the test cases, you can download from current git repo.

# 3, run the JAR file.
First of all, in command line, go to the directory where Java2UMLParser.jar is downloaded.

(1) to generate class diagram from source code:
$java -jar Java2UMLParser.jar class [path_to_test_case] [path_to_where_graph_is_created]

for example,

$java -jar Java2UMLParser.jar class /Users/laurazhou/Desktop/new-test-case/test1 /Users/laurazhou/Desktop/new-test-case/test1.png

in above example, "/Users/laurazhou/Desktop/new-test-case/test1" is the path to source code. "/Users/laurazhou/Desktop/new-test-case/test1.png" is the path where the class diagram is generated, and the ending "test1.png" is the graph name.

(2) to generate sequence diagram from source code:
$java -jar Java2UMLParser.jar seq [path_to_test_case] [path_to_where_graph_is_created] main Main

for example, 

$java -jar Java2UMLParser.jar seq /Users/laurazhou/Desktop/new-test-case/sequence /Users/laurazhou/Desktop/new-test-case/sequence.png main Main

in above example, "/Users/laurazhou/Desktop/new-test-case/sequence" is the path to source code, "/Users/laurazhou/Desktop/new-test-case/sequence.png" is the path where the sequence diagram is generated, and the ending "sequence.png" is the graph name.

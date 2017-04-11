/**
 * Created by laurajoe on 2/19/17.
 */
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import com.github.javaparser.ast.*;
import java.io.FileInputStream;
import com.github.javaparser.*;
import com.github.javaparser.ast.body.*;
import java.util.*;

public class SrcParserClass {

    private String parsedCode;                              // code that can be recognized by yUML
    private String srcPath;                              // provided by user as parameter
    private String umlGraphPath;                            //provided by user as parameter
    private HashMap<String, Boolean> mapIfInterface;        // to tell if a certain .java is an interface or not
    private HashMap<String, String> classRelationMap;       // the relationship between different .java files
    private ArrayList<CompilationUnit> compilationUnits;    // one .java file is one CompilationUnit


    /**
     * Constructor
     * @param srcPath
     */
    public SrcParserClass(String srcPath, String umlGraphPath) {

        this.srcPath = srcPath;
        this.umlGraphPath = umlGraphPath;

        mapIfInterface = new HashMap<>();
        parsedCode = "";
        classRelationMap = new HashMap<>();
        compilationUnits = new ArrayList<>();
    }


    public void run() {
        getCompilationUnits(srcPath);

        checkIfInterface(compilationUnits);

        for (CompilationUnit cu : compilationUnits) {
            CompilationUnitParser cuParser = new CompilationUnitParser(cu, mapIfInterface, classRelationMap);
            parsedCode += cuParser.parse();
        }

        parsedCode += addClassAssociations();
        parsedCode = parsedCode.substring(0, parsedCode.length()-1); //get rid of ending ","
        System.out.println("Parsed Code: " + parsedCode); // FOR DEBUG.

        UMLGenerator graphGenerator = new UMLGenerator(parsedCode, umlGraphPath);
        graphGenerator.generateGraph();
    }



    /***
     * Get a list of CompilationUnits from the given file path and put it into "compilationUnits".
     *
     * @param filePath
     */
    private void getCompilationUnits(String filePath) {
        File file = new File(filePath);

        for(File f : file.listFiles()) {
            if(f.isFile() && f.getName().endsWith(".java")) {
                FileInputStream in = null;
                try {

                    in = new FileInputStream(f);
                    CompilationUnit cu = JavaParser.parse(in);
                    compilationUnits.add(cu);

                } catch (FileNotFoundException e) {
                    System.out.println("FileNotFoundException from getCompilationUnits method");
                    e.printStackTrace();
                } catch (Exception e) {
                    System.out.println("Exception from getCompilationUnits method");
                    e.printStackTrace();
                } finally {
                    try {

                        in.close();

                    } catch (IOException e) {
                        System.out.println("IOException from getCompilationUnits method");
                        e.printStackTrace();
                    }
                }
            }
        }
    }


    /***
     * For every .java file, check if it is an Interface or not.
     *
     * @param compilationUnits
     */
    private void checkIfInterface(ArrayList<CompilationUnit> compilationUnits) {
        for(CompilationUnit cu : compilationUnits) {
            List<TypeDeclaration> typeDeclarations = cu.getTypes();

            // actually, we assume there's only one TypeDeclaration in one CompilationUnit
            for(TypeDeclaration td : typeDeclarations) {
                mapIfInterface.put(td.getName(), ((ClassOrInterfaceDeclaration) td).isInterface());
            }
        }
    }



    private String addClassAssociations() {

        String result = "";
        Set<String> keys = classRelationMap.keySet(); // get all keys

        for (String key : keys) {

            String[] classes = key.split("-");

            if(classes[0].compareTo(classes[1]) < 0) {   // to get rid of duplicate relations between classes
                if (mapIfInterface.get(classes[0])) result += "[«interface»;" + classes[0] + "]";
                else result += "[" + classes[0] + "]";

                result += classRelationMap.get(key); // Add connection

                if (mapIfInterface.get(classes[1])) result += "[«interface»;" + classes[1] + "]";
                else result += "[" + classes[1] + "]";

                result += ",";
            }
        }
        return result;
    }


}

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.expr.MethodCallExpr;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by laurazhou on 4/4/17.
 */
public class SrcParserSeq {

    private String parsedCode;

    private String folderName;
    private String umlGraphName;
    private String calleeFuncName;
    private String calleeClassName;

    private ArrayList<CompilationUnit> compilationUnits;
    private HashMap<String, String> methodClassMap;
    private HashMap<String, ArrayList<MethodCallExpr>> methodExprsMap;

    /**
     * Constructor
     * @param folderName
     */
    public SrcParserSeq(String folderName, String umlGraphName, String calleeFuncName, String calleeClassName) {

        parsedCode = "@startuml\n";  // conform to plantuml format

        this.folderName = folderName;
        this.umlGraphName = umlGraphName;
        this.calleeFuncName = calleeFuncName;
        this.calleeClassName = calleeClassName;

        compilationUnits = new ArrayList<>();
        methodClassMap = new HashMap<>();
        methodExprsMap = new HashMap<>();
    }

    public void run() {
        // step 1:  to get the project root path.
        String projRootPath = new File("f").getAbsolutePath();
        projRootPath = projRootPath.substring(0, projRootPath.length()-2); //to get rid of the ending "/f"

        // step 2:  to parse the java source code.
        getCompilationUnits(projRootPath + "/" + folderName);
        //populateMaps();

        System.out.println(compilationUnits);
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

    //populate methodClassMap<methodName, className> and methodExprsMap<methodName, methodExpressions>
    private void populateMaps() {

    }
}

import net.sourceforge.plantuml.SourceStringReader;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.*;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import net.sourceforge.plantuml.core.DiagramDescription;

import java.io.*;
import java.util.*;


/**
 * Created by laurazhou on 4/4/17.
 */
public class SrcParserSeq {

    private String parsedCode;

    private String srcPath;
    private String umlGraphPath;
    private String startFuncName;
    private String startClassName;

    private ArrayList<CompilationUnit> compilationUnits;
    private HashMap<String, String> methodClassMap;         //methodName : corresponding className
    private HashMap<String, ArrayList<MethodCallExpr>> methodExprsMap;    //methodName : methodCallExpressions

    /**
     * Constructor
     * @param srcPath
     */
    public SrcParserSeq(String srcPath, String umlGraphPath, String startFuncName, String startClassName) {

        parsedCode = "";

        this.srcPath = srcPath;
        this.umlGraphPath = umlGraphPath;
        this.startFuncName = startFuncName;
        this.startClassName = startClassName;

        compilationUnits = new ArrayList<>();
        methodClassMap = new HashMap<>();
        methodExprsMap = new HashMap<>();
    }

    public void run() {

        getCompilationUnits(srcPath);
        populateMaps();

        parsedCode = "@startuml\n";                     // conform to plantuml format
        parsedCode += "actor Actor #black\n";
        parsedCode += "activate " + methodClassMap.get(startFuncName) + "\n";

        parse(startFuncName);

        parsedCode += "@enduml";
        System.out.println("Parsed Code for Sequence Diagram: \n" + parsedCode);

        generateSeqDiagram(parsedCode, umlGraphPath);

    }

    public void parse(String callerFuncName) {
        String callerFuncClass = methodClassMap.get(callerFuncName);
        String calleeFuncName = "";
        String calleeFuncClass = "";

        for(MethodCallExpr methodCallExpr : methodExprsMap.get(callerFuncName)) {
            calleeFuncName = methodCallExpr.getName();

            if(methodClassMap.containsKey(calleeFuncName)) {
                calleeFuncClass = methodClassMap.get(calleeFuncName);

                parsedCode = parsedCode + callerFuncClass + " -> " + calleeFuncClass + " : " + methodCallExpr.toStringWithoutComments() + "\n";
                parsedCode = parsedCode + "activate " + calleeFuncClass + "\n";

                parse(calleeFuncName);

                parsedCode = parsedCode + calleeFuncClass + " -->> " + callerFuncClass + "\n";
                parsedCode = parsedCode + "deactivate " + calleeFuncClass + "\n";
            }
        }
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
                        System.out.println("IOException from closing FileInputStream in getCompilationUnits method");
                        e.printStackTrace();
                    } catch (Exception e) {
                        System.out.println("Exception from closing FileInputStream in getCompilationUnits method");
                        e.printStackTrace();
                    }
                }
            }
        }
    }


    /***
     * Populate methodClassMap<methodName, className> and methodExprsMap<methodName, methodExpressions>.
     *
     */
    private void populateMaps() {
        String className = "";

        for(CompilationUnit compilationUnit : compilationUnits) {

            List<TypeDeclaration> typeDeclarations = compilationUnit.getTypes();
            for(TypeDeclaration typeDeclaration : typeDeclarations) {

                className = typeDeclaration.getName();

                for(BodyDeclaration bodyDeclaration : typeDeclaration.getMembers()) {

                    if(bodyDeclaration instanceof MethodDeclaration) {
                        ArrayList<MethodCallExpr> methodCallExprs = new ArrayList<>();

                        for(Node nodeMethodDeclChild : ((MethodDeclaration) bodyDeclaration).getChildrenNodes()) {

                            if(nodeMethodDeclChild instanceof BlockStmt) {
                                for(Node nodeBlockStmtChild : ((BlockStmt) nodeMethodDeclChild).getChildrenNodes()) {

                                    if(nodeBlockStmtChild instanceof ExpressionStmt && ((ExpressionStmt) nodeBlockStmtChild).getExpression() instanceof MethodCallExpr) {
                                        methodCallExprs.add((MethodCallExpr) ((ExpressionStmt) nodeBlockStmtChild).getExpression());
                                    }
                                }
                            }
                        }

                        methodExprsMap.put(((MethodDeclaration) bodyDeclaration).getName(), methodCallExprs);
                        methodClassMap.put(((MethodDeclaration) bodyDeclaration).getName(), className);
                    }
                }
            }
        }
    }


    /***
     * Generate Sequence Diagram based on parsedcode.
     *
     */
    public DiagramDescription generateSeqDiagram(String parsedCode, String umlGraphPath) {
        OutputStream out = null;
        DiagramDescription diagramDescription = null;

        try {

            out = new FileOutputStream(umlGraphPath);
        } catch (FileNotFoundException e) {
            System.out.println("FileNotFoundException in generateSeqDiagram method: " + e.getMessage());
            e.printStackTrace();
        }

        SourceStringReader reader = new SourceStringReader(parsedCode);

        try {

            diagramDescription = reader.generateImage(out);
        } catch (IOException e) {
            System.out.println("IOException in generateSeqDiagram method: " + e.getMessage());
            e.printStackTrace();
        }

        return diagramDescription;
    }
}




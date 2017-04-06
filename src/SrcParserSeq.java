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

    private String folderName;
    private String umlGraphName;
    private String startFuncName;
    private String startClassName;

    private ArrayList<CompilationUnit> compilationUnits;
    private HashMap<String, String> methodClassMap;         //methodName : corresponding className
    private HashMap<String, ArrayList<MethodCallExpr>> methodExprsMap;    //methodName : methodCallExpressions

    /**
     * Constructor
     * @param folderName
     */
    public SrcParserSeq(String folderName, String umlGraphName, String startFuncName, String startClassName) {

        parsedCode = "";

        this.folderName = folderName;
        this.umlGraphName = umlGraphName;
        this.startFuncName = startFuncName;
        this.startClassName = startClassName;

        compilationUnits = new ArrayList<>();
        methodClassMap = new HashMap<>();
        methodExprsMap = new HashMap<>();
    }

    public void run() {
        // step 1:  to get the project root path.
        String projRootPath = new File("f").getAbsolutePath();
        projRootPath = projRootPath.substring(0, projRootPath.length()-2); //to get rid of the ending "/f"
        String umlGraphPath = projRootPath + "/" + umlGraphName + ".png";

        // step 2:  to parse the java source code.
        getCompilationUnits(projRootPath + "/" + folderName);
        populateMaps();

        System.out.println("******************************** methodExprsMap\n********************************\n");
        System.out.println(methodExprsMap);
        System.out.println("******************************** methodExprsMap\n********************************\n");

        System.out.println("******************************** methodClassMap\n********************************\n");
        System.out.println(methodClassMap);
        for (Map.Entry entry : methodClassMap.entrySet()) {
            System.out.println("\n***" + entry.getKey() + ", " + entry.getValue());
        }
        System.out.println("******************************** methodClassMap\n********************************\n");

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
                        System.out.println("IOException from getCompilationUnits method");
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    //populate methodClassMap<methodName, className> and methodExprsMap<methodName, methodExpressions>
    private void populateMaps() {
        String className = "";


        for(CompilationUnit compilationUnit : compilationUnits) {
            System.out.println("********************************Begin: .java file\n********************************");
            System.out.println("/******/1 - compilationUnit.");
            System.out.println(compilationUnit);
            System.out.println("********************************End: .java file\n********************************\n");


            List<TypeDeclaration> typeDeclarations = compilationUnit.getTypes();
            for(TypeDeclaration typeDeclaration : typeDeclarations) {
                System.out.println("/******/2 - TypeDeclaration.\n" + typeDeclaration);

                className = typeDeclaration.getName();
                System.out.println("$$$$$$$$$$$$$$$" + className);

                for(BodyDeclaration bodyDeclaration : typeDeclaration.getMembers()) {
                    System.out.println("/******/3 - BodyDeclaration.\n" + bodyDeclaration);

                    if(bodyDeclaration instanceof MethodDeclaration) {
                        ArrayList<MethodCallExpr> methodCallExprs = new ArrayList<>();
                        
                        System.out.println("/******/4 - bodyDeclaration as MethodDeclaration.\n" + bodyDeclaration);

                        for(Node nodeMethodDeclChild : ((MethodDeclaration) bodyDeclaration).getChildrenNodes()) {
                            System.out.println("/******/5 - node of ((MethodDeclaration) bodyDeclaration).getChildrenNodes().");

                            System.out.println(nodeMethodDeclChild + "\n" + nodeMethodDeclChild);

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




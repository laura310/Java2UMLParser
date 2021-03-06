import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.type.ClassOrInterfaceType;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * Created by laurajoe on 2/20/17.
 */
public class CompilationUnitParser {

    CompilationUnit compilationUnit;
    private String parsedCode;
    private HashMap<String, Boolean> mapIfInterface;    // to tell if a certain .java is an interface or not
    private HashMap<String, String> classAssociationMap;  // the relationship between different .java files

    private String parsedClassInfo;
    private String className;
    private String methods;     // parsed code for methods
    private String fields;      // parsed code for fields
    private String relations;   // parsed code for relations
    ArrayList<String> makeGetterSetterPublicAttri;

    /**
     * Constructor
     */
    public CompilationUnitParser(CompilationUnit cu, HashMap<String, Boolean> mapIfInterface, HashMap<String, String> classAssociationMap) {
        this.compilationUnit = cu;
        this.mapIfInterface = mapIfInterface;
        this.classAssociationMap = classAssociationMap;

        parsedCode = "";
        parsedClassInfo = "";
        className = "";
        methods = "";
        fields = "";
        relations = ",";
        makeGetterSetterPublicAttri = new ArrayList<>();
    }

    /***
     *
     * @return parsed code of a CompilationUnit
     */
    public String parse() {

        // assuming only one class in one .java file
        ClassOrInterfaceDeclaration coiDecl = (ClassOrInterfaceDeclaration) compilationUnit.getTypes().get(0);

        // parse class info
        if (coiDecl.isInterface())   parsedClassInfo = "[" + "<interface>;";
        else                         parsedClassInfo = "[";
        parsedClassInfo += coiDecl.getName();
        className = coiDecl.getName();

        for(BodyDeclaration bd : coiDecl.getMembers()) {
            if (bd instanceof MethodDeclaration) {
                populateMakeGetterSetterPublicAttri(bd, coiDecl);
            }
        }

        for (BodyDeclaration bd : coiDecl.getMembers()) {

            if (bd instanceof FieldDeclaration) {
                parseFieldDeclaration(bd);
            }

            else if (bd instanceof ConstructorDeclaration) {
                parseConstructorDeclaration(bd, coiDecl);
            }

            else if (bd instanceof MethodDeclaration) {
                parseMethodDeclaration(bd, coiDecl);
            }
        }


        // check if extends from other class
        if (coiDecl.getExtends() != null) {
            for(ClassOrInterfaceType coiTmp : coiDecl.getExtends()) {
                if(coiTmp != null) {
                    relations += "[" + className + "] " + "-^ " + coiDecl.getExtends().toString();
                    relations += ",";
                }
            }
        }

        // check implement from some interface
        if (coiDecl.getImplements() != null) {
            for (ClassOrInterfaceType classOrInterfaceType : coiDecl.getImplements()) {
                relations += "[" + className + "] " + "-.-^ " + "[" + "<interface>;" + classOrInterfaceType + "]";
                relations += ",";
            }
        }

        // Combine ParsedClassInfo, methods and fields
        parsedCode += parsedClassInfo;

        parsedCode += "| ";
        if (fields.length() != 0) {
            parsedCode += fields;
        }

        parsedCode += "| ";
        if (methods.length() != 0) {
            parsedCode += methods;
        }

        parsedCode += "],";

        return parsedCode;
    }

    /**
     *
     * @return relations for this .java file.
     */
    public String getRelations() {
        return relations;
    }


    /**
     * Parsing Fields, only include private and public attributes.
     * For a FieldDeclaration, there are: scope, fieldClassName, and variableName.
     */
    private void parseFieldDeclaration(BodyDeclaration bd) {

        FieldDeclaration fd = ((FieldDeclaration) bd);

        String scope = convertAccessModifierToSym(bd.toStringWithoutComments().substring(0, bd.toStringWithoutComments().indexOf(" ")));
        String fieldClassName = convert2Parenthesis(fd.getChildrenNodes().get(0).toString());

        String variableName = fd.getChildrenNodes().get(1).toString();

        // i.e. private Collection<Observer> observers = new ArrayList<Observer>() ;
        if (variableName.contains("="))
            variableName = variableName.split(" ")[0];

        // if this private attribute can be accessed by public getters/setters, it should be public attribute in UML
        if (makeGetterSetterPublicAttri.contains(variableName.toLowerCase()) && scope.equals("-"))
            scope = "+";

        /** populate the classAssociationMap **/
        String relationClass = "";
        boolean getRelationMultiple = false;

        if (fieldClassName.contains("(")) {
            getRelationMultiple = true;
            String potentialRelationClass = fieldClassName.substring(fieldClassName.indexOf("(") + 1, fieldClassName.indexOf(")"));
            relationClass = mapIfInterface.containsKey(potentialRelationClass) ? potentialRelationClass : relationClass;

        } else if (mapIfInterface.containsKey(fieldClassName))
            relationClass = fieldClassName;

        if (relationClass.length() > 0 && mapIfInterface.containsKey(relationClass)) {
            String relation = getRelationMultiple ? "-*" : "-";
            classAssociationMap.put(this.className + "-" + relationClass, relation);
        }


        if ((scope == "+" || scope == "-") && !mapIfInterface.containsKey(relationClass)) { //get rid of unnecessary fields representation
            fields += scope + " " + variableName + " : " + fieldClassName + "; ";
        }
    }


    /**
     * Parsing Constructors.
     * Constructors are treated as public methods in UML graph.
     */
    private void parseConstructorDeclaration(BodyDeclaration bd, ClassOrInterfaceDeclaration coiDecl) {
        ConstructorDeclaration cd = (ConstructorDeclaration) bd;

        if (cd.getDeclarationAsString().startsWith("public") && !coiDecl.isInterface()) {

            methods += "+ " + cd.getName() + "("; // methods prefix
            for (Object childNode : cd.getChildrenNodes()) {
                if (childNode instanceof Parameter) {
                    parseParameterInMethods(childNode, coiDecl);
                }
            }
            methods += ");"; // methods postfix
        }
    }


    /**
     * Parsing Methods.
     * Public Setters/Getters should be interpreted as "Public Attributes".
     */
    private void parseMethodDeclaration(BodyDeclaration bd, ClassOrInterfaceDeclaration coiDecl) {

        MethodDeclaration md = ((MethodDeclaration) bd);

        // Get only public methods
        if (md.getDeclarationAsString().startsWith("public")) {

            /** Ignoring getters and setters **/

            if (  !(md.getName().startsWith("get") || md.getName().startsWith("set"))  ) {

                methods += "+ " + md.getName() + "(";  // methods prefix
                for (Object childNode : md.getChildrenNodes()) {

                    if (!coiDecl.isInterface() && childNode instanceof Parameter) {
                        parseParameterInMethods(childNode, coiDecl);

                    } else {
                        String[] methodBodys = childNode.toString().split(" ");

                        for (String methodBody : methodBodys) {

                            if (mapIfInterface.containsKey(methodBody) && !mapIfInterface.get(className)) {
                                relations += "[" + className + "] -.-> [";
                                if (mapIfInterface.get(methodBody))
                                    relations += "<interface>;" + methodBody + "]";
                                else
                                    relations += "[" + methodBody + "]";
                                relations += ",";
                            }
                        }
                    }
                }
                methods += ") : " + md.getType() + ";";  //methods postfix
            }
        }
    }


    /***
     * Populate the HashMap: MakeGetterSetterPublicAttri to make private attribute public if there's corresponding getters / setters for that attribute.
     * @param bd
     * @param coiDecl
     */
    private void populateMakeGetterSetterPublicAttri(BodyDeclaration bd, ClassOrInterfaceDeclaration coiDecl) {
        MethodDeclaration md = ((MethodDeclaration) bd);

        if (md.getDeclarationAsString().startsWith("public") && md.getName().startsWith("get") || md.getName().startsWith("set")) {
            String varName = md.getName().substring(3);
            makeGetterSetterPublicAttri.add(varName.toLowerCase());
        }

    }

    /** only consider dependencies to interfaces **/
    private void parseParameterInMethods(Object childNode, ClassOrInterfaceDeclaration coiDecl) {

        String paramClass = ((Parameter) childNode).getChildrenNodes().get(1).toString();
        String paramName = ((Parameter) childNode).getChildrenNodes().get(0).toString();
        methods += paramName + " : " + paramClass;

        if (mapIfInterface.containsKey(paramClass)) {
            String dependencyToInterface = "[<interface>;" + paramClass + "]";

            /** to avoid duplicate "------->" (Dependency) in graph **/
            if(!relations.contains("[" + className + "] -.->" + dependencyToInterface)) {

                relations += "[" + className + "] -.->";

                if (mapIfInterface.get(paramClass))
                    relations += dependencyToInterface;
            }
        }
        relations += ",";
    }

    /**
     * Avoid confusing with yUML language of "[", "]", "<<", and ">>".
     *
     * replace "[" and "<" in a String with "("
     * replace "]" and ">" in a String with ")"
     *
     * @param string
     * @return processed string
     */
    private String convert2Parenthesis(String string) {
        string = string.replace("[", "(");
        string = string.replace("]", ")");
        string = string.replace("<", "(");
        string = string.replace(">", ")");

        return string;
    }


    /**
     * In UML: public: "+"; private: "-"; protected: "#".
     *
     * @param accessModifier
     * @return
     */
    private String convertAccessModifierToSym(String accessModifier) {
        switch (accessModifier) {
            case "private":
                return "-";
            case "public":
                return "+";
            case "protected":
                return "#";
            default:
                return "";
        }
    }

}

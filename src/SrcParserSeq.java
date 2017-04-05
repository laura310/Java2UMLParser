import com.github.javaparser.ast.CompilationUnit;

import java.util.ArrayList;

/**
 * Created by laurazhou on 4/4/17.
 */
public class SrcParserSeq {

    private String parsedCode;
    private String folderName;
    private String umlGraphName;

    private ArrayList<CompilationUnit> compilationUnits;


    /**
     * Constructor
     * @param folderName
     */
    public SrcParserSeq(String folderName, String umlGraphName) {

        this.folderName = folderName;
        this.umlGraphName = umlGraphName;

        parsedCode = "";
    }
}

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
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
}

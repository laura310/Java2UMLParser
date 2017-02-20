/**
 * Created by laurajoe on 2/19/17.
 */
import java.io.File;
import java.util.ArrayList;
import com.github.javaparser.ast.*;


public class SrcParser {

    String folderName;
//    String umlGraph;
    ArrayList<CompilationUnit> compilationUnits = new ArrayList<CompilationUnit>();

    public SrcParser(String folderName) {
        this.folderName = folderName;
//        this.umlGraph = umlGraphName;
    }

    public void run() {

        String projRootPath = new File("f").getAbsolutePath();
        projRootPath = projRootPath.substring(0, projRootPath.length()-2); //to get rid of the ending "/f"
//        System.out.println("url = " + projRootPath);  //DEBUG


        File folder = new File(projRootPath + "/" + folderName);
        for(File f : folder.listFiles()) {
            System.out.println(f.getName());
        }
    }
}

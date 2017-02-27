import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by laurajoe on 2/19/17.
 */
public class UMLGenerator {
    private String parsedCode;
    private String graphNamePath;

    public UMLGenerator(String parsedCode, String graphNamePath) {
        this.parsedCode = parsedCode;
        this.graphNamePath = graphNamePath;
    }

    public void generateGraph() {
        try {
            URL url = new URL("https://yuml.me/diagram/scruffy/class/draw/" + parsedCode + ".png");
            HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
            int responseCode = httpConn.getResponseCode();



        } catch (MalformedURLException e) {
            System.out.println("MalformedURLException from creating URL in generateGraph() method.");
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("IOException from creating HttoURLConnection in generateGraph() method.");
            e.printStackTrace();
        }
    }
}

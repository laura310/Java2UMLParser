
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by laurajoe on 2/19/17.
 */
public class UMLGenerator {
    private static final int BUFFER_SIZE = 4096;

    private String parsedCode;
    private String graphNamePath;

    public UMLGenerator(String parsedCode, String graphNamePath) {
        this.parsedCode = parsedCode;
        this.graphNamePath = graphNamePath;
    }

    public void generateGraph() {

        try {

            URL url = new URL("https://yuml.me/diagram/plain/class/" + parsedCode + ".png");

            HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();

            InputStream in = urlConn.getInputStream();
            OutputStream out = new FileOutputStream(graphNamePath);

            byte[] buffer = new byte[BUFFER_SIZE];
            int byteRead;
            while ((byteRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, byteRead);
            }

            in.close();
            out.close();

        } catch (MalformedURLException e) {

            System.out.println("MalformedURLException from creating URL in generateGraph() method.");
            e.printStackTrace();
        } catch (IOException e) {

            System.out.println("IOException from opening stream in generateGraph() method.");
            e.printStackTrace();
        }

    }
}

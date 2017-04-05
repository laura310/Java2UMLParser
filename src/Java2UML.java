/**
 * Created by laurajoe on 2/19/17.
 */
public class Java2UML {
    public static void main(String[] args) {
        if(args[0].equals("class")) {
            SrcParserClass srcParserClass = new SrcParserClass(args[1], args[2]);
            srcParserClass.run();
        }
    }
}

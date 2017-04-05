/**
 * Created by laurajoe on 2/19/17.
 */
public class Java2UML {
    public static void main(String[] args) {
        SrcParserClass srcParserClass = new SrcParserClass(args[0], args[1]);
        srcParserClass.run();
    }
}

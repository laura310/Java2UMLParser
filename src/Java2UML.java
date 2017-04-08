/**
 * Created by laurajoe on 2/19/17.
 */
public class Java2UML {
    public static void main(String[] args) {
        if(args[0].equals("class")) {

            SrcParserClass srcParserClass = new SrcParserClass(args[1], args[2]);
            srcParserClass.run();
        } else if(args[0].equals("seq")) {

            SrcParserSeq srcParserSeq = new SrcParserSeq(args[1], args[2], args[3], args[4]);
            srcParserSeq.run();
        } else {

            System.out.println("First Parameter should be \"class\" (Class Diagram) or \"seq\" (Sequence Diagram)");
        }
    }
}

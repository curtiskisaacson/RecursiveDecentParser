import components.map.Map;
import components.program.Program;
import components.program.Program1;
import components.queue.Queue;
import components.simplereader.SimpleReader;
import components.simplereader.SimpleReader1L;
import components.simplewriter.SimpleWriter;
import components.simplewriter.SimpleWriter1L;
import components.statement.Statement;
import components.utilities.Reporter;
import components.utilities.Tokenizer;

/**
 * Layered implementation of secondary method {@code parse} for {@code Program}.
 *
 * @author Curtis Isaacson
 *
 */
public final class Program1Parse1 extends Program1 {

    /*
     * Private members --------------------------------------------------------
     */

    /**
     * Parses a single BL instruction from {@code tokens} returning the
     * instruction name as the value of the function and the body of the
     * instruction in {@code body}.
     *
     * @param tokens
     *            the input tokens
     * @param body
     *            the instruction body
     * @return the instruction name
     * @replaces body
     * @updates tokens
     * @requires <pre>
     * [<"INSTRUCTION"> is a prefix of tokens]  and
     *  [<Tokenizer.END_OF_INPUT> is a suffix of tokens]
     * </pre>
     * @ensures <pre>
     * if [an instruction string is a proper prefix of #tokens]  and
     *    [the beginning name of this instruction equals its ending name]  and
     *    [the name of this instruction does not equal the name of a primitive
     *     instruction in the BL language] then
     *  parseInstruction = [name of instruction at start of #tokens]  and
     *  body = [Statement corresponding to statement string of body of
     *          instruction at start of #tokens]  and
     *  #tokens = [instruction string at start of #tokens] * tokens
     * else
     *  [report an appropriate error message to the console and terminate client]
     * </pre>
     */
    private static String parseInstruction(Queue<String> tokens,
            Statement body) {
        assert tokens != null : "Violation of: tokens is not null";
        assert body != null : "Violation of: body is not null";
        assert tokens.length() > 0 && tokens.front().equals("INSTRUCTION") : ""
                + "Violation of: <\"INSTRUCTION\"> is proper prefix of tokens";
        //dequeue INSTRUCTION
        tokens.dequeue();
        //dequeue name of instruction
        String instrName = tokens.dequeue();
        //dequeue IS
        tokens.dequeue();
        //make sure name of instruction isn't a primitive call
        if (instrName.equals("turnright") || instrName.equals("skip")
                || instrName.equals("infect") || instrName.equals("turnleft")
                || instrName.equals("move")) {

            Reporter.assertElseFatalError(false,
                    "Instruction name cannot be primitive");
        }
        //parses statement block
        body.parseBlock(tokens);
        //dequeue END
        tokens.dequeue();
        //dequeue ending instruction name
        String endIntsrName = tokens.dequeue();
        //checks if starting and ending name of instruction is the same
        if (!instrName.equals(endIntsrName)) {
            Reporter.assertElseFatalError(false,
                    "starting and ending instruction name do not match");
        }
        //return instruction name
        return instrName;
    }

    /*
     * Constructors -----------------------------------------------------------
     */

    /**
     * No-argument constructor.
     */
    public Program1Parse1() {
        super();
    }

    /*
     * Public methods ---------------------------------------------------------
     */

    @Override
    public void parse(SimpleReader in) {
        assert in != null : "Violation of: in is not null";
        assert in.isOpen() : "Violation of: in.is_open";
        Queue<String> tokens = Tokenizer.tokens(in);
        this.parse(tokens);
    }

    @Override
    public void parse(Queue<String> tokens) {
        assert tokens != null : "Violation of: tokens is not null";
        assert tokens.length() > 0 : ""
                + "Violation of: Tokenizer.END_OF_INPUT is a suffix of tokens";
        //dequeue PROGRAM
        tokens.dequeue();
        //dequeue name of program name
        String startprogName = tokens.dequeue();
        //dequeue IS
        tokens.dequeue();
        this.replaceName(startprogName);

        Map<String, Statement> c = this.newContext();
        //checks if the block is an instruction
        while (tokens.front().equals("INSTRUCTION")) {
            Statement instrBody = this.newBody();
            String sName = parseInstruction(tokens, instrBody);
            //adds instruction if not already in the this's context
            if (!c.hasKey(sName)) {
                c.add(sName, instrBody);
            } else {
                Reporter.assertElseFatalError(false,
                        "Instruction already exists");
            }
        }
        this.replaceContext(c);
        Statement sBody = this.newBody();
        //dequeue BEGIN
        tokens.dequeue();
        sBody.parseBlock(tokens);
        this.replaceBody(sBody);
        //dequeue END
        tokens.dequeue();
        //dequeue ending name of program
        String endProgName = tokens.dequeue();
        //check if identifier is same at beginning and end of the program
        if (!endProgName.equals(startprogName)) {
            Reporter.assertElseFatalError(false,
                    "Finishing name does not match starting name");
        }
        //make sure only end of input is the remaining string in the token
        if (tokens.length() != 1) {
            Reporter.assertElseFatalError(false,
                    "Can't have any more code after the END of input");
        }
    }

    /*
     * Main test method -------------------------------------------------------
     */

    /**
     * Main method.
     *
     * @param args
     *            the command line arguments
     */
    public static void main(String[] args) {
        SimpleReader in = new SimpleReader1L();
        SimpleWriter out = new SimpleWriter1L();
        /*
         * Get input file name
         */
        out.print("Enter valid BL program file name: ");
        String fileName = in.nextLine();
        /*
         * Parse input file
         */
        out.println("*** Parsing input file ***");
        Program p = new Program1Parse1();
        SimpleReader file = new SimpleReader1L(fileName);
        Queue<String> tokens = Tokenizer.tokens(file);
        file.close();
        p.parse(tokens);
        /*
         * Pretty print the program
         */
        out.println("*** Pretty print of parsed program ***");
        p.prettyPrint(out);

        in.close();
        out.close();
    }

}

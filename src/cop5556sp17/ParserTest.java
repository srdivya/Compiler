package cop5556sp17;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.Timeout;

import cop5556sp17.Parser.SyntaxException;
import cop5556sp17.Scanner.IllegalCharException;
import cop5556sp17.Scanner.IllegalNumberException;


public class ParserTest {

	@Rule
	public ExpectedException thrown = ExpectedException.none();
	@SuppressWarnings("deprecation")
	@Rule
    public Timeout globalTimeout = new Timeout(1000);
	@Test
	public void testFactor0() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "abc";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		parser.factor();
	}

	@Test
	public void testArg() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "  (3,5) ";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		//System.out.println(scanner);
		Parser parser = new Parser(scanner);
        parser.arg();
	}

	@Test
	public void testArgerror() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "  (3,) ";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		thrown.expect(Parser.SyntaxException.class);
		parser.arg();
	}


	@Test
	public void testProgram0() throws IllegalCharException, IllegalNumberException, SyntaxException{
		String input = "prog0 {}";
		Parser parser = new Parser(new Scanner(input).scan());
		parser.parse();
	}
	@Test
	public void testProgram1() throws IllegalCharException, IllegalNumberException, SyntaxException{
		String input = "prog0 {integer a sleep x*true%b+c>b+c;while(a<b){blur|->hide;}}";
		Parser parser = new Parser(new Scanner(input).scan());
		parser.parse();
	}
	@Test
	public void testTerm() throws IllegalCharException,IllegalNumberException, SyntaxException{
		String input = "";
		Parser parser = new Parser(new Scanner(input).scan());
		thrown.expect(Parser.SyntaxException.class);
		parser.term();
	}
	@Test
	public void testProgram() throws IllegalCharException,IllegalNumberException, SyntaxException
	{
		String input = "prog{i <- 0;\n while(i < 10){\n if( i % 2 == 0){\n j <- i / 2; \n show(j);\n }\n i <- i + 1;\n}}";
		Parser parser = new Parser(new Scanner(input).scan());
		thrown.expect(Parser.SyntaxException.class);
		parser.parse();
	}
	@Test
	public void testblock() throws IllegalCharException,IllegalNumberException, SyntaxException
	{
		String input = "{boolean b if(a==b){x<-a!=b;}}";
		Parser parser = new Parser(new Scanner(input).scan());
		parser.block();
	}
	@Test
	public void testStatement() throws IllegalCharException,IllegalNumberException, SyntaxException
	{
		String input = "show(screenheight/screenwidth | false <=10) -> hide |-> xloc(a<b);";
		Parser parser = new Parser(new Scanner(input).scan());
		parser.statement();
	}
	@Test
	public void testArgerror1() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "()";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		thrown.expect(Parser.SyntaxException.class);
		parser.arg();
	}
	@Test
	public void testChain() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "a->gray(true,b)width(6)|->frame";
		Parser parser = new Parser(new Scanner(input).scan());
		parser.chain();
	}
	@Test
	public void validFactors() throws IllegalCharException,IllegalNumberException,SyntaxException{
		String input = "1234 abcd true false";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		parser.factor();
	}

	@Test
	public void invalidFactors() throws IllegalCharException,IllegalNumberException,SyntaxException{
		String input = ">";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		thrown.expect(Parser.SyntaxException.class);
		parser.factor();
	}

	@Test
	public void validElem() throws IllegalCharException,IllegalNumberException,SyntaxException{
		String input = "abcd * 1234";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		parser.elem();
	}

	@Test
	public void invalidElem() throws IllegalCharException,IllegalNumberException,SyntaxException{
		String input = "abcd / ";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		thrown.expect(Parser.SyntaxException.class);
		parser.elem();
	}

	@Test
	public void validTerm() throws IllegalCharException,IllegalNumberException,SyntaxException{
		String input = "abcd * 1234 + abcd * 1234";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		parser.term();
	}

	@Test
	public void invalidTerm() throws IllegalCharException,IllegalNumberException,SyntaxException{
		String input = "abcd * 1234 + abcd / ";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		thrown.expect(Parser.SyntaxException.class);
		parser.term();
	}

	@Test
	public void validExpression() throws IllegalCharException,IllegalNumberException,SyntaxException{
		String input = "abcd * 1234 + abcd * 1234 != abcd * 1234 + abcd * 1234";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		parser.expression();
	}

	@Test
	public void invalidExpression() throws IllegalCharException,IllegalNumberException,SyntaxException{
		String input = "abcd * 1234 + abcd * 1234 == abcd * 1234 + abcd / ";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		thrown.expect(Parser.SyntaxException.class);
		parser.expression();
	}

	@Test
	public void invalidArg() throws IllegalCharException,IllegalNumberException,SyntaxException{
		String input = "(abcd * 1234 + abcd * 1234 != abcd * 1234 + abcd * 1234 , )";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		thrown.expect(Parser.SyntaxException.class);
		parser.arg();
	}

	@Test
	public void validChainElem() throws IllegalCharException,IllegalNumberException,SyntaxException{
		String input = "blur (abcd,1234)";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		//thrown.expect(Parser.SyntaxException.class);
		parser.chainElem();
	}

	@Test
	public void invalidChainElem() throws IllegalCharException,IllegalNumberException,SyntaxException{
		String input = "*+-/";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		thrown.expect(Parser.SyntaxException.class);
		parser.chainElem();
	}

	@Test
	public void validIfStatement() throws IllegalCharException,IllegalNumberException,SyntaxException{
		String input = "if (a+b) { a -> b; }";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		//thrown.expect(Parser.SyntaxException.class);
		parser.ifStatement();
	}

	@Test
	public void invalidIfStatement() throws IllegalCharException,IllegalNumberException,SyntaxException{
		String input = "if {a->b;}";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		thrown.expect(Parser.SyntaxException.class);
		parser.ifStatement();
	}

	@Test
	public void validWhileStatement() throws IllegalCharException,IllegalNumberException,SyntaxException{
		String input = "while (a+b) { a -> b; }";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		//thrown.expect(Parser.SyntaxException.class);
		parser.whileStatement();
	}

	@Test
	public void invalidWhileStatement() throws IllegalCharException,IllegalNumberException,SyntaxException{
		String input = "while { a -> b; }";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		thrown.expect(Parser.SyntaxException.class);
		parser.whileStatement();
	}

	@Test
	public void validChain() throws IllegalCharException,IllegalNumberException,SyntaxException{
		String input = "blur (abcd,1234) -> blur +";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		//thrown.expect(Parser.SyntaxException.class);
		parser.chain();
	}

	@Test
	public void invalidChain() throws IllegalCharException,IllegalNumberException,SyntaxException{
		String input = "{ xyza123; }";//"blur (abcd,1234) != blur (abcd,1234)";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		thrown.expect(Parser.SyntaxException.class);
		parser.chain();
	}

	@Test
	public void validAssign() throws IllegalCharException,IllegalNumberException,SyntaxException{
		String input = "abc <- 1234";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		//thrown.expect(Parser.SyntaxException.class);
		parser.assign();
	}

	@Test
	public void invalidAssign() throws IllegalCharException,IllegalNumberException,SyntaxException{
		String input = "abc <- /";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		thrown.expect(Parser.SyntaxException.class);
		parser.assign();
	}

	@Test
	public void validStatement() throws IllegalCharException,IllegalNumberException,SyntaxException{
		String input = "sleep a+b;";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		//thrown.expect(Parser.SyntaxException.class);
		parser.statement();
	}


	@Test
	public void invalidStatement() throws IllegalCharException,IllegalNumberException,SyntaxException{
		String input = "sleep a+b";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		thrown.expect(Parser.SyntaxException.class);
		parser.statement();
	}


	@Test
	public void validDec() throws IllegalCharException,IllegalNumberException,SyntaxException{
		String input = "boolean abc";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		//thrown.expect(Parser.SyntaxException.class);
		parser.dec();
	}

	@Test
	public void invalidDec() throws IllegalCharException,IllegalNumberException,SyntaxException{
		String input = "boolean ";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		thrown.expect(Parser.SyntaxException.class);
		parser.dec();
	}

	@Test
	public void validBlock() throws IllegalCharException,IllegalNumberException,SyntaxException{
		String input = "{boolean abc}";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		//thrown.expect(Parser.SyntaxException.class);
		parser.block();
	}

	@Test
	public void invalidBlock() throws IllegalCharException,IllegalNumberException,SyntaxException{
		String input = "{a+b;}";//"{boolean abc";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		thrown.expect(Parser.SyntaxException.class);
		parser.block();
	}

	@Test
	public void validParamDec() throws IllegalCharException,IllegalNumberException,SyntaxException{
		String input = "url google";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		//thrown.expect(Parser.SyntaxException.class);
		parser.paramDec();
	}

	@Test
	public void invalidParamDec() throws IllegalCharException,IllegalNumberException,SyntaxException{
		String input = "url +*/";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		thrown.expect(Parser.SyntaxException.class);
		parser.paramDec();
	}

	@Test
	public void validProgram() throws IllegalCharException,IllegalNumberException,SyntaxException{
		String input = "abc {boolean abc}";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		//thrown.expect(Parser.SyntaxException.class);
		parser.program();
	}

	@Test
	public void invalidProgram() throws IllegalCharException,IllegalNumberException,SyntaxException{
		String input = "abc {boolean }";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		thrown.expect(Parser.SyntaxException.class);
		parser.program();
	}
}

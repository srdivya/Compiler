package cop5556sp17;

import static cop5556sp17.Scanner.Kind.*;
import static org.junit.Assert.assertEquals;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import cop5556sp17.Scanner.IllegalCharException;
import cop5556sp17.Scanner.IllegalNumberException;
import cop5556sp17.Scanner.Kind;
import cop5556sp17.Scanner.LinePos;
import cop5556sp17.Scanner.Token;

public class ScannerTest 
{
	
	@Rule
    public ExpectedException thrown = ExpectedException.none();
	Token checkNext(Scanner scanner, Scanner.Kind kind, int pos, String text) {
		Scanner.Token token = scanner.nextToken();
		assertEquals(kind, token.kind);
		assertEquals(pos, token.pos);
		assertEquals(text.length(), token.length);
		assertEquals(text, token.getText());
		return token;
	}
	Boolean doPrint = true;
	 //Don't use this with idents or numlits
	Token checkNext(Scanner scanner, Scanner.Kind kind, int pos) {
		Scanner.Token token = scanner.nextToken();
		assertEquals(kind, token.kind);
		assertEquals(pos, token.pos);
		String text = kind.getText();
		assertEquals(text.length(), token.length);
		assertEquals(text, token.getText());
		return token;
	}
	
	Token checkNext(Scanner scanner, Scanner.Kind kind) {
		Scanner.Token token = scanner.nextToken();
		assertEquals(kind, token.kind);
		return token;
	}

	Token getAndCheckEnd(Scanner scanner) {
		Scanner.Token token = scanner.nextToken();
		assertEquals(Scanner.Kind.EOF,token.kind);
		return token;
	}
	
	void checkPos(Scanner scanner, Token t, int line, int posInLine){
		LinePos p = scanner.getLinePos(t);
		assertEquals(line,p.line);
		assertEquals(posInLine, p.posInLine);
	}
	

	
	@Test
	public void testEmpty() throws IllegalCharException, IllegalNumberException {
		String input = "";
		Scanner scanner = new Scanner(input);
		scanner.scan();
	}
	/*@Test

	public void test1() throws IllegalCharException, IllegalNumberException {

		String input = "|->-->->-";
		Scanner scanner = new Scanner(input);
		Kind[] kindArr = {BARARROW,MINUS,ARROW,ARROW,MINUS,EOF};
		int[] posArr = {0,3,4,6,8,9};
		scanner.scan();
		Token token = scanner.nextToken();
		int i=0;
		while(token.kind != EOF)
		{
			assertEquals(kindArr[i], token.kind);
			
			assertEquals(posArr[i], token.pos);

			String text = kindArr[i].getText();

			assertEquals(text.length(), token.length);
			
			assertEquals(text, token.getText());
			i++;
			token = scanner.nextToken();
		}
	}
	
	
	@Test
	public void test2() throws IllegalCharException, IllegalNumberException {
		String input = "{\n-|->\n\n\nA$&#\n";
		Scanner scanner = new Scanner(input);
		thrown.expect(IllegalCharException.class);
		scanner.scan();

	}*/
	@Test
	public void testSemiConcat() throws IllegalCharException, IllegalNumberException {
		//input string
		String input = "intege  +  1;;; \n abcs hioin 46346 gr & ";
		String text="";
		//create and initialize the scanner
		Scanner scanner = new Scanner(input);
		scanner.scan();
		//get the first token and check its kind, position, and contents
		Scanner.Token token = scanner.nextToken();
		assertEquals(IDENT, token.kind);
		assertEquals(0, token.pos);
		assertEquals(0,token.getLinePos().line);
		assertEquals(0,token.getLinePos().posInLine);
		text = token.getText();
		assertEquals(6, token.length);
		assertEquals("intege",token.getText());		
		//next Token
		token=scanner.nextToken();
		assertEquals(PLUS, token.kind);	
		assertEquals(8, token.pos);
		text = PLUS.getText();
		assertEquals(text.length(), token.length);
		assertEquals(text, token.getText());
		//next
		token=scanner.nextToken();
		assertEquals(INT_LIT, token.kind);
		assertEquals(11, token.pos);
		assertEquals(0,token.getLinePos().line);
		assertEquals(11,token.getLinePos().posInLine);
		text = token.getText();
		assertEquals(1, token.length);
		assertEquals("1",token.getText());
		//next
		token=scanner.nextToken();
		assertEquals(SEMI, token.kind);	
		assertEquals(12, token.pos);
		text = SEMI.getText();
		assertEquals(text.length(), token.length);
		assertEquals(text, token.getText());
		//get the next token and check its kind, position, and contents
		Scanner.Token token1 = scanner.nextToken();
		assertEquals(SEMI, token1.kind);
		assertEquals(13, token1.pos);
		assertEquals(text.length(), token1.length);
		assertEquals(text, token1.getText());
		Scanner.Token token2 = scanner.nextToken();
		assertEquals(SEMI, token2.kind);
		assertEquals(14, token2.pos);
		assertEquals(text.length(), token2.length);
		assertEquals(text, token2.getText());
		//next abcs
		token=scanner.nextToken();
		assertEquals(IDENT, token.kind);
		assertEquals(18, token.pos);
		assertEquals(1,token.getLinePos().line);
		assertEquals(1,token.getLinePos().posInLine);
		text = token.getText();
		assertEquals(4, token.length);
		assertEquals("abcs",token.getText());
		//hioin
		token=scanner.nextToken();
		assertEquals(IDENT, token.kind);
		assertEquals(23, token.pos);
		assertEquals(1,token.getLinePos().line);
		assertEquals(6,token.getLinePos().posInLine);
		text = token.getText();
		assertEquals(5, token.length);
		assertEquals("hioin",token.getText());
		//
		token=scanner.nextToken();
		assertEquals(INT_LIT, token.kind);
		assertEquals(29, token.pos);
		assertEquals(1,token.getLinePos().line);
		assertEquals(12,token.getLinePos().posInLine);
		text = token.getText();
		assertEquals(5, token.length);
		assertEquals("46346",token.getText());
		//gr
		token=scanner.nextToken();
		assertEquals(IDENT, token.kind);
		assertEquals(35, token.pos);
		assertEquals(1,token.getLinePos().line);
		assertEquals(18,token.getLinePos().posInLine);
		text = token.getText();
		assertEquals(2, token.length);
		assertEquals("gr",token.getText());
		//&
		token=scanner.nextToken();
		assertEquals(AND, token.kind);	
		assertEquals(38, token.pos);
		text = AND.getText();
		assertEquals(text.length(), token.length);
		assertEquals(text, token.getText());
		//check that the scanner has inserted an EOF token at the end
		Scanner.Token token3 = scanner.nextToken();
		assertEquals(Scanner.Kind.EOF,token3.kind);
	}
	@Test
	public void testIntOverflowError() throws IllegalCharException, IllegalNumberException{
		String input = "99999999999999999";
		Scanner scanner = new Scanner(input);
		thrown.expect(IllegalNumberException.class);
		scanner.scan();		
	}
	
	@Test
	public void TestComments() throws IllegalCharException, IllegalNumberException
	{
		String input = "/abcd wonderful + & * %$  /* 46546467";
		Scanner scan = new Scanner(input);
		scan.scan();
		Kind[] kindArr = {DIV,IDENT,IDENT,PLUS,AND,TIMES,MOD,IDENT,IDENT};
		int[] posArr = {0,1,6,16,18,20,22,23,25};
		String[] strArr = {"/","abcd","wonderful","+","&","*","%","$","$$__hihg"};

		Token token = scan.nextToken();
		int i=0;
		while(token.kind != EOF)
		{
			assertEquals(kindArr[i], token.kind);
			
			assertEquals(posArr[i], token.pos);

			String text = kindArr[i].getText();

			assertEquals(strArr[i].length(), token.length);
			//System.out.println("i =" + i + "text.length() = " + text.length()+ "token.length = "+ token.length);
			assertEquals(strArr[i], token.getText());
			i++;
			token = scan.nextToken();
		}
	}
	
	@Test
	public void Test3() throws IllegalCharException, IllegalNumberException
	{
		String input = "1=*}{Ohrbdu;/!";
		Scanner scan = new Scanner(input);
		thrown.expect(IllegalCharException.class);
		scan.scan();
	}
	@Test
	public void testSepOp() throws IllegalCharException, IllegalNumberException {
		String input = "!===";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Scanner.Token token1 = scanner.nextToken();
		assertEquals(Kind.NOTEQUAL, token1.kind);
		assertEquals("!=", token1.getText());
		Scanner.Token token2 = scanner.nextToken();
		assertEquals(Kind.EQUAL, token2.kind);
		assertEquals("==", token2.getText());
	}
	@Test
	public void testIllegalChar() throws IllegalCharException, IllegalNumberException {
		String input = "test ~ :string";
		Scanner scanner = new Scanner(input);
		thrown.expect(IllegalCharException.class);
		scanner.scan();
		Scanner.Token token1 = scanner.nextToken();
		assertEquals(Kind.IDENT, token1.kind);
		Scanner.Token token2 = scanner.nextToken();
		assertEquals(Kind.IDENT, token2.kind);
	}
	@Test
	public void Test4() throws IllegalCharException, IllegalNumberException
	{
		String input = "{sleep/eat/code|08989845354353453412323321321332313a12313123";
		Scanner scan = new Scanner(input);
		thrown.expect(IllegalNumberException.class);
		scan.scan();
	}
	@Test
	public void Test5() throws IllegalCharException, IllegalNumberException
	{
		String input = "0cat frame/;+9876543211111114444";
		Scanner scan = new Scanner(input);
		thrown.expect(IllegalNumberException.class);
		scan.scan();
	}
	@Test
	public void test1() throws IllegalCharException, IllegalNumberException {
		String input = ";()(;";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		if (doPrint) {
			System.out.println(input);
			System.out.println(scanner);
		}
		checkNext(scanner, SEMI, 0);
		checkNext(scanner, LPAREN, 1);
		checkNext(scanner, RPAREN, 2);
		checkNext(scanner, LPAREN, 3);
		checkNext(scanner, SEMI, 4);
		getAndCheckEnd(scanner);
	}

	@Test
	public void test2() throws IllegalCharException, IllegalNumberException {
		String input = "}{+)!(";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		if (doPrint) {
			System.out.println(input);
			System.out.println(scanner);
		}
		checkNext(scanner, RBRACE, 0);
		checkNext(scanner, LBRACE, 1);
		checkNext(scanner, PLUS, 2);
		checkNext(scanner, RPAREN, 3);
		checkNext(scanner, NOT, 4);
		checkNext(scanner, LPAREN, 5);
		getAndCheckEnd(scanner);
	}

	@Test
	public void test3() throws IllegalCharException, IllegalNumberException {
		String input = "!!!=!=!,";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		if (doPrint) {
			System.out.println(input);
			System.out.println(scanner);
		}
		checkNext(scanner, NOT, 0);
		checkNext(scanner, NOT, 1);
		checkNext(scanner, NOTEQUAL, 2);
		checkNext(scanner, NOTEQUAL, 4);
		checkNext(scanner, NOT, 6);
		checkNext(scanner, COMMA, 7);
		getAndCheckEnd(scanner);
	}

	@Test
	public void test4() throws IllegalCharException, IllegalNumberException {
		String input = "--->->-";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		if (doPrint) {
			System.out.println(input);
			System.out.println(scanner);
		}
		checkNext(scanner, MINUS, 0);
		checkNext(scanner, MINUS, 1);
		checkNext(scanner, ARROW, 2);
		checkNext(scanner, ARROW, 4);
		checkNext(scanner, MINUS, 6);
		getAndCheckEnd(scanner);
	}

	@Test
	public void test5() throws IllegalCharException, IllegalNumberException {
		String input = "|;|--->->-|->";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		if (doPrint) {
			System.out.println(input);
			System.out.println(scanner);
		}
		checkNext(scanner, OR, 0);
		checkNext(scanner, SEMI, 1);
		checkNext(scanner, OR, 2);
		checkNext(scanner, MINUS, 3);
		checkNext(scanner, MINUS, 4);
		checkNext(scanner, ARROW, 5);
		checkNext(scanner, ARROW, 7);
		checkNext(scanner, MINUS, 9);
		checkNext(scanner, BARARROW, 10);
		getAndCheckEnd(scanner);
	}

	@Test
	public void test6() throws IllegalCharException, IllegalNumberException {
		String input = "<<<=>>>=>< ->-->";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		if (doPrint) {
			System.out.println(input);
			System.out.println(scanner);
		}
		checkNext(scanner, LT, 0);
		checkNext(scanner, LT, 1);
		checkNext(scanner, LE, 2);
		checkNext(scanner, GT, 4);
		checkNext(scanner, GT, 5);
		checkNext(scanner, GE, 6);
		checkNext(scanner, GT, 8);
		checkNext(scanner, LT, 9);
		checkNext(scanner, ARROW, 11);
		checkNext(scanner, MINUS, 13);
		checkNext(scanner, ARROW, 14);
		getAndCheckEnd(scanner);
	}

	@Test
	public void test7() throws IllegalCharException, IllegalNumberException {
		String input = "123()+4+54321";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		if (doPrint) {
			System.out.println(input);
			System.out.println(scanner);
		}
		checkNext(scanner, INT_LIT, 0, "123");
		checkNext(scanner, LPAREN, 3);
		checkNext(scanner, RPAREN, 4);
		checkNext(scanner, PLUS, 5);
		checkNext(scanner, INT_LIT, 6, "4");
		checkNext(scanner, PLUS, 7);
		checkNext(scanner, INT_LIT, 8, "54321");
		getAndCheckEnd(scanner);
	}

	@Test
	public void test8() throws IllegalCharException, IllegalNumberException {
		String input = "a+b;a23a4";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		if (doPrint) {
			System.out.println(input);
			System.out.println(scanner);
		}
		checkNext(scanner, IDENT, 0, "a");
		checkNext(scanner, PLUS, 1);
		checkNext(scanner, IDENT, 2, "b");
		checkNext(scanner, SEMI, 3);
		checkNext(scanner, IDENT, 4, "a23a4");
		getAndCheckEnd(scanner);
	}

	@Test
	public void test9() throws IllegalCharException, IllegalNumberException {
		String input = "ifwhile;if;while;boolean;boolean0;integer;integer32|->frame->-image";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		if (doPrint) {
			System.out.println(input);
			System.out.println(scanner);
		}
		checkNext(scanner, IDENT, 0, "ifwhile");
		checkNext(scanner, SEMI, 7);
		checkNext(scanner, KW_IF, 8);
		checkNext(scanner, SEMI, 10);
		checkNext(scanner, KW_WHILE, 11);
		checkNext(scanner, SEMI, 16);
		checkNext(scanner, KW_BOOLEAN, 17);
		checkNext(scanner, SEMI, 24);
		checkNext(scanner, IDENT, 25, "boolean0");
		checkNext(scanner, SEMI, 33);
		checkNext(scanner, KW_INTEGER, 34);
		checkNext(scanner, SEMI, 41);
		checkNext(scanner, IDENT, 42, "integer32");
		checkNext(scanner, BARARROW, 51, "|->");
		checkNext(scanner, KW_FRAME, 54);
		checkNext(scanner, ARROW, 59);
		checkNext(scanner, MINUS, 61);
		checkNext(scanner, KW_IMAGE, 62);
		getAndCheckEnd(scanner);
	}

	@Test
	public void test10() throws IllegalCharException, IllegalNumberException {
		String input = "abc 234 a23";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		if (doPrint) {
			System.out.println(input);
			System.out.println(scanner);
		}
		checkNext(scanner, IDENT, 0, "abc");
		checkNext(scanner, INT_LIT, 4, "234");
		checkNext(scanner, IDENT, 8, "a23");
		getAndCheckEnd(scanner);
	}

	@Test
	public void test11() throws IllegalCharException, IllegalNumberException {
		String input = "abc! !d";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		if (doPrint) {
			System.out.println(input);
			System.out.println(scanner);
		}
		checkNext(scanner, IDENT, 0, "abc");
		checkNext(scanner, NOT, 3);
		checkNext(scanner, NOT, 5);
		checkNext(scanner, IDENT, 6, "d");
		getAndCheckEnd(scanner);
	}

	@Test
	public void test12() throws IllegalCharException, IllegalNumberException {
		String input = "   ;";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		if (doPrint) {
			System.out.println(input);
			System.out.println(scanner);
		}
		checkNext(scanner, SEMI, 3);
		getAndCheckEnd(scanner);
	}

	@Test
	public void test13() throws IllegalCharException, IllegalNumberException {
		String input = "\n\n \r;";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		if (doPrint) {
			System.out.println(input);
			System.out.println(scanner);
		}
		checkNext(scanner, SEMI, 4);
		getAndCheckEnd(scanner);
	}

	@Test
	public void test14() throws IllegalCharException, IllegalNumberException {
		String input = "a\nbc! !\nd";
		Scanner scanner = new Scanner(input);
		scanner.scan();

		if (doPrint) {
			System.out.println(input);
			System.out.println(scanner);
		}
		//System.out.println(scanner.lineStarts);
		Scanner.Token t = scanner.nextToken();
		assertEquals("a",t.getText());
		checkPos(scanner,t,0,0);
		t = scanner.nextToken();
		assertEquals("bc",t.getText());
		checkPos(scanner,t,1,0);
		t = scanner.nextToken();
		assertEquals("!",t.getText());
		checkPos(scanner,t,1,2);
		t = scanner.nextToken();
		assertEquals("!",t.getText());
		checkPos(scanner,t,1,4);
		t = scanner.nextToken();
		assertEquals("d",t.getText());
		checkPos(scanner,t,2,0);		

		

	}
	
	@Test
	public void test15() throws IllegalCharException, IllegalNumberException {
		String input = "/*...*/a/***/\nbc!/ /*/ /**/ !\nd/*.**/";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		if (doPrint) {
			System.out.println(input);
			System.out.println(scanner);
		}
		checkNext(scanner, IDENT, 7, "a");
		checkNext(scanner, IDENT, 14, "bc");
		checkNext(scanner, NOT, 16);
		checkNext(scanner, DIV, 17);
		checkNext(scanner,NOT, 28);
		checkNext(scanner, IDENT, 30, "d");
		getAndCheckEnd(scanner);			
	}

	
	@Test
	public void test16error() throws IllegalCharException, IllegalNumberException{
		String input = "abc def/n345 #abc";
		Scanner scanner = new Scanner(input);
		thrown.expect(IllegalCharException.class);
		thrown.expectMessage("#");
		scanner.scan();
	}
	
	@Test
	public void test17error() throws IllegalCharException, IllegalNumberException{
		String input = "99999999999999999";
		Scanner scanner = new Scanner(input);
		thrown.expect(IllegalNumberException.class);
		scanner.scan();		
	}
	
	@Test
	public void test18() throws IllegalCharException, IllegalNumberException{
		String input = "/* * ** */\nabc";
		Scanner scanner = new Scanner(input);
		scanner.scan();		
		if (doPrint) {
			System.out.println(input);
			System.out.println(scanner);
		}
		checkNext(scanner, IDENT,11,"abc");
		getAndCheckEnd(scanner);			
	}
	
	@Test
	public void test19() throws IllegalCharException, IllegalNumberException{
		String input="123 456";
		Scanner scanner = new Scanner(input);
		scanner.scan();		
		if (doPrint) {
			System.out.println(input);
			System.out.println(scanner);
		}
		Scanner.Token t = scanner.nextToken();
		assertEquals(123,t.intVal());
		t = scanner.nextToken();
		assertEquals(456,t.intVal());	
		getAndCheckEnd(scanner);		
	}
	
	@Test
	public void test20() throws IllegalCharException, IllegalNumberException {
		String input="***%&";
		Scanner scanner = new Scanner(input);
		scanner.scan();		
		if (doPrint) {
			System.out.println(input);
			System.out.println(scanner);
		}
		checkNext(scanner, TIMES, 0);
		checkNext(scanner, TIMES, 1);
		checkNext(scanner, TIMES, 2);
		checkNext(scanner, MOD, 3);
		checkNext(scanner, AND, 4);
	}
	
	@Test
	public void test21() throws IllegalCharException, IllegalNumberException{
		String input="";
		Scanner scanner = new Scanner(input);
		scanner.scan();		
		if (doPrint) {
			System.out.println(input);
			System.out.println(scanner);
		}
		getAndCheckEnd(scanner);		
	}
	
	@Test
	public void test22() throws IllegalCharException, IllegalNumberException{
		String input="/****";
		Scanner scanner = new Scanner(input);
		scanner.scan();		
		if (doPrint) {
			System.out.println(input);
			System.out.println(scanner);
		}
		getAndCheckEnd(scanner);	
	}
	
	@Test
	public void test23() throws IllegalCharException, IllegalNumberException{
		String input="== ==";
		Scanner scanner = new Scanner(input);
		scanner.scan();		
		if (doPrint) {
			System.out.println(input);
			System.out.println(scanner);
		}
		checkNext(scanner, EQUAL, 0);
		checkNext(scanner, EQUAL, 3);		
	}
	
	@Test
	public void test24() throws IllegalCharException, IllegalNumberException{
		thrown.expect(IllegalCharException.class);	
		String input="=";
		Scanner scanner = new Scanner(input);
		scanner.scan();		
		if (doPrint) {
			System.out.println(input);
			System.out.println(scanner);
		}
	
	}
	
	@Test
	public void test25() throws IllegalCharException, IllegalNumberException{
		String input = "show\r\n hide \n move \n file";
		Scanner scanner = new Scanner(input);
		scanner.scan();		
		if (doPrint) {
			System.out.println(input);
			System.out.println(scanner);
		}	
		Token t0 = checkNext(scanner,KW_SHOW,0);
		checkPos(scanner,t0,0,0);
		Token t1 = checkNext(scanner,KW_HIDE,7);
		checkPos(scanner,t1,1,1);
		Token t2 = checkNext(scanner,KW_MOVE,14);
		checkPos(scanner,t2,2,1);
		Token t3 = checkNext(scanner,KW_FILE,21);

		
	}
	
	@Test
	public void testArg2() throws IllegalCharException, IllegalNumberException {
		String input = "  (3,5) ";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		if (doPrint) {
			System.out.println(input);
			System.out.println(scanner);
		}			
	}
	
	@Test
	public void testAssign() throws IllegalCharException, IllegalNumberException{
		String input = "  -< <- <+ <= <\n-<--";		
		Scanner scanner = new Scanner(input);
		scanner.scan();		
		if (doPrint) {
			System.out.println(input);
			System.out.println(scanner);
		}	
		checkNext(scanner,MINUS);
		checkNext(scanner,LT);
		checkNext(scanner,ASSIGN);
		checkNext(scanner,LT);
		checkNext(scanner,PLUS);
		checkNext(scanner,LE);
		checkNext(scanner,LT);
		checkNext(scanner,MINUS);
		checkNext(scanner,ASSIGN);
		checkNext(scanner,MINUS);
	}
	
}

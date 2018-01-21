package cop5556sp17;

//import static cop5556sp17.Scanner.Kind.EOF;
//import static cop5556sp17.Scanner.Kind.PLUS;
import static cop5556sp17.Scanner.Kind.*;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import cop5556sp17.Parser.SyntaxException;
import cop5556sp17.Scanner.IllegalCharException;
import cop5556sp17.Scanner.IllegalNumberException;
import cop5556sp17.Scanner.Kind;
import cop5556sp17.AST.*;
//import cop5556sp17.AST.ASTNode;
//import cop5556sp17.AST.BinaryExpression;
//import cop5556sp17.AST.IdentExpression;
//import cop5556sp17.AST.IntLitExpression;

public class ParseTest {
	@Rule
	public ExpectedException thrown = ExpectedException.none();
	@Test
	public void testProgram1() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "abc {}";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode ast = parser.program();
		assertEquals(Program.class,ast.getClass());
		Program be = (Program) ast;
		assertEquals(Kind.IDENT,be.getFirstToken().kind);
		assertEquals(java.util.ArrayList.class, be.getParams().getClass());
		assertEquals(Block.class, be.getB().getClass());
		Block b = be.getB();
		assertEquals("{",b.getFirstToken().getText());
		assertEquals(new ArrayList<Statement>(),b.getStatements());
		
	}
	
	@Test
	public void testProgram2() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "file abc";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		thrown.expect(Parser.SyntaxException.class);
		parser.program();
	}
	
	@Test
	public void testProgram3() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "mnv {integer nn}";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode ast = parser.program();
		assertEquals(Program.class,ast.getClass());
		Program be = (Program) ast;
		assertEquals(Kind.IDENT,be.getFirstToken().kind);
		assertEquals(java.util.ArrayList.class, be.getParams().getClass());
		assertEquals(Block.class, be.getB().getClass());
		Block b = be.getB();
		assertEquals("{",b.getFirstToken().getText());
		Dec a = b.getDecs().get(0);
		assertEquals(Kind.KW_INTEGER,a.getFirstToken().kind);
		assertEquals("nn",a.getIdent().getText());
		
	}
	
	@Test
	public void testProgram4() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "mnv {grayish <-  998|(abcgg*77%true+9<=false<98>=abc>nncd8==(a!=b));}";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode ast = parser.program();
		assertEquals(Program.class,ast.getClass());
		Program be = (Program) ast;
		assertEquals(Kind.IDENT,be.getFirstToken().kind);
		assertEquals(java.util.ArrayList.class, be.getParams().getClass());
		assertEquals(Block.class, be.getB().getClass());
		Block b = be.getB();
		assertEquals("{",b.getFirstToken().getText());
		AssignmentStatement s = (AssignmentStatement) b.getStatements().get(0);
		assertEquals(Kind.IDENT,be.getFirstToken().kind);
		assertEquals(IdentLValue.class, s.getVar().getClass());
		assertEquals("grayish", s.getVar().getText());
		assertEquals(BinaryExpression.class, s.getE().getClass());
		
	}
	
	@Test
	public void testProgram5() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "mnddv {integer nn grayish <-  998|(abcgg*77%true+9<=false<98>=abc>nncd8==(a!=b));}";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode ast = parser.program();
		assertEquals(Program.class,ast.getClass());
		Program be = (Program) ast;
		assertEquals(Kind.IDENT,be.getFirstToken().kind);
		assertEquals("mnddv",be.getFirstToken().getText());
		assertEquals(java.util.ArrayList.class, be.getParams().getClass());
		assertEquals(Block.class, be.getB().getClass());
		Block b = be.getB();
		assertEquals("{",b.getFirstToken().getText());
		Dec a = b.getDecs().get(0);
		assertEquals(Kind.KW_INTEGER,a.getFirstToken().kind);
		assertEquals("nn",a.getIdent().getText());
		AssignmentStatement s = (AssignmentStatement) b.getStatements().get(0);
		assertEquals(Kind.IDENT,be.getFirstToken().kind);
		assertEquals(IdentLValue.class, s.getVar().getClass());
		assertEquals("grayish", s.getVar().getText());
		assertEquals(BinaryExpression.class, s.getE().getClass());
	}
	
	@Test
	public void testProgram6() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "mnv url fcv {}";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode ast = parser.program();
		assertEquals(Program.class,ast.getClass());
		Program be = (Program) ast;
		assertEquals(Kind.IDENT,be.getFirstToken().kind);
		assertEquals("mnv",be.getFirstToken().getText());
		assertEquals(java.util.ArrayList.class, be.getParams().getClass());
		assertEquals(Block.class, be.getB().getClass());
		Block b = be.getB();
		ParamDec p = be.getParams().get(0);
		assertEquals(Kind.KW_URL,p.getFirstToken().kind);
		assertEquals("fcv",p.getIdent().getText());
		assertEquals("{",b.getFirstToken().getText());
	}
	

	@Test
	public void testProgram7() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "mnddv integer kkk,file vfrh {image hello}";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode ast = parser.program();
		assertEquals(Program.class,ast.getClass());
		Program be = (Program) ast;
		assertEquals(Kind.IDENT,be.getFirstToken().kind);
		assertEquals("mnddv",be.getFirstToken().getText());
		assertEquals(java.util.ArrayList.class, be.getParams().getClass());
		assertEquals(Block.class, be.getB().getClass());
		Block b = be.getB();
		ParamDec p = be.getParams().get(0);
		assertEquals(Kind.KW_INTEGER,p.getFirstToken().kind);
		assertEquals("kkk",p.getIdent().getText());
		p = be.getParams().get(1);
		assertEquals(Kind.KW_FILE,p.getFirstToken().kind);
		assertEquals("vfrh",p.getIdent().getText());
		
		assertEquals("{",b.getFirstToken().getText());
		Dec a = b.getDecs().get(0);
		assertEquals(Kind.KW_IMAGE,a.getFirstToken().kind);
		assertEquals("hello",a.getIdent().getText());
	}
	
	@Test
	public void testProgram8() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "url fcv {}";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		thrown.expect(Parser.SyntaxException.class);
		parser.program();
	}

	@Test
	public void testProgram9() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "mnddv ";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		thrown.expect(Parser.SyntaxException.class);
		parser.program();
	}
	
	@Test
	public void testProgram10() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "mnv url fcv ";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		thrown.expect(Parser.SyntaxException.class);
		parser.program();
	}
	

	@Test
	public void testProgram11() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "mnddv {hello}";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		thrown.expect(Parser.SyntaxException.class);
		parser.program();
	}
	
	@Test
	public void testProgram12() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "mnv url fcv, {}";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		thrown.expect(Parser.SyntaxException.class);
		parser.program();
	}
	@Test
	public void testBinaryExpr1() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "(4 + 8) * 9";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode ast = parser.expression();
		assertEquals(BinaryExpression.class, ast.getClass());
		BinaryExpression be = (BinaryExpression) ast;
		assertEquals(BinaryExpression.class, be.getE0().getClass());
		assertEquals(IntLitExpression.class, be.getE1().getClass());
	}
	
	@Test
	public void testBinaryExpr2() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "3 + 4 * 3";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode ast = parser.expression();
		assertEquals(BinaryExpression.class, ast.getClass());
		BinaryExpression be = (BinaryExpression) ast;
		assertEquals(IntLitExpression.class, be.getE0().getClass());
		assertEquals(BinaryExpression.class, be.getE1().getClass());
		assertEquals(PLUS, be.getOp().kind);
		//assertEquals(LPAREN,be.getE0().getFirstToken());
	}
	
	@Test
	public void testTuple() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "(6 * 8, 2 * 8, (1 + 9))";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode ast = parser.arg();
		assertEquals(Tuple.class, ast.getClass());
		Tuple tu = (Tuple) ast;
		assertEquals(3, tu.getExprList().size());
		assertEquals(BinaryExpression.class, tu.getExprList().get(0).getClass());
		assertEquals(BinaryExpression.class, tu.getExprList().get(1).getClass());
		assertEquals(BinaryExpression.class, tu.getExprList().get(2).getClass());
	}
	

	@Test
	public void testIfStatement() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "if(true){integer blue sleep\n true-false!=k;}";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode ast = parser.statement();
		assertEquals(IfStatement.class, ast.getClass());
		IfStatement ifState = (IfStatement) ast;
		assertEquals("blue", ifState.getB().getDecs().get(0).getIdent().getText());
		assertEquals(SleepStatement.class, ifState.getB().getStatements().get(0).getClass());
		assertEquals("if", ifState.getFirstToken().getText());
		assertEquals(BooleanLitExpression.class,ifState.getE().getClass());
	}
	
	@Test
	public void testWhileStatement() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "while(true){integer blue sleep\n true-false!=k;}";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode ast = parser.statement();
		assertEquals(WhileStatement.class, ast.getClass());
		WhileStatement whileState = (WhileStatement) ast;
		assertEquals("blue", whileState.getB().getDecs().get(0).getIdent().getText());
		assertEquals(SleepStatement.class, whileState.getB().getStatements().get(0).getClass());
		assertEquals("while", whileState.getFirstToken().getText());
		assertEquals(BooleanLitExpression.class,whileState.getE().getClass());
	}
	
	@Test
	public void testAssignmentStatement() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "{a <- k ; }";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode ast = parser.block();
		Block block = (Block) ast;
		assertEquals(Block.class, ast.getClass());
		assertEquals(AssignmentStatement.class,block.getStatements().get(0).getClass());
		AssignmentStatement assgnStmt = (AssignmentStatement) block.getStatements().get(0);
		assertEquals(IdentExpression.class,assgnStmt.getE().getClass());	
	}
	
	@Test
	public void testParamDec() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "integer k_1578";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode ast = parser.paramDec();
		ParamDec paramDec = (ParamDec) ast;
		assertEquals(ParamDec.class, ast.getClass());
		assertEquals(IDENT,paramDec.getIdent().kind);
	}
	
	@Test
	public void testChainStatement() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "convolve  (1,4)->convolve;";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode ast = parser.statement();
		BinaryChain chainState = (BinaryChain) ast;
		assertEquals(BinaryChain.class, ast.getClass());
		assertEquals(FilterOpChain.class,chainState.getE0().getClass());
		assertEquals(ARROW, chainState.getArrow().kind);
		assertEquals(FilterOpChain.class,chainState.getE1().getClass());	
	}
	
	@Test
	public void testParamDec1() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "file test";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode ast = parser.paramDec();
		ParamDec paramdec = (ParamDec) ast;
		assertEquals(ParamDec.class, ast.getClass());
		assertEquals("file", paramdec.getFirstToken().getText());
		assertEquals("test",paramdec.getIdent().getText());
		
	}
	
	@Test
	public void testIfStatemet1() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "_$ {if(false){}}";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode ast = parser.program();
		Program programParser = (Program) ast;
		assertEquals(Program.class, ast.getClass());
		assertEquals(IDENT, programParser.getFirstToken().kind);
		assertEquals(IfStatement.class,programParser.getB().getStatements().get(0).getClass());
	}
	
	@Test
	public void testProgram0() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "program {}";
		Parser parser = new Parser(new Scanner(input).scan());
		ASTNode ast = parser.parse();
		Program programParser = (Program) ast;
		assertEquals(Program.class, ast.getClass());
		assertEquals(IDENT, programParser.getFirstToken().kind);
	}
	
	@Test
	public void testBlock() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = " {if(k) {x<-y;}}  ";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode ast = parser.block();
		Block blockVal = (Block) ast;
		assertEquals(Block.class, ast.getClass());
		assertEquals(LBRACE, blockVal.firstToken.kind);
		IfStatement ifStmt = (IfStatement) blockVal.getStatements().get(0);
		assertEquals(IdentExpression.class, ifStmt.getE().getClass());
		assertEquals(Block.class, ifStmt.getB().getClass());
		Block innerBlock = (Block) ifStmt.getB();
		assertEquals(AssignmentStatement.class, innerBlock.getStatements().get(0).getClass());
	}
}
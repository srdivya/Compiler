package cop5556sp17;

import static cop5556sp17.Scanner.Kind.*;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import cop5556sp17.Parser.SyntaxException;
import cop5556sp17.Scanner.IllegalCharException;
import cop5556sp17.Scanner.IllegalNumberException;
import cop5556sp17.AST.*;
import cop5556sp17.AST.ASTNode;
import cop5556sp17.AST.BinaryExpression;
import cop5556sp17.AST.IdentExpression;
import cop5556sp17.AST.IntLitExpression;

public class ASTTest {

	static final boolean doPrint = true;
	static void show(Object s){
		if(doPrint){System.out.println(s);}
	}
	

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Test
	public void testFactor0() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "abc";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode ast = parser.expression();
		assertEquals(IdentExpression.class, ast.getClass());
	}

	@Test
	public void testFactor1() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "123";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode ast = parser.expression();
		assertEquals(IntLitExpression.class, ast.getClass());
	}


	@Test
	public void testProgram() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "Test file abc, boolean xyz { integer x sleep screenwidth != screenheight;}";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		//thrown.expect(Parser.SyntaxException.class);
		ASTNode ast = parser.program();
		Program p = (Program) ast;
		assertEquals(Program.class, ast.getClass());
		assertEquals(Block.class,p.getB().getClass());
		assertEquals(ParamDec.class, p.getParams().get(0).getClass());
		Block b = p.getB();
		assertEquals(Dec.class, b.getDecs().get(0).getClass());
		assertEquals(SleepStatement.class, b.getStatements().get(0).getClass());
		ArrayList<Dec> d = b.getDecs();
		ArrayList<Statement> s = b.getStatements();
		assertEquals(KW_INTEGER, d.get(0).firstToken.kind);
		assertEquals(IDENT, d.get(0).getIdent().kind);
		assertEquals(OP_SLEEP, s.get(0).firstToken.kind);
		BinaryExpression be = (BinaryExpression) ((SleepStatement)s.get(0)).getE();
		assertEquals(ConstantExpression.class,be.getE0().getClass());
		assertEquals(ConstantExpression.class,be.getE1().getClass());
		assertEquals(NOTEQUAL, be.getOp().kind);
	}
	
	@Test
	public void testBinaryExpr0() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "1+abc";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode ast = parser.expression();
		assertEquals(BinaryExpression.class, ast.getClass());
		BinaryExpression be = (BinaryExpression) ast;
		assertEquals(IntLitExpression.class, be.getE0().getClass());
		assertEquals(IdentExpression.class, be.getE1().getClass());
		assertEquals(PLUS, be.getOp().kind);
	}
	
	@Test
	public void testBlock() throws IllegalCharException,IllegalNumberException, SyntaxException{
		String input = "{scale(a,10,abc==xyz)->b|->show;k<-(i!=1)+1; if(true){boolean testingifthisworks}}";
		Parser parser = new Parser(new Scanner(input).scan());
		ASTNode ast = parser.block();
		Block block = (Block) ast;
		assertEquals(Block.class, ast.getClass());	
		assertEquals(BinaryChain.class,block.getStatements().get(0).getClass());
		assertEquals(AssignmentStatement.class,block.getStatements().get(1).getClass());
		BinaryChain bChain = (BinaryChain) block.getStatements().get(0);
		AssignmentStatement astmt = (AssignmentStatement) block.getStatements().get(1);
		assertEquals(FrameOpChain.class,bChain.getE1().getClass());
		assertEquals(BARARROW,bChain.getArrow().kind);
		BinaryChain bChain1 = (BinaryChain) bChain.getE0();
		assertEquals(ImageOpChain.class,bChain1.getE0().getClass());
		assertEquals(ARROW,bChain1.getArrow().kind);
		assertEquals(IdentChain.class,bChain1.getE1().getClass());
		ImageOpChain img = (ImageOpChain) bChain1.getE0();
		assertEquals(KW_SCALE,img.firstToken.kind);
		Tuple tp = img.getArg();
		assertEquals(IdentExpression.class,tp.getExprList().get(0).getClass());
		assertEquals(IntLitExpression.class,tp.getExprList().get(1).getClass());
		assertEquals(BinaryExpression.class, tp.getExprList().get(2).getClass());
		assertEquals(IdentExpression.class, ((BinaryExpression)tp.getExprList().get(2)).getE0().getClass());
		assertEquals(IdentExpression.class, ((BinaryExpression)tp.getExprList().get(2)).getE1().getClass());
		assertEquals(EQUAL, ((BinaryExpression)tp.getExprList().get(2)).getOp().kind);
		assertEquals(IDENT, astmt.var.firstToken.kind);
		assertEquals(BinaryExpression.class,astmt.getE().getClass());
		BinaryExpression bExp = (BinaryExpression) astmt.getE();
		assertEquals(PLUS,bExp.getOp().kind);
		assertEquals(IntLitExpression.class,bExp.getE1().getClass());
		assertEquals(BinaryExpression.class,bExp.getE0().getClass());
		BinaryExpression bExp1 = (BinaryExpression) bExp.getE0();
		assertEquals(NOTEQUAL,bExp1.getOp().kind);
		assertEquals(IdentExpression.class,bExp1.getE0().getClass());
		assertEquals(IntLitExpression.class,bExp1.getE1().getClass());
		assertEquals(IfStatement.class, block.getStatements().get(2).getClass());
		IfStatement ifstmt = (IfStatement) block.getStatements().get(2);
		assertEquals(BooleanLitExpression.class, ifstmt.getE().getClass());
		Block b = ifstmt.getB();
		assertEquals(KW_BOOLEAN, b.getDecs().get(0).getType().kind);
		assertEquals("testingifthisworks", b.getDecs().get(0).getIdent().getText());
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
	public void testArg() throws IllegalCharException,IllegalNumberException,SyntaxException{
		String input = "(xyz,abc_123 | s$1 <= true, screenwidth != 269)";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode ast = parser.arg();
		assertEquals(Tuple.class, ast.getClass());
		Tuple tuple = (Tuple) ast;
		assertEquals(IdentExpression.class, tuple.getExprList().get(0).getClass());
		BinaryExpression be = (BinaryExpression) tuple.getExprList().get(1);
		BinaryExpression be1 = (BinaryExpression) be.getE0();
		assertEquals(IdentExpression.class, be1.getE0().getClass());
		assertEquals(LE, be.getOp().kind);
		assertEquals(OR, be1.getOp().kind);
		assertEquals(IdentExpression.class, be1.getE1().getClass());
		assertEquals(BooleanLitExpression.class, be.getE1().getClass());
		BinaryExpression be2 = (BinaryExpression) tuple.getExprList().get(2);
		assertEquals(ConstantExpression.class, be2.getE0().getClass());
		assertEquals(NOTEQUAL, be2.getOp().kind);
		assertEquals(IntLitExpression.class, be2.getE1().getClass());
	}
	@Test
	public void testChain() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "move(a==21)|->width(10>=true)->convolve(5)";
		Parser parser = new Parser(new Scanner(input).scan());
		ASTNode ast = parser.chain();
		BinaryChain chn = (BinaryChain) ast;
		assertEquals(BinaryChain.class, ast.getClass());
		BinaryChain chn1 = (BinaryChain) chn.getE0();
		FrameOpChain frameOp = (FrameOpChain) chn1.getE0();
		assertEquals(KW_MOVE,frameOp.firstToken.kind);
		Tuple tp = frameOp.getArg();
		assertEquals(IdentExpression.class,((BinaryExpression)tp.getExprList().get(0)).getE0().getClass());
		assertEquals(IntLitExpression.class,((BinaryExpression)tp.getExprList().get(0)).getE1().getClass());
		assertEquals(EQUAL,((BinaryExpression)tp.getExprList().get(0)).getOp().kind);
		assertEquals(BARARROW,chn1.getArrow().kind);
		ImageOpChain img = (ImageOpChain) chn1.getE1();
		assertEquals(OP_WIDTH,img.firstToken.kind);
		Tuple tp2 = img.getArg();
		assertEquals(IntLitExpression.class,((BinaryExpression)tp2.getExprList().get(0)).getE0().getClass());
		assertEquals(BooleanLitExpression.class,((BinaryExpression)tp2.getExprList().get(0)).getE1().getClass());
		assertEquals(GE,((BinaryExpression)tp2.getExprList().get(0)).getOp().kind);
		assertEquals(ARROW,chn.getArrow().kind);
		FilterOpChain filOp = (FilterOpChain) chn.getE1();
		assertEquals(OP_CONVOLVE,filOp.firstToken.kind);
		Tuple tp1 = filOp.getArg();
		assertEquals(IntLitExpression.class,tp1.getExprList().get(0).getClass());
	}
}

package cop5556sp17;

import cop5556sp17.Scanner.Kind;
import static cop5556sp17.Scanner.Kind.*;
import cop5556sp17.AST.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import cop5556sp17.Scanner.Token;

public class Parser {

	/**
	 * Exception to be thrown if a syntax error is detected in the input.
	 * You will want to provide a useful error message.
	 *
	 */
	@SuppressWarnings("serial")
	public static class SyntaxException extends Exception {
		public SyntaxException(String message) {
			super(message);
		}
	}
	
	/**
	 * Useful during development to ensure unimplemented routines are
	 * not accidentally called during development.  Delete it when 
	 * the Parser is finished.
	 *
	 */
	@SuppressWarnings("serial")	
	public static class UnimplementedFeatureException extends RuntimeException {
		public UnimplementedFeatureException() {
			super();
		}
	}

	Scanner scanner;
	Token t;

	Parser(Scanner scanner) {
		this.scanner = scanner;
		t = scanner.nextToken();
	}

	/**
	 * parse the input using tokens from the scanner.
	 * Check for EOF (i.e. no trailing junk) when finished
	 * 
	 * @throws SyntaxException
	 */
	Program parse() throws SyntaxException {
		Program p = program();
		matchEOF();
		return p;
	}

	Expression expression() throws SyntaxException //expression ::= term ( relOp term)*
	{
		try 
		{
			Token firstToken = t;
			Expression expr1 = null;
			Expression expr2 = null;
			expr1 = term();
			ArrayList<Kind> lstRelOpKind = new ArrayList<Kind>();
			lstRelOpKind.add(LT ); lstRelOpKind.add( LE ); 
			lstRelOpKind.add( GT ); lstRelOpKind.add( GE ); 
			lstRelOpKind.add( EQUAL ); lstRelOpKind.add( NOTEQUAL);
			while(lstRelOpKind.contains(t.kind))
			{
				Token op = t;
				consume();
				expr2 =  term();
				expr1 = new BinaryExpression(firstToken, expr1, op, expr2);
			}
			return expr1;
		} 
		catch (Exception e) 
		{
			throw new SyntaxException("Illegal token in expression " + t.kind.text + " pos = " + t.pos);
		}
	}

	Expression term() throws SyntaxException //elem ( weakOp elem)*
	{
		try 
		{
			Token firstToken = t;
			Expression expr1 = null;
			Expression expr2 = null;
			expr1 = elem();
			ArrayList<Kind> lstWeakOpKind = new ArrayList<Kind>();
			lstWeakOpKind.add(PLUS);
			lstWeakOpKind.add(MINUS);
			lstWeakOpKind.add(OR);
			while(lstWeakOpKind.contains(t.kind))
			{
				Token op = t;
				consume();
				expr2 =  elem();
				expr1 = new BinaryExpression(firstToken, expr1, op, expr2);
			}
			return expr1;
		} 
		catch (Exception e) 
		{
			throw new SyntaxException("Illegal token in term " + t.kind.text + " pos = " + t.pos);
		}
	}

	Expression elem() throws SyntaxException //elem ::= factor ( strongOp factor)*
	{
		try 
		{
			Token firstToken = t;
			Expression expr1 = null;
			Expression expr2 = null;
			expr1 = factor();
			ArrayList<Kind> lstStrongOp = new ArrayList<Kind>();
			lstStrongOp.add(TIMES);
			lstStrongOp.add(DIV);
			lstStrongOp.add(AND);
			lstStrongOp.add(MOD);
			while(lstStrongOp.contains(t.kind))
			{
				Token op = t;
				consume();
				expr2 =  factor();
				expr1 = new BinaryExpression(firstToken, expr1, op, expr2);
			}
			return expr1;
		} 
		catch (Exception e) 
		{
			throw new SyntaxException("Illegal token in elem() " + t.kind.text + " pos = " + t.pos);
		}
	}

	Expression factor() throws SyntaxException //factor ::= IDENT | INT_LIT | KW_TRUE | KW_FALSE
										//| KW_SCREENWIDTH | KW_SCREENHEIGHT | ( expression )
	{
		Token firstToken = t;
		Expression expr = null;
		Kind kind = t.kind;
		switch (kind) {
		case IDENT: {
			expr = new IdentExpression(firstToken);
			consume();
		}
			break;
		case INT_LIT: {
			expr = new IntLitExpression(firstToken);
			consume();
		}
			break;
		case KW_TRUE:
		case KW_FALSE: {
			expr = new BooleanLitExpression(firstToken);
			consume();
		}
			break;
		case KW_SCREENWIDTH:
		case KW_SCREENHEIGHT: {
			expr = new ConstantExpression(firstToken);
			consume();
		}
			break;
		case LPAREN: {
			consume();
			expr = expression();
			match(RPAREN);
		}
			break;
		default:
			//you will want to provide a more useful error message
			throw new SyntaxException("illegal factor");
		}
		return expr;
	}

	Block block() throws SyntaxException //block ::= { ( dec | statement) * }
	{
		try 
		{
			Token firstToken = t;
			ArrayList<Dec> decs = new ArrayList<Dec>();
			ArrayList<Statement> statements = new ArrayList<Statement>();
			Block objBlock = null;
			match(Kind.LBRACE);
			ArrayList<Kind> lstKind = new ArrayList<Kind>();
			lstKind.add(KW_INTEGER);
			lstKind.add(KW_BOOLEAN);
			lstKind.add(KW_IMAGE);
			lstKind.add(KW_FRAME);
			lstKind.add(OP_SLEEP);
			lstKind.add(KW_WHILE);
			lstKind.add(KW_IF);
			ArrayList<Kind> lstChainKind = new ArrayList<Kind>();
			lstChainKind.add(IDENT); lstChainKind.add(OP_BLUR); 
			lstChainKind.add(OP_GRAY);lstChainKind.add(OP_CONVOLVE); 
			lstChainKind.add(KW_SHOW); lstChainKind.add(KW_HIDE); 
			lstChainKind.add(KW_MOVE); lstChainKind.add(KW_XLOC); 
			lstChainKind.add(KW_YLOC); lstChainKind.add(OP_WIDTH); 
			lstChainKind.add(OP_HEIGHT); lstChainKind.add(KW_SCALE);
			
			while (lstKind.contains(t.kind) || lstChainKind.contains(t.kind)) //for assign/chain
			{
				if(t.isKind(KW_INTEGER ) || t.isKind( KW_BOOLEAN ) || t.isKind( KW_IMAGE ) || t.isKind( KW_FRAME))
					 decs.add(dec());
				else
					statements.add(statement());
			}
			match(RBRACE);
			objBlock = new Block(firstToken, decs, statements);
			return objBlock;
		} 
		catch (Exception e) 
		{
			throw new SyntaxException("Illegal token in block " + t.kind.name());
		}
	}

	Program program() throws SyntaxException //program ::= IDENT block
	//program ::= IDENT param_dec ( , param_dec )* block
	{
		try 
		{
			Token firstToken = t;
			ArrayList<ParamDec> paramList = new ArrayList<ParamDec>();
			Block objBlock = null;
			match(IDENT); //IDENT
			if (t.isKind(Kind.LBRACE)) 
				objBlock = block(); 
			else 
			{
				paramList.add(paramDec());
				while(t.isKind(COMMA))
				{
					consume();
					paramList.add(paramDec());
				}
				objBlock = block();
			}
			return new Program(firstToken, paramList, objBlock);
		} 
		catch (Exception e) 
		{
			throw new SyntaxException("Illegal token in program " + t.kind.text + " pos = " + t.pos);
		}
	}

	ParamDec paramDec() throws SyntaxException //paramDec ::= ( KW_URL | KW_FILE | KW_INTEGER | KW_BOOLEAN ) IDENT 
	{
		try 
		{

			ParamDec objParamDec = null;
			Token firstToken = t;
			ArrayList<Kind> lstParam = new ArrayList<Kind>();
			lstParam.add(KW_FILE); lstParam.add(KW_BOOLEAN);
			lstParam.add(KW_URL); lstParam.add(KW_INTEGER);
			if(lstParam.contains(t.kind))
			{
				consume();
				Token ident = t;
				match(IDENT);
				objParamDec = new ParamDec(firstToken, ident);
			}
			else
				throw new SyntaxException("Illegal token in paramDec " + t.kind.text + " pos = " + t.pos);
			return objParamDec;
		} 
		catch (Exception e) 
		{
			throw new SyntaxException("Illegal token in paramDec " + t.kind.text + " pos = " + t.pos);
		}
	}

	Dec dec() throws SyntaxException //dec ::= ( KW_INTEGER | KW_BOOLEAN | KW_IMAGE | KW_FRAME ) IDENT 
	{
		try 
		{
			Dec objDec = null;
			Token firstToken = t;
			ArrayList<Kind> lstDec = new ArrayList<Kind>();
			lstDec.add(KW_IMAGE); lstDec.add(KW_BOOLEAN);
			lstDec.add(KW_FRAME); lstDec.add(KW_INTEGER);
			if(lstDec.contains(t.kind))
			{
				consume();
				Token ident = t;
				match(IDENT);
				objDec = new Dec(firstToken, ident);
			}
			else
				throw new SyntaxException("Illegal token in dec " + t.kind.text + " pos = " + t.pos);
			return objDec;
		}
		catch (Exception e) 
		{
			throw new SyntaxException("Illegal token in dec " + t.kind.text + " pos = " + t.pos);
		}
	}

	Statement statement() throws SyntaxException //statement ::= OP_SLEEP expression ; | whileStatement | ifStatement | chain ; | assign ; 
	{
		try 
		{
			Token firstToken = t;
			Statement stmt = null;
			switch(t.kind)
			{
				case OP_SLEEP:
					consume();
					Expression expr = expression();
					match(SEMI);
					stmt = new SleepStatement(firstToken, expr);
					break;
				case KW_WHILE:
					stmt = whileStatement();
					break;
				case KW_IF:
					stmt = ifStatement();
					break;
				case IDENT:
					Token nextToken = scanner.peek();
					ArrayList<Kind> lstKind = new ArrayList<Kind>();
					lstKind.add(ARROW); lstKind.add(BARARROW);
					if(lstKind.contains(nextToken.kind))
					{
						stmt = chain();
						match(SEMI);
					}
					else if (nextToken.isKind(ASSIGN))
					{
						stmt = assign();
						match(SEMI);
					}
					else
						throw new SyntaxException("Illegal token in statement " + t.kind.text + " pos = " + t.pos);
					break;
				default:
					ArrayList<Kind> lstChainKind = new ArrayList<Kind>();
					lstChainKind.add(IDENT); lstChainKind.add(OP_BLUR); 
					lstChainKind.add(OP_GRAY);lstChainKind.add(OP_CONVOLVE); 
					lstChainKind.add(KW_SHOW); lstChainKind.add(KW_HIDE); 
					lstChainKind.add(KW_MOVE); lstChainKind.add(KW_XLOC); 
					lstChainKind.add(KW_YLOC); lstChainKind.add(OP_WIDTH); 
					lstChainKind.add(OP_HEIGHT); lstChainKind.add(KW_SCALE);
					if(lstChainKind.contains(t.kind))
					{
						stmt = chain();
						match(SEMI);
					}
					else
						throw new SyntaxException("Illegal token in statement " + t.kind.text + " pos = " + t.pos);
			}
			return stmt;
		} 
		catch (Exception e) 
		{
			throw new SyntaxException("Illegal token in statement " + t.kind.text + " pos = " + t.pos);
		}
	}

	AssignmentStatement assign() throws SyntaxException //assign ::= IDENT ASSIGN expression
	{
		try 
		{
			Token firstToken = t;
			IdentLValue identLValue = new IdentLValue(firstToken);
			match(IDENT);
			match(ASSIGN);
			Expression expr = expression();
			//identLValue.setDec(new Dec(expr.firstToken, firstToken));
			return new AssignmentStatement(firstToken, identLValue, expr);
		} 
		catch (Exception e) 
		{
			throw new SyntaxException("Illegal token in assign " + t.kind.text + " pos = " + t.pos);
		}
	}

	void arrowOp() throws SyntaxException //arrowOp ::= ARROW | BARARROW
	{
		try 
		{
			if(t.isKind(ARROW))
				match(ARROW);
			else
				match(BARARROW);
		} 
		catch (Exception e) 
		{
			throw new SyntaxException("Illegal token in arrowOp " + t.kind.text + " pos = " + t.pos);
		}
	}

	IfStatement ifStatement() throws SyntaxException //ifStatement ::= KW_IF ( expression ) block
	{
		try 
		{
			Token firstToken = t;
			match(KW_IF);
			match(LPAREN);
			Expression expr = expression();
			match(RPAREN);
			Block objBlock = block();
			return new IfStatement(firstToken, expr, objBlock);
		}
		catch (Exception e) 
		{
			throw new SyntaxException("Illegal token in ifstatement " + t.kind.text + " pos = " + t.pos);
		}
	}

	WhileStatement whileStatement() throws SyntaxException //whileStatement ::= KW_WHILE ( expression ) block
	{
		try 
		{
			Token firstToken = t;
			match(KW_WHILE);
			match(LPAREN);
			Expression expr = expression();
			match(RPAREN);
			Block objBlock = block();
			return new WhileStatement(firstToken, expr, objBlock);
		} 
		catch (Exception e) 
		{
			throw new SyntaxException("Illegal token in whilestatement " + t.kind.text + " pos = " + t.pos);
		}
	}

	Chain chain() throws SyntaxException //chain ::= chainElem arrowOp chainElem (arrowOp chainElem)* 
	{
		try 
		{
			Token firstToken = t;
			ChainElem c1 = chainElem();
			Token arrow = t;
			arrowOp();
			ChainElem c2 = chainElem();
			Chain objChain = new BinaryChain(firstToken,c1,arrow,c2);
			while (t.isKind( ARROW ) || t.isKind( BARARROW ))
			{
				arrow = t;
				arrowOp();
				c1 = chainElem();
				objChain = new BinaryChain(firstToken, objChain, arrow, c1);
			}
			return objChain;
		} 
		catch (Exception e) 
		{
			throw new SyntaxException("Illegal token in chain " + t.kind.text + " pos = " + t.pos);
		}
	}

	ChainElem chainElem() throws SyntaxException //chainElem ::= IDENT | filterOp arg | frameOp arg | imageOp arg
	{
		try 
		{
			Token firstToken = t;
			ChainElem chain = null;
			Tuple tuple = null;
			switch (t.kind) 
			{
				case IDENT:
					chain = new IdentChain(firstToken); 
					consume();
					break;
				case OP_BLUR: case  OP_GRAY: 
				case  OP_CONVOLVE: 
					consume();
					tuple = arg();
					chain = new FilterOpChain(firstToken, tuple);
					break;
				case  KW_SHOW: 
				case  KW_HIDE: case  KW_MOVE: 
				case  KW_XLOC: case  KW_YLOC:
					consume();
					tuple = arg();
					chain = new FrameOpChain(firstToken, tuple);
					break;
				case  OP_WIDTH: case  OP_HEIGHT: 
				case  KW_SCALE:
					consume();
					tuple = arg();
					chain = new ImageOpChain(firstToken, tuple);
					break;
				default:
					throw new SyntaxException("Illegal token in chainElem " + t.kind.text + " pos = " + t.pos);
			}
			return chain;
		} 
		catch (Exception e) 
		{
			throw new SyntaxException("Illegal token in chainElem " + t.kind.text + " pos = " + t.pos);
		}
	}

	Tuple arg() throws SyntaxException //arg ::= epsilon | ( expression ( , expression)* )
	{
		try 
		{
			Token firstToken = t;
			Tuple tuple = null;
			List<Expression> exprList = new ArrayList<Expression>();
			if(t.isKind(LPAREN))
			{
				consume();
				exprList.add(expression());
				while(t.isKind(COMMA))
				{
					consume();
					exprList.add(expression());
				}
				match(RPAREN);
			}
			tuple = new Tuple(firstToken, exprList);
			return tuple;
		} 
		catch (Exception e) 
		{
			throw new SyntaxException("Illegal token in arg " + t.kind.text + " pos = " + t.pos);
		}
	}

	/**
	 * Checks whether the current token is the EOF token. If not, a
	 * SyntaxException is thrown.
	 * 
	 * @return
	 * @throws SyntaxException
	 */
	private Token matchEOF() throws SyntaxException {
		if (t.isKind(EOF)) {
			return t;
		}
		throw new SyntaxException("expected EOF");
	}

	/**
	 * Checks if the current token has the given kind. If so, the current token
	 * is consumed and returned. If not, a SyntaxException is thrown.
	 * 
	 * Precondition: kind != EOF
	 * 
	 * @param kind
	 * @return
	 * @throws SyntaxException
	 */
	private Token match(Kind kind) throws SyntaxException {
		if (t.isKind(kind)) {
			return consume();
		}
		throw new SyntaxException("saw " + t.kind + "expected " + kind);
	}

	/**
	 * Checks if the current token has one of the given kinds. If so, the
	 * current token is consumed and returned. If not, a SyntaxException is
	 * thrown.
	 * 
	 * * Precondition: for all given kinds, kind != EOF
	 * 
	 * @param kinds
	 *            list of kinds, matches any one
	 * @return
	 * @throws SyntaxException
	 */
	@SuppressWarnings("unused")
	private Token match(ArrayList<Kind> kinds) throws SyntaxException {
		
		if(kinds.contains(t.kind))
			return consume();
		throw new SyntaxException("Token mismatch saw " + t.kind + "expected in " + String.join(",", kinds.toString()));
	}

	/**
	 * Gets the next token and returns the consumed token.
	 * 
	 * Precondition: t.kind != EOF
	 * 
	 * @return
	 * 
	 */
	private Token consume() throws SyntaxException {
		Token tmp = t;
		t = scanner.nextToken();
		return tmp;
	}

}

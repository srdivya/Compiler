package cop5556sp17;

import cop5556sp17.AST.ASTVisitor;
import cop5556sp17.AST.Tuple;
import cop5556sp17.AST.AssignmentStatement;
import cop5556sp17.AST.BinaryChain;
import cop5556sp17.AST.BinaryExpression;
import cop5556sp17.AST.Block;
import cop5556sp17.AST.BooleanLitExpression;
import cop5556sp17.AST.ChainElem;
import cop5556sp17.AST.ConstantExpression;
import cop5556sp17.AST.Dec;
import cop5556sp17.AST.Expression;
import cop5556sp17.AST.FilterOpChain;
import cop5556sp17.AST.FrameOpChain;
import cop5556sp17.AST.IdentChain;
import cop5556sp17.AST.IdentExpression;
import cop5556sp17.AST.IdentLValue;
import cop5556sp17.AST.IfStatement;
import cop5556sp17.AST.ImageOpChain;
import cop5556sp17.AST.IntLitExpression;
import cop5556sp17.AST.ParamDec;
import cop5556sp17.AST.Program;
import cop5556sp17.AST.SleepStatement;
import cop5556sp17.AST.Statement;
import cop5556sp17.AST.Type.TypeName;
import cop5556sp17.AST.WhileStatement;

import java.util.ArrayList;

import cop5556sp17.Scanner.Kind;
import static cop5556sp17.AST.Type.TypeName.*;
import static cop5556sp17.Scanner.Kind.KW_HIDE;
import static cop5556sp17.Scanner.Kind.KW_MOVE;
import static cop5556sp17.Scanner.Kind.KW_SHOW;
import static cop5556sp17.Scanner.Kind.KW_XLOC;
import static cop5556sp17.Scanner.Kind.KW_YLOC;
import static cop5556sp17.Scanner.Kind.OP_BLUR;
import static cop5556sp17.Scanner.Kind.OP_CONVOLVE;
import static cop5556sp17.Scanner.Kind.OP_GRAY;
import static cop5556sp17.Scanner.Kind.OP_HEIGHT;
import static cop5556sp17.Scanner.Kind.OP_WIDTH;
import static cop5556sp17.Scanner.Kind.*;

public class TypeCheckVisitor implements ASTVisitor {

	@SuppressWarnings("serial")
	public static class TypeCheckException extends Exception {
		public TypeCheckException(String message) {
			super(message);
		}
	}

	SymbolTable symtab = new SymbolTable();

	@Override
	public Object visitBinaryChain(BinaryChain binaryChain, Object arg) throws Exception
	{
		binaryChain.getE0().visit(this, arg);
		binaryChain.getE1().visit(this, arg);
		TypeName chainType = binaryChain.getE0().getTypeName();
		ChainElem objChainElem = binaryChain.getE1();
		Kind opKind = binaryChain.getArrow().kind;
		switch(opKind)
		{
			case ARROW:
			{
				switch(chainType)
				{
					case URL:
					case FILE:
						if(objChainElem.getTypeName().isType(IMAGE))
							binaryChain.setType(IMAGE);
						else
							throw new TypeCheckException("Chain is of FILE/URL but chainElem is " + objChainElem.getTypeName().toString());
						break;
					case FRAME:
						if((objChainElem instanceof FrameOpChain) &&
								(objChainElem.getFirstToken().isKind(KW_XLOC) || objChainElem.getFirstToken().isKind(KW_YLOC)))
							binaryChain.setType(INTEGER);
						else if((objChainElem instanceof FrameOpChain) &&
								(objChainElem.getFirstToken().isKind(KW_SHOW) || objChainElem.getFirstToken().isKind(KW_HIDE)
										|| objChainElem.getFirstToken().isKind(KW_MOVE)))
							binaryChain.setType(FRAME);
						else
							throw new TypeCheckException("Chain is of FRAME but chainElem is " + objChainElem.getTypeName().toString());
						break;
					case IMAGE:
						if((objChainElem instanceof ImageOpChain) &&
								(objChainElem.getFirstToken().isKind(OP_WIDTH) || objChainElem.getFirstToken().isKind(OP_HEIGHT)))
							binaryChain.setType(INTEGER);
						else if((objChainElem instanceof FilterOpChain) &&
								(objChainElem.getFirstToken().isKind(OP_GRAY) || objChainElem.getFirstToken().isKind(OP_BLUR)
										|| objChainElem.getFirstToken().isKind(OP_CONVOLVE)))
							binaryChain.setType(IMAGE);
						else if(objChainElem.getTypeName().isType(FRAME))
							binaryChain.setType(FRAME);
						else if(objChainElem.getTypeName().isType(FILE))
							binaryChain.setType(NONE);
						else if((objChainElem instanceof ImageOpChain) &&
								(objChainElem.getFirstToken().isKind(KW_SCALE)))
							binaryChain.setType(IMAGE);
						else if(objChainElem instanceof IdentChain && objChainElem.getTypeName() == IMAGE)
							binaryChain.setType(IMAGE);
						else
							throw new TypeCheckException("Chain is of IMAGE but chainElem is " + objChainElem.getTypeName().toString());
						break;
					case INTEGER:
						if(objChainElem instanceof IdentChain && objChainElem.getTypeName() == INTEGER)
							binaryChain.setType(INTEGER);
						else
							throw new TypeCheckException("Chain is of INTEGER but chainElem is " + objChainElem.getTypeName().toString());
						break;
					default:
						throw new TypeCheckException("Chain is not of IMAGE,FRAME,URL or FILE type");
				}
			}
			break;
			case BARARROW:
				if(chainType.isType(IMAGE) && (objChainElem instanceof FilterOpChain) &&
						(objChainElem.getFirstToken().isKind(OP_GRAY) || objChainElem.getFirstToken().isKind(OP_BLUR)
								|| objChainElem.getFirstToken().isKind(OP_CONVOLVE)))
					binaryChain.setType(IMAGE);
				else
					throw new TypeCheckException("Chain is of IMAGE but chainElem is " + objChainElem.getTypeName().toString());
				break;
			default:
				throw new TypeCheckException("opKind is not BARARROW/ARROW : " + opKind.text);
		}
		return binaryChain;
	}

	@Override
	public Object visitBinaryExpression(BinaryExpression binaryExpression, Object arg) throws Exception
	{
		binaryExpression.getE0().visit(this, arg);
		binaryExpression.getE1().visit(this, arg);
		TypeName e0 = binaryExpression.getE0().getTypeName();
		TypeName e1 = binaryExpression.getE1().getTypeName();
		Kind opKind = binaryExpression.getOp().kind;
		switch(e0)
		{
			case INTEGER:
			switch(e1)
			{
				case INTEGER:
					switch(opKind)
					{
						case PLUS:
						case MINUS:
						case TIMES:
						case DIV:
						case MOD:
							binaryExpression.setType(INTEGER);
							break;
						case LT:
						case GT:
						case LE:
						case GE:
						case EQUAL:
						case NOTEQUAL:
						case AND:
						case OR:
							binaryExpression.setType(BOOLEAN);
							break;
						default:
							throw new TypeCheckException("E0 and E1 not of INTEGER type");
					}
					break;
				case IMAGE:
					switch(opKind)
					{
						case TIMES:
							binaryExpression.setType(IMAGE);
							break;
						default:
							throw new TypeCheckException("E0 is INTEGER but E1 not of IMAGE type");
					}
					break;
				default:
					throw new TypeCheckException("E0 is INTEGER but E1 not of IMAGE/INTEGER type");
			}
			break;
			case IMAGE:
			switch(e1)
			{
				case IMAGE:
					switch(opKind)
					{
					case PLUS:
					case MINUS:
						binaryExpression.setType(IMAGE);
						break;
					case EQUAL:
					case NOTEQUAL:
						binaryExpression.setType(BOOLEAN);
						break;
					default:
						throw new TypeCheckException("E0 and E1 not of IMAGE type");
					}
					break;
				case INTEGER:
					if(opKind.equals(TIMES) || opKind.equals(DIV) || opKind.equals(MOD))
						binaryExpression.setType(IMAGE);
					else
						throw new TypeCheckException("E0 is of IMAGE type but E1 not of INTEGER type");
					break;
				default:
					throw new TypeCheckException("E0 is IMAGE but E1 not of IMAGE/INTEGER type");
			}
			break;
			case BOOLEAN:
				if(e1.isType(BOOLEAN))
				{
					switch(opKind)
					{
						case LT:
						case GT:
						case LE:
						case GE:
						case EQUAL:
						case NOTEQUAL:
						case AND:
						case OR:
							binaryExpression.setType(BOOLEAN);
							break;
						default:
							throw new TypeCheckException("E0 and E1 not of BOOLEAN type");
					}
				}
				else
					throw new TypeCheckException("E0 is BOOLEAN and E1 not of BOOLEAN type");
				break;
		default:
			break;
		}
		if((opKind.equals(EQUAL) || opKind.equals(NOTEQUAL)) && binaryExpression.typeName==null)
		{
			if(e0.isType(e1))
			{
				binaryExpression.setType(BOOLEAN);
				return binaryExpression;
			}
			else
				throw new TypeCheckException("opKind is EQUAL/NOTEQUAL but E1.type != E2.type");
		}
		else if(binaryExpression.typeName != null)
			return binaryExpression;
		else
			throw new TypeCheckException("opKind is not EQUAL or NOTEQUAL");

	}

	@Override
	public Object visitBlock(Block block, Object arg) throws Exception
	{
		try
		{
			symtab.enterScope();
			ArrayList<Dec> decList = block.getDecs();
			for(Dec d:decList)
			{
				Dec tempDec = (Dec) d.visit(this, arg);
				if(tempDec != null)
					continue;
			}
			ArrayList<Statement> statementList = block.getStatements();
			for(Statement s:statementList)
			{
				Statement tempStatement = (Statement) s.visit(this, arg);
				if(tempStatement != null)
					continue;
			}
			symtab.leaveScope();
			return block;
		}
		catch (Exception e)
		{
			String s = e.getMessage() == ""?"Exception caught in Block: " + block.toString():e.getMessage();
			throw new TypeCheckException(s);
		}
	}

	@Override
	public Object visitBooleanLitExpression(BooleanLitExpression booleanLitExpression, Object arg) throws Exception
	{
		try
		{
			booleanLitExpression.setType(BOOLEAN);
			return booleanLitExpression;
		}
		catch (TypeCheckException e)
		{
			throw new TypeCheckException("Illegal Expression in booleanLitExpression: " + booleanLitExpression.toString());
		}
	}

	@Override
	public Object visitFilterOpChain(FilterOpChain filterOpChain, Object arg) throws Exception
	{
		filterOpChain.getArg().visit(this, arg);
		if(filterOpChain.getArg().getExprList().size() == 0)
		{
			filterOpChain.setType(TypeName.IMAGE);
		}
		else
			throw new TypeCheckException("Illegal Chain in FrameOpChain: " + filterOpChain.toString());
		return filterOpChain;
	}

	@Override
	public Object visitFrameOpChain(FrameOpChain frameOpChain, Object arg) throws Exception
	{
		frameOpChain.getArg().visit(this, arg);
		if(frameOpChain.firstToken.isKind(KW_SHOW) || frameOpChain.firstToken.isKind(KW_HIDE))
		{
			if(frameOpChain.getArg().getExprList().size() == 0)
			{
				frameOpChain.setType(TypeName.NONE);
			}
			else
				throw new TypeCheckException("Illegal Chain in FrameOpChain: " + frameOpChain.toString());
		}
		else if (frameOpChain.firstToken.isKind(KW_XLOC) || frameOpChain.firstToken.isKind(KW_YLOC))
		{
			if(frameOpChain.getArg().getExprList().size() == 0)
			{
				frameOpChain.setType(TypeName.INTEGER);
			}
			else
				throw new TypeCheckException("Illegal Chain in FrameOpChain: " + frameOpChain.toString());
		}
		else if(frameOpChain.firstToken.isKind(KW_MOVE))
		{
			if(frameOpChain.getArg().getExprList().size() == 2)
			{
				frameOpChain.setType(TypeName.NONE);
			}
			else
				throw new TypeCheckException("Illegal Chain in FrameOpChain: " + frameOpChain.toString());
		}
		else
			throw new TypeCheckException("Illegal Chain in FrameOpChain: " + frameOpChain.toString());
		return frameOpChain;
	}

	@Override
	public Object visitIdentChain(IdentChain identChain, Object arg) throws Exception
	{
		String identText = identChain.getFirstToken().getText();
		Dec dec = symtab.lookup(identText);
		if(dec != null)
		{
			dec.setType(null);
			identChain.setType(dec.getTypeName());
			identChain.setDec(dec);
		}
		return identChain;
	}

	@Override
	public Object visitIdentExpression(IdentExpression identExpression, Object arg) throws Exception
	{
		String identText = identExpression.getFirstToken().getText();
		Dec decSymTab = symtab.lookup(identText);
		if(decSymTab != null)
		{
			decSymTab.setType(null);
			identExpression.setType(decSymTab.getTypeName());
			identExpression.setDec(decSymTab);
		}
		return identExpression;
	}

	@Override
	public Object visitIfStatement(IfStatement ifStatement, Object arg) throws Exception
	{
		ifStatement.getB().visit(this, arg);
		ifStatement.getE().visit(this, arg);
		if(ifStatement.getE().getTypeName().isType(BOOLEAN))
			return ifStatement;
		else
			throw new TypeCheckException("IfStatement is not of BOOLEAN type");
	}

	@Override
	public Object visitIntLitExpression(IntLitExpression intLitExpression, Object arg) throws Exception
	{
		try
		{
			intLitExpression.setType(INTEGER);
			return intLitExpression;
		}
		catch (TypeCheckException e)
		{
			throw new TypeCheckException("Illegal Expression in intLitExpression: " + intLitExpression.toString());
		}
	}

	@Override
	public Object visitSleepStatement(SleepStatement sleepStatement, Object arg) throws Exception
	{
		sleepStatement.getE().visit(this, arg);
		if(sleepStatement.getE().getTypeName().isType(INTEGER))
			return sleepStatement;
		else
			throw new TypeCheckException("SleepStatement is not of INTEGER type");
	}

	@Override
	public Object visitWhileStatement(WhileStatement whileStatement, Object arg) throws Exception
	{
		whileStatement.getE().visit(this, arg);
		whileStatement.getB().visit(this, arg);
		if(whileStatement.getE().getTypeName().isType(BOOLEAN))
			return whileStatement;
		else
			throw new TypeCheckException("WhileStatement is not of BOOLEAN type");
	}

	@Override
	public Object visitDec(Dec declaration, Object arg) throws Exception
	{
		boolean	bInsert = symtab.insert(declaration.getIdent().getText(), declaration);
		if(bInsert)
		{
			declaration.setType(null);
			return declaration;
		}
		else
			throw new TypeCheckException("Insertion of declaration into Symbol Table failed: " + declaration.toString());
	}

	@Override
	public Object visitProgram(Program program, Object arg) throws Exception
	{
		try
		{
			ArrayList<ParamDec> pList = program.getParams();
			for(ParamDec p:pList)
			{
				p.visit(this, arg);
			}
			Block block = program.getB();
			block.visit(this, arg);
			return program;
		}
		catch (Exception e)
		{
			String s = e.getMessage() == ""?"Parsing of program failed: " + program.toString():e.getMessage();
			throw new TypeCheckException(s);
		}
	}

	@Override
	public Object visitAssignmentStatement(AssignmentStatement assignStatement, Object arg) throws Exception
	{
		assignStatement.getVar().visit(this, arg);
		assignStatement.getE().visit(this, arg);
		if(assignStatement.getVar().getDec().getTypeName().isType(assignStatement.getE().getTypeName()))
			return assignStatement;
		else
			throw new TypeCheckException("IdentLValue of AssignmentStatement is not of Expression type");
	}

	@Override
	public Object visitIdentLValue(IdentLValue identX, Object arg) throws Exception
	{
		//Dec dec = identX.getDec();
		String identText = identX.getText();
		Dec decSymTab = symtab.lookup(identText);
		if(decSymTab != null)
		{
			decSymTab.setType(null);
			identX.setDec(decSymTab);
			identX.dec.setType(null);
		}
		return identX;
	}

	@Override
	public Object visitParamDec(ParamDec paramDec, Object arg) throws Exception
	{
		boolean bInsert = symtab.insert(paramDec.getIdent().getText(), paramDec);
		if(bInsert)
		{
			paramDec.setType(null);
			return paramDec;
		}
		else
			throw new TypeCheckException("Insertion of ParamDec into Symbol Table failed: " + paramDec.toString());
	}

	@Override
	public Object visitConstantExpression(ConstantExpression constantExpression, Object arg)
	{
		try
		{
			constantExpression.setType(INTEGER);
			return constantExpression;
		}
		catch (TypeCheckException e)
		{
			return null;
		}
	}

	@Override
	public Object visitImageOpChain(ImageOpChain imageOpChain, Object arg) throws Exception
	{
		imageOpChain.getArg().visit(this, arg);
		if (imageOpChain.firstToken.isKind(OP_WIDTH) || imageOpChain.firstToken.isKind(OP_HEIGHT))
		{
			if(imageOpChain.getArg().getExprList().size() == 0)
			{
				imageOpChain.setType(INTEGER);
			}
			else
				throw new TypeCheckException("Illegal Chain in imageOpChain " + imageOpChain.toString());
		}
		else if (imageOpChain.firstToken.isKind(KW_SCALE))
		{
			if(imageOpChain.getArg().getExprList().size() == 1)
			{
				imageOpChain.setType(IMAGE);
			}
			else
				throw new TypeCheckException("Illegal Chain in imageOpChain " + imageOpChain.toString());
		}
		return imageOpChain;
	}

	@Override
	public Object visitTuple(Tuple tuple, Object arg) throws Exception
	{
		for(Expression e:tuple.getExprList())
		{
			e.visit(this, arg);
			if(e.getTypeName() != TypeName.INTEGER)
				throw new TypeCheckException("Illegal Expression in Tuple " + e.toString());
		}
		return tuple;
	}


}

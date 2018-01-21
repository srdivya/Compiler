package cop5556sp17.AST;

import cop5556sp17.AST.Type.TypeName;
import cop5556sp17.Scanner.Token;
import cop5556sp17.TypeCheckVisitor.TypeCheckException;


public abstract class Chain extends Statement {

	public Chain(Token firstToken) {
		super(firstToken);
	}
	public TypeName typeName;

	public void setType(TypeName type) throws TypeCheckException
	{
		try
		{
			if(type != null)
				this.typeName = type;
			else
				this.typeName = Type.getTypeName(firstToken);
		}
		catch(Exception e)
		{
			throw new TypeCheckException("Cannot get type of firstToken = " + firstToken.getText());
		}

	}

	public TypeName getTypeName() throws TypeCheckException
	{
		return this.typeName;
	}

	boolean Left;
	public boolean isLeft()
	{
		return Left;
	}

	public void setLeft(boolean left) {
		Left = left;
	}
}

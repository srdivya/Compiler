package cop5556sp17;

import java.util.ArrayList;
import java.util.Arrays;



public class Scanner {
	/**
	 * Kind enum
	 */
	
	public static enum Kind {
		IDENT(""), INT_LIT(""), KW_INTEGER("integer"), KW_BOOLEAN("boolean"), 
		KW_IMAGE("image"), KW_URL("url"), KW_FILE("file"), KW_FRAME("frame"), 
		KW_WHILE("while"), KW_IF("if"), KW_TRUE("true"), KW_FALSE("false"), 
		SEMI(";"), COMMA(","), LPAREN("("), RPAREN(")"), LBRACE("{"), 
		RBRACE("}"), ARROW("->"), BARARROW("|->"), OR("|"), AND("&"), 
		EQUAL("=="), NOTEQUAL("!="), LT("<"), GT(">"), LE("<="), GE(">="), 
		PLUS("+"), MINUS("-"), TIMES("*"), DIV("/"), MOD("%"), NOT("!"), 
		ASSIGN("<-"), OP_BLUR("blur"), OP_GRAY("gray"), OP_CONVOLVE("convolve"), 
		KW_SCREENHEIGHT("screenheight"), KW_SCREENWIDTH("screenwidth"), 
		OP_WIDTH("width"), OP_HEIGHT("height"), KW_XLOC("xloc"), KW_YLOC("yloc"), 
		KW_HIDE("hide"), KW_SHOW("show"), KW_MOVE("move"), OP_SLEEP("sleep"), 
		KW_SCALE("scale"), EOF("eof");

		Kind(String text) {
			this.text = text;
		}

		final String text;

		String getText() {
			return text;
		}
	}
/**
 * Thrown by Scanner when an illegal character is encountered
 */
	@SuppressWarnings("serial")
	public static class IllegalCharException extends Exception {
		public IllegalCharException(String message) {
			super(message);
		}
	}
	
	/**
	 * Thrown by Scanner when an int literal is not a value that can be represented by an int.
	 */
	@SuppressWarnings("serial")
	public static class IllegalNumberException extends Exception {
	public IllegalNumberException(String message){
		super(message);
		}
	}
	

	/**
	 * Holds the line and position in the line of a token.
	 */
	static class LinePos {
		public final int line;
		public final int posInLine;
		
		public LinePos(int line, int posInLine) {
			super();
			this.line = line;
			this.posInLine = posInLine;
		}

		@Override
		public String toString() {
			return "LinePos [line=" + line + ", posInLine=" + posInLine + "]";
		}
	}
		

	

	public class Token {
		public final Kind kind;
		public final int pos;  //position in input array
		public final int length;  

		//returns the text of this Token
		public String getText() {
			if(kind.text == "")
				return chars.substring(pos, pos + length);
			else
				return this.kind.text;
		}
		
		//returns a LinePos object representing the line and column of this Token
		LinePos getLinePos(){
			int[] nLineArr = new int[nLine.size()];
			for(int i = 0; i < nLine.size(); i++)
				nLineArr[i] = nLine.get(i);
			int nInsertPos = Arrays.binarySearch(nLineArr,this.pos);
			if(nInsertPos < 0)
			{
				nInsertPos = (nInsertPos * (-1)) - 2;
			}
			int nPosInLine = this.pos - nLineArr[nInsertPos];// == 0 ? this.pos - nLineArr[nInsertPos]: this.pos - nLineArr[nInsertPos] -1;
			LinePos LPval = new LinePos(nInsertPos, nPosInLine );
			return LPval;
		}
				
		Token(Kind kind, int pos, int length) {
			this.kind = kind;
			this.pos = pos;
			this.length = length;
		}

		/** 
		 * Precondition:  kind = Kind.INT_LIT,  the text can be represented with a Java int.
		 * Note that the validity of the input should have been checked when the Token was created.
		 * So the exception should never be thrown.
		 * 
		 * @return  int value of this token, which should represent an INT_LIT
		 * @throws NumberFormatException
		 */
		public int intVal() throws NumberFormatException{
			int val=0;
			try {
				if(kind == Kind.INT_LIT)
					val = Integer.parseInt(chars.substring(pos, pos+length));
			} 
			catch (NumberFormatException e) 
			{	
				e.printStackTrace();
			}
			
			return val;
		}
		public boolean isKind(Kind kindCheck)
		{
			if (this.kind == kindCheck)
				return true;
			else
				return false;
		}
		@Override
		public int hashCode() 
		{
		   final int prime = 31;
		   int result = 1;
		   result = prime * result + getOuterType().hashCode();
		   result = prime * result + ((kind == null) ? 0 : kind.hashCode());
		   result = prime * result + length;
		   result = prime * result + pos;
		   return result;
		}

		  @Override
		  public boolean equals(Object obj) 
		  {
			   if (this == obj) {
			    return true;
			   }
			   if (obj == null) {
			    return false;
			   }
			   if (!(obj instanceof Token)) {
			    return false;
			   }
			   Token other = (Token) obj;
			   if (!getOuterType().equals(other.getOuterType())) {
			    return false;
			   }
			   if (kind != other.kind) {
			    return false;
			   }
			   if (length != other.length) {
			    return false;
			   }
			   if (pos != other.pos) {
			    return false;
			   }
			   return true;
		  }
		  private Scanner getOuterType() 
		  {
			  return Scanner.this;
		  }
	}

	 public static enum State
	 {
		 START,IN_DIGIT,IN_IDENT,AFTER_EQ, AFTER_EXCL, AFTER_BAR,IN_COMMENT,
		 AFTER_GREAT, AFTER_LESS, AFTER_MINUS, AFTER_FWDSLASH;
	 }

	public ArrayList<Integer> nLine = new ArrayList<Integer>();
	Scanner(String chars) 
	{
		this.chars = chars;
		tokens = new ArrayList<Token>();
	}


	
	/**
	 * Initializes Scanner object by traversing chars and adding tokens to tokens list.
	 * 
	 * @return this scanner
	 * @throws IllegalCharException
	 * @throws IllegalNumberException
	 */
	public Scanner scan() throws IllegalCharException, IllegalNumberException 
	{
		int pos = 0;
		Boolean bInc = false;
	    int length = chars.length();
	    State state = State.START;
	    int startPos = 0; //to store the token position
	    int ch;
		nLine.add(0);
	    while (pos <= length) 
	    {
	        ch = pos < length ? chars.charAt(pos) : -1;
	        if(ch == -1)
	        	break;
	        switch (state) 
	        {
	            case START: 
	            {
	                if(bInc)
	                	pos++;
	            	pos = checkWhiteSpace(pos);
	            	ch = pos < length ? chars.charAt(pos) : -1;
	            	startPos = pos;
	                switch (ch) 
	                {
	                    case -1: 
	                    {
	                    	//tokens.add(new Token(Kind.EOF, pos, 0)); 
	                    	bInc = false;
	                    	startPos = pos;
	                    }
	                    break;
	                    case '+': 
	                    {
	                    	tokens.add(new Token(Kind.PLUS, startPos, 1));
	                    	bInc = true;
	                    	startPos = pos;
	                    	state = State.START;
                    	}
	                    break;
	                    case '*': 
	                    {
	                    	tokens.add(new Token(Kind.TIMES, startPos, 1));
	                    	bInc = true;
	                    	startPos = pos;
                    	}
	                    break;
	                    case '=': 
	                    {
	                    	state = State.AFTER_EQ;
	                    	bInc = true;
	                    }
	                    break;
	                    case '0': 
	                    {
	                    	tokens.add(new Token(Kind.INT_LIT,startPos, 1));
	                    	bInc = true;
	                    	startPos = pos;
	                    }
	                    break;
	                    case '!':
	                    {
	                    	state = State.AFTER_EXCL;
	                    	bInc = true;
	                    }
	                    break;
	                    case '|':
	                    {
	                    	state = State.AFTER_BAR;
	                    	bInc = true;
	                    }
	                    break;
	                    case '>':
	                    {
	                    	state = State.AFTER_GREAT;
	                    	bInc = true;
	                    }
	                    break;
	                    case '<':
	                    {
	                    	state = State.AFTER_LESS;  	
	                    	bInc = true;
	                    }
	                    break;
	                    case '-':
	                    {
	                    	state = State.AFTER_MINUS;
	                    	bInc = true;
	                    }
	                    break;
	                    case '/':
	                    {
	                    	state = State.AFTER_FWDSLASH;
	                    	bInc = true;
	                    }
	                    break;
	                    case '(':
	                    {
	                    	tokens.add(new Token(Kind.LPAREN,startPos,1));
	                    	bInc = true;
	                    	startPos = pos;
	                    }
                    	break;
	                    case ')':
	                    {
	                    	tokens.add(new Token(Kind.RPAREN,startPos,1));
	                    	bInc = true;
	                    	startPos = pos;
	                    }
	                    break;
	                    case '{':
	                    {
	                    	tokens.add(new Token(Kind.LBRACE,startPos,1));
	                    	bInc = true;
	                    	startPos = pos;
	                    }
                    	break;
	                    case '}':
	                    {
	                    	tokens.add(new Token(Kind.RBRACE,startPos,1));
	                    	bInc = true;
	                    	startPos = pos;
	                    }
	                    break;
	                    case '&':
	                    {
	                    	tokens.add(new Token(Kind.AND,startPos,1));
	                    	bInc = true;
	                    	startPos = pos;
	                    }
	                    break;
	                    case ';':
	                    {
	                    	//System.out.println("In ;");
	                    	tokens.add(new Token(Kind.SEMI,startPos,1));
	                    	//System.out.println("pos =" + pos);
	                    	bInc = true;
	                    	startPos = pos;
	                    }
	                    break;
	                    case ',':
	                    {
	                    	//System.out.println("In ,");
	                    	tokens.add(new Token(Kind.COMMA,startPos,1));
	                    	//System.out.println("pos =" + pos);
	                    	bInc = true;
	                    	startPos = pos;
	                    }
	                    break;
	                    case '%':
	                    {
	                    	//System.out.println("In ,");
	                    	tokens.add(new Token(Kind.MOD,startPos,1));
	                    	//System.out.println("pos =" + pos);
	                    	bInc = true;
	                    	startPos = pos;
	                    }
	                    break;
	                    default: 
	                    {
	                        if (Character.isDigit(ch)) 
                        	{
                        		state = State.IN_DIGIT;
                        		bInc = true;	                        		 
                        	}
	                        else if (Character.isJavaIdentifierStart(ch)) 
	                        {
	                             state = State.IN_IDENT;
	                             bInc = true;
	                        } 
	                        else 
	                        {
	                        	throw new IllegalCharException("illegal char " +(char)ch+" at pos "+pos);
	                        }
	                    }
	                }
	            }break;
	            case IN_DIGIT: 
	            {
	            	while (pos < chars.length())
	            	{
	            	    if(Character.isDigit(chars.charAt(pos)))
	            	    	pos++;
	            	    else
	            	    	break;
	            	}
	            	String strTemp = chars.substring(startPos,pos);
            		try
            		{
            			int val = Integer.parseInt(strTemp);
            			tokens.add(new Token(Kind.INT_LIT, startPos, pos - startPos));
            			bInc = false;
            			state = State.START;
            		}
            		catch(Exception e)
            		{
            			throw new IllegalNumberException("Integer value can only be upto 2,147,483,647");
            		} 
	            	
	            }break;
	            case IN_IDENT: 
	            {
	            	while(pos < chars.length() && Character.isJavaIdentifierPart(chars.charAt(pos)))  //change to while
	            	{
	                    pos++;
                    	//System.out.println("In in ident if pos=" + pos);
	            	}
	            	Kind kValue = null;
            		String strTemp = chars.substring(startPos,pos); //check if its -1 or just pos
            		switch (strTemp)
            		{
            			case "integer":
            				//System.out.println("In int");
            				kValue = Kind.KW_INTEGER;
            				break;
            			case "boolean":
            				kValue = Kind.KW_BOOLEAN;
            				break;
            			case "image":
            				kValue = Kind.KW_IMAGE;
            				break;
            			case "url":
            				kValue = Kind.KW_URL;
            				break;
            			case "file":
            				kValue = Kind.KW_FILE;
            				break;
            			case "frame":
            				kValue = Kind.KW_FRAME;
            				break;
            			case "while":
            				kValue = Kind.KW_WHILE;
            				break;
            			case "if":
            				kValue = Kind.KW_IF;
            				break;
            			case "sleep":
            				kValue = Kind.OP_SLEEP;
            				break;
            			case "screenheight":
            				kValue = Kind.KW_SCREENHEIGHT;
            				break;
            			case "screenwidth":
            				kValue = Kind.KW_SCREENWIDTH;
            				break;
            			case "gray":
            				kValue = Kind.OP_GRAY;
            				break;
            			case "convolve":
            				kValue = Kind.OP_CONVOLVE;
            				break;
            			case "blur":
            				kValue = Kind.OP_BLUR;
            				break;
            			case "scale":
            				kValue = Kind.KW_SCALE;
            				break;
            			case "width":
            				kValue = Kind.OP_WIDTH;
            				break;
            			case "height":
            				kValue = Kind.OP_HEIGHT;
            				break;
            			case "xloc":
            				kValue = Kind.KW_XLOC;
            				break;
            			case "yloc":
            				kValue = Kind.KW_YLOC;
            				break;
            			case "hide":
            				kValue = Kind.KW_HIDE;
            				break;
            			case "show":
            				kValue = Kind.KW_SHOW;
            				break;
            			case "move":
            				kValue = Kind.KW_MOVE;
            				break;
            			case "true":
            				kValue = Kind.KW_TRUE;
            				break;
            			case "false":
            				kValue = Kind.KW_FALSE;
            				break;
        				default:
        					while(pos < chars.length() && Character.isJavaIdentifierPart(chars.charAt(pos)))
        					{ 
        						pos++;
        					}
        					kValue = Kind.IDENT;
        					break;
            		} 
            		bInc = false;
					tokens.add(new Token(kValue, startPos, pos - startPos));
					//System.out.println(tokens.size() + "token=" + strTemp);
					//pos++;
					startPos = pos;
					state = State.START;
        		}break;
	            case AFTER_EQ: 
	            {
	            	if(pos+1 < chars.length())
	            	{	
		            	if(chars.charAt(pos+1) == '=')
		            	{
		            		tokens.add(new Token(Kind.EQUAL, startPos, 2));
		            		pos+=2;
		            		state = state.START;
		            		bInc = false; 
		            	}
		            	else
		            		throw new IllegalCharException("Exception found at Char " + chars.charAt(pos+1) + " at pos = " + pos+1);
	            	}
	            	else
	            		//state = state.START;
	            		throw new IllegalCharException("Char " + chars.charAt(pos));
	            }  break;
	            case AFTER_EXCL: 
	            {
	            	if(pos+1 < chars.length())
	            	{	
		            	if(chars.charAt(pos+1) == '=')
		            	{
		            		tokens.add(new Token(Kind.NOTEQUAL, startPos, 2));
		            		pos = pos + 2;
		            		state = state.START;
		            		bInc = false; 
		            	}
		            	else
		            	{
		            		tokens.add(new Token(Kind.NOT, startPos, 1));
		            		pos++;
		            		state = state.START;
		            		bInc = false; 
		            	}
	            	}
	            	else
	            	{
	            		tokens.add(new Token(Kind.NOT, startPos, 1));
	            		pos++;
	            		state = state.START;
	            		bInc = false; 
	            	}
	            }  break;
	            case AFTER_BAR: 
	            {
	            	if(pos+2 < chars.length())
	            	{
		            	if(chars.charAt(pos+1) == '-' && chars.charAt(pos+2) == '>')
		            	{
		            		tokens.add(new Token(Kind.BARARROW, startPos, 3));
		            		pos = pos + 3;
		            		state = state.START;
		            		bInc = false; 
		            	}
		            	else
		            	{
		            		tokens.add(new Token(Kind.OR, startPos, 1));
		            		pos ++;
		            		state = state.START;
		            		bInc = false; 
		            	}
	            	}
	            	else
	            	{
	            		tokens.add(new Token(Kind.OR, startPos, 1));
	            		pos ++;
	            		state = state.START;
	            		bInc = false; 
	            	}
	            }  break;
	            case AFTER_GREAT: 
	            {	
	            	if(pos+1 < chars.length())
	            	{	
		            	if(chars.charAt(pos+1) == '=') //>=
		            	{
		            		tokens.add(new Token(Kind.GE, startPos, 2));
		            		pos= pos + 2;
		            		state = state.START;
		            		bInc = false; 
		            	}
		            	else
		            	{
		            		tokens.add(new Token(Kind.GT, startPos, 1));
		            		pos ++;
		            		state = state.START;
		            		bInc = false; 
		            	}
	            	}
	            	else
	            	{
	            		tokens.add(new Token(Kind.GT, startPos, 1));
	            		pos ++;
	            		state = state.START;
	            		bInc = false; 
	            	}
	            }  break;
	            case AFTER_LESS: 
	            {
	            	if(pos+1 < chars.length())
	            	{	
		            	if(chars.charAt(pos+1) == '=')
		            	{
		            		tokens.add(new Token(Kind.LE, startPos, 2));
		            		pos = pos + 2;
		            		state = state.START;
		            		bInc = false; 
		            	}
		            	else if(chars.charAt(pos+1) == '-')
		            	{
		            		tokens.add(new Token(Kind.ASSIGN, startPos, 2));
		            		pos += 2;
		            		state = state.START;
		            		bInc = false; 
		            	}
		            	else
		            	{
		            		tokens.add(new Token(Kind.LT, startPos, 1));
		            		pos++;
		            		state = state.START;
		            		bInc = false; 
		            	}
	            	}
	            	else
	            	{
	            		tokens.add(new Token(Kind.LT, startPos, 1));
	            		pos ++;
	            		state = state.START;
	            		bInc = false; 
	            	}
	            }  break;
	            case AFTER_MINUS: 
	            {
	            	if(pos+1 < chars.length())
	            	{	
		            	if ( chars.charAt(pos+1) == '>')
		            	{
		            		tokens.add(new Token(Kind.ARROW, startPos, 2));
		            		pos = pos + 2;
		            		state = state.START;
		            		bInc = false; 
		            	}
		            	else
		            	{
		            		tokens.add(new Token(Kind.MINUS, startPos, 1));
		            		pos++;
		            		state = state.START;
		            		bInc = false; 
		            	}	
	            	}
	            	else
	            	{
	            		tokens.add(new Token(Kind.MINUS, startPos, 1));
	            		pos ++;
	            		state = state.START;
	            		bInc = false; 
	            	}
            	}  break;
	            case AFTER_FWDSLASH: //can be either a comment or a div symbol
	            {
	            	if(pos+1 < chars.length())
	            	{
		            	if(chars.charAt(pos+1) == '*')
		            	{	
		            		pos++;
		            		state = state.IN_COMMENT;
		            		bInc = false; 
		            	}
	            	
		            	else
		            	{
		            		tokens.add(new Token(Kind.DIV, startPos, 1));
		            		pos ++;
		            		state = state.START;
		            		bInc = false; 
		            	}
	            	}
	            	else
	            	{
	            		tokens.add(new Token(Kind.DIV, startPos, 1));
	            		pos ++;
	            		state = state.START;
	            		bInc = false; 
	            	}break;
	            }
	            case IN_COMMENT:
	            {	
	            	bInc = false;
	            	if(pos+2 < chars.length()) //while loop here
	            	{
		            	if (chars.charAt(pos) == '\n')
		            		nLine.add(pos + 1);
		            	else if(chars.charAt(pos) == '*' && chars.charAt(pos+1) == '/')
		            	{
		            		pos++;
		            		state = State.START;
		            		bInc = false;
		            	}	
		            	pos++;
	            	}
	            	else if (chars.charAt(pos) == '\n')
	            	{
	            		nLine.add(pos + 1);
	            		pos++;
	            	}
	            	else if(ch==-1)
	            		break;
	            	else
	            	{
	            		pos++;
	            		break;
	            	}
	            }
	            break;
	            default:  assert false;
	        }// switch(state)
	    } // while
	    //System.out.println("token count " +tokens.size() );
	    tokens.add(new Token(Kind.EOF,pos,0));
	    /*for(int i=0;i<tokens.size();i++)
	    	System.out.println("token " + tokens.get(i).kind);*/
	    return this;

		
		//return this;  
	}
	int checkWhiteSpace(int nPos) //add for 0 as well
	{
		while(nPos < chars.length()) //check for EOF
		{
			if (Character.isWhitespace(chars.charAt(nPos)))
			{
				if(chars.charAt(nPos) == '\n')
					nLine.add(nPos + 1);
				nPos++;
			}
			else return nPos;
		}
		return nPos;
	}


	final ArrayList<Token> tokens;
	final String chars;
	int tokenNum;

	/*
	 * Return the next token in the token list and update the state so that
	 * the next call will return the Token..  
	 */
	public Token nextToken() {
		if (tokenNum >= tokens.size())
			return null;
		return tokens.get(tokenNum++);
	}
	
	/*
	 * Return the next token in the token list without updating the state.
	 * (So the following call to next will return the same token.)
	 */
	public Token peek(){
		if (tokenNum >= tokens.size())
			return null;
		return tokens.get(tokenNum);		
	}

	

	/**
	 * Returns a LinePos object containing the line and position in line of the 
	 * given token.  
	 * 
	 * Line numbers start counting at 0
	 * 
	 * @param t
	 * @return
	 */
	public LinePos getLinePos(Token t) 
	{
		return t.getLinePos();
	}
}
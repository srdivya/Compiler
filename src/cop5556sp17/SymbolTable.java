package cop5556sp17;

import java.util.*;
import cop5556sp17.TypeCheckVisitor.TypeCheckException;
import cop5556sp17.AST.Dec;

public class SymbolTable 
{
	public int  current_scope, next_scope;
	Stack<Integer> scope_stack;
	HashMap<String, HashMap<Integer,Dec>> mapTable; 

	/** 
	 * to be called when block entered
	 */
	public void enterScope()
	{
		current_scope = next_scope++; 
		scope_stack.push(current_scope);
	}
	
	/**
	 * leaves scope
	 */
	public void leaveScope()
	{
		scope_stack.pop();
		current_scope = scope_stack.peek();
	}
	
	public boolean insert(String ident, Dec dec) throws TypeCheckException
	{
		//HashMap<Integer,Dec> retMap = null;
		if(mapTable.containsKey(ident))
		{
			HashMap<Integer,Dec> decMap = mapTable.get(ident);
			if(decMap.containsKey(current_scope))
				throw new TypeCheckException("Ident " + ident +" is already declared.");
			decMap.put(current_scope, dec);
			mapTable.put(ident,decMap);
		}
		else
		{
			HashMap<Integer,Dec> decMap = new HashMap<Integer,Dec>();
			decMap.put(current_scope, dec);
			mapTable.put(ident,decMap);
		}
		if(mapTable.get(ident) == null)
			return false;
		else
			return true;
	}
	
	public Dec lookup(String ident)
	{
		Dec dec = null;
		HashMap<Integer, Dec> decMap = mapTable.get(ident);
		int scope;
		ListIterator<Integer> scopeIterator = scope_stack.listIterator((int)scope_stack.size());
		
		while(scopeIterator.hasPrevious())//.hasNext())
		{
			scope = scopeIterator.previous();//.next();
			if(decMap.get(scope) != null)
			{
				dec = decMap.get(scope);
				break;
			}
			else
				continue;
		}
		return dec;
	}
	
	public SymbolTable() 
	{
		current_scope = 0;
		scope_stack = new Stack<Integer>();
		scope_stack.push(0);
		mapTable = new HashMap<String, HashMap<Integer,Dec>>();
		next_scope = current_scope + 1;
	}


	@Override
	public String toString() 
	{
		return "SymbolTable [current_scope=" + current_scope + ", next_scope=" + next_scope + ", scope_stack="
				+ scope_stack + ", mapTable=" + mapTable + "]";
	}
	
	


}

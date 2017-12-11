package apps;




import java.io.*;
import java.util.*;




import structures.Stack;




public class Expression {




	/**
	 * Expression to be evaluated
	 */
	String expr;                

	/**
	 * Scalar symbols in the expression 
	 */
	ArrayList<ScalarSymbol> scalars;   

	/**
	 * Array symbols in the expression
	 */
	ArrayList<ArraySymbol> arrays;

	/**
	 * String containing all delimiters (characters other than variables and constants), 
	 * to be used with StringTokenizer
	 */
	public static final String delims = " \t*+-/()[]";

	/**
	 * Initializes this Expression object with an input expression. Sets all other
	 * fields to null.
	 * 
	 * @param expr Expression
	 */
	public Expression(String expr) {
		this.expr = expr;
	}




	/**
	 * Populates the scalars and arrays lists with symbols for scalar and array
	 * variables in the expression. For every variable, a SINGLE symbol is created and stored,
	 * even if it appears more than once in the expression.
	 * At this time, values for all variables are set to
	 * zero - they will be loaded from a file in the loadSymbolValues method.
	 */
	public void buildSymbols() {
		arrays = new ArrayList<ArraySymbol>();
		scalars = new ArrayList<ScalarSymbol>();
		StringTokenizer st = new StringTokenizer(expr, delims);
		String str = "";
		while (st.hasMoreTokens()){
			str = st.nextToken();
			if (Character.isLetter(str.charAt(0)) == true){
				int index = expr.indexOf(str);
				int size = str.length();
				if (index + size + 1 > expr.length()){
					scalars.add(new ScalarSymbol(str));
					break;
				}
				else if(str.matches("\\d+")){

				}
				else if (expr.charAt(index + size) == '['){
					ArraySymbol tempA = new ArraySymbol(str);
					if (arrays.contains(tempA)){
						continue;
					}

					arrays.add(new ArraySymbol(str));
				}
				else {
					ScalarSymbol tempS = new ScalarSymbol(str);
					if (scalars.contains(tempS)){
						continue;
					}
					scalars.add(new ScalarSymbol(str));
				}
			}
		}
		System.out.println(arrays);
		System.out.println(scalars);
	}


	/**
	 * Loads values for symbols in the expression
	 * 
	 * @param sc Scanner for values input
	 * @throws IOException If there is a problem with the input 
	 */
	public void loadSymbolValues(Scanner sc) 
			throws IOException {
		while (sc.hasNextLine()) {
			StringTokenizer st = new StringTokenizer(sc.nextLine().trim());
			int numTokens = st.countTokens();
			String sym = st.nextToken();
			ScalarSymbol ssymbol = new ScalarSymbol(sym);
			ArraySymbol asymbol = new ArraySymbol(sym);
			int ssi = scalars.indexOf(ssymbol);
			int asi = arrays.indexOf(asymbol);
			if (ssi == -1 && asi == -1) {
				continue;
			}
			int num = Integer.parseInt(st.nextToken());
			if (numTokens == 2) { // scalar symbol
				scalars.get(ssi).value = num;
			} else { // array symbol
				asymbol = arrays.get(asi);
				asymbol.values = new int[num];
				// following are (index,val) pairs
				while (st.hasMoreTokens()) {
					String tok = st.nextToken();
					StringTokenizer stt = new StringTokenizer(tok," (,)");
					int index = Integer.parseInt(stt.nextToken());
					int val = Integer.parseInt(stt.nextToken());
					asymbol.values[index] = val;              
				}
			}
		}
	}


	/**
	 * Evaluates the expression, using RECURSION to evaluate subexpressions and to evaluate array 
	 * subscript expressions.
	 * 
	 * @return Result of evaluation
	 */
	public float evaluate() {
		//might not have to use private method
		expr.trim();
		Stack<String> operator = new Stack<String>(); 
		Stack<Float> values = new Stack<Float>();
		Stack<String> tempO = new Stack<String>(); //use to evaluate the expression in the right order
		Stack<String> tempN = new Stack<String>(); //push orig stack into temp and pop it back out
		float result = 0;
		StringTokenizer stoke = new StringTokenizer(expr, delims, true);
		String tk;// = stoke.nextToken();
		if((!expr.contains("+") || !expr.contains("-") || !expr.contains("/") || !expr.contains("*") || !expr.contains("[") || expr.contains("(")) && (!Character.isLetter(expr.charAt(0)))){
			return onlyNumber(expr);
		}
		if((!expr.contains("+") || !expr.contains("-") || !expr.contains("/") || !expr.contains("*") || !expr.contains("[") || expr.contains("(")) && (Character.isLetter(expr.charAt(0)))){
			
			return getValue(expr);
		}
		while (stoke.hasMoreTokens()){
			tk = stoke.nextToken();
			//put variables and numbers into stacks
			if (!Character.isDigit(tk.charAt(0)) && !Character.isLetter(tk.charAt(0))){
				operator.push(tk);
			}
			else if (isNumber(tk) == false){ //check if number or variable
				values.push(getValue(tk));
			}
			else{
				values.push(Float.parseFloat(tk));
			}

			if (!expr.contains("(") && !expr.contains("{")){
				//evaluate the expression, no need for stacks/recursion
			}
			
			//tk = stoke.nextToken();
			
			
			float val1 = values.pop();
			float val2 = values.pop();
			System.out.println(val1);
			System.out.println(val2);
			result = orderOfOperations(val1, val2, operator.pop());

		}
		return result;
	}

	private float getValue(String input){
		int index = 0;
		for(int i = 0; i < scalars.size(); i++){
			if(scalars.get(i).name.equals(input)){
				index = i;
			}
		}
		float value = scalars.get(index).value;
		return value;
	}
	
	private boolean isNumber(String abc){
		try{
			Float.parseFloat(abc);
		}
		catch(Exception ex){
			return false;
		}
		return true;

	}

	private float eval(String str){
		str.trim();
		if (str.contains("(")){
			str = str.substring(str.indexOf("("), str.indexOf(")"));
		}
		eval(str);
		return 0;
		//do recursion here
	}

	private float orderOfOperations(float y, float w, String i){
		if(i.equals("+")){
			float answer = y + w;
			return answer;
		}
		if(i.equals("/")){
			float answer = y/w;
			return answer;
		}
		if(i.equals("*")){
			float answer = y*w;
			return answer;
		}
		if (i.equals("-")){
			float answer = y-w;
			return answer;
		}
		expr.trim();
		//do pemdas and math here
		return 0;
	}

	private float onlyNumber(String expression){
		return Float.parseFloat(expression);
	}


	/**
	 * Utility method, prints the symbols in the scalars list
	 */
	public void printScalars() {
		for (ScalarSymbol ss: scalars) {  
			System.out.println(ss);
		}
	}

	/**
	 * Utility method, prints the symbols in the arrays list
	 */
	public void printArrays() {
		for (ArraySymbol as: arrays) {
			System.out.println(as);
		}
	}




}












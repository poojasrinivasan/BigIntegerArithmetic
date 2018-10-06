/**
 * @author Akhila Perabe, Pooja Srinivasan, Shreeya Girish Degaonkar
 * 
 *  Num class implementation for numbers larger than MAX(Long)
 *  
 *  Version History
 *  1.0		09/05/2018 Base Code					
 *  2.0		09/20/2018 Implemented member functions
 *  3.0		09/25/2018 Performance improvements
 */

package axp178830;

import java.util.ArrayDeque;

public class Num implements Comparable<Num> {

	static long defaultBase = 1000000000;
	long base = defaultBase;
	
	long[] arr; // array to store arbitrarily large integers
	boolean isNegative; // boolean flag to represent negative numbers
	int len; // actual number of elements of array that are used; number is stored in
				// arr[0..len-1]

	/**
	 * Constructor for Num object with defaultBase
	 * @param s		Number in string format
	 */
	public Num(String s) {
		this(s,defaultBase);
	}

	/**
	 * Constructor for Num object with the specified base
	 * @param s		Number in the string format
	 * @param base	Base for the number storage
	 */
    private Num(String s, long base) {
		// covert s to base 10
		this.base = base;
		char[] characters = s.toCharArray();
		int index = 0;
		if(characters[0] == '-'){
			index++;
		}
		// remove starting zeroes
		while(index < characters.length && characters[index]=='0' ){
			index++;
		}
		arr = new long[1];
		Num res = new Num(0, base);
		Num ten = new Num(10, base);
		while(index < characters.length){
			res = add(product(res,ten),new Num(Character.getNumericValue(characters[index]), base));
			index++;
		}
		//res contains number in decimal format
		if(characters[0] == '-')
			this.isNegative = true;
		this.arr = res.arr;
		this.len = res.len;
	}
	
    /**
     * Constructor for Num object with default base
     * @param x		Number in long
     */
	public Num(long x) {
		this(x,defaultBase);
	}

	/**
	 * Constructor for Num object with base specified
	 * @param x		Number in long
	 * @param base	Base for the number storage
	 */
	private Num(long x, long base) {
		this.base = base;
		
		// In case num 0, store 0 in arr[0]
		if(x==0) {
			arr = new long[1];
			arr[0] = 0;
			this.len = 1;
		}
		else{
			//Divide x by base and save it in array
			long val = x < 0 ? -x : x;

			int maxLongDigits = 19;
			int numDigits = (maxLongDigits+1)/ (int)Math.log10(base) + 1;
			arr = new long[numDigits];

			int count = 0;
			while (val > 0) {
				long rem = val % base;
				val = val / base;
				arr[count] = rem;
				count++;
			}

			// Set length
			len = count;

			// Set isNegative
			if (x < 0) {
				isNegative = true;
			} else {
				isNegative = false;
			}

		}
	}
	
	/**
	 * Returns a Num object with value 1
	 * @return
	 */
	private static Num one() {
		return new Num(1);
	}
	
	/**
	 * Returns a Num object with value 0
	 * @return
	 */
	private static Num zero() {
		return new Num(0);
	}

	
	/**
	 * Adds the given parameter a and b
	 * @param a
	 * @param b
	 * @return sum, of type Num, which is the sum of a and b 
	 */
	private static Num addActual(Num a, Num b) {
		long base = a.base();
		Num sum = new Num(0, base);
		sum.arr = new long[Math.max(a.len, b.len)+1];
		sum.len = Math.max(a.len, b.len);
		int i = 0;
		long rem = 0;
		while(i < a.len && i < b.len) {
			long s = a.arr[i] + b.arr[i] + rem;
			sum.arr[i] = s%base;
			rem = s/base;
			i++;
		}
		while(i < a.len) {
			long s = a.arr[i] + rem;
			sum.arr[i] = s%base;
			rem = s/base;
			i++;
		}
		while(i < b.len) {
			long s = b.arr[i] + rem;
			sum.arr[i] = s%base;
			rem = s/base;
			i++;
		}
		if(rem > 0) {
			sum.arr[i] = rem;
			sum.len++;
		}
		
		return sum;
	}

	/**
	 * checks for the of a and b and performs addition or subtraction
	 * @param a
	 * @param b
	 * @return result, a Num which is the sum of a and b
	 */
	public static Num add(Num a, Num b) {
		long base = a.base();
		if(base != b.base()) {
			b = b.convertBase(base);
		}
		
		Num result;
		if(a.isNegative && b.isNegative) {
			result = addActual(a,b);
			result.isNegative = true;
		}
		else if (!a.isNegative && !b.isNegative) {
			result  = addActual(a,b);
		} else {
			b.isNegative = !b.isNegative;
			result = subtract(a,b);
			b.isNegative = !b.isNegative;
			if(a.compareAbs(b) < 0) {
				result.isNegative = b.isNegative;
			} else {
				result.isNegative = a.isNegative;
			}
		}

		return result;
	}

	/**
	 * subtracts number b that is smaller from a
	 * @param a
	 * @param b
	 * @return diff, of type Num, which is the difference between a and b 
	 */
	private static Num subtractActual(Num a, Num b) {
		long base = a.base();
		Num diff = new Num(0, base);
		diff.arr = new long[Math.max(a.len, b.len)];
		int i = 0;
		//create cope of first array 'a'
		long[] first = new long[a.len];
		for(int k = 0;k < a.len; k++){
			first[k] = a.arr[k];
		}
		while(i < a.len && i < b.len) {
			if(first[i] >= b.arr[i]) {
				diff.arr[i] = first[i] - b.arr[i];
			} else {
				int j = i+1;
				
				//Borrow from the last available digit
				while (j < a.len && first[j] <= 0) {
					first[j] = base-1;
					j++;
				}
				
				first[j]--;
				first[i] += base;
				
				diff.arr[i] = first[i] - b.arr[i];
			}
			i++;
		}
		while(i < a.len) {
			diff.arr[i] = first[i];
			i++;
		}
		
		//Set length
		int count = diff.arr.length;
		while(diff.arr[count-1] <= 0) {
			count--;
		}
		diff.len = count;
		return diff;
	}

	/**
	 * compares number a and b and based on sign, performs addition or subtraction
	 * @param a
	 * @param b
	 * @return result, of type Num, the difference between a and b
	 */
	public static Num subtract(Num a, Num b) {
		long base = a.base();
		if(base != b.base()) {
			b = b.convertBase(base);
		}
		
		Num result;
		if(a.isNegative && !b.isNegative) {
			result = addActual(a,b);
			result.isNegative = true;
		}
		else if(!a.isNegative && b.isNegative) {
			result = addActual(a,b);
			result.isNegative= false;
		}
		else {
			b.isNegative = !b.isNegative;
			if(a.compareAbs(b) < 0) {
				result = subtractActual(b,a);
				result.isNegative = b.isNegative;
			} else if(a.compareAbs(b) > 0) {
				result = subtractActual(a,b);
				result.isNegative = a.isNegative;
			} else {
				result = zero();
			}
			b.isNegative = !b.isNegative;
		}
		return result;
	}

	/**
	 * performs product of a and b using normal multiplication using O(n2) method
	 * @param a
	 * @param b
	 * @return res, a Num which is the product of a and b
	 */
	public static Num product(Num a, Num b) {
		Num res = new Num(0,a.base());
		Num zero = new Num(0,a.base());
		
		if (b.base()!=a.base()){
			b.convertBase(a.base);
		}
		
		if(a.compareTo(zero)==0 || b.compareTo(zero)==0) return res;
		
		res.arr = new long[a.len+b.len];
		long base = a.base();
		for(int i = 0; i < a.len; i++){
			for(int j = 0; j < b.len;j++){
				long product = a.arr[i] * b.arr[j];
				int remainderIndex = i+j;
				int quotientIndex = i+j+1;
				long total = res.arr[remainderIndex]+product;
				res.arr[remainderIndex]=total%base;
				res.arr[quotientIndex]+=total/base;
			}
		}
		
		//Update result length
		res.len = a.len+b.len;
		int index = a.len+b.len-1;
		while(res.arr[index--]==0){
			res.len--;
		}
		
		//Update result sign
		if(a.isNegative&&b.isNegative){
			res.isNegative = false;
		}
		else if(a.isNegative || b.isNegative){
			res.isNegative = true;
		}
		
		return res;
	}

	/**
	 * calculates a ^ n using divide and conquer
	 * @param a
	 * @param n
	 * @return prod, a Num which is pow(a,n)
	 */
	public static Num power(Num a, long n) {
		
		if(n==0) return one();
        if(n==1) return a;
        
        Num val= power(a,n/2);
        Num prod = product(val,val);
        if(n%2==0){
        	return prod;
		}
		return product(prod,a);
	}

	/**
	 * method to calculate division using binary search
	 * @param a
	 * @param b
	 * @return mid, a Num which is the quotient of a/b 
	 */
	public static Num divide(Num a, Num b) {
		
		//Check for divide by 0 case
		Num one = one();
		Boolean aSign = a.isNegative;
		Boolean bSign = b.isNegative;
		if(b.len==0 || (b.len==1 && b.arr[0] == 0)) {
			throw new ArithmeticException("Error: Divide by zero");
	    }
	    if(a.compareAbs(b) < 0){
	    	return zero();
		}
		if(a.compareAbs(b)==0){
			if(aSign!=bSign)
				one.isNegative = true;
			return one;
		}
	    if(b.compareAbs(one)==0){
	    	return new Num(a.toString());
		}

		// Convert base of b if not same as a
		long base = a.base();
		if(base != b.base()) {
			b = b.convertBase(base);
		}

		a.isNegative = b.isNegative  = false;

		Num mid = new Num(0, base);
		Num low = new Num(1, base);
		//Num high = new Num(a.toString(), base);
		Num high = a.by2();
		while(low.compareTo(high)<=0) {
			mid = Num.add(high, low).by2();
			Num prod = Num.product(mid, b);
			//Num diff = Num.subtract(prod, a);
            if(prod.compareTo(a) == 1){
            	high = subtract(mid,new Num(1, base));
			}
			else if(product(add(mid,new Num(1, base)),b).compareTo(a) != 1){
				low = add(mid,new Num(1, base));
			}
			else break;
		}

		if(aSign != bSign) mid.isNegative = true;
		
		//Revert the sign of a and b
		a.isNegative = aSign;
		b.isNegative = bSign;
		return mid;
	}

	/**
	 * calculates mod using a - b*(a/b)
	 * @param a
	 * @param b
	 * @return returns result, a Num which is a%b
	 */
		public static Num mod(Num a, Num b) {
			if(b.compareTo(zero())==0) return null;
			long base = a.base();
			Num result=new Num(1, base);
			Num quotient=new Num(0, base);
			Num prod=new Num(0, base);
			if(a.compareTo(b)<0) {
				return a;
			}
			else if(a.compareTo(b)==0) {
				return zero();
			}
			else {
				//result.arr=new long[a.len];
				//result.len=a.arr.length;
				quotient = divide(a,b);
				prod=product(b,quotient);
				result = subtract(a,prod);
				return result;	
			}
		}

	/**
	 * method to calculate square root using binary search
	 * @param a
	 * @return result, a Num which is sqrt(a)
	 */
	public static Num squareRoot(Num a) {
		Num result=new Num(0);
		//Check for negative numbers
		if(a.isNegative) {
			System.out.println("Error: Square root of negative number");
			return null;
		}
				
		//Check for 0 or 1
		if(a.len==0) {
			System.out.println("Error: Enter some number");
		return null;
	}
		if(a.len==1 && (a.compareAbs(new Num(4)) <= 0)) {
			result = new Num(1);
			return result;
		}

		
		Num low = new Num(0, a.base());
		Num high = a.by2();
		
		while(low.compareTo(high) <= 0) {
			Num mid = Num.add(high, low).by2();
			int c = Num.product(mid, mid).compareTo(a);
			// This is a perfect square
			if(c == 0) {
				return mid;
			}
			if(c < 0) {
				low = Num.add(mid, new Num(1, a.base()));
				result = mid;
			} else {
				high = Num.subtract(mid, new Num(1, a.base()));
			}
		}
		
		return result;
	}

	/**
	 * method to compare magnitude of two numbers
	 * @param other
	 * @return an integer (0 when a==b, 1 when a>b or -1 when a<b)
	 */
	private int compareAbs(Num other) {
			int flag = 0;
			if (this.len < other.len) {
				flag = -1;
			} else if (other.len < this.len) {
				flag =  1;
			} else {
				int i = this.len - 1;
				while (i >= 0) {
					if (this.arr[i] > other.arr[i]) {
						flag = 1;
						break;
					} else if (this.arr[i] < other.arr[i]) {
						flag = -1;
						break;
					} else {
						i--;
					}
				}
			}
			return flag;
	}

	/**
	 * method to compare 2 num objects
	 * @param other
	 * @return an integer that determines which of the 2 objects is greater
	 */
	public int compareTo(Num other) {
		if(this.isNegative != other.isNegative) {
			return this.isNegative ? -1 : 1;
		} else {
			int flag = 0;
			if (this.len < other.len) {
				flag = -1;
			} else if (other.len < this.len) {
				flag =  1;
			} else {
				int i = this.len - 1;
				while (i >= 0) {
					if (this.arr[i] > other.arr[i]) {
						flag = 1;
						break;
					} else if (this.arr[i] < other.arr[i]) {
						flag = -1;
						break;
					} else {
						i--;
					}
				}
			}
			if(this.isNegative && other.isNegative) {
				return -flag;
			}
			return flag;
		}
	}

	/**Output using the format "base: elements of list ..."
	// For example, if base=100, and the number stored corresponds to 10965,
	// then the output is "100: 65 9 1" **/
	public void printList() {
		System.out.print(base + ":");
		if(isNegative) {
			System.out.print("-");
		}

		for(int i=0; i < len; i++) {
			System.out.print(" " + arr[i]);
		}
		System.out.println("");
	}

	/**
	 * Custom toString for the default base of 10^9
	 * @return
	 */
	private String customToString() {
		StringBuilder result = new StringBuilder();
		if(this.isNegative){
			result.append("-");
		}
		int index = len-1;
		if(index>=0) {
			result.append(arr[index]);
			index--;
		}
		while(index>=0) {
			result.append(String.format("%09d", arr[index]));
			index--;
		}
		return result.toString();
	}
	
	/**
	 *	Return number to a string in base 10
	 */
	public String toString() {
		//if(base == defaultBase) {
			//10 power 9 base
			return customToString();
		//}

		/*Num decimalEquivalent = this.convertBaseToDecimal();
		StringBuilder result = new StringBuilder();
		if(this.isNegative){
			result.append("-");
		}
		int length = decimalEquivalent.len-1;
		while(length >= 0){
			result.append(decimalEquivalent.arr[length--]);
		}
		return result.toString();*/
	}

	/**
	 * returns base of a number
	 * @return base
	 */
	public long base() {
		return base;
	}

	/**
	 *  Return number equal to "this" number, in base=newBase
	 * * @param newBase
	 * @return res, the Num converted to the newBase
	 */
	public Num convertBase(long newBase) {
		//Num decimalEquivalent = this.convertBaseToDecimal();
		Num res = new Num(this.toString(),newBase);
		return res;
	}

	/**
	 * convert number in any base to decimal
	 * @return ans, a Num which the the given number in decimal form
	 */
	public Num convertBaseToDecimal() {
		Num b = new Num(this.base(),10);
		if(this.base()==10) return this;
		int len = this.len;
		Num ans = new Num(this.arr[len-1],10);
		for(int i = len-2;i>=0;i--) {
			ans = product(ans,b);
			Num digit = new Num(this.arr[i],10);
			ans = add(ans,digit);
		}
		return ans;
	}

	/**
	 * Divide by 2, for using in binary search
 	 */
	public Num by2() {
		Num res = new Num(0);
		int size = this.len;
		if(this.arr[len-1]<2){
			size--;
		}
		res.arr = new long[size];
		int i = this.len-1;
		long val = 0;
		long rem = 0;
		while(i >= 0) {
			val = rem * this.base() + this.arr[i];
			if(i!= this.len-1 || (i==this.len-1 && val/2!=0)) {
				res.arr[i] = val/2;
			}
			rem = val % 2;
			i--;
		}
		res.len = res.arr.length;
		if(this.isNegative) {res.isNegative = true;}
		return res;
	}

	/** Evaluate an expression in postfix and return resulting number.
	 *  Each string is one of: "*", "+", "-", "/", "%", "^", "0", or
	 * 	a number: [1-9][0-9]*. There is no unary minus operator.
	 * @param expr
	 * @return result of the postfix operation
	 */
	public static Num evaluatePostfix(String[] expr) {
		ArrayDeque<Num> valStack = new ArrayDeque<>();

		for(int i = 0; i < expr.length; i++) {
			String cur = expr[i];
			
			if (isOperator(cur)) {
				Num secondOperand = valStack.pop();
				Num firstOperand = valStack.pop();
				valStack.push(doOperation(cur, firstOperand, secondOperand));
			} else {
				valStack.push(new Num(cur));
			}
		}
		
		return valStack.pop();
	}

	/**
	 *  Returns the precedence of the operators
	 * @param op
	 * @return int	Precedence value
	 */
	private static int precedence(String op) { 
		switch(op) {
		case "+":
		case "-":
			return 0;
		case "*":
		case "/":
		case "%":
			return 1;
		case "^":
			return 2;
		default:
			throw new IllegalArgumentException();
		}
	}
	
	/**
	 * Returns if the operator is left associative
	 * @param op
	 * @return Boolean
	 */
	private static Boolean isLeftAssociative(String op) {
		return (op == "+" || op=="-" || op == "*" || op == "/" || op == "%");
	}
	
	/**
	 *  Returns if the string is an operator
	 * @param value
	 * @return Boolean value
	 */
	private static Boolean isOperator(String value) {
		switch(value) {
		case "+" :
		case "-":
		case "*":
		case "/":
		case "%":
		case "^":
			return true;
		default:
			return false;
		}
	}
	
	/**
	 *  Performs the operation on the numbers
	 * @param operand
	 * @param first
	 * @param second
	 * @return Num
	 */
	private static Num doOperation(String operand, Num first, Num second) {
		Num result;
		switch(operand) {
		case "+" :
			result = add(first, second);
			break;
		case "-":
			result = subtract(first, second);
			break;
		case "*":
			result = product(first, second);
			break;
		case "/":
			result = divide(first, second);
			break;
		case "%":
			result = mod(first, second);
			break;
		case "^":
			result = power(first, Long.parseLong(second.toString()));
			break;
		default:
			throw new IllegalArgumentException();
		}
		return result;
	}
	
	/**
	 *  Evaluate an expression in infix and return resulting number
	 *  Each string is one of: "*", "+", "-", "/", "%", "^", "(", ")", "0", or
	 *  a number: [1-9][0-9]*. There is no unary minus operator.
	 * @param expr
	 * @return Num, which is the result of the infix evaluation
	 */

	public static Num evaluateInfix(String[] expr) {
		ArrayDeque<Num> valStack = new ArrayDeque<>();
		ArrayDeque<String> opStack = new ArrayDeque<>();
		
		for(int i = 0; i < expr.length; i++) {
			String cur = expr[i];
			
			if (isOperator(cur)) {
				//Token is operator
				// Until operator on stack top is not ( or
				// precedence of operator on stack top is < the token,
				// pop from stack to the output
				while(!opStack.isEmpty() &&
						opStack.peek() != "(" &&
						(precedence(opStack.peek()) > precedence(cur) ||
						(precedence(opStack.peek()) == precedence(cur) &&
							isLeftAssociative(opStack.peek())))) {
					
					String operator = opStack.pop();
					Num secondOperand = valStack.pop();
					Num firstOperand = valStack.pop();
					
					//Do operation
					valStack.push(doOperation(operator, firstOperand, secondOperand));
				}
				
				// Now the precedence of top of stack < the token
				// so now push the operator to stack
				opStack.push(cur);
			} else if (cur == "(") {
				//Token is (
				opStack.push(cur);
				
			} else if (cur == ")") {
				//Token is )
				String operator = opStack.pop();
				// Pop all operators in stack to the output
				// Until you pop the op (
				while(operator != "(") {
					Num secondOperand = valStack.pop();
					Num firstOperand = valStack.pop();
					
					//Do operation
					valStack.push(doOperation(operator, firstOperand, secondOperand));
					
					operator = opStack.pop();
				}
				
			} else {
				// Token is number
				valStack.push(new Num(cur));
				
			}
		}
		
		// Pop all the remaining op in stack to output
		while(!opStack.isEmpty()) {
			String operator = opStack.pop();
			Num secondOperand = valStack.pop();
			Num firstOperand = valStack.pop();
			
			//Do operation
			valStack.push(doOperation(operator, firstOperand, secondOperand));
		}
		
		return valStack.pop();
	}
}

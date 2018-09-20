
// Starter code for lp1.
// Version 1.0 (8:00 PM, Wed, Sep 5).

// Change following line to your NetId
package axp178830;

import java.util.List;
import java.util.LinkedList;
import java.util.ArrayList;

public class Num implements Comparable<Num> {

	static long defaultBase = 10;
	long base;
	long[] arr; // array to store arbitrarily large integers
	boolean isNegative; // boolean flag to represent negative numbers
	int len; // actual number of elements of array that are used; number is stored in
				// arr[0..len-1]

	public Num(String s) {
		this(s, defaultBase);
	}

	public Num(String s, long base) {
		this.base = base;
		//TODO
	}
	
	public Num(long x) {
		this(x, defaultBase);
	}

	public Num(long x, long base) {
		this.base = base;
		
		// In case num 0, store 0 in arr[0]
		if(x==0) {
			arr = new long[1];
			arr[0] = 0;
			len = 1;
		}
			
		//Divide x by base and save it in array
		long val = x < 0 ? -x : x;
		ArrayList<Long> array = new ArrayList<>();
		while(val > 0) {
			long rem = val%base;
			val = val/base;
			array.add(rem);
		}
		
		// Set length
		len = array.size();

		//TODO need to find a way to fill array without knowing size
		// Method given in forum
		arr = new long[len];
		for (int i=0; i< len; i++) {
			arr[i] = array.get(i);
		}
		
		// Set isNegative
		if(x < 0) {
			isNegative = true;
		}
		else {
			isNegative = false;
		}
	}
	
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

	private static Num subtractActual(Num a, Num b) {
		long base = a.base();
		Num diff = new Num(0, base);
		diff.arr = new long[Math.max(a.len, b.len)];
		int i = 0;
		while(i < a.len && i < b.len) {
			if(a.arr[i] >= b.arr[i]) {
				diff.arr[i] = a.arr[i] - b.arr[i];
			} else {
				int j = i+1;
				
				//Borrow from the last available digit
				while (j < a.len && a.arr[j] <= 0) {
					a.arr[j] = base-1;
					j++;
				}
				
				a.arr[j]--;
				a.arr[i] += base;
				
				diff.arr[i] = a.arr[i] - b.arr[i];
			}
			i++;
		}
		while(i < a.len) {
			diff.arr[i] = a.arr[i];
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
			if(a.compareAbs(b) < 0) {
				result = subtractActual(b,a);
			} else if(a.compareAbs(b) > 0) {
				result = subtractActual(a,b);
			} else {
				result = new Num(0);
			}
			
			result.isNegative = b.isNegative;
		}
		return result;
	}

	public static Num product(Num a, Num b) {
		Num res = new Num(0);
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
		res.len = a.len+b.len;
		int index = a.len+b.len-1;
		while(res.arr[index--]==0){
			res.len--;
		}
		if(a.isNegative&&b.isNegative){
			res.isNegative = false;
		}
		else if(a.isNegative || b.isNegative){
			res.isNegative = true;
		}
		return res;
	}

	// Use divide and conquer
	public static Num power(Num a, long n) {
		if(n==0){
			Num ans = new Num(1);
			ans.arr = new long[1];
			ans.arr[0]= 1;
			return ans;
		}
        if(n==1) return a;
        Num val= power(a,n/2);
        Num prod = product(val,val);
        if(n%2==0){
        	return prod;
		}
		return product(prod,a);
	}

	// Use binary search to calculate a/b
	public static Num divide(Num a, Num b) {
		
		//Check for divide by 0 case
		if(b.len==0 || (b.len==1 && b.arr[0] == 0)) {
			System.out.println("Error: Divide by zero");
		return null;
	}

		// Convert base of b if not same as a
		long base = a.base();
		if(base != b.base()) {
			b = b.convertBase(base);
		}
		
		Boolean aSign = a.isNegative;
		Boolean bSign = b.isNegative;
		
		a.isNegative = b.isNegative  = false;
		
		Num result;
		Num low = new Num(0, base);
		Num high = new Num(a.toString(), base);
		
		while(true) {
			Num mid = Num.add(high, low).by2();
			
			Num prod = Num.product(mid, b);
			Num diff = Num.subtract(prod, a);
			
			//If difference is less than b/2 then break
			diff.isNegative = false;
			Num precision = Num.subtract(diff, b.by2()); //TODO may be +1
			if(precision.compareTo(new Num(0, base)) <= 0) {
				result = mid;
				break;
			}
			
			// Update high and low
			if(prod.compareTo(a) <= 0) {
				low = mid;
			} else {
				high = mid;
			}
		}
		
		//Update the result sign if one of them is -ve
		if(aSign != bSign)
			result.isNegative = true;
		
		//Revert the sign of a and b
		a.isNegative = aSign;
		b.isNegative = bSign;
		return result;
	}

	// return a%b
	public static Num mod(Num a, Num b) {
		return null;
	}

	// Use binary search
	public static Num squareRoot(Num a) {
		
		//Check for negative numbers
		if(a.isNegative) {
			System.out.println("Error: Square root of negative number");
			return null;
		}
				
		//Check for 0 or 1
		if(a.len==0 ||
				(a.len==1 && (a.arr[0] == 0 || a.arr[0] == 1))) {
			System.out.println("Error: Divide by zero");
		return null;
	}

		Num result = null;
		Num low = new Num(0, a.base());
		Num high = a;
		
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
	
	// Utility functions
	// compare "this" to "other": return +1 if this is greater, 0 if equal, -1
	// otherwise
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

	// Output using the format "base: elements of list ..."
	// For example, if base=100, and the number stored corresponds to 10965,
	// then the output is "100: 65 9 1"
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

	// Return number to a string in base 10
	public String toString() {
		return null;
	}

	public long base() {
		return base;
	}

	// Return number equal to "this" number, in base=newBase
	public Num convertBase(long newBase) {
		return null;
	}

	// Divide by 2, for using in binary search
	public Num by2() {
		Num res = new Num(0);
		List<Long> ans = new LinkedList<>();
		int i = this.len-1;
		long val = 0;
		long rem = 0;
		while(i >= 0) {
			val = rem * this.base() + this.arr[i];
			if(i!= this.len-1 || (i==this.len-1 && val/2!=0)) {
				ans.add(0,val/2);
			}
			rem = val % 2;
			i--;
		}
		res.arr = new long[ans.size()];
		res.len = res.arr.length;
		int j = 0;
		for(Long temp : ans) {
			res.arr[j++] = temp;
		}
		if(this.isNegative) {res.isNegative = true;}
		return res;
	}

	// Evaluate an expression in postfix and return resulting number
	// Each string is one of: "*", "+", "-", "/", "%", "^", "0", or
	// a number: [1-9][0-9]*. There is no unary minus operator.
	public static Num evaluatePostfix(String[] expr) {
		return null;
	}

	// Evaluate an expression in infix and return resulting number
	// Each string is one of: "*", "+", "-", "/", "%", "^", "(", ")", "0", or
	// a number: [1-9][0-9]*. There is no unary minus operator.
	public static Num evaluateInfix(String[] expr) {
		return null;
	}

	public static void main(String[] args) {
		Num x = new Num(120005);
		Num y = new Num(-1234567985);
		
		System.out.println("Add:");
		Num z = Num.add(x, y);
		z.printList();
		
		System.out.println("Subtract:");
		z = Num.subtract(x, y);
		z.printList();

		x = new Num(144);
		System.out.println("By 2:");
		x.by2().printList();
		
		System.out.println("Square root:");
		squareRoot(x).printList();
		
		System.out.println("Product:");
		z = Num.product(x,y);
        z.printList();

		System.out.println("Power:");
        x = new Num(-25000);
        z = Num.power(x,9);
		z.printList();

	}
}

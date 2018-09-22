// Starter code for lp1.
// Version 1.0 (8:00 PM, Wed, Sep 5).

// Change following line to your NetId
package axp178830;

import java.util.List;
import java.util.LinkedList;
import java.util.ArrayDeque;
import java.util.ArrayList;

public class Num implements Comparable<Num> {

	static long defaultBase = 10;
	long base = defaultBase;
	long[] arr; // array to store arbitrarily large integers
	boolean isNegative; // boolean flag to represent negative numbers
	int len; // actual number of elements of array that are used; number is stored in
				// arr[0..len-1]

	public Num(String s) {
		this(s,defaultBase);
	}

    public Num(String s, long base) {
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
		Num res = new Num(0);
		Num ten = new Num(10);
		while(index < characters.length){
			res = add(product(res,ten),new Num(Character.getNumericValue(characters[index])));
			index++;
		}
		//res contains number in decimal format
		if(characters[0] == '-') res.isNegative = true;
		this.arr = res.arr;
		this.len = res.len;
	}

	public Num(long x) {
		this(x,defaultBase);
	}

	private Num(long x, long base) {
		this.base = base;
		// In case num 0, store 0 in arr[0]
		if(x==0) {
			arr = new long[1];
			this.len = 1;
		}
		else{
			//Divide x by base and save it in array
			long val = x < 0 ? -x : x;

			ArrayList<Long> array = new ArrayList<>();
			while (val > 0) {
				long rem = val % base;
				val = val / base;
				array.add(rem);
			}

			// Set length
			len = array.size();

			//TODO need to find a way to fill array without knowing size
			// Method given in forum
			arr = new long[len];
			for (int i = 0; i < len; i++) {
				arr[i] = array.get(i);
			}

			// Set isNegative
			if (x < 0) {
				isNegative = true;
			} else {
				isNegative = false;
			}

		}
	}
	
	private static Num addActual(Num a, Num b) {
		long base = a.base();
		Num sum = new Num(0);
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
		Num diff = new Num(0);
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
				result.isNegative = true;
			} else if(a.compareAbs(b) > 0) {
				result = subtractActual(a,b);
				result.isNegative = false;
			} else {
				result = new Num(0);
			}
		}
		return result;
	}

	public static Num product(Num a, Num b) {
		Num res = new Num(0,a.base());
		Num zero = new Num(0,a.base());
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
		Num dividend = new Num(a.toString());
		Num one = new Num(1);
		Boolean aSign = a.isNegative;
		Boolean bSign = b.isNegative;
		if(b.len==0 || (b.len==1 && b.arr[0] == 0)) {
			System.out.println("Error: Divide by zero");
		return null;
	    }
	    if(a.compareAbs(b) < 0){
	    	return new Num(0);
		}
		if(a.compareAbs(b)==0){
			if(aSign!=bSign) one.isNegative = true;
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

		Num mid = new Num(0);
		Num low = new Num(1);
		//Num high = new Num(a.toString(), base);
		Num high = a.by2();
		while(low.compareTo(high)<=0) {
			mid = Num.add(high, low).by2();
			Num prod = Num.product(mid, b);
			//Num diff = Num.subtract(prod, a);
            if(prod.compareTo(a) == 1){
            	high = subtract(mid,new Num(1));
			}
			else if(product(add(mid,new Num(1)),b).compareTo(a) != 1){
				low = add(mid,new Num(1));
			}
			else break;
		}

		if(aSign != bSign) mid.isNegative = true;
		
		//Revert the sign of a and b
		a.isNegative = aSign;
		b.isNegative = bSign;
		return mid;
	}

	// return a%b
		public static Num mod(Num a, Num b) {
			Num result=new Num(1);
			Num quotient=new Num(0);
			Num prod=new Num(0);
			if(a.compareTo(b)<0) {
				return a;
			}
			else if(a.compareTo(b)==0) {
				Num ans = new Num(0);
				return ans;
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
		Num low = new Num(0);
		Num high = a.by2();
		
		while(low.compareTo(high) <= 0) {
			Num mid = Num.add(high, low).by2();
			System.out.println(mid.toString());
			int c = Num.product(mid, mid).compareTo(a);
			// This is a perfect square
			if(c == 0) {
				return mid;
			}
			
			if(c < 0) {
				low = Num.add(mid, new Num(1));
				result = mid;
			} else {
				high = Num.subtract(mid, new Num(1));
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
		Num decimalEquivalent = this.convertBaseToDecimal();
		StringBuffer result = new StringBuffer();
		if(this.isNegative){
			result.append("-");
		}
		int length = decimalEquivalent.len-1;
		while(length >= 0){
			result.append(decimalEquivalent.arr[length--]);
		}
		return result.toString();
	}

	public long base() {
		return base;
	}

	// Return number equal to "this" number, in base=newBase
	public Num convertBase(long newBase) {
		Num decimalEquivalent = this.convertBaseToDecimal();
		Num res = new Num(decimalEquivalent.toString(),newBase);
		return res;
	}
	
	public Num convertBaseToDecimal() {
		Num b = new Num(this.base(),10);
		if(this.base()==10) return this;
		int len = this.len;
		Num ans = new Num(this.arr[len-1],10);
		for(int i = len-2;i>=0;i--) {
			ans = product(ans,b);
			ans = add(ans,new Num(this.arr[i],10));
		}
		return ans;
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

	/** Evaluate an expression in postfix and return resulting number.
	 *  Each string is one of: "*", "+", "-", "/", "%", "^", "0", or
	 * 	a number: [1-9][0-9]*. There is no unary minus operator.
	 * @param expr
	 * @return
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
	 * @return
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
	 * @return
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
	 * @return
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

	public static void main(String[] args) {
		Num.defaultBase =  10 ;
		String x1 = "250";
		//"786454200076408439092539632260828272593836316707406246059174043946920151039708532602579215182984566646367189932773772980014958081319773837317054373334273743071587716369123281583905136996488497402800547670135068119803935742756570453273419399227836172472060046428915578599058204384129093549017658922592717316130710163695859438224487687121127147561525389674924756881728548432235047211346262573929492300963633729189906314391062527618752980534790723298399198031360968612409402509071990399909895039556843903501653765015828088505468700676418265481212436386083286370523140274175434836993092076338967950671381688046535853565374386579494190007549142381431264148621816513403028738279152549372843843763926043535169002751667429265757333742051428853597147152410674211604250943607600337449993014223807420977347236257022349666066784341818480887447033111421121901157303219608991870699531144622719694056840866312639792230315345443541208269037304281551738129244965794081345219078783762364931213740743289158519511383087890728931356279483025663971453613975059451789238576713007844681937881832084711939840172561494725810075043187770936276288369656266213684373183983032873194851815137298378522485573694381280218972044583276438420495868727722908595655603908957578566078383414703250256782232839255732352477411648350945819752331225126890120841807237643124991299322759094961258613590606098351792666026656565502599968553261368163563416875135968150974258344673370734926333513223462172895028251352244047324263174044442680235306301253519273602943320231264265103379525387715502324641632489682902873478240910242164933947308443296152470166767863866427847592792228102622263216477400966853730061592511009421693531393146791398715865452069683673659313416540614342398530602157312262706722204601620271302770243835730452358024950699196489797061594339303215049743081518549789356734710023458370431323655070348330154377073506419903366090639123352491470897969408488963183802230138589307129800676923624543758837890114129974458547207677833008332132664957374445558422483707665666990607522838261481759897824882599557449182469619251286221274579940795977668767882792993659091424760907989720667341901455042930959421615995176953104867214089073850078535443489379214863202661688287639439561718070106327120294394846063870563655990625023605676096826458569408274590153885810214108211204399898858929102293556878266487541511787623254939724439535715283863167882426532434783233005568852192629894559680013581670811431406900580189476713739508523467107683101477692161463737207320568872831308974774004972745997592681812486160010117491014699979218169236231077610833240459419896348210573609246299894312398900164011910111475126759074104259088196439902438152947605959946031437666595001743891163566344110524252234012968121540907135435504422582118732450000632413593387240951889130993870270914865734041695001228247584755548242261442127354412530052198387683041662507200211635988065444118024124279913315548294994361165534774552250273325455613259665724286532426375686880935499094168366384533028683889893802301138386097429774122827083319027770185462725509910209795344983242222466863479879380026821231578202567514834419947286538355726617865361859063402478343287922871213496409583349238671093187314689995100881477359123411175455667152359300566969217313284603852337596150019279326903244208703134062671056206783512771188052346253890643543492095632532118115986121179735356077954454782599156719511866506107610942398805388347955371745124635325402882658832180462078019176004338973591200730379447212543614364573169207309758697393750436971247280166681171958575535768948820227854926045274869200542597207737672331260230320587243252368535986321638552241341859824429553177692219053706983954734122228720673012315067666286579116420949163988712430383939497598880129284969850055123763619957298003710083534999940634697793775750176837513118939521994772393280854953646588301324981316238766518811609896884157919809809572935716326764903575270826786336957794055836228425814154407104809883534322336346378784038999305253222371533794372456944697718806386996499965590465465428154048050116939936768515935082613152049663136962535518591664975039051712814928569347957684835403949920062213545296841666950974455240403034056134012163636138022047274841143755008442266011338767832447742938968649235963790537305321420734375584047414641469254057203982165705726655134682238645680631653110497489734667882166427173593035793757296174519418965077005213978397403837459527943403575673055957970653996798938890493462269677968170378972559299717978899245205701180860500931816484679572698098822661239525292116313748508488777894502440787041953258257106457109185104880259816669917866366281943031647385975686321081654655479450848797172686627004093753970983947118850817702812028910874781159594662523643931457315591733466011946287359197252001564523227962955134711218167163137928197722216912983422806595070824717063233677430007587079681000270543788137576206526794857653833758198861141349825856448529270656261767618436511200682457100597723681533694117233483803777430148940024526797555006498306337829436870052670583128509875165787839249273305768825908921439068269965405712664885998184171684190839053231588760133731182878218880696762827821949505247733146509756740109523498433546121828859256109223876485191975900709794487585951432020328245982135689203702448113342135342528922386195622792357734507243881494981435518922849408864232139459360913283255759923071609311107439903047279429020700935626701148536879080464738591509944921343846298252623915446267442312425179397993638223466634164271070406579140065806635943351887060079766888675978427376753383828259667218014363697897400857221246524806737284098083500437712347557688822685708212061171321921635098726511876585473899715628092412659958240650326061669302568740056115080286890893475838022315397780762307580063670829980415819742117283560747529793523983488966873291456106850440324834251033506083604669614744243219668516099470747350885789097793673922937734973440880962640555469784463560267885564776849320333133320088271662049543289699249451550552953956067131292681316744029334596026778189362304989418514628655157108444373265276259665803571825165472089144813149312522490661544013845629516631860943068023778067990064372874654492995422150506758013759937352324064340572801090298075175696259572519370155369076089006506977262960866649005386243296955307797902335966348751603592213704814872408906867341326734545033884533645045294413064084635134872263853377405624063219689462366383070313835046618807104926419534876151268133642293835482078109819930890285018052509125558150105363927280638699753369551366157975387966042245252473418048883917877058569268560372340472494578518999317683729255731309264411287342010159304312165220005916946286363378416383068641442436207189330348179288480176987423865102001867298421219654647970326365550753631744260513253744165710280619292681718209695501217510668300332941366081847566874422729130991741600616112181356375729785986851873879136164811910002354821835303455697568885851125339066543395578591872494822891711629574846527180192312516327603741594129844253802422816516965614128063988053146963812527629354004900199074346833067993002258212718806129162346952830257898484806290483396467629818439569489638016122726077972274320549654868514377902280976636184724632937048422461486996434090708062531496080738711097289572589257527329811364889262534304119837022489509822576115086728461249647412576076569698199883983526773470255659923917921617003295888679274265854781328534995011357033191596636147356555966709503364939423304225096273672616696606103531762468795308692491908970831916998742845660232975100061916895889871598947114626841197413234154941014551293932930546666289873654728158109781904700095580476249968235513656412084584619373694789611576908786441342186863209742352843204317463663399778479759871906375134521051929650696395866192136587599391570512427091288680669728026810883233337803344412704144830844457507035483736828054825639628961732756930835195679075094628552048911949525045685776879751194549839872891486654999650676021650413327234587436829076875063811339646175253593038236788435766026585435288166186222593804171635047893260524303883504700654618322392732403738444777660791279245978829911287476125442268835492873728987464503838144892596120967967802704189717265254095823253661823622007941901106677658134945711965955249872058682400147280649331159019791361075889765975328693276977757163304239160953323803531475174984511026141621018775835344690307409942052363704930033090903107167289890026536407030814849548150705140763877228597690397298625935886459146659769453371413101295654142020072878411796942849301229081901670666194201912687002523481129239818893441071300128751032285850489002077346553594914170873332243978668025062121689746398112048043740663348159319079132384672742884048573627690019343441861251149592488564189374061693999597283064344081844128929267542459215163199764705629484173073670697336035212781503533695863532506853785785491579186438413416703879678096268971117469735759940853338328909048216462697004887356821756408093726771383152883674024409630584470312526763178200914581141023635606041780459393996997101664351608014942138948909601283110847843042871635322594260966323429787984171264146066012695765184279261261892653320922318809232671462597004949626614035644854268530968998711709433726618113856032125740988250038173550848423716173572844849867239795416839129704292281920853908953140608446853078317436042367186259899865306165544133474305270723037469084675792740758110429417082390509790854714099339232666085761138957024562817299770455893252827009618834663710756753823790438003504587806936655318736669608489845326345947688104897579002115864663659617984234129817122753803984191181867692018352923820683316508060441834894016503174601233957611665590521150185777320045387613870665216180999690003328379519528831540734247638248486737709100228499571004038920192141578790277503834017220151184465536911339305960156938206473231169644883777974626694579393730365873570587864542000764084390925396322608282725938363167074062460591740439469201510397085326025792151829845666463671899327737729800149580813197738373170543733342737430715877163691232815839051369964884974028005476701350681198039357427565704532734193992278361724720600464289155785990582043841290935490176589225927173161307101636958594382244876871211271475615253896749247568817285484322350472113462625739294923009636337291899063143910625276187529805347907232983991980313609686124094025090719903999098950395568439035016537650158280885054687006764182654812124363860832863705231402741754348369930920763389679506713816880465358535653743865794941900075491423814312641486218165134030287382791525493728438437639260435351690027516674292657573337420514288535971471524106742116042509436076003374499930142238074209773472362570223496660667843418184808874470331114211219011573032196089918706995311446227196940568408663126397922303153454435412082690373042815517381292449657940813452190787837623649312137407432891585195113830878907289313562794830256639714536139750594517892385767130078446819378818320847119398401725614947258100750431877709362762883696562662136843731839830328731948518151372983785224855736943812802189720445832764384204958687277229085956556039089575785660783834147032502567822328392557323524774116483509458197523312251268901208418072376431249912993227590949612586135906060983517926660266565655025999685532613681635634168751359681509742583446733707349263335132234621728950282513522440473242631740444426802353063012535192736029433202312642651033795253877155023246416324896829028734782409102421649339473084432961524701667678638664278475927922281026222632164774009668537300615925110094216935313931467913987158654520696836736593134165406143423985306021573122627067222046016202713027702438357304523580249506991964897970615943393032150497430815185497893567347100234583704313236550703483301543770735064199033660906391233524914708979694084889631838022301385893071298006769236245437588378901141299744585472076778330083321326649573744455584224837076656669906075228382614817598978248825995574491824696192512862212745799407959776687678827929936590914247609079897206673419014550429309594216159951769531048672140890738500785354434893792148632026616882876394395617180701063271202943948460638705636559906250236056760968264585694082745901538858102141082112043998988589291022935568782664875415117876232549397244395357152838631678824265324347832330055688521926298945596800135816708114314069005801894767137395085234671076831014776921614637372073205688728313089747740049727459975926818124861600101174910146999792181692362310776108332404594198963482105736092462998943123989001640119101114751267590741042590881964399024381529476059599460314376665950017438911635663441105242522340129681215409071354355044225821187324500006324135933872409518891309938702709148657340416950012282475847555482422614421273544125300521983876830416625072002116359880654441180241242799133155482949943611655347745522502733254556132596657242865324263756868809354990941683663845330286838898938023011383860974297741228270833190277701854627255099102097953449832422224668634798793800268212315782025675148344199472865383557266178653618590634024783432879228712134964095833492386710931873146899951008814773591234111754556671523593005669692173132846038523375961500192793269032442087031340626710562067835127711880523462538906435434920956325321181159861211797353560779544547825991567195118665061076109423988053883479553717451246353254028826588321804620780191760043389735912007303794472125436143645731692073097586973937504369712472801666811719585755357689488202278549260452748692005425972077376723312602303205872432523685359863216385522413418598244295531776922190537069839547341222287206730123150676662865791164209491639887124303839394975988801292849698500551237636199572980037100835349999406346977937757501768375131189395219947723932808549536465883013249813162387665188116098968841579198098095729357163267649035752708267863369577940558362284258141544071048098835343223363463787840389993052532223715337943724569446977188063869964999655904654654281540480501169399367685159350826131520496631369625355185916649750390517128149285693479576848354039499200622135452968416669509744552404030340561340121636361380220472748411437550084422660113387678324477429389686492359637905373053214207343755840474146414692540572039821657057266551346822386456806316531104974897346678821664271735930357937572961745194189650770052139783974038374595279434035756730559579706539967989388904934622696779681703789725592997179788992452057011808605009318164846795726980988226612395252921163137485084887778945024407870419532582571064571091851048802598166699178663662819430316473859756863210816546554794508487971726866270040937539709839471188508177028120289108747811595946625236439314573155917334660119462873591972520015645232279629551347112181671631379281977222169129834228065950708247170632336774300075870796810002705437881375762065267948576538337581988611413498258564485292706562617676184365112006824571005977236815336941172334838037774301489400245267975550064983063378294368700526705831285098751657878392492733057688259089214390682699654057126648859981841716841908390532315887601337311828782188806967628278219495052477331465097567401095234984335461218288592561092238764851919759007097944875859514320203282459821356892037024481133421353425289223861956227923577345072438814949814355189228494088642321394593609132832557599230716093111074399030472794290207009356267011485368790804647385915099449213438462982526239154462674423124251793979936382234666341642710704065791400658066359433518870600797668886759784273767533838282596672180143636978974008572212465248067372840980835004377123475576888226857082120611713219216350987265118765854738997156280924126599582406503260616693025687400561150802868908934758380223153977807623075800636708299804158197421172835607475297935239834889668732914561068504403248342510335060836046696147442432196685160994707473508857890977936739229377349734408809626405554697844635602678855647768493203331333200882716620495432896992494515505529539560671312926813167440293345960267781893623049894185146286551571084443732652762596658035718251654720891448131493125224906615440138456295166318609430680237780679900643728746544929954221505067580137599373523240643405728010902980751756962595725193701553690760890065069772629608666490053862432969553077979023359663487516035922137048148724089068673413267345450338845336450452944130640846351348722638533774056240632196894623663830703138350466188071049264195348761512681336422938354820781098199308902850180525091255581501053639272806386997533695513661579753879660422452524734180488839178770585692685603723404724945785189993176837292557313092644112873420101593043121652200059169462863633784163830686414424362071893303481792884801769874238651020018672984212196546479703263655507536317442605132537441657102806192926817182096955012175106683003329413660818475668744227291309917416006161121813563757297859868518738791361648119100023548218353034556975688858511253390665433955785918724948228917116295748465271801923125163276037415941298442538024228165169656141280639880531469638125276293540049001990743468330679930022582127188061291623469528302578984848062904833964676298184395694896380161227260779722743205496548685143779022809766361847246329370484224614869964340907080625314960807387110972895725892575273298113648892625343041198370224895098225761150867284612496474125760765696981998839835267734702556599239179216170032958886792742658547813285349950113570331915966361473565559667095033649394233042250962736726166966061035317624687953086924919089708319169987428456602329751000619168958898715989471146268411974132341549410145512939329305466662898736547281581097819047000955804762499682355136564120845846193736947896115769087864413421868632097423528432043174636633997784797598719063751345210519296506963958661921365875993915705124270912886806697280268108832333378033444127041448308444575070354837368280548256396289617327569308351956790750946285520489119495250456857768797511945498398728914866549996506760216504133272345874368290768750638113396461752535930382367884357660265854352881661862225938041716350478932605243038835047006546183223927324037384447776607912792459788299112874761254422688354928737289874645038381448925961209679678027041897172652540958232536618236220079419011066776581349457119659552498720586824001472806493311590197913610758897659753286932769777571633042391609533238035314751749845110261416210187758353446903074099420523637049300330909031071672898900265364070308148495481507051407638772285976903972986259358864591466597694533714131012956541420200728784117969428493012290819016706661942019126870025234811292398188934410713001287510322858504890020773465535949141708733322439786680250621216897463981120480437406633481593190791323846727428840485736276900193434418612511495924885641893740616939995972830643440818441289292675424592151631997647056294841730736706973360352127815035336958635325068537857854915791864384134167038796780962689711174697357599408533383289090482164626970048873568217564080937267713831528836740244096305844703125267631782009145811410236356060417804593939969971016643516080149421389489096012831108478430428716353225942609663234297879841712641460660126957651842792612618926533209223188092326714625970049496266140356448542685309689987117094337266181138560321257409882500381735508484237161735728448498672397954168391297042922819208539089531406084468530783174360423671862598998653061655441334743052707230374690846757927407581104294170823905097908547140993392326660857611389570245628172997704558932528270096188346637107567538237904380035045878069366553187366696084898453263459476881048975790021158646636596179842341298171227538039841911818676920183529238206833165080604418348940165031746012339576116655905211501857773200453876138706652161809996900033283795195288315407342476382484867377091002284995710040389201921";

		Num x=new Num(x1);
		Num y=new Num(11);
/*
		String z1 = y.toString();
		System.out.println(z1);*/
/*
		Num.defaultBase = 15;
		Num w = new Num(600);
*/

		System.out.println("Add:");
		Num z = Num.add(x, y);
		/*z.printList();

	    System.out.println("Subtract:");
		z = Num.subtract(x, y);
		z.printList();

		System.out.println("By 2:");
		x.by2().printList();
		*/
	/*	System.out.println("Square root:");
		squareRoot(x).printList();*/
		
		/*System.out.println("Product:");
		z = Num.product(x,y);
        z.printList();

		System.out.println("Power:");
        x = new Num(-25000);
        z = Num.power(x,9);
   		z.printList();*/



		System.out.println("modulo");
		z=Num.mod(x,y);
		System.out.println(z.toString());

		/*Num x = new Num(521,30);
		Num z = convertBaseToDecimal(x);
		z.printList();*/

	}
}
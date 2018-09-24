package axp178830;

public class TestMain {
    private static void testAdd() {
        Num a = new Num("10");
        Num b = new Num("5");
        System.out.println("Add 1: " + Num.add(a, b).toString());

        a = new Num("10");
        b = new Num("-5");
        System.out.println("Add 2: " + Num.add(a, b).toString());

        a = new Num("-10");
        b = new Num("5");
        System.out.println("Add 3: " + Num.add(a, b).toString());

        a = new Num("-10");
        b = new Num("-5");
        System.out.println("Add 4: " + Num.add(a, b).toString());
    }

    private static void testSubtract() {

        Num a = new Num(-5);
        Num b = new Num (-10);
        System.out.println("Subtract"+Num.subtract(a,b));

        a = new Num(000003223);
        b = new Num(10000);
        System.out.println("SUBTRACT"+Num.subtract(a,b));
        a = new Num(-10);
        b = new Num (-5);
        System.out.println("Subtract"+Num.subtract(a,b));

        a = new Num(5);
        b = new Num (10);
        System.out.println("Subtract"+Num.subtract(a,b));

        a = new Num(10);
        b = new Num (5);
        System.out.println("Subtract"+Num.subtract(a,b));

        a = new Num("9999999999999999999999999999999999999999999999999999999");
        b = new Num("10000000000000000000000000000000000000000000000000000000");
        System.out.println("Subtract 1: " + Num.subtract(a, b).toString());

        a = new Num("9999999999999999999999999999999999999999999999999999999");
        b = new Num("-10000000000000000000000000000000000000000000000000000000000");
        System.out.println("Subtract 2: " + Num.subtract(a, b).toString());

        a = new Num("-987654321987654321987654321");
        b = new Num("111111111111111111111111111");
        System.out.println("Subtract 3: " + Num.subtract(a, b).toString());

        a = new Num("-123123123123123123123123123");
        b = new Num("-456456456456456456456456456");
        System.out.println("Subtract 4: " + Num.subtract(a, b).toString());
    }

    private static void testDivide() {
        Num a = new Num(144);
        Num b = new Num("-12");
        System.out.println("Divide 1: " + Num.divide(a, b).toString());


        a = new Num("-15005555555444444444444");
        b = new Num(2456);
        System.out.println(Num.divide(a,b).toString());

        a = new Num("-890728978164836463583465949575987349583410671073917697439673406704376017607439760197609760");
        b = new Num("-983468365846581463587364578613485619274057108457409759175904797957");
        System.out.println("Divide 2: " + Num.divide(a, b).toString());

        a = new Num("918248685678436589600365823507205709475981364934632885762875678657863487568745681258165861875681658");
        b = new Num("2918785974326381698236986239856149836598146598619898659643017070769439864839793795734975749574389");
        System.out.println("Divide 3: " + Num.divide(a, b).toString());

        a = new Num("123123123123");
        b = new Num("123123123123");
        System.out.println("Divide 4: " + Num.divide(a, b).toString());

        a = new Num("123123123123");
        b = new Num("1231231231231212");
        System.out.println("Divide 4: " + Num.divide(a, b).toString());

	/*	a = new Num("123123123123");
		b = new Num("0");
		System.out.println("Divide 4: " + Num.divide(a, b));*/

    }

    private static void testPower(){
        Num a = new Num("600");
        long power = 19;
        System.out.println("Power: " + Num.power(a, power).toString());

        a = new Num("60000000000");
        power = 00001L;
        System.out.println("Power: " + Num.power(a, power).toString());


        a = new Num("60000000000");
        power = 0;
        System.out.println("Power: " + Num.power(a, power).toString());
    }

    private static void testInfix(){
        String[] s = {"5","*","3","^","(","4","-","2",")"};
        Num z = Num.evaluateInfix(s);
        System.out.println(z.toString());
    }

    private static void testPostfix(){
        String[] s ={"17","10","+","3","*","9","/"};
        Num z = Num.evaluatePostfix(s);
        System.out.println(z.toString());
    }

    private static void testMod(){
        Num a = new Num("15005555555444444444444");
        Num b = new Num(2456);
        Num z = Num.mod(a,b);
        System.out.println("Mod" + z.toString());
    }

    private static void testSquareRoot(){
        Num a = new Num(5);
        Num z = Num.squareRoot(a);
        System.out.println("square root"+z.toString());

        a = new Num("17556555523332222222224666666666666666666666666666666666666666666666666666666666666666666666666666666666662222222222222222222222222000066666666666666456");
        z = Num.squareRoot(a);
        System.out.println("square root"+z.toString());
    }
    public static void main(String[] args) {
        Num.defaultBase =  10 ;
        testSquareRoot();
      /*   testInfix();
         testPostfix();*/
        //testSquareRoot();
	/*	Num a = new Num(3223);
		Num b = new Num(10000);
		System.out.println("SUBTRACT"+Num.subtract(a,b));*/
		/*testAdd();
		testSubtract();
		testPower();*/
	   /* testDivide();
	    testMod();*/

		/*Num c = new Num("1023333333333333333344335555555555555555555555");
		System.out.println(c.toString());*/
    }
}

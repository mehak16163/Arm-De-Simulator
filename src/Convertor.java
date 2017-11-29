import java.math.BigInteger;

public class Convertor {
	
	public Convertor() {
		
	}
	public String convert(String initial) {
		String result="";
		for (int i = 0 ; i<initial.length();i++) {
			String c= initial.substring(i,i+1).toLowerCase();
			if (c.equals("0")) {
				result = result+"0000";
			}
			if (c.equals("1")) {
				result = result+"0001";
			}
			if (c.equals("2")) {
				result = result+"0010";
			}
			if (c.equals("3")) {
				result = result+"0011";
			}
			if (c.equals("4")) {
				result = result+"0100";
			}
			if (c.equals("5")) {
				result = result+"0101";
			}
			if (c.equals("6")) {
				result = result+"0110";
			}
			if (c.equals("7")) {
				result = result+"0111";
			}
			if (c.equals("8")) {
				result = result+"1000";
			}
			if (c.equals("9")) {
				result = result+"1001";
			}
			if (c.equals("a")) {
				result = result+"1010";
			}
			if (c.equals("b")) {
				result = result+"1011";
			}
			if (c.equals("c")) {
				result = result+"1100";
			}
			if (c.equals("d")) {
				result = result+"1101";
			}
			if (c.equals("e")) {
				result = result+"1110";
			}
			if (c.equals("f")) {
				result = result+"1111";
			}
		}
		return result;
	}
	public static String sign(String s) {
		String x = "";
		for (int i=0;i<s.length();i++){
			if (s.substring(i, i+1).equals("0")) {
				x=x+"1";
			}
			else x = x+"0";
		}
		return x;
	}
	public int toint(String bin) {
		return Integer.parseInt(bin,2);
	}
	public static void main(String[] args) {
		Convertor c = new Convertor();
		System.out.println(c.convert("E3A02001"));
		System.out.println(Integer.parseInt("01001",2));
		System.out.println(1+ ~(Integer.parseInt("00000000000000000000000000000101",2)));
	}
}

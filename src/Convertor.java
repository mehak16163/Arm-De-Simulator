import java.math.BigInteger;

public class Convertor {
	
	public Convertor() {
		
	}
	public String convert(String initial) {
		String value = new BigInteger(initial, 32).toString(2);
		value =String.format("%32s", value).replace(" ", "0");
		return value;
	}
	public int toint(String bin) {
		return Integer.parseInt(bin,2);
	}
	public static void main(String[] args) {
		Convertor c = new Convertor();
		//System.out.println(c.convert("E3A0200A"));
		System.out.println(Integer.parseInt("01001",2));
		System.out.println(1+ ~(Integer.parseInt("01001",2)));
	}
}

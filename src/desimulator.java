import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;

public class desimulator {
	static String[][] instruction_mem = new String[1000][3];
	public static boolean end;
	static int[] regarray = new int[16];
	static int N , C , V ,Z;
	static int mem[] = new int[4000];
	static int[] stack = new int[1000];
	public static HashMap<String , Integer> regs = new HashMap<String , Integer>();
	public desimulator() {
		regs.put("0000", 0);
		regs.put("0001", 1);
		regs.put("0010", 2);
		regs.put("0011", 3);
		regs.put("0100", 4);
		regs.put("0101", 5);
		regs.put("0110", 6);
		regs.put("0111", 7);
		regs.put("1000", 8);
		regs.put("1001", 9);
		regs.put("1010", 10);
		regs.put("1011", 11);
		regs.put("1100", 12);
		regs.put("1101", 13);
		regs.put("1110", 14);
		regs.put("1111", 15);	
		regarray[13] = 1000;
	}
	
	public static void fetch() throws NumberFormatException, IOException {
		String inst = instruction_mem[regarray[15]][0];
		System.out.println("Fetch instruction " + instruction_mem[regarray[15]][1]+  " from address " + instruction_mem[regarray[15]][2]);
		
		if (inst.substring(4, 10).equals("00000")) {
			regarray[15]++;
			Mult(inst);
		}
		else if (inst.substring(4,5).equals("0") && inst.substring(5,6).equals("0")) {
			regarray[15]++;
			performALU(inst);
		}
		else if (inst.substring(4,5).equals("0") && inst.substring(5,6).equals("1")) {
			regarray[15]++;
			memory(inst);
		}
		else if (inst.substring(4,5).equals("1") && inst.substring(5,6).equals("0") && inst.substring(6,7).equals("1")) {
			regarray[15]++;
			branch(inst);
		}
		else if (inst.substring(4,5).equals("1") && inst.substring(5,6).equals("1") && inst.substring(6,7).equals("1") && inst.substring(7,8).equals("1")){
			regarray[15]++;
			swi(inst);
		}
	}
	public static void swi(String s) throws NumberFormatException, IOException {
		String comment = s.substring(8);
		int opcode = Integer.parseInt(comment, 2);
		if (opcode == 107) {
			System.out.println(regarray[0]);
			System.out.println("DECODE: operation is swi0x6b\nMEMORY: No memory operation");
		}
		else if (opcode == 108) {
			BufferedReader r = new BufferedReader(new InputStreamReader(System.in)) ;
			regarray[0] = Integer.parseInt(r.readLine());
			System.out.println("DECODE: operation is swi0x6c\nMEMORY: No memory operation");

		}
		if (opcode == 17) {
			System.out.println("MEMORY: No memory operation\nEXIT:");
			end =true;
		}
	}
	public static void branch(String s) {
		int  link = Integer.parseInt(s.substring(7,8));
		int result=0;
		String off = s.substring(8);
		if ((Integer.parseInt(off.substring(0,1))==0)) {
			result = Integer.parseInt(off, 2);
		}
		else {
			String offa = Convertor.sign(off);
			result = Integer.parseInt(offa , 2);
			result = ~(result);
			result ++;
		}
		HashMap<String , String> hm = new HashMap<>();
		hm.put("0000", "BEQ");
		hm.put("0001", "BNE");
		hm.put("1010", "BGE");
		hm.put("1011", "BLT");
		hm.put("1100", "BGT");
		hm.put("1101", "BNE");
		hm.put("1110","B");
		String condition = s.substring(0, 4);
		if (condition.equals("0000") && Z!=1 ) {
			return;
		}
		else if (condition.equals("0001") && Z==1) {
			return;
		}
		else if (condition.equals("1010") && N==1) {
			return;
		}
		else if (condition.equals("1011") && N==0) {
			return;
		}
		else if (condition.equals("1100") && (N==1 || Z==1)) {
			return;
		}
		else if (condition.equals("1101") && N==0 && Z==0) {
			return;
		}
		else {
			if (link == 1) {
				regarray[14] = regarray[15];
			}
			if (Integer.parseInt(off.substring(0,1))==0) {
				regarray[15] = regarray[15] + Integer.parseInt(off, 2);
			}
			else {
				regarray[15] = regarray[15] + result;
			}
		
		}
		String oper = hm.get(condition);
		String op = oper;
		if (link==1) {
			
			oper = oper + "L";
		}
		System.out.println("DECODE: Operation is "+ oper );
		System.out.println("Offset is" + result);
		System.out.println("EXECUTE: If " + op+ " move to PC + (" +result+")." );
		System.out.println("MEMORY: No memory operation");
		System.out.println("WRITEBACK: no writeback");
		
	}
	
	
	public static void memory(String s) {
		String base = s.substring(12, 16);
		int base_1 = regs.get(base);
		int op1 = regarray[base_1];
		
		String dest = s.substring(16,20);
		int des_1 = regs.get(base);
		String offset = s.substring(20);
		
		int ls = Integer.parseInt(s.substring(11,12));
		int ud = Integer.parseInt(s.substring(8,9));
		int pp =Integer.parseInt(s.substring(7,8));;
		int im = Integer.parseInt(s.substring(6,7));;
		int op2;
		if (im==0) {
			op2 = Integer.parseInt(offset, 2);
		}
		else {
			String reg3 = s.substring(28);
			int reg_3 = regs.get(reg3);
			op2 = regarray[reg_3];
		}
		if (ls==1) {
			int loc = op1 ;
			if (ud==1) {
				if (pp==1) {
					loc = loc + op2;
				}
				regarray[base_1] = regarray[base_1] + op2;
			}
			else {
				if (pp==1) {
					loc = loc - op2;
				}
				regarray[base_1] = regarray[base_1] - op2;
			}
			int result = mem[loc];
			regarray[des_1] = result;
			System.out.println("DECODE: Operation is LDR, Base register is R"+base_1+", Offset is "+op2+", Destination register is R"+des_1+".");
			System.out.println("Read registers: R"+base_1+" is " + op1 + ", Offset is " + op2 );
			System.out.println("EXECUTE: LDR "+op1 +" and " + op2);
			System.out.println("MEMORY: Loading memory location "+loc + " in register R"+des_1);
			System.out.println("WRITEBACK: No writeback");
		}
		else {
			int loc = op1 ;
			if (ud==1) {
				if (pp==1) {
					loc = loc + op2;
				}
				regarray[base_1] = regarray[base_1] + op2;
			}
			else {
				if (pp==1) {
					loc = loc - op2;
				}
				regarray[base_1] = regarray[base_1] - op2;
			}
			mem[loc] = regarray[des_1];
			System.out.println("DECODE: Operation is STR, Base register is R"+base_1+", Offset is "+op2+", Source register is R"+des_1+".");
			System.out.println("Read registers: R"+base_1+" is " + op1 + ", Offset is " + op2 );
			System.out.println("EXECUTE: STR "+op1 +" and " + op2);
			System.out.println("MEMORY: Storing  "+mem[loc] + " in location " +loc);
			System.out.println("WRITEBACK: No writeback");
		}
		
	}
	
	public static void run() throws NumberFormatException, IOException {
		while (!end) {
			fetch();
			System.out.println("\n");
			
		}
		for (int i=0;i<=15;i++) {
			System.out.println("R"+i+": "+regarray[i]);
		}
	}
	public static void Mult(String s) {
		String rd = s.substring(12,16);
		String 	rs = s.substring(20,24);
		String rm = s.substring(28,32);
		int reg_des = regs.get(rd);
		int reg1 = regs.get(rs);
		int reg2 = regs.get(rm);
		int op1 = regarray[reg1];
		int op2 = regarray[reg2];
		System.out.println("DECODE: Operation is MUL, First Operand is R"+reg1+", Second operand is R"+reg2+", Destination register is R"+reg_des+".");
		System.out.println("Read registers: R"+reg1+" is " + op1 +", R"+reg2+" is "+op2);
		System.out.println("EXECUTE: MUL "+op1 +" and " + op2);
		System.out.println("MEMORY: No memory operation");
		int result = op1 * op2;
		System.out.println("WRITEBACK: write "+result+" to R"+reg_des);
	}
	public static void performALU(String s) {
		HashMap<String, String> opcodes = new HashMap<String , String>();
		opcodes.put("0000", "AND");
		opcodes.put("0001", "EOR");
		opcodes.put("0010", "SUB");
		opcodes.put("0100", "ADD");
		opcodes.put("1010", "CMP");
		opcodes.put("1100", "ORR");
		opcodes.put("1101", "MOV");
		opcodes.put("1111", "MVN");
		String opcode = s.substring(7, 11);
		String rn1 = s.substring(12,16);
		int reg1 = regs.get(rn1);
		String rd = s.substring(16,20);
		int reg3 = regs.get(rd);
		String operand = s.substring(20);
		int op1 = regarray[reg1];
		int op2=0;
		int reg2=0;
		if (Integer.parseInt(s.substring(6,7))==1) {
			String oper = s.substring(24);
			op2 = Integer.parseInt(oper, 2);
		}
		else {
			String rn2 = s.substring(28);
			reg2 = regs.get(rn2);
			op2 = regarray[reg2];
		}
		System.out.print("DECODE: Operation is "+opcodes.get(opcode)+", First operand is R"+reg1+", ");
		if (s.substring(6,7).equals("1")) {
			System.out.println("immediate Second Operand is "+ op2+", Destination register is R"+reg3+".");
			System.out.println("Read Registers: R"+reg1+": "+op1);
		}
		else {
			System.out.println("Second Register is R"+reg2+", Destination register is R"+reg3+".");
			System.out.println("Read Register is R"+reg1+": "+op1+", R"+reg2+": "+op2);
		}
		if (opcode.equals("1010")) {
			int result = op1 - op2;
			if (result == 0 ) {
				Z = 1;
				N=0;
			}
			if (result<0) {
				N=1;
				Z=0;
			}
			System.out.println("EXECUTE: " + opcodes.get(opcode) + " " +op1 +" and " + op2+"\nMEMORY: No Memory operation\nWRITEBACK: No writeback");
			
		}
		if (opcode.equals("0000")) {
			int result = op1 & op2;
			regarray[reg3] = result;
			System.out.println("EXECUTE: " + opcodes.get(opcode)+" "+op1 + " and " + op2 );
			System.out.println("MEMORY: No memory operation\nWRITEBACK: write " + result + " to R" +reg3 );
		}
		else if (opcode.equals("0001")) {
			int result = op1 ^ op2;
			regarray[reg3] = result;
			System.out.println("EXECUTE: " + opcodes.get(opcode)+" "+op1 + " and " + op2 );
			System.out.println("MEMORY: No memory operation\nWRITEBACK: write " + result + " to R" +reg3 );
		}
		else if (opcode.equals("0010")) {
			int result = op1-op2;
			regarray[reg3] = result;
			System.out.println("EXECUTE: " + opcodes.get(opcode)+" "+op1 + " and " + op2 );
			System.out.println("MEMORY: No memory operation\nWRITEBACK: write " + result + " to R" +reg3 );
		}
		else if (opcode.equals("0100")) {
			int result = op1+op2;
			regarray[reg3] = result;
			System.out.println("EXECUTE: " + opcodes.get(opcode)+" "+op1 + " and " + op2 );
			System.out.println("MEMORY: No memory operation\nWRITEBACK: write " + result + " to R" +reg3 );
		}
		else if (opcode.equals("1100")) {
			int result = op1|op2;
			regarray[reg3] = result;
			System.out.println("EXECUTE: " + opcodes.get(opcode)+" "+op1 + " and " + op2 );
			System.out.println("MEMORY: No memory operation\nWRITEBACK: write " + result + " to R" +reg3 );
		}
		else if (opcode.equals("1101")) {
			regarray[reg3] = op2;
			System.out.println("EXECUTE: " + opcodes.get(opcode)+" "+op2+" in R"+reg3);
			System.out.println("MEMORY: No memory operation\nWRITEBACK: No writeback" );
		}
		else if (opcode.equals("1111")) {
			regarray[reg3] = ~op2;
			System.out.println("EXECUTE: " + opcodes.get(opcode)+" "+op2+" in R"+reg3);
			System.out.println("MEMORY: No memory operation\nWRITEBACK: No writeback" );
		}
	}
	public static void main(String[] args) throws IOException {
		BufferedReader rd = new BufferedReader(new FileReader("./src/code.mem"));
		desimulator desim = new desimulator();
		String instruction = rd.readLine();
		while(instruction != null) {

			String address = instruction.split(" ")[0].substring(2);
			String hex = instruction.split(" ")[1].substring(2);
			Convertor c = new Convertor();
			int add = c.toint(c.convert(address))/4;
			String hexa = c.convert(hex);
			desim.instruction_mem[add][0] = hexa;
			desim.instruction_mem[add][1] = hex;
			desim.instruction_mem[add][2] = address;
			instruction=rd.readLine();
		}
		desim.run();
	}	
}

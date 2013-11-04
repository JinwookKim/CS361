/**
 *
 * @author Vaibha Gupta, Prad Nellaru
 */

import java.util.Collections;
import java.util.Arrays;
import java.util.Map;
import java.util.HashMap;

public class algorithm {

	static Map<Integer, byte[][]> keyMap;
	static byte key[][];
	static byte stream[][];
	static int iterations;

	public static void debug(String title) {
		if (false) {
			System.out.print("After " + title);
			if (title == "addRoundKey")
				System.out.print("(" + (iterations-1) + ")");
			System.out.println(":");
			print(stream, true);
		}
	}

	public static byte[] encrypt(byte[] input, byte[] keyIn) {
		keyMap = new HashMap<Integer, byte[][]>();;
		stream = twoD(input);
		key = twoD(keyIn);
		keyMap.put(0, twoD(keyIn));
		iterations = 1;
		for(int i = 0; i < 10; i++) {
			getRoundKey();
			iterations++;
		}

		iterations = 0;
		addRoundKey(iterations++);
		debug("addRoundKey");
		for (int i = 0; i < 10; i++) {
			subBytes();
			debug("subBytes");
			shiftRows();
			debug("shiftRows");
			if ( i != 9) {
				mixColumns();
				debug("mixColumns");
			}
			addRoundKey(iterations++);
			debug("addRoundKey");
		}
		return oneD(stream);
	}

	public static byte[] decrypt(byte[] input, byte[] keyIn) {
		keyMap = new HashMap<Integer, byte[][]>();;
		stream = twoD2(input);
		key = twoD(keyIn);
		keyMap.put(0, twoD(keyIn));
		iterations = 1;
		for(int i = 0; i < 10; i++) {
			getRoundKey();
			iterations++;
		}

		for(int i = 10; i > 0; i--)
		{
			iterations = i;
			addRoundKey(i);
			if( i != 10) {
				invMixColumns();
			}
			invShiftRows();
			invSubBytes();
		}
		addRoundKey(0);
		
		return oneD(stream);
	}

	public static byte[][] twoD(byte[] oneD) {
		byte twoD[][] = { { 0, 0, 0, 0 }, { 0, 0, 0, 0 }, { 0, 0, 0, 0 },
				{ 0, 0, 0, 0 } };
		for (int i = 0; i < oneD.length; i++) {
			twoD[i / 4][i % 4] = oneD[i];
		}
		return twoD;
	}

	public static byte[][] twoD2(byte[] oneD) {
		byte twoD[][] = { { 0, 0, 0, 0 }, { 0, 0, 0, 0 }, { 0, 0, 0, 0 },
				{ 0, 0, 0, 0 } };
		for (int i = 0; i < oneD.length; i++) {
			twoD[i % 4][i / 4] = oneD[i];
		}
		return twoD;
	}

	public static byte[] oneD(byte[][] twoD) {
		byte oneD[] = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
		for (int i = 0; i < oneD.length; i++) {
			oneD[i] = twoD[i % 4][i / 4];
		}
		return oneD;
	}
	
	static void print2d(byte[] array) {
		byte[][] barray = twoD(array);
		for (int row = 0; row < 4 ; row++ ){
			for(int col = 0; col < 4; col++ )
				System.out.format("%x ", barray[col][row]);
			System.out.println();
		}
	}
	static void print(byte[] array, boolean endline) {
		for (byte c : array)
			System.out.format("%x ", c);
		if (endline)
			System.out.println();
	}

	static void print(byte[] array) {
		print(array, false);
	}

	static void print(byte[][] array, boolean endline) {
		print(oneD(array), endline);
	}

	static void print(byte[][] array) {
		print(oneD(array), false);
	}

	static void subBytes() {
		for (int row = 0; row < 4; row++)
			for (int col = 0; col < 4; col++)
				stream[row][col] = lookup.find(lookup.type.SBOX,
						stream[row][col]);
	}

	static void invSubBytes() {
		for (int row = 0; row < 4; row++)
			for (int col = 0; col < 4; col++)
				stream[row][col] = lookup.find(lookup.type.ISBOX,
						stream[row][col]);
	}


	static void shiftRows() {
		for (int i = 0; i < 4; i++)
			rotateRowsLeft(stream[i], i);
	}

	static void rotateRowsLeft(byte[] row, int counter) {
		while (counter-- != 0) {
			byte temp = row[0];
			for (int i = 0; i < row.length - 1; i++)
				row[i] = row[i + 1];
			row[row.length - 1] = temp;
		}
	}

	static void invShiftRows() {
		for (int i = 0; i < 4; i++)
			rotateRowsLeft(stream[i], 4 - i);
	}

	static void mixColumns() {
		for (int i = 0; i < 4; i++) {
			byte[] t = {stream[0][i], stream[1][i], stream[2][i], stream[3][i]};
			stream[0][i] = (byte) (lookup.find(lookup.type.TABLE2,t[0]) ^ lookup.find(lookup.type.TABLE3,t[1]) ^ t[2] ^ t[3]);
			stream[1][i] = (byte) (t[0] ^ lookup.find(lookup.type.TABLE2,t[1]) ^ lookup.find(lookup.type.TABLE3,t[2]) ^ t[3]);
			stream[2][i] = (byte) (t[0] ^ t[1] ^ lookup.find(lookup.type.TABLE2,t[2]) ^ lookup.find(lookup.type.TABLE3,t[3]));
			stream[3][i] = (byte) (lookup.find(lookup.type.TABLE3,t[0]) ^ t[1] ^ t[2] ^ lookup.find(lookup.type.TABLE2,t[3]));
		}
	}

	static void invMixColumns() {
		for (int i = 0; i < 4; i++) {
			byte[] t = {stream[0][i], stream[1][i], stream[2][i], stream[3][i]};
			stream[0][i] = (byte) (lookup.find(lookup.type.TABLE14,t[0]) ^ lookup.find(lookup.type.TABLE11,t[1]) ^ 
				lookup.find(lookup.type.TABLE13,t[2]) ^ lookup.find(lookup.type.TABLE9,t[3]));
			stream[1][i] = (byte) (lookup.find(lookup.type.TABLE9,t[0]) ^ lookup.find(lookup.type.TABLE14,t[1]) ^ 
				lookup.find(lookup.type.TABLE11,t[2]) ^ lookup.find(lookup.type.TABLE13,t[3]));
			stream[2][i] = (byte) (lookup.find(lookup.type.TABLE13,t[0]) ^ lookup.find(lookup.type.TABLE9,t[1]) ^ 
				lookup.find(lookup.type.TABLE14,t[2]) ^ lookup.find(lookup.type.TABLE11,t[3]));
			stream[3][i] = (byte) (lookup.find(lookup.type.TABLE11,t[0]) ^ lookup.find(lookup.type.TABLE13,t[1]) ^ 
				lookup.find(lookup.type.TABLE9,t[2]) ^ lookup.find(lookup.type.TABLE14,t[3]));
		}
	}

	static void addRoundKey(int round) {
		//System.out.println("Using iterations: " + iterations);
		key = keyMap.get(round);
		for (int row = 0; row < 4; row++)
			for (int col = 0; col < 4; col++)
				stream[row][col] = (byte) (stream[row][col] ^ key[row][col]);
	}

	static void getRoundKey() {
		byte[][] next = nextKey128();
		keyMap.put(iterations, next);	
	};

	// code for generating next round key below
	static void roundkeyCore(byte[] t, byte iter) {
		rotateRowsLeft(t, 1);
		for (int i = 0; i < t.length; i++)
			t[i] = lookup.find(lookup.type.SBOX, t[i]);
		t[0] ^= lookup.find(lookup.type.RCON, iter);
	}

	static byte[][] nextKey128() {
		byte[][] k = keyMap.get(iterations - 1);
		byte[][] k2 = { { 0, 0, 0, 0 }, { 0, 0, 0, 0 }, { 0, 0, 0, 0 },
				{ 0, 0, 0, 0 } };
		for (int i = 0; i < 4; i++)
		for (int j = 0; j < 4; j++)
			key[i][j] = k[i][j];
		byte[] t = { 0, 0, 0, 0 };
		int c = 0;
		for (int ctr = 0; ctr < 4; ctr++) {
			for (int a = 0; a < 4; a++)
				t[a] = key[a][(c + 3) % 4];
			if (c % 4 == 0)
				roundkeyCore(t, (byte) iterations);
			for (int a = 0; a < 4; a++) {
				k2[a][c % 4] = (byte) (k[a][c % 4] ^ t[a]);
				key[a][c % 4] = (byte) (k[a][c % 4] ^ t[a]);
			}
			c++;
		}
		return k2;
	}
}

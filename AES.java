/**
*
* @author Vaibha Gupta, Prad Nellaru
*/

import java.util.*;
import java.io.*;

public class AES {

	public static void main(String[] args) {

		if(args.length < 3)
		{
			System.out.println("USAGE <e or d> <keyfile> <inputfile>");
			System.exit(-1);
		}

		boolean encrypt = true;

		if(args[0].equals("e"))
			encrypt = true;
		else if(args[0].equals("d"))
			encrypt = false;
		else
		{
			System.out.println("Invalid flag.");
			System.exit(-1);
		}

		
		String keyFile = args[1];
		String inputFile = args[2];

		try{
			Scanner keyScanner = new Scanner(new File(keyFile));
			String keyString = keyScanner.next();
			System.out.println("Key: " + keyString);
			byte[] keyBytes = hexStringToByteArray(keyString);
			keyScanner.close();

			String outputFileName = encrypt ? inputFile + ".enc" : inputFile + ".dec";
			PrintWriter output = new PrintWriter(outputFileName);

			Scanner inputScanner = new Scanner(new File(inputFile));
			while(inputScanner.hasNext())
			{
				String inputString = inputScanner.next();
				byte[] inputBytes = hexStringToByteArray(inputString);
				byte[] encryptedBytes = encrypt ? 
					algorithm.encrypt(inputBytes, keyBytes) : 
					algorithm.decrypt(inputBytes, keyBytes);
				String outString = byteArrayToHexString(encryptedBytes);
				System.out.println("OutStr " + outString);
				output.println(outString);
			}

			inputScanner.close();

			output.flush();
			output.close();

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
			

		System.exit(-1);

		try{

			byte[] input = {
							(byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, 
							(byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00,
							(byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00,
							(byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00
			};
			byte[] inKey = {
				(byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00,
				(byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00,
				(byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00,
				(byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00
			};
			System.out.println("Input: ");
			algorithm.print2d(input);

			System.out.println("Key: ");
			algorithm.print2d(inKey);

			byte[] encr = algorithm.encrypt(input, inKey);
			System.out.println("Encrypted: ");
			algorithm.print2d(encr);

			System.out.println("Key: ");
			algorithm.print2d(inKey);

			byte[] decr = algorithm.decrypt(encr, inKey);
			System.out.println("Decrypted: ");
			algorithm.print2d(decr);
			System.out.println("------------------");

		}catch(Exception e){
			e.printStackTrace();
		}
	}

	static String byteArrayToHexString(byte[] array)
	{
		StringBuilder buf = new StringBuilder();
		for (byte c : array)
		{
			String hex = Integer.toHexString(0xFF & c);
			if(hex.length() == 1)
				buf.append('0');
			buf.append(hex);
		}

		return buf.toString().toUpperCase();
	}

	static byte[] hexStringToByteArray(String s)
	{
		if(s.length() != 32)
		{
			System.err.println("hexString length not 32: " + s);
		}

		int len = s.length();
    	byte[] data = new byte[len / 2];
    	for (int i = 0; i < len; i += 2) {
        	data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                             + Character.digit(s.charAt(i+1), 16));
    	}

		return data;
	}

}

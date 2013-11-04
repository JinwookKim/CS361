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
			long totalBytes = 0;
			long totalTime = 0;
			while(inputScanner.hasNext())
			{
				String inputString = inputScanner.next();
				if( isValid(inputString)) {
					long start_time = System.nanoTime();
					byte[] inputBytes = hexStringToByteArray(inputString);
					byte[] encryptedBytes = encrypt ? 
						algorithm.encrypt(inputBytes, keyBytes) : 
						algorithm.decrypt(inputBytes, keyBytes);
					String outString = byteArrayToHexString(encryptedBytes);
					totalTime += System.nanoTime() - start_time;
					System.out.println("Out: " + outString);
					output.println(outString);
					totalBytes += inputString.length() / 2;
				}
			}
			inputScanner.close();
			output.flush();
			output.close();
			System.out.println("MB / second: " + ((totalBytes / 1048576) / (totalTime / 1e9)));
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		System.exit(0);
	}

	static boolean isValid(String input) {
		for (int i = 0; i < input.length(); i++) {
			char c = input.charAt(i);
			if ( !(c >= 0x30 && c < 0x39) && !( c >= 0x41 && c <= 0x46) )
				return false;
		}
		return true;
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
		while(s.length() < 32)
		{
			// System.err.println("hexString length not 32: " + s);
			s = s + "0";
		}
		if (s.length() > 32)
			s = s.substring(0, 32);

		int len = s.length();
    	byte[] data = new byte[len / 2];
    	for (int i = 0; i < len; i += 2) {
        	data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                             + Character.digit(s.charAt(i+1), 16));
    	}

		return data;
	}

}

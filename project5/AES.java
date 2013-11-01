/**
*
* @author Vaibha Gupta, Prad Nellaru
*/
public class AES {

	public static void main(String[] args) {
		try{

			String text = "Hello World";
			String j = "00000000000000";
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
			System.out.println("Raw Text: " + text);
			byte[] encr = algorithm.encrypt(input, inKey);
			System.out.println("Encrypted: ");
			algorithm.print2d(encr);

			byte[] decr = algorithm.decrypt(encr, inKey);
			System.out.println("Decrypted: ");
			algorithm.print2d(decr);
			System.out.println("------------------");

		}catch(Exception e){
			e.printStackTrace();
		}
	}

}
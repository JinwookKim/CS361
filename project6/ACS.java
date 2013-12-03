import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.TreeSet;


public class ACS
{
	private static class FileInfo
	{
		String user;
		char[] perms;
	}
	
	private static List<String> getLines(String fileName)
	{
		Scanner in = null;
		LinkedList<String> lines = new LinkedList<>();
		
		try
		{
			in = new Scanner(new File(fileName));
			
			while(in.hasNextLine())
			{
				String line = in.nextLine();
				lines.add(line);
			}
		} catch (FileNotFoundException e)
		{
			System.err.println("File " + fileName + " not found.");
			System.exit(-1);
		}
		finally
		{
			in.close();
		}
		
		return lines;
	}
	
	private static boolean canSetUsr(char num)
	{
		return num == '7' || num == '5' || num == '4';
	}
	
	private static boolean canSetGrp(char num)
	{
		return num == '7' || num == '3' || num == '2';
	}
	
	private static boolean canSticky(char num)
	{
		return num == '7' || num == '5' || num == '3' || num == '1';
	}
	
	private static boolean canRead(char num)
	{
		return num == '7' || num == '5' || num == '4';
	}
	
	private static boolean canWrite(char num)
	{
		return num == '7' || num == '3' || num == '2';
	}
	
	private static boolean canExe(char num)
	{
		return num == '7' || num == '5' || num == '3' || num == '1';
	}
	
	private static String getMode(char[] perms)
	{
		char[] base = {'-', '-', '-', '-', '-', '-', '-', '-', '-'};
		
		if (canRead(perms[1]))
            base[0] = 'r';
	    if (canWrite(perms[1]))
	            base[1] = 'w';
	    if (canExe(perms[1]))
	            base[2] = 'x';
	    if (canRead(perms[2]))
	            base[3] = 'r';
	    if (canWrite(perms[2]))
	            base[4] = 'w';
	    if (canExe(perms[2]))
	            base[5] = 'x';
	    if (canRead(perms[3]))
	            base[6] = 'r';
	    if (canWrite(perms[3]))
	            base[7] = 'w';
	    if (canExe(perms[3]))
	            base[8] = 'x';
	    if (canSetUsr(perms[0]))
	    	if (base[2] == 'x')
	            base[2] = 's';
	    	else
	            base[2] = 'S';
	    if (canSetGrp(perms[0]))
    		if (base[5] == 'x')
	            base[5] = 's';
	    	else
	            base[5] = 'S';
	    if (canSticky(perms[0]))
    		if (base[8] == 'x')
	            base[8] = 't';
	    	else
	            base[8] = 'T'; //changed
			
		return new String(base);
	}
	
	private static void writeLog(HashMap<String, String> userMap,
			HashMap<String, FileInfo> fileMap)
	{
		PrintWriter out = null;
		try
		{
			out = new PrintWriter(new File("state.log"));
			
			for(String file : new TreeSet<String>(fileMap.keySet()))
			{
				FileInfo data = fileMap.get(file);
				String owner = data.user;
				String group = userMap.get(owner);
				String mode = getMode(data.perms);
				out.write(mode + " " + owner + " " + group + " " + file + "\n");
			}
			
		} catch (FileNotFoundException e)
		{
			System.err.println("Can't write to state.log");
			System.exit(-1);
		}
		finally
		{
			out.close();
		}
		
	}

	private static int allowRead(String fileOwner, char[] filePerm,
			String fileGroup, String user, String userGroup, boolean root)
	{
		if(user.equals("root"))
			if(root)
				return 0;
			else
				return -1;
		if(user.equals(fileOwner) && canRead(filePerm[1]))
			return 1;
		if(userGroup.equals(fileGroup) && canRead(filePerm[2]))
			return 2;
		if(canRead(filePerm[3]))
			return 3;
		
		return -1;
	}
	
	private static int allowWrite(String fileOwner, char[] filePerm,
			String fileGroup, String user, String userGroup, boolean root)
	{
		if(user.equals("root"))
			if(root)
				return 0;
			else
				return -1;
		if(user.equals(fileOwner) && canWrite(filePerm[1]))
			return 1;
		if(userGroup.equals(fileGroup) && canWrite(filePerm[2]))
			return 2;
		if(canWrite(filePerm[3]))
			return 3;
		
		return -1;
	}
	
	private static int allowExe(String fileOwner, char[] filePerm,
			String fileGroup, String user, String userGroup, boolean root)
	{
		if(user.equals("root"))
			if(root)
				return 0;
			else
				return -1;
		if(user.equals(fileOwner) && canExe(filePerm[1]))
			return 1;
		if(userGroup.equals(fileGroup) && canExe(filePerm[2]))
			return 2;
		if(canRead(filePerm[3]))
			return 3;
		
		return -1;
	}
	
	private static int allowChmod(String fileOwner, char[] filePerm,
			String fileGroup, String user, String userGroup, boolean root)
	{
		if(user.equals("root"))
			if(root)
				return 0;
			else
				return -1;
		
		if(user.equals(fileOwner))
			return 1;
				
		return -1;
	}
	
	private static void read(String user, String file,
			HashMap<String, String> userMap, HashMap<String, FileInfo> fileMap,
			boolean root)
	{
		boolean status = true;
		if(!fileMap.containsKey(file))
			status = false;
		String userGroup = "";
		if(!userMap.containsKey(user))
		{
			status = false;
			userGroup = "None";
		}
		
		if(status)
		{
			FileInfo data = fileMap.get(file);
			userGroup = userMap.get(user);
			String fileOwner = data.user;
			String fileGroup = userMap.get(fileOwner);
			char[] filePerm = data.perms;
			if (allowRead(fileOwner, filePerm, fileGroup, user, userGroup, root) == -1)
				status = false;
		}
		int q = status ? 1 : 0;
		System.out.println("READ " + user + " " + userGroup + " " + q);
	}

	private static void write(String user, String file,
			HashMap<String, String> userMap, HashMap<String, FileInfo> fileMap,
			boolean root)
	{
		boolean status = true;
		if(!fileMap.containsKey(file))
			status = false;
		String userGroup = "";
		if(!userMap.containsKey(user))
		{
			status = false;
			userGroup = "None";
		}
		
		if(status)
		{
			FileInfo data = fileMap.get(file);
			userGroup = userMap.get(user);
			String fileOwner = data.user;
			String fileGroup = userMap.get(fileOwner);
			char[] filePerm = data.perms;
			if (allowWrite(fileOwner, filePerm, fileGroup, user, userGroup, root) == -1)
				status = false;
		}
		int q = status ? 1 : 0;
		System.out.println("WRITE " + user + " " + userGroup + " " + q);
	}
	
	private static void exe(String user, String file,
			HashMap<String, String> userMap, HashMap<String, FileInfo> fileMap,
			boolean root)
	{
		boolean status = true;
		if(!fileMap.containsKey(file))
			status = false;
		String userGroup = "";
		if(!userMap.containsKey(user))
		{
			status = false;
			userGroup = "None";
		}
		
		if(status)
		{
			FileInfo data = fileMap.get(file);
			userGroup = userMap.get(user);
			String fileOwner = data.user;
			String fileGroup = userMap.get(fileOwner);
			char[] filePerm = data.perms;
			if (allowExe(fileOwner, filePerm, fileGroup, user, userGroup, root) == -1)
				status = false;
			else
				if(canSetUsr(filePerm[0]))
					user = fileOwner;
				else
					userGroup = fileGroup;
		}
		int q = status ? 1 : 0;
		System.out.println("EXECUTE " + user + " " + userGroup + " " + q);
	}
	
	private static void chmod(String user, String file, String perms,
			HashMap<String, String> userMap, HashMap<String, FileInfo> fileMap,
			boolean root)
	{
		boolean status = true;
		if(!fileMap.containsKey(file))
			status = false;
		String userGroup = "";
		if(!userMap.containsKey(user))
		{
			status = false;
			userGroup = "None";
		}
		
		if(status)
		{
			FileInfo data = fileMap.get(file);
			userGroup = userMap.get(user);
			String fileOwner = data.user;
			String fileGroup = userMap.get(fileOwner);
			char[] filePerm = data.perms;
			if (allowChmod(fileOwner, filePerm, fileGroup, user, userGroup, root) == -1)
				status = false;
			else
				fileMap.get(file).perms = perms.toCharArray();
		}
		int q = status ? 1 : 0;
		System.out.println("CHMOD " + user + " " + userGroup + " " + q);
	}
	
	private static void startSystem(boolean root,
			HashMap<String, String> userMap, HashMap<String, FileInfo> fileMap)
	{
		boolean running = true;
		Scanner in = new Scanner(System.in);
		while(running)
		{
			String command = in.nextLine();
			String[] parts = command.split(" ");
			
			switch(parts[0].toLowerCase())
			{
			case "read":
				read(parts[1], parts[2], userMap, fileMap, root);
				break;
			case "write":
				write(parts[1], parts[2], userMap, fileMap, root);
				break;
			case "execute":
				exe(parts[1], parts[2], userMap, fileMap, root);
				break;
			case "chmod":
				chmod(parts[1], parts[2], parts[3], userMap, fileMap, root);
				break;
			case "exit":
				running = false;
				break;
			}
			writeLog(userMap, fileMap);
		}
		in.close();
	}

	public static void main(String[] args)
	{
		if(args.length < 2)
		{
			System.out.println("Usage: java ACS [-r] <userList> <fileList>");
			System.exit(-1);
		}
		
		boolean root = false;
		String userFile;
		String fileFile;
		
		if(args[0].equals("-r"))
		{
			root = true;
			userFile = args[1];
			fileFile = args[2];
		}
		else
		{
			userFile = args[0];
			fileFile = args[1];
		}
		
		HashMap<String, String> userMap = new HashMap<>();
		List<String> userLines = getLines(userFile);
		
		int lineNum = 0;
		for(String line : userLines)
		{
			String[] sarr = line.split(" ");
			if(sarr.length != 2)
			{
				System.err.println("Line " + lineNum + " in user list malformed.");
				System.exit(-1);
			}
			String user = sarr[0];
			String group = sarr[1];
			userMap.put(user, group);
			lineNum++;
		}
		
		userMap.put("root", "root");
		
		List<String> fileLines = getLines(fileFile);
		HashMap<String, FileInfo> fileMap = new HashMap<>();
		
		lineNum = 0;
		for(String line : fileLines)
		{
			String[] sarr = line.split(" ");
			if(sarr.length != 3)
			{
				System.err.println("Line " + lineNum + " in file list malformed.");
				System.exit(-1);
			}
			String file = sarr[0];
			String user = sarr[1];
			String perm = sarr[2];
			char[] perms = perm.toCharArray();
			
			FileInfo fi = new FileInfo();
			fi.user = user;
			fi.perms = perms;
			
			fileMap.put(file, fi);
			lineNum++;
		}
		
		startSystem(root, userMap, fileMap);
	}
}

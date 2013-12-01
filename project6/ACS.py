import sys

def canSetUsr(num):
	if num == '7' or num == '5' or num == '4':
		return True
	return False

def canSetGrp(num):
	if num == '7' or num == '3' or num == '2':
		return True
	return False

def canSticky(num):
	if num == '7' or num == '5' or num == '3' or num == '1':
		return True
	return False

def canRead(num):
	if num == '7' or num == '5' or num == '4':
		return True
	return False

def canWrite(num):
	if num == '7' or num == '3' or num == '2':
		return True
	return False

def canExe(num):
	if num == '7' or num == '5' or num == '3' or num == '1':
		return True
	return False


def allowRead(flOwn, flPerm, flGrp, usr, usrGrp, root):
	if usr == "root":
		if root:
			return 0
		else:
			return -1
	if usr == flOwn and canRead(flPerm[1]):
		return 1
	if usrGrp == flGrp and canRead(flPerm[2]):
		return 2
	if canRead(flPerm[3]):
		return 3
	return -1

def allowWrite(flOwn, flPerm, flGrp, usr, usrGrp, root):
	if usr == "root":
		if root:
			return 0
		else:
			return -1
	if usr == flOwn and canWrite(flPerm[1]):
		return 1
	if usrGrp == flGrp and canWrite(flPerm[2]):
		return 2
	if canWrite(flPerm[3]):
		return 3
	return -1

def allowExe(flOwn, flPerm, flGrp, usr, usrGrp, root):
	if usr == "root":
		if root:
			return 0
		else:
			return -1
	if usr == flOwn and canExe(flPerm[1]):
		return 1
	if usrGrp == flGrp and canExe(flPerm[2]):
		return 2
	if canExe(flPerm[3]):
		return 3
	return -1

def allowChmod(flOwn, flPerm, flGrp, usr, usrGrp, root):
	if usr == "root":
		if root:
			return 0
		else:
			return -1
	if usr == flOwn:
		return 1
	return -1

def read(user, fl, usrMap, flMap, root):
	status = True
	if fl not in flMap:
		status = False
	if usr not in usrMap:
		status = False
		usrGrp = None
	if status:
		flData = flMap[fl]
		usrGrp = usrMap[user]
		flOwn = flData[0]
		flGrp = usrMap[flOwn]
		flPerm = flData[1]
		if allowRead(flOwn, flPerm, flGrp, user, usrGrp, root) == -1:
			status = False
	print "READ", user, usrGrp, int(status)

def write(user, fl, usrMap, flMap, root):
	status = True
	if fl not in flMap:
		status = False
	if usr not in usrMap:
		status = False
		usrGrp = None
	if status:
		flData = flMap[fl]
		usrGrp = usrMap[user]
		flOwn = flData[0]
		flGrp = usrMap[flOwn]
		flPerm = flData[1]
		if allowWrite(flOwn, flPerm, flGrp, user, usrGrp, root) == -1:
			status = False
	print "WRITE", user, usrGrp, int(status)

def exe(user, fl, usrMap, flMap, root):
	status = True
	if fl not in flMap:
		status = False
	if usr not in usrMap:
		status = False
		usrGrp = None
	if status:
		flData = flMap[fl]
		usrGrp = usrMap[user]
		flOwn = flData[0]
		flGrp = usrMap[flOwn]
		flPerm = flData[1]
		if allowExe(flOwn, flPerm, flGrp, user, usrGrp, root) == -1:
			status = False
		else:
			if canSetUsr(flPerm[0]):
				user = flOwn
			if canSetGrp(flPerm[0]):
				usrGrp = flGrp
	print "EXECUTE", user, usrGrp, int(status)

def chmod(user, fl, perms, usrMap, flMap, root):
	status = True
	if fl not in flMap:
		status = False
	if usr not in usrMap:
		status = False
		usrGrp = None
	if status:
		flData = flMap[fl]
		usrGrp = usrMap[user]
		flOwn = flData[0]
		flGrp = usrMap[flOwn]
		flPerm = flData[1]
		if allowChmod(flOwn, flPerm, flGrp, user, usrGrp, root) == -1:
			status = False
		else:
			flMap[fl][1] = [x for x in perms]
	print "CHMOD", user, usrGrp, int(status)

def getMode(perms):
	base = ["-"] * 9;
	if canRead(perms[1]):
		base[0] = 'r'
	if canWrite(perms[1]):
		base[1] = 'w'
	if canExe(perms[1]):
		base[2] = 'x'
	if canRead(perms[2]):
		base[3] = 'r'
	if canWrite(perms[2]):
		base[4] = 'w'
	if canExe(perms[2]):
		base[5] = 'x'
	if canRead(perms[3]):
		base[6] = 'r'
	if canWrite(perms[3]):
		base[7] = 'w'
	if canExe(perms[3]):
		base[8] = 'x'
	if canSetUsr(perms[0]):
		base[2] = 's' if base[2] == 'x' else 'S'
	if canSetGrp(perms[0]):
		base[5] = 's' if base[5] == 'x' else 'S'
	if canSticky(perms[0]):
		base[8] = 't' if base[8] == 'x' else 't'
	return "".join(base)


def writeLog(usrMap, flMap):
	output = open('state.log', 'w')
	for fl in flMap:
		flData = flMap[fl]
		flOwn = flData[0]
		flGrp = userMap[flOwn]
		flMode = getMode(flData[1])
		line = flMode + " " + flOwn + " " + flGrp + " " + fl
		output.write(line + "\n")
	output.close()

def startSystem(root, usrMap, flMap):
	running = True
	while running:
		command = raw_input()
		parts = command.split(' ')
		if parts[0] == "READ":
			read(parts[1], parts[2], userMap, flMap, root)
		elif parts[0] == "WRITE":
			write(parts[1], parts[2], userMap, flMap, root)
		elif parts[0] == "EXECUTE":
			exe(parts[1], parts[2], userMap, flMap, root)
		elif parts[0] == "CHMOD":
			chmod(parts[1], parts[2], parts[3], userMap, flMap, root)
		elif parts[0] == "EXIT":
			running = False
	writeLog(userMap, flMap)


if __name__ == '__main__':
	if len(sys.argv) < 3:
		print "Usage: python ACS.py [-r] <userList> <fileList>"
		exit(-1)
	if sys.argv[1] == "-r":
		root = True
		userFile = sys.argv[2]
		fileFile = sys.argv[3]
	else:
		root = False
		userFile = sys.argv[1]
		fileFile = sys.argv[2]
	
	userMap = {}
	fileMap = {}

	# get users mapped to groups
	for line in open(userFile):
		usr, grp = line[0:-1].split(' ')
		userMap[usr] = grp

	if root:
		userMap["root"] = "root"

	# get files mapped with users and permissions
	for line in open(fileFile):
		fl, usr, perm = line[0:-1].split(' ')
		fileMap[fl] = [usr, [x for x in perm]]

	startSystem(root, userMap, fileMap)
COMPSYS 725 - Assignment 1
SFTP Client-Server System
by Dhairya Trivedi, dtri542

--------------------------------------------------------------
Contents:
> How to install/setup
> Operation
--------------------------------------------------------------

SETUP:
	1. Extract all files into a folder. Client and server file can be placed in separate folders however make sure the "users.txt" file is placed in
	folder as the server file.

	2. Open 2 terminal windows and navigate both to the server file's folder and the client file's folder.

	3. Run the command "javac *.java" in any one of the terminals.
	
	4. In one terminal, run the command "java SFTPServer" and in another run "java SFTPClient". Make sure you run the server file before the client

	5. Enter commands into the client terminal. The format will be:

		"COMMAND <args>"

		"FROM SERVER: <server-response>"

	where the "FROM SERVER" is the response from the server based on the user commands.

OPERATION:
	The commands that are available for the user in this system are as follows:

		USER | PASS | ACCT | TYPE | LIST | CDIR | KILL | NAME | TOBE | DONE
	
	A user must first begin by logging in to the server to use any of the commands. This is done by using the USER command in the following way:

		USER <username>

	If a valid username is entered, then the corresponding correct password must be entered and subsequentyl the account associated with that user:

		PASS <password>
		ACCT <account-name>

	The PASS command must be done before the ACCT command can be used in order to successfully verify the user.
	After this verification is successful, the remaining commands can be used. The DONE command is used to end the connection with the server.
	The remaining commands are explained in further detail below:
	
	TYPE {A | B | C}
	This command determines the mode of transfer of files: A-ASCII B-Binary C-Continuous

	LIST {F | V} <directory-path>(optional)
	This command allows you to list all the files that are listed in the current working directory and any sub-directories that exist within it as well.
	The F argument will list just the filenames, whilst the V argument will list extra data as well in the following order:

	<filename> <time-created> <size> <last-modified-time> with the time values in the format "YY-MM-DD hh:mm:ss"
	
	If the optional directory-path argument is also given, then the listing will be shown for that relevant directory, otherwise it will list from the 
	current operating directory of the server.

	CDIR <directory-path>
	This command allows you to change the current operating directory of the server to the directory-path provided in the argument.
	The directory path can be to go further into one of the subdirectories or to go above a level using the "../" character.

	KILL <filename>
	This command is used to delete a file existing in the current working directory.

	NAME <filename>
	This command is used to rename a file that currently exists in the working directory. The filename provided as an argument for this command is the 
	existing name, and is followed up by the TOBE command.
	
	TOBE <filename>
	This command gives the new name for the file to be renamed, if the original file exists. 

	To add any users into the server system, you can modify the current existing user.txt file with each user on a newline.
	The format for adding user details is as follows:
		<username>;<password>;<account-name>	
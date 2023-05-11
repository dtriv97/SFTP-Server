import java.io.BufferedInputStream;
import java.io.DataOutputStream;

import javax.xml.crypto.Data;

public class ServerRequestHandler {
    /* Server IO interactions */
    private BufferedInputStream req;
    private DataOutputStream resp;
    private String clientSentence;
    private String response = "";
    private Boolean connected = false;

    /* Variables for file read/write and directory location */
    private String userFileName = "userData.txt";
    private Boolean fileFound = false;
    private String fileToChange = "";
    
    /* Variables for user verification process */
    private Boolean userVerified = false;
    private Boolean passwordVerified = false;
    private Boolean accountVerified = false;
    private Boolean userLoggedIn = false;

    public ServerRequestHandler(BufferedInputStream req, DataOutputStream resp) {
        this.req = req;
        this.resp = resp;
        requestHandler();
    }

    private void requestHandler() {
        System.out.println("Hello World!\n");
        //Variables used for functions within server commands
        int userLine = 0;
        String line = null;

        //Main thread of the server
        while(this.connected) {

            //Reading input from client user
            clientSentence = inFromClient.readLine();
            
            //Decoding the input from user
            switch(clientSentence.substring(0,4)){

                /*
                    USER Command - the first command that is used to verify a user
                */
                case "USER":
                    FileReader fileReader = new FileReader(userFileName);
                    BufferedReader bufferedReader = new BufferedReader(fileReader);

                    String userIn = clientSentence.substring(5);
                    userLine = 0;
                    if(userIn.equals("SuperUser")){
                        accountVerified = true;
                        response = "!Logged in";
                        break;
                    }
                    while((line = bufferedReader.readLine()) != null) {
                        int i = line.indexOf(';');
                        if(i == -1) break;
                        if(line.substring(0, i).equals(userIn)) {
                            userVerified = true;
                            response = "+User-id valid, send account and password";
                            break;
                        } else {
                            response = "-Invalid user-id, try again";
                        }
                        userLine++;
                    }
                    bufferedReader.close();  
                    break;

                /*
                    PASS Command - the next input after USER must be PASS with the user password, to verify user
                */
                case "PASS":
                    if(userVerified) {
                        FileReader passReader = new FileReader(userFileName);
                        BufferedReader passBufferedReader = new BufferedReader(passReader);
                        String passIn = clientSentence.substring(5);
                        int passLine = 0;
                        while((line = passBufferedReader.readLine()) != null) {
                            if(passLine == userLine) {
                                int i = line.indexOf(';');
                                int j = line.indexOf(';', i+1);
                                if(line.substring(i+1, j).equals(passIn)) {
                                    passwordVerified = true;
                                    response = "+Send account";
                                    break;
                                } else {
                                    passwordVerified = false;
                                    response = "-Wrong password, try again";
                                    break;
                                }
                            }
                            passLine++;
                        }
                        passBufferedReader.close();
                    } else {
                        System.out.println("PLEASE ENTER USER NAME FIRST");
                    }
                    break;

                /*
                    ACCT Command - After USER and PASS verification, user must enter the account they want to use
                */
                case "ACCT":
                    if(userVerified && passwordVerified){
                        FileReader acctReader = new FileReader(userFileName);
                        BufferedReader acctBufferedReader = new BufferedReader(acctReader);
                        String passIn = clientSentence.substring(5);
                        int acctLine = 0;
                        while((line = acctBufferedReader.readLine()) != null) {
                            if(acctLine == userLine) {
                                int i = line.indexOf(';');
                                int j = line.indexOf(';', i+1);
                                if(line.substring(j+1).equals(passIn)) {
                                    accountVerified = true;
                                    response = "!Account valid, logged-in";
                                    break;
                                } else {
                                    accountVerified = false;
                                    response = "-Invalid acount, try again";
                                    break;
                                }

                            }
                            acctLine++;
                        }
                        acctBufferedReader.close();
                    } else {
                        response = "-User needs to login to the server first";
                    }
                    break;

                /*
                    TYPE Command - used to determine file transfer mode
                */
                case "TYPE":
                    if(accountVerified){    //Verify account logged in
                        String transferMode = clientSentence.substring(5);    //Command argument
                        if(transferMode.equals("A")){
                            response = "+Using ASCII mode";
                        } else if(transferMode.equals("B")){
                            response = "+Using Binary mode";
                        } else if(transferMode.equals("C")){
                            response = "+Using Continuous mode";
                        } else {
                            response = "-Type not valid";
                        }
                    } else {
                        response = "-User needs to login to the server first";
                    }
                    break;

                /*
                    LIST Command - used to list files stored in the current directory (default is root folder)
                */
                case "LIST":
                    response = "\n";
                    if(accountVerified){    //Verify account logged in
                        try{
                            String dirToGo = "";
                            try {
                                dirToGo = clientSentence.substring(7);
                                dirToGo = currentDir + '\\' + clientSentence.substring(7) + '\\';
                            } catch (Exception e){
                                dirToGo = currentDir;
                            }
                            try (Stream<Path> walk = Files.walk(Paths.get(dirToGo))) {
                                List<String> result = walk.filter(Files::isRegularFile).map(x -> x.toString()).collect(Collectors.toList());
                                if(clientSentence.substring(5, 6).equals("F")) {            //Normal file listing format

                                    for(int i = 0; i<result.size(); i++) {
                                        String name = result.get(i);
                                        int index = name.lastIndexOf("\\");
                                        name = name.substring(index+1);
                                        response = response + name + '\n';
                                    }

                                } else if(clientSentence.substring(5,6).equals("V")) {        //Verbose File listing format

                                    String pattern = "yyyy-MM-dd HH:mm:ss";
                                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);

                                    for(int i = 0; i<result.size(); i++){
                                        
                                        String name = result.get(i);
                                        int index = name.lastIndexOf("\\");
                                        name = name.substring(index+1);

                                        Path file = Paths.get(result.get(i));
                                        BasicFileAttributes attr = Files.readAttributes(file, BasicFileAttributes.class);
                                        String timeCreated = simpleDateFormat.format(attr.creationTime().toMillis());
                                        String timeModified = simpleDateFormat.format(attr.lastModifiedTime().toMillis());
                                        long size = attr.size();
                                        response = response + name + "  " + timeCreated + "  " + size +  " bytes  " + timeModified +'\n';
                                    }
                                } else{
                                    response = "-Invalid argument provided";
                                }
                            } catch (IOException e) {
                                response = "-Folder doesn't exist";
                            }
                        } catch (Exception e){
                            response = "-Invalid command or argument";
                        }
                    }
                    break;

                /*
                    CDIR Command - used to change current working directory of the user
                */    
                case "CDIR":
                    if(accountVerified){
                        try {
                            String dirIn = clientSentence.substring(5);
                            if(dirIn.substring(0,2).equals("..")){                                    //Using ".." implies moving 1 level above current directory
                                String temp = currentDir.substring(0,currentDir.lastIndexOf('\\'));
                                try{
                                    Path test = Paths.get(temp + dirIn.substring(2));
                                    System.out.println(dirIn.substring(2));
                                    if(Files.exists(test)){
                                        currentDir = temp + dirIn.substring(2);
                                        response = "!Changed working dir to " + currentDir;
                                    }
                                } catch(Exception e) {
                                    currentDir = temp;
                                    response = "!Changed working dir to " + currentDir;
                                }
                            } else {                                                                //Moving into a folder within current directory
                                Path p = Paths.get(currentDir + "\\" + dirIn);
                                if(Files.exists(p)) {
                                    currentDir = currentDir + "\\" + dirIn;
                                    response = "!Changed working dir to " + currentDir;
                                } else {
                                    response = "-Directory does not exist";
                                }
                            }
                        } catch (Exception e) {
                            response = "-Valid directory name must be provided";
                        }
                    }
                    break;

                /*
                    KILL Command - used to delete a file in the current directory
                */
                case "KILL":
                    if(accountVerified){
                        try {
                            String inFile = clientSentence.substring(5);
                            Path p = Paths.get(currentDir+'\\'+inFile);
                            if(Files.exists(p)){
                                File file = new File(currentDir+'\\'+inFile);
                                file.delete();
                                response = "+" + inFile + " deleted";
                            } else {
                                response = "-not deleted because file does not exist";
                            }
                        } catch(Exception e) {
                            response = "-Invalid file name used";
                        }
                    }
                    break;

                /*
                    NAME Command - used to rename a file on the remote server
                */    
                case "NAME":
                    if(accountVerified){
                        try{
                            String inFile = clientSentence.substring(5);
                            Path p = Paths.get(currentDir+'\\'+inFile);
                            if(Files.exists(p)){
                                fileToChange = inFile;
                                response = "+File exists";
                                fileFound = true;
                            } else {
                                response = "-Can't find " + inFile;
                                fileFound = false;
                            }
                        } catch(Exception e){
                            response = "-Invalid argument provided";
                        }
                    }
                    break;

                /*
                    TOBE Command - used to rename a file on the remote server
                */    
                case "TOBE":
                    if(accountVerified){
                        if(fileFound){
                            String inFile = clientSentence.substring(5);
                            if(inFile.equals("")){
                                response = "-Enter valid name";
                            } else {
                                File origFile = new File(currentDir+'\\'+fileToChange);
                                File newName = new File(currentDir+'\\'+inFile);
                                System.out.println(origFile.renameTo(newName));
                                fileFound = false;
                                response = "+" + fileToChange + " changed to " + inFile;
                            }
                        } else {
                            response = "-Enter a valid NAME command first";
                        }
                    }
                    break;

                /*
                    DONE Command - used to close connection with the server
                */    
                case "DONE":
                    connected = false;
                    response = "+Closing server connection";
                    break;

                /*
                    MISC Command - any other commands are treated as incorrect inputs and an error is returned
                */    
                default:
                    response = "-Invalid command";
            }

            //Send server response to client
            outToClient.writeBytes(response + '\n');
        }        
    }
}

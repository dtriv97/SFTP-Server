/*    
    SFTP Server - Dhairya Trivedi
    This implements a server based on the SFTP (Simple File Transfer Protocol).
    The accompanying readme provides details on how to interact with this server and 
    how to read the responses. The server must be run before the client in order to 
    operate successfully.
*/
import java.io.*; 
import java.net.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Stream;
import javax.management.loading.PrivateClassLoader;
import java.util.stream.Collectors;
import java.text.SimpleDateFormat;

public class SFTPServer {
    // ------------- MEMBER VARIABLES --------------
    private ServerSocket serverSock;

    private static int checkPortParam(String argv[]) {
        /* Add parsing logic for the input port value */
        return Integer.parseInt(argv[0]);
    }

    public void init(int portNum) throws IOException {
        this.serverSock = new ServerSocket(portNum);
        if (this.serverSock != null) {

            this.connected = true;

            //Establish connection with client
            Socket connectionSocket = this.serverSock.accept();
            BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream())); 
            DataOutputStream  outToClient = new DataOutputStream(connectionSocket.getOutputStream()); 
        }
    }

    /* TODO: Remove main function from this and create the SFTP Server as a class by itself */
    public static void main(String argv[]) throws Exception {
        new SFTPServer().init(checkPortParam(argv));
    }
}

/**
 * author: Yanjun Peng
 * student ID:906571
 */
package DictionaryClient;

import java.io.*;
import java.net.*;

public class Client {
    private String host;
    private int port;
    private Socket client;
    private DataOutputStream os;
    private DataInputStream is;

    /**
     * Client constructor: prepare for socket communication
     * @param host
     * @param port
     * @throws IOException
     */
    public Client(String host,int port) throws IOException {
        this.host=host;
        this.port=port;
        this.client = new Socket(this.host, this.port);
        this.os = new DataOutputStream(client.getOutputStream());
        this.is = new DataInputStream(client.getInputStream());
    }

    /**
     * close client, at the same time tells the server that I will exit.
     */
    public void clientClose(){
        try {
            ioStream("exit");
            client.close();
        }catch (Exception e){
            System.exit(1);
        }
    }

    /**
     * io with the server; if exception occurs, return "deny"
     * @param argsStr
     * @return
     */
    public String ioStream(String argsStr){
        String resStr="";
        try
        {
            os.writeUTF(argsStr);
            os.flush();
            resStr = is.readUTF();
            return resStr;
        }catch (Exception e){
            return "deny";
        }
    }

    /**
     * tell the server the client will exit, finally.
     * @throws IOException
     */
    protected void finalize()throws IOException{
        os.writeUTF("exit");
        clientClose();
    }

}

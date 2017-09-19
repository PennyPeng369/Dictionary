/**
 * author: Yanjun Peng
 * student ID:906571
 */
package DictionaryServer;

import java.net.*;
import java.io.*;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.logging.Level;

import org.json.*;

import static DictionaryServer.Server._LOG;

class ServerThread implements Runnable {
    private Socket fromClient=null;
    private DictOperations dictOperator=null;
    private String dictPath="src/DictionaryServer/dictionary2.json";

    /**
     * ServerThread constructor
     * @param socket
     * @param dictPath
     */
    public ServerThread(Socket socket,String dictPath){
        this.fromClient=socket;
        this.dictPath=dictPath;
    }

    /**
     * override run()
     */
    public void run() {
        DataInputStream is = null;
        DataOutputStream os = null;
        String word;
        String operation;
        String argsStr=null;
        String resStr;
        String notes;
        try {
            is = new DataInputStream(fromClient.getInputStream());
            os = new DataOutputStream(fromClient.getOutputStream());
            while (true) {
                dictOperator=new DictOperations(this.dictPath);
                argsStr = is.readUTF();  //read the args form client
                dictOperator.readDict();
                if (argsStr.equals("exit")){
                    os.writeUTF("");
                    break;
                }
                resStr=parseAndOperate(argsStr);
                os.writeUTF(resStr);
                os.flush();

            }
        } catch (IOException ex) {
            InetAddress address = fromClient.getInetAddress();
            _LOG.log(Level.WARNING, ex.getMessage(), ex);

        } finally {
            try {
                is.close();
                os.close();
            } catch (Exception ioe) {
                _LOG.log(Level.WARNING, ioe.getMessage(), ioe);
            }
            try {
                fromClient.close();
            } catch (Exception clientEx) {
                _LOG.log(Level.WARNING, clientEx.getMessage(), clientEx);
            }
        }
    }

    /**
     * parse the JSON from the client and return the result of an operation
     * @param argsStr
     * @return
     */
    public String parseAndOperate(String argsStr){
        JSONObject argsJSON= new JSONObject(argsStr);
        String word=argsJSON.getString("word");
        String operation=argsJSON.getString("operation");
        String notes=argsJSON.getString("notes");
        if (operation.equals("wordQuery")){
            String res=dictOperator.wordQuery(word);
            if (res!=""){
                argsJSON.put("status",1);
                argsJSON.put("notes",res);
            }else {
                argsJSON.put("status",0);
                _LOG.info("Failed to query or the word is not in MyDictionary. IP:"+fromClient.getInetAddress()+" Data:"+argsStr);
            }
        }
        if (operation.equals("wordAdd")){
            int rt=dictOperator.wordAdd(word,notes);
            if (rt==1){
                argsJSON.put("status",1);
            }else if (rt==0){
                argsJSON.put("status",0);
                _LOG.info("Failed to add the word to MyDictionary. IP:"+fromClient.getInetAddress()+" Data:"+argsStr);
            }else if (rt==2){
                argsJSON.put("status",2);
            }
        }
        if (operation.equals("wordRemove")){
            int rt=dictOperator.wordRemove(word);
            if (rt==0){
                argsJSON.put("status",1);
                _LOG.info("Failed to remove the word from MyDictionary. IP:"+fromClient.getInetAddress()+" Data:"+argsStr);
            }else if (rt==1){
                argsJSON.put("status",0);
            }else if (rt==2){
                argsJSON.put("status",2);
            }
        }
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HH:mm:ss").format(Calendar.getInstance().getTime());
        argsJSON.put("time",timeStamp);
        String resStr=argsJSON.toString();
        return resStr;
    }
}


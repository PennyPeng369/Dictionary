/**
 * author: Yanjun Peng
 * student ID:906571
 */
package DictionaryServer;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;

import static DictionaryServer.Server._LOG;

public class ServerExecutor extends Thread{
    private String dictPath;
    private int port;
    protected volatile static String DICT_STR_CACHE;

    /**
     * ServerExecutor constructor
     * @param port
     * @param dictPath
     */
    public ServerExecutor(int port,String dictPath ){
        this.port=port;
        this.dictPath=dictPath;
    }

    /**
     * override run():create a new cached thread pool
     */
    public void run(){
        try{
            ServerSocket server = new ServerSocket(port);
            Socket fromClient;
            int count = 0;
            ExecutorService executorService= Executors.newCachedThreadPool();
            while (true) {
                fromClient = server.accept();
                //ServerThread serverThread = new ServerThread(fromClient,dictPath);
                executorService.execute(new ServerThread(fromClient,dictPath));
                //Runtime.getRuntime().addShutdownHook(serverThread);
                //serverThread.start();

                count++;
                System.out.println("the number of clients: " + count);
            }
        }catch (Exception e){
            _LOG.log(Level.SEVERE, e.getMessage(), e);

        }
    }

    /**
     * reload dictionary cache
     * @param dictStr
     */
    public static synchronized void setDictStrCache(String dictStr){
        DICT_STR_CACHE=dictStr;
    }

    /**
     * initiate the dictionary cache
     */
    public void initiateDictStrCache(){
        FileReader fr=null;
        BufferedReader br=null;
        String st;
        StringBuilder fileStr= new StringBuilder();
        try{
            fr=new FileReader(dictPath);
            br=new BufferedReader(fr);
            while ((st=br.readLine())!=null){
                fileStr.append(st.trim());
            }
            DICT_STR_CACHE=fileStr.toString();
        }catch (IOException e){
            _LOG.log(Level.SEVERE, e.getMessage(), e);
        }finally {
            try{
                if (br!=null)
                    br.close();
                if (fr!=null)
                    fr.close();
            }catch (IOException e){
                _LOG.log(Level.WARNING, e.getMessage(), e);
            }
        }
    }
}

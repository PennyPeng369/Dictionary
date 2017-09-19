/**
 * author: Yanjun Peng
 * student ID:906571
 */
package DictionaryServer;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Scanner;
import java.util.logging.*;

public class Server {
    protected static final Logger _LOG=Logger.getLogger("serverLogger");

    /**
     * initiate the log components
     */
    private static void initiateLog(){
        try {
            _LOG.setLevel(Level.ALL);
            String logDate = new SimpleDateFormat("yyyyMMdd_HH:mm:ss").format(Calendar.getInstance().getTime());
            FileHandler fileHandler = new FileHandler(logDate);
            fileHandler.setLevel(Level.ALL);
            fileHandler.setFormatter(new LogFormatter());
            _LOG.addHandler(fileHandler);
        }catch (Exception e){
            System.out.println("Failed to initiate log...");
            e.printStackTrace();
        }

    }

    /**
     * main entrance of the server:create a new ServerExecutor thread; listening "quit" command from the console.
     * @param args
     */
    public static void main(String[] args)  {
        initiateLog();
        try {
            ServerExecutor executorThread = new ServerExecutor(Integer.parseInt(args[0]), args[1]);
            executorThread.initiateDictStrCache();
            Scanner scanner = new Scanner(System.in);
            int count = 0;
            Runtime.getRuntime().addShutdownHook(executorThread);
            executorThread.start();
            System.out.println("MyDictionary Server starts");
            while (true) {
                if (scanner.hasNext()) {
                    String command = scanner.nextLine();
                    if (command.equals("quit")){
                        _LOG.info("MyDictionary Server is quiting.");
                        for(Handler h:_LOG.getHandlers())
                        {
                            h.close();   //must call h.close or a .LCK file will remain.
                        }
                        break;
                    }
                }
            }
            System.exit(0);
        }catch (Exception ex){
            _LOG.log(Level.SEVERE, ex.getMessage(), ex);

        }finally {
            System.exit(-1);
        }

    }
}

/**
 * LogFormatter
 */
class LogFormatter extends Formatter {
    @Override
    public String format(LogRecord record) {
        Date date = new Date();
        String sDate = date.toString();
        return "[" + sDate + "]"  +"[" + record.getLevel() + "]"
                 + "[" +record.getMessage() + "]" +"\n";
    }

}
/**
 * author: Yanjun Peng
 * student ID:906571
 */
package DictionaryServer;

import org.json.*;
import java.io.*;
import java.util.logging.Level;

import static DictionaryServer.Server._LOG;


public class DictOperations {
    private  String dictPath="src/DictionaryServer/dictionary2.json";
    //private  String dictStr=null;

    /**
     * DictOperations constructor
     * @param dictPath
     */
    public DictOperations(String dictPath){
        this.dictPath=dictPath;
    }

    /**
     * read from the dictionary cache
     * @return
     */
    public String readDict(){
        return ServerExecutor.DICT_STR_CACHE;
    }

    /**
     * write new dictionary info into the dictionary file
     * @param newDict
     * @return
     */
    private synchronized int writeDict(String newDict){
        FileWriter fw;
        try {
            fw=new FileWriter(dictPath);
            fw.write(newDict);
            fw.close();
            ServerExecutor.setDictStrCache(newDict);

        }catch (IOException e){
            _LOG.log(Level.WARNING, e.getMessage(), e);
            return 0;
        }
        return 1;
    }

    /**
     * query a word
     * @param word
     * @return
     */
    public String wordQuery(String word){
        String rt="";
        JSONObject dictJSON=new JSONObject(readDict());
        try {
            rt=dictJSON.getString(word.toUpperCase());

        }catch (JSONException e){
            return "";
        }
        return rt;
    }

    /**
     * add a word
     * @param word
     * @param meaning
     * @return
     */
    public int wordAdd(String word,String meaning){
        if (this.wordQuery(word)!="")
            return 2;   //"return 2" means the word exists.
        int rt=0;
        JSONObject dictJSON=new JSONObject(readDict());
        String newString;
        try{
            if (word.isEmpty())
                return 0;
            dictJSON.put(word.toUpperCase(),meaning);
            newString=dictJSON.toString();
            rt=this.writeDict(newString);
            //dictStr=newString;
        }catch (JSONException e){
            return 0;
        }
        return rt;
    }

    /**
     * remove a word
     * @param word
     * @return
     */
    public int wordRemove(String word){
        if (this.wordQuery(word)==""||word.isEmpty())
            return 2;      //"return 2" means the word doesn't exist.
        JSONObject dictJSON=new JSONObject(readDict());
        int rt;
        try{
            if (dictJSON.remove(word.toUpperCase())==null)
                return 0;
            String newString=dictJSON.toString();
            rt=this.writeDict(newString);
            //dictStr=newString;
        }catch (JSONException e){
            return 0;
        }
        return rt;
    }

}

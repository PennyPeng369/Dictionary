/**
 * author: Yanjun Peng
 * student ID:906571
 */
package DictionaryClient;

import org.json.JSONObject;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static javax.swing.JOptionPane.showMessageDialog;

public class SwingGUI {
    private JButton queryButton;
    private JButton addButton;
    private JButton removeButton;
    private JTextField textField1;
    private JTextField textField2;
    private JTextPane welcomeToMyDictionaryTextPane;
    private JPanel panel1;

    private Client client;

    /**
     * SwingGUI constructor
     * @param host
     * @param port
     */
    public SwingGUI(String host,int port) {
        try {
            this.client=new Client(host,port);
        }catch (Exception e){
            showMessageDialog(null,"Sorry. MyDictionary server is traveling outside:)\nPlease restart later.");
        }
        /**
         * action listener of querying button
         */
        queryButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String word=textField1.getText().trim();
                if (word.equals("")||!wordJudge(word)){
                    showMessageDialog(null, "Please input a valid word.");
                }else {
                    JSONObject argsJSON = new JSONObject();
                    argsJSON.put("word",word.toUpperCase());
                    argsJSON.put("operation","wordQuery");
                    argsJSON.put("notes","");
                    String timeStamp = new SimpleDateFormat("yyyyMMdd_HH:mm:ss").format(Calendar.getInstance().getTime());
                    argsJSON.put("time",timeStamp);
                    String argsStr=argsJSON.toString();
                    String resStr="";
                    try{
                        resStr=client.ioStream(argsStr);
                    }catch(Exception ex){
                        showMessageDialog(null,"Sorry. MyDictionary server is traveling outside:)\nPlease restart later.");
                    }
                    if (resStr.equals("deny")){
                        showMessageDialog(null,"Sorry. MyDictionary server is traveling outside:)\nPlease restart later.");
                    }else if (!resStr.equals("")){
                        JSONObject resJSON=new JSONObject(resStr);
                        String wordRes=resJSON.getString("word");
                        String operation=resJSON.getString("operation");
                        String notes=resJSON.getString("notes");
                        String time=resJSON.getString("time");
                        int status=resJSON.getInt("status");
                        if (status==0){
                            showMessageDialog(null, "Sorry. The word is not in my dictionary.");
                        }else {
                            textField2.setText(notes);
                        }
                    }
                }
            }
        });
        /**
         * action listener of adding button
         */
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String word=textField1.getText().trim();
                String notes=textField2.getText();
                if (word.equals("")||!wordJudge(word)){
                    showMessageDialog(null, "Please input a valid word.");
                }else if(!notesJudge(notes)){
                    showMessageDialog(null, "Please input a valid definition.");
                }else{
                    JSONObject argsJSON = new JSONObject();
                    argsJSON.put("word",word.toUpperCase());
                    argsJSON.put("operation","wordAdd");
                    argsJSON.put("notes",notes);
                    String timeStamp = new SimpleDateFormat("yyyyMMdd_HH:mm:ss").format(Calendar.getInstance().getTime());
                    argsJSON.put("time",timeStamp);
                    String argsStr=argsJSON.toString();
                    String resStr="";
                    try {
                        resStr=client.ioStream(argsStr);
                    }catch (Exception ex){
                        showMessageDialog(null,"Sorry. MyDictionary server is traveling outside:)\nPlease restart later.");
                    }
                    if (resStr.equals("deny")){
                        showMessageDialog(null,"Sorry. MyDictionary server is traveling outside:)\nPlease restart later.");
                    }else if (!resStr.equals("")){
                        JSONObject resJSON=new JSONObject(resStr);
                        String wordRes=resJSON.getString("word");
                        String operation=resJSON.getString("operation");
                        String time=resJSON.getString("time");
                        int status=resJSON.getInt("status");
                        if (status==0){
                            showMessageDialog(null, "Sorry, failed to add the word");
                        }else if (status==1){
                            showMessageDialog(null, "Good! Add the word successfully.");
                        }else if (status==2){
                            showMessageDialog(null, "The word has already been in my dictionary. And you can not edit its definition.");
                        }
                    }
                }
            }
        });
        /**
         * action listener of removing button
         */
        removeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String word=textField1.getText().trim();
                if (word.equals("")||!wordJudge(word)){
                    showMessageDialog(null, "Please input a valid word.");
                }else {
                    JSONObject argsJSON = new JSONObject();
                    argsJSON.put("word",word.toUpperCase());
                    argsJSON.put("operation","wordRemove");
                    argsJSON.put("notes","");
                    String timeStamp = new SimpleDateFormat("yyyyMMdd_HH:mm:ss").format(Calendar.getInstance().getTime());
                    argsJSON.put("time",timeStamp);
                    String argsStr=argsJSON.toString();
                    String resStr="";
                    try {
                        resStr=client.ioStream(argsStr);
                    }catch (Exception ex){
                        showMessageDialog(null,"Sorry. MyDictionary server is traveling outside:)\nPlease restart later.");
                    }
                    if (resStr.equals("deny")){
                        showMessageDialog(null,"Sorry. MyDictionary server is traveling outside:)\nPlease restart later.");
                    }else if (!resStr.equals("")){
                        JSONObject resJSON=new JSONObject(resStr);
                        String wordRes=resJSON.getString("word");
                        String operation=resJSON.getString("operation");
                        String notes=resJSON.getString("notes");
                        String time=resJSON.getString("time");
                        int status=resJSON.getInt("status");
                        if (status==1){
                            showMessageDialog(null, "Sorry. Failed to remove the word.");
                        }else if (status==0){
                            showMessageDialog(null, "Good! Remove the word successfully.");
                        }else if (status==2){
                            showMessageDialog(null, "Sorry. The word is not in my dictionary. So you can not remove it.");
                        }
                    }
                }
            }
        });

    }

    /**
     * main entrance of client
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException{
        String host = args[0];
        int port = Integer.parseInt(args[1]);
        JFrame frame = new JFrame("MyDictionary");
        SwingGUI objGUI=new SwingGUI(host,port);
        frame.setLocation(500,200);
        frame.setPreferredSize(new Dimension(400,300));
        frame.setContentPane(objGUI.panel1);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter(){
            public void windowClosing(WindowEvent e){
                try{
                    objGUI.client.clientClose();
                }catch (Exception ex){
                    System.exit(1);
                }
            }
        });
        frame.pack();
        frame.setVisible(true);
    }

    /**
     * judge whether a string matches the pattern of a word or not
     * @param word
     * @return
     */
    public boolean wordJudge(String word) {
        Pattern p = Pattern.compile("[a-zA-Z]+");
        Matcher m = p.matcher(word);
        return m.matches();
    }

    /**
     * judge whether a string matches the pattern of a definiton or not
     * @param notes
     * @return
     */
    public boolean notesJudge(String notes){
        Pattern p = Pattern.compile(".*[a-zA-Z]+.*");
        Matcher m = p.matcher(notes);
        return m.matches();
    }
}

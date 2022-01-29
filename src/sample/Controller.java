package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.stage.FileChooser;

import java.io.*;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.ResourceBundle;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

public class Controller implements Initializable {

    public String ident ="([a-zA-Z])+(_?([a-zA-Z0-9])+)*";
    public String integer ="[0-9]+" ;
    public String reel ="[0-9]+.[0-9]+" ;
    public String comment ="\\w";
    public String message ="\"([a-zA-Z])+('?([a-zA-Z0-9])+)*\"";

    @FXML
    private TextArea textArea;

    @FXML
    private TextArea analyzeTextArea;

    FileChooser fileChooser = new FileChooser();
    File file ;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("snail files", "*.snl"));
        String userHome = System.getProperty("user.home");
        fileChooser.setInitialDirectory(new File(userHome));
    }
    @FXML
    public void chooseFileOnClick(ActionEvent event) throws IOException {
        file = fileChooser.showOpenDialog(null);
        textArea.clear();
        if(file != null){
            Files.lines(file.toPath(), Charset.forName("UTF-8"))
                    .forEach(line -> textArea.appendText(line.concat("\n")));
        }
        textArea.setEditable(true);
        }

    @FXML
    void saveOnClick(ActionEvent event) throws IOException {
        FileWriter fw = new FileWriter(file);
        fw.write(textArea.getText());
        fw.close();
    }
    HashMap<String,String> hash = new HashMap<>();
    String [] lines ;

    @FXML
    void lexicalAnalyzerOnClick(ActionEvent event) {
        lines = textArea.getText().split("\n");
        for (int i=0;i<lines.length;i++) {
            StringTokenizer tokenizer = new StringTokenizer(lines[i],"\s");
            while (tokenizer.hasMoreTokens()){
                String token = tokenizer.nextToken();
                if (token.contains(",")){
                   String[] idents= token.split(",");
                    for (int k = 0; k < idents.length; k++) {
                        if (Pattern.matches(ident,idents[k])){
                            hash.put(idents[k],"identifier");}
                        else {
                            hash.put(idents[k],"Error, cannot resolve symbol");
                        }
                    }
                }else if (Pattern.matches("Snl_Start",token)){
                    hash.put(token,"reserved word for starting program");}
                else if (Pattern.matches("Snl_Int",token)){
                    hash.put(token,"reserved word for declaring an int");}
                else if (Pattern.matches("%.",token)){
                    hash.put(token,"reserved word for end of line");}
                else if (Pattern.matches("Set",token)){
                    hash.put(token,"reserved word for assigning a value");}
                else if (Pattern.matches("Snl_Real",token)){
                    hash.put(token,"reserved word for declaring a reel");}
                else if (Pattern.matches("if",token)){
                    hash.put(token,"reserved word for condition");}
                else if (Pattern.matches("%",token)){
                    hash.put(token,"reserved word for start and end of condition");}
                else if (Pattern.matches("Else",token)){
                    hash.put(token,"reserved word for condition");}
                else if (Pattern.matches("Start",token)){
                    hash.put(token,"reserved word for starting a condition code");}
                else if (Pattern.matches("Get",token)){
                    hash.put(token,"reserved word for getting a value");}
                else if (Pattern.matches("Finish",token)){
                    hash.put(token,"reserved word for ending a condition code");}
                else if (Pattern.matches("Snl_Put",token)){
                    hash.put(token,"reserved word for showing message");}
                else if (Pattern.matches("\"",token)){
                    hash.put(token,"reserved word for starting and ending message");}
                else if (Pattern.matches("Snl_St",token)){
                    hash.put(token,"reserved word for declaring a string");}
                else if (Pattern.matches("%..",token)){
                    hash.put(token,"reserved word for starting a comment");}
                else if (Pattern.matches("Snl_Close",token)){
                    hash.put(token,"reserved word for ending program");}
                else if (Pattern.matches("<",token)){
                    hash.put(token,"reserved word for less then");}
                else if (Pattern.matches(">",token)){
                    hash.put(token,"reserved word for greater then");}
                else if (Pattern.matches("=",token)){
                    hash.put(token,"reserved word for equal to");}
                else if (Pattern.matches("&",token)){
                    hash.put(token,"reserved word for AND");}
                else if (Pattern.matches("\\|",token)){
                    hash.put(token,"reserved word for OR");}
                else if (Pattern.matches(ident,token)){
                    hash.put(token,"identifier");}
                else if (Pattern.matches(integer,token)){
                    hash.put(token,"integer");}
                else if (Pattern.matches(reel,token)){
                    hash.put(token,"reel");}
                else if (Pattern.matches(comment,token)){
                    hash.put(token,"comment");}
                else if (Pattern.matches(message,token)){
                    hash.put(token,"message");}
                else {
                    hash.put(token,"ERROR, cannot resolve symbol");
                }
            }

        }
        analyzeTextArea.clear();
        for (String i : hash.keySet()) {
            analyzeTextArea.appendText(i + " : " + hash.get(i)+"\n");
        }
    }
    @FXML
    void syntacticAnalyzerOnClick(ActionEvent event) {
        HashMap<String,String> hashS =new HashMap<>();
        analyzeTextArea.clear();
        if (hash.containsValue("ERROR, cannot resolve symbol")){
            analyzeTextArea.setText("There is a Lexical error you can't do a syntactic analyze");
        }else{
           if (!lines[0].equals("Snl_Start")){
               hashS.put("Snl_Start missing","at line 1");
           }
           if (!lines[lines.length - 1].equals("Snl_Close")){
               hashS.put("Snl_Close missing","at line "+ (lines.length+1));
           }
           for (int y=1;y<lines.length-1;y++){
               if (lines[y].equals("Snl_Start")){
                   hashS.put("Snl_Start in wrong place ","at line "+(y+1));
               }else
               if (lines[y].equals("Snl_Close")){
                   hashS.put("Snl_Close in wrong place ","at line "+(y+1));
               }
            }




        }
        analyzeTextArea.clear();
        for (String i : hashS.keySet()) {
            analyzeTextArea.appendText(i + " : " + hashS.get(i)+"\n");
        }
    }

}

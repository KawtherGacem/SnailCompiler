package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.text.TextFlow;
import javafx.stage.FileChooser;

import java.io.*;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.spi.FileTypeDetector;
import java.util.HashMap;
import java.util.ResourceBundle;
import java.util.Scanner;
import java.util.regex.Pattern;

public class Controller implements Initializable {

    public String ident ="([a-zA-Z])+(_?([a-zA-Z0-9])+)*";
    public String integer ="[0-9]+" ;
    public String reel ="[0-9]+.[0-9]+" ;
    public String comment ="\\w";
    public String message ="\"([a-zA-Z])+('?([a-zA-Z0-9])+)*\"";

    @FXML
    private Button chooseFileBtn;

    @FXML
    private TextArea textArea;

    @FXML
    private TextArea lexicalTextArea;

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


    @FXML
    void lexicalAnalyzerOnClick(ActionEvent event) {
        HashMap<String,String> hash = new HashMap<>();
        String [] lines = textArea.getText().split("\n");
        for (int i=0;i<lines.length;i++) {
            String[] words = lines[i].split("\s");
            for (int j = 0; j < words.length; j++) {
                if (words[j].contains(",")){
                   String[] idents= words[j].split(",");
                    for (int k = 0; k < idents.length; k++) {
                        if (Pattern.matches(ident,idents[k])){
                            hash.put(idents[k],"identifier");}
                        else {
                            hash.put(idents[k],"Error, cannot resolve symbol");
                        }
                    }
                }else if (Pattern.matches("Snl_Start",words[j])){
                    hash.put(words[j],"reserved word for starting program");}
                else if (Pattern.matches("Snl_Int",words[j])){
                    hash.put(words[j],"reserved word for declaring an int");}
                else if (Pattern.matches("%.",words[j])){
                    hash.put(words[j],"reserved word for end of line");}
                else if (Pattern.matches("Set",words[j])){
                    hash.put(words[j],"reserved word for assigning a value");}
                else if (Pattern.matches("Snl_Real",words[j])){
                    hash.put(words[j],"reserved word for declaring a reel");}
                else if (Pattern.matches("if",words[j])){
                    hash.put(words[j],"reserved word for condition");}
                else if (Pattern.matches("%",words[j])){
                    hash.put(words[j],"reserved word for start and end of condition");}
                else if (Pattern.matches("Else",words[j])){
                    hash.put(words[j],"reserved word for condition");}
                else if (Pattern.matches("Start",words[j])){
                    hash.put(words[j],"reserved word for starting a condition code");}
                else if (Pattern.matches("Get",words[j])){
                    hash.put(words[j],"reserved word for getting a value");}
                else if (Pattern.matches("Finish",words[j])){
                    hash.put(words[j],"reserved word for ending a condition code");}
                else if (Pattern.matches("Snl_Put",words[j])){
                    hash.put(words[j],"reserved word for showing message");}
                else if (Pattern.matches("\"",words[j])){
                    hash.put(words[j],"reserved word for starting and ending message");}
                else if (Pattern.matches("Snl_St",words[j])){
                    hash.put(words[j],"reserved word for declaring a string");}
                else if (Pattern.matches("%..",words[j])){
                    hash.put(words[j],"reserved word for starting a comment");}
                else if (Pattern.matches("Snl_close",words[j])){
                    hash.put(words[j],"reserved word for ending program");}
                else if (Pattern.matches("<",words[j])){
                    hash.put(words[j],"reserved word for less then");}
                else if (Pattern.matches(">",words[j])){
                    hash.put(words[j],"reserved word for greater then");}
                else if (Pattern.matches("=",words[j])){
                    hash.put(words[j],"reserved word for equal to");}
                else if (Pattern.matches("&",words[j])){
                    hash.put(words[j],"reserved word for AND");}
                else if (Pattern.matches("\\|",words[j])){
                    hash.put(words[j],"reserved word for OR");}
                else if (Pattern.matches(ident,words[j])){
                    hash.put(words[j],"identifier");}
                else if (Pattern.matches(integer,words[j])){
                    hash.put(words[j],"integer");}
                else if (Pattern.matches(reel,words[j])){
                    hash.put(words[j],"reel");}
                else if (Pattern.matches(comment,words[j])){
                    hash.put(words[j],"comment");}
                else if (Pattern.matches(message,words[j])){
                    hash.put(words[j],"message");}
                else {
                    hash.put(words[j],"ERROR, cannot resolve symbol");
                }
            }
        }
        lexicalTextArea.clear();
        for (String i : hash.keySet()) {
            lexicalTextArea.appendText(i + " : " + hash.get(i)+"\n");
        }
    }


}

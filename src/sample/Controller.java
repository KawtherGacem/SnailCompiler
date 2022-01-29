package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import javafx.stage.FileChooser;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;
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
            Files.lines(file.toPath(), StandardCharsets.UTF_8)
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
        hash.clear();
        lines = textArea.getText().split("\n");
        for (String line : lines) {
            StringTokenizer tokenizer = new StringTokenizer(line, "\s");
            while (tokenizer.hasMoreTokens()) {
                String token = tokenizer.nextToken();
                if (token.contains(",") & !token.contains("\"")) {
                    String[] idents = token.split(",");
                    for (String s : idents) {
                        if (Pattern.matches(ident, s)) {
                            hash.put(s, "identifier");
                        } else {
                            hash.put(s, "Error, cannot resolve symbol");
                        }
                    }
                } else if (Pattern.matches("Snl_Start", token)) {
                    hash.put(token, "reserved word for starting program");
                } else if (Pattern.matches("Snl_Int", token)) {
                    hash.put(token, "reserved word for declaring an int");
                } else if (Pattern.matches("%.", token)) {
                    hash.put(token, "reserved word for end of line");
                } else if (Pattern.matches("Set", token)) {
                    hash.put(token, "reserved word for assigning a value");
                } else if (Pattern.matches("Snl_Real", token)) {
                    hash.put(token, "reserved word for declaring a reel");
                } else if (Pattern.matches("if", token)) {
                    hash.put(token, "reserved word for condition");
                } else if (Pattern.matches("%", token)) {
                    hash.put(token, "reserved word for start and end of condition");
                } else if (Pattern.matches("Else", token)) {
                    hash.put(token, "reserved word for condition");
                } else if (Pattern.matches("Start", token)) {
                    hash.put(token, "reserved word for starting a condition code");
                } else if (Pattern.matches("Get", token)) {
                    hash.put(token, "reserved word for getting a value");
                } else if (Pattern.matches("Finish", token)) {
                    hash.put(token, "reserved word for ending a condition code");
                } else if (Pattern.matches("Snl_Put", token)) {
                    hash.put(token, "reserved word for showing message");
                } else if (Pattern.matches("\"", token)) {
                    hash.put(token, "reserved word for starting and ending message");
                } else if (Pattern.matches("Snl_St", token)) {
                    hash.put(token, "reserved word for declaring a string");
                } else if (Pattern.matches("%..", token)) {
                    hash.put(token, "reserved word for starting a comment");
                } else if (Pattern.matches("Snl_Close", token)) {
                    hash.put(token, "reserved word for ending program");
                } else if (Pattern.matches("<", token)) {
                    hash.put(token, "reserved word for less then");
                } else if (Pattern.matches(">", token)) {
                    hash.put(token, "reserved word for greater then");
                } else if (Pattern.matches("=", token)) {
                    hash.put(token, "reserved word for equal to");
                } else if (Pattern.matches("&", token)) {
                    hash.put(token, "reserved word for AND");
                } else if (Pattern.matches("\\|", token)) {
                    hash.put(token, "reserved word for OR");
                } else if (Pattern.matches(ident, token)) {
                    hash.put(token, "identifier");
                } else if (Pattern.matches(integer, token)) {
                    hash.put(token, "integer");
                } else if (Pattern.matches(reel, token)) {
                    hash.put(token, "reel");
                } else if (Pattern.matches(comment, token)) {
                    hash.put(token, "comment");
                } else if (Pattern.matches(message, token)) {
                    hash.put(token, "message");
                } else {
                    hash.put(token, "ERROR, cannot resolve symbol");
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
           int x=1;
           while ( x<lines.length-1){
                StringTokenizer tokenizer = new StringTokenizer(lines[x],"\s");
                List<String> tokens = new ArrayList<>();
                while (tokenizer.hasMoreTokens()){
                   tokens.add(tokenizer.nextToken());}
                switch (tokens.get(0)){
                   case "Snl_Int":
                       case "Snl_Real":
                       if (tokens.size()<2 | tokens.size()>3 ) {
                           hashS.put("incorrect statement ", "at line " + (x + 1));
                       }else {
                           if (!tokens.get(1).contains(",")) {
                               if (!hash.get(tokens.get(1)).equals("identifier")) {
                                   hashS.put("expected identifier ", "at line " + (x + 1));
                               }
                           }
                           if (!tokens.get(tokens.size() - 1).equals("%.")) {
                               hashS.put("expected %. at end of line ", "at line " + (x + 1));
                           }
                       }
                       break;
                    case "Snl_St":
                        if (tokens.size()<2 | tokens.size()>3 ) {
                            hashS.put("incorrect statement ", "at line " + (x + 1));
                        }else {
                            if (!hash.get(tokens.get(1)).equals("message")) {
                                    hashS.put("expected string ", "at line " + (x + 1));
                            }
                            if (!tokens.get(tokens.size() - 1).equals("%.")) {
                                hashS.put("expected %. at end of line ", "at line " + (x + 1));
                            }
                        }
                        break;


                }
                x++;
           }



        }
        analyzeTextArea.clear();
        if (hashS.isEmpty()){
            analyzeTextArea.setText("There are no detected syntactic errors");
        }
        for (String i : hashS.keySet()) {
            analyzeTextArea.appendText(i + ": " + hashS.get(i)+"\n");
        }
    }

}

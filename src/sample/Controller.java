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
    public String string ="\"([a-zA-Z])+('?([a-zA-Z0-9])+)*\"";

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
                } else if (Pattern.matches("from", token)) {
                    hash.put(token, "reserved word for getting a value");
                } else if (Pattern.matches("Finish", token)) {
                    hash.put(token, "reserved word for ending a condition code");
                } else if (Pattern.matches("Snl_Put", token)) {
                    hash.put(token, "reserved word for showing string");
                } else if (Pattern.matches("\"", token)) {
                    hash.put(token, "reserved word for starting and ending string");
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
                } else if (Pattern.matches(string, token)) {
                    hash.put(token, "string");
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
    HashMap<String,String> hashtype = new HashMap<>();

    @FXML
    void syntacticAnalyzerOnClick(ActionEvent event) {
        ArrayList<String> errors = new ArrayList<>();
        analyzeTextArea.clear();
        if (hash.containsValue("ERROR, cannot resolve symbol")){
            analyzeTextArea.setText("There is a Lexical error you can't do a syntactic analyze");
        }else{
           if (!lines[0].equals("Snl_Start")){
               errors.add("Snl_Start missingat line 1" );
           }
           if (!lines[lines.length - 1].equals("Snl_Close")){
               errors.add("Snl_Close missingat line "+ (lines.length+1));
           }
           for (int y=1;y<lines.length-1;y++){
               if (lines[y].equals("Snl_Start")){
                   errors.add("Snl_Start in wrong place at line "+(y+1));
               }else
               if (lines[y].equals("Snl_Close")){
                   errors.add("Snl_Close in wrong place at line "+(y+1));
               }
            }
           int x=1;


           // lines rah fiha ga3 lignes ta3 le code

//           dert while bach nparcouri lines w tokenizer bach n9asem lines l words
           while ( x<lines.length){
                StringTokenizer tokenizer = new StringTokenizer(lines[x],"\s");
                List<String> tokens = new ArrayList<>();
                while (tokenizer.hasMoreTokens()){
                   tokens.add(tokenizer.nextToken());
                    }
//                tokens fiha lwords ta3 la ligne li rana fiha nchoufou word par word la raha syntaxiquement nichan
//               nchoufou l word lewla hiya 0 3la 7sabha na3arfou la ligne cha normalement yji fiha

                switch (tokens.get(0)){
                   case "Snl_Int":
                       case "Snl_Real":
                       if (tokens.size()<2 | tokens.size()>3 ) {
                           errors.add("incorrect statement at line " + (x + 1));
                       }else {
                           if (!tokens.get(1).contains(",")) { // hna za3ma la kan identifiers mafsoulin b ","
                               if (!hash.get(tokens.get(1)).equals("identifier")) {
                                   errors.add("expected identifier at line " + (x + 1));
                               }
                           }
                           if (!tokens.get(tokens.size() - 1).equals("%.")) {
                               errors.add("expected %. at end of statement at line " + (x + 1));
                           }
                           hashtype.put(tokens.get(1),tokens.get(0));
                       }
                       break;
                    case "Snl_Put":
                        if (tokens.size()<2 | tokens.size()>3 ) {
                            errors.add("incorrect statement at line " + (x + 1));
                        }else {
                            if (!tokens.get(1).contains(",")) { // hna za3ma la kan identifiers mafsoulin b ","
                                if (!hash.get(tokens.get(1)).equals("identifier")) {
                                    errors.add("expected identifier at line " + (x + 1));
                                }
                            }
                            if (!tokens.get(tokens.size() - 1).equals("%.")) {
                                errors.add("expected %. at end of statement at line " + (x + 1));
                            }
                            break;
                        }
                       case "Snl_St":

                           if (tokens.size()<2 | tokens.size()>3 ) {
                               errors.add("incorrect statement at line " + (x + 1));
                        }else {
                            if (!hash.get(tokens.get(1)).equals("string")) {
                                if (!hash.get(tokens.get(1)).equals("identifier")) {
                                    errors.add("expected string at line " + (x + 1));
                                }
                            }
                            if (!tokens.get(tokens.size() - 1).equals("%.")) {
                                errors.add("expected %. at end of statement at line " + (x + 1));
                            }
                        }
                           hashtype.put(tokens.get(1),tokens.get(0));

                           break;
                    case "Set":
                        if (tokens.size()<3 | tokens.size()>4 ) {
                            errors.add("incorrect statement at line " + (x + 1));
                        }else {
                            if (!hash.get(tokens.get(1)).equals("identifier")) {
                                errors.add("expected identifier at line " + (x + 1));
                            }
                            if (!hash.get(tokens.get(2)).equals("integer")&!hash.get(tokens.get(2)).equals("reel")&!hash.get(tokens.get(2)).equals("string")) {
                                errors.add("expected value at line " + (x + 1));
                            }
                            if (!tokens.get(tokens.size() - 1).equals("%.")) {
                                errors.add("expected %. at end of statement at line " + (x + 1));
                            }
                        }
                        break;
                    case "Get":
                        if (tokens.size()<4 | tokens.size()>5 ) {
                            errors.add("incorrect statement at line " + (x + 1));
                        }else {
                            if (!hash.get(tokens.get(1)).equals("identifier")) {
                                errors.add("expected first identifier at line " + (x + 1));
                            }
                            if (!tokens.get(2).equals("from")) {
                                errors.add("expected from at line " + (x + 1));
                            }
                            if (!hash.get(tokens.get(3)).equals("identifier")) {
                                errors.add("expected second identifier at line " + (x + 1));
                            }
                            if (!tokens.get(tokens.size() - 1).equals("%.")) {
                                errors.add("expected %. at end of statement at line " + (x + 1));
                            }
                        }
                        break;
                    case "If":

                        break;
                }
                x++;
           }



        }
        analyzeTextArea.clear();
        if (!hash.containsValue("ERROR, cannot resolve symbol")){
        if (errors.isEmpty()){
            analyzeTextArea.setText("There are no detected syntactic errors");
        }
        for (String i : errors) {
            analyzeTextArea.appendText(i+"\n");
        }}
    }

    @FXML
    void symanticAnalyzerOnClick(ActionEvent event) {
        for (String i : hashtype.keySet()){
            System.out.println(i+ " "+hashtype.get(i));
        }
        //       fel semantique tdiri kima dert besah f les cas ta3 switch tdiri ghi set get w if w snl_put w diri code bach tverifyi
//         exemple :   set i 34 %. lazem ykoun i declari deja w ykoun integer
//            hadou ta3arfihoum mel hash fiha koul identifier type ta3ah
    }


}

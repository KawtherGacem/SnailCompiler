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
                } else if (Pattern.matches("If", token)) {
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
                } else if (Pattern.matches("Snl_St", token)) {
                    hash.put(token, "reserved word for declaring a string");
                } else if (Pattern.matches("%..", token)) {
                    hash.put(token, "reserved word for starting a comment");
                } else if (Pattern.matches("Snl_Close", token)) {
                    hash.put(token, "reserved word for ending program");
                } else if (Pattern.matches("do", token)) {
                    hash.put(token, "reserved word for If statement");
                } else if (Pattern.matches("<", token)) {
                    hash.put(token, "less then");
                } else if (Pattern.matches(">", token)) {
                    hash.put(token, "greater then");
                } else if (Pattern.matches("=", token)) {
                    hash.put(token, "equal to");
                } else if (Pattern.matches("&", token)) {
                    hash.put(token, "AND");
                } else if (Pattern.matches("\\|", token)) {
                    hash.put(token, "OR");
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
    ArrayList<String> errors = new ArrayList<>();
    ArrayList<String> identErrors = new ArrayList<>();


    @FXML
    void syntacticAnalyzerOnClick(ActionEvent event) {
        errors.clear();
        identErrors.clear();
        hashtype.clear();
        analyzeTextArea.clear();
        if (hash.containsValue("ERROR, cannot resolve symbol")) {
            analyzeTextArea.setText("There is a Lexical error you can't do a syntactic analyze");
        } else {
            if (!lines[0].equals("Snl_Start")) {
                errors.add("Snl_Start missingat line 1");
            }
            if (!lines[lines.length - 1].equals("Snl_Close")) {
                errors.add("Snl_Close missingat line " + (lines.length + 1));
            }
            for (int y = 1; y < lines.length - 1; y++) {
                if (lines[y].equals("Snl_Start")) {
                    errors.add("Snl_Start in wrong place at line " + (y + 1));
                } else if (lines[y].equals("Snl_Close")) {
                    errors.add("Snl_Close in wrong place at line " + (y + 1));
                }
            }
            int x = 1;


            while (x < lines.length) {
                StringTokenizer tokenizer = new StringTokenizer(lines[x], "\s");
                List<String> tokens = new ArrayList<>();
                while (tokenizer.hasMoreTokens()) {
                    tokens.add(tokenizer.nextToken());
                }

                switch (tokens.get(0)) {
                    case "Snl_Int":
                    case "Snl_Real":
                        if (tokens.size() < 3) {
                            errors.add("incompleted statement at line " + (x + 1));
                        }
                        if (!tokens.get(1).contains(",")) {
                            if (tokens.size() < 2 || (!hash.get(tokens.get(1)).equals("identifier"))) {
                                errors.add("expected identifier at line " + (x + 1));
                            }
                        }


                        if (!tokens.get(tokens.size() - 1).equals("%.")) {
                                errors.add("expected %. at end of statement at line " + (x + 1));
                        }
//                        storing declared variables and errors about identifier names that are already token

                        if (!tokens.get(1).contains(",")&!hashtype.containsKey(tokens.get(1))){
                        hashtype.put(tokens.get(1), tokens.get(0));
                        } else if (tokens.get(1).contains(",")){
                            StringTokenizer tokenizer1 = new StringTokenizer(tokens.get(1), ",");
                            while (tokenizer1.hasMoreTokens()) {
                                String t = tokenizer1.nextToken();
                                if (!hashtype.containsKey(t)){
                                hashtype.put(t,tokens.get(0));
                                  } else
                                    identErrors.add("identifier "+t+ " already taken, rename it! at line "+ (x + 1));
                            }
                        }else if (hashtype.containsKey(tokens.get(1))){
                            identErrors.add("identifier "+tokens.get(1)+ " already taken, rename it! at line "+ (x + 1));
                        }




                        break;
                    case "Snl_St":
                        if (tokens.size() < 3) {
                            errors.add("incompleted statement at line " + (x + 1));
                        }
                        if (!tokens.get(1).contains(",")) {
                            if (tokens.size() < 2 || (!hash.get(tokens.get(1)).equals("identifier"))) {
                                errors.add("expected identifier at line " + (x + 1));
                            }
                        }

                        if (tokens.size() < 3 || !tokens.get(tokens.size() - 1).equals("%.")) {
                                errors.add("expected %. at end of statement at line " + (x + 1));
                        }
//                        storing declared variables and errors about identifier names that are already token
                        if (!tokens.get(1).contains(",")&!hashtype.containsKey(tokens.get(1))){
                            hashtype.put(tokens.get(1), tokens.get(0));
                        } else if (tokens.get(1).contains(",")){
                            StringTokenizer tokenizer1 = new StringTokenizer(tokens.get(1), ",");
                            while (tokenizer1.hasMoreTokens()) {
                                String t = tokenizer1.nextToken();
                                if (!hashtype.containsKey(t)){
                                    hashtype.put(t,tokens.get(0));
                                } else
                                    identErrors.add("identifier "+t+ " already taken, rename it! at line"+ (x + 1));
                            }
                        }else if (hashtype.containsKey(tokens.get(1))){
                            identErrors.add("identifier "+tokens.get(1)+ " already taken, rename it! at line "+ (x + 1));
                        }
                    break;

                    case "Snl_Put":

                        if (tokens.size() < 3) {
                            errors.add("incompleted statement at line " + (x + 1));
                        }
                            if (tokens.size() < 2 || (!hash.get(tokens.get(1)).equals("string") & !hash.get(tokens.get(1)).equals("identifier"))) {
                                    errors.add("expected string at line " + (x + 1));
                                }

                            if (tokens.size() < 3 || !tokens.get(tokens.size() - 1).equals("%.")) {
                                errors.add("expected %. at end of statement at line " + (x + 1));
                            }


                        break;
                    case "Set":
                        if (tokens.size() < 4) {
                            errors.add("incompleted statement at line " + (x + 1));
                        }
                            if (tokens.size() < 2 || !hash.get(tokens.get(1)).equals("identifier")) {
                                errors.add("expected identifier at line " + (x + 1));
                            }
                            if (tokens.size() < 3 || (!hash.get(tokens.get(2)).equals("integer") & !hash.get(tokens.get(2)).equals("reel") & !hash.get(tokens.get(2)).equals("string"))) {
                                errors.add("expected value at line " + (x + 1));
                            }
                            if (tokens.size() < 4 || !tokens.get(tokens.size() - 1).equals("%.")) {
                                errors.add("expected %. at end of statement at line " + (x + 1));
                            }

                        break;
                    case "Get":
                        if (tokens.size() < 5) {
                            errors.add("incompleted statement at line " + (x + 1));
                          }
                            if (tokens.size() < 2 || !hash.get(tokens.get(1)).equals("identifier")) {
                                errors.add("expected first identifier at line " + (x + 1));
                            }
                            if (tokens.size() < 3 || !tokens.get(2).equals("from")) {
                                errors.add("expected from at line " + (x + 1));
                            }
                            if (tokens.size() < 4 || !hash.get(tokens.get(3)).equals("identifier")) {
                                errors.add("expected second identifier at line " + (x + 1));
                            }
                            if (tokens.size() < 5 || !tokens.get(tokens.size() - 1).equals("%.")) {
                                errors.add("expected %. at end of statement at line " + (x + 1));
                            }

                        break;
                    case "If":
                        if (tokens.size() < 7) {
                            errors.add("incompleted statement at line " + (x + 1));
                        }
                        if (tokens.size() < 2 || !tokens.get(1).equals("%")) {
                            errors.add("expected opening % at line " + (x + 1));
                        }
                        if (tokens.size() < 6 || !tokens.get(5).equals("%")) {
                            errors.add("expected closing % at line " + (x + 1));
                        }
                        if (tokens.size() < 3|| (!hash.get(tokens.get(2)).equals("integer") & !hash.get(tokens.get(2)).equals("reel") & !hash.get(tokens.get(2)).equals("string") & !hash.get(tokens.get(2)).equals("identifier"))) {
                                errors.add("expected first part condition at line " + (x + 1));
                        }
                        if (tokens.size() < 5 || (!hash.get(tokens.get(4)).equals("integer") & !hash.get(tokens.get(4)).equals("reel") & !hash.get(tokens.get(4)).equals("string") & !hash.get(tokens.get(4)).equals("identifier"))) {
                                errors.add("expected second part in condition values at line " + (x + 1));
                        }
                        if (tokens.size() < 4 || (!hash.get(tokens.get(3)).equals("less then") & !hash.get(tokens.get(3)).equals("greater then") & !hash.get(tokens.get(3)).equals("equal to") & !hash.get(tokens.get(3)).equals("AND") & !hash.get(tokens.get(3)).equals("OR"))) {
                                errors.add("expected operator in condition values at line " + (x + 1));
                        }
                        if (!tokens.get(tokens.size() - 1).equals("do")) {
                            errors.add("expected do at end of statement at line " + (x + 1));
                        }

//


                        break;
                    case "Else":
                        int y=x;
                        boolean start = false;
                        boolean finish = false;
                        while (y<lines.length) {
                            if (lines[y].contains("Start")){
                                start=true;
                            }
                            if (lines[y].contains("Finish")){
                                finish=true;
                            }
                            y++;
                        }
                        if (start != finish)
                            errors.add("missing start or finish after else");
                        break;
                    case "Snl_Close":
                    case "Start":
                    case "Finish":
                    case "%..":
                        break;
                    default:
                        errors.add("unrecognized line at line "+(x+1));

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
        else {
            analyzeTextArea.setText("There are lexical errors, correct them!");
        }
    }
    ArrayList<String> errors2 = new ArrayList<>();

    @FXML
    void symanticAnalyzerOnClick(ActionEvent event) {
    errors2.clear();
        for (String i : hashtype.keySet()){
            System.out.println(i+ " "+hashtype.get(i));
        }
        analyzeTextArea.clear();
        if (!errors.isEmpty()) {
            analyzeTextArea.setText("There is a syntaxic error you can't do a semantic analyze, correct them!!!");
        }
        int x=1;
        while (x < lines.length) {
            StringTokenizer tokenizer = new StringTokenizer(lines[x], "\s");
            List<String> tokens = new ArrayList<>();
            while (tokenizer.hasMoreTokens()) {
                tokens.add(tokenizer.nextToken());
            }
//

            switch (tokens.get(0)) {
                case "Set":
                    if (!hashtype.containsKey(tokens.get(1))){
                        errors2.add("undeclared variable at line " + (x + 1));
                    }else if (hashtype.get(tokens.get(1)).equals("Snl_Int") & !hash.get(tokens.get(2)).equals("integer")){
                        errors2.add("Type mismatch non Integer value at line " + (x + 1));
                    } else if (hashtype.get(tokens.get(1)).equals("Snl_Real") & !hash.get(tokens.get(2)).equals("reel")){
                     errors2.add("Type mismatch non Real value at line " + (x + 1));
                    } else if (hashtype.get(tokens.get(1)).equals("Snl_St") & !hash.get(tokens.get(2)).equals("string")) {
                        errors2.add("Type mismatch non string value at line " + (x + 1));
                    }
                    break;
                case "Get":
                    if (!hashtype.containsKey(tokens.get(1))|!hashtype.containsKey(tokens.get(3))){
                        errors2.add("undeclared variable at line " + (x + 1));
                    }else {
                        if (!hashtype.get(tokens.get(1)).equals(hashtype.get(tokens.get(3)))){
                            errors2.add("Type mismatch at line " + (x + 1));
                        }
                    }
                    break;
                case "If":
                    if (!hashtype.containsKey(tokens.get(2))|!hashtype.containsKey(tokens.get(4))){
                        errors2.add("undeclared variable at line " + (x + 1));
                    }else {
                        if (!hashtype.get(tokens.get(2)).equals(hashtype.get(tokens.get(4)))){
                            errors2.add("Type mismatch at line " + (x + 1));
                        }
                    }
                    break;
                case "Snl_Put":
                    if (!hashtype.containsKey(tokens.get(1))&!hash.get(tokens.get(1)).equals("string")){
                        errors2.add("undeclared variable at line " + (x + 1));
                    }
                    break;
            }
            x++;
        }


        analyzeTextArea.clear();
            if (errors2.isEmpty()&identErrors.isEmpty()){
                analyzeTextArea.setText("There are no detected symantic errors");
            } else {
                for (String i : errors2) {
                    analyzeTextArea.appendText(i+"\n");
                }
                for (String i : identErrors) {
                    analyzeTextArea.appendText(i+"\n");
                }
            }
    }


}

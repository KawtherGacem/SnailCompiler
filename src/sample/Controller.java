package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.text.TextFlow;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.spi.FileTypeDetector;
import java.util.HashMap;
import java.util.Scanner;

public class Controller {

    @FXML
    private Button chooseFileBtn;

    @FXML
    private TextArea textArea;

    @FXML
    private TextArea lexicalTextArea;

    FileChooser fileChooser = new FileChooser();

    @FXML
    public void chooseFileOnClick(ActionEvent event) throws IOException {
        File file = fileChooser.showOpenDialog(null);
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("snail files", "*.snl"));
        String userHome = System.getProperty("user.home");
        fileChooser.setInitialDirectory(new File(userHome));
        FileReader fileReader = new FileReader(file);
        textArea.clear();
        if(file != null){
            Files.lines(file.toPath(), Charset.forName("UTF-8"))
                    .forEach(line -> textArea.appendText(line.concat("\n")));
        }
        }

    @FXML
    void saveOnClick(ActionEvent event) {

    }


    @FXML
    void lexicalAnalyzerOnClick(ActionEvent event) {
        HashMap<String,String> hash = new HashMap<>();
        String [] lines = textArea.getText().split("\n");
        for (int i=0;i<lines.length;i++) {
            String[] words = lines[i].split("\s");
            for (int j = 0; j < words.length; j++) {
                System.out.println(words[j]);
            }
        }
    }

}

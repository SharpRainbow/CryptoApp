package ru.mirea.cryptoapp;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class AtbashFormController implements Initializable {

    @FXML
    private Button convertBtn;
    @FXML
    private TextArea inputData;
    @FXML
    private TextArea outputData;
    @FXML
    private Button clearBtn;
    private final static int RU_LOWCASE_START = 1072;
    private final static int EN_LOWCASE_START = 97;
    private static ArrayList<String> alphabets = new ArrayList<>();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        alphabets.add(en());
        alphabets.add(ru());
        convertBtn.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                outputData.setText(atbash(inputData.getText()));
            }
        });
        outputData.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                outputData.selectAll();
            }
        });
        clearBtn.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                inputData.clear();
                outputData.clear();
            }
        });
    }

    private static String ru(){
        StringBuilder alpha = new StringBuilder();
        for (int i = 0; i < 32; i++){
            alpha.append((char) (RU_LOWCASE_START + i));
        }
        alpha.insert(6, (char) 1105);
        return alpha.toString();
    }

    private static String en(){
        StringBuilder alpha = new StringBuilder();
        for (int i = 0; i < 26; i++){
            alpha.append((char) (EN_LOWCASE_START + i));
        }
        return alpha.toString();
    }

    private static String atbash(String string){
        StringBuilder result = new StringBuilder();
        for (char c : string.toCharArray()){
            for (int i = 0; i < alphabets.size(); i++){
                String a = alphabets.get(i);
                int charIdx = a.indexOf(Character.toLowerCase(c));
                if (charIdx != -1){
                    char resultChar = a.charAt(a.length() - charIdx - 1);
                    result.append(Character.isUpperCase(c) ? Character.toUpperCase(resultChar) : resultChar);
                    break;
                }
                else if (i == alphabets.size() - 1)
                    result.append(c);
            }
        }
        return result.toString();
    }

}

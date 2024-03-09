package ru.mirea.cryptoapp;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;

import java.net.URL;
import java.util.Random;
import java.util.ResourceBundle;

public class MagicSquareFormController implements Initializable {

    @FXML
    private ChoiceBox<String> mode;
    @FXML
    private CheckBox autoSize;
    @FXML
    private TextField squareSize;
    @FXML
    private TextArea inData;
    @FXML
    private TextArea outData;
    @FXML
    private Button btnTransform;
    @FXML
    private Button btnClear;
    @FXML
    private CheckMenuItem fillSquare;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ObservableList<String> modes = FXCollections.observableArrayList(
                "Зашифровать", "Расшифровать"
        );
        mode.setItems(modes);
        mode.setValue(modes.get(0));
        autoSize.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observableValue, Boolean aBoolean, Boolean t1) {
                if (!aBoolean)
                    squareSize.clear();
                squareSize.setEditable(aBoolean);
            }
        });
        squareSize.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue,
                                String newValue) {
                if (!newValue.matches("\\d*")) {
                    squareSize.setText(newValue.replaceAll("[^\\d]", ""));
                }
            }
        });
        outData.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                outData.selectAll();
            }
        });
        btnTransform.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                String input = inData.getText();
                int sqSize = (int) Math.ceil(Math.sqrt(input.length()));
                if (!autoSize.selectedProperty().get()){
                    sqSize = squareSize.getText().isEmpty() ? 0 : Integer.parseInt(squareSize.getText());
                    if (sqSize * sqSize < input.length()){
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Ошибка!");
                        alert.setHeaderText("Неверный порядок квадрата!");
                        alert.setContentText("Квадрат должен полностью вмещать входные данные!");
                        alert.showAndWait();
                        return;
                    }
                }
                if (sqSize < 3)
                    sqSize = 3;
                int[][] sq = generateMagicSquare(sqSize, 1);
                char[] output = new char[sqSize * sqSize];
                if (mode.getValue().equals(modes.get(0))) {
                    Random random = new Random();
                    for (int i = 0; i < sqSize; i++) {
                        for (int j = 0; j < sqSize; j++) {
                            if (sq[i][j] <= input.length())
                                output[sqSize * i + j] = input.charAt(sq[i][j] - 1);
                            else if (fillSquare.isSelected()) {
                                if (Character.UnicodeBlock.of(input.charAt(0)) == Character.UnicodeBlock.BASIC_LATIN)
                                    output[sqSize * i + j] = (char) (random.nextInt(26) + 'a');
                                else if (Character.UnicodeBlock.of(input.charAt(0)) == Character.UnicodeBlock.CYRILLIC)
                                    output[sqSize * i + j] = (char) (random.nextInt(32) + 'а');
                            }
                        }
                    }
                }
                else {
                    int idx = 0;
                    for (int i = 0; i < sqSize; i++) {
                        for (int j = 0; j < sqSize; j++) {
                            if (sq[i][j] <= input.length())
                                output[sq[i][j] - 1] = input.charAt(idx++);
                        }
                    }
                }
                outData.setText(new String(output));
            }
        });
        btnClear.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                inData.clear();
                outData.clear();
            }
        });
    }

    private int[][] generateMagicSquare(int n, int firstNum) {
        int[][] square = new int[n][n];
        if (n % 2 != 0) {
            int x = 0, y = n / 2;
            for (int i = firstNum; i <= n * n + firstNum - 1; i++) {
                square[x][y] = i;
                int newX = x - 1 >= 0 ? x - 1 : n - 1;
                int newY = y + 1 < n ? y + 1 : 0;
                if (square[newX][newY] != 0) {
                    x = x + 1;
                } else {
                    x = newX;
                    y = newY;
                }
            }
        } else if (n % 4 == 0) {
            for (int i = 0; i < n; i++)
                for (int j = 0; j < n; j++)
                    square[i][j] = (n * i) + j + firstNum;
            for (int i = 0; i < n / 4; i++)
                for (int j = 0; j < n / 4; j++)
                    square[i][j] = (n * n + 2 * firstNum - 1) - square[i][j];
            for (int i = 0; i < n / 4; i++)
                for (int j = 3 * (n / 4); j < n; j++)
                    square[i][j] = (n * n + 2 * firstNum - 1) - square[i][j];
            for (int i = 3 * n / 4; i < n; i++)
                for (int j = 0; j < n / 4; j++)
                    square[i][j] = (n * n + 2 * firstNum - 1) - square[i][j];
            for (int i = 3 * n / 4; i < n; i++)
                for (int j = 3 * n / 4; j < n; j++)
                    square[i][j] = (n * n + 2 * firstNum - 1) - square[i][j];
            for (int i = n / 4; i < 3 * n / 4; i++)
                for (int j = n / 4; j < 3 * n / 4; j++)
                    square[i][j] = (n * n + 2 * firstNum - 1) - square[i][j];
        } else {
            int n1 = n / 2;
            int[][] lt = generateMagicSquare(n1, firstNum);
            int[][] rt = generateMagicSquare(n1, (2 * n * n) / 4 + firstNum);
            int[][] lb = generateMagicSquare(n1, (3 * n * n) / 4 + firstNum);
            int[][] rb = generateMagicSquare(n1, (n * n) / 4 + firstNum);
            int k = (n - 2) / 4;
            for (int i = 0; i < n1; i++) {
                for (int j = 0; j < k; j++) {
                    int tmp = lt[i][j];
                    lt[i][j] = lb[i][j];
                    lb[i][j] = tmp;
                }
                for (int j = 0; j < k - 1; j++) {
                    int tmp = rt[i][j];
                    rt[i][j] = rb[i][j];
                    rb[i][j] = tmp;
                }
            }
            int tmp = lt[n1 / 2][0];
            lt[n1 / 2][0] = lb[n1 / 2][0];
            lb[n1 / 2][0] = tmp;
            tmp = lt[n1 / 2][n1 / 2];
            lt[n1 / 2][n1 / 2] = lb[n1 / 2][n1 / 2];
            lb[n1 / 2][n1 / 2] = tmp;
            for (int i = 0; i < lt.length; i++) {
                System.arraycopy(lt[i], 0, square[i], 0, lt[i].length);
                System.arraycopy(rt[i], 0, square[i], n1, lt[i].length);
                System.arraycopy(lb[i], 0, square[i + n1], 0, lb[i].length);
                System.arraycopy(rb[i], 0, square[i + n1], n1, lb[i].length);
            }
        }
        return square;
    }
}

package ru.mirea.cryptoapp;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MainScreenController implements Initializable {

    @FXML
    private Button transpCipherBtn;

    @FXML
    private Button replaceCipherBtn;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        replaceCipherBtn.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                FXMLLoader fxmlLoader = new FXMLLoader(CryptoApplication.class.getResource("atbash-form.fxml"));
                try {
                    Parent root = fxmlLoader.load();
                    Scene scene = new Scene(root);
                    Stage newWindow = new Stage();
                    newWindow.setTitle("Шифр Атбаш");
                    newWindow.setScene(scene);
                    newWindow.sizeToScene();
                    newWindow.show();
                    newWindow.setMinWidth(newWindow.getWidth());
                    newWindow.setMinHeight(newWindow.getHeight());
                    newWindow.setOnCloseRequest(new EventHandler<WindowEvent>() {
                        @Override
                        public void handle(WindowEvent windowEvent) {
                            Stage stage = new Stage();
                            FXMLLoader loader = new FXMLLoader(CryptoApplication.class.getResource("main-form.fxml"));
                            Scene scene = null;
                            try {
                                scene = new Scene(loader.load());
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                            stage.setTitle("Выберите шифр");
                            stage.setScene(scene);
                            stage.setResizable(false);
                            stage.show();
                        }
                    });
                    Stage parentStage = (Stage) replaceCipherBtn.getScene().getWindow();
                    parentStage.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        transpCipherBtn.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                FXMLLoader fxmlLoader = new FXMLLoader(CryptoApplication.class.getResource("magic-square-form.fxml"));
                try {
                    Parent root = fxmlLoader.load();
                    Scene scene = new Scene(root);
                    Stage newWindow = new Stage();
                    newWindow.setTitle("Шифр Магический Квадрат");
                    newWindow.setScene(scene);
                    newWindow.sizeToScene();
                    newWindow.show();
                    newWindow.setMinWidth(newWindow.getWidth());
                    newWindow.setMinHeight(newWindow.getHeight());
                    newWindow.setOnCloseRequest(new EventHandler<WindowEvent>() {
                        @Override
                        public void handle(WindowEvent windowEvent) {
                            Stage stage = new Stage();
                            FXMLLoader loader = new FXMLLoader(CryptoApplication.class.getResource("main-form.fxml"));
                            Scene scene = null;
                            try {
                                scene = new Scene(loader.load());
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                            stage.setTitle("Выберите шифр");
                            stage.setScene(scene);
                            stage.setResizable(false);
                            stage.show();
                        }
                    });
                    Stage parentStage = (Stage) transpCipherBtn.getScene().getWindow();
                    parentStage.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }
}

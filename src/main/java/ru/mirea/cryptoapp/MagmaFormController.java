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
import javafx.stage.FileChooser;

import java.io.*;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Objects;
import java.util.Random;
import java.util.ResourceBundle;

public class MagmaFormController implements Initializable {

    @FXML
    private ChoiceBox<String> cbModesText;
    @FXML
    private ChoiceBox<String> cbKeyTypeText;
    @FXML
    private ChoiceBox<String> cbModesFile;
    @FXML
    private ChoiceBox<String> cbKeyTypeFile;

    @FXML
    private TextField keyInText;
    @FXML
    private TextField keyInFile;
    @FXML
    private TextField inFile;
    @FXML
    private TextField outFile;

    @FXML
    private TextArea textIn;
    @FXML
    private TextArea textOut;

    @FXML
    private Button btnTransformText;
    @FXML
    private Button btnClearText;
    @FXML
    private Button btnTransformFile;
    @FXML
    private Button btnClearFile;
    @FXML
    private Button pickInFile;
    @FXML
    private Button pickOutFile;

    private final int BLOCK_SIZE = 8;//64-bit block
    private final byte[][] keys = new byte[32][4];
    private final String defaultKey = "ffeeddccbbaa99887766554433221100f0f1f2f3f4f5f6f7f8f9fafbfcfdfeff";

    private final byte[][] Pi = new byte[][]
            {
                    {1,7,14,13,0,5,8,3,4,15,10,6,9,12,11,2},
                    {8,14,2,5,6,9,1,12,15,4,11,0,13,10,3,7},
                    {5,13,15,6,9,2,12,10,11,7,8,1,4,3,14,0},
                    {7,15,5,10,8,1,6,13,0,9,3,14,11,4,2,12},
                    {12,8,2,1,13,4,15,6,7,0,10,5,3,14,9,11},
                    {11,3,5,8,2,15,10,13,14,1,7,4,12,9,6,0},
                    {6,8,2,3,9,10,5,12,1,14,4,7,11,13,0,15},
                    {12,4,6,2,10,5,11,9,14,8,13,7,0,3,15,1}
            };

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ObservableList<String> modes = FXCollections.observableArrayList(
                "Зашифровать", "Расшифровать"
        );
        ObservableList<String> keyTypes = FXCollections.observableArrayList(
                "По умолчанию", "Случайный", "Пользовательский"
        );
        cbModesText.setItems(modes);
        cbModesText.setValue(modes.get(0));
        cbModesFile.setItems(modes);
        cbModesFile.setValue(modes.get(0));
        cbKeyTypeText.setItems(keyTypes);
        cbKeyTypeText.setValue(keyTypes.get(0));
        cbKeyTypeFile.setItems(keyTypes);
        cbKeyTypeFile.setValue(keyTypes.get(0));
        cbKeyTypeText.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                if (t1.intValue() == 0) {
                    keyInText.setText(defaultKey);
                }
                else if (t1.intValue() == 1) {
                    Random random = new Random();
                    byte[] key = new byte[32];
                    random.nextBytes(key);
                    String[] asStr = toHexArray(key);
                    keyInText.setText(String.join("", asStr));
                }
                else if (t1.intValue() == 2) {
                    keyInText.clear();
                    keyInText.setEditable(true);
                }
            }
        });
        cbKeyTypeFile.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                if (t1.intValue() == 0) {
                    keyInFile.setText(defaultKey);
                }
                else if (t1.intValue() == 1) {
                    Random random = new Random();
                    byte[] key = new byte[32];
                    random.nextBytes(key);
                    String[] asStr = toHexArray(key);
                    keyInFile.setText(String.join("", asStr));
                }
                else if (t1.intValue() == 2) {
                    keyInFile.clear();
                    keyInFile.setEditable(true);
                }
            }
        });
        btnTransformText.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                String text = textIn.getText();
                byte[] bt = hexStringToByteArray(keyInText.getText().trim());
                if (bt.length < 32) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Ошибка!");
                    alert.setHeaderText("Недостаточная длина ключа!");
                    alert.showAndWait();
                    return;
                }
                getKeys(bt);
                if (Objects.equals(cbModesText.getValue(), modes.get(0))) {
                    byte[] tmp = text.getBytes();
                    tmp = setPadding(tmp, tmp.length);
                    processPart(tmp, tmp.length, tmp, true);
                    textOut.setText(String.join("", toHexArray(tmp)));
                }
                else {
                    byte[] tmp = hexStringToByteArray(text);
                    processPart(tmp, tmp.length, tmp, false);
                    textOut.setText(new String(tmp).replace((char) 65533, ' ').replace((char) 0, ' ').trim());
                }
                clearKeys();
            }
        });
        btnTransformFile.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                Path in = Path.of(inFile.getText().trim());
                Path out = Path.of(outFile.getText().trim());
                byte[] bt = hexStringToByteArray(keyInFile.getText().trim());
                if (bt.length < 32) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Ошибка!");
                    alert.setHeaderText("Недостаточная длина ключа!");
                    alert.showAndWait();
                    return;
                }
                getKeys(bt);
                if (inFile.getText().trim().isEmpty() || outFile.getText().trim().isEmpty() || Files.notExists(in)) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Ошибка!");
                    alert.setHeaderText("Не удалось открыть файл!");
                    alert.setContentText("Проверьте путь к файлу!");
                    alert.showAndWait();
                    return;
                }
                try {
                    if (Files.notExists(out))
                        Files.createFile(out);
                    processBytes(in, out, Objects.equals(cbModesFile.getValue(), modes.get(0)));
                } catch (IOException e) {
                    clearKeys();
                    throw new RuntimeException(e);
                }
                clearKeys();
                inFile.clear();
                outFile.clear();
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Успешно!");
                alert.setHeaderText("Файл " + out.toAbsolutePath() + " сохранен!");
                alert.showAndWait();
            }
        });
        pickInFile.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Выберите входной файл");
                File f = fileChooser.showOpenDialog(pickInFile.getScene().getWindow());
                if (f != null)
                    inFile.setText(f.getAbsolutePath());
            }
        });
        pickOutFile.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Выберите выходной файл");
                File f = fileChooser.showOpenDialog(pickOutFile.getScene().getWindow());
                if (f != null)
                    outFile.setText(f.getAbsolutePath());
            }
        });
        textOut.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                textOut.selectAll();
            }
        });
        btnClearText.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                textIn.clear();
                textOut.clear();
            }
        });
        btnClearFile.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                inFile.clear();
                outFile.clear();
            }
        });
    }

    private void addition2(byte[] x, byte[] y, byte[] out) {
        for (int i = 0; i < x.length; i++) {
            out[i] = (byte) ((x[i] & 0xFF) ^ (y[i] & 0xFF));
        }
    }

    private void addition32(byte[] x, byte[] y, byte[] out) {
        int overflow = 0;
        for (int i = 3; i >= 0; i--) {
            overflow = (x[i] & 0xFF) + (y[i] & 0xFF) + (overflow >> 8);
            out[i] = (byte) (overflow & 0xFF);
        }
    }

    private void substitute(byte[] input) {
        byte left, right;
        for (int i = 0; i < 4; i++) {
            left = (byte) ((input[i] >> 4) & 0xF);
            right = (byte) (input[i] & 0xF);
            left = Pi[i * 2][left];
            right = Pi[i * 2 + 1][right];
            input[i] = (byte) ((left << 4) | right);
        }
    }

    private byte[] g(byte[] input, byte[] key) {
        byte[] out = new byte[4];
        addition32(input, key, out);
        substitute(out);
        ByteBuffer buffer = ByteBuffer.wrap(out);
        long r = buffer.getInt() & 0xFFFFFFFFL;
        r = (r << 11) | (r >> 21);
        out[3] = (byte) r;
        out[2] = (byte) (r >> 8);
        out[1] = (byte) (r >> 16);
        out[0] = (byte) (r >> 24);
        return out;
    }

    private byte[] G(byte[] input, byte[] key, boolean last, byte[] out) {
        byte[] left;
        byte[] right;
        left = Arrays.copyOfRange(input, 0 , 4);
        right = Arrays.copyOfRange(input, 4, 8);
        byte[] tmp = g(right, key);
        addition2(left, tmp, tmp);
        for (int i = 0; i < out.length / 2; i++) {
            if (!last) {
                out[i] = right[i];
                out[i + 4] = tmp[i];
            }
            else {
                out[i] = tmp[i];
                out[i + 4] = right[i];
            }
        }
        return out;
    }

    private void processPart(byte[] inBuf, int dataLen, byte[] outBuf, boolean encrypt)
    {
        byte[] tmp = new byte[BLOCK_SIZE];
        for (int i = 0; i < dataLen / BLOCK_SIZE; i++)
        {
            System.arraycopy(inBuf, i * BLOCK_SIZE, tmp, 0, BLOCK_SIZE);
            if (encrypt)
                magmaEncrypt(tmp, tmp);
            else
                magmaDecrypt(tmp, tmp);
            System.arraycopy(tmp, 0, outBuf, i * BLOCK_SIZE, BLOCK_SIZE);
        }
    }

    private void processBytes(Path inpFile, Path outFile, boolean encrypt) throws IOException {
        byte[] inBuf = new byte[BUF_SIZE];
        byte[] outBuf = new byte[BUF_SIZE];
        try(InputStream inStream = new BufferedInputStream(Files.newInputStream(inpFile));
            OutputStream outStream = Files.newOutputStream(outFile);) {
            while (inStream.available() > 0)
            {
                if (inStream.available() > BUF_SIZE)
                {
                    inStream.readNBytes(inBuf, 0, inBuf.length);
                    processPart(inBuf, inBuf.length, outBuf, encrypt);
                    outStream.write(outBuf);
                }
                else
                {
                    int dataRead = inStream.read(inBuf);
                    if (encrypt) {
                        inBuf = setPadding(inBuf, dataRead);
                        processPart(inBuf, inBuf.length, outBuf, encrypt);
                        outStream.write(outBuf, 0, inBuf.length);
                    }
                    else {
                        processPart(inBuf, dataRead, outBuf, encrypt);
                        int padIdx = dataRead;
                        for (int i = dataRead; i >= 0; i--) {
                            if (outBuf[i] == (byte) 0x80) {
                                padIdx = i;
                                break;
                            }
                        }
                        outStream.write(outBuf, 0, padIdx);
                    }
                }
            }
        }
    }

    private final int BUF_SIZE = 1024;

    private void magmaEncrypt(byte[] input, byte[] out) {
        G(input, keys[0], false, out);
        for (int i = 2; i < 32; i++) {
            G(out, keys[i - 1], false, out);
        }
        G(out, keys[31], true, out);
    }

    private void magmaDecrypt(byte[] input, byte[] out) {
        G(input, keys[31], false, out);
        for (int i = 2; i < 32; i++) {
            G(out, keys[keys.length - i], false, out);
        }
        G(out, keys[0], true, out);
    }

    private byte[] setPadding(byte[] data, int dataLen) {
        int padSize = BLOCK_SIZE - (dataLen % BLOCK_SIZE);
        byte[] out = new byte[dataLen + padSize];
        System.arraycopy(data, 0, out, 0, dataLen);
        out[dataLen] = (byte) 0x80;
        for (int i = dataLen + 1; i < out.length; i++)
            out[i] = 0x00;
        return out;
    }

    private void getKeys(byte[] key) {
        for (int i = 0; i < keys.length; i++) {
            if (i < 24)
                for (int j = 0; j < keys[i].length; j++) {
                    keys[i][j] = key[(4 * i + j) % 32];
                }
            else
                for (int j = 0; j < keys[i].length; j++)
                    keys[i][keys[i].length - j - 1] = key[key.length - j - 1 - (i % 24 * 4)];
        }
    }

    private void clearKeys() {
        for (byte[] key : keys) {
            Arrays.fill(key, (byte) 0);
        }
    }

    private static final char[] HEX_ARRAY = "0123456789abcdef".toCharArray();

    private static String[] toHexArray(byte[] arr) {
        String[] rVal = new String[arr.length];
        int i = 0;
        for (byte b : arr) {
            int v = b & 0xFF;
            char[] hexChars = new char[2];
            hexChars[0] = HEX_ARRAY[v >>> 4];
            hexChars[1] = HEX_ARRAY[v & 0x0F];
            rVal[i++] = new String(hexChars);
        }
        return rVal;
    }

    public static byte[] hexStringToByteArray(String s) {
        if (s.length() % 2 != 0)
            s += "0";
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }
}

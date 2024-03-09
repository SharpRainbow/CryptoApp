module ru.mirea.cryptoapp {
    requires javafx.controls;
    requires javafx.fxml;


    opens ru.mirea.cryptoapp to javafx.fxml;
    exports ru.mirea.cryptoapp;
}
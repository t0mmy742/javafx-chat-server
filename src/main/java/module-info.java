module chat.server {
    requires javafx.controls;
    requires javafx.fxml;

    opens fr.thomasleberre.chat.server.controller to javafx.fxml;

    exports fr.thomasleberre.chat.server to javafx.graphics;
}
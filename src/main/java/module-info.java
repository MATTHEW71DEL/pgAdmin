module matvLabs {
    requires javafx.controls;
    requires javafx.fxml;

    opens org.matv to javafx.fxml;
    exports org.matv;
}
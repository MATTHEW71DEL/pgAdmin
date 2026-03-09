module matvLabs {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;

    requires org.controlsfx.controls;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;

    requires java.sql;
    requires org.postgresql.jdbc;
    requires static lombok;
    requires org.yaml.snakeyaml;

    opens org.matv to javafx.fxml;
    opens org.matv.ui to javafx.fxml;

    exports org.matv;
    opens org.matv.db to javafx.base;
}
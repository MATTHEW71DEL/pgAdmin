module matvLabs {
    // Модули JavaFX
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;

    // Библиотеки, которые были видны в вашем логе ошибок (classpath)
    requires org.controlsfx.controls;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;

    // Если планируете работать с БД
    requires java.sql;
    requires org.postgresql.jdbc;
    requires static lombok;
    requires org.yaml.snakeyaml;

    // Разрешаем JavaFX доступ к вашим пакетам (через рефлексию)
    // Это критично для работы @FXML и контроллеров
    opens org.matv to javafx.fxml;
    opens org.matv.ui to javafx.fxml;

    opens org.matv.model to javafx.base;
    // Экспортируем пакеты для использования другими модулями
    exports org.matv;
}
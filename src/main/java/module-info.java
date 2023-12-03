module vacinafx {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires transitive javafx.graphics;

    opens vacinafx to javafx.fxml, javafx.graphics;
    opens vacinafx.model to javafx.base, javafx.graphics;
    opens vacinafx.controller to javafx.fxml, javafx.graphics;
    opens vacinafx.dao to java.base, javafx.graphics;
    exports vacinafx;
}

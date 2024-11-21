module group24 {
    requires javafx.controls;
    requires javafx.fxml;
    requires transitive javafx.graphics;
    requires com.fasterxml.jackson.databind;

    opens group24 to javafx.fxml, com.fasterxml.jackson.databind;
    exports group24;
}

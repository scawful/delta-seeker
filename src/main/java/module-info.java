module org.halext.deltaseeker {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.base;
    requires json.simple;
    requires transitive javafx.graphics;

    opens org.halext.deltaseeker to javafx.fxml;
    exports org.halext.deltaseeker;

    opens org.halext.deltaseeker.service to json.simple;
    exports org.halext.deltaseeker.service;

}

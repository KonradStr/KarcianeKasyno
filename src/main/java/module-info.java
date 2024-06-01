module com.example.casino {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires java.sql;

    opens com.example.casino to javafx.fxml;
    exports com.example.casino;
    exports com.example.casino.Server;
    opens com.example.casino.Server to javafx.fxml;
    exports com.example.casino.Packets;
    opens com.example.casino.Packets to javafx.fxml;
    exports com.example.casino.Controllers;
    opens com.example.casino.Controllers to javafx.fxml;
}
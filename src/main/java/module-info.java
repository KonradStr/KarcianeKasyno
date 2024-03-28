module com.example.karcianekasyno {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.karcianekasyno to javafx.fxml;
    exports com.example.karcianekasyno;
}
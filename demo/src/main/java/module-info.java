module org.example.demo {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.bootstrapfx.core;
    requires org.apache.lucene.queries;
    requires org.apache.lucene.queryparser;
    requires org.apache.lucene.core;
    requires commons.csv;
    requires static lombok;


    opens org.example.demo to javafx.fxml;
    exports org.example.demo;
}
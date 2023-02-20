module escape {
    requires javafx.controls;
    requires javafx.graphics;
    requires javafx.web;

    requires com.google.gson;
    requires java.xml.bind;
    //TODO: fix me
//    requires antlr4;
    requires Escape.Configuration.Tool;

    exports escape.gui;
    exports escape.gui.utilities;
    exports escape.builder;
    exports escape;
    exports escape.gui.states;
}
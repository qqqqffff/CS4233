module escape {
    requires javafx.controls;
    requires javafx.graphics;
    requires javafx.web;

    requires com.google.gson;
    requires java.xml.bind;
    //FIXED! or not (lazy prof with bad coding practices teaching a course about having good coding practices). Note if you have dependencies for a starter code you should use a dependency manager especially if you build your own dependency
//    requires antlr4;
    requires Escape.Configuration.Tool;

    exports escape.gui;
    exports escape.gui.utilities;
    exports escape.builder;
    exports escape;
    exports escape.gui.states;
    exports escape.required;
}
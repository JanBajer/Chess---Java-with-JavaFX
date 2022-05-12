module chess {
    requires javafx.graphics;
    requires javafx.controls;
    requires javafx.swing;
    requires java.logging;

    exports sample;
    opens sample;
}
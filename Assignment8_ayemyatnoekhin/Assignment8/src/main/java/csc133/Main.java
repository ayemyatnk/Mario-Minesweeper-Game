package csc133;

import SlRenderer.slTilesManager;
import SlRenderer.slWindow;

public class Main {
    public static void main(String[] args) {
        try {
            // Create an instance of slWindow
            slWindow window = SlRenderer.slWindow.get();

            // Run the window with a specified number of mines
            window.run(4);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}


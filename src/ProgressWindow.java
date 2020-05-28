import javax.swing.*;
import java.awt.*;

public class ProgressWindow extends JPanel {
    static JLabel mainLbl;
    static JProgressBar pbar;
    static JFrame frame;

    static String mainLblTxt = "Processing...";
    static int MINIMUM = 0;
    static int MAXIMUM = 100;
    static int isVisible = 0;

    public ProgressWindow(String labelInput, int maximumInput) {//Add data here to input mainLabel String
        if(!labelInput.equals(""))
            mainLblTxt = labelInput;
        if(maximumInput != 0)
            MAXIMUM = maximumInput;

        runWindow();
    }

    private ProgressWindow(){
        pbar = new JProgressBar();
        pbar.setMinimum(MINIMUM);
        pbar.setMaximum(MAXIMUM);
        mainLbl = new JLabel(mainLblTxt);
        add(mainLbl);
        add(pbar);
    }

    public int getIsVisible() {
        return isVisible;
    }

    public void setIsVisible(int isVisible) {
        ProgressWindow.isVisible = isVisible;
    }

    public void updateBar(int newValue) {
        pbar.setValue(newValue);
    }

    public void closeWindow(){
        frame.setVisible(false);
        isVisible = 0;
    }

    public void runWindow() {
        final ProgressWindow it = new ProgressWindow();

        frame = new JFrame("Processing...");
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.setContentPane(it);
        frame.pack();
        center(frame);
        frame.setVisible(true);
        isVisible = 1;
    }

    public static void center(JFrame frame) {

        // get the size of the screen, on systems with multiple displays,
        // the primary display is used
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();

        // calculate the new location of the window
        int w = frame.getSize().width;
        int h = frame.getSize().height;

        int x = (dim.width - w) / 2;
        int y = (dim.height - h) / 2;

        // moves this component to a new location, the top-left corner of
        // the new location is specified by the x and y
        // parameters in the coordinate space of this component's parent
        frame.setLocation(x, y);
    }
}
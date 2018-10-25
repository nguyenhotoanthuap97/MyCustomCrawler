import javax.swing.*;
import java.awt.*;

public class MyCrawlerGUI {

  public static void main(String[] args) {

    String crawlerStorageFolder = "Results/";
    int MAX_DEPTH = 1;
    int MAX_THREAD = 7;
    int DELAY = 1000;
    String url = "http://phimmoi.com";

    JFrame form = new JFrame("My crawler");
    MyCrawlerGUI myCrawlerGUI = new MyCrawlerGUI();
    form.setLayout(new BorderLayout());
    form.add(new MyPanel());
    form.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    form.pack();
    form.setVisible(true);
    form.setResizable(false);
    form.setLocationRelativeTo(null);
    form.setSize(700, 450);

  }
}

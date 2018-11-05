import javax.swing.*;
import java.awt.*;

public class MyCrawlerGUI {

  public static void main(String[] args) {

    SwingUtilities.invokeLater(() -> {
      JFrame form = new JFrame("My crawler");
      form.setLayout(new BorderLayout());
      form.add(new MyPanel());
      form.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      form.pack();
      form.setVisible(true);
      form.setResizable(false);
      form.setLocationRelativeTo(null);
      form.setSize(700, 600);
    });
  }
}

import javax.swing.*;
import javax.swing.text.DefaultCaret;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MyPanel extends JPanel {
  private JTextArea urlTxt = new JTextArea(1, 32);
  private JTextArea storageTxt = new JTextArea(1, 15);
  private JTextArea depthTxt = new JTextArea(1, 4);
  private JTextArea threadTxt = new JTextArea(1, 4);
  private JTextArea delayTxt = new JTextArea(1, 4);
  private JTextArea curCrawlTxt = new JTextArea(3, 40);
  JButton startBtton = new JButton("Start crawling");

  public String url = "";
  public String storage = "";
  public int depth = 0;
  public int threadCount = 0;
  public int politeDelay = 0;

  public MyPanel() {
    setLayout(new GridBagLayout());
    GridBagConstraints gridBagConstraints = new GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.insets = new Insets(10, 2, 10, 2);

    Font headerFont = new Font("Times new roman", 1, 36);
    Font bigFont = new Font("Times new roman", 0, 18);
    Font smallFont = new Font("arial", 0, 18);

    gridBagConstraints.gridwidth = 4;
    JLabel jLabel = new JLabel();
    jLabel.setText("Crawler");
    jLabel.setFont(headerFont);
    add(jLabel, gridBagConstraints);
    gridBagConstraints.gridy++;

    gridBagConstraints.gridwidth = 1;
    JLabel jLabel1 = new JLabel();
    jLabel1.setText("Link to crawl: ");
    jLabel1.setFont(smallFont);
    add(jLabel1, gridBagConstraints);
    gridBagConstraints.gridx++;
    gridBagConstraints.gridwidth = 3;

    urlTxt.getDocument().putProperty("filterNewlines", Boolean.TRUE);
    urlTxt.setFont(smallFont);
    JScrollPane urlScrollPane = new JScrollPane(urlTxt);
    urlScrollPane.getHorizontalScrollBar().setPreferredSize(new Dimension(0,0));
    add(urlScrollPane, gridBagConstraints);
    gridBagConstraints.gridy++;

    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridwidth = 1;
    JLabel jLabel2 = new JLabel();
    jLabel2.setFont(smallFont);
    jLabel2.setText("Storage folder: ");
    add(jLabel2, gridBagConstraints);
    gridBagConstraints.gridx++;

    storageTxt.getDocument().putProperty("filterNewlines", Boolean.TRUE);
    storageTxt.setFont(smallFont);
    JScrollPane storageScrollPane = new JScrollPane(storageTxt);
    storageScrollPane.getHorizontalScrollBar().setPreferredSize(new Dimension(0,0));
    add(storageScrollPane, gridBagConstraints);
    gridBagConstraints.gridx++;

    JLabel jLabel3 = new JLabel();
    jLabel3.setFont(smallFont);
    jLabel3.setText("Depth: ");
    add(jLabel3, gridBagConstraints);
    gridBagConstraints.gridx++;

    depthTxt.getDocument().putProperty("filterNewlines", Boolean.TRUE);
    depthTxt.setFont(smallFont);
    depthTxt.setText("1");
    JScrollPane depthScrollPane = new JScrollPane(depthTxt);
    depthScrollPane.getHorizontalScrollBar().setPreferredSize(new Dimension(0,0));
    add(depthScrollPane, gridBagConstraints);
    gridBagConstraints.gridy ++;

    gridBagConstraints.gridx = 0;
    JLabel jLabel4 = new JLabel();
    jLabel4.setFont(smallFont);
    jLabel4.setText("Thread count: ");
    add(jLabel4, gridBagConstraints);
    gridBagConstraints.gridx++;

    threadTxt.getDocument().putProperty("filterNewlines", Boolean.TRUE);
    threadTxt.setFont(smallFont);
    threadTxt.setText("1");
    JScrollPane threadScrollPane = new JScrollPane(threadTxt);
    threadScrollPane.getHorizontalScrollBar().setPreferredSize(new Dimension(0,0));
    add(threadScrollPane, gridBagConstraints);
    gridBagConstraints.gridx++;

    JLabel jLabel5 = new JLabel();
    jLabel5.setFont(smallFont);
    jLabel5.setText("Polite delay (ms): ");
    add(jLabel5, gridBagConstraints);
    gridBagConstraints.gridx++;

    delayTxt.getDocument().putProperty("filterNewlines", Boolean.TRUE);
    delayTxt.setFont(smallFont);
    delayTxt.setText("200");
    JScrollPane delayScrollPane = new JScrollPane(delayTxt);
    delayScrollPane.getHorizontalScrollBar().setPreferredSize(new Dimension(0,0));
    add(delayScrollPane, gridBagConstraints);
    gridBagConstraints.gridy ++;
    gridBagConstraints.gridx = 0;

    JLabel jLabel6 = new JLabel();
    jLabel6.setFont(smallFont);
    jLabel6.setText("Currently crawling: ");
    add(jLabel6, gridBagConstraints);
    gridBagConstraints.gridy++;

    gridBagConstraints.gridwidth = 4;
    curCrawlTxt.setFont(bigFont);
    curCrawlTxt.setEditable(false);
    JScrollPane curCrawlScrollPane = new JScrollPane(curCrawlTxt);
    curCrawlScrollPane.getHorizontalScrollBar().setPreferredSize(new Dimension(0,0));
    add(curCrawlScrollPane, gridBagConstraints);
    gridBagConstraints.gridy ++;
    gridBagConstraints.gridx = 0;

    gridBagConstraints.gridwidth = 4;
    startBtton.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent e)
      {
        startBtton.setEnabled(false);
        url = urlTxt.getText();
        depth = Integer.parseInt(depthTxt.getText());
        threadCount = Integer.parseInt(threadTxt.getText());
        storage = storageTxt.getText();
        politeDelay = Integer.parseInt(delayTxt.getText());

        MyCrawler crawler = new MyCrawler(url, storage, depth, threadCount, politeDelay, false, curCrawlTxt);
        crawler.start();
        JOptionPane.showMessageDialog(null, "Crawling complete!!!");
        startBtton.setEnabled(true);
      }
    });
    add(startBtton, gridBagConstraints);
  }
}

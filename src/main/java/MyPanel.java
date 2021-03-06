import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class MyPanel extends JPanel implements ActionListener {
  private JTextArea urlTxt = new JTextArea(1, 32);
  private JButton addServerBtton = new JButton("Add server to crawl");
  private JButton removeServerBtton = new JButton("Remove");
  private String[] columnName = {"Server to crawl"};
  TableModel tableModel = new DefaultTableModel(columnName, 0);
  private JTable urlList = new JTable(tableModel);
  private DefaultTableModel model = (DefaultTableModel) urlList.getModel();
  private JTextArea storageTxt = new JTextArea(1, 15);
  private JButton pathChoosingBtton = new JButton("Browse...");
  private JTextArea depthTxt = new JTextArea(1, 4);
  private JTextArea threadTxt = new JTextArea(1, 4);
  private JTextArea curCrawlTxt = new JTextArea(3, 40);
  private JButton startBtton = new JButton("Start crawling");

  public String url = "";
  public List<String> urlTxtList = new ArrayList<>();
  public String storage = "";
  public int depth = 0;
  public int threadCount = 0;

  public MyPanel() {
    setLayout(new GridBagLayout());
    GridBagConstraints gridBagConstraints = new GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.insets = new Insets(8, 2, 8, 2);

    Font headerFont = new Font("Times new roman", 1, 36);
    Font bigFont = new Font("Times new roman", 0, 18);
    Font smallFont = new Font("arial", 0, 18);

    gridBagConstraints.gridwidth = 5;
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

    gridBagConstraints.gridwidth = 4;
    urlTxt.getDocument().putProperty("filterNewlines", Boolean.TRUE);
    urlTxt.setText("http://");
    urlTxt.setFont(smallFont);
    urlTxt.setCaretPosition(urlTxt.getDocument().getLength());
    JScrollPane urlScrollPane = new JScrollPane(urlTxt);
    urlScrollPane.getHorizontalScrollBar().setPreferredSize(new Dimension(0, 0));
    add(urlScrollPane, gridBagConstraints);
    gridBagConstraints.gridy++;

    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridwidth = 2;
    removeServerBtton.setEnabled(false);
    removeServerBtton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        int selectedRow = urlList.getSelectedRow();
        urlTxtList.remove(selectedRow);
        model.removeRow(selectedRow);
        System.out.print(selectedRow);
        if (urlList.getRowCount() == 0) {
          removeServerBtton.setEnabled(false);
        }
        else urlList.setRowSelectionInterval(0, 0);
      }
    });
    add(removeServerBtton, gridBagConstraints);
    gridBagConstraints.gridx++;

    gridBagConstraints.gridx = 2;
    gridBagConstraints.gridwidth = 3;
    addServerBtton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        removeServerBtton.setEnabled(true);
        String temp = urlTxt.getText();
        urlTxtList.add(temp);
        model.addRow(new Object[]{temp});
        if (urlList.getRowCount() > 0) urlList.setRowSelectionInterval(0, 0);
        urlTxt.setText("http://");
      }
    });
    add(addServerBtton, gridBagConstraints);
    gridBagConstraints.gridy++;

    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridwidth = 5;
    JScrollPane urlListScrollPane = new JScrollPane(urlList);
    urlListScrollPane.setPreferredSize(new Dimension(500, 150));
    urlListScrollPane.getHorizontalScrollBar().setPreferredSize(new Dimension(0, 0));
    add(urlListScrollPane, gridBagConstraints);
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
    storageTxt.setEditable(false);
    JScrollPane storageScrollPane = new JScrollPane(storageTxt);
    storageScrollPane.getHorizontalScrollBar().setPreferredSize(new Dimension(0, 0));
    add(storageScrollPane, gridBagConstraints);
    gridBagConstraints.gridx++;

    gridBagConstraints.gridwidth = 1;
    pathChoosingBtton.addActionListener(this);
    add(pathChoosingBtton, gridBagConstraints);
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
    depthScrollPane.getHorizontalScrollBar().setPreferredSize(new Dimension(0, 0));
    add(depthScrollPane, gridBagConstraints);
    gridBagConstraints.gridy++;

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
    threadScrollPane.getHorizontalScrollBar().setPreferredSize(new Dimension(0, 0));
    add(threadScrollPane, gridBagConstraints);
    gridBagConstraints.gridx++;
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy++;

    JLabel jLabel6 = new JLabel();
    jLabel6.setFont(smallFont);
    jLabel6.setText("Currently crawling: ");
    add(jLabel6, gridBagConstraints);
    gridBagConstraints.gridy++;

    gridBagConstraints.gridwidth = 5;
    curCrawlTxt.setFont(bigFont);
    curCrawlTxt.setEditable(false);
    JScrollPane curCrawlScrollPane = new JScrollPane(curCrawlTxt);
    curCrawlScrollPane.getHorizontalScrollBar().setPreferredSize(new Dimension(0, 0));
    add(curCrawlScrollPane, gridBagConstraints);
    gridBagConstraints.gridy++;
    gridBagConstraints.gridx = 0;

    gridBagConstraints.gridwidth = 5;
    startBtton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        startBtton.setEnabled(false);
        depth = Integer.parseInt(depthTxt.getText());
        threadCount = Integer.parseInt(threadTxt.getText());
        storage = storageTxt.getText();

        urlTxtList.stream().forEach(url -> {
          MyCrawler crawler = new MyCrawler(url, storage, depth, threadCount, false, curCrawlTxt, startBtton);
          crawler.start();
        });
      }
    });
    add(startBtton, gridBagConstraints);

    //Listener for tabstop
    urlTxt.addKeyListener(new KeyAdapter() {
      @Override
      public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_TAB) {
          if (e.getModifiers() > 0) {
            urlTxt.transferFocusBackward();
          } else {
            urlTxt.transferFocus();
          }
          e.consume();
        }
      }
    });
    storageTxt.addKeyListener(new KeyAdapter() {
      @Override
      public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_TAB) {
          if (e.getModifiers() > 0) {
            storageTxt.transferFocusBackward();
          } else {
            storageTxt.transferFocus();
          }
          e.consume();
        }
      }
    });
    depthTxt.addKeyListener(new KeyAdapter() {
      @Override
      public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_TAB) {
          if (e.getModifiers() > 0) {
            depthTxt.transferFocusBackward();
          } else {
            depthTxt.transferFocus();
          }
          e.consume();
        }
      }
    });
    threadTxt.addKeyListener(new KeyAdapter() {
      @Override
      public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_TAB) {
          if (e.getModifiers() > 0) {
            threadTxt.transferFocusBackward();
          } else {
            threadTxt.transferFocus();
          }
          e.consume();
        }
      }
    });
    startBtton.addKeyListener(new KeyAdapter() {
      @Override
      public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_TAB) {
          if (e.getModifiers() > 0) {
            startBtton.transferFocusBackward();
          } else {
            startBtton.transferFocus();
          }
          e.consume();
        }
      }
    });
  }

  //folder choosing dialog
  public void actionPerformed(ActionEvent e) {
    JFileChooser chooser = new JFileChooser();
    chooser.setCurrentDirectory(new java.io.File("."));
    chooser.setDialogTitle("Save directory");
    chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
    chooser.setAcceptAllFileFilterUsed(false);

    if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
      storage = chooser.getSelectedFile().getPath();
      storageTxt.setText(chooser.getSelectedFile().getPath());
    }
  }
}

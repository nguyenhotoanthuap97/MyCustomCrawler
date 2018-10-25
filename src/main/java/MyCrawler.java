import com.panforge.robotstxt.RobotsTxt;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.swing.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;
import java.util.TreeSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

public class MyCrawler {
  private static final Pattern FILTERS = Pattern.compile(".*(\\.(css|js|gif|jpg|png|mp3|zip|gz))$");
  private String userAgent = "WebScienceCrawler";
  private String storageFolder = "Results/";
  private String baseUrl = "http://";
  private int maxDepth;
  private static TreeSet<String> visitedNode = new TreeSet<>();
  private String domain = "";
  private RobotsTxt robotsTxt;
  private ExecutorService executorService;
  private static Date lastDownload = new Date();
  private long crawlerDelay = 200;
  private JTextArea result;

  public MyCrawler(String urlString, String folder, int inputMaxDepth, int maxThread, long delay, boolean fakeUserAgent, JTextArea resultPanel) {
    try {
      domain = urlString;
      storageFolder = folder;
      maxDepth = inputMaxDepth;
      crawlerDelay = delay;
      result = resultPanel;
      if (fakeUserAgent)
        userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/69.0.3497.100 Safari/537.36";
      URL url = new URL(urlString);
      URLConnection robotsUrl = new URL("http://" + url.getHost() + "/robots.txt").openConnection();
      robotsUrl.addRequestProperty("User-Agent", userAgent);
      robotsTxt = RobotsTxt.read(robotsUrl.getInputStream());
      baseUrl += robotsUrl.getURL().getHost() + "/";
      storageFolder += baseUrl.replaceAll("(^http|https)+://", "").replaceAll("www.", "").split("[.]")[0];
      new File(storageFolder).mkdirs();
      executorService = Executors.newFixedThreadPool(maxThread);
    } catch (IOException ex) {
      Logger.getLogger(MyCrawler.class.getName()).log(Level.SEVERE, null, ex);
    }
  }

  public void start() {
    if (domain.charAt(domain.length() - 1) != '/')
      domain += '/';
    visitedNode.add(domain);
    executorService.execute(() -> visit(domain, 0));
  }

  private boolean shouldVisit(String url) {
    String href = url.toLowerCase();
    return (href.length() <= 128) &&
            (href.length() >= baseUrl.length()) &&
            href.startsWith(baseUrl) &&
            robotsTxt.query(userAgent, url) &&
            !FILTERS.matcher(href).matches();
  }

  private synchronized Document download(String url) throws InterruptedException, IOException {
    Date now = new Date();
    if ((now.getTime() - lastDownload.getTime()) <= crawlerDelay) {
      System.out.println("wait for time :" + (now.getTime() - lastDownload.getTime()));
      wait(crawlerDelay - (now.getTime() - lastDownload.getTime()));
    }
    Document htmlDocument = Jsoup.connect(url)
            .userAgent(userAgent)
            .get();
    lastDownload = new Date();
    return htmlDocument;
  }

  private void visit(String url, int nodeIndex) {
    try {
      result.setText("url: " + url + "\nnodeIndex: " + nodeIndex + "\n...");

      Document htmlDocument = download(url);

      String pageName = htmlDocument.select("title").text();
      saveResult(htmlDocument.text(), htmlDocument.html(), pageName);

      if (nodeIndex < maxDepth) {
        Elements linksElements = htmlDocument.select("a");
        for (Element e : linksElements) {
          String childNode = e.attr("abs:href");
          if (shouldVisit(childNode) && visitedNode.add(childNode))
            executorService.execute(() -> visit(childNode, nodeIndex + 1));
        }
      }
    } catch (InterruptedException | IOException ex) {
      Logger.getLogger(MyCrawler.class.getName()).log(Level.SEVERE, null, ex);
    }
  }

  private void saveResult(String txtResult, String htmlResult, String pageName) {
    String fileName = pageName.replaceAll("[\\\\/:*?\"<>|]", "");
    try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(storageFolder + "/text/" + fileName + ".txt"), "UTF-8"))) {
      bw.write(txtResult);
      bw.flush();
      bw.close();
    } catch (IOException ex) {
      Logger.getLogger(MyCrawler.class.getName()).log(Level.SEVERE, null, ex);
    }
    try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(storageFolder + "/html" + fileName + ".html"), "UTF-8"))) {
      bw.write(htmlResult);
      bw.flush();
      bw.close();
    } catch (IOException ex) {
      Logger.getLogger(MyCrawler.class.getName()).log(Level.SEVERE, null, ex);
    }
  }
}

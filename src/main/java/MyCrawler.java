import com.panforge.robotstxt.RobotsTxt;
import org.jsoup.Connection;
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
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.TreeSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

public class MyCrawler {
  private static final Pattern FILTERS = Pattern.compile(".*(\\.(css|js|gif|jpg|png|mp3|zip|gz))$");
  private String userAgent = "WebScienceProject";
  private String storageFolder = "Results/";
  private String baseUrl = "http://";
  private int maxDepth;
  private static TreeSet<String> visitedNode = new TreeSet<>();
  private String domain = "";
  private String domainHost = "";
  private RobotsTxt robotsTxt;
  private static Date lastDownload = new Date();
  private long crawlerDelay = 0;
  private JTextArea result;
  private JButton startButton;
  private ExecutorService executorService;
  private int threadCount = 0;

  public MyCrawler(String urlString, String folder, int inputMaxDepth, int maxThread, boolean fakeUserAgent, JTextArea resultPanel, JButton startBtn) {
    domain = urlString;
    storageFolder = folder;
    maxDepth = inputMaxDepth;
    result = resultPanel;
    startButton = startBtn;
    if (fakeUserAgent)
      userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/69.0.3497.100 Safari/537.36";
    try {
      URL url = new URL(urlString);
      domainHost = url.getHost();
      domainHost = domainHost.replaceAll("www.", "");
      URLConnection robotsUrl = new URL(url.getProtocol() + "://" + url.getHost() + "/robots.txt").openConnection();
      baseUrl = url.getProtocol() + "://";
      if (robotsUrl != null) {
        robotsUrl.addRequestProperty("User-Agent", userAgent);
        robotsTxt = RobotsTxt.read(robotsUrl.getInputStream());
        baseUrl += robotsUrl.getURL().getHost() + "/";
        String robotsStr = robotsTxt.toString();
        String[] robotsArrays = robotsStr.split("\n");
        List<String> robotsList = Arrays.asList(robotsArrays);
        int crawlDelay = -1;
        for (int i = 0; i < robotsList.size() - 1; i++) {
          if (robotsList.get(i).toLowerCase().contains(("User-agent: " + userAgent).toLowerCase())) {
            for (int j = i + 1; j < robotsList.size(); j++) {
              if (robotsList.get(j).toLowerCase().contains("user-agent")) {
                break;
              }
              if (robotsList.get(j).toLowerCase().contains("crawl-delay:")) {
                crawlDelay = Integer.parseInt(robotsList.get(j).split(" ")[1]);
                break;
              }
            }
            if (crawlDelay >= 0) break;
          }
        }
        if (crawlDelay < 0) {
          for (int i = 0; i < robotsList.size() - 1; i++) {
            if (robotsList.get(i).toLowerCase().contains("user-agent: *")) {
              for (int j = i + 1; j < robotsList.size(); j++) {
                if (robotsList.get(j).toLowerCase().contains("user-agent")) {
                  break;
                }
                if (robotsList.get(j).toLowerCase().contains("crawl-delay:")) {
                  crawlDelay = Integer.parseInt(robotsList.get(j).split(" ")[1]);
                  break;
                }
              }
              if (crawlDelay >= 0) break;
            }
          }
        }
        crawlerDelay = (crawlDelay == -1) ? 200 : crawlDelay * 1000;
      } else
        baseUrl += url.getHost() + "/";
    } catch (IOException ex) {
      Logger.getLogger(MyCrawler.class.getName()).log(Level.SEVERE, null, ex);
    }
    storageFolder += "/" + domainHost;
    new File(storageFolder + "/text").mkdirs();
    new File(storageFolder + "/html").mkdirs();
    executorService = Executors.newFixedThreadPool(maxThread);
  }

  public void start() {
    if (domain.charAt(domain.length() - 1) != '/')
      domain += '/';
    visitedNode.add(domain);
    executorService.execute(() -> visit(domain, 0));
    threadCount += 1;
  }

  private boolean shouldVisit(String url) {
    String href = url.toLowerCase();
    boolean should = href.length() <= 128 &&
            (href.length() >= domainHost.length()) &&
            !FILTERS.matcher(href).matches();
    if (!href.contains(domainHost)) {
      return false;
    }
    if (robotsTxt != null) {
      should = should && robotsTxt.query(userAgent, url);
    }
    return should;
  }

  private synchronized Document download(String url) throws InterruptedException, IOException {
    Date now = new Date();
    try {
      if ((now.getTime() - lastDownload.getTime()) <= crawlerDelay) {
        System.out.println("wait for time :" + (now.getTime() - lastDownload.getTime()));
        wait(crawlerDelay - (now.getTime() - lastDownload.getTime()));
      }
      Connection jsoup = Jsoup.connect(url);
      int retryCount = 0;
      while (jsoup == null && retryCount < 3) {
        jsoup = Jsoup.connect(url);
        retryCount++;
      }
      if (jsoup == null)
        return null;
      lastDownload = new Date();
      return jsoup
              .userAgent(userAgent)
              .get();
    } catch (Exception e) {
      System.out.println(String.format("Can't connect to %s", url));
      threadCount--;
      System.out.println("Exit eadthread: " + threadCount);
      return null;
    }
  }

  private void visit(String url, int nodeIndex) {
    try {
      result.setText("url: " + url + "\nnodeIndex: " + nodeIndex + "\n...");

      Document htmlDocument = download(url);

      if (htmlDocument != null) {
        String pageName = htmlDocument.select("title").text();
        saveResult(htmlDocument.text(), htmlDocument.html(), pageName);

        if (nodeIndex < maxDepth) {
          Elements linksElements = htmlDocument.select("a");
          int crawlLimit = 0;
          for (Element e : linksElements) {
            String childNode = e.attr("abs:href");
            if (shouldVisit(childNode) && visitedNode.add(childNode)) {
              executorService.submit(() -> visit(childNode, nodeIndex + 1));
              threadCount += 1;
              System.out.println("New thread: " + threadCount);
              crawlLimit++;
              if (crawlLimit > 1000) {
                break;
              }
            }
          }
        }
        threadCount -= 1;
        System.out.println("Exit eadthread: " + threadCount);
      }
      if (threadCount == 0) {
        JOptionPane.showMessageDialog(null, domain + " crawling complete!!!");
        startButton.setEnabled(true);
      }
    } catch (InterruptedException | IOException ex) {
      Logger.getLogger(MyCrawler.class.getName()).log(Level.SEVERE, null, ex);
    }
  }

  private void saveResult(String txtResult, String htmlResult, String pageName) {
    String fileName = pageName.replaceAll("[\\\\/:*?\"<>|]", "");
    if (fileName.length() > 100) {
      fileName = fileName.substring(0, 99);
    }
    try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(storageFolder + "/text/" + fileName + ".txt"), "UTF-8"))) {
      bw.write(txtResult);
      bw.flush();
      bw.close();
    } catch (IOException ex) {
      Logger.getLogger(MyCrawler.class.getName()).log(Level.SEVERE, null, ex);
    }
    try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(storageFolder + "/html/" + fileName + ".html"), "UTF-8"))) {
      bw.write(htmlResult);
      bw.flush();
      bw.close();
    } catch (IOException ex) {
      Logger.getLogger(MyCrawler.class.getName()).log(Level.SEVERE, null, ex);
    }
  }
}

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import com.panforge.robotstxt.RobotsTxt;

public class MyCrawler {
  private static final Pattern FILTERS = Pattern.compile(".*(\\.(css|js|gif|jpg|png|mp3|zip|gz))$");
  private static String userAgent = "WebCrawler";
  private static String storageFolder = "Results/";
  private static String baseUrl = "http://";
  private static int maxDepth = 0;
  private static int maxThread = 1;
  private static TreeSet<String> visitedNode = new TreeSet<>();
  private String mainUrl = "";
  private static RobotsTxt robotsTxt;

    public MyCrawler(String urlString, String folder, int maxDept, int maxThread, boolean fakeUserAgent){
      try {
          mainUrl = urlString;
          storageFolder = folder;
          maxDepth = maxDept;
          MyCrawler.maxThread = maxThread;
          if (fakeUserAgent)
              userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/69.0.3497.100 Safari/537.36";
          URL url = new URL(urlString);
          URLConnection robotsUrl = new URL("http://" + url.getHost() +"/robots.txt").openConnection();
          robotsUrl.addRequestProperty("User-Agent", userAgent);
          robotsTxt = RobotsTxt.read(robotsUrl.getInputStream());
          baseUrl += robotsUrl.getURL().getHost() + "/";
          storageFolder += baseUrl.replaceAll("(^http|https)+://","");
          new File(storageFolder).mkdirs();
      } catch (IOException ex) {
          Logger.getLogger(MyCrawler.class.getName()).log(Level.SEVERE, null, ex);
      }
    }

  public void start(){
      if (mainUrl.charAt(mainUrl.length() - 1) != '/')
          mainUrl += '/';
      visitedNode.add(mainUrl);
      visit(mainUrl, 0);
  }

  private boolean shouldVisit(String url) {
      String href = url.toLowerCase();
      return (href.length() >= baseUrl.length()) &&
              href.startsWith(baseUrl) &&
              robotsTxt.query(userAgent,url) &&
              !FILTERS.matcher(href).matches();
  }

  private void visit(String url, int nodeIndex) {
      try {
          System.out.println("url: " + url);
          System.out.println("nodeIndex: " + nodeIndex);

          Document htmlDocument = Jsoup.connect(url)
                  .userAgent(userAgent)
                  .get();

          String pageName = htmlDocument.select("title").text();
          saveResult(htmlDocument.text(), pageName);
          if (nodeIndex < maxDepth){
              Elements linksElements = htmlDocument.select("a");
              for (Element e: linksElements){
                  String childNode = e.attr("abs:href");
                  if (shouldVisit(childNode) && visitedNode.add(childNode))
                    visit(childNode, nodeIndex + 1);
              }
          }
      } catch (IOException ex) {
          Logger.getLogger(MyCrawler.class.getName()).log(Level.SEVERE, null, ex);
      }
  }

  private void saveResult(String result, String pageName){
      try {
          String fileName = pageName.replaceAll("[\\\\/:*?\"<>|]", "");
          BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new  FileOutputStream(storageFolder + "/" + fileName + ".txt"),"UTF-8"));
          bw.write(result);
          bw.flush();
          bw.close();
      } catch (IOException ex) {
          Logger.getLogger(MyCrawler.class.getName()).log(Level.SEVERE, null, ex);
      }
  }
}

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class MyCrawler {
  private static final Pattern FILTERS = Pattern.compile(".*(\\.(css|js|gif|jpg|png|mp3|zip|gz))$");
  private static String storageFolder = "Results/";
  private static String baseUrl = "http://";
  private static int MAX_DEPTH = 0;
  private static int MAX_THREAD = 1;
  private static TreeSet<String> visitedNode = new TreeSet<>();
  private String mainUrl = "";
  private static String[] schemes = {"http","https"};

  public MyCrawler(String urlString, String folder, int maxDept, int maxThread){
      try {
          mainUrl = urlString;
          storageFolder = folder;
          MAX_DEPTH = maxDept;
          MAX_THREAD = maxThread;
          URL url = new URL(urlString);
          baseUrl += url.getHost() + "/";
          storageFolder += url.getHost();
          new File(storageFolder).mkdirs();
      } catch (MalformedURLException ex) {
          Logger.getLogger(MyCrawler.class.getName()).log(Level.SEVERE, null, ex);
      }
  }

  public void start(){
      if (mainUrl.charAt(mainUrl.length() - 1) != '/')
          mainUrl += '/';
      visitedNode.add(mainUrl);
      visit(mainUrl, 0);
  }

  private void getRobotsFile(){
      //download file robots.txt and parse to object
  }

  private boolean shouldVisit(String url) {
      String href = url.toLowerCase();
      //need to check with robots.txt, ignore for now
      return  (href.length() > 0) && !(href.length() > 128) && !FILTERS.matcher(href).matches() && href.startsWith(href);
  }

  private void visit(String url, int nodeIndex) {
      try {
          System.out.println("url: " + url);
          System.out.println("nodeIndex: " + nodeIndex);

          Document htmlDocument = Jsoup.connect(url)
                  .header("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8")
                  .header("Accept-Encoding","gzip, deflate")
                  .header("Accept-Language","vi,en-US;q=0.9,en;q=0.8")
                  .header("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/69.0.3497.100 Safari/537.36")
                  .get();
          String pageName = baseUrl.equals(url) ? url : url.replace(baseUrl, "");
          saveResult(htmlDocument.text(), pageName);
          if (nodeIndex < MAX_DEPTH){
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
          String filename = pageName.replaceAll("http://", "");
          int indexQuery = filename.indexOf("?");
          if (indexQuery >= 0)
              filename = filename.substring(0, indexQuery);
          filename = filename.replaceAll("^/|/$", "");
          filename = filename.replaceAll("/", "+");
          BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new  FileOutputStream(storageFolder + "/" + filename + ".txt"),"UTF-8"));
          bw.write(result);
          bw.flush();
          bw.close();
      } catch (IOException ex) {
          Logger.getLogger(MyCrawler.class.getName()).log(Level.SEVERE, null, ex);
      }
  }
}

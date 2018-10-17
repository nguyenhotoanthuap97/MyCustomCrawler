import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.url.WebURL;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Set;
import java.util.regex.Pattern;

public class MyCrawler extends WebCrawler {
  private static final Pattern FILTERS = Pattern.compile(".*(\\.(css|js|gif|jpg" + "|png|mp3|zip|gz))$");
  private String storageFolder = "Results";

  @Override
  public boolean shouldVisit(Page referringPage, WebURL url) {
    String href = url.getURL().toLowerCase();
    String referenceDomain = referringPage.getWebURL().getDomain().toLowerCase();
    return !(href.length() > 128) && !FILTERS.matcher(href).matches() && href.lastIndexOf(referenceDomain) != -1;
  }

  @Override
  public void visit(Page page) {
    String url = page.getWebURL().getURL();
    String seed = page.getWebURL().getDomain().split("[.]")[0];
    File dir = new File(storageFolder + "/" + seed);
    if (!dir.exists()) {
      dir.mkdir();
    }
    File textDir = new File(storageFolder + "/" + seed + "/text");
    if (!textDir.exists()) {
      textDir.mkdir();
    }
    File htmlDir = new File(storageFolder + "/" + seed + "/html");
    if (!htmlDir.exists()) {
      htmlDir.mkdir();
    }
    System.out.println("URL: " + url);
    if (page.getParseData() instanceof HtmlParseData) {
      HtmlParseData htmlParseData = (HtmlParseData) page.getParseData();
      String text = htmlParseData.getText();
      String html = htmlParseData.getHtml();
      Set<WebURL> links = htmlParseData.getOutgoingUrls();
      String textStoragePath = storageFolder + "/" + seed + "/text/" + ((HtmlParseData) page.getParseData()).getTitle() + ".txt";
      String htmlStoragePath = storageFolder + "/" + seed + "/html/" + ((HtmlParseData) page.getParseData()).getTitle() + ".html";

      try (BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(textStoragePath), "UTF-8"))) {
        bufferedWriter.write(text);
      } catch (IOException e) {
        e.printStackTrace();
      }
      try (BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(htmlStoragePath), "UTF-8"))) {
        bufferedWriter.write(html);
      } catch (IOException e) {
        e.printStackTrace();
      }
      System.out.println("Text length: " + text.length());
      System.out.println("Html length: " + html.length());
      System.out.println("Number of outgoing links: " + links.size());
    }
  }
}

public class Main {
    public static void main(String[] args) {
        String crawlerStorageFolder = "Results/";
        int MAX_DEPTH = 1;
        int MAX_THREAD = 7;
        String url = "http://www.phimmoi.net";
        MyCrawler crawler = new MyCrawler(url, crawlerStorageFolder, MAX_DEPTH, MAX_THREAD);
        crawler.start();
    }
}

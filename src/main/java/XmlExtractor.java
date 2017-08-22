import org.jsoup.Jsoup;
import org.testng.annotations.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Vector;
import org.w3c.dom.Node;


public class XmlExtractor  {
    private static final int NUMBER_OF_ARTICLES_FOR_EACH_RSS = 60;

    // The function takes the url of the rss feed and returns a vector of NewsArticle
    public static Vector<NewsArticle> execute(String url, String category) throws InterruptedException {

        Vector<NewsArticle> newsArticles = new Vector<>();
        SimpleDateFormat sdf=new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z");

        try {
            Document document = (Document) DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(url);

            NodeList nodes = document.getElementsByTagName("item");

            for(int i=0;i<nodes.getLength()&&i<NUMBER_OF_ARTICLES_FOR_EACH_RSS;i++) {
                Node node = nodes.item(i);

                if(node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;
                    String title = element.getElementsByTagName("title").item(0).getTextContent();
                    title=titleFilter(title);
                    if(title.length()<20){
                        continue;
                    }

                    Timestamp pubDate = new Timestamp(0);

                    try {
                        pubDate = new Timestamp(sdf.parse(element.getElementsByTagName("pubDate").item(0).getTextContent()).getTime());
                    } catch (Exception e1) {
                        sdf=new SimpleDateFormat("EEE, d MMM yyyy HH:mm Z");
                        pubDate = new Timestamp(sdf.parse(element.getElementsByTagName("pubDate").item(0).getTextContent()).getTime());
                    }
                    String description = "";
                    try {
                        description = Jsoup.parse(element.getElementsByTagName("description").item(0).getTextContent()).text();
                    } catch (Exception e1) {
                        continue;
                    }

                    if(title.equals(description)){
                        description="";
                    }
                    if(description.length()>250){
                        description=description.substring(0,250)+"...";
                    }
                    String link = element.getElementsByTagName("link").item(0).getTextContent();
                   // System.out.println(url);
                    newsArticles.add(new NewsArticle(title,description,link,pubDate,category));
                   // System.out.println(title);
                }
            }

        } catch (Exception e) {
             e.printStackTrace();
        }
        return newsArticles;
    }

    @Test
    public void test() throws InterruptedException {
        Vector<NewsArticle> newsArticles = execute("http://www.politico.com/rss/politics08.xml","business");
    }

    private static String titleFilter(String title){
        for(int i=0;i<title.length();i++){
            if(title.charAt(i)!=' '&&title.charAt(i)!='\n'&&title.charAt(i)!='\t'){
                return title.substring(i);
            }
        }
        return "";
    }
}

import org.jsoup.Jsoup;
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

    // The function takes the url of the rss feed and returns a vector of NewsArticle
    public static Vector<NewsArticle> execute(String url, String category) throws InterruptedException {

        Vector<NewsArticle> newsArticles = new Vector<>();
        try {
            Document document = (Document) DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(url);

            NodeList nodes = document.getElementsByTagName("item");

            for(int i=0;i<nodes.getLength()&&i<50;i++) {
                Node node = nodes.item(i);

                if(node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;
                    String title = element.getElementsByTagName("title").item(0).getTextContent();
                    if(title.length()<20){
                        continue;
                    }
                    SimpleDateFormat sdf=new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z");
                    Timestamp pubDate = new Timestamp(sdf.parse(element.getElementsByTagName("pubDate").item(0).getTextContent()).getTime());
                    String description = Jsoup.parse(element.getElementsByTagName("description").item(0).getTextContent()).text();
                    if(description.length()>1020){
                        description=description.substring(0,1020);
                    }
                    String link = element.getElementsByTagName("link").item(0).getTextContent();
                    newsArticles.add(new NewsArticle(title,description,link,pubDate,category));
                }
            }

        } catch (Exception e) {
             e.printStackTrace();
        }
        return newsArticles;
    }
}

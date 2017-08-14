/**
 * Created by yash.raj on 09/08/17.
 */
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.util.Vector;
import org.w3c.dom.Node;


public class XmlExtractor {

    // The function takes the url of the rss feed and returns a vector of NewsArticle
    public Vector<NewsArticle> execute(String url, String category) {
        String tags[] = {"title", "pubDate","link"};
        String title = null;
        String pubdate = null;
        String link = null;
        String desc = null;
        String cat = category;

        Vector<NewsArticle> newsArticle = new Vector<NewsArticle>();
        try {
            DocumentBuilderFactory inputFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = inputFactory.newDocumentBuilder();
            Document document = (Document) builder.parse(url);

          //  System.out.println(document.getDocumentElement().getNodeName());
            NodeList nodes = document.getElementsByTagName("item");

          //  System.out.println(nodes.getLength());

            for(int i=0;i<nodes.getLength();i++) {
                Node node = nodes.item(i);
              //  System.out.println("Current element : " + node.getNodeName());

                if(node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;

                    title = element.getElementsByTagName("title").item(0).getTextContent();
                    pubdate = new utility().toDate(element.getElementsByTagName("pubDate").item(0).getTextContent());


                 //   System.out.println(pubdate);
                    link = element.getElementsByTagName("link").item(0).getTextContent();
                    newsArticle.add(new NewsArticle(title,link,pubdate,cat));
                }
            }

        } catch (Exception e) {
             e.printStackTrace();
        }

        return newsArticle;
    }
/*
    public static void main(String[] args) {
        Vector<NewsArticle> vec = new XmlExtractor().execute("http://timesofindia.indiatimes.com/rssfeedstopstories.cms","hhdd");
    }
*/
}

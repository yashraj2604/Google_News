import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomAttr;
import com.gargoylesoftware.htmlunit.html.HtmlImage;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import javax.sound.midi.Soundbank;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.*;
import java.util.List;
import java.util.Vector;
import org.junit.Test;

/**
 * Created by yash.raj on 19/08/17.
 */
public class ImageScraper {


    // This function is used to scrape the first image on google image according to a given search

    private static String googleImageScraper(String query) {
        String userAgent = "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/53.0.2785.116 Safari/537.36";
        String url = "https://www.google.com/search?site=imghp&tbm=isch&source=hp&q="+query+"&gws_rd=cr";

        try {
            Document doc = Jsoup.connect(url).userAgent(userAgent).referrer("https://www.google.com/").get();

            Elements elements = doc.select("div.rg_meta");

            String s = elements.get(0).childNode(0).toString();

            JSONObject json = new JSONObject(s);

            String url_first_image = json.getString("ou");

            return url_first_image;

        } catch (Exception e) {
            return "";
        }
    }
    // This is working currently
    private static String imageScraperTimesofindia(String url,WebClient webClient) throws IOException {

        try {
            HtmlPage currentPage = (HtmlPage) webClient.getPage(new URL(url));

            // System.out.println(currentPage.getWebResponse().getStatusCode());

            // System.out.println(currentPage.getWebResponse().getStatusMessage());
            HtmlImage image = (HtmlImage) currentPage.getByXPath("//section[@class='highlight clearfix']/img").get(0);


            String imageurl = currentPage.getFullyQualifiedUrl(image.getSrcAttribute()).toString();

            return imageurl;
        } catch (Exception e) {
            return "";
        }

    }

    // This is working currently
    private static String imageScraperNdtvprofit(String url, WebClient webClient) throws IOException {

        try {
            HtmlPage currentPage = (HtmlPage) webClient.getPage(new URL(url));

            // System.out.println(currentPage.getWebResponse().getStatusCode());

            //  System.out.println(currentPage.getWebResponse().getStatusMessage());

            HtmlImage image = (HtmlImage) currentPage.getFirstByXPath("//div[@class='whosaid_top_mainimg_cont']/img");


            String imageurl = currentPage.getFullyQualifiedUrl(image.getSrcAttribute()).toString();

            return imageurl;
        } catch (Exception e) {
            return "";
        }
    }

    // This is working currently
    private static String imageScrapernytimes(String url,WebClient webClient) throws IOException {

        try {
            HtmlPage currentPage = (HtmlPage) webClient.getPage(new URL(url));
            DomAttr domAttr = (DomAttr) currentPage.getByXPath("//div[@class='story-body story-body-1']/figure/@id").get(0);

            String check  = domAttr.getNodeValue().toString();
            //   System.out.println(check);
            HtmlImage image = (HtmlImage) currentPage.getByXPath("//div[@class='story-body story-body-1']/figure[@id='" + check + "']/div[@class='image']/img").get(0);

            String  imageurl = currentPage.getFullyQualifiedUrl(image.getSrcAttribute()).toString();

            return imageurl;
        } catch(Exception e) {

            try {
                HtmlPage currentPage = (HtmlPage) webClient.getPage(new URL(url));
                DomAttr domAttr = (DomAttr) currentPage.getByXPath("//div[@class='story-body story-body-2']/figure/@id").get(0);

                String check  = domAttr.getNodeValue().toString();
                //  System.out.println(check);
                HtmlImage image = (HtmlImage) currentPage.getByXPath("//div[@class='story-body story-body-2']/figure[@id='" + check + "']/div[@class='image']/img").get(0);

                String imageurl = currentPage.getFullyQualifiedUrl(image.getSrcAttribute()).toString();

                return imageurl;
            } catch (Exception e1) {
                return "";
            }
        }

    }

    private static String imageScraperbbc(String url, WebClient webClient) throws IOException {

        try {
            HtmlPage currentPage = (HtmlPage) webClient.getPage(new URL(url));

            HtmlImage image = (HtmlImage) currentPage.getByXPath("//div[@class='story-body__inner']/figure[@class='media-landscape has-caption full-width lead']/span[@class='image-and-copyright-container']/img").get(0);

            String imageurl = currentPage.getFullyQualifiedUrl(image.getSrcAttribute()).toString();

            return imageurl;
        } catch(Exception e) {

            try {
                HtmlPage currentPage = (HtmlPage) webClient.getPage(new URL(url));

                HtmlImage image = (HtmlImage) currentPage.getByXPath("//div[@class='story-body__inner']/figure[@class='media-landscape no-caption full-width lead']/span[@class='image-and-copyright-container']/img").get(0);

                String  imageurl = currentPage.getFullyQualifiedUrl(image.getSrcAttribute()).toString();

                return imageurl;
            } catch(Exception e1) {
                return "";
            }
        }

    }



/*
    public String imageScraperBusinessinsider(String url) throws IOException, InterruptedException {

        //WebClient webClient = new WebClient(Opera);
        WebClient webClient = new WebClient();
        webClient.getOptions().setCssEnabled(false);
        webClient.getOptions().setJavaScriptEnabled(false);
        HtmlPage currentPage = (HtmlPage) webClient.getPage(new URL(url));

        Thread.sleep(10 * 1000 );

        System.out.println(currentPage.getWebResponse().getStatusCode());

        System.out.println(currentPage.getWebResponse().getStatusMessage());

        List<HtmlImage> image = (List<HtmlImage>) currentPage.getByXPath("//div[@id='meetingImg']/img");
        System.out.println(image);

        URL imageurl = currentPage.getFullyQualifiedUrl(image.get(0).getSrcAttribute());

        return imageurl.toString();
    }
*/

/*
    public String imageScraperdeccanherald(String url) throws IOException {
        //WebClient webClient = new WebClient(Opera);
        WebClient webClient = new WebClient();
        webClient.getOptions().setCssEnabled(false);
        webClient.getOptions().setJavaScriptEnabled(false);
        HtmlPage currentPage = (HtmlPage) webClient.getPage(new URL(url));

        System.out.println(currentPage.getWebResponse().getStatusCode());

        System.out.println(currentPage.getWebResponse().getStatusMessage());

        HtmlImage image = (HtmlImage) currentPage.getFirstByXPath("//div[@class='new_article_image _hoverrDone']/img");


        URL imageurl = currentPage.getFullyQualifiedUrl(image.getSrcAttribute());

        return imageurl.toString();
    }
*/


    /*
    public URL imageScrapercnn(String url) throws IOException {

        WebClient webClient = new WebClient();
        webClient.getOptions().setCssEnabled(false);
        webClient.getOptions().setJavaScriptEnabled(false);

        /*
        String status_message = currentPage.getWebResponse().getStatusMessage();

        if(!status_message.equals("OK"))
            return null;

        try {
            HtmlPage currentPage = (HtmlPage) webClient.getPage(new URL(url));

            HtmlImage image = (HtmlImage) currentPage.getByXPath("//section[@class='zn zn-large-media zn-body zn--idx-0 zn--ordinary zn-has-one-container']/div[@class='l-container']/img").get(0);

            URL imageurl = currentPage.getFullyQualifiedUrl(image.getSrcAttribute());

            return imageurl;
        } catch(Exception e) {
                return null;
        }

    }

*/
    public static String scrapeImages(String url,WebClient webClient,String title) throws IOException {
        String imageurl = "";
        String timesofindia = "timesofindia";
        String ndtvprofit = "profit";
        String nytimes = "nytimes";
        String bbc = "bbc";


        if(url.split("//")[1].split("\\.")[0].equals(timesofindia)) {

            imageurl = ImageScraper.imageScraperTimesofindia(url,webClient);

            if(imageurl.equals("")) {
                imageurl = ImageScraper.googleImageScraper(title);
            }
        }
        else if(url.split("//")[1].split("\\.")[0].equals(ndtvprofit)) {

            imageurl = ImageScraper.imageScraperNdtvprofit(url,webClient);

            if(imageurl.equals("")) {
                imageurl = ImageScraper.googleImageScraper(title);
            }
        }
        else if(url.split("//")[1].split("\\.")[1].equals(nytimes)) {
            imageurl = ImageScraper.imageScrapernytimes(url,webClient);

            if(imageurl.equals("")) {
                imageurl = ImageScraper.googleImageScraper(title);
            }
        }
        else if(url.split("//")[1].split("\\.")[1].equals(bbc)) {
            imageurl = ImageScraper.imageScraperbbc(url,webClient);

            if(imageurl.equals("")) {
                imageurl = ImageScraper.googleImageScraper(title);
            }
        }
        else {
            imageurl = ImageScraper.googleImageScraper(title);
        }


        return imageurl;
    }


    @Test
    public void testimages() throws IOException {

        WebClient webClient = new WebClient();
        webClient.getOptions().setCssEnabled(false);
        webClient.getOptions().setJavaScriptEnabled(false);



        String title = "Thompson: McGregor doesn't realize that his real opponent is not Floyd Mayweather";
        String url="https://www.nysadvtimes.com/2017/08/19/sports/baseball/an-eye-for-talent-a-place-in-history-making-her-mark-in-baseball-scouting.html?partner=rss&emc=rss";
        System.out.println(ImageScraper.scrapeImages(url,webClient,title));

    }

/*
    public static void main(String[] args) throws IOException, InterruptedException, SQLException, ClassNotFoundException {


        ImageScraper imageScraper = new ImageScraper();

        WebClient webClient = new WebClient();
        webClient.getOptions().setCssEnabled(false);
        webClient.getOptions().setJavaScriptEnabled(false);



        String title = "Thompson: McGregor doesn't realize that his real opponent is not Floyd Mayweather";
        String url="https://www.nysadvtimes.com/2017/08/19/sports/baseball/an-eye-for-talent-a-place-in-history-making-her-mark-in-baseball-scouting.html?partner=rss&emc=rss";
        System.out.println(imageScraper.scrapeImages(url,webClient,title));

        Class.forName("com.mysql.jdbc.Driver");
        Connection con= DriverManager.getConnection(
                "jdbc:mysql://MMLDVSIDHART:3306/newsv2","root","qwerty123");



    }
*/
}




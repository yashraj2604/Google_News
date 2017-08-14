import de.l3s.boilerpipe.BoilerpipeProcessingException;
import de.l3s.boilerpipe.document.TextDocument;
import de.l3s.boilerpipe.extractors.ArticleExtractor;
import de.l3s.boilerpipe.extractors.CommonExtractors;
import de.l3s.boilerpipe.sax.BoilerpipeSAXInput;
import de.l3s.boilerpipe.sax.HTMLDocument;
import de.l3s.boilerpipe.sax.HTMLFetcher;

import java.net.URL;
import org.testng.annotations.Test;
/**
 * Created by Sidharth.na on 8/9/2017.
 */
public class BoilerPipe  {

//    @Test
//    public void doit(){
//        System.out.println(BoilerPipe.getContent("http://abcnews.go.com/WNT/video/car-sales-hit-summer-slump-dropping-july-5th-49043913"));
//    }
    public static String getContent(String url) {
        try {
            final HTMLDocument htmlDoc = HTMLFetcher.fetch(new URL(url));
            final TextDocument doc = new BoilerpipeSAXInput(htmlDoc.toInputSource()).getTextDocument();
            String content = CommonExtractors.ARTICLE_EXTRACTOR.getText(doc);
            return filter(content);
        }catch(Exception e){
            System.out.println("Error in URL "+url);
            e.printStackTrace();
        }
        return "";
    }

    private static String filter(String inp){
        StringBuilder out=new StringBuilder("");
        for(int i=0;i<inp.length();i++){
            char c=inp.charAt(i);
            if(c>='0' && c<='9'){
                out.append(c);
            }else if(c>='a' && c<='z'){
                out.append(c);
            }else if(c>='A' && c<='Z'){
                out.append((char)(c+('a'-'A')));
            }else if(c==' '){
                out.append(c);
            }else if(c=='\n'){
                out.append(c);
            }
        }
        return out.toString();
    }
}

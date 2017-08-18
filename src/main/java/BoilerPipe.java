import de.l3s.boilerpipe.document.TextDocument;
import de.l3s.boilerpipe.extractors.CommonExtractors;
import de.l3s.boilerpipe.sax.BoilerpipeSAXInput;
import de.l3s.boilerpipe.sax.HTMLDocument;
import de.l3s.boilerpipe.sax.HTMLFetcher;
import java.net.URL;
public class BoilerPipe  {

    public static String getContent(String url) {
        try {
            HTMLDocument htmlDoc = HTMLFetcher.fetch(new URL(url));
            TextDocument doc = new BoilerpipeSAXInput(htmlDoc.toInputSource()).getTextDocument();
            String content = CommonExtractors.ARTICLE_EXTRACTOR.getText(doc);
            return filter(content);
        }catch(Exception e){
            System.out.println("Error in URL "+url);
            return "";
        }
    }

    private static String filter(String inp){
        StringBuilder out=new StringBuilder("");
        char last='\n';
        for(int i=0;i<inp.length();i++){
            char c=inp.charAt(i);
            if(c>='0' && c<='9'){
                out.append(c);
            }else if(c>='a' && c<='z'){
                out.append(c);
            }else if(c>='A' && c<='Z'){
                out.append((char)(c+('a'-'A')));
            }else if(c==' '){
                if(last!=' '&&last!='\n'){
                    out.append(c);
                }
            }else if(c=='\n'){

                if(last !='\n'){
                    if(last==' '){
                        out.setCharAt(out.length()-1,'\n');
                    }else{
                        out.append(c);
                    }
                }
            }
            last=c;
        }
        return out.toString();
    }
}

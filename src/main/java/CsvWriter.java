/**
 * Created by yash.raj on 10/08/17.
 */
import com.sun.org.apache.bcel.internal.generic.NEW;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Vector;

public class CsvWriter {

    public void writetocsv(Vector<NewsArticle> vec, String path) throws FileNotFoundException{

        PrintWriter pw = new PrintWriter(new File(path));
        StringBuilder sb = new StringBuilder();
        sb.append("Title");
        sb.append(',');
        sb.append("url");
        sb.append(',');
        sb.append(("pubdate"));
        sb.append(',');
        sb.append(("category"));
        sb.append('\n');

        for(int i=0;i<vec.size();i++) {
           // StringBuilder sb = new StringBuilder();
            sb.append(vec.get(i).getTitle());
            sb.append(',');
            sb.append(vec.get(i).getUrl());
            sb.append(',');
            sb.append(vec.get(i).getPubDate());
            sb.append(',');
            sb.append(vec.get(i).getCategory());
            sb.append('\n');

        }
        pw.write(sb.toString());
        pw.close();
    }

}

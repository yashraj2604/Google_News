import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Vector;

public class FileExtractor {

    public static Vector<String> extractRSSFeeds(String filepath) {

        Vector<String> vector = new Vector<>();

        try {
            FileReader  fr = new FileReader(filepath);
            BufferedReader br = new BufferedReader(fr);

            String currentLine;

            while((currentLine = br.readLine()) != null) {
                vector.add(currentLine);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return vector;
    }
}

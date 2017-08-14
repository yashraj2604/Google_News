/**
 * Created by yash.raj on 09/08/17.
 */
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Vector;

public class FileExtractor {

    public Vector<String> run(String filepath) {

        BufferedReader br = null;
        FileReader fr = null;
        Vector<String> vector = new Vector<String>();

        try {
            fr = new FileReader(filepath);
            br = new BufferedReader(fr);

            String currentline;

            while((currentline = br.readLine()) != null) {
                vector.add(currentline);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return vector;
    }
}

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class Cluster {

    private static final long CLUSTER_CONSTANT = TimeUnit.HOURS.toMillis(5);
    private static final double COSINE_THRESHOLD = 0.25;
    private static final double CLUSTER_COSINE_THRESHOLD = 0.05;
    private static final double DUPLICATE_THRESHOLD = 0.85;

    public static void addToCluster(int article_id, HashMap<String,Double> hm, String category, Connection con, long articleScore) throws SQLException{
        PreparedStatement ps;
        ResultSet result;
        String query;
        HashMap<String,Double> hmWord=new HashMap<>();
        ps=con.prepareStatement("SELECT * FROM "+category+"_words");
        result=ps.executeQuery();
        while(result.next()){
            hmWord.put(result.getString("word"),(double)result.getInt("documentFrequency"));
        }
        Double noOfDocuments=hmWord.get("#");
        for(Map.Entry<String,Double> e:hmWord.entrySet()){
            e.setValue(calculateIDF(e.getValue(),noOfDocuments));
        }
        hm= updateIDF(hm,hmWord);

        query="SELECT clusterID FROM "+category+"_cluster_score ORDER BY clusterScore DESC LIMIT 20 ";
        ps=con.prepareStatement(query);
        result=ps.executeQuery();
        ArrayList<Integer> clusterIDs=new ArrayList<>();
        while(result.next()){
            clusterIDs.add(result.getInt("clusterID"));
        }
        int bestMatchCluster=-1;
        double bestMatchSimilarity=-1;
        for (Integer clusterID : clusterIDs) {
            query = "SELECT id FROM " + category + "_cluster WHERE clusterID=" + clusterID;
            ps = con.prepareStatement(query);
            result = ps.executeQuery();
            ArrayList<Integer> articleIDs = new ArrayList<>();
            while (result.next()) {
                articleIDs.add(result.getInt("id"));
            }
            ArrayList<Double> similarities=new ArrayList<>();

            for (Integer curArticleID : articleIDs) {
                query = "SELECT content FROM " + category + "_content where id=" + curArticleID;
                ps = con.prepareStatement(query);
                result = ps.executeQuery();
                if (result.next()) {
                    String curContent = result.getString("content");
                    HashMap<String, Double> hmcur = getHashMap(curContent);
                    hmcur = updateIDF(hmcur, hmWord);
                    double sim = similarity(hm, hmcur);
                    if(sim>DUPLICATE_THRESHOLD){
                        query="SELECT url FROM "+category + " WHERE id="+article_id+" OR id = " + curArticleID;
                        result=ps.executeQuery(query);
                        result.next();
                        String dup1=result.getString("url");
                        result.next();
                        String dup2=result.getString("url");
                        System.out.println("These articles are duplicate "+dup1+" "+dup2 + " with similarity " + sim);
                        removeFromDatabase(article_id, category,con);
                        return;
                    }
                    similarities.add(sim);

                }
            }
            boolean flag=true;
            for(Double sim:similarities){
                if(sim<CLUSTER_COSINE_THRESHOLD){
                    flag=false;
                }
            }
            if(flag){
                for(Double sim:similarities){
                    if (sim > COSINE_THRESHOLD && sim>bestMatchSimilarity) {
                        bestMatchCluster = clusterID;
                        bestMatchSimilarity = sim;
                    }
                }
            }
        }
        if(bestMatchCluster!=-1){
            query="INSERT INTO "+category+"_cluster (id,articleScore, clusterID) value("+article_id+", "+articleScore+", "+bestMatchCluster+")";
            ps=con.prepareStatement(query);
            ps.execute();
            query="UPDATE "+category+"_cluster_score set clusterScore = "+articleScore+"+ numberOfArticles * "+CLUSTER_CONSTANT+" where clusterID = "+bestMatchCluster;
            ps=con.prepareStatement(query);
            ps.executeUpdate();
            query="UPDATE "+category+"_cluster_score set numberOfArticles = 1+ numberOfArticles where clusterID = "+bestMatchCluster;
            ps=con.prepareStatement(query);
            ps.executeUpdate();
        }else{
            query="SELECT MAX(clusterID) FROM "+category+"_cluster_score";
            ps=con.prepareStatement(query);
            result=ps.executeQuery();
            if(result.next()){
                query="INSERT INTO "+category+"_cluster_score (clusterScore,numberOfArticles) value("+(articleScore+CLUSTER_CONSTANT)+",1)";
                ps=con.prepareStatement(query);
                ps.execute();

                bestMatchCluster=result.getInt("max(clusterID)")+1;
                query="INSERT INTO "+category+"_cluster (id,articleScore, clusterID) value("+article_id+", "+articleScore+", "+bestMatchCluster+")";
                ps=con.prepareStatement(query);
                ps.execute();

            }
        }
    }

    private static void removeFromDatabase(int articleID, String category, Connection con) throws SQLException{
        PreparedStatement ps;
        String query = "DELETE FROM "+category + "_content WHERE id = "+articleID;
        ps=con.prepareStatement(query);
        ps.execute();
        query= "DELETE FROM "+category + " WHERE id = "+articleID;
        ps=con.prepareStatement(query);
        ps.execute();
    }

    private static double similarity(HashMap<String,Double> hm1,HashMap<String,Double> hm2){
        double dot=0,mod1=0,mod2=0;
        for(Map.Entry<String,Double> e:hm1.entrySet()){
            mod1+=e.getValue()*e.getValue();
            if(hm2.get(e.getKey())!=null){
                dot+=hm2.get(e.getKey())*e.getValue();
            }
        }
        for(Map.Entry<String,Double> e:hm2.entrySet()) {
            mod2 += e.getValue() * e.getValue();
        }
        return dot/(Math.sqrt(mod1)*Math.sqrt(mod2));
    }

    private static HashMap<String,Double> updateIDF(HashMap<String,Double> hm, HashMap<String,Double> hmWord){
        ArrayList<String > badWords =new ArrayList<>();
        for(Map.Entry<String, Double> e:hm.entrySet()) {
            try {
                e.setValue(e.getValue() * hmWord.get(e.getKey()));
            }catch(NullPointerException exp){
                System.out.println("Word not found "+e.getKey());
                badWords.add(e.getKey());
            }
        }
        for (String badWord : badWords) {
            hm.remove(badWord);
        }
        return hm;
    }

    private static HashMap<String,Double> getHashMap(String content){
        HashMap<String,Double> hm=new HashMap<>();
        Scanner sc=new Scanner(content);
        while(sc.hasNext()){
            String s=sc.next();
            if(s.length()>31){
                continue;
            }
            if(hm.get(s)==null){

                hm.put(s,1.0);
            }else{
                hm.put(s,hm.get(s)+1);
            }
        }
        sc.close();
        return hm;
    }


    private static double calculateIDF(Double wordInDocuments, Double totalDocuments){
        return Math.log((totalDocuments/wordInDocuments));
    }
}

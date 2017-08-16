import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
public class Cluster {

    public static final long CLUSTER_CONSTANT = 18000000;
    public static final double COSINE_THRESHOLD=0.2;
    public void addToCluster(int article_id, HashMap<String,Double> hm, String category, Connection con, long articleScore) throws SQLException{
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
        int bestMatchArticle=-1;
        int bestMatchCluster=-1;
        double bestMatchSimilarity=-1;
        for(int i=0;i<clusterIDs.size();i++){
            int clusterID=clusterIDs.get(i);
            query="SELECT id FROM "+category+"_cluster WHERE clusterID="+clusterID;
            ps=con.prepareStatement(query);
            result=ps.executeQuery();
            ArrayList<Integer> articleIDs=new ArrayList<>();
            while(result.next()){
                articleIDs.add(result.getInt("id"));
            }
            for(int j=0;j<articleIDs.size();j++){
                int curArticleID=articleIDs.get(j);
                query="SELECT content FROM "+category+"_content where id="+curArticleID;
                ps=con.prepareStatement(query);
                result=ps.executeQuery();
                if(result.next()){
                    String curContent=result.getString("content");
                    HashMap<String,Double> hmcur=getHashmap(curContent);
                    hmcur=updateIDF(hmcur,hmWord);
                    double sim=similarity(hm,hmcur);
                    if(sim>COSINE_THRESHOLD){
//                        System.out.println("these articles of category "+category+" are similar "+article_id+" "+curArticleID +" with similarity "+sim);
                        if(sim>bestMatchSimilarity){
                            bestMatchArticle=curArticleID;
                            bestMatchCluster=clusterID;
                            bestMatchSimilarity=sim;
                        }
                    }
                }
            }
        }
        if(bestMatchCluster!=-1){
            System.out.println("Adding article "+article_id+" to cluster "+bestMatchCluster+" due to similarity "+bestMatchSimilarity+" with article "+bestMatchArticle);
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

    private double similarity(HashMap<String,Double> hm1,HashMap<String,Double> hm2){
        double dot=0,mod1=0,mod2=0;
        for(Map.Entry<String,Double> e:hm1.entrySet()){
            mod1+=e.getValue()*e.getValue();
            if(hm2.get(e.getKey())!=null){
                dot+=hm2.get(e.getKey())*e.getValue();
            }
//            System.out.println(mod1+" "+dot);
        }
        for(Map.Entry<String,Double> e:hm2.entrySet()) {
            mod2 += e.getValue() * e.getValue();
        }
//        System.out.println(dot+" "+mod1+" "+mod2);
        return dot/(Math.sqrt(mod1)*Math.sqrt(mod2));

    }

    private HashMap<String,Double> updateIDF(HashMap<String,Double> hm, HashMap<String,Double> hmWord){
        ArrayList<String > temp =new ArrayList<>();
        for(Map.Entry<String, Double> e:hm.entrySet()) {
            try {
                e.setValue(e.getValue() * hmWord.get(e.getKey()));
            }catch(NullPointerException exp){
                System.out.println("Word not found "+e.getKey());
                temp.add(e.getKey());
            }
        }
        for(int i=0;i<temp.size();i++){
            hm.remove(temp.get(i));
        }
        return hm;
    }

//    private void add(String word)throws SQLException{
//        Connection con= DriverManager.getConnection(
//                "jdbc:mysql://localhost:3306/googlenews?user=root&password=qwerty123");
//        PreparedStatement ps;
//        ps=con.prepareStatement("select * from words where word='"+word+"'");
//        ResultSet result=ps.executeQuery();
//        if(result.next()){
//            ps=con.prepareStatement("update words set documentFrequency = documentFrequency + 1 where word = '"+word+"'");
//            ps.executeUpdate();
//        }else{
//            try {
//                ps = con.prepareStatement("INSERT INTO words (word , documentFrequency) values('" + word + "',1)");
//                ps.executeUpdate();
//            }catch (Exception e){
//                System.out.println("Word too big "+word);
//            }
//        }
//        con.close();
//    }

    private HashMap<String,Double> getHashmap(String content){
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


    private double calculateIDF(Double wordInDocuments, Double totalDocuments){
        return Math.log((totalDocuments/wordInDocuments));
    }
}

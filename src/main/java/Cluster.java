import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Cluster {

    public  Cluster(String url) throws SQLException{


        String content=BoilerPipe.getContent(url);
        HashMap<String,Double> hm=getHashmap(content);
        for(Map.Entry<String,Double> e:hm.entrySet()){
            add(e.getKey());
        }
        add("#");



        Connection con= DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/googlenews?user=root&password=qwerty123");
        PreparedStatement ps;
        ResultSet result;
        String query="SELECT * FROM business where url = '"+url+"'";
        ps=con.prepareStatement(query);
        result=ps.executeQuery();
        int articleID=0;
        if(result.next()) {
            articleID=result.getInt("id");
        }


        HashMap<String,Double> hmWord=new HashMap<>();
        ps=con.prepareStatement("SELECT * FROM words");
        result=ps.executeQuery();
        while(result.next()){
            hmWord.put(result.getString("word"),(double)result.getInt("documentFrequency"));
        }
        Double noOfDocuments=hmWord.get("#");
        for(Map.Entry<String,Double> e:hmWord.entrySet()){
            e.setValue(calculateIDF(e.getValue(),noOfDocuments));
        }
        hm= updateIDF(hm,hmWord);

        query="SELECT id,url FROM business ";
        ps=con.prepareStatement(query);
        result=ps.executeQuery();

        while(result.next()){
            int curID=result.getInt("id");
            if(curID==articleID){
                continue;
            }
            String curContent=BoilerPipe.getContent(result.getString("url"));
            HashMap<String,Double> hmcur=getHashmap(curContent);
            hmcur=updateIDF(hmcur,hmWord);
            if(similarity(hm,hmcur)){
                System.out.println("these articles are similar "+articleID+" "+curID);
            }
        }
        con.close();
    }

    private boolean similarity(HashMap<String,Double> hm1,HashMap<String,Double> hm2){
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
        return dot/(Math.sqrt(mod1)*Math.sqrt(mod2))>0.15;

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

    private void add(String word)throws SQLException{
        Connection con= DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/googlenews?user=root&password=qwerty123");
        PreparedStatement ps;
        ps=con.prepareStatement("select * from words where word='"+word+"'");
        ResultSet result=ps.executeQuery();
        if(result.next()){
            ps=con.prepareStatement("update words set documentFrequency = documentFrequency + 1 where word = '"+word+"'");
            ps.executeUpdate();
        }else{
            try {
                ps = con.prepareStatement("INSERT INTO words (word , documentFrequency) values('" + word + "',1)");
                ps.executeUpdate();
            }catch (Exception e){
                System.out.println("Word too big "+word);
            }
        }
        con.close();
    }
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
//        return Math.log(1+(totalDocuments/wordInDocuments));
        return Math.log((totalDocuments/wordInDocuments));
    }
}

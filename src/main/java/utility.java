/**
 * Created by yash.raj on 10/08/17.
 */
public class utility {

    public String toDate(String d) {

        StringBuilder sb = new StringBuilder();

        String[] tok = d.split(" ");
        // Code for parsing the month and year has not been implemented, we are taking data only for the month of august and year is 2017
        sb.append("2017-");
        if(tok[2].equals("Aug")){
            sb.append("08-");
        }else{
            sb.append("07-");
        }
        sb.append(tok[1]+" "+tok[4]);



        return sb.toString();
    }

}
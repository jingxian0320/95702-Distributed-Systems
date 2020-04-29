import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JsonHelper {

    //get the feature from the json string (for server)
    public static String getField(String json, String field){
        String tempString = "";
        Pattern p = Pattern.compile("\""+field+"\":\"([^\"]*)(\"[,|}])");
        Matcher m = p.matcher(json);
        if(m.find()) {
            //System.out.println(m.group());
            tempString = m.group(1);
        }

        return tempString;
    }


    //get the Msg from json string (for client)
    public static String getMsg(String json){
        String result = json.split(":",2)[1];
        result = result.substring(1,result.length()-3);
        return result;
    }
    public static void main(java.lang.String[] args) {

        //String m = "{\"ClientId\":\"9837kwergf\",\"PublicKey\":\"27859283+298374598237\",\"OperationId\":\"5\",\"Arg1\":\"faoiewjf\",\"Signature\":\"23452489\"}";
        String m = "{\"Msg\":\"Current size of chain: 1\n" +
                "Current hashes per second by this machine: 617665\n" +
                "Difficulty of most recent block: 2\n" +
                "Nonce for most recent block: 86\n" +
                "Chain hash: 007457C2CE7B0D88073EBD975C16D451000337F5F11E2743E4F0BA43E03156D8\n" +
                "\"}\n";
        System.out.println(m);
        System.out.println(getMsg(m));
        //System.out.println(getMessage(m, "ClientId"));
        //System.out.println(getMessage(m, "PublicKey"));
        //System.out.println(getMessage(m, "OperationId"));
        //System.out.println(getMessage(m, "Arg1"));
        //System.out.println(getMessage(m, "Signature"));
    }

}

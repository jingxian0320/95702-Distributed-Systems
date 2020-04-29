import com.mongodb.client.*;

import org.bson.Document;
import java.util.Iterator;
import java.util.Scanner;


public class MongoDBTest {
    public static void main(String [] args){
        MongoClient mongoClient = MongoClients.create("mongodb+srv://admin:00000000@cluster0-oh0ho.mongodb.net/test?retryWrites=true&w=majority");
        MongoDatabase db = mongoClient.getDatabase("test");
        MongoCollection table = db.getCollection("name");
        Scanner s = new Scanner(System.in);
        System.out.println("Please provide a name to store in the DB: ");
        String name = s.nextLine();
        s.close();
        Document document = new Document("name", name);
        table.insertOne(document);

        // Getting the iterable object
        System.out.println("Displaying all documents currently stored in the database:");
        FindIterable<Document> iterDoc = table.find();
        int i = 1;

        // Getting the iterator
        Iterator it = iterDoc.iterator();

        while (it.hasNext()) {
            System.out.println(it.next());
            i++;
        }
    }
}

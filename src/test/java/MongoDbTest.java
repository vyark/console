import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Indexes;
import com.mongodb.client.model.Projections;
import org.bson.Document;
import org.junit.Before;
import org.junit.Test;

import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Updates.*;
import static org.junit.Assert.*;

public class MongoDbTest {
    private MongoClient mongo;
    private MongoCredential credential;
    private MongoDatabase database;
    private MongoCollection<Document> taskCollection;
    private List<Document> documents;

    @Before
    public void init() {
        mongo = new MongoClient("localhost", 27017);
        mongo.dropDatabase("database");

        credential = MongoCredential.createCredential("user", "database",
                "password".toCharArray());
        database = mongo.getDatabase("database");
        taskCollection = database.getCollection("task");

        insertData();
    }

    @Test
    public void displayAllTasks() {
        FindIterable<Document> iterDoc = taskCollection.find();
        int i = 1;
        Iterator it = iterDoc.iterator();
        while (it.hasNext()) {
            assertEquals(it.next(), documents.get(i-1));
            i++;
        }
    }

    @Test
    public void displayOverdueTasks() {
        List results = new ArrayList<>();
        taskCollection.find(gte("deadline", "2022-03-17")).into(results);

        assertEquals(results.size(), 5);
    }

    @Test
    public void displayAllTasksWithTheSpecificCategory() {
        Document query = new Document("category", "test");
        List results = new ArrayList<>();
        taskCollection.find(query).into(results);

        assertEquals(results.size(), 1);
    }


    @Test
    public void displayAllSubtasksRelatedToTasksWithTheSpecificCategory() {
        List results = new ArrayList<>();
        taskCollection.find(eq("category", "sql")).projection(Projections.fields(Projections.include("subtasks"))).into(results);

        assertEquals(results.size(), 1);
    }

    @Test
    public void performInsertUpdateDeleteOfTheTask() {
        Document document = new Document("taskId", 6)
                .append("dateOfCreation", new Date())
                .append("deadline", "2022-04-18")
                .append("name", "windows")
                .append("description", "operating system")
                .append("category", "system");
        taskCollection.insertOne(document);

        Document query = new Document("taskId", 6);
        List insertResults = new ArrayList<>();
        taskCollection.find(query).into(insertResults);
        assertEquals(insertResults.size(), 1);

        taskCollection.updateOne(eq("taskId", 6), new Document("$set", new Document("deadline", "2022-05-01")));

        List updateResults = new ArrayList<>();
        taskCollection.find(query).into(updateResults);
        assertEquals(updateResults.size(), 1);

        taskCollection.deleteOne(query);
    }

    @Test
    public void performInsertUpdateDeleteAllSubtasksOfTheGivenTask() {
        Document document = new Document("subtaskId", 4)
                .append("name", "hibernate")
                .append("description", "mapping tool");
        taskCollection.updateOne(eq("taskId", 3), push("subtasks",document));

        Document query = new Document("taskId", 3);
        List results = new ArrayList<>();
        taskCollection.find(query).into(results);
        assertEquals(results.size(), 1);

        taskCollection.updateOne(eq("subtasks.subtaskId", 4), new Document("$set", new Document("subtasks.$.name", "jpa")));

        List updateResults = new ArrayList<>();
        taskCollection.find(query).into(updateResults);
        assertEquals(updateResults.size(), 1);

        taskCollection.updateOne(eq("taskId", 3), unset("subtasks"));

        List removeResults = new ArrayList<>();
        taskCollection.find(query).into(removeResults);
        assertEquals(removeResults.size(), 1);
    }

    @Test
    public void supportFullTextSearchByWordInTaskDescription(){
        taskCollection.dropIndexes();
        taskCollection.createIndex(Indexes.text("description"));
        List results = new ArrayList<>();
        taskCollection.find(text("framework")).into(results);
        assertEquals(results.size(), 2);
    }

    @Test
    public void supportFullTextSearchBySubTaskName(){
        taskCollection.dropIndexes();
        taskCollection.createIndex(Indexes.text("subtasks.name"));
        List results = new ArrayList<>();
        taskCollection.find(text("sql")).into(results);
        assertEquals(results.size(), 1);
    }

    private void insertData() {
       documents = Arrays.asList(new Document("taskId", 1)
                        .append("dateOfCreation", new Date())
                        .append("deadline", "2022-04-11")
                        .append("name", "mongodb")
                        .append("description", "database connection")
                        .append("category", "sql")
                        .append("subtasks", Arrays.asList(new Document("subtaskId", 1)
                                        .append("name", "sql")
                                        .append("description", "open connection"),
                                new Document("subtaskId", 2)
                                        .append("name", "sql")
                                        .append("description", "close connection"))),
                new Document("taskId", 2)
                        .append("dateOfCreation", new Date())
                        .append("deadline", "2022-04-15")
                        .append("name", "maven")
                        .append("description", "build tool")
                        .append("category", "build"),
                new Document("taskId", 3)
                        .append("dateOfCreation", new Date())
                        .append("deadline", "2022-04-15")
                        .append("name", "spring")
                        .append("description", "application framework")
                        .append("category", "framework"),
                new Document("taskId", 4)
                        .append("dateOfCreation", new Date())
                        .append("deadline", "2022-04-17")
                        .append("name", "java")
                        .append("description", "programming language")
                        .append("category", "language")
                        .append("subtasks", Arrays.asList(new Document("subtaskId", 3)
                                .append("name", "update")
                                .append("description", "java update")))
                , new Document("taskId", 5)
                        .append("dateOfCreation", new Date())
                        .append("deadline", "2022-04-17")
                        .append("name", "junit")
                        .append("description", "unit testing framework")
                        .append("category", "test"));
        taskCollection.insertMany(documents);
    }
}

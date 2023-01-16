package com.sibi;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.neo4j.driver.Record;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;

public class QueryDB {
    static Driver driver = GraphDatabase.driver("neo4j+s://bf553ae3.databases.neo4j.io", AuthTokens.basic("neo4j", "ilxA6yW7FIYsW-4q1zld5Smy3Wo7O8naJ9pyMgJmakY"));
    static Session session = driver.session();

    //Get all the categories in the DB
    List<String> retrieveCategories()
    {
        int index = 0;
        List<String> categories = new ArrayList<String>();

        Result result = session.run("MATCH (c:Category) RETURN c.Category as category");
        while (result.hasNext()) {
            Record record = result.next();
            categories.add(index, record.get("category").asString());
            index++;
        }

        Collections.sort(categories);
        return categories;
    }

    String retrieveDescription(String bookTitle)
    {
        Result result = session.run("MATCH (b:Book {Title: \"" + bookTitle + "\"}) RETURN b.Book_Description AS description");
        Record record = result.next();

        return record.get("description").asString();
    }

    //Get all the books in the DB
    List<String> retrieveBooks()
    {
        int index = 0;
        List<String> books = new ArrayList<String>();

        Result result = session.run("MATCH (b:Book) RETURN b.Title as title");
        while (result.hasNext()) {
            Record record = result.next();
            books.add(index, record.get("title").asString());
            index++;
        }

        Collections.sort(books);
        return books;
    }

    //From a book, get it's category
    String getBookCategory(String bookTitle)
    {
        Result result = session.run("MATCH (c:Category)<-[CATEGORY_IS]-(:Book {Title: \"" + bookTitle + "\"}) RETURN c.Category AS category");
        Record record = result.next();

        return record.get("category").asString();
    }

    //Query for suggestion based on selection
    List<String> suggestBook(String category)
    {
        List<String> books = new ArrayList<String>();
        List<Integer> stars = new ArrayList<Integer>();
        System.out.println("Here are some " + category + " books that you might like:");
        System.out.println("--------------------");

        Result result = session.run("MATCH (b:Book)-[CATEGORY_IS]->(:Category {Category: \"" + category + "\"}) RETURN b.Title AS title, b.Stars AS stars");
        while (result.hasNext()) {
            Record record = result.next();
            books.add(record.get("title").asString());
            stars.add(record.get("stars", 0));
        }

        //Sorting based on rating of the books
        final Map<String, Integer> bookStarsMap = new HashMap<>();
        for(int i = 0; i < books.size(); i++) {
            bookStarsMap.put(books.get(i), stars.get(i));
        }
        Collections.sort(books, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return bookStarsMap.get(o1) - bookStarsMap.get(o2);
            }
        });

        return books;
    }

    //Close program
    static void terminate()
    {
        session.close();
        driver.close();
        System.exit(0);
    }
}
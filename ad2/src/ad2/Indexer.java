package ad2;

import java.io.StringReader;
import java.io.File;
import java.nio.file.*;
import java.lang.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.sql.*;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.NumericUtils;
import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.document.Field.Store;
import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Indexer {
    public Indexer() {}
    public static IndexWriter indexWriter;
    public static void main(String args[]) {
	String usage = "java Indexer";
	rebuildIndexes("indexes");
    }
//    public static void insertDoc(IndexWriter i, String doc_id, String line){
//	Document doc = new Document();
//	doc.add(new TextField("doc_id", doc_id, Field.Store.YES));
//	doc.add(new TextField("line", line,Field.Store.YES));
//	try { i.addDocument(doc); } catch (Exception e) { e.printStackTrace(); }
//    }
    
    public static void insertDoc(IndexWriter i, itemBean item){
	Document doc = new Document();
	int item_id = item.getItem_ID();
	String item_name=item.getName();
	String description=item.getDescription();
	String category=item.getCatagory();
	double buy_price = item.getBuy_price();
	double latitude = item.getLatitude();
	double longitude = item.getLongitude();
	double dist = item.getDistance();	
	doc.add(new IntField("item_id", item_id, Field.Store.YES));
	doc.add(new TextField("item_name", item_name,Field.Store.YES));
	doc.add(new TextField("description", description, Field.Store.YES));
	doc.add(new TextField("category", category,Field.Store.YES));
	doc.add(new DoubleField("buy_price",buy_price,Field.Store.YES));
	doc.add(new DoubleField("lat",latitude,Field.Store.YES));
	doc.add(new DoubleField("lng",longitude,Field.Store.YES));
	doc.add(new DoubleField("dist",dist,Field.Store.YES));
	try { i.addDocument(doc); } catch (Exception e) { e.printStackTrace(); }
    }
    public static void rebuildIndexes(String indexPath) {
	try {
	    Path path = Paths.get(indexPath);
	    System.out.println("Indexing to directory '" + indexPath + "'...\n");
	    Directory directory = FSDirectory.open(path);
	    IndexWriterConfig config = new IndexWriterConfig(new SimpleAnalyzer());
	    //	    IndexWriterConfig config = new IndexWriterConfig(new StandardAnalyzer());
	    //IndexWriterConfig config = new IndexWriterConfig(new EnglishAnalyzer());
	    IndexWriter i = new IndexWriter(directory, config);
	    i.deleteAll();
	    List<itemBean> itemList=jdbc.get_items("indexes");
	    for(int j=0;j<itemList.size();j++){
	    	insertDoc(i,itemList.get(j));
	    }
	    i.close();
	    directory.close();
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }
    public static void rebuildIndexes_in_dist(String indexPath,double lat,double lng,double dist) {
    	try {
    	    Path path = Paths.get(indexPath);
    	    System.out.println("Indexing to directory '" + indexPath + "'...\n");
    	    Directory directory = FSDirectory.open(path);
    	    IndexWriterConfig config = new IndexWriterConfig(new SimpleAnalyzer());
    	    //	    IndexWriterConfig config = new IndexWriterConfig(new StandardAnalyzer());
    	    //IndexWriterConfig config = new IndexWriterConfig(new EnglishAnalyzer());
    	    IndexWriter i = new IndexWriter(directory, config);
    	    i.deleteAll();
    	    List<itemBean> itemList=jdbc.get_items_with_dist(lat, lng, dist);
    	    for(int j=0;j<itemList.size();j++){
    	    	insertDoc(i,itemList.get(j));
    	    }
    	    i.close();
    	    directory.close();
    	} catch (Exception e) {
    	    e.printStackTrace();
    	}
        }
    
    }

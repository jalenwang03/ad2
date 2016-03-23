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
import java.util.ArrayList;
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
import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.document.Field.Store;
import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;

public class jdbc {
    public jdbc() {
    }
    public static void main(String args[]) {
	String usage = "java jdbc";
//	List<itemBean> l=rebuildIndexes("indexes");
//	for(int i=0;i<l.size();i++){
//		System.out.println(l.get(i).getItem_ID()+"    "+l.get(i).getCatagory());
//	}
    }
    public static List rebuildIndexes(String indexPath) {
	Connection conn = null;
	Statement stmt = null;
	List<itemBean> itemList=new ArrayList();
	try {
	    conn = DbManager.getConnection(true);
	    stmt = conn.createStatement();
	    //String sql = "SELECT * from item limit 3;";
//	    String sql = "SELECT count(*) as count from item;";
//	    String sql = "select a.item_id,a.item_name,a.description,b.category_name from item a,has_category b where a.item_id=b.item_id";
	    String sql="select a.item_id,a.item_name,a.description,b.category_name,c.latitude,c.longitude,d.buy_price from item a,has_category b,item_coordinates c,buy_price d where a.item_id=b.item_id and a.item_id=c.item_id and a.item_id=d.item_id";
	    ResultSet rs = stmt.executeQuery(sql);
	    while(rs.next()){
	    	itemBean item=new itemBean();
	    	item.setItem_ID(rs.getInt("item_id"));
	    	item.setName(rs.getString("item_name"));
	    	item.setDescription(rs.getString("description"));
	    	item.setCatagory(rs.getString("category_name"));
	    	item.setLatitude(Double.valueOf(String.valueOf(rs.getFloat("latitude"))));
	    	item.setLongitude(Double.valueOf(String.valueOf(rs.getFloat("longitude"))));
	    	itemList.add(item);
//		String count = rs.getString("count");
//		System.out.println("count: " + count);
	    }
	    rs.close();
	    conn.close();
	} catch (SQLException ex) {
	    System.out.println(ex);
	}
    return itemList;
    }
}


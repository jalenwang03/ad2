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
import java.io.BufferedReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.spatial.SpatialStrategy;
import org.apache.lucene.spatial.prefix.RecursivePrefixTreeStrategy;
import org.apache.lucene.spatial.prefix.tree.GeohashPrefixTree;
import org.apache.lucene.spatial.prefix.tree.SpatialPrefixTree;
import org.apache.lucene.spatial.query.SpatialArgs;
import org.apache.lucene.spatial.query.SpatialOperation;
import org.apache.lucene.search.Explanation;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import com.spatial4j.core.context.SpatialContext;
import com.spatial4j.core.distance.DistanceUtils;

import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.analysis.standard.*;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.KeywordAnalyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.document.Field.Store;
import java.io.*;
import java.nio.file.Path;

public class Searcher {
    public Searcher() {}
    
    public static void main(String[] args) throws Exception {
	String usage = "java Searcher";
//	search(args[0], "indexes");
	if(args.length==0)
		search("Disney","indexes");
	else{
	for(int i=0;i<args.length;i++){
		search(args[i],"indexes");
	}
	}
    }
    
    private static TopDocs search(String searchText, String p) {   
	System.out.println("Running search(" + searchText + ")");
	SpatialContext ctx = SpatialContext.GEO;
	int maxLevels = 11;
	SpatialPrefixTree grid = new GeohashPrefixTree(ctx, maxLevels);
	SpatialStrategy strategy=new RecursivePrefixTreeStrategy(grid, "myGeoField");;
	double dis = 100.0;
	SpatialArgs args = new SpatialArgs(SpatialOperation.Intersects,
			ctx.makeCircle(116.430459, 39.939802, DistanceUtils
			.dist2Degrees(dis/1000.0,
			DistanceUtils.EARTH_MEAN_RADIUS_KM)));
	Filter filter = strategy.makeFilter(args);
	try {   
		
		SortField[] sortArray;  
	    Path path = Paths.get(p);
	    String[] field=new String[]{"item_name","description","category"};
	    Directory directory = FSDirectory.open(path);       
	    IndexReader indexReader =  DirectoryReader.open(directory);
	    IndexSearcher indexSearcher = new IndexSearcher(indexReader);
//	    QueryParser queryParser = new QueryParser("item_name", new SimpleAnalyzer());  
//	    sortArray = new SortField[] {new SortField("order", SortField.STRING), new SortField("num", SortField.STRING)}; 
	    MultiFieldQueryParser  queryParser = new MultiFieldQueryParser(field, new SimpleAnalyzer());
	    Query query = queryParser.parse(searchText);
	    TopDocs topDocs = indexSearcher.search(query,1000);
	    System.out.println("Number of Hits: " + topDocs.totalHits);
	    for (ScoreDoc scoreDoc : topDocs.scoreDocs) {           
		Document document = indexSearcher.doc(scoreDoc.doc);
		resultItemBean result=new resultItemBean();
		result.setCore(scoreDoc.score);
		result.setItem_ID(Integer.parseInt(document.get("item_id")));
		result.setName(document.get("item_name"));
		System.out.println("[item_id:"+document.get("item_id")+"]    [item_name:"+document.get("item_name")+ "]    [score: " + scoreDoc.score + "] ");
	    }
//	    show();
	    return topDocs;
	} catch (Exception e) {
	    e.printStackTrace();
	    return null;
	}
    }
    

}

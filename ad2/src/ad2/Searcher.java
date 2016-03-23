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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.io.BufferedReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.MultiPhraseQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.spatial.SpatialStrategy;
import org.apache.lucene.spatial.prefix.RecursivePrefixTreeStrategy;
import org.apache.lucene.spatial.prefix.tree.GeohashPrefixTree;
import org.apache.lucene.spatial.prefix.tree.SpatialPrefixTree;
import org.apache.lucene.spatial.query.SpatialArgs;
import org.apache.lucene.spatial.query.SpatialOperation;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Explanation;
import org.apache.lucene.search.Filter;
import org.apache.lucene.search.GeoPointDistanceQuery;
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
	String p = null;
	if(args.length==0)
		System.out.println("Usage:1.Searcher \"Key word\",2.Search \"key word\" -x latitude -y longitude -w dist");
	else if(args.length>5 && args[args.length-2].equals("-w")&&args[args.length-4].equals("-y")&&args[args.length-6].equals("-x")){
		for(int i=0;i<args.length-6;i++){
			p=args[i]+" ";
		}
		Indexer.rebuildIndexes_in_dist("indexes", Double.valueOf(args[2]), Double.valueOf(args[4]), Double.valueOf(args[6]));
		spatialSearch(p,"indexes");
	}else{
		
	for(int i=0;i<args.length;i++){
		p="star trek";
	}
//	Indexer.rebuildIndexes("indexes");
	basicSearch(p,"indexes");
	}
    }

    private static TopDocs basicSearch(String searchText, String p) {   
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
//	Filter filter = strategy.makeFilter(args);
	try {   
//		String[] searchList=searchText.split(" ")
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
	    MultiPhraseQuery multiquery = new MultiPhraseQuery();
//	    BooleanQuery query = new BooleanQuery();
//	    multiquery.add(queryParser.parse(searchText));
	    TopDocs topDocs = indexSearcher.search(query,1000);
	    System.out.println("Number of Hits: " + topDocs.totalHits);
	    for (ScoreDoc scoreDoc : topDocs.scoreDocs) {           
		Document document = indexSearcher.doc(scoreDoc.doc);
		BooleanQuery query1 = new BooleanQuery();
		double lon = 0;
		double lat = 0;
		double radiusKM = 0;
		Query query_geo = new GeoPointDistanceQuery("test", lon, lat, (double) radiusKM);
//		m_BooleanQuery.ad
//		query1.add(query,true);
		BooleanClause.Occur[] flags=new BooleanClause.Occur[]{BooleanClause.Occur.MUST,BooleanClause.Occur.MUST};   
		
		resultItemBean result=new resultItemBean();
		
		result.setCore(scoreDoc.score);
		result.setItem_ID(Integer.parseInt(document.get("item_id")));
		result.setName(document.get("item_name"));
		System.out.println("[item_id:"+document.get("item_id")+"]    [item_name:"+document.get("item_name")+ "]  [item_name:"+document.get("description")+ "] [item_name:"+document.get("category")+ "]   [score: " + scoreDoc.score + "] ");
	    }
//	    show();
	    return topDocs;
	} catch (Exception e) {
	    e.printStackTrace();
	    return null;
	}
    }
    

    private static TopDocs spatialSearch(String searchText,String p) {   
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
//	Filter filter = strategy.makeFilter(args);
	try {   
//		itemMap=jdbc.get_item_id_in_dist(lat, lng, dist);
		SortField[] sortArray;  
	    Path path = Paths.get(p);
	    String[] field=new String[]{"item_name","description","category"};
	    Directory directory = FSDirectory.open(path);       
	    IndexReader indexReader =  DirectoryReader.open(directory);
	    IndexSearcher indexSearcher = new IndexSearcher(indexReader);
//	    QueryParser queryParser = new QueryParser("item_name", new SimpleAnalyzer());  
//	    sortArray = new SortField[] {new SortField("order", SortField.STRING), new SortField("num", SortField.STRING)}; 
	    MultiFieldQueryParser  queryParser = new MultiFieldQueryParser(field, new SimpleAnalyzer());
//	    queryParser.
	    BooleanClause.Occur[] flags=new BooleanClause.Occur[]{BooleanClause.Occur.MUST,BooleanClause.Occur.MUST};  
	    BooleanQuery booleanquery=new BooleanQuery();
	    Query query = queryParser.parse(searchText);
//	    booleanquery.add(query, flags);
	    Query query1 = MultiFieldQueryParser.parse("hello", field, flags, new SimpleAnalyzer());  
	    
	    booleanquery.add(new TermQuery(new Term("item_name")),BooleanClause.Occur.SHOULD);
	    booleanquery.add(new TermQuery(new Term("description")),BooleanClause.Occur.SHOULD);
	    GeoPointDistanceQuery geoquery=new GeoPointDistanceQuery("geo", dis, dis, dis);
	    SpatialContext ctx1 = null;
	    double distance = 0;
		double y = 0;
		double x = 0;
		ctx1.makeCircle(x, y, distance);
		ctx.makeCircle(116.430459, 39.939802, DistanceUtils.dist2Degrees(dis/1000.0,DistanceUtils.EARTH_MEAN_RADIUS_KM));
//		Filter filter = strategy.makeFilter(args);
		
	    TopDocs topDocs = indexSearcher.search(query,1000);
	    System.out.println("Number of Hits: " + topDocs.totalHits);
	    for (ScoreDoc scoreDoc : topDocs.scoreDocs) {           
		Document document = indexSearcher.doc(scoreDoc.doc);
		resultItemBean result=new resultItemBean();
		result.setCore(scoreDoc.score);
		result.setItem_ID(Integer.parseInt(document.get("item_id")));
		result.setName(document.get("item_name"));
//		System.out.println(itemMap.size());
//		System.out.println(Integer.valueOf(document.get("item_id")));
//		if(itemMap.containsKey(Integer.valueOf(document.get("item_id")))){
		System.out.println("[item_id:"+document.get("item_id")+"]    [item_name:"+document.get("item_name")+ "]  distance: "+document.get("dist")+"  [score: " + scoreDoc.score + "] ");
//		}
		}
//	    show();
	    return topDocs;
	} catch (Exception e) {
	    e.printStackTrace();
	    return null;
	}
    }
    

}


import java.io.IOException;
import java.nio.file.Paths;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;

/**
 * Searches for files under a directory indexed using indexer {@code Indexer}
 * @author Kedar
 *
 */
public class Searcher {

	private final String indexPath;
	private final String field;
	private final int hitsPerPage =10;
	private final boolean raw;
	
	public Searcher(String indexPath,String field,boolean raw,String searchfield,StringBuilder sb){
		this.indexPath = indexPath;
		this.field = field;
		this.raw = raw;
		String search = searchfield;
		SearchFiles(search,sb);		
	}
	
	public void SearchFiles(String queryString,StringBuilder sb){
		try {
			IndexReader reader = DirectoryReader.open(
					FSDirectory.open(Paths.get(indexPath)));
			IndexSearcher searcher = new IndexSearcher(reader);
			Analyzer analyzer = new StandardAnalyzer();
			QueryParser parser = new QueryParser(field, analyzer);
			try {
				Query query = parser.parse(queryString);
				sb.append("Searching for: " + query.toString(field)+"\n");
				System.out.println("Searching for: " + query.toString(field));
				doSearch(searcher, query,raw,sb);
				reader.close();
			} catch (ParseException e) {
				System.out.println("Unable to parse the query " + queryString);
			}
			
		} catch (IOException e) {
			System.out.println("Unable to read indexes from " + indexPath);
		}
	}

	private void doSearch(IndexSearcher searcher,Query query,boolean raw,StringBuilder sb) {
		try {
			long startTime = System.nanoTime();			
			TopDocs results = searcher.search(query, 5 * hitsPerPage);
			long endTime = System.nanoTime();
			ScoreDoc[] hits = results.scoreDocs;
			int numTotalHits = results.totalHits;
			System.out.println(numTotalHits + " total matching documents found in " 
					+ (endTime-startTime)/1e9 + " secs");
			sb.append(numTotalHits + " total matching documents found in " 
					+ (endTime-startTime)/1e9 + " secs \n");
			int start = 0;
			int end = Math.min(numTotalHits, hitsPerPage);
			end = numTotalHits;
			for (int i = start; i < end; i++) {				
				Document doc = searcher.doc(hits[i].doc);
				String path = doc.get("path");
				String uri = doc.get("uri"); //Get Uri from index files
				if (path != null) {
					System.out.println((i+1) + ". " + uri);//Print Line uri
					sb.append((i+1) + ". " + uri+" \n");
					String title = doc.get("title");
					if (title != null) {
						System.out.println("   Title: " + doc.get("title"));
					}
				} else {
					System.out.println((i+1) + ". " + "No path for this document");
				}
				if (raw) {                              
					System.out.println("doc="+hits[i].doc+" score="+hits[i].score);
					
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		SpiderControllerXML.unlockButton(); //Unlock button
	}

}

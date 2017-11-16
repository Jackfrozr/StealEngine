
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Scanner;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.springframework.stereotype.Component;

@Component
public class IndexImpl implements Index
{
	private final String indexPath;
	private boolean create;
	private final String docsPath;
	private int count;
	
	public IndexImpl() {
		indexPath = "index/";
		create = true;
		docsPath = "web/";
	}
	
	public void IndexStart(){
		create=true;
    	SpiderControllerXML.unlockButton(); // Unlock the buttons for gui
		indexFiles();
	}
	
	public void displayText(String text){
		//To display text in console and gui log
		System.out.println(text);
    	SpiderControllerXML.updateLog(text);
	}
	
	public void indexFiles(){
		try 
		{
			final Path docDir = Paths.get(docsPath);
			
			if (!Files.isReadable(docDir)) {
				displayText("Document directory '" 
						+ docDir.toAbsolutePath() 
						+ "' does not exist or is not readable, please check the path");
				System.exit(1);
			}
			displayText("Indexing directory" + docsPath + " to directory '" + indexPath + "'...");
	    	
			long startTime = System.nanoTime();
			Directory dir = FSDirectory.open(Paths.get(indexPath));
			Analyzer analyzer = new StandardAnalyzer();
			IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
			if (create) {
				iwc.setOpenMode(OpenMode.CREATE);				
			}else{
				iwc.setOpenMode(OpenMode.CREATE_OR_APPEND);
			}
			IndexWriter writer = new IndexWriter(dir, iwc);
			indexDocs(writer, docDir);
			writer.close();
			long endTime = System.nanoTime();
			
			displayText("\nIndexing completed in " + (endTime - startTime)/1e9  + " secs");
	    	displayText(count + " files scanned.\n");
			
		} 
		catch (IOException e) 
		{
			System.out.println(" caught a " + e.getClass() + 
					"\n with message: " + e.getMessage());
		}
	}
	
	/**
	 * Indexes the given file using the given writer, or if a directory is given,
	 * recurses over files and directories found under the given directory.
	 * @param writer
	 * @param file
	 * @param lastModified
	 * @throws IOException
	 */
	private void indexDocs(final IndexWriter writer, Path path) throws IOException {
		if(Files.isDirectory(path)){
			Files.walkFileTree(path, new SimpleFileVisitor<Path>(){
				@Override
				public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)throws IOException{
					try {
						if(file.toString().contains(".html"))//Only index html files, the txt files are used to kept the uri inside
						{
							indexDoc(writer, file, attrs.lastModifiedTime().toMillis());
						}
					}catch(IOException ex){
						System.out.println("Unable to read " + file.getFileName());
						
					}
					return FileVisitResult.CONTINUE;
				}							
			});
		}else {
			indexDoc(writer, path, Files.getLastModifiedTime(path).toMillis());
		}	
	}
	
	private void indexDoc(IndexWriter writer, Path file, long lastModified) throws IOException{
		try (InputStream stream = Files.newInputStream(file)) 
		{

				Document doc = new Document();
				Field pathField = new StringField("path", file.toString(), Field.Store.YES);			
				doc.add(pathField);
				pathField = new StringField("name", file.getFileName().toString(), Field.Store.YES);
				doc.add(pathField);
	
				//URI
				String Fname = "Web/"+file.getFileName();
				Scanner in = new Scanner(new FileReader(Fname.replace(".html", ".txt")));
				String uri = in.next();
				in.close();
				System.out.println(uri);
				
				pathField = new StringField("uri", uri, Field.Store.YES);
				doc.add(pathField);
				
				doc.add(new TextField("contents", 
						new BufferedReader(
								new InputStreamReader(
										stream, StandardCharsets.UTF_8)
								)
						));
				if (writer.getConfig().getOpenMode() == OpenMode.CREATE) {
					displayText("adding " + file);
					writer.addDocument(doc);
				}else{
					displayText("updating " + file);
					writer.updateDocument(new Term("path", file.toString()), doc);
				}
				count++;
			}
		
	}
	
}

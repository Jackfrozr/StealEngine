

import java.net.URI;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import org.springframework.stereotype.Component;
import org.springframework.context.annotation.Bean;


@Component
public class Spider {
	
	private HttpClient client;
	private HttpParser parser;
	private Index index;
	private Queue<URI> urlQueue = new LinkedList<URI>();
	private Set<URI> alreadyVisited = new HashSet<URI>();
	
	@Bean
	public HttpClient getHttpClient()
	{
		return new HttpClientImpl();
	}
	
	public Spider(HttpClient client, HttpParser parser, Index index)
	{
		this.client = client;
		this.parser = parser;
		this.index = index;
	}

	public void startIndex()
	{
		index();
	}
	
	public void startCrawl(URI uri, String depth, String maxLink)
	{
		System.out.println("i am working :: startcrawl");
	  Thread thread = new Thread(){
		    public void run(){
				try {
					int pdepth = Integer.parseInt(depth);
					int pmaxLink = Integer.parseInt(maxLink);
					crawl(uri,pdepth,pmaxLink);
				} catch (Exception e) {
					e.printStackTrace();
				}
		    }
		  };

		  thread.start();
	}
	
	public void index(){
		index.IndexStart();
	}
	
	public void crawl(URI uri, int depth, int maxLink)
	{		
		
		//=====================================================//
		// WebCrawler
		//=====================================================//
		
		final int maxLinkPerDepth = maxLink; //Max links that can be added per depth
		List<URI> storedParseLinks = new ArrayList<URI>(); //Temporary List to store links for current depth	
		urlQueue.add(uri); // Add the uri to linked list
		//SpiderControllerXML.updateLog("Crawling "+uri.toString());
		for(int i=0; i<depth; i++)
		{
			storedParseLinks.clear(); //Reset the temp array list
			while(!urlQueue.isEmpty()) // Loop when queue is not empty
			{
			
				StringBuilder content = new StringBuilder();
				
				//=====================================================//
				// Client
				//=====================================================//
				
				client.get(urlQueue.peek(),content);
			
				//=====================================================//
				// Parser
				//=====================================================//
				
				List<URI> parseLinks;
				parseLinks= parser.parseLinks(content.toString(),urlQueue.peek().toString());
				
				for(URI a: parseLinks)
				{
					if (!alreadyVisited.contains(a))//Check duplicates
					{
						storedParseLinks.add(a);
					}
				}
				
				alreadyVisited.add(urlQueue.peek());
				urlQueue.poll();// Remove first in queue
			}
			
			//=====================================================//
			//  Adding and removing from queue
			//=====================================================//
			
				int j = 0;
				for(URI a: storedParseLinks)
				{
					if(j<maxLinkPerDepth)
					{
						urlQueue.add(a);
						j++;
					}
				}
				System.out.println("Current Depth: "+i );
		}	
		System.out.println(" Crawling Complete      " );
		
    	StringBuilder temp = new StringBuilder();
		for(URI a: alreadyVisited)
		{
			temp.append(">"+a+" \n");
			//SpiderControllerXML.updateLog(">"+a.toString());
		}
		try{
			SpiderControllerXML.updateLog(temp+"Finished Crawling, files are saved inside web folder. \n");
		}
		catch(Exception e){
			System.err.println("SPIDER::Oopss something went wrong when updating javaFX log");
		}

		SpiderControllerXML.unlockButton();//Unlock gui buttons
	}
}

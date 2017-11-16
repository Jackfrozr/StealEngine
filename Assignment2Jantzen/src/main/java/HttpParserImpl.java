

import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

@Component
public class HttpParserImpl implements HttpParser{
	
	public List<URI> parseLinks(final String content, final String url)
	{
		final List<URI> urls = new ArrayList <URI> ();
		
		Document doc = Jsoup.parse(content);
		
        Elements links = doc.getElementsByTag("a");
        
        for (Element link : links) 
        {
           String linkHref = link.attr("abs:href");
           if((linkHref!="")&&(linkHref!=url)&&(linkHref.contains("http"))) //Check if link is valid, not null, not the same as the base url
           {
        	   
        	   try{
        		   //Convert string > to URL > URI
	        	   URL tempurl = new URL(linkHref);
	        	   URI uri = tempurl.toURI();
    	           if (!urls.contains(uri)) //Check for duplicates
    	           {
		        	   urls.add(uri);
    	           }
    	           
        	   }
        	   catch(Exception e)
        	   {
        		   System.err.println("HttpParser::Invalid Link "+linkHref);
        	   }
	           
           }
        }
        
		return Collections.unmodifiableList(urls);
	}
	

}



import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.net.URI;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Component;

@Component
public class HttpClientImpl implements HttpClient
{
	public HttpClientImpl() {

	}
	
	public Boolean get(URI uri, StringBuilder response)
	{
		String URL = uri.toString();
		try
		{
			Document doc = Jsoup.connect(URL).get(); 
			response.append(doc);

			//Write to local file
			try{
	        	BufferedWriter  writer = null;
	        	System.out.println(URL);
	        	String URLtrim = URL.replaceAll("[\\?/:*|<>\"]", "_");
	        	File file = new File("web/"+URLtrim );
	        	if (null != file.getParentFile())
	        	{
	        		file.getParentFile().mkdirs(); //Create folder if it does not exist
	        	}
	        	//Create HTML File
	        	writer = new BufferedWriter(new FileWriter(file+ ".html"));
	            writer.write(doc.toString()); 
	            writer.close();
	            //Create Text File to store url
	        	writer = new BufferedWriter(new FileWriter(file+ ".txt"));
	            writer.write(URL); 
	            writer.close();
			}
			catch(Exception e)
			{
	            System.err.println("HttpClient::Problem writing file to local disk");
			}
			return true;
		}
		catch( Exception e)
		{
			System.err.println("HttpClient::Jsoup has problem Connecting to the given url: " + URL);
		}
		return false;
	}
	
}

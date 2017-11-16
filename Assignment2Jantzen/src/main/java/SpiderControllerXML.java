

import java.net.URI;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.core.io.ClassPathResource;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class SpiderControllerXML extends Application{
	private static FxController controller;
	private static Boolean buttonLock;
	
	
	  public void start(Stage primaryStage) throws Exception {
	        FXMLLoader loader = new FXMLLoader(getClass().getResource("fx.fxml"));
	        Parent root = loader.load();
	        controller = (FxController)loader.getController();
	        Scene scene = new Scene(root);
	        primaryStage.setTitle("StealEngine");
	        primaryStage.setScene(scene);
	        primaryStage.show();     
	        buttonLock = false;
	    }
	
	public static void main(String[] args)
	{
 
        Application.launch(args);

	}
	
	public static boolean getlockButton(){
		return buttonLock;
	}
	
	public static void lockButton(){
		buttonLock = true;
	}
	
	public static void unlockButton(){
		buttonLock = false;
	}
	
	public static void updateLog(String content){
		controller.updateLog(content);;
	}
  
	public static void crawl ()
	{

		DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
		XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(beanFactory);
        reader.loadBeanDefinitions(new ClassPathResource("spring.xml"));
		Spider webCrawler = (Spider) beanFactory.getBean("Spider");
		String url = controller.getURL();
		try
		{
			URI uri = new URI(url);
			//webCrawler.startCrawl(uri,depth,maxLink);	
			webCrawler.startCrawl(uri,controller.getDepth(),controller.getMaxLink());	
		}
		catch(Exception e){
			
		}

	}
	public static void index ()
	{

		DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
		XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(beanFactory);
        reader.loadBeanDefinitions(new ClassPathResource("spring.xml"));
		Spider webCrawler = (Spider) beanFactory.getBean("Spider");
		webCrawler.startIndex();	
	}	
	
}

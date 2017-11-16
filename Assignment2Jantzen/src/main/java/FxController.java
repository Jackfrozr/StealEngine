

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class FxController {

    @FXML
    private TextField txtURL;

    @FXML
    private TextArea txtArea;

    @FXML
    private TextField txtDepth;

    @FXML
    private TextField txtMaxLink;
    
    @FXML
    private Button btnStart;

    @FXML
    private Button btnIndex;

    @FXML
    private TextField txtSearch;

    @FXML
    private Button btnSearch;
    
    @FXML
    private Button btnClear;

    @FXML
    void btnClear(ActionEvent event) {
    	if(SpiderControllerXML.getlockButton()==false)
    	{
    		clearLog();
    	}
    	else
    	{
    		updateLog("ERROR:Program is currently running, please wait until all process is finished before trying again. \n");
    	}
    }

    //Index button pressed
    @FXML
    void btnIndex(ActionEvent event) 
    {
    	if(SpiderControllerXML.getlockButton()==false){
    		updateLog("======================================================================\nIndexing Folder"
    				+ "\n======================================================================");
        	SpiderControllerXML.lockButton();
    		SpiderControllerXML.index();
    	}
    	else
    	{
    		updateLog("ERROR:Program is already running, please wait until all process is finished before trying again. \n");
    	}

    }

    //Crawl button pressed
    @FXML
    void btnStart(ActionEvent event) 
    {
    	if(SpiderControllerXML.getlockButton()==false)
    	{
    		String URL = txtURL.getText();
    		if(!URL.isEmpty())	
    		{
    			//Try to fix user link
    			if(URL.contains("http")&&(URL.charAt(0)=='w'))
    			{
    				txtURL.setText("https://"+URL);
    			}
	    		try{
	    			Integer.parseInt(txtDepth.getText());//Check if integer
	    			Integer.parseInt(txtMaxLink.getText());//Check if integer
		    		updateLog("======================================================================\nWeb Crawling"
		    				+ "\n======================================================================");
		    		updateLog("Crawling " + txtURL.getText()+" Depth: "+txtDepth.getText());
		        	SpiderControllerXML.crawl();
		        	SpiderControllerXML.lockButton();
	    			} 
	    		catch (NumberFormatException e) 
	    		{
	    			updateLog("ERROR:Please input an integer for depth and max link per depth.\n");
	    		}
    		}
    		else{
        		updateLog("ERROR:Please enter a link.\n");
    		}
    	}
    	else
    	{
    		updateLog("ERROR:Program is already running, please wait until all process is finished before trying again.\n");
        }
    }
    
    //Search button pressed
    @FXML
    void btnSearch(ActionEvent event) {
    	
    	if(SpiderControllerXML.getlockButton()==false){
	    	String searchfield = txtSearch.getText();
	    	
	    	//Check if search is valid
	    	if(searchfield.length()>1)
	    	{
	    		updateLog("======================================================================\nSearching Content"
	    				+ "\n======================================================================");
		    	SpiderControllerXML.lockButton(); //Lock button from being pressed while a process is running.
		    	StringBuilder sb = new StringBuilder();
		    	System.out.println(searchfield);
		    	new Searcher("index/","contents", false, searchfield,sb);
		    	updateLog(sb.toString());
	    	}
	    	else{
		    	updateLog("ERROR:Search field must be more than 1 character \n");
	    	}
    	}
    	else
    	{
    		updateLog("ERROR:Program is already running, please wait until all process is finished before trying again. \n");
    	}
    }
    

    @FXML
    void menuClose(ActionEvent event) {
    	System.exit(0);
    }
    
    public String getURL(){
    	return txtURL.getText();
    }
    
    public String getMaxLink(){
    	return txtMaxLink.getText();
    }
    
    public String getDepth(){
    	return txtDepth.getText();
    }
    
    void clearLog(){
    	txtArea.clear();
    }
    	
    
    void updateLog(String content){
    	txtArea.appendText(content+"\n");
    	
    }
    
    void drawLineLog(){
    	txtArea.appendText(" \n");
    	
    }
    
    @FXML
    private void initialize(){
    	txtDepth.setText("2");
    	txtURL.setText("http://www.unitec.ac.nz/");
    	txtMaxLink.setText("10");
    }

}

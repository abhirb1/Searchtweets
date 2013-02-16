package com.activity.asyncadpter;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import com.fedorvlasov.lazylist.R;


import android.app.Activity;
import android.content.Context;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;


import android.view.View;

import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;




public class MainActivity extends Activity {
    
    ListView list;
    AsyncAdapter adapter;
    ProgressBar	progressBar1;
    Button search;
    
 
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        //EditText searchedit=(EditText)findViewById(R.id.search_edit);
        //String searchterm=searchedit.getText().toString();
        list=(ListView)findViewById(R.id.list);
      
    }
	
   
    @Override
    public void onDestroy()
    {
        list.setAdapter(null);
        super.onDestroy();
    }
    
   
    public void searchTwitter(View view) throws Exception{
    	
    	
    	progressBar1=(ProgressBar)findViewById(R.id.progressBar1);
		search=(Button)findViewById(R.id.search_btn);
		EditText searchedit=(EditText)findViewById(R.id.search_edit);
		if(searchedit.length()<1)
		{
			Toast.makeText(getApplicationContext(), "Please enter a query to search", Toast.LENGTH_SHORT).show();
			return;
		}
		search.setEnabled(false);
		progressBar1.setVisibility(View.VISIBLE);
		//get user entered search term
		
		if(checkInternetConnection())
		{
			String searchterm=searchedit.getText().toString();
        
			try
			{
				String encodedSearch = URLEncoder.encode(searchterm, "UTF-8");
				String searchURL = "http://search.twitter.com/search.json?q="+encodedSearch;
				new GetTweets().execute(searchURL);
        	
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		else
		{
			Toast.makeText(getApplicationContext(), "Please check your network connectivity!", Toast.LENGTH_LONG).show();
			search.setEnabled(true);
			progressBar1.setVisibility(View.INVISIBLE);
			return;
		}

    }

 
	public boolean isNetworkAvailable() {
	    ConnectivityManager cm = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo networkInfo = cm.getActiveNetworkInfo();
	    // if no network is available networkInfo will be null
	    // otherwise check if we are connected
	    if (networkInfo != null && networkInfo.isConnected()) 
	    {
	        return true;
	    }
	    return false;
	} 
	
	 
	private boolean checkInternetConnection() {

		ConnectivityManager conMgr = (ConnectivityManager) getSystemService (Context.CONNECTIVITY_SERVICE);

		// ARE WE CONNECTED TO THE NET

		if (conMgr.getActiveNetworkInfo() != null

		&& conMgr.getActiveNetworkInfo().isAvailable()

		&& conMgr.getActiveNetworkInfo().isConnected()) {

		return true;

		} else {

		return false;

		}

		} 
    private class GetTweets extends AsyncTask<String, Void, String> {
    	/*
    	 * Carry out fetching task in background
    	 * - receives search URL via execute method
    	 */
    	//ArrayList<Tweet> tweets=new ArrayList<Tweet>();
    	
    	@Override
    	protected String doInBackground(String... twitterURL) {
    		//start building result which will be json string
    		StringBuilder tweetFeedBuilder = new StringBuilder();
    		
    		
    		//should only be one URL, receives array
    		for (String searchURL : twitterURL) {
    			HttpClient tweetClient = new DefaultHttpClient();
    			try {
    				//pass search URL string to fetch
    				HttpGet tweetGet = new HttpGet(searchURL);
    				//execute request
    				
    				HttpResponse tweetResponse = tweetClient.execute(tweetGet);
    				
    				//check status, only proceed if ok
    				StatusLine searchStatus = tweetResponse.getStatusLine();
    				if (searchStatus.getStatusCode() == 200) {
    					//get the response
    					HttpEntity tweetEntity = tweetResponse.getEntity();
    					InputStream tweetContent = tweetEntity.getContent();
    					//process the results
    					InputStreamReader tweetInput = new InputStreamReader(tweetContent);
    					BufferedReader tweetReader = new BufferedReader(tweetInput);
    					String lineIn;
    					while ((lineIn = tweetReader.readLine()) != null) {
    						tweetFeedBuilder.append(lineIn);
    					}
    				}
    				else
    				{
    					
    					Toast.makeText(getApplicationContext(), "Connection could not be established!", Toast.LENGTH_SHORT).show();
    					
    				}
    					
    			}
    			catch(Exception e){ 
    				Toast.makeText(getApplicationContext(), "Connection could not be established!", Toast.LENGTH_SHORT).show();
    				
    				e.printStackTrace(); 
    				break;
    				
    			}
    		}
    		//return result string
    		return tweetFeedBuilder.toString();
    	}
    	/*
    	 * Process result of search query
    	 * - this receives JSON string representing tweets with search term included
    	 */
    	
		protected void onPostExecute(String result) {
    		//start preparing result string for display
    		ArrayList<String> list_image=new ArrayList<String>();
    		ArrayList<String> list_username=new ArrayList<String>();
    		ArrayList<String> list_tweet=new ArrayList<String>();
    		String[] images=null;
    		String[] usernames=null;
    		String[] tweets=null;
    		
    		try {
    			//get JSONObject from result
    			JSONObject resultObject = new JSONObject(result);
    			//get JSONArray contained within the JSONObject retrieved - "results"
    			JSONArray tweetArray = resultObject.getJSONArray("results");
    			//loop through each item in the tweet array
    		
    			for (int t=0; t<tweetArray.length(); t++) {
    				//each item is a JSONObject
    				JSONObject tweetObject = tweetArray.getJSONObject(t);
    				//get the username and text content for each tweet
    				   				
    				list_username.add(tweetObject.get("from_user").toString());
    				list_tweet.add(tweetObject.get("text").toString());
    				list_image.add(tweetObject.get("profile_image_url").toString());
    				
    				
    				}
    		}
    		catch (Exception e) {
    			Toast.makeText(getApplicationContext(), "Whoops - something went wrong!", Toast.LENGTH_SHORT).show();
    			e.printStackTrace();
    		}
    		//check result exists
    		if(list_tweet.size()<1 && list_username.size()<1)
    		{
    			Toast.makeText(getApplicationContext(), "No tweets found!", Toast.LENGTH_LONG).show();
    		}
    		else
    		{
        	images=list_image.toArray(new String[list_image.size()]);
        	usernames=list_username.toArray(new String[list_username.size()]);
        	tweets=list_tweet.toArray(new String[list_tweet.size()]);
        	
        	adapter=new AsyncAdapter(MainActivity.this, images,usernames,tweets);
            list.setAdapter(adapter);
    		}	
    				
    		progressBar1.setVisibility(View.INVISIBLE);
    		search.setEnabled(true);
    			
    	
    		
    	}
    }


}
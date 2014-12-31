/*
 * ====================================================================
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
 */
package client;


import org.w3c.dom.*;

import javax.xml.parsers.*;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
// import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.*;//UrlEncodedFormEntity;
// import org.apache.http.client.entity.
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

@SuppressWarnings("deprecation")
public class QueryLastFm {

	
	public static boolean isMapCreated = false;
	public static Map<String,String> mbidMap;
	public static boolean isAlreadyInserted(String id1, String id2) // also inserts into hashMap if not inserted
	{
		boolean isPresent = false;
		if(!isMapCreated)
		{
			mbidMap = new HashMap<String,String>();
			isMapCreated = true;
		}
		String key = "", value = "";
		
		// id1 = "2527625b-0752-48f3-95fd-c3f216686db8";
		// id2 = "35143c2f-0484-44e2-bf27-7baa7ca5e135";
		
		// mbidMap.put(id1+id2, value);
		// System.out.println(mbidMap.containsKey(id1 + id2));
		// return false;
		if(id1.compareTo(id2) < 0) 
		{
			// System.out.println("Expected!!");
			// key = id1 + id2;
			if(mbidMap.containsKey(id1+id2)) return true;
			mbidMap.put(id1+id2, value);
			// value = id2;
		}
		else if(id1.compareTo(id2) > 0)
		{
			// System.out.println("Un-Expected!!");
			// key = id2 + id1;
			if(mbidMap.containsKey(id2+id1)) return true;
			mbidMap.put(id2+id1, value);
			// value = id1;
		}
		else // both keys are equal : No need to insert. Just return true
		{
			// System.out.println("Equal!!");
			return true;
		}
		return isPresent;
	}

    public static void findSimilarTracks(PrintWriter track_id_out, String sourceMbid, String trackName, String artistName) throws Exception
    {
    	String destMbid;
    	CloseableHttpClient httpclient = HttpClients.createDefault();
    	
    	try {
    	    Thread.sleep(50);                 //1000 milliseconds is one second.
    	} catch(InterruptedException ex) {
    	    Thread.currentThread().interrupt();
    	}
    	try{
    		URI uri = new URIBuilder()
        	.setScheme("http")
        	.setHost("ws.audioscrobbler.com")
        	.setPath("/2.0/")
        	.setParameter("method", "track.getsimilar")
        	.setParameter("artist", artistName)
        	.setParameter("track", trackName)
        	.setParameter("limit", "10")
        	.setParameter("api_key", "88858618961414f8bec919bddd057044")
        	.build();
        
        	
        	// new URIBuilder().
        	HttpGet request = new HttpGet(uri);
        	
        	// request.
        	// This is useful for last.fm logging and preventing them from blocking this client
        	request.setHeader(HttpHeaders.USER_AGENT, "nileshmore@gatech.edu - ClassAssignment at GeorgiaTech Non-commercial use");
        	
            CloseableHttpResponse response = httpclient.execute(request);
            
            int statusCode = response.getStatusLine().getStatusCode();
            
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            
            try 
            {
                if(statusCode == 200)
                {
                	HttpEntity entity1 = response.getEntity();
                	BufferedReader br = new BufferedReader(new InputStreamReader((response.getEntity().getContent())));
                	Document document = builder.parse((response.getEntity().getContent()));
                	Element root = document.getDocumentElement();
                	root.normalize();
                	
                	NodeList mbidList = root.getElementsByTagName("mbid");
                	// System.out.println("mbid" + mbidList.getLength());
                	
                	for (int n = 0; n < mbidList.getLength(); n++)
            		{
            			Node attribute = mbidList.item(n);
            			if(mbidList.item(n).hasChildNodes())
            			{
            				if(mbidList.item(n).getParentNode().getNodeName().matches("track")) // to get correct mbid
            				{
            					destMbid = mbidList.item(n).getFirstChild().getNodeValue();
            					// track_id_out.print(sourceMbid);
        						// track_id_out.print(",");
        						// track_id_out.println(destMbid);
            					if(isAlreadyInserted(sourceMbid, destMbid)) //  if not inserted , insert into map
            					{
            						// System.out.println(sourceMbid + "---"  + destMbid);
            						// track_id_out.print(sourceMbid);
            						// track_id_out.print(",");
            						// track_id_out.println(destMbid);
            						// continue;
            					}
            				    /*if(isAlreadyInserted(sourceMbid, destMbid))
            					{
            						System.out.println("Ok got the match !!");
            					}*/
            					else
            					{
            						track_id_out.print(sourceMbid);
            						track_id_out.print(",");
            						track_id_out.println(destMbid); //
            						// track_id_out.print(",");
            						// track_id_out.println("Undirected");
            					}
            					// track_id_out.print()
            				}
            			}
            		}
            		
        		}
        		
                
            }
            finally {
                response.close();
            }
    	}
    	finally
    	{
    			httpclient.close();
    	}
    	
    }
    public static void main(String[] args) throws Exception {
    	
    	// isAlreadyInserted("asdfs","jas,jnjkah");
    	
    	// FileWriter fw = new FileWriter(".\\tracks.csv");
    	OutputStream track_os = new FileOutputStream(".\\tracks.csv");
    	PrintWriter out = new PrintWriter(new OutputStreamWriter(track_os, "UTF-8"));

    	OutputStream track_id_os = new FileOutputStream(".\\track_id_sim_track_id.csv");
    	PrintWriter track_id_out = new PrintWriter(new OutputStreamWriter(track_id_os, "UTF-8"));
    	
    	track_id_out.print("");
    	
    	ByteArrayInputStream input;
    	Document doc = null;
        CloseableHttpClient httpclient = HttpClients.createDefault();

        String trackName = "";
        String artistName = "";
        String sourceMbid = "";
        out.print("ID");// first row first column
        out.print(",");
        out.print("TrackName");// first row second column
        out.print(",");
        out.println("Artist");// first row third column
        
        
        track_id_out.print("source");// first row second column
        track_id_out.print(",");
        track_id_out.println("target");// first row third column
        // track_id_out.print(",");
        // track_id_out.println("type");// first row third column
        
        // out.flush();
        
        // out.close();
        
        // fw.close();
        
        // os.close();
        
        try {
        	URI uri = new URIBuilder()
        	.setScheme("http")
        	.setHost("ws.audioscrobbler.com")
        	.setPath("/2.0/")
        	.setParameter("method", "track.getsimilar")
        	.setParameter("artist", "cher")
        	.setParameter("track", "believe")
        	.setParameter("limit", "100")
        	.setParameter("api_key", "88858618961414f8bec919bddd057044")
        	.build();
        
        	
        	// new URIBuilder().
        	HttpGet request = new HttpGet(uri);
        	
        	// request.
        	// This is useful for last.fm logging and preventing them from blocking this client
        	request.setHeader(HttpHeaders.USER_AGENT, "nileshmore@gatech.edu - ClassAssignment at GeorgiaTech Non-commercial use");
        	
            HttpGet httpGet = new HttpGet("http://ws.audioscrobbler.com/2.0/?method=track.getsimilar&artist=cher&track=believe&limit=4&api_key=88858618961414f8bec919bddd057044");
            CloseableHttpResponse response = httpclient.execute(request);
            
            int statusCode = response.getStatusLine().getStatusCode();
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder(); 
            // The underlying HTTP connection is still held by the response object
            // to allow the response content to be streamed directly from the network socket.
            // In order to ensure correct deallocation of system resources
            // the user MUST call CloseableHttpResponse#close() from a finally clause.
            // Please note that if response content is not fully consumed the underlying
            // connection cannot be safely re-used and will be shut down and discarded
            // by the connection manager.
            try {
                if(statusCode == 200)
                {
                	HttpEntity entity1 = response.getEntity();
                	BufferedReader br = new BufferedReader(new InputStreamReader((response.getEntity().getContent())));
                	Document document = builder.parse((response.getEntity().getContent()));
                	Element root = document.getDocumentElement();
                	root.normalize();
                	// Need to focus and resolve this part
                	NodeList nodes; 
                	nodes = root.getChildNodes();
                	
                		
                	nodes = root.getElementsByTagName("track");
                	if(nodes.getLength() == 0)
                	{
                		// System.out.println("empty");
                		return;
                	}
                	Node trackNode;	
                	for (int k = 0; k < nodes.getLength(); k++) // can access all tracks now
                	{
                		trackNode = nodes.item(k);
                		NodeList trackAttributes = trackNode.getChildNodes();
                		
                		// check if mbid is present in track attributes
                		// System.out.println("Length  " + (trackAttributes.item(5).getNodeName().compareToIgnoreCase("mbid") == 0));
                		
                		if ((trackAttributes.item(5).getNodeName().compareToIgnoreCase("mbid") == 0))
                		{
                			if (((Element)trackAttributes.item(5)).hasChildNodes())
                				;// System.out.println("Go aHead");
                			else continue;
                		}
                		else continue;
                		
                		for (int n = 0; n < trackAttributes.getLength(); n++)
                		{
                			Node attribute = trackAttributes.item(n);
                			if ((attribute.getNodeName().compareToIgnoreCase("name")) == 0){
      							 // System.out.println(((Element)attribute).getFirstChild().getNodeValue());
      							 trackName = ((Element)attribute).getFirstChild().getNodeValue(); // make string encoding as UTF-8 ************ 
      							 
      						}
      						
      						if ((attribute.getNodeName().compareToIgnoreCase("mbid")) == 0){
      							 // System.out.println(n +  "   " +  ((Element)attribute).getFirstChild().getNodeValue());
      							sourceMbid = attribute.getFirstChild().getNodeValue(); 
      							 
      						}
      						
      						if ((attribute.getNodeName().compareToIgnoreCase("artist")) == 0)
      						{
      							NodeList ArtistNodeList = attribute.getChildNodes();
      							for(int j = 0; j < ArtistNodeList.getLength(); j++)
      							{
      								Node Artistnode = ArtistNodeList.item(j);
      								if ((Artistnode.getNodeName().compareToIgnoreCase("name")) == 0)
      								{
      		  							 // System.out.println(((Element)Artistnode).getFirstChild().getNodeValue());
      		  							 artistName = ((Element)Artistnode).getFirstChild().getNodeValue();
      		  						}
      							}
      						}
                		}
                		out.print(sourceMbid);
                		out.print(",");
                		out.print(trackName);
                		out.print(",");
                		out.println(artistName);
                		// out.print(",");
                		findSimilarTracks(track_id_out, sourceMbid, trackName, artistName);
          
                	}
                	track_id_out.flush();

                	
                	out.flush();
                	out.close();
                	track_id_out.close();
                	track_os.close();
                	
                	// fw.close();
                	Element trac = (Element)nodes.item(0);
                			// trac.normalize();
                			nodes = trac.getChildNodes();
                	// System.out.println(nodes.getLength());
                	
					for(int i = 0; i < nodes.getLength(); i++){
  						Node node = nodes.item(i);
  						// System.out.println(node.getNodeName());
  						if ((node.getNodeName().compareToIgnoreCase("name")) == 0){
  							// System.out.println(((Element)node).getFirstChild().getNodeValue());
  						}
  						
  						if ((node.getNodeName().compareToIgnoreCase("mbid")) == 0){
  							// System.out.println(((Element)node).getFirstChild().getNodeValue());
  						}
  						
  						if ((node.getNodeName().compareToIgnoreCase("artist")) == 0){
  							
  							// System.out.println("Well");
  							NodeList ArtistNodeList = node.getChildNodes();
  							for(int j = 0; j < ArtistNodeList.getLength(); j++){
  								Node Artistnode = ArtistNodeList.item(j);
  								if ((Artistnode.getNodeName().compareToIgnoreCase("name")) == 0){
  		  							/* System.out.println(((Element)Artistnode).getFirstChild().getNodeValue());*/
  		  						}
  								/*System.out.println(Artistnode.getNodeName());*/
  							}
  						}
  						
  					}
  						/*if(node instanceof Element){
    						//a child element to process
    						Element child = (Element) node;
    						String attribute = child.getAttribute("width");
  						}*/
					
                	
                	// System.out.println(root.getAttribute("status"));
                	NodeList tracks = root.getElementsByTagName("track");
                	Element track = (Element)tracks.item(0);
                	// System.out.println(track.getTagName());
                	track.getChildNodes();
                	
                	}
                else
                {
                	System.out.println("failed with status" + response.getStatusLine());
                }
                // input = (ByteArrayInputStream)entity1.getContent();
                // do something useful with the response body
                // and ensure it is fully consumed
            } 
            finally {
                response.close();
            }
        }
        
        finally {
        	System.out.println("Exited succesfully.");
            httpclient.close();
            
        }
    }

    
}

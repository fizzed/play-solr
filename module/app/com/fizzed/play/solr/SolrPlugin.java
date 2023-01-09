package com.fizzed.play.solr;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.SolrPingResponse;

import play.Application;
import play.Configuration;
import play.Logger;
import play.Plugin;
import play.libs.Akka;
import scala.concurrent.duration.FiniteDuration;
import akka.dispatch.MessageDispatcher;

public class SolrPlugin extends Plugin {

	private String url;
	private String site;
	private IndexingHandler indexingHandler;
	private String indexingDispatcherName;
	private long indexingInterval;
	// create at startup
	private SolrServer server;

    private final Application application;

    public SolrPlugin(Application application) {
        this.application = application;
    }

    /**
     * Reads the configuration file.
     */
    @Override
    public void onStart() {
        Configuration configuration = application.configuration();
        this.url = configuration.getString(PluginConstants.SOLR_URL);
        this.site = configuration.getString(PluginConstants.SOLR_SITE);
        
        // if an indexing handler is defined
        if (configuration.keys().contains(PluginConstants.SOLR_INDEXING_HANDLER)) {
            String indexingHandlerName = null;
            try {
            	indexingHandlerName = configuration.getString(PluginConstants.SOLR_INDEXING_HANDLER);
            	indexingHandler = (IndexingHandler)Class.forName(indexingHandlerName, true, application.classloader()).newInstance();
            } catch (Exception e) {
                throw configuration.reportError(PluginConstants.SOLR_INDEXING_HANDLER, "Unable to create indexing handler: " + indexingHandlerName, e);
            }
        } else {
            Logger.warn("No indexing handler declared for solr search plugin");
        }
        
        // create server and verify it works
        this.server = new HttpSolrServer(this.url);
        silentPing();
        
        // if an indexing handler exists, check for other required configs and start job
        if (this.indexingHandler != null) {
        	this.indexingDispatcherName = configuration.getString(PluginConstants.SOLR_INDEXING_DISPATCHER_NAME, "SolrIndexingJob");
        	
        	// if indexing-handler specified, create it!
            this.indexingInterval = configuration.getMilliseconds(PluginConstants.SOLR_INDEXING_INTERVAL);
            
            Logger.info("Scheduling job [" + this.indexingDispatcherName + "] to run every [" + this.indexingInterval + " ms]");
            MessageDispatcher dispatcher = Akka.system().dispatchers().lookup(this.indexingDispatcherName);
    	    Akka.system().scheduler().schedule(
	        	FiniteDuration.create(0, TimeUnit.MILLISECONDS),
	        	FiniteDuration.create(this.indexingInterval, TimeUnit.MILLISECONDS),
	            new IndexingJob(this.indexingHandler),
	            dispatcher
	        );
        }
        
        Logger.info("Solr search plugin started for site [" + this.site + "] via solr [" + this.url + "]");
    }

    public void ping() throws SolrServerException, IOException {
    	// throws an exception if the server isn't reachable
    	SolrPingResponse response = this.server.ping();
    	Logger.debug("Ping to solr server [" + this.url + "] ok (" + response.getElapsedTime() + " ms)");
    }
    
    public boolean silentPing() {
    	try {
    		ping();
    		return true;
    	} catch (Exception e) {
    		Logger.warn("Solr server down: " + e.getMessage());
    		return false;
    	}
    }
    
    public SolrServer server() {
    	return server;
    }
    
    /**
     * E.g. "http://localhost:8983/solr"
     */
	public String url() {
		return url;
	}

	/**
	 * E.g. mfizz.com
	 */
	public String site() {
		return site;
	}
	
	public IndexingHandler indexingHandler() {
		return this.indexingHandler;
	}
	
	public long indexingInterval() {
		return indexingInterval;
	}
	
}

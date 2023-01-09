package com.fizzed.play.solr;

import play.Logger;
import play.Play;

public class IndexingJob implements Runnable {

	private final IndexingHandler handler;
	
	public IndexingJob(IndexingHandler handler) {
		super();
		this.handler = handler;
	}

	@Override
	public void run() {
		// need to get a reference to the plugin
		SolrPlugin plugin = Play.application().plugin(SolrPlugin.class);
		long start = System.currentTimeMillis();
		Logger.info("Starting solr search (re)indexing...");
		try {
			handler.reindex(plugin);
			long stop = System.currentTimeMillis();
			Logger.info("Completed solr search (re)index (in " + (stop-start) + " ms)");
		} catch (Exception e) {
			long stop = System.currentTimeMillis();
			Logger.error("Unable to successfully (re)index (in " + (stop-start) + " ms)", e);
		}
	}

}

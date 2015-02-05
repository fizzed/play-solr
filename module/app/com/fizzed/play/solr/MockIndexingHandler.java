package com.fizzed.play.solr;

import play.Logger;

public class MockIndexingHandler extends IndexingHandler {

	@Override
	public void index(SolrSearchPlugin plugin) throws Exception {
		Logger.info("Mock index running...");
		plugin.server().commit();
	}

}

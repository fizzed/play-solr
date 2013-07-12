package com.mfizz.play.solrsearch;

public abstract class IndexingHandler {
	
	abstract public void index(SolrSearchPlugin plugin) throws Exception;
	
	public void delete(SolrSearchPlugin plugin, boolean commit) throws Exception {
		// by default delete all documents w/ matching site
		plugin.server().deleteByQuery("site:" + plugin.site());
		if (commit) {
			plugin.server().commit();
		}
	}
	
	public void reindex(SolrSearchPlugin plugin) throws Exception {
		// default is to delete (but not commit), then index
		delete(plugin, false);
		index(plugin);
	}
	
}

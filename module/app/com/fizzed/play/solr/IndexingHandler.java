package com.fizzed.play.solr;

public abstract class IndexingHandler {
	
	abstract public void index(SolrPlugin plugin) throws Exception;
	
	public void delete(SolrPlugin plugin, boolean commit) throws Exception {
		// by default delete all documents w/ matching site
		plugin.server().deleteByQuery("site:" + plugin.site());
		if (commit) {
			plugin.server().commit();
		}
	}
	
	public void reindex(SolrPlugin plugin) throws Exception {
		// default is to delete (but not commit), then index
		delete(plugin, false);
		index(plugin);
	}
	
}

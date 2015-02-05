package com.fizzed.play.solr;

import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;

public class SolrUtil {
	
	/** TODO: not working */
	/**
	public static String highlighting(QueryResponse response, SolrDocument doc) {
		String id = (String)doc.getFieldValue("id");
		List<String> highlights = response.getHighlighting().get(id).get("content");
		if (highlights != null && highlights.size() > 0) {
			return highlights.get(0);
		} else {
			return "";
		}
	}
	*/
	
	public static String absoluteUrl(SolrDocument doc) {
		String url = (String)doc.getFirstValue("url");
		// if it's not an absolute url -- append first site
		if (url.startsWith("/")) {
			url = "http://" + play.mvc.Http.Context.current().request().host() + url;
		}
		return url;
	}
	
	public static int relevanceStars(SolrDocumentList results, SolrDocument doc) {
		float relevance = relevancePctAsFloat(results, doc);
		if (relevance >= 0.95f) {
			return 5;
		} else if (relevance >= 0.80f) {
			return 4;
		} else if (relevance >= 0.60f) {
			return 3;
		} else if (relevance >= 0.40f) {
			return 2;
		} else {
			return 1;
		} 
	}
	
	public static String[] relevanceIcons(SolrDocumentList results, SolrDocument doc) {
		float relevance = relevancePctAsFloat(results, doc);
		if (relevance >= 1f) {
			return new String[] { "star", "star", "star", "star", "star" };
		} else if (relevance >= 0.90f) {
			return new String[] { "star", "star", "star", "star", "star-half-full" };
		} else if (relevance >= 0.80f) {
			return new String[] { "star", "star", "star", "star", "star-empty" };
		} else if (relevance >= 0.70f) {
			return new String[] { "star", "star", "star", "star-half-full", "star-empty" };
		} else if (relevance >= 0.60f) {
			return new String[] { "star", "star", "star", "star-empty", "star-empty" };
		} else if (relevance >= 0.50f) {
			return new String[] { "star", "star", "star-half-full", "star-empty", "star-empty" };
		} else if (relevance >= 0.40f) {
			return new String[] { "star", "star", "star-empty", "star-empty", "star-empty" };
		} else if (relevance >= 0.30f) {
			return new String[] { "star", "star-half-full", "star-empty", "star-empty", "star-empty" };
		} else if (relevance >= 0.10f) {
			return new String[] { "star", "star-empty", "star-empty", "star-empty", "star-empty" };
		} else if (relevance > 0f) {
			return new String[] { "star-half-full", "star-empty", "star-empty", "star-empty", "star-empty" };
		} else {
			return new String[] { "star-empty", "star-empty", "star-empty", "star-empty", "star-empty" };
		}
	}
	
	public static float relevancePctAsFloat(SolrDocumentList results, SolrDocument doc) {
		float maxScore = results.getMaxScore();
		float docScore = (float)doc.getFirstValue("score");
		return docScore / maxScore;
	}

	public static String relevancePct(SolrDocumentList results, SolrDocument doc) {
		float relevancePct = relevancePctAsFloat(results, doc) * 100;
		return String.format("%.1f", relevancePct);
	}
	
}

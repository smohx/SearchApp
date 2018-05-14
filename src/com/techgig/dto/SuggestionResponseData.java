package com.techgig.dto;

import java.util.List;

public class SuggestionResponseData {
	String query;
	List<SuggestionData> suggestions;
	public String getQuery() {
		return query;
	}
	public void setQuery(String query) {
		this.query = query;
	}
	public List<SuggestionData> getSuggestions() {
		return suggestions;
	}
	public void setSuggestions(List<SuggestionData> suggestions) {
		this.suggestions = suggestions;
	}
	
	

}

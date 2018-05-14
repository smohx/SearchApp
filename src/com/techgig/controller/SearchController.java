package com.techgig.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.techgig.dto.SuggestionData;
import com.techgig.dto.SuggestionResponseData;
import com.techgig.service.SearchService;



@Controller
public class SearchController {

	@Autowired
	SearchService searchService;
	
	@RequestMapping(value ="/",method=RequestMethod.GET)
	public ModelAndView adminLogin() {

		ModelAndView model = new ModelAndView();
		model.setViewName("home");
		return model;

	}
	
	@ResponseBody
	@RequestMapping(value ="/getsuggestion",method=RequestMethod.GET)
	public SuggestionResponseData getSuggestion(@RequestParam(value = "query", required = true) String query) {

		List<SuggestionData> result = new ArrayList<SuggestionData>(); 
		for(String value:searchService.getSuggestion(query))
		{
			SuggestionData s = new SuggestionData();
			s.setValue(value);
			s.setData("X");
			result.add(s);
		}
		SuggestionResponseData response = new SuggestionResponseData();
		response.setSuggestions(result);
		response.setQuery(query);
		return response;
	}
	
	

}

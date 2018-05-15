package com.techgig.util;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;

import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.RegexNameFinder;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import opennlp.tools.util.Span;

public class CategoryDetectorUtility {
	private InputStream inputStream;
	private InputStream tokenInputStream;
	private TokenizerModel tokenizerModel;
	private Tokenizer tokenizer;
	private TokenNameFinderModel nerModel;

	public CategoryDetectorUtility(String modelFile) {
		Objects.nonNull(modelFile);

		initNERModel(modelFile);


	}


	private void initNERModel(String modelFile) {
		try {
			//inputStream = new FileInputStream(modelFile);
			tokenInputStream = new FileInputStream("WebContent\\resources\\file\\nlp\\en-token.bin");
			//nerModel = new TokenNameFinderModel(inputStream);
			tokenizerModel = new TokenizerModel(tokenInputStream);
			tokenizer = new TokenizerME(tokenizerModel);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}



	}



	public Map<String,String> getNERCategory(String text) {

		NameFinderME nameFinder = new NameFinderME(nerModel);
		System.out.println(text);
		String tokens[] = tokenizer.tokenize(text);
		Span nameSpans[] = nameFinder.find(tokens); 
		double []prob = nameFinder.probs(nameSpans);
		Map<String,String> result = displayEntity(nameSpans, tokens,prob);

		return result;
	}

		private static Map<String,String> displayEntity(Span[] nameSpans, String[] sentence, double []prob) {
			Map<String,String> result = new HashMap<String,String>();
			for (int si = 0; si < nameSpans.length; si++) {
				StringBuilder cb = new StringBuilder();
				for (int ti = nameSpans[si].getStart(); ti < nameSpans[si].getEnd(); ti++) {
					cb.append(sentence[ti]).append(" ");
				}
				result.put(nameSpans[si].getType(), cb.substring(0, cb.length() - 1));
				System.out.println(cb.substring(0, cb.length() - 1) + " : "
						+ nameSpans[si].getType()+" : "+ prob[si]);
			}
			return result;
		}
	
	public List<String> tokenizeNumeric(String text)
	{
		List<String> numlist = new ArrayList<String>();
		Pattern testPattern = Pattern.compile("[0-9]+");
		String sentence[] = tokenizer.tokenize(text);

		Pattern[] patterns = new Pattern[]{testPattern};
		Map<String, Pattern[]> regexMap = new HashMap<>();
		String type = "testtype";

		regexMap.put(type, patterns);

		RegexNameFinder finder = new RegexNameFinder(regexMap);

		Span[] result = finder.find(sentence);
		for(int sp = 0; sp < result.length; sp++)
		{
			StringBuilder builder = new StringBuilder();
			for (int i = result[sp].getStart(); i < result[sp].getEnd(); i++) {
			    builder.append(sentence[i]).append(" ");
			}
			String name = builder.toString();
			numlist.add(name);
		}
		return numlist;
	}

}
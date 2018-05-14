package com.techgig.service.impl;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.RAMDirectory;
import org.codehaus.plexus.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.techgig.service.SearchService;
import com.techgig.util.CategoryDetectorUtility;

public class SearchServiceImpl implements SearchService {

	@Autowired
	RAMDirectory ramDir;

	private static final String INDEX_DIR_P = "WebContent\\resources\\file\\luceneindex\\product";
	private static final String INDEX_DIR_C = "WebContent\\resources\\file\\luceneindex\\classifier";
	private String modelFile = "WebContent\\resources\\file\\nlp\\cat_train.bin";


	@Override
	public List<String> getSuggestion(String searchQuery)   {

		List <String> result = new ArrayList<String>();



		String query = searchQuery + "*";
		try
		{
			CategoryDetectorUtility detector = new CategoryDetectorUtility(modelFile);



			IndexSearcher catSearcher = createSearcherClassification();

			TopDocs catDocs = searchForCategory(query, catSearcher);
			Map<String,String> classMap = new HashMap<String,String>();
			List<String> tokens = new ArrayList<String>();
			for (ScoreDoc sd : catDocs.scoreDocs)
			{
				Document d = catSearcher.doc(sd.doc);
				String cat =d.get("category");
				if(classMap.get(cat) == null)
				{
					String s = d.get("keyword");
					classMap.put(cat,s);
					if(!s.equals("comparator"))
					{
						tokens.add(s);
					}
				}
			}
			detector.getNERCategory(searchQuery);
			for(String s:classMap.keySet()) {
				if(!s.equals("comparator"))
				{
					result.add(StringUtils.capitalise(s)+" : "+classMap.get(s));
				}
			}

			List<Integer> pricelist = validatePrices(detector.tokenizeNumeric(searchQuery));


			if(!pricelist.isEmpty())
			{
				int lowerlimit = -1;
				int upperlimit = -1;

				if(pricelist.size()==1) {
					upperlimit = pricelist.get(0);
				}
				for(int s:pricelist)
				{
					if(s < lowerlimit || lowerlimit == -1)
					{
						lowerlimit = s;
					}
					if(s > upperlimit || upperlimit == -1)
					{
						upperlimit = s;
					}
				}
				if(lowerlimit == upperlimit)
				{
					lowerlimit = 0;
				}
				result.add("Price Range : " + lowerlimit +" to " + upperlimit);
			}


		}
		catch(Exception e)
		{
			e.printStackTrace();
		}


		return result;
	}

	@SuppressWarnings("unused")
	private static TopDocs searchByName(String name, IndexSearcher searcher) throws Exception
	{
		QueryParser qp = new QueryParser("title2", new StandardAnalyzer());
		qp.setAllowLeadingWildcard(true);
		Query firstNameQuery = qp.parse(name);

		TopDocs hits = searcher.search(firstNameQuery, 10);
		return hits;
	}

	private static TopDocs searchForCategory(String keyword, IndexSearcher searcher) throws Exception
	{
		QueryParser qp = new QueryParser("keyword", new StandardAnalyzer());
		qp.setAllowLeadingWildcard(true);
		Query firstNameQuery = qp.parse(keyword);  
		TopDocs hits = searcher.search(firstNameQuery, 10);
		return hits;
	}

	@SuppressWarnings("unused")
	private static IndexSearcher createSearcherProduct() throws IOException {
		Directory dir = FSDirectory.open(Paths.get(INDEX_DIR_P));
		IndexReader reader = DirectoryReader.open(dir);
		IndexSearcher searcher = new IndexSearcher(reader);
		return searcher;
	}

	private static IndexSearcher createSearcherClassification() throws IOException {
		Directory dir = FSDirectory.open(Paths.get(INDEX_DIR_C));
		IndexReader reader = DirectoryReader.open(dir);
		IndexSearcher searcher = new IndexSearcher(reader);
		return searcher;
	}

	private List<Integer> validatePrices(List<String> prices)
	{
		List<Integer> result = new ArrayList<Integer>();
		for(String s:prices) {
			int price = Integer.parseInt(s.trim());
			if(price>100) {
				result.add(price);
			}
		}
		return result;
	}


}

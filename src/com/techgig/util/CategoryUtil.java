package com.techgig.util;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.TextField;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.RAMDirectory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;

import com.techgig.bean.Classifier;
import com.techgig.bean.Product;

import opennlp.tools.ml.AbstractTrainer;
import opennlp.tools.ml.naivebayes.NaiveBayesTrainer;
import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.RegexNameFinder;
import opennlp.tools.tokenize.TokenSample;
import opennlp.tools.tokenize.TokenSampleStream;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.TokenizerFactory;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import opennlp.tools.util.MarkableFileInputStreamFactory;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.PlainTextByLineStream;
import opennlp.tools.util.Span;
import opennlp.tools.util.TrainingParameters;

public class CategoryUtil {

	private static final String INDEX_DIR_P = "WebContent\\resources\\file\\luceneindex\\product";
	private static final String INDEX_DIR_C = "WebContent\\resources\\file\\luceneindex\\classifier";

	public static RAMDirectory productIndex = new RAMDirectory();

	public static RAMDirectory classifierIndex = new RAMDirectory();
	
	static final ClassLoader loader = CategoryUtil.class.getClassLoader();

	public static InputStream tokenInputStream;
	public static TokenizerModel tokenizerModel;
	public static Tokenizer tokenizer;


	public static void initialize() {


		try 
		{
			performIndexProduct();
			performIndexClassifier();

			String modelFile = "WebContent\\resources\\file\\nlp\\cat_train.bin";
			String inputFile = "WebContent\\resources\\file\\nlp\\training_data.txt";
			String modelFileToken = "WebContent\\resources\\file\\nlp\\cat_train_token.bin";
			String inputFileToken = "WebContent\\resources\\file\\nlp\\token_training_data.txt";

			//CategoryTrainUtility.trainNERModel(inputFile, modelFile);
			//trainTokenizer(inputFileToken, modelFileToken);
			initNERModel();

		} 
		catch (IOException e) {
			e.printStackTrace();
		}

	}

	private static void performIndexProduct() throws IOException {
		IndexWriter writer = createWriter(productIndex);
		System.out.println("Started Deleting Existing Indexed Product Data");
		writer.deleteAll();
		writer.commit();
		System.out.println("Finished Deleting Existing Indexed Product Data");
		System.out.println("Started Reading Product Data from CSV");
		List<Product> productList = new CSVReader().parseProductData();
		System.out.println("Finished Reading Product Data from CSV");

		System.out.println("Startd Indexing " + productList.size() + " products");
		List<Document> documents = new ArrayList<Document>();
		for(Product product:productList) {
			Document doc = new Document();
			doc.add( new TextField("vesion", product.getVesion(), Store.YES));
			doc.add( new TextField("title", product.getTitle(), Store.YES));
			doc.add( new TextField("description", product.getDescription(), Store.YES));
			doc.add( new TextField("link", product.getLink(), Store.YES));
			doc.add( new TextField("id", product.getId(), Store.YES));
			doc.add( new TextField("itemgroupid", product.getItemgroupid(), Store.YES));
			doc.add( new TextField("title2", product.getTitle2(), Store.YES));
			doc.add( new TextField("link3", product.getLink3(), Store.YES));
			doc.add( new TextField("description4", product.getDescription4(), Store.YES));
			doc.add( new TextField("googleproductcategory", product.getGoogleproductcategory(), Store.YES));
			doc.add( new TextField("l2category", product.getL2category(), Store.YES));
			doc.add( new TextField("producttype", product.getProducttype(), Store.YES));
			doc.add( new TextField("imagelink", product.getImagelink(), Store.YES));
			doc.add( new TextField("condition", product.getCondition(), Store.YES));
			doc.add( new TextField("size", product.getSize(), Store.YES));
			doc.add( new TextField("color", product.getColor(), Store.YES));
			doc.add( new TextField("availability", product.getAvailability(), Store.YES));
			doc.add( new TextField("price", product.getPrice(), Store.YES));
			doc.add( new TextField("brand", product.getBrand(), Store.YES));
			doc.add( new TextField("shipping", product.getShipping(), Store.YES));
			doc.add( new TextField("saleprice", product.getSaleprice(), Store.YES));
			doc.add( new TextField("totaldiscount", product.getTotaldiscount(), Store.YES));
			doc.add( new TextField("pattern", product.getPattern(), Store.YES));
			doc.add( new TextField("adult", product.getAdult(), Store.YES));
			doc.add( new TextField("customlabel3", product.getCustomlabel3(), Store.YES));
			doc.add( new TextField("customlabel2", product.getCustomlabel2(), Store.YES));
			doc.add( new TextField("customlabel4", product.getCustomlabel4(), Store.YES));
			doc.add( new TextField("gtin", product.getGtin(), Store.YES));
			doc.add( new TextField("gender", product.getGender(), Store.YES));
			doc.add( new TextField("material", product.getMaterial(), Store.YES));
			documents.add(doc);

		}

		writer.addDocuments(documents);
		writer.commit();
		System.out.println("Finished Indexing " + productList.size() + " products");
		writer.close();

	}

	private static void performIndexClassifier() throws IOException {
		IndexWriter writer = createWriter(classifierIndex);
		System.out.println("Started Deleting Existing Indexed Classifier Data");
		writer.deleteAll();
		writer.commit();
		System.out.println("Finished Deleting Existing Indexed Classifier Data");
		System.out.println("Started Reading Classifier Data from CSV");
		List<Classifier> classifierList = new CSVReader().parseClassifierData();
		System.out.println("Finished Reading Classifier Data from CSV");

		System.out.println("Startd Indexing " + classifierList.size() + " classification items");
		List<Document> documents = new ArrayList<Document>();
		for(Classifier classifier:classifierList) {
			Document doc = new Document();
			doc.add( new TextField("keyword", classifier.getKeyword(), Store.YES));
			doc.add( new TextField("category", classifier.getCategory(), Store.YES));
			documents.add(doc);

		}

		writer.addDocuments(documents);
		writer.commit();
		System.out.println("Finished Indexing " + classifierList.size() + " classification items");
		writer.close();

	}
	private static IndexWriter createWriter(RAMDirectory index) throws IOException
	{
		//FSDirectory dir = FSDirectory.open(Paths.get(path));
		IndexWriterConfig config = new IndexWriterConfig(new StandardAnalyzer());
		IndexWriter writer = new IndexWriter(index, config);
		return writer;
	}

	public static void initNERModel() {
		try {
			//inputStream = new FileInputStream(modelFile);
			String root = System.getProperty("user.dir");
			tokenInputStream = new FileInputStream(root+"/WebContent/resources/file/nlp/en-token.bin");
			//nerModel = new TokenNameFinderModel(inputStream);
			tokenizerModel = new TokenizerModel(tokenInputStream);
			tokenizer = new TokenizerME(tokenizerModel);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}



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

	public static List<String> tokenizeNumeric(String text)
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

	public static void trainTokenizer(String inputFile, String modelFile)throws IOException {
		MarkableFileInputStreamFactory factory = new MarkableFileInputStreamFactory(new File(inputFile));
		ObjectStream<String> lineStream = new PlainTextByLineStream(factory, "UTF-8");
		TokenizerModel model;

		ObjectStream<TokenSample> sampleStream = new TokenSampleStream(lineStream);
		try {
			TrainingParameters params = new TrainingParameters();
			params.put(TrainingParameters.ITERATIONS_PARAM, 1000+"");
			params.put(TrainingParameters.CUTOFF_PARAM, 0+"");
			params.put(AbstractTrainer.ALGORITHM_PARAM, NaiveBayesTrainer.NAIVE_BAYES_VALUE);

			model = TokenizerME.train(sampleStream, new TokenizerFactory("",null,true,null) , params);
		}
		finally {
			sampleStream.close();
		}

		OutputStream modelOut = null;
		try {
			modelOut = new BufferedOutputStream(new FileOutputStream(modelFile));
			model.serialize(modelOut);
		} finally {
			if (modelOut != null)
				modelOut.close();
		}
	}

}

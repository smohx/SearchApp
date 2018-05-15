package com.techgig.startup;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;

import org.apache.lucene.store.FSDirectory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import com.techgig.bean.Classifier;
import com.techgig.bean.Product;
import com.techgig.util.CSVReader;
import com.techgig.util.CategoryTrainUtility;



@Component
public class SystemInit implements ApplicationListener<ContextRefreshedEvent> {

	/*public void performIndex() {
		try {
			String urlString = "http://localhost:8983/solr/mystore";
			HttpSolrClient solr = new HttpSolrClient.Builder(urlString).build();
			System.out.println("Deleting existing SOLR items");
			solr.commit();

			solr.deleteByQuery("*:*");
			System.out.println("Started SOLR Inexing");
			solr.setParser(new XMLResponseParser());
			List<Product> productList = new CSVReader().parseFile();
			System.out.println("Indexing " + productList.size() + " items");
			for(Product product:productList) {
				solr.addBean(product);
			}
			solr.commit();
			System.out.println("Finished SOLR Inexing");
		} catch (IOException | SolrServerException e) {
			System.out.println("Exception in SOLR indexing");
			e.printStackTrace();
		}

	}*/


	private static final String INDEX_DIR_P = "/app/WebContent/resources/file/luceneindex/product";
	private static final String INDEX_DIR_C = "/app/WebContent/resources/file/luceneindex/classifier";
	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {

		if(event.getApplicationContext().getParent() == null)
		{
			try 
			{
				performIndexProduct();
				performIndexClassifier();
				
				String modelFile = "/app/WebContent/resources/file/nlp/cat_train.bin";
				String inputFile = "/app/WebContent/resources/file/nlp/training_data.txt";
				String modelFileToken = "/app/WebContent/resources/file/nlp/cat_train_token.bin";
				String inputFileToken = "/app/WebContent/resources/file/nlp/token_training_data.txt";

				CategoryTrainUtility.trainNERModel(inputFile, modelFile);
				CategoryTrainUtility.trainTokenizer(inputFileToken, modelFileToken);
			} 
			catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void performIndexProduct() throws IOException {
		IndexWriter writer = createWriter(INDEX_DIR_P);
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

	private void performIndexClassifier() throws IOException {
		IndexWriter writer = createWriter(INDEX_DIR_C);
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
	private static IndexWriter createWriter(String path) throws IOException
	{
		FSDirectory dir = FSDirectory.open(Paths.get(path));
		IndexWriterConfig config = new IndexWriterConfig(new StandardAnalyzer());
		IndexWriter writer = new IndexWriter(dir, config);
		return writer;
	}
}

package com.techgig.util;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import com.techgig.bean.Classifier;
import com.techgig.bean.Product;

public class CSVReader {

	public  List<Product> parseProductData(){

		BufferedReader br = null;

		List<Product> sourceList = new ArrayList<Product>();

		try {
			String root = System.getProperty("user.dir");
			Reader reader = Files.newBufferedReader(Paths.get(root+"/WebContent/resources/file/product_data.csv"));
			CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT
					.withFirstRecordAsHeader()
					.withIgnoreHeaderCase()
					.withTrim());

			for (CSVRecord csvRecord : csvParser) {

				Product product = new Product();
				product.setVesion(csvRecord.get(0));
				product.setTitle(csvRecord.get(1));
				product.setDescription(csvRecord.get(2));
				product.setLink(csvRecord.get(3));
				product.setId(csvRecord.get(4));
				product.setItemgroupid(csvRecord.get(5));
				product.setTitle2(csvRecord.get(6));
				product.setLink3(csvRecord.get(7));
				product.setDescription4(csvRecord.get(8));
				product.setGoogleproductcategory(csvRecord.get(9));
				product.setL2category(csvRecord.get(10));
				product.setProducttype(csvRecord.get(11));
				product.setImagelink(csvRecord.get(12));
				product.setCondition(csvRecord.get(13));
				product.setSize(csvRecord.get(14));
				product.setColor(csvRecord.get(15));
				product.setAvailability(csvRecord.get(16));
				product.setPrice(csvRecord.get(17));
				product.setBrand(csvRecord.get(18));
				product.setShipping(csvRecord.get(19));
				product.setSaleprice(csvRecord.get(20));
				product.setTotaldiscount(csvRecord.get(21));
				product.setPattern(csvRecord.get(22));
				product.setAdult(csvRecord.get(23));
				product.setCustomlabel3(csvRecord.get(24));
				product.setCustomlabel2(csvRecord.get(25));
				product.setCustomlabel4(csvRecord.get(26));
				product.setGtin(csvRecord.get(27));
				product.setGender(csvRecord.get(28));
				product.setMaterial(csvRecord.get(29));


				sourceList.add(product);
			}
			csvParser.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {

			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return sourceList;
	}

	public  List<Classifier> parseClassifierData(){

		BufferedReader br = null;

		List<Classifier> sourceList = new ArrayList<Classifier>();

		try {
			String root = System.getProperty("user.dir");
			Reader reader = new BufferedReader(new InputStreamReader(new FileInputStream(root+"/WebContent/resources/file/classification_data.csv"),"utf-8"));
					
					//Files.newBufferedReader(Paths.get());
			CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT
					.withFirstRecordAsHeader()
					.withIgnoreHeaderCase()
					.withTrim());

			for (CSVRecord csvRecord : csvParser) {

				Classifier classifier = new Classifier();
				classifier.setKeyword(csvRecord.get(0));
				classifier.setCategory(csvRecord.get(1));

				sourceList.add(classifier);
			}
			csvParser.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {

			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return sourceList;
	}
}

package com.techgig.util;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;

import opennlp.tools.ml.AbstractTrainer;
import opennlp.tools.ml.naivebayes.NaiveBayesTrainer;
import opennlp.tools.namefind.BioCodec;
import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.NameSample;
import opennlp.tools.namefind.NameSampleDataStream;
import opennlp.tools.namefind.TokenNameFinderFactory;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.tokenize.TokenSample;
import opennlp.tools.tokenize.TokenSampleStream;
import opennlp.tools.tokenize.TokenizerFactory;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import opennlp.tools.util.MarkableFileInputStreamFactory;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.PlainTextByLineStream;
import opennlp.tools.util.TrainingParameters;

public class CategoryTrainUtility {

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

	
	public static void trainNERModel(String inputFile, String modelFile)throws IOException {
		MarkableFileInputStreamFactory factory = new MarkableFileInputStreamFactory(new File(inputFile));
		ObjectStream<String> lineStream = new PlainTextByLineStream(factory, "UTF-8");
		TrainingParameters params = new TrainingParameters();
		params.put(TrainingParameters.ITERATIONS_PARAM, 1000+"");
		params.put(TrainingParameters.CUTOFF_PARAM, 1+"");
		params.put(AbstractTrainer.ALGORITHM_PARAM, NaiveBayesTrainer.NAIVE_BAYES_VALUE);
	
	
		ObjectStream<NameSample> sampleStream = new NameSampleDataStream(lineStream);
		
		TokenNameFinderModel model = null;
		try {
			model = NameFinderME.train("", null, sampleStream,
		        params, TokenNameFinderFactory.create(null, null, new HashMap<String,Object>(), new BioCodec()));
		} catch (IOException e) {
		    e.printStackTrace();
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
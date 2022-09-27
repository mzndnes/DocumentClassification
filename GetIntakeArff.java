
package com.smartdatasolutions.test;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.zip.ZipInputStream;

import javax.media.jai.PlanarImage;

import org.apache.torque.Torque;
import org.apache.torque.util.Criteria;
import org.jsoup.Jsoup;

import com.smartdatasolutions.common.Framework;
import com.smartdatasolutions.common.SDSFile;
import com.smartdatasolutions.imaging.ImageUtils;
import com.smartdatasolutions.imaging.SDSImage;
import com.smartdatasolutions.jobs.SDSJob;
import com.smartdatasolutions.machinelearning.MachineLearningInstanceUtils;
import com.smartdatasolutions.machinelearning.MachineLearningModelUtils;
import com.smartdatasolutions.machinelearning.filters.SDSEdgeHistogramFilter;
import com.smartdatasolutions.machinelearning.filters.SDSGridDensityFilter;
import com.smartdatasolutions.om.rcmwf.Customer;
import com.smartdatasolutions.om.rcmwf.CustomerPeer;
import com.smartdatasolutions.om.rcmwf.IntakeQueue;
import com.smartdatasolutions.om.rcmwf.MachineLearningModel;
import com.smartdatasolutions.om.rcmwf.MachineLearningModelPeer;
import com.smartdatasolutions.om.rcmwf.MachineLearningModel.MachineLearningModelLabel;

import weka.classifiers.Classifier;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ArffSaver;
import weka.core.converters.CSVLoader;
import weka.core.converters.ConverterUtils.DataSink;
import weka.filters.unsupervised.instance.imagefilter.EdgeHistogramFilter;

public class GetIntakeArff extends EdgeHistogramFilter{
	boolean							debug					= true;
	protected Classifier			classifier				= null;
	protected MachineLearningModel	classifierModel			= null;
	protected List< String >		classCategories			= null;
	Instances documentArff=null;
	
	protected Classifier			binaryClassifier		= null;
	protected MachineLearningModel	binaryClassifierModel	= null;
	protected List< String >		binaryClassCategories	= null;
	Instances binaryArff=null;
	
	static int f=0;
	
	
	final static String path="D:\\Users\\dmaharjan\\SDSRequired\\Project\\Intake\\";
	
	public GetIntakeArff() throws Exception {
		loadModel();
	}
	protected void debug( String debugString ) {
		if ( debug ) {
			System.out.println( debugString );
		}
	}
	
	protected void loadModel( ) throws Exception {
		Customer customer=getCustomer();

		if ( classifierModel == null ) {

			Criteria modelCriteria = new Criteria( );
			modelCriteria.add( MachineLearningModelPeer.MODEL_NAME, "DocumentClassification" );
			modelCriteria.add( MachineLearningModelPeer.CUSTOMER_ID, customer.getIdAsInt( ) );
			modelCriteria.setLimit( 1 );

			debug( "Loading DocumentClassification Model" );

			List< MachineLearningModel > modelList = MachineLearningModelPeer.doSelect( modelCriteria );

			if ( !modelList.isEmpty( ) ) {

				this.classifierModel = modelList.get( 0 );
				classCategories = classifierModel.getMachineLearningLabels( ).stream( ).map( MachineLearningModelLabel::getLabel_description ).collect( Collectors.toList( ) );
				//System.out.println(classCategories);
				//this.classifier = MachineLearningModelUtils.loadModel( this.classifierModel.getModelLocation( ) );

			} else {

				throw new Exception( "Unable to locate DocumentClassification model for " + getCustomer( ).getCode( ) + "." );

			}
		}
		
		

		if ( binaryClassifierModel == null ) {

			Criteria modelCriteria = new Criteria( );
			modelCriteria.add( MachineLearningModelPeer.MODEL_NAME, "BinaryDocumentClassification" );
			modelCriteria.add( MachineLearningModelPeer.CUSTOMER_ID, customer.getIdAsInt( ) );
			modelCriteria.setLimit( 1 );

			debug( "Loading BinaryDocumentClassification Model" );

			List< MachineLearningModel > modelList = MachineLearningModelPeer.doSelect( modelCriteria );

			if ( !modelList.isEmpty( ) ) {

				this.binaryClassifierModel = modelList.get( 0 );
				
				
				//this.binaryClassifier = MachineLearningModelUtils.loadModel( this.binaryClassifierModel.getModelLocation( ) );

			} else {

				throw new Exception( "Unable to locate BinaryDocumentClassification model for " + getCustomer( ).getCode( ) + "." );

			}
		}
		classCategories = classifierModel.getMachineLearningLabels( ).stream( ).map( MachineLearningModelLabel::getLabel_description ).collect( Collectors.toList( ) );
		binaryClassCategories = binaryClassifierModel.getMachineLearningLabels( ).stream( ).map( MachineLearningModelLabel::getLabel_description ).collect( Collectors.toList( ) );
		
	}
	
	
	public Customer getCustomer( ) {
		Customer customer=null;
		if ( customer == null ) {
			try {
				customer = CustomerPeer.retrieveByCode( "AGP" );
				
			} catch ( Exception e ) {
				System.out.println( "Customer not found for AGP - returning null" );
			}
		}

		return customer;
	}


	public void GetDataSet(String fileName) throws Exception {
		String line="";
		
		String label;
		
		boolean flg=true;
//		System.out.println(fileName);
		File csvFile = new File(fileName);
		try {
			FileReader reader = new FileReader(csvFile);
			BufferedReader br = new BufferedReader(reader);
			
			while ((line = br.readLine()) != null) {
				

				List<Object> documentObject = new ArrayList<>();
				List<Object> binaryObject = new ArrayList<>();
				String[] fields = line.split(",");
				String xmlFile = path + "XML1\\" + fields[1] ;
				String document = path + "Documents1\\" + fields[0] ;
				label=fields[2];
				getDataPoint(xmlFile,document,fields[0],documentObject,binaryObject);
				documentObject.add(classCategories);
				
				binaryObject.add(binaryClassCategories);
				String binaryLabel="";
				if(label.equalsIgnoreCase("21")) {
					binaryLabel=label;
				}
				else {
					binaryLabel="not21";
				}
								
				if(flg) {
					documentArff= LearningInstance.createSingleInstanceArff(label,documentObject.toArray( ) );
					binaryArff= LearningInstance.createSingleInstanceArff(binaryLabel,binaryObject.toArray( ) );
					
					flg=false;
				}
				else {
					LearningInstance.createSingleInstanceArff( documentArff,label,documentObject.toArray( ) );
					LearningInstance.createSingleInstanceArff( binaryArff,binaryLabel,binaryObject.toArray( ) );
				}
			}
			

			reader.close();
			System.out.println("Successfully read from the file");

		} catch (IOException e) {
			System.out.println("Could not read csv file");
//			e.printStackTrace( );
		}
		
	}
	
	public void getDataPoint(  String xmlFile,String document,
			String documentName,
			List< Object > documentObject,
			List< Object > binaryObject) throws Exception {

		double[] blankEdge = new double[ 80 ];
		double[] blankGrid = new double[ 100 ];

		
		
		
		documentObject.add( documentName );
		binaryObject.add( documentName );

		String xml = getXML( xmlFile );
		
		int documentLength = Jsoup.parse( xml ).select( "pages" ).size( );
		
		
//		System.out.println(documentLength);
//		System.out.println(objects);
		
		// Add page text as an individual feature. If shorter than 8 pages add "" for each missing page.
		for ( int i = 0; i <= 7; i++ ) {

			if ( i <= documentLength - 1 ) {

				String text = Jsoup.parse( xml ).select( "pages" ).get( i ).text( );
				String cleanedText="\"" + cleanTextContent( text.replace( "\"", "'" ).replaceAll( "\\\\$", "" ) ) + "\"";
				documentObject.add(cleanedText  );
				binaryObject.add(cleanedText  );

			} else {

				documentObject.add( "" );
				binaryObject.add( "" );
			}

		}
		
//		System.out.println(documentLength);
		SDSFile imageFile=new SDSFile(document);
		// Add edge and grid density features for the first 8 pages. Add a blank array if document has fewer than 8 pages.
		List< SDSImage > imageList = ImageUtils.burstMultiPageImage( imageFile);
		int pageLimit = imageList.size( );

		
		for ( int i = 0; i <= 7; i++ ) {

			if ( i <= pageLimit - 1 ) {
				BufferedImage image = PlanarImage.wrapRenderedImage( imageList.get( i ).getRenderedImage( ) ).getAsBufferedImage( );

				EdgeHistogramFilter edge = new EdgeHistogramFilter( );
				double[] edgeHistogram = getFeatures( image );

				double[] gridDensityHistogram = SDSGridDensityFilter.process( image, 10, 10 );

				documentObject.add( edgeHistogram );
				documentObject.add( gridDensityHistogram );
				binaryObject.add( edgeHistogram );
				binaryObject.add( gridDensityHistogram );

			} else {

				documentObject.add( blankEdge );
				documentObject.add( blankGrid );
				binaryObject.add( blankEdge );
				binaryObject.add( blankGrid );

			}

		}
		Double page=new Double(pageLimit);
		documentObject.add( page );
		binaryObject.add( page );
//		System.out.println(binaryObject.get(0));
	}
	
	protected String getXML( String fileName ) throws Exception {
		

		try (ZipInputStream zis = new ZipInputStream( new FileInputStream( new SDSFile( fileName ) ) )) {

			StringBuilder s = new StringBuilder( );
			byte[] buffer = new byte[ 1024 ];
			int read = 0;
			while ( ( zis.getNextEntry( ) ) != null ) {
				while ( ( read = zis.read( buffer, 0, 1024 ) ) >= 0 ) {
					s.append( new String( buffer, 0, read ).trim( ) );
				}
			}
			
			return s.toString( );

		}
	}
	
	private static String cleanTextContent( String text ) {
		// strips off all non-ASCII characters
		text = text.replaceAll( "[^\\x00-\\x7F]", "" );

		// erases all the ASCII control characters
		text = text.replaceAll( "[\\p{Cntrl}&&[^\r\n\t]]", "" );

		// removes non-printable characters from Unicode
		text = text.replaceAll( "\\p{C}", "" );

		return text.trim( );
	}
	
	public void instancesToArff(Instances data, String fileName) {
		
		
		// save ARFF
		try {
			ArffSaver saver = new ArffSaver();
			saver.setInstances(data);
			saver.setFile(new File(fileName));
//			saver.setDestination(new File(fileName));
			saver.writeBatch();
		} catch (IOException e) {
			 // Auto-generated catch block
			e.printStackTrace();
		}
		
//		BufferedWriter writer;
//		try {
//			writer = new BufferedWriter(new FileWriter(fileName));
////			System.out.println(data);
//			writer.write(data.toString());
//			writer.flush();
//			writer.close();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		 
		
	}
	
	public void csvToArff(String fileName,String dstFile) throws Exception {


	    // load CSV
	    CSVLoader loader = new CSVLoader();
	    loader.setSource(new File(fileName));
	    Instances data = loader.getDataSet();

	    // save ARFF
	    ArffSaver saver = new ArffSaver();
	    saver.setInstances(data);
	    saver.setFile(new File(dstFile));
	    saver.writeBatch();
	    // .arff file will be created in the output location
	  }
	
		
	
	public static void subMain() throws Exception {
		// TODO Auto-generated method stub
		
		String csvFile=path+"21vnot21_updated_2.csv";
		String documentArff=path+"21vnot21_updated_2.arff";
		String dstFile=path+"new21vnot21_updated_2.arff";
//		String binaryArff=path+"Test2Binary.arff";
		Framework.startup();
		try {
			
//			GetIntakeArff obj = new GetIntakeArff();
			
//			obj.GetDataSet(csvFile);
//			obj.instancesToArff(obj.documentArff, documentArff);
//			obj.instancesToArff(obj.binaryArff, binaryArff);
//			obj.csvToArff(csvFile, dsstFile);
			
//			obj.instancesToArff(data, dstFile);
			
		}
		
		finally {
			Framework.shutdown( );
		}
		

	}

	public static void main(String[] args)  {
		// TODO Auto-generated method stub		
		try {
			
//			subMain();
			
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		
		
		
//		String s1="\"javat\\po\"int is a very go'od webs'ite\\\"";  
//		String replaceString="\"" + s1.replace( "\"", "'" ).replaceAll( "\\\\$", "" )  + "\"";//replaces all occurrences of "a" to "e"  
//		System.out.println(replaceString);
		System.out.println("hi");

	}

}

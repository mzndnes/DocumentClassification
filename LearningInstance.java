package com.smartdatasolutions.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.smartdatasolutions.machinelearning.MachineLearningInstanceUtils;

import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ArffSaver;


public class LearningInstance extends MachineLearningInstanceUtils{
	final static String path="D:\\Users\\dmaharjan\\SDSRequired\\Project\\Intake\\";
	public static Instances createSingleInstanceArff( String label ,Object... attrs) {
		
		
		ArrayList< Attribute > fvWekaAttributes = new ArrayList<>( );

		int attrCounter = 0;
		for ( Object object: attrs ) {

			if ( object instanceof Integer || object instanceof Double ) {
				Attribute attribute = new Attribute( "attr" + attrCounter++ );
//				System.out.println("ID");
				fvWekaAttributes.add( attribute );
			} else if ( object instanceof String ) {
				Attribute attribute = new Attribute( "attr" + attrCounter++, ( List< String > ) null );
//				System.out.println("S");
				fvWekaAttributes.add( attribute );
			} else if ( object instanceof List ) {
				List< String > categories = ( List< String > ) object;
				Attribute attribute = new Attribute( "attr" + attrCounter++, categories );
//				System.out.println("L");
				fvWekaAttributes.add( attribute );
			
			} else if ( object instanceof double[] ) {
//				System.out.println("DA");
				for ( double d: ( double[] ) object ) {
					Attribute attribute = new Attribute( "attr" + attrCounter++ );
					fvWekaAttributes.add( attribute );
				}
			}

		}
		
	
//		System.out.println("ID");

		Instances arff = new Instances( "Classifier", fvWekaAttributes, 1 );

		arff.setClassIndex( attrCounter - 1 );
		
		createSingleInstanceArff(arff,label,attrs);

		
		return arff;

	}
	
	public static void createSingleInstanceArff( Instances arff,String label,Object... attrs) {
		int attrCounter=arff.numAttributes();
		DenseInstance instance = new DenseInstance( attrCounter );
		instance.setDataset(arff);
		
		
		int j = 0;
//		System.out.println("ID");
		for ( int i = 0; i < attrCounter; i++ ) {
//			System.out.println(i);
			
			Object object = attrs[ j ];
//			System.out.println(object instanceof List );
			if ( object instanceof Integer ) {
				instance.setValue( i, ( ( Integer ) object ).intValue( ) );
//				System.out.println("I");
			} else if ( object instanceof Double ) {
				instance.setValue( i, ( ( Double ) object ).doubleValue( ) );
//				System.out.println("D");
			} else if ( object instanceof String ) {
				instance.setValue( i, ( String ) object );
//				System.out.println("S");
			} else if ( object instanceof List ) {
				instance.setValue( i, label );
//				System.out.println("L");
			} else if ( object instanceof double[] ) {
//				System.out.println("DL");
				for ( double d: ( double[] ) object ) {
					instance.setValue( i, d );
					i++;
				}
				i--;

			}
			j++;

		}
		
		
//		System.out.println("instance");
		arff.add( instance );
		
		
	}
	public Instances loadingArff(String arffFile) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(arffFile));
		Instances data = new Instances(reader);
		reader.close();
		data.setClassIndex(data.numAttributes() - 1);
		return data;
	}
	
	public Instances getGoodInstances(Instances data) {
		int numRec=data.numInstances();
		Instances data1=new Instances(data,numRec);
		int c=0;
		int max21=2000;
		String label="21";
		System.out.println(numRec);
		
		int labelIndex=data.classIndex();
		for(Instance d:data) {
//			d.setDataset(data);

			if(d.attribute(labelIndex).value(0).toString().equals(label) && c<max21) {
				data1.add(d);
				c+=1;

			}
			else if(!d.attribute(labelIndex).value(0).toString().equals(label)) {
				data1.add(d);
				
			}
			
			
		}
		data1.setClassIndex(data1.numAttributes() - 1);
		return data1;
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
		 
		
	}

	public static void main(String[] args) throws IOException  {
		LearningInstance obj=new LearningInstance();
		String csvFile=path+"21vnot21_updated_2.csv";
		String documentArff=path+"21vnot21_updated_2.arff";
		String dstFile=path+"new21vnot21_updated_2.arff";
		Instances data=obj.loadingArff(documentArff);
		Instances data2=obj.getGoodInstances(data);
		obj.instancesToArff(data2, dstFile);
		
	}
	



}

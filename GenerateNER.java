package com.smartdatasolutions.test;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

//import edu.stanford.nlp.ie.machinereading.structure.EntityMention;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.CoreDocument;
//import edu.stanford.nlp.pipeline.CoreEntityMention;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;



public class GenerateNER {
	static final String path="D:\\Users\\dmaharjan\\sds-required\\relay\\pro\\";

	public void read_csv_file(String fname) {

		String csvfile = path+"csv\\"+fname+".csv";

		String text = null;
		String line="";
		try {
			FileReader fr = new FileReader( csvfile );
			BufferedReader br = new BufferedReader( fr );

			while ( ( line = br.readLine( ) ) != null )   //returns a Boolean value
			{
				System.out.println( line );
				text = text +" " + line;
			}
			fr.close( );
			System.out.println( "Successfully read from the file" );

		} catch ( IOException e ) {
			System.out.println( "An error occurred." );
			e.printStackTrace( );
		}
		this.generate_words( text,fname );
	}
	
	public void generate_words(String text,String fname){
		Set<String> ner_text=new HashSet<>(  );
		String[] words =text.split(" ");
		for (String word:words) {
			
			ner_text.add(this.generate_ner(word));
		}
		this.write_ner(ner_text,fname);
	}
	
	public String generate_ner(String word){
		String w="";
		StanfordCoreNLP stanfordCoreNLP=Pipeline.getPipeline();
		CoreDocument coreDocument=new CoreDocument(word);
		stanfordCoreNLP.annotate(coreDocument);

		List<CoreLabel> coreLabels=coreDocument.tokens();

		for(CoreLabel coreLabel:coreLabels) {
			String ner=coreLabel.get( CoreAnnotations.NamedEntityTagAnnotation.class );
			
			if ( ner.equalsIgnoreCase("PERSON" ) ||ner.equalsIgnoreCase(  "CITY")||ner.equalsIgnoreCase( "ORGANIZATION")||(ner.equalsIgnoreCase( "NUMBER")&&coreLabel.originalText().length()>=5)){
				w=coreLabel.originalText().toLowerCase();

			}

		}
		return w;

	}
	public void write_ner( Set< String > text,String fname) {

		String csvfile=null;
		csvfile=path+"ner\\"+fname+".csv";
		String hd=null;
		try {
			FileWriter fw = new FileWriter(csvfile);

			for (String j:text) {
				fw.append(j);
				fw.append("\n");

			}
			fw.flush();
			fw.close();
			System.out.println("Successfully Written into the file");

		} catch (IOException e) {
			System.out.println("An error occurred.");
			e.printStackTrace();
		}
	}
	public void get_file_name() throws Exception {
		File file_csv=new File(path+"RHES.csv");
		String line="";
		try {
			FileReader fr = new FileReader( file_csv );
			BufferedReader br = new BufferedReader( fr );

			while ( ( line = br.readLine( ) ) != null )   //returns a Boolean value
			{
				String[] data = line.split(",");

				this.read_csv_file(data[2]);

			}
			fr.close( );
			System.out.println( "Successfully read from the file" );

		} catch ( IOException e ) {
			System.out.println( "An error occurred." );
			e.printStackTrace( );
		}

	}

	public static void main(String[] args) throws Exception {
		GenerateNER obj=new GenerateNER();
		obj.get_file_name();
		

	}

}

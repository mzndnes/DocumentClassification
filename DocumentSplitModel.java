package com.smartdatasolutions.test;

import java.io.*; 


import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

public class DocumentSplitModel {
	ArrayList<HashSet<String>> xdata=new ArrayList<HashSet<String>>();
	ArrayList<ArrayList<String>> ydata=new ArrayList<ArrayList<String>>();
	int train_size,test_size=500;
	final static String path="D:\\Users\\dmaharjan\\sds-required\\relay\\pro\\";
	float j_same=(float) 0.0,j_diff=(float) 0.0;
	int sm=0,diff=0;
//	int mx_same=Integer.MIN_VALUE,mn_same=Integer.MAX_VALUE,mx_diff=Integer.MIN_VALUE,mn_diff=Integer.MAX_VALUE;


	
	
	
	public  DocumentSplitModel(String dr) throws Exception {
		
		
		File file_csv=new File(path+dr+"\\"+dr+"_posdata.csv");
		String line="";
		
		try {
			FileReader fr = new FileReader( file_csv );
			BufferedReader br = new BufferedReader( fr );

			while ( ( line = br.readLine( ) ) != null )   //returns a Boolean value
			{

				this.create_set(line);
				
				
			}
			this.train_size=this.xdata.size();
			
			fr.close( );
			System.out.println( "Successfully read from the file" );

		} catch ( IOException e ) {
			System.out.println( "An error occurred." );
			e.printStackTrace( );
		}
	}
	
	public void create_set(String line) {
		HashSet<String> text=new HashSet<String>();
		ArrayList<String> cl=new ArrayList<String>();
		String words[]=line.split(",");
		int lcount=0;
		
		for (String word:words) {

			if(lcount<4) {
				cl.add(word);
			}
			else {
				text.add(word);
			}
			lcount++;
		}
//		System.out.println(text);
//		System.out.println(cl);
		this.xdata.add(text);
		this.ydata.add(cl);
		
	}
	
	public float get_accuracy() {
		int tp=0,tn=0,tr,t=0;
		boolean docu_res;
		Random seeed=new Random();
		
		int s=seeed.nextInt(train_size);


		Random tr_rnd=new Random(s);
		

		
		String train_id="",test_id="";
		int test_size=this.test_size;
		for(int i=0;i<test_size;i++) {
			
			tr=tr_rnd.nextInt(train_size-1);
										
			docu_res=this.predict_docu(tr);
			train_id=this.ydata.get(tr).get(1);
			test_id=this.ydata.get(tr+1).get(1);
			
//			System.out.println(docu_res);
			
			if (test_id.equalsIgnoreCase(train_id) && docu_res) {
				tp++;
			}
			else if(!test_id.equalsIgnoreCase(train_id) && !docu_res){
				tn++;
			}
			else {
				System.out.println("no "+tr);
				
			}

		}

		return (float)(tp+tn)/this.test_size;
		
	}
	public int get_intersection(int k) {
		HashSet<String>text1=new HashSet<String>();
		HashSet<String>text2=new HashSet<String>();
		text1=this.xdata.get(k);
		text2=this.xdata.get(k+1);
		int c=0;
		for (String w:text1) {
			if(text2.contains(w)) {
				c++;
				
				

//				System.out.println(c);
//				System.out.println(w);
			}
		}
		return c;
	}
	
	public boolean predict_docu(int k) {
		
		int in_sect=this.get_intersection(k);
		if (in_sect>=50) {
			return true;
		}
		
		return false;
	}
	public void dis() {
		System.out.println(this.j_same/this.sm);
		System.out.println(this.j_diff/this.diff);
		
	}
	public float get_jaccard(int k) {
		HashSet<String>text1=new HashSet<String>();
		HashSet<String>text2=new HashSet<String>();
		text1=this.xdata.get(k);
		text2=this.xdata.get(k+1);
		int cm=this.get_intersection(k);
		
		float j_sim=(float)cm/(text1.size()+text2.size());
		return j_sim;
		
	}
	
	public void write_jaccard( ) {
		
			
		int k=(int)this.xdata.size();
		
		for (int c=0;c<k-1;c++) {
			float j_sim=this.get_jaccard(c);
//			System.out.println(j_sim);
			
			if(this.ydata.get(c).get(1).equalsIgnoreCase(this.ydata.get(c+1).get(1))) {
				this.j_same=this.j_same+j_sim;
				this.sm++;
			}
			else {
				this.j_same=this.j_same+j_sim;
				this.diff++;
			}
			
		}	
		
	}

	public static void main(String args[]) throws Exception  {
		String dr="all";
		DocumentSplitModel obj=new DocumentSplitModel(dr);
//		obj.write_jaccard();
//		obj.dis();

//        System.out.println(obj.get_intersection(obj.xdata.get(3), obj.xdata.get(4)));
        System.out.println(obj.get_accuracy());
//		System.out.println(obj.predict_docu(obj.xdata.get(902),obj.xdata.get(903)));
//		System.out.println(obj.xdata.get(70));
//		System.out.println(obj.xdata.get(69));
//		System.out.println(Integer.parseInt("34")+6);
//		System.out.println(Float.parseFloat("4.9")+3);
//		try {
//			Integer.parseInt("din");
//		}catch ( NumberFormatException e ) {
//			System.out.println( "An error occurred." );
//			e.printStackTrace( );
		
//		}
//		
//		System.out.println("gu:");
	}
	
	

}


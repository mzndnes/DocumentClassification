package com.smartdatasolutions.identification;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

public class GenerateMinhash {
	ArrayList<ArrayList<String>> hashData;
	ArrayList<HashSet<Integer>> imageData;
	int hashSize,universalSize;
	ArrayList<Integer>seed=null;
	ArrayList<ArrayList<Integer>>permutation=new ArrayList<ArrayList<Integer>>();
	ArrayList<ArrayList<String>> targetData=null;
	
	GenerateMinhash(ArrayList<ArrayList<String>> hashData,
			ArrayList<HashSet<Integer>> imageData,
			ArrayList<ArrayList<String>>targetData,
			int hashSize,int universalSize){
		this.hashData=hashData;
		this.imageData=imageData;
		this.targetData=targetData;
		this.hashSize=hashSize;
		this.universalSize=universalSize;
//		this.generateSeed();
		this.getPermutation();
	}
	public void generateSeed() {
		this.seed=new ArrayList<Integer>();
		int r;
		Random randomGenerator=new Random();
		for(int i=0;i<this.hashSize;i++) {
			r=randomGenerator.nextInt(10000);
			this.seed.add(r);
		}
		
	}
	public void getPermutation() {
		Random seedGenerator=new Random();
		int seed;
		for(int i=0;i<this.hashSize;i++) {
			seed=seedGenerator.nextInt();
			this.getPermutation(i,seed);
		}
	
	}
	public void getPermutation(int k,int seed) {
		ArrayList<Integer>temporaryPermu=new ArrayList<Integer>();
		ArrayList<Integer>helpingPermu=new ArrayList<Integer>();
		
		for(int i=0;i<universalSize;i++) {
			helpingPermu.add(i);
		}
		
		Random randomGenerator=new Random(seed);
		int r,count=0;
		while(count<universalSize) {
			r=randomGenerator.nextInt(helpingPermu.size());
			temporaryPermu.add(helpingPermu.get(r));
			helpingPermu.remove(r);
			count++;

		}
		this.permutation.add(temporaryPermu);
	}
	
	public void getMinhash() {
		for (int i=0;i<this.imageData.size();i++) {
			this.hashData.add(this.getMinhash(i));
			System.out.println(hashData);
		}
	}

	public ArrayList<String> getMinhash(int k){
		ArrayList<String>hashInstance=new ArrayList<String>(this.hashSize);
		HashSet<Integer>imageSet=this.imageData.get(k);
		ArrayList<Integer>temporaryPermu=null;
		int hashValue;

		for(int i=0;i<this.hashSize;i++) {
			hashValue=Integer.MAX_VALUE;
			temporaryPermu=this.permutation.get(i);

			for(Integer pixelIndex:imageSet) {
//					System.out.println(p_text.size());
//					System.out.println(temp_perm.size());
				if(hashValue>temporaryPermu.get(pixelIndex)) {
					hashValue=temporaryPermu.get(pixelIndex);
				}
			}
			hashInstance.add(Integer.toString(hashValue));
		}
		hashInstance.add(this.targetData.get(k).get(4));
		return hashInstance;
	
		

	}


}

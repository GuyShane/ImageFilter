package com.imagefilter;

public class MeanFilter extends Filter {

	@Override
	public int filterCalculation(int[] window) {
		return avg(window);
	}
	
	private int avg(int []arr) {
		long avg=0;
		for (int num:arr) {
			avg+=num;
		}
		avg/=arr.length;
		return (int) avg;
	}
}

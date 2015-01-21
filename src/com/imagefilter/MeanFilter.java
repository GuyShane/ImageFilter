package com.imagefilter;

public class MeanFilter extends Filter {

	@Override
	public int filterCalculation(int[] window) {
		int a=0;
		int r=0;
		int g=0;
		int b=0;
		for (int num: window) {
			a+=(num>>24)&0xff;
			r+=(num>>16)&0xff;
			g+=(num>>8)&0xff;
			b+=num&0xff;
		}
		a/=window.length;
		r/=window.length;
		g/=window.length;
		b/=window.length;
		return (a<<24)|(r<<16)|(g<<8)|(b);
	}
}

package com.imagefilter;

public class MeanFilter extends Filter {

	@Override
	public int filterCalculation(int[] window) {
		int r=0;
		int g=0;
		int b=0;
		for (int num: window) {
			r+=(num>>16)&0xff;
			g+=(num>>8)&0xff;
			b+=num&0xff;
		}
		r/=window.length;
		g/=window.length;
		b/=window.length;
		return (0xff<<24)|(r<<16)|(g<<8)|(b);
	}
}

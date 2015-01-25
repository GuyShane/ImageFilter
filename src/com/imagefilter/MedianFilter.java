package com.imagefilter;

import java.util.Arrays;

public class MedianFilter extends Filter {

	@Override
	public int filterCalculation(int[] window) {
		int []a=new int [window.length];
		int []r=new int[window.length];
		int []g=new int[window.length];
		int []b=new int[window.length];
		for (int i=0;i<window.length;i++) {
			a[i]=(window[i]>>24)&0xff;
			r[i]=(window[i]>>16)&0xff;
			g[i]=(window[i]>>8)&0xff;
			b[i]=window[i]&0xff;
		}
		Arrays.sort(a);
		Arrays.sort(r);
		Arrays.sort(g);
		Arrays.sort(b);
		int color=(a[a.length/2]<<24)|(r[r.length/2]<<16)|(g[g.length/2]<<8)|b[b.length/2];
		return color;
	}

}

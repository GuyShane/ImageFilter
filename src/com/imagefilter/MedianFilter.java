package com.imagefilter;

import java.util.Arrays;

public class MedianFilter extends Filter {

	@Override
	public int filterCalculation(int[] window) {
		Arrays.sort(window);
		return window[window.length/2];
	}

}

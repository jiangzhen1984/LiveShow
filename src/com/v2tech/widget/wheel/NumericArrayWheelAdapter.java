package com.v2tech.widget.wheel;

import android.content.Context;

import com.v2tech.widget.wheel.adapters.AbstractWheelTextAdapter;

public class NumericArrayWheelAdapter extends AbstractWheelTextAdapter {

	private int arr[];
	private String format;

	public NumericArrayWheelAdapter(Context context, int[] arr, String format) {
		super(context);
		this.arr = arr;
		this.format = format;
	}

	@Override
	public int getItemsCount() {
		return arr == null ? 0 : arr.length;
	}

	@Override
	protected CharSequence getItemText(int index) {
		int value = arr[index];
		return format != null ? String.format(format, value) : Integer
				.toString(value);
	}

}

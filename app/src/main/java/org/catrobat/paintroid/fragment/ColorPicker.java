/*
 * Paintroid: An image manipulation application for Android.
 * Copyright (C) 2010-2019 The Catrobat Team
 * (<http://developer.catrobat.org/credits>)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.catrobat.paintroid.fragment;

import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.skydoves.colorpickerview.ColorPickerView;
import com.skydoves.colorpickerview.listeners.ColorListener;
import com.skydoves.colorpickerview.sliders.AlphaSlideBar;
import com.skydoves.colorpickerview.sliders.BrightnessSlideBar;

import org.catrobat.paintroid.R;

import java.util.ArrayList;
import java.util.List;

public class ColorPicker extends Fragment {

	public static final String KEY_COLOR = "COLOR";
	private List<OnColorPickedListener> colorPickedListeners = new ArrayList<>();
	private int currentColor;

	public static ColorPicker newInstance(@ColorInt int color) {
		Bundle args = new Bundle();
		args.putInt(KEY_COLOR, color);

		ColorPicker fragment = new ColorPicker();
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Bundle arguments = savedInstanceState != null ? savedInstanceState : getArguments();
		if (arguments != null) {
			currentColor = arguments.getInt(KEY_COLOR);
		}
	}

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.dialog_pocketpaint_colorpicker, container, false);

		ColorPickerView colorPickerView = view.findViewById(R.id.colorPickerView);

		BrightnessSlideBar brightnessSlideBar = view.findViewById(R.id.brightnessSlide);
		colorPickerView.attachBrightnessSlider(brightnessSlideBar);

		AlphaSlideBar alphaSlideBar = view.findViewById(R.id.alphaSlideBar);
		colorPickerView.attachAlphaSlider(alphaSlideBar);

		colorPickerView.setColorListener(new ColorListener() {
			@Override
			public void onColorSelected(int color, boolean fromUser) {
				for (OnColorPickedListener listener : colorPickedListeners) {
					listener.colorChanged(color);
				}
				currentColor = color;
			}
		});

		return view;
	}

	@Override
	public void onSaveInstanceState(@NonNull Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt(KEY_COLOR, currentColor);
	}

	public void addOnColorPickedListener(OnColorPickedListener onColorPickedListener) {
		colorPickedListeners.add(onColorPickedListener);
	}

	public interface OnColorPickedListener {
		void colorChanged(int color);
	}
}

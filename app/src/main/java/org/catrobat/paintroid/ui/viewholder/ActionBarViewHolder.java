/*
 * Paintroid: An image manipulation application for Android.
 * Copyright (C) 2010-2015 The Catrobat Team
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

package org.catrobat.paintroid.ui.viewholder;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import org.catrobat.paintroid.R;
import org.catrobat.paintroid.contract.MainActivityContracts;

public class ActionBarViewHolder implements MainActivityContracts.ActionBarViewHolder {
	private AppCompatActivity activity;
	private final ViewGroup layout;
	private final Toolbar toolbar;
	private boolean undoEnabled;
	private boolean redoEnabled;

	public ActionBarViewHolder(AppCompatActivity activity, ViewGroup layout) {
		this.activity = activity;
		this.layout = layout;
		this.toolbar = layout.findViewById(R.id.pocketpaint_toolbar);
	}

	@Override
	public void setUndoButtonEnabled(boolean enabled) {
		undoEnabled = enabled;
	}

	@Override
	public void setRedoButtonEnabled(boolean enabled) {
		redoEnabled = enabled;
	}

	@Override
	public void initialize() {
		activity.setSupportActionBar(toolbar);
	}

	@Override
	public void hide() {
		layout.setVisibility(View.GONE);
	}

	@Override
	public void show() {
		layout.setVisibility(View.VISIBLE);
	}

	@Override
	public int getHeight() {
		return layout.getHeight();
	}

	@Override
	public void invalidateOptionsMenu() {
		activity.invalidateOptionsMenu();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
		ActionBar supportActionBar = activity.getSupportActionBar();
		if (supportActionBar != null) {
			supportActionBar.setTitle(R.string.pocketpaint_app_name);
			supportActionBar.setDisplayShowTitleEnabled(true);
			supportActionBar.setDisplayHomeAsUpEnabled(false);
		}

		menuInflater.inflate(R.menu.menu_pocketpaint_more_options, menu);
		menu.findItem(R.id.pocketpaint_nav_undo).setEnabled(undoEnabled);
		menu.findItem(R.id.pocketpaint_nav_redo).setEnabled(redoEnabled);
		return true;
	}
}

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

import android.content.Context;
import android.support.annotation.DrawableRes;
import android.support.design.internal.BottomNavigationItemView;
import android.support.design.internal.BottomNavigationMenuView;
import android.support.design.widget.BottomNavigationView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.catrobat.paintroid.R;
import org.catrobat.paintroid.contract.MainActivityContracts;
import org.catrobat.paintroid.tools.ToolType;

import static android.content.pm.ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;

public class BottomNavigationViewHolder implements MainActivityContracts.BottomNavigationViewHolder {
	public final View layout;
	private final BottomNavigationView bottomNavigationView;
	private final BottomNavigationMenuView bottomNavigationMenuView;
	private int orientation;

	public BottomNavigationViewHolder(View layout, Context context) {
		this.layout = layout;
		this.bottomNavigationView = layout.findViewById(R.id.pocketpaint_bottom_navigation);
		this.bottomNavigationMenuView = (BottomNavigationMenuView) bottomNavigationView.getChildAt(0);
		this.orientation = context.getResources().getConfiguration().orientation;
	}

	@Override
	public void show() {
		layout.setVisibility(View.VISIBLE);
	}

	@Override
	public void hide() {
		layout.setVisibility(View.GONE);
	}

	@Override
	public void setCurrentTool(ToolType toolType) {
		if (orientation == SCREEN_ORIENTATION_PORTRAIT) {
			Menu menu = bottomNavigationView.getMenu();
			MenuItem currentTool = menu.findItem(R.id.pocketpaint_action_current_tool);
			currentTool.setIcon(getImageResourceForToolType(toolType));
			currentTool.setTitle(toolType.getNameResource());
		} else {
			View item = bottomNavigationMenuView.findViewById(R.id.pocketpaint_action_current_tool);
			ImageView icon = item.findViewById(R.id.icon);
			TextView title = item.findViewById(R.id.title);
			icon.setImageResource(getImageResourceForToolType(toolType));
			title.setText(toolType.getNameResource());
		}
	}

	private @DrawableRes int getImageResourceForToolType(ToolType toolType) {
		switch (toolType) {
			case PIPETTE:
				return R.drawable.ic_pocketpaint_tool_pipette;
			case BRUSH:
				return R.drawable.ic_pocketpaint_tool_brush;
			case FILL:
				return R.drawable.ic_pocketpaint_tool_fill;
			case STAMP:
				return R.drawable.ic_pocketpaint_tool_stamp;
			case LINE:
				return R.drawable.ic_pocketpaint_tool_line;
			case CURSOR:
				return R.drawable.ic_pocketpaint_tool_cursor;
			case IMPORTPNG:
				return R.drawable.ic_pocketpaint_tool_import;
			case TRANSFORM:
				return R.drawable.ic_pocketpaint_tool_transform;
			case ERASER:
				return R.drawable.ic_pocketpaint_tool_eraser;
			case SHAPE:
				return R.drawable.ic_pocketpaint_tool_rectangle;
			case TEXT:
				return R.drawable.ic_pocketpaint_tool_text;
			case HAND:
				return R.drawable.ic_pocketpaint_tool_hand;
			default:
				return 0;
		}
	}

	@Override
	public void setColorButtonColor(int toolColor) {
		//Needs to be implemented
	}

	public BottomNavigationView getBottomNavigationView() {
		return bottomNavigationView;
	}

	public void setLandscapeStyle(Context context) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		Menu menu = bottomNavigationView.getMenu();
		for (int i = 0; i < menu.size(); i++) {
			BottomNavigationItemView item = (BottomNavigationItemView) bottomNavigationMenuView.getChildAt(i);
			View itemBottomNavigation = inflater.inflate(R.layout.pocketpaint_layout_bottom_navigation_item, bottomNavigationMenuView, false);
			ImageView icon = itemBottomNavigation.findViewById(R.id.icon);
			TextView text = itemBottomNavigation.findViewById(R.id.title);
			icon.setImageDrawable(menu.getItem(i).getIcon());
			text.setText(menu.getItem(i).getTitle());
			item.removeAllViews();
			item.addView(itemBottomNavigation);
		}
	}
}

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

package org.catrobat.paintroid.tools;

import android.support.annotation.DrawableRes;
import android.support.annotation.IdRes;
import android.support.annotation.StringRes;

import org.catrobat.paintroid.R;
import org.catrobat.paintroid.tools.Tool.StateChange;

import java.util.EnumSet;

import static org.catrobat.paintroid.common.Constants.INVALID_RESOURCE_ID;

public enum ToolType {
	PIPETTE(R.string.button_pipette, EnumSet.of(StateChange.ALL), R.id.pocketpaint_tools_pipette, INVALID_RESOURCE_ID, false),
	BRUSH(R.string.button_brush, EnumSet.of(StateChange.ALL), R.id.pocketpaint_tools_brush, INVALID_RESOURCE_ID, true),
	FILL(R.string.button_fill, EnumSet.of(StateChange.ALL), R.id.pocketpaint_tools_fill, INVALID_RESOURCE_ID, true),
	STAMP(R.string.button_stamp, EnumSet.of(StateChange.ALL), R.id.pocketpaint_tools_stamp, R.drawable.pocketpaint_stamp_tool_overlay, false),
	LINE(R.string.button_line, EnumSet.of(StateChange.ALL), R.id.pocketpaint_tools_line, INVALID_RESOURCE_ID, true),
	CURSOR(R.string.button_cursor, EnumSet.of(StateChange.ALL), R.id.pocketpaint_tools_cursor, INVALID_RESOURCE_ID, true),
	IMPORTPNG(R.string.button_import_image, EnumSet.of(StateChange.ALL), R.id.pocketpaint_tools_import, R.drawable.pocketpaint_import_tool_overlay, false),
	TRANSFORM(R.string.button_transform, EnumSet.of(StateChange.RESET_INTERNAL_STATE, StateChange.NEW_IMAGE_LOADED), R.id.pocketpaint_tools_transform, INVALID_RESOURCE_ID, true),
	ERASER(R.string.button_eraser, EnumSet.of(StateChange.ALL), R.id.pocketpaint_tools_eraser, INVALID_RESOURCE_ID, true),
	SHAPE(R.string.button_shape, EnumSet.of(StateChange.ALL), R.id.pocketpaint_tools_rectangle, R.drawable.pocketpaint_rectangle_tool_overlay, true),
	TEXT(R.string.button_text, EnumSet.of(StateChange.ALL), R.id.pocketpaint_tools_text, R.drawable.pocketpaint_text_tool_overlay, true),
	HAND(R.string.button_hand, EnumSet.of(StateChange.ALL), R.id.pocketpaint_tools_hand, INVALID_RESOURCE_ID, false);

	private int nameResource;
	private EnumSet<StateChange> stateChangeBehaviour;
	private int toolButtonID;
	private int overlayDrawableResource;
	private boolean hasOptions;

	ToolType(int nameResource, EnumSet<StateChange> stateChangeBehaviour,
			int toolButtonID, int overlayDrawableResource, boolean hasOptions) {
		this.nameResource = nameResource;
		this.stateChangeBehaviour = stateChangeBehaviour;
		this.toolButtonID = toolButtonID;
		this.overlayDrawableResource = overlayDrawableResource;
		this.hasOptions = hasOptions;
	}

	public @StringRes int getNameResource() {
		return nameResource;
	}

	public @DrawableRes int getOverlayDrawableResource() {
		return overlayDrawableResource;
	}

	public boolean shouldReactToStateChange(StateChange stateChange) {
		return stateChangeBehaviour.contains(StateChange.ALL) || stateChangeBehaviour.contains(stateChange);
	}

	public @IdRes int getToolButtonID() {
		return toolButtonID;
	}

	public boolean hasOptions() {
		return hasOptions;
	}
}

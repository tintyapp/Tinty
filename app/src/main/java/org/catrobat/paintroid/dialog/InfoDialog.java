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

package org.catrobat.paintroid.dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;

import org.catrobat.paintroid.R;

public class InfoDialog extends AppCompatDialogFragment implements
		DialogInterface.OnClickListener {

	public static final String MESSAGE_RESOURCE_KEY = "messageResource";
	public static final String TITLE_RESOURCE_KEY = "titleResource";

	public static InfoDialog newInstance(@StringRes int messageResource, @StringRes int titleResource) {
		InfoDialog infoDialog = new InfoDialog();
		Bundle bundle = new Bundle();
		bundle.putInt(MESSAGE_RESOURCE_KEY, messageResource);
		bundle.putInt(TITLE_RESOURCE_KEY, titleResource);
		infoDialog.setArguments(bundle);
		return infoDialog;
	}

	@NonNull
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

		Bundle arguments = getArguments();
		if (arguments != null) {
			builder.setTitle(arguments.getInt(TITLE_RESOURCE_KEY))
					.setIcon(R.drawable.ic_pocketpaint_dialog_warning)
					.setMessage(arguments.getInt(MESSAGE_RESOURCE_KEY));
		}

		builder.setPositiveButton(android.R.string.ok, this);
		return builder.create();
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		dialog.cancel();
	}
}

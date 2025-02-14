/*
 * Paintroid: An image manipulation application for Android.
 * Copyright (C) 2010-2015 The Catrobat Team
 * (<http://developer.catrobat.org/credits>)
 * <p>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 * <p>
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.catrobat.paintroid.test.espresso.rtl;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.catrobat.paintroid.MainActivity;
import org.catrobat.paintroid.R;
import org.catrobat.paintroid.test.espresso.rtl.util.RtlActivityTestRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isClickable;
import static android.support.test.espresso.matcher.ViewMatchers.isCompletelyDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class)
public class ButtonTopLayersRtlLayoutTest {
	@Rule
	public ActivityTestRule<MainActivity> mainActivityActivityTestRule =
			new RtlActivityTestRule<>(MainActivity.class, "ar");

	@Test
	public void testButtonTopLayers() {
		onView(withId(R.id.pocketpaint_action_layers))
				.perform(click());
		onView(withId(R.id.pocketpaint_nav_view_layer))
				.check(matches(isCompletelyDisplayed()));

		onView(withId(R.id.pocketpaint_layer_side_nav_button_add))
				.check(matches(isClickable()))
				.perform(click());
		onView(withId(R.id.pocketpaint_layer_side_nav_button_delete))
				.check(matches(isClickable()))
				.perform(click());
	}
}

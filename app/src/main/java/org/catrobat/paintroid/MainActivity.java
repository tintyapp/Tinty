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

package org.catrobat.paintroid;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import org.catrobat.paintroid.command.Command;
import org.catrobat.paintroid.command.CommandFactory;
import org.catrobat.paintroid.command.CommandManager;
import org.catrobat.paintroid.command.implementation.AsyncCommandManager;
import org.catrobat.paintroid.command.implementation.DefaultCommandFactory;
import org.catrobat.paintroid.command.implementation.DefaultCommandManager;
import org.catrobat.paintroid.common.CommonFactory;
import org.catrobat.paintroid.contract.LayerContracts;
import org.catrobat.paintroid.contract.MainActivityContracts;
import org.catrobat.paintroid.controller.DefaultToolController;
import org.catrobat.paintroid.controller.ToolController;
import org.catrobat.paintroid.listener.PresenterColorPickedListener;
import org.catrobat.paintroid.model.LayerModel;
import org.catrobat.paintroid.model.MainActivityModel;
import org.catrobat.paintroid.presenter.LayerPresenter;
import org.catrobat.paintroid.presenter.MainActivityPresenter;
import org.catrobat.paintroid.tools.ContextCallback;
import org.catrobat.paintroid.tools.ToolPaint;
import org.catrobat.paintroid.tools.ToolReference;
import org.catrobat.paintroid.tools.ToolType;
import org.catrobat.paintroid.tools.Workspace;
import org.catrobat.paintroid.tools.implementation.DefaultContextCallback;
import org.catrobat.paintroid.tools.implementation.DefaultToolFactory;
import org.catrobat.paintroid.tools.implementation.DefaultToolPaint;
import org.catrobat.paintroid.tools.implementation.DefaultToolReference;
import org.catrobat.paintroid.tools.implementation.DefaultWorkspace;
import org.catrobat.paintroid.tools.options.ToolOptionsViewController;
import org.catrobat.paintroid.ui.DrawingSurface;
import org.catrobat.paintroid.ui.KeyboardListener;
import org.catrobat.paintroid.ui.LayerAdapter;
import org.catrobat.paintroid.ui.LayerNavigator;
import org.catrobat.paintroid.ui.MainActivityInteractor;
import org.catrobat.paintroid.ui.MainActivityNavigator;
import org.catrobat.paintroid.ui.Perspective;
import org.catrobat.paintroid.ui.dragndrop.DragAndDropListView;
import org.catrobat.paintroid.ui.tools.DefaultToolOptionsViewController;
import org.catrobat.paintroid.ui.viewholder.ActionBarViewHolder;
import org.catrobat.paintroid.ui.viewholder.BottomBarViewHolder;
import org.catrobat.paintroid.ui.viewholder.BottomNavigationViewHolder;
import org.catrobat.paintroid.ui.viewholder.DrawerLayoutViewHolder;
import org.catrobat.paintroid.ui.viewholder.LayerMenuViewHolder;

import java.io.File;

public class MainActivity extends AppCompatActivity implements MainActivityContracts.MainView,
		CommandManager.CommandListener {
	public static final String TAG = MainActivity.class.getSimpleName();
	private static final String IS_SAVED_KEY = "isSaved";
	private static final String WAS_INITIAL_ANIMATION_PLAYED = "wasInitialAnimationPlayed";
	private static final String SAVED_PICTURE_URI_KEY = "savedPictureUri";
	private static final String CAMERA_IMAGE_URI_KEY = "cameraImageUri";
	private static final String APP_FRAGMENT_KEY = "customActivityState";

	@VisibleForTesting
	public MainActivityContracts.Model model;
	@VisibleForTesting
	public Perspective perspective;
	@VisibleForTesting
	public Workspace workspace;
	@VisibleForTesting
	public LayerContracts.Model layerModel;
	@VisibleForTesting
	public CommandManager commandManager;
	@VisibleForTesting
	public ToolPaint toolPaint;
	@VisibleForTesting
	public ToolReference toolReference;
	@VisibleForTesting
	public ToolOptionsViewController toolOptionsViewController;

	private LayerPresenter layerPresenter;
	private DrawingSurface drawingSurface;
	private MainActivityContracts.ActionBarViewHolder actionBarViewHolder;
	private MainActivityContracts.Presenter presenter;
	private KeyboardListener keyboardListener;
	private PaintroidApplicationFragment appFragment;

	private Runnable deferredRequestPermissionsResult;

	@Override
	public MainActivityContracts.Presenter getPresenter() {
		return presenter;
	}

	@Override
	public DisplayMetrics getDisplayMetrics() {
		return getResources().getDisplayMetrics();
	}

	@Override
	protected void onResume() {
		super.onResume();

		if (deferredRequestPermissionsResult != null) {
			Runnable runnable = deferredRequestPermissionsResult;
			deferredRequestPermissionsResult = null;

			runnable.run();
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		setTheme(R.style.PocketPaintTheme);
		super.onCreate(savedInstanceState);

		getAppFragment();
		PaintroidApplication.cacheDir = getCacheDir();
		PaintroidApplication.checkeredBackgroundBitmap =
				BitmapFactory.decodeResource(getResources(), R.drawable.pocketpaint_checkeredbg);
		setContentView(R.layout.activity_pocketpaint_main);

		onCreateGlobals();

		onCreateMainView();
		onCreateLayerMenu();
		onCreateDrawingSurface();

		presenter.onCreateTool();

		if (savedInstanceState == null) {
			presenter.initializeFromCleanState();
		} else {
			boolean isSaved = savedInstanceState.getBoolean(IS_SAVED_KEY, false);
			boolean wasInitialAnimationPlayed = savedInstanceState.getBoolean(WAS_INITIAL_ANIMATION_PLAYED, false);
			Uri savedPictureUri = savedInstanceState.getParcelable(SAVED_PICTURE_URI_KEY);
			Uri cameraImageUri = savedInstanceState.getParcelable(CAMERA_IMAGE_URI_KEY);

			presenter.restoreState(isSaved, wasInitialAnimationPlayed,
					savedPictureUri, cameraImageUri);
		}

		commandManager.addCommandListener(this);
		presenter.finishInitialize();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater menuInflater = getMenuInflater();
		return actionBarViewHolder.onCreateOptionsMenu(menu, menuInflater);
	}

	@Override
	public boolean onOptionsItemSelected(@NonNull MenuItem item) {
		int i = item.getItemId();
		if (i == R.id.pocketpaint_nav_undo) {
			presenter.undoClicked();
		} else if (i == R.id.pocketpaint_nav_redo) {
			presenter.redoClicked();
		} else if (i == R.id.pocketpaint_nav_save_image) {
			presenter.saveImageClicked();
		} else if (i == R.id.pocketpaint_nav_save_image_as) {
			presenter.saveCopyClicked();
		} else if (i == R.id.pocketpaint_nav_load_image) {
			presenter.loadImageClicked();
		} else if (i == R.id.pocketpaint_nav_new_image) {
			presenter.newImageClicked();
		} else if (i == R.id.pocketpaint_nav_about) {
			presenter.showAboutClicked();
		} else {
			return false;
		}
		return true;
	}

	private void getAppFragment() {
		FragmentManager fragmentManager = getSupportFragmentManager();
		appFragment = (PaintroidApplicationFragment) fragmentManager.findFragmentByTag(APP_FRAGMENT_KEY);
		if (appFragment == null) {
			appFragment = new PaintroidApplicationFragment();
			fragmentManager.beginTransaction().add(appFragment, APP_FRAGMENT_KEY).commit();
		}
	}

	private void onCreateGlobals() {
		if (appFragment.getLayerModel() == null) {
			appFragment.setLayerModel(new LayerModel());
		}
		layerModel = appFragment.getLayerModel();
		if (appFragment.getCommandManager() == null) {
			DisplayMetrics metrics = getResources().getDisplayMetrics();

			CommandFactory commandFactory = new DefaultCommandFactory();
			CommandManager synchronousCommandManager = new DefaultCommandManager(new CommonFactory(), layerModel);
			commandManager = new AsyncCommandManager(synchronousCommandManager, layerModel);
			Command initCommand = commandFactory.createInitCommand(metrics.widthPixels, metrics.heightPixels);
			commandManager.setInitialStateCommand(initCommand);
			commandManager.reset();
			appFragment.setCommandManager(commandManager);
		} else {
			commandManager = appFragment.getCommandManager();
		}
		if (appFragment.getToolPaint() == null) {
			appFragment.setToolPaint(new DefaultToolPaint(getApplicationContext()));
		}
		toolPaint = appFragment.getToolPaint();
		if (appFragment.getCurrentTool() == null) {
			appFragment.setCurrentTool(new DefaultToolReference());
		}
		toolReference = appFragment.getCurrentTool();
	}

	private void onCreateMainView() {
		Context context = getApplicationContext();
		DrawerLayout drawerLayout = findViewById(R.id.pocketpaint_drawer_layout);
		AppBarLayout topBarLayout = findViewById(R.id.pocketpaint_layout_top_bar);
		View bottomBarLayout = findViewById(R.id.pocketpaint_main_bottom_bar);
		View bottomNavigationView = findViewById(R.id.pocketpaint_main_bottom_navigation);

		toolOptionsViewController = new DefaultToolOptionsViewController(this);
		MainActivityContracts.DrawerLayoutViewHolder drawerLayoutViewHolder = new DrawerLayoutViewHolder(drawerLayout);
		actionBarViewHolder = new ActionBarViewHolder(this, topBarLayout);
		BottomBarViewHolder bottomBarViewHolder = new BottomBarViewHolder(bottomBarLayout);
		BottomNavigationViewHolder bottomNavigationViewHolder = new BottomNavigationViewHolder(bottomNavigationView, getApplicationContext());

		if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
			bottomNavigationViewHolder.setLandscapeStyle(context);
		}

		perspective = new Perspective(layerModel.getWidth(), layerModel.getHeight());
		workspace = new DefaultWorkspace(layerModel, perspective, new DefaultWorkspace.Listener() {
			@Override
			public void invalidate() {
				drawingSurface.refreshDrawingSurface();
			}
		});
		MainActivityContracts.Navigator navigator = new MainActivityNavigator(this, toolReference);
		MainActivityContracts.Interactor interactor = new MainActivityInteractor();
		model = new MainActivityModel();
		ContextCallback contextCallback = new DefaultContextCallback(context);
		ToolController toolController = new DefaultToolController(toolReference, toolOptionsViewController,
				new DefaultToolFactory(), commandManager, workspace, toolPaint, contextCallback);
		presenter = new MainActivityPresenter(this, model, workspace,
				navigator, interactor, actionBarViewHolder, bottomBarViewHolder, drawerLayoutViewHolder,
				bottomNavigationViewHolder, new DefaultCommandFactory(), commandManager, toolController);
		toolController.setOnColorPickedListener(new PresenterColorPickedListener(presenter));

		keyboardListener = new KeyboardListener(drawerLayout);
		setBottomBarListeners(bottomBarViewHolder);
		setBottomNavigationListeners(bottomNavigationViewHolder);
	}

	private void onCreateLayerMenu() {
		ViewGroup layerLayout = findViewById(R.id.pocketpaint_layer_side_nav_menu);
		DragAndDropListView layerListView = findViewById(R.id.pocketpaint_layer_side_nav_list);

		LayerMenuViewHolder layerMenuViewHolder = new LayerMenuViewHolder(layerLayout);
		LayerNavigator layerNavigator = new LayerNavigator(getApplicationContext());

		layerPresenter = new LayerPresenter(layerModel, layerListView, layerMenuViewHolder,
				commandManager, new DefaultCommandFactory(), layerNavigator);
		LayerAdapter layerAdapter = new LayerAdapter(layerPresenter);
		layerPresenter.setAdapter(layerAdapter);
		layerListView.setPresenter(layerPresenter);
		layerListView.setAdapter(layerAdapter);

		layerPresenter.refreshLayerMenuViewHolder();
		setLayerMenuListeners(layerMenuViewHolder);
	}

	private void onCreateDrawingSurface() {
		drawingSurface = findViewById(R.id.pocketpaint_drawing_surface_view);
		drawingSurface.setArguments(layerModel, perspective, toolReference, toolOptionsViewController);

		appFragment.setPerspective(perspective);
	}

	private void setLayerMenuListeners(LayerMenuViewHolder layerMenuViewHolder) {
		layerMenuViewHolder.layerAddButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				layerPresenter.addLayer();
			}
		});
		layerMenuViewHolder.layerDeleteButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				layerPresenter.removeLayer();
			}
		});
	}

	private void setBottomBarListeners(final BottomBarViewHolder viewHolder) {
		ToolType[] toolTypes = ToolType.values();
		for (final ToolType type : toolTypes) {
			View toolButton = viewHolder.layout.findViewById(type.getToolButtonID());
			if (toolButton == null) {
				continue;
			}

			toolButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					presenter.toolClicked(type);
				}
			});
		}
	}

	private void setBottomNavigationListeners(final BottomNavigationViewHolder viewHolder) {
		viewHolder.getBottomNavigationView().setOnNavigationItemSelectedListener(
				new BottomNavigationView.OnNavigationItemSelectedListener() {
					@Override
					public boolean onNavigationItemSelected(@NonNull MenuItem item) {
						if (item.getItemId() == R.id.pocketpaint_action_tools) {
							presenter.actionToolsClicked();
						} else if (item.getItemId() == R.id.pocketpaint_action_current_tool) {
							presenter.actionCurrentToolClicked();
						} else if (item.getItemId() == R.id.pocketpaint_action_color_picker) {
							presenter.showColorPickerClicked();
						} else if (item.getItemId() == R.id.pocketpaint_action_layers) {
							presenter.showLayerMenuClicked();
						} else {
							return false;
						}
						return true;
					}
				});
	}

	@Override
	public boolean onSupportNavigateUp() {
		onBackPressed();
		return true;
	}

	@Override
	public void commandPreExecute() {
		presenter.onCommandPreExecute();
	}

	@Override
	public void commandPostExecute() {
		if (!isFinishing()) {
			layerPresenter.invalidate();
			presenter.onCommandPostExecute();
		}
	}

	@Override
	protected void onDestroy() {
		commandManager.removeCommandListener(this);

		if (isFinishing()) {
			commandManager.shutdown();
			appFragment.setCurrentTool(null);
			appFragment.setCommandManager(null);
			appFragment.setLayerModel(null);
		}

		super.onDestroy();
	}

	@Override
	protected void onSaveInstanceState(@NonNull Bundle outState) {
		super.onSaveInstanceState(outState);

		outState.putBoolean(IS_SAVED_KEY, model.isSaved());
		outState.putBoolean(WAS_INITIAL_ANIMATION_PLAYED, model.wasInitialAnimationPlayed());
		outState.putParcelable(SAVED_PICTURE_URI_KEY, model.getSavedPictureUri());
		outState.putParcelable(CAMERA_IMAGE_URI_KEY, model.getCameraImageUri());
	}

	@Override
	public void onBackPressed() {
		FragmentManager supportFragmentManager = getSupportFragmentManager();
		if (supportFragmentManager.isStateSaved()) {
			super.onBackPressed();
		} else if (!supportFragmentManager.popBackStackImmediate()) {
			presenter.onBackPressed();
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		presenter.handleActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void superHandleActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onRequestPermissionsResult(final int requestCode, @NonNull final String[] permissions, @NonNull final int[] grantResults) {

		if (VERSION.SDK_INT == Build.VERSION_CODES.M) {
			deferredRequestPermissionsResult = new Runnable() {
				@Override
				public void run() {
					presenter.handleRequestPermissionsResult(requestCode, permissions, grantResults);
				}
			};
		} else {
			presenter.handleRequestPermissionsResult(requestCode, permissions, grantResults);
		}
	}

	@Override
	public void superHandleRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
	}

	@Override
	public boolean isKeyboardShown() {
		return keyboardListener.isSoftKeyboardVisible();
	}

	@Override
	public void refreshDrawingSurface() {
		drawingSurface.refreshDrawingSurface();
	}

	@Override
	public Uri getUriFromFile(File file) {
		return Uri.fromFile(file);
	}

	@Override
	public void hideKeyboard() {
		InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		if (inputMethodManager != null) {
			View rootView = getWindow().getDecorView().getRootView();
			inputMethodManager.hideSoftInputFromWindow(rootView.getWindowToken(), 0);
		}
	}
}

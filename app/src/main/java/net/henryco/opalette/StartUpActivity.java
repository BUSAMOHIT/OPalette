package net.henryco.opalette;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import net.henryco.opalette.api.utils.OPallUtils;
import net.henryco.opalette.application.activities.ProtoActivity;


/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class StartUpActivity extends AppCompatActivity implements OPallUtils.ImageLoadable {

	public static final long SPLASH_LOADING_DELAY = 3000;

	private View mContentView;
	private View mControlsView;


	public static final class BitmapPack {
		private static Bitmap pushUpBitmap = null;
		public static Bitmap get() {
			return pushUpBitmap;
		}
		public static void close(){
			pushUpBitmap.recycle();
			pushUpBitmap = null;
		}
	}


	private void hide() {
		ActionBar actionBar = getSupportActionBar();
		if (actionBar != null) actionBar.hide();
		mControlsView.setVisibility(View.GONE);
		if (actionBar != null) actionBar.show();
		mControlsView.setVisibility(View.VISIBLE);

		mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE | View.SYSTEM_UI_FLAG_FULLSCREEN
				| View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
				| View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
		);
	}




	private void initSplash() {
		ImageView logo = (ImageView) findViewById(R.id.logoImageVIew);
		findViewById(R.id.imageButtonGall).setOnClickListener(v -> OPallUtils.loadImageActivity(this));
		new Thread(() -> {
			long t0 = System.currentTimeMillis();
			while (System.currentTimeMillis() < t0 + SPLASH_LOADING_DELAY)
				synchronized (this) {
					try {
						float nt = ((float)(System.currentTimeMillis() - t0) / SPLASH_LOADING_DELAY);
						logo.setAlpha((float) Math.min(1., Math.sin(Math.PI * nt) * 1.25));
						Thread.sleep(1);
					} catch (Exception ignored) {}
				}
			runOnUiThread(() -> {
				logo.setVisibility(View.GONE);
				findViewById(R.id.firstPickLayout).setVisibility(View.VISIBLE);
			});
		}).start();
	}




	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_start_up_activiy);

		mControlsView = findViewById(R.id.fullscreen_content_controls);
		mContentView = findViewById(R.id.fullscreen_content);
		hide();
		initSplash();
	}


	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			if (requestCode == OPallUtils.activity.REQUEST_PICK_IMAGE) {
				Intent intent = new Intent(this, ProtoActivity.class);
				BitmapPack.pushUpBitmap = OPallUtils.loadIntentBitmap(this, data);
				startActivity(intent);
				finish();
			}
		}
	}

	@Override
	public AppCompatActivity getActivity() {
		return this;
	}


}

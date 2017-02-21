package net.henryco.opalette.activity.t1;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import net.henryco.opalette.R;
import net.henryco.opalette.glES.layouts.OPallSurfaceView;
import net.henryco.opalette.glES.render.camera.OPallCamera2D;
import net.henryco.opalette.glES.render.graphics.textures.OPallTexture;
import net.henryco.opalette.glES.render.renderers.OPallRenderer;
import net.henryco.opalette.utils.Utils;

public class T1Activity extends AppCompatActivity {

	private static final int ACTIVITY_SELECT_IMAGE = 2137;

	private OPallSurfaceView contentSurface;





	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_t1);

		contentSurface = (OPallSurfaceView) findViewById(R.id.contentSurface);
		contentSurface.setDimProcessor(OPallSurfaceView.DimensionProcessors.RELATIVE_SQUARE);
		contentSurface.setOnClickListener(v -> loadImage());
		int width = contentSurface.getWidth();
		int height = contentSurface.getHeight();

		contentSurface.setRenderer(new OPallRenderer(this, new OPallCamera2D(width, height, true)));



	}







	private void loadImage() {
		Intent intent = new Intent(Intent.ACTION_PICK,
				android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		startActivityForResult(intent, ACTIVITY_SELECT_IMAGE);
	}



	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		contentSurface.update(() -> contentSurface.executeWhenDraw(gl -> {
			OPallRenderer renderer = (OPallRenderer) contentSurface.getRenderer();
			renderer.setShader(new OPallTexture(Utils.loadURIBitmap(this, data), this, OPallTexture.filter.LINEAR));
		}));
	}






}

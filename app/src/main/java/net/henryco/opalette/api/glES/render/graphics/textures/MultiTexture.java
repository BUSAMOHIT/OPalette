package net.henryco.opalette.api.glES.render.graphics.textures;

import android.content.Context;
import android.graphics.Bitmap;
import android.opengl.GLES20;

import net.henryco.opalette.api.glES.camera.Camera2D;
import net.henryco.opalette.api.glES.render.graphics.shaders.Shader;
import net.henryco.opalette.api.utils.GLESUtils;
import net.henryco.opalette.api.utils.bounds.Bounds2D;
import net.henryco.opalette.api.utils.bounds.OPallBounds;
import net.henryco.opalette.api.utils.bounds.consumer.BoundsConsumer;
import net.henryco.opalette.api.utils.bounds.observer.OPallMultiBoundsHolder;

import java.nio.FloatBuffer;

/**
 * Created by HenryCo on 23/02/17.
 */

public class MultiTexture extends Shader implements OPallMultiBoundsHolder <Bounds2D>, OPallMultiTexture {




	protected final FloatBuffer texelBuffer;
	protected final boolean[][] textureFlip;
	protected final int[] textureGL_ID;
	protected final int[] textureData_ID;
	public final Bounds2D[] bounds2D;
	private final int texNumb;
	private int focus;







	public MultiTexture(Context context) {
		this(context, DEF_SHADER + ".vert", DEF_SHADER + ".frag", 1);
	}
	public MultiTexture(Context context, int texNumb) {
		this(context, DEF_SHADER + ".vert", DEF_SHADER + ".frag", texNumb);
	}
	public MultiTexture(Context context, String VERT, String FRAG) {
		this(context, VERT, FRAG, 1);
	}

	public MultiTexture(Context context, String VERT, String FRAG, int texNumb) {
		super(context, VERT, FRAG, 2);
		this.texNumb = (texNumb <= 5 && texNumb > 0) ? texNumb : 1;
		texelBuffer = GLESUtils.createFloatBuffer(new float[]{0,1, 0,0, 1,0, 1,1});
		textureGL_ID = new int[this.texNumb];
		textureData_ID = new int[this.texNumb];
		this.textureFlip = new boolean[this.texNumb][2];
		bounds2D = new Bounds2D[this.texNumb];
		for (int i = 0; i < bounds2D.length; i++)
			bounds2D[i] = new Bounds2D().setVertices(OPallBounds.vertices.FLAT_SQUARE_2D())
					.setOrder(OPallBounds.order.FLAT_SQUARE_2D()).setHolder(this);
		this.focus = this.texNumb - 1;
		setFlip(false, false);
	}







	public MultiTexture setFocusOn(int n) {
		this.focus = (n >= 0 && n < texNumb) ? n : texNumb;
		return this;
	}



	public MultiTexture setTexture(int n, Texture texture) {
		this.bounds2D[n] = texture.bounds2D;
		this.textureFlip[n] = texture.textureFlip;
		this.textureGL_ID[n] = texture.textureGL_ID;
		this.textureData_ID[n] = texture.textureData_ID;
		return this;
	}



	@Override
	public void setScreenDim(float w, float h) {
		super.setScreenDim(w, h);
		if (w != 0 && h != 0) updateBounds();
	}







	@Override
	public MultiTexture setBitmap(int n, Bitmap image, Filter filterMin, Filter filterMag) {
		if (image == null || filterMin == null || filterMag == null) return this;
		GLESUtils.glUseProgram(program, () -> {
			textureData_ID[n] = OPallTexture.methods.loadTexture(n, image, filterMin, filterMag);
			textureGL_ID[n] = getTextureUniformHandle(n);
			bounds2D[n].setUniSize(image.getWidth(), image.getHeight());
		});
		return this;
	}

	@Override
	public MultiTexture setBitmap(int n, Bitmap image, Filter filter) {
		return setBitmap(n, image, filter, filter);
	}
	@Override
	public MultiTexture setBitmap(int n, Bitmap image) {
		return setBitmap(n, image, Filter.LINEAR);
	}
	@Override
	public MultiTexture setBitmap(Bitmap image, Filter filterMin, Filter filterMag) {
		return setBitmap(focus, image, filterMin, filterMag);
	}
	@Override
	public MultiTexture setBitmap(Bitmap image, Filter filter) {
		return setBitmap(focus, image, filter);
	}
	@Override
	public MultiTexture setBitmap(Bitmap image) {
		return setBitmap(focus, image);
	}


	@Override
	public MultiTexture setSize(int n, int w, int h) {
		bounds2D[n].setSize(w, h);
		return this;
	}

	@Override
	public MultiTexture setSize(int w, int h) {
		for (int i = 0; i < texNumb; i++) setSize(i, w, h);
		return this;
	}


	@Override
	public MultiTexture setFlip(int n, boolean x, boolean y) {
		textureFlip[n] = new boolean[]{x, y};
		return this;
	}

	@Override
	public MultiTexture setFlip(boolean x, boolean y) {
		for (int i = 0; i < texNumb; i++) setFlip(i, x, y);
		return this;
	}

	@Override
	public MultiTexture bounds(int n, BoundsConsumer<Bounds2D> processor) {
		bounds2D[n].apply(processor);
		return this;
	}

	@Override
	public MultiTexture bounds(BoundsConsumer<Bounds2D> processor) {
		for (Bounds2D d : bounds2D) d.apply(processor);
		return this;
	}

	@Override
	public MultiTexture updateBounds(int n) {
		bounds2D[n].generateVertexBuffer(getScreenWidth(), getScreenHeight());
		return this;
	}

	@Override
	public MultiTexture updateBounds() {
		for (Bounds2D d : bounds2D)
			d.generateVertexBuffer(getScreenWidth(), getScreenHeight());
		return this;
	}




	@Override
	public int getWidth() {
		return (int) bounds2D[focus].getWidth();
	}

	@Override
	public int getHeight() {
		return (int) bounds2D[focus].getHeight();
	}



	@Override
	protected void render(int glProgram, Camera2D camera) {

		int positionHandle = getPositionHandle();
		int mTextureCoordinateHandle = getTextureCoordinateHandle();

		OPallMultiTexture.methods.applyTexNumb(program, texNumb);
		OPallMultiTexture.methods.applyFlip(program, textureFlip);


		GLESUtils.glUseVertexAttribArray(positionHandle, mTextureCoordinateHandle, (Runnable) () -> {

			GLES20.glVertexAttribPointer(positionHandle, COORDS_PER_VERTEX, GLES20.GL_FLOAT, false, vertexStride, bounds2D[focus].vertexBuffer);
			GLES20.glVertexAttribPointer(mTextureCoordinateHandle, COORDS_PER_TEXEL, GLES20.GL_FLOAT, false, texelStride, texelBuffer);
			OPallMultiTexture.methods.bindTextures(textureData_ID, textureGL_ID);
			GLES20.glDrawElements(GLES20.GL_TRIANGLE_STRIP, bounds2D[focus].getVertexCount(), GLES20.GL_UNSIGNED_SHORT, bounds2D[focus].orderBuffer);

		});

	}



}

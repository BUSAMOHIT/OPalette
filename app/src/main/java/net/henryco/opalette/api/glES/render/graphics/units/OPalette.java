/*
 *   /*
 *    * Copyright (C) Henryk Timur Domagalski
 *    *
 *    * Licensed under the Apache License, Version 2.0 (the "License");
 *    * you may not use this file except in compliance with the License.
 *    * You may obtain a copy of the License at
 *    *
 *    *      http://www.apache.org/licenses/LICENSE-2.0
 *    *
 *    * Unless required by applicable law or agreed to in writing, software
 *    * distributed under the License is distributed on an "AS IS" BASIS,
 *    * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    * See the License for the specific language governing permissions and
 *    * limitations under the License.
 *
 */

package net.henryco.opalette.api.glES.render.graphics.units;

import net.henryco.opalette.api.glES.camera.Camera2D;
import net.henryco.opalette.api.glES.render.OPallRenderable;
import net.henryco.opalette.api.glES.render.graphics.fbo.FrameBuffer;
import net.henryco.opalette.api.glES.render.graphics.fbo.OPallFBOCreator;
import net.henryco.opalette.api.glES.render.graphics.shaders.textures.Texture;
import net.henryco.opalette.api.glES.render.graphics.shaders.textures.extend.PaletteTexture;
import net.henryco.opalette.api.glES.render.graphics.units.bar.BarHorizontal;
import net.henryco.opalette.api.glES.render.graphics.units.bar.BarVertical;
import net.henryco.opalette.api.glES.render.graphics.units.bar.OPallBar;
import net.henryco.opalette.api.glES.render.graphics.units.palette.CellPaletter;
import net.henryco.opalette.api.utils.GLESUtils;

/**
 * Created by HenryCo on 31/03/17.
 */

public class OPalette implements OPallRenderable {

	public static final int ORIENTATION_HORIZONTAL = 2;
	public static final int ORIENTATION_VERTICAL = 1;
	public static final int ORIENTATION_NONE = 0;

	private FrameBuffer barGradientBuffer;
	private FrameBuffer barSrcBufferW;
	private FrameBuffer barScrBufferH;

	private OPallBar backBarW;
	private OPallBar backBarH;

	private CellPaletter cellPaletterW;
	private CellPaletter cellPaletterH;

	private PaletteTexture paletteTextureW;
	private PaletteTexture paletteTextureH;

	private int orientation;
	private int buffer_quantum;
	private float scrW, scrH;
	private float[] lineCoeffs;

	private Texture renderData;

	public OPalette(final int orientation) {
		this(orientation, 0, 0);
	}

	public OPalette(final int orientation, int w, int h) {
		setRangeLineCoeffs(new float[]{});
		setOrientation(orientation);
		setBufferQuantum(5);
		create(w, h);
	}

	public OPalette create(int w, int h) {
		barGradientBuffer = OPallFBOCreator.FrameBuffer(w, h, false);

		barSrcBufferW = OPallFBOCreator.FrameBuffer()
				.createFBO(w, buffer_quantum, w, h, false).beginFBO(GLESUtils::clear);
		barScrBufferH = OPallFBOCreator.FrameBuffer()
				.createFBO(buffer_quantum, h, w, h, false).beginFBO(GLESUtils::clear);

		backBarW = new BarHorizontal(w, h);
		backBarH = new BarVertical(w, h);

		cellPaletterW = new CellPaletter(CellPaletter.CellType.Horizontal, w, h);
		cellPaletterH = new CellPaletter(CellPaletter.CellType.Vertical, w, h);

		paletteTextureW = new PaletteTexture(PaletteTexture.TYPE_HORIZONTAL, w, h);
		paletteTextureH = new PaletteTexture(PaletteTexture.TYPE_VERTICAL, w, h);

		setScreenDim(w, h);
		return this;
	}

	@Override
	public void render(Camera2D camera) {
		if (orientation == ORIENTATION_NONE) return;
		if (orientation == ORIENTATION_HORIZONTAL) {

			paletteTextureW.set(0, renderData);
			paletteTextureW.set(1, barSrcBufferW.getTexture());
			paletteTextureW.setFocusOn(1);

			barGradientBuffer.beginFBO(() -> {
				GLESUtils.clear();
				paletteTextureW.setDimension(scrW, scrH);
				paletteTextureW.setStart(scrH - backBarW.getPosY() + backBarW.getHeight());
				paletteTextureW.setEnd(scrH - backBarW.getPosY());
				paletteTextureW.setRangeLineCoeffs(lineCoeffs);
				paletteTextureW.render(camera);
			});

			cellPaletterW.generate(barGradientBuffer.getTexture(), camera);
			backBarW.render(camera, cellPaletterW, buffer_quantum);

		} else if (orientation == ORIENTATION_VERTICAL) {
			// TODO
		}
	}

	public OPalette setBufferQuantum(int bufferQuantum) {
		this.buffer_quantum = bufferQuantum;
		return this;
	}

	public OPalette setRangeLineCoeffs(float[] coeffs) {
		this.lineCoeffs = coeffs;
		return this;
	}

	public OPalette setRenderData(Texture renderData) {
		this.renderData = renderData;
		return this;
	}

	public OPalette setOrientation(int orientation) {
		this.orientation = orientation;
		return this;
	}

	@Override
	public void setScreenDim(float w, float h) {
		scrW = w;
		scrH = h;
	}

	@Override
	public int getWidth() {
		return (int) scrW;
	}

	@Override
	public int getHeight() {
		return (int) scrH;
	}
}

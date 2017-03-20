package net.henryco.opalette.application.programs.sub.programs.image;

import net.henryco.opalette.api.glES.camera.Camera2D;
import net.henryco.opalette.api.glES.render.OPallRenderable;
import net.henryco.opalette.api.glES.render.graphics.fbo.FrameBuffer;
import net.henryco.opalette.api.glES.render.graphics.fbo.OPallFBOCreator;
import net.henryco.opalette.api.glES.render.graphics.shaders.textures.Texture;
import net.henryco.opalette.api.glES.render.graphics.shaders.textures.extend.EdTexture;
import net.henryco.opalette.api.utils.GLESUtils;
import net.henryco.opalette.api.utils.requester.OPallRequester;
import net.henryco.opalette.api.utils.requester.Request;
import net.henryco.opalette.api.utils.views.OPallViewInjector;
import net.henryco.opalette.application.programs.sub.AppSubProgram;
import net.henryco.opalette.application.programs.sub.AppSubProtocol;
import net.henryco.opalette.application.proto.AppMainProto;

import javax.microedition.khronos.opengles.GL10;

/**
 * Created by HenryCo on 16/03/17.
 */

public class ImageProgram implements AppSubProgram<AppMainProto>, AppSubProtocol {

	private long id = methods.genID(ImageProgram.class);

	private FrameBuffer imageBuffer;
	private EdTexture imageTexture;

	private OPallRequester feedBackListener;


	@Override
	public void setFeedBackListener(OPallRequester feedBackListener) {
		this.feedBackListener = feedBackListener;
	}

	@Override
	public void acceptRequest(Request request) {

	}

	@Override
	public long getID() {
		return id;
	}

	@Override
	public void setID(long id) {
		this.id = id;
	}

	@Override
	public void create(GL10 gl, int width, int height, AppMainProto context) {

		if (feedBackListener == null) throw new RuntimeException("FeedBackListener(OPallRequester) == NULL!");

		imageBuffer = OPallFBOCreator.FrameBuffer();

		imageTexture = new EdTexture();
		imageTexture.setScreenDim(width, height);


		OPallViewInjector.inject(context.getActivityContext(), new MaxColorControl(imageTexture));
		OPallViewInjector.inject(context.getActivityContext(), new MinColorControl(imageTexture));
		OPallViewInjector.inject(context.getActivityContext(), new BrightnessControl(imageTexture));
		OPallViewInjector.inject(context.getActivityContext(), new TuneControl(imageTexture));
		OPallViewInjector.inject(context.getActivityContext(), new TranslationControl());
	}



	@Override
	public void onSurfaceChange(GL10 gl, AppMainProto context, int width, int height) {

		imageBuffer.createFBO(width, height, false);
		imageTexture.setScreenDim(width, height);
	}



	@Override
	public void render(GL10 gl10, AppMainProto context, Camera2D camera, int w, int h) {

		imageBuffer.beginFBO(() -> imageTexture.render(camera, program -> GLESUtils.clear()));
		imageBuffer.render(camera);
	}



	@Override
	public void setRenderData(OPallRenderable data) {
		imageTexture.set((Texture) data);
	}

	@Override
	public OPallRenderable getRenderData() {
		return imageBuffer.getTexture();
	}
}

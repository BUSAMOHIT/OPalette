package net.henryco.opalette.application.programs.sub;

import net.henryco.opalette.api.glES.camera.Camera2D;
import net.henryco.opalette.api.glES.glSurface.renderers.universal.OPallSubProgram;
import net.henryco.opalette.api.utils.requester.OPallRequestFeedBack;

import javax.microedition.khronos.opengles.GL10;

/**
 * Created by HenryCo on 16/03/17.
 */

public interface AppSubProgram<T> extends OPallSubProgram<T>, OPallRequestFeedBack {

	void render(GL10 gl10, T context, Camera2D camera, int w, int h);

}

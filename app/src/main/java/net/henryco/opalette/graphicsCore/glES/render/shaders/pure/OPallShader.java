package net.henryco.opalette.graphicsCore.glES.render.shaders.pure;

/**
 * Created by root on 13/02/17.
 */

import android.content.Context;
import android.opengl.GLES20;

import net.henryco.opalette.graphicsCore.glES.render.camera.OPallCamera;
import net.henryco.opalette.utils.GLESUtils;
import net.henryco.opalette.utils.Utils;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

public abstract class OPallShader {

    public final int program;
    public final ShortBuffer orderBuffer;

    public final int COORDS_PER_VERTEX;
    public final int vertexCount;
    public final int vertexStride;

	protected FloatBuffer vertexBuffer;

    /*  Requested in *.vert file:
     *
     *      attribute vec4 a_Position;
     *      uniform mat4 u_MVPMatrix;
     *
     *      void main() {
     *          ...
     *      }
     */

    public OPallShader(Context context, String VERT, String FRAG) {
        this(context, VERT, FRAG, 3);
    }

    public OPallShader(Context context, String VERT, String FRAG, int coordsPerVertex) {

        String vertex = Utils.getSourceAssetsText(VERT, context);
        String fragment = Utils.getSourceAssetsText(FRAG, context);
        program = GLES20.glCreateProgram();
        GLES20.glAttachShader(program, GLESUtils.loadShader(GLES20.GL_VERTEX_SHADER, vertex));
        GLES20.glAttachShader(program, GLESUtils.loadShader(GLES20.GL_FRAGMENT_SHADER, fragment));
        GLES20.glLinkProgram(program);
        GLES20.glUseProgram(program);
        vertexBuffer = GLESUtils.createFloatBuffer(getVertices());
        orderBuffer = GLESUtils.createShortBuffer(getOrder());
        COORDS_PER_VERTEX = coordsPerVertex;
        vertexCount = getOrder().length;
        vertexStride = COORDS_PER_VERTEX * 4; //coz float = 4 byte
    }


    protected abstract float[] getVertices();
    protected abstract short[] getOrder();
    protected abstract void render(final int glProgram, final int positionHandle, final FloatBuffer vertexBuffer, final ShortBuffer orderBuffer, OPallCamera camera);


    public void render(OPallCamera camera) {

        GLES20.glUseProgram(program);
        render(program, GLES20.glGetAttribLocation(program, GLESUtils.a_Position), vertexBuffer, orderBuffer, camera.setProgram(program));
        GLES20.glUseProgram(-1);
    }

    public OPallShader outErrorLog() {
		System.out.println(getErrorLog());
		return this;
    }

	public String getErrorLog() {
		return GLES20.glGetShaderInfoLog(program);
	}

}
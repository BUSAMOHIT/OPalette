package net.henryco.opalette.api.glES.render.graphics.shaders.textures.extend;

import android.content.Context;
import android.graphics.Bitmap;
import android.opengl.GLES20;

import net.henryco.opalette.api.glES.camera.Camera2D;
import net.henryco.opalette.api.glES.render.graphics.shaders.textures.OPallTexture;

import java.nio.FloatBuffer;

/**
 * Created by HenryCo on 20/03/17.
 */

public class ConvolveTexture extends OPallTextureExtended {



	private static final String u_matrixSize = "u_matrixSize";
	private static final String u_matrix3 = "u_matrix3";
	private static final String u_matrix5 = "u_matrix5";
	private static final String u_texDim = "u_screenDim";
	private static final String u_enable = "u_enable";

	public static final FilterMatrices matrix = new FilterMatrices();

	private float[] original_filter_matrix;
	private float[] work_filter_matrix;
	private float center_scale = 1;
	private int matrix_size = 0;
	private int matrix_sqrt_size = 0;
	private int scale_pow;

	private boolean enable = true;

	public ConvolveTexture(Context context) {
		this(context, Filter.LINEAR);
	}
	public ConvolveTexture(Context context, Filter filter) {
		super(context, filter, OPallTexture.DEF_SHADER+".vert", FRAG_DIR);
		init();
	}
	public ConvolveTexture(Bitmap image, Context context) {
		this(image, context, Filter.LINEAR);
	}
	public ConvolveTexture(Bitmap image, Context context, Filter filter) {
		super(image, context, filter, OPallTexture.DEF_SHADER+".vert", FRAG_DIR);
		init();
	}
	public ConvolveTexture() {
		super(OPallTexture.DEFAULT_VERT_FILE, FRAG_FILE);
		init();
	}
	public ConvolveTexture(Bitmap image) {
		this(image, Filter.LINEAR);
	}
	public ConvolveTexture(Bitmap image, Filter filter) {
		super(image, filter, OPallTexture.DEFAULT_VERT_FILE, FRAG_FILE);
		init();
	}


	private void init() {
		setFilterMatrix(matrix.m_identity());
		setScalePower(2);
	}

	@Override
	protected void render(int program, Camera2D camera) {
		GLES20.glUniform2f(GLES20.glGetUniformLocation(program, u_texDim), getWidth(), getHeight());
		GLES20.glUniform1f(GLES20.glGetUniformLocation(program, u_matrixSize), matrix_sqrt_size);
		GLES20.glUniform1i(GLES20.glGetUniformLocation(program, u_enable), enable ? 1 : 0);
		String matrixTarget;
		if (matrix_sqrt_size == 3) matrixTarget = u_matrix3;
		else matrixTarget = u_matrix5;
		GLES20.glUniform1fv(GLES20.glGetUniformLocation(program, matrixTarget), matrix_size, FloatBuffer.wrap(work_filter_matrix));
	}




	public ConvolveTexture setFilterMatrix(float ... matrix) {

		if (matrix.length == 0 || (matrix.length == 1 && matrix[0] == -1))
			matrix = ConvolveTexture.matrix.m_identity();
		if (Math.sqrt(matrix.length) % 2 == 0)
			throw new RuntimeException(getClass().getName()
					+ ": Filter matrix dimension must be 3x3, 5x5, 7x7, 9x9 ...");
		original_filter_matrix = matrix;
		matrix_size = original_filter_matrix.length;
		matrix_sqrt_size = (int) Math.sqrt(matrix_size);
		return setEffectScale(center_scale);
	}

	public ConvolveTexture setEffectScale(final float s) {

		float sc = (float) Math.min(1, Math.pow(s, scale_pow));
		float[] matrix = new float[matrix_size];
		int center = (int) (0.5f * (matrix_sqrt_size - 1f));
		int cp = (center * matrix_sqrt_size) + center;
		for (int i = 0; i < matrix_size; i++) {
			matrix[i] = original_filter_matrix[i];
			if (i != cp) matrix[i] *= sc;
			else {
				float x = matrix[i];
				if (x != 0) {
					float rx = 1f / x;
					matrix[i] = rx + ((x - rx) * sc);
				} else matrix[i] = 1 - sc;
			}
		}
		work_filter_matrix = normalize(matrix);
		center_scale = s;
		return this;
	}

	public ConvolveTexture setScalePower(int scale_pow) {
		this.scale_pow = scale_pow;
		return this;
	}

	public float getEffectScale() {
		return center_scale;
	}


	private static float[] normalize(float[] matrix) {

		float sum = 0;
		for (float v : matrix) sum += v;
		if (sum == 0) return matrix;
		for (int i = 0; i < matrix.length; i++)
			matrix[i] /= sum;
		return matrix;
	}


	public ConvolveTexture setFilterEnable(boolean b) {
		this.enable = b;
		return this;
	}
	public boolean isFilterEnable() {
		return enable;
	}

	private static final String FRAG_DIR = OPallTexture.FRAG_DIR+"/ConvolveFilter.frag";

	private static final String FRAG_FILE =
			"precision mediump float;\n" +
			"\n" +
			"// necessary part\n" +
			"varying vec4 v_Position;\n" +
			"varying vec4 v_WorldPos;\n" +
			"varying vec2 v_TexCoordinate;\n" +
			"\n" +
			"uniform sampler2D u_Texture0;\n" +
			"\n" +
			"\n" +
			"// custom part\n" +
			"uniform float u_matrixSize;  // 3, 5\n" +
			"uniform float u_matrix3[9];\n" +
			"uniform float u_matrix5[25];\n" +
			"uniform vec2 u_screenDim;\n" +
			"uniform int u_enable; // 0 == false, 1... == true\n" +
			"\n" +
			"void main() {\n" +
			"\n" +
			"    if (u_enable == 0) gl_FragColor = texture2D(u_Texture0, v_TexCoordinate).rgba;\n" +
			"    else {\n" +
			"        vec2 cor = vec2((u_matrixSize - 1.) / 2.);\n" +
			"        vec3 rgb = vec3(0.);\n" +
			"\n" +
			"        for (float i = 0.; i < u_matrixSize; i++) {\n" +
			"            for (float k = 0.; k < u_matrixSize; k++) {\n" +
			"                vec2 ipos = vec2(i - cor.x, k - cor.y);\n" +
			"                vec3 irgb = texture2D(u_Texture0, v_TexCoordinate + (ipos / u_screenDim)).rgb;\n" +
			"\n" +
			"                int n = int(i * u_matrixSize + k);\n" +
			"                if (u_matrixSize == 3.) irgb *= u_matrix3[n];\n" +
			"                else irgb *= u_matrix5[n];\n" +
			"\n" +
			"                rgb += irgb;\n" +
			"            }\n" +
			"        }\n" +
			"        gl_FragColor = vec4(rgb, 1.);\n" +
			"    }\n" +
			"}";


	public static final class FilterMatrices {

		public final float[] m_identity(){
			return new float[] {
					0, 0, 0,
					0, 1, 0,
					0, 0, 0
			};
		}
		public final float[] m_boxBlur() {
			return new float[] {
					1, 1, 1,
					1, 1, 1,
					1, 1, 1
			};
		}
		public final float[] m_blur() {
			return new float[] {
					0, 0, 1, 0, 0,
					0, 1, 1, 1, 0,
					1, 1, 1, 1, 1,
					0, 1, 1, 1, 0,
					0, 0, 1, 0, 0
			};
		}
		public final float[] m_gaussianBlur() {
			return new float[] {
					1, 2, 1,
					2, 4, 2,
					1, 2, 1
			};
		}
		public final float[] m_sharpen() {
			return new float[] {
					-1, -1, -1, -1, -1,
					-1,  2,  2,  2, -1,
					-1,  2,  8,  2, -1,
					-1,  2,  2,  2, -1,
					-1, -1, -1, -1, -1
			};
		}
		public final float[] m_sharpen1() {
			return new float[] {
					0, -1, 0,
					-1, 5, -1,
					0, -1, 0
			};
		}
		public final float[] m_sharpen2() {
			return new float[] {
					-1, -1, -1,
					-1, 0, -1,
					-1, -1, -1
			};
		}
		public final float[] m_sharpen3() {
			return new float[]{
					-1, -1, -1,
					-1, 8, -1,
					-1, -1, -1
			};
		}
		public final float[] m_sharpen4() {
			return new float[]{
					1, -2, 1,
					-2, 5, -2,
					1, -2, 1
			};
		}
		public final float[] m_sharpen5() {
			return new float[] {
					-1, -1, -1, -1, -1,
					-1, 3, 4, 3, -1,
					-1, 4, 13, 4, -1,
					-1, 3, 4, 3, -1,
					-1, -1, -1, -1, -1
			};
		}
		public final float[] m_emboss1() {
			return new float[] {
					-2, -1, 0,
					-1, 1, 1,
					0, 1, 2
			};
		}
		public final float[] m_emboss2() {
			return new float[] {
					-2, 0, 0,
					0, 1, 0,
					0, 0, 2
			};
		}
		public final float[] m_diagShatter() {
			return new float[] {
					1, 0, 0, 0, 1,
					0, 0, 0, 0, 0,
					0, 0, 0, 0, 0,
					0, 0, 0, 0, 0,
					1, 0, 0, 0, 1
			};
		}
		public final float[] m_horizontalMotionBlur() {
			return new float[] {
					0, 0, 0, 0, 0,
					0, 0, 0, 0, 0,
					2, 3, 4, 5, 6,
					0, 0, 0, 0, 0,
					0, 0, 0, 0, 0
			};
		}
		public final float[] m_unsharp() {
			return new float[] {
					1, 4, 6, 4, 1,
					4, 16, 24, 16, 4,
					6, 24, -476, 24, 6,
					4, 16, 24, 16, 4,
					1, 4, 6, 4, 1
			};
		}

	}
}

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

package net.henryco.opalette.api.glES.camera;


/*
 * Created by root on 13/02/17.
 */

import net.henryco.opalette.api.utils.Utils;
import net.henryco.opalette.api.utils.lambda.functions.OPallFunction;

/**
 * GL camera class, tuned for 2D, but implements some 3D functionality
 */
public class Camera2D {


	public final OPallCameraMatrix matrix;

	private boolean flipX = true, flipY = true;
	private float pxFacX = 1, pxFacY = 1;
	private float zoom = 1;
	private int width, height;




	public Camera2D(int width, int height, boolean flip) {
		matrix = new OPallCameraMatrix();
		if (flip) flipXY();
		set(width, height);
	}
	public Camera2D(int width, int height) {
		this(width, height, false);
	}
	public Camera2D(boolean flip) {
		this(0, 0, flip);
	}
	public Camera2D() {
		this(false);
	}






    public Camera2D set(int width, int height){
		this.width = width;
		this.height = height;
		Utils.log(width + " : " + height);
		pxFacX = 1 / (width * 0.5f);
		pxFacY = 1 / (height * 0.5f);
		Utils.log(pxFacX + " :: " + pxFacY);
		return this;
    }




    public Camera2D update() {
		matrix.update(0, 0, width, height, zoom, flipX, flipY);
		return this;
    }

	public final float[] getMVPMatrix() {
		return matrix.mMVPMatrix;
	}


	public Camera2D setZoom(float z) {
		zoom = z >= 0 ? z : zoom;
		return this;
	}
	public Camera2D zoom(OPallFunction<Float, Float> zoom) {
		return setZoom(zoom.apply(this.zoom));
	}



	public Camera2D flipXY() {
		return flipX().flipY();
	}
	public Camera2D flipX() {
		flipX = !flipX;
		return this;
	}
	public Camera2D flipY() {
		flipY = !flipY;
		return this;
	}



	public Camera2D backTranslate(Runnable r) {
		float[] position = getPosition();
		float[] rotation = getRotation();
		r.run();
		return setPosition(position).setRotation(rotation);
	}



	public Camera2D translateX_absolute(float x) {
		matrix.eye.x += x;
		matrix.center.x += x;
		return this;
	}
	public Camera2D translateY_absolute(float y) {
		matrix.eye.y += y;
		matrix.center.y += y;
		return this;
	}
	public Camera2D translateZ_absolute(float z) {
		matrix.eye.z += z;
		matrix.center.z += z;
		return this;
	}
	public Camera2D translateXY_absolute(float x, float y) {
		return translateX_absolute(x).translateY_absolute(y);
	}
	public Camera2D translateXYZ_absolute(float x, float y, float z) {
		return translateXY_absolute(x, y).translateZ_absolute(z);
	}





	public Camera2D translateX(float x_px) {
		return translateX_absolute(x_px * pxFacX);
	}
	public Camera2D translateY(float y_px) {
		return translateY_absolute(y_px * pxFacY);
	}
	public Camera2D translateXY(float x_px, float y_px) {
		return translateX(x_px).translateY(y_px);
	}
	public Camera2D translateXY(float[] xy_px) {
		return translateX(xy_px[0]).translateY(xy_px[1]);
	}


	public float[] getPosition() {
		return new float[]{matrix.eye.x, matrix.eye.y};
	}
	public Camera2D setPosition(float[] vec) {
		return setPosXY_absolute(vec[0], vec[1]);
	}
	public float[] getRotation() {
		return new float[]{matrix.rotation.x, matrix.rotation.y, matrix.rotation.z};
	}
	public Camera2D setRotation(float[] rot) {
		setRotation(rot[0], rot[1], rot[2]);
		return this;
	}

	public Camera2D setPosX_absolute(float x) {
		matrix.eye.x = x;
		matrix.center.x = x;
		return this;
	}
	public Camera2D setPosY_absolute(float y) {
		matrix.eye.y = y;
		matrix.center.y = y;
		return this;
	}
	public Camera2D setPosZ_absolute(float z) {
		matrix.eye.z = z;
		matrix.center.z = z;
		return this;
	}
	public Camera2D setPosXY_absolute(float x, float y) {
		return setPosX_absolute(x).setPosY_absolute(y);
	}
	public Camera2D setPosXYZ_absolute(float x, float y, float z) {
		return setPosXY_absolute(x, y).setPosZ_absolute(z);
	}





	public Camera2D setPosX(float x_px) {
		return setPosX_absolute(x_px * pxFacX);
	}
	public Camera2D setPosY(float y_px) {
		return setPosY_absolute(y_px * pxFacY);
	}
	public Camera2D setPosXY(float x_px, float y_px) {
		return setPosX(x_px).setPosY(y_px);
	}








	public Camera2D rotateX(float deg) {
		return rotate(deg,0,  0);
	}
	public Camera2D rotateY(float deg) {
		return rotate(0, deg, 0);
	}
	public Camera2D rotateZ(float deg) {
		return rotate(0,  0,deg);
	}
	public Camera2D rotate(float deg_x, float deg_y, float deg_z) {
		matrix.rotation.x += deg_x;
		matrix.rotation.y += deg_y;
		matrix.rotation.z += deg_z;
		return this;
	}
	public Camera2D rotate(float angle_deg) {
		return rotateZ(angle_deg);
	}




	public Camera2D setRotationX(float deg) {
		return setRotation(deg, matrix.rotation.y, matrix.rotation.z);
	}
	public Camera2D setRotationY(float deg) {
		return setRotation(matrix.rotation.x, deg, matrix.rotation.z);
	}
	public Camera2D setRotationZ(float deg) {
		return setRotation(matrix.rotation.x, matrix.rotation.y, deg);
	}
	public Camera2D setRotation(float deg_x, float deg_y, float deg_z) {
		return rotate(deg_x - matrix.rotation.x, deg_y - matrix.rotation.y, deg_z - matrix.rotation.z);
	}
	public Camera2D setRotation(float angle_deg) {
		return setRotationZ(angle_deg);
	}

}


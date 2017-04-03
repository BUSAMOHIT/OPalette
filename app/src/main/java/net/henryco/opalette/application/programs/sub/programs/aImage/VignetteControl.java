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

package net.henryco.opalette.application.programs.sub.programs.aImage;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import net.henryco.opalette.R;
import net.henryco.opalette.api.glES.render.graphics.shaders.shapes.Vignette;
import net.henryco.opalette.api.glES.render.graphics.shaders.textures.extend.ConvolveTexture;
import net.henryco.opalette.api.utils.RefreshableTimer;
import net.henryco.opalette.api.utils.views.OPallViewInjector;
import net.henryco.opalette.api.utils.views.widgets.OPallSeekBarListener;
import net.henryco.opalette.application.injectables.InjectableSeekBar;
import net.henryco.opalette.application.programs.sub.AppSubProgram;
import net.henryco.opalette.application.programs.sub.programs.AppAutoSubControl;
import net.henryco.opalette.application.proto.AppMainProto;

/**
 * Created by HenryCo on 03/04/17.
 */

public class VignetteControl extends AppAutoSubControl<AppMainProto> {

 	private static final int img_button_res = R.drawable.ic_vignette_white_24dp;
	private static final int txt_button_res = R.string.control_vignette;

	private final Vignette vignette;
	private AppSubProgram.ProxyRenderData<ConvolveTexture> imgHolder;

	public VignetteControl(final Vignette vignette, final AppSubProgram.ProxyRenderData<ConvolveTexture> renderData) {
		super(img_button_res, txt_button_res);
		this.vignette = vignette;
		this.imgHolder = renderData;
	}

	private Runnable updateFunc = () -> imgHolder.setStateUpdated();
	private Runnable stopFunc = () -> {
		imgHolder.getRenderData().setFilterEnable(true);
		updateFunc.run();
	};
	private RefreshableTimer timer = new RefreshableTimer(500, stopFunc);
	private OPallSeekBarListener stop = new OPallSeekBarListener().onStop(bar -> timer.startIfWaiting().refresh());


	@Override
	protected void onFragmentCreate(View view, AppMainProto context, @Nullable Bundle savedInstanceState) {

		updateFunc = () -> {
			imgHolder.setStateUpdated();
			context.getRenderSurface().update();
		};

		InjectableSeekBar vigBar = new InjectableSeekBar(view, context.getActivityContext().getResources().getString(txt_button_res));
		vigBar.onBarCreate(bar -> bar.setProgress(vigBar.de_norm(vignette.getPower())));
		vigBar.setBarListener(new OPallSeekBarListener().onProgress((bar, progress, fromUser) -> {
			if (fromUser) {
				imgHolder.getRenderData().setFilterEnable(false);
				vignette.setPower(vigBar.norm(progress));
				vignette.setActive(!(progress == 0));
				updateFunc.run();
			}
		}).onStop(stop));

		context.setTopControlButton(button -> button.setVisible(true).setEnabled(true).setTitle(R.string.disable), () -> {
			vigBar.setProgress(0);
			vignette.setPower(0).setActive(false);
			updateFunc.run();
		});

		OPallViewInjector.inject(context.getActivityContext(), vigBar);
	}
}

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

package net.henryco.opalette.application.programs.sub.programs.gradient;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import net.henryco.opalette.R;
import net.henryco.opalette.api.glES.render.graphics.units.OPalette;
import net.henryco.opalette.api.utils.views.OPallViewInjector;
import net.henryco.opalette.api.utils.views.widgets.OPallSeekBarListener;
import net.henryco.opalette.application.injectables.InjectableSeekBar;
import net.henryco.opalette.application.programs.sub.programs.AppAutoSubControl;
import net.henryco.opalette.application.proto.AppMainProto;

/**
 * Created by HenryCo on 01/04/17.
 */

public class BarCellControl extends AppAutoSubControl<AppMainProto> {

	private static final int img_button_res = R.drawable.ic_view_column_white_24dp;
	private static final int txt_button_res = R.string.control_cells;
	private static final int target_layer = R.id.paletteOptionsContainer;

	private static final int MAX_CELLS = 20;

	private final OPalette palette;
	private final int def_cell_numb;
	private final float def_cell_margin;
	private final float def_cell_content_size;

	public BarCellControl(final OPalette palette) {
		super(target_layer, img_button_res, txt_button_res);
		this.def_cell_numb = palette.getCellNumb();
		this.def_cell_margin = palette.getMargin_pct();
		this.def_cell_content_size = palette.getContentSize_pct();
		this.palette = palette;
	}

	@Override
	protected void onFragmentCreate(View view, AppMainProto context, @Nullable Bundle savedInstanceState) {

		InjectableSeekBar numBar = new InjectableSeekBar(view, InjectableSeekBar.TYPE_SMALL, "Number");
		numBar.setMax(MAX_CELLS).setDiscrete(true);
		numBar.onBarCreate(bar -> bar.setProgress(Math.min(MAX_CELLS, palette.getCellNumb())));
		numBar.setBarListener(new OPallSeekBarListener().onProgress((bar, progress, fromUser) -> {
			palette.setCellNumb(progress);
			if (palette.isDiscrete()) context.getRenderSurface().update();
		}));




		OPallViewInjector.inject(context.getActivityContext(), numBar);

	}
}
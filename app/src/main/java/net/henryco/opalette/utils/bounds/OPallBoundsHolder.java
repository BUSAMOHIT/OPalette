package net.henryco.opalette.utils.bounds;


/**
 * Created by HenryCo on 23/02/17.
 */
public interface OPallBoundsHolder <T extends OPallBounds> {

	OPallBoundsHolder bounds(BoundsConsumer<T> processor);
	OPallBoundsHolder updateBounds();


	OPallBoundsHolder proxyHolder = new OPallBoundsHolder() {
		@Override
		public OPallBoundsHolder bounds(BoundsConsumer processor) {
			return this;
		}
		@Override
		public OPallBoundsHolder updateBounds() {
			return this;
		}
	};
}

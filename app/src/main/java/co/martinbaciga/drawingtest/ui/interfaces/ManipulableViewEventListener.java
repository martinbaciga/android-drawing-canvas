package co.martinbaciga.drawingtest.ui.interfaces;

import co.martinbaciga.drawingtest.ui.component.ManipulableView;

public interface ManipulableViewEventListener
{
	public void onDragFinished(ManipulableView v);
	public void onScaleFinished(ManipulableView v);
	public void onDeleteClick(ManipulableView v);
	public void onTap(ManipulableView v);
}

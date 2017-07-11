package hr.fer.zemris.java.hw16.jvdraw.model;

import javax.swing.AbstractListModel;
import javax.swing.JList;

import hr.fer.zemris.java.hw16.jvdraw.graphicalObject.GeometricalObject;

/**
 * Razred koji nasljeđuje {@link AbstractListModel}. Ovaj razred predstavlja
 * objektni adapter za primjerak razreda koji implementira sučelje
 * {@link DrawingModel}, odnosnu sav dohvata primjeraka razreda
 * {@link GeometricalObject} i sve dodatne informacije primjerci ovog razreda
 * dohvaćaju iz {@link DrawingModel}a. Ovaj razred implementira i sučelje
 * {@link DrawingModelListener} kako bi slušao na svaku promjenu unutar modela,
 * te time mogao obavjestiti i svoj pogled modeliran razredom {@link JList} da
 * promjeni prikaz.
 * 
 * @see DrawingModel
 * @see DrawingModelListener
 * @see JList
 * 
 * @author Davor Češljaš
 */
public class DrawingObjectListModel extends AbstractListModel<GeometricalObject> implements DrawingModelListener {

	/**
	 * Konstanta koja se koristi prilikom serijalizacije objekata ovog razreda
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Članska varijabla koja predstavlja primjerak razreda koji implementira
	 * sučelje {@link DrawingModel}, a čiji je ovo objektni adapter
	 */
	private DrawingModel model;

	/**
	 * Konstruktor koji inicijalizira primjerak ovog razreda. Unutar ovog
	 * konstruktora predani parametar <b>model</b> interno se pohranjuje te se
	 * nad njim registrira primjerak ovog razreda
	 *
	 * @param model
	 *            primjerak razreda koji implementira sučelje
	 *            {@link DrawingModel}, a čiji je ovo objektni adapter
	 */
	public DrawingObjectListModel(DrawingModel model) {
		this.model = model;
		this.model.addDrawingModelListener(this);
	}

	@Override
	public int getSize() {
		return model.getSize();
	}

	@Override
	public GeometricalObject getElementAt(int index) {
		return model.getObject(index);
	}

	@Override
	public void objectsAdded(DrawingModel source, int index0, int index1) {
		fireIntervalAdded(this, index0, index1);
	}

	@Override
	public void objectsRemoved(DrawingModel source, int index0, int index1) {
		fireIntervalRemoved(this, index0, index1);
	}

	@Override
	public void objectsChanged(DrawingModel source, int index0, int index1) {
		fireContentsChanged(this, index0, index1);
	}
}

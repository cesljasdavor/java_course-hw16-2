package hr.fer.zemris.java.hw16.jvdraw.model;

import hr.fer.zemris.java.hw16.jvdraw.graphicalObject.GeometricalObject;

/**
 * Sučelje koje predstavlja model podataka za komponentu koja može iscrtavati
 * objekte koji su primjerci sučelja {@link GeometricalObject}. Ovaj model nudi
 * sljedeće metode, kako bi se navedena funkcionalnsot ostvarila:
 * <ul>
 * <li>{@link #getSize()}</li>
 * <li>{@link #add(GeometricalObject)}</li>
 * <li>{@link #getObject(int)}</li>
 * </ul>
 * 
 * Sučelje također predstavlja apstraktni subjekt unutar oblikovnog obrasca
 * <a href ="https://en.wikipedia.org/wiki/Visitor_pattern">promatrač</a>. Time
 * sučelje nudi i metodu za dodavanja promatrača
 * {@link #addDrawingModelListener(DrawingModelListener)} te metodu za brisanje
 * promatrača {@link #removeDrawingModelListener(DrawingModelListener)}
 * 
 * @see DrawingModelListener
 * 
 * @author Davor Češljaš
 */
public interface DrawingModel {

	/**
	 * Metoda koja dohvaća količinu primjeraka razreda koji implementiraju
	 * sučelje {@link GeometricalObject}, a koji su spremljeni unutar ovog
	 * modela.
	 *
	 * @return količinu primjeraka razreda koji implementiraju sučelje
	 *         {@link GeometricalObject}, a koji su spremljeni unutar ovog
	 *         modela.
	 */
	int getSize();

	/**
	 * Metoda koja dohvaća primjerak razreda koji implementira sučelje
	 * {@link GeometricalObject} koji se nalazi na poziciju <b>index</b>
	 *
	 * @param index
	 *            pozicija traženog primjerka razreda koji implementira sučelje
	 *            {@link GeometricalObject}
	 * @return primjerak razreda koji implementira sučelje
	 *         {@link GeometricalObject} koji se nalazi na poziciju <b>index</b>
	 */
	GeometricalObject getObject(int index);

	/**
	 * Metoda koja interno pohranjuje primjerak razreda koji implementira
	 * sučelje {@link GeometricalObject} koji je predan kao parametar
	 * <b>object</b>
	 *
	 * @param object
	 *            primjerak razreda koji implementira sučelje
	 *            {@link GeometricalObject} koji se interno pohranjuje
	 */
	void add(GeometricalObject object);

	/**
	 * Metoda koja interno pohranjuje primjerak razreda koji implementira
	 * sučelje {@link DrawingModelListener}, a koji predstavlja promatrača nad
	 * ovim subjektom
	 *
	 * @param l
	 *            primjerak razreda koji implementira sučelje
	 *            {@link DrawingModelListener}
	 */
	void addDrawingModelListener(DrawingModelListener l);

	/**
	 * Metoda koja briše interno pohranjeni primjerak razreda koji implementira
	 * sučelje {@link DrawingModelListener} <b>l</b>, ukoliko takav postoji.
	 *
	 * @param l
	 *            interno pohranjeni primjerak razreda koji implementira sučelje
	 *            {@link DrawingModelListener}
	 */
	void removeDrawingModelListener(DrawingModelListener l);
}

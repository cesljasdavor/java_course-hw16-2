package hr.fer.zemris.java.hw16.jvdraw.model;

/**
 * Sučelje koje predstavlja apstraktnog promatrača iz oblikovnog obrsca
 * <a href ="https://en.wikipedia.org/wiki/Visitor_pattern">promatrač</a>. Ovaj
 * promatrač promatra promjene nad primjerkom razreda koji implementira sučelje
 * {@link DrawingModel}
 * 
 * @see DrawingModel
 * 
 * @author Davor Češljaš
 * 
 */
public interface DrawingModelListener {

	/**
	 * Metoda koju subjekt (primjerak razreda koji implementira sučelje
	 * {@link DrawingModel}) poziva ukoliko su elementi dodani u interno
	 * spremište subjekta.
	 *
	 * @param source
	 *            primjerak razreda koji implementira sučelje
	 *            {@link DrawingModel} nad kojim je ovaj apstraktni promatrač
	 *            registriran
	 * @param index0
	 *            početna pozicija na koju je element dodan (uključivo)
	 * @param index1
	 *            završna pozicija na koju je element dodan (uključivo)
	 */
	void objectsAdded(DrawingModel source, int index0, int index1);

	/**
	 * Metoda koju subjekt (primjerak razreda koji implementira sučelje
	 * {@link DrawingModel}) poziva ukoliko su elementi obrisani unutar internog
	 * spremišta subjekta.
	 *
	 * @param source
	 *            primjerak razreda koji implementira sučelje
	 *            {@link DrawingModel} nad kojim je ovaj apstraktni promatrač
	 *            registriran
	 * @param index0
	 *            početna pozicija na kojoj je element obrisan (uključivo)
	 * @param index1
	 *            završna pozicija na kojoj je element obrisan (uključivo)
	 */
	void objectsRemoved(DrawingModel source, int index0, int index1);

	/**
	 * Metoda koju subjekt (primjerak razreda koji implementira sučelje
	 * {@link DrawingModel}) poziva ukoliko su elementi promijenjeni unutar
	 * internog spremišta subjekta.
	 *
	 * @param source
	 *            primjerak razreda koji implementira sučelje
	 *            {@link DrawingModel} nad kojim je ovaj apstraktni promatrač
	 *            registriran
	 * @param index0
	 *            početna pozicija na kojoj se dogodila promjena (uključivo)
	 * @param index1
	 *            završna pozicija na kojoj se dogodila promjena (uključivo)
	 */
	void objectsChanged(DrawingModel source, int index0, int index1);
}

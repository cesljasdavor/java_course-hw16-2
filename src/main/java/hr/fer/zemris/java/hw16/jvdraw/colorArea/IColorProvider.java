package hr.fer.zemris.java.hw16.jvdraw.colorArea;

import java.awt.Color;

/**
 * Sučelje koje predstavlja apstraktnog subjekta unutar oblikovnog obrasca
 * <a href ="https://en.wikipedia.org/wiki/Visitor_pattern">promatrač</a>.
 * Konkretan subjekt koji implementira ovo sučelje dužan je obavijestiti sve
 * registrirane promatrače kada se dogodi promjena boje predstavljene razredom
 * {@link Color}.
 * 
 * @see Color
 * 
 * @author Davor Češljaš
 */
public interface IColorProvider {

	/**
	 * Metoda koja dohvaća trenutno aktivnu boju predstavljenu razredom
	 * {@link Color} koju interno pohranjuje ovaj objekt.
	 *
	 * @return trenutno aktivnu boju predstavljenu razredom {@link Color}
	 */
	Color getCurrentColor();

	/**
	 * Metoda koja interno pohranjuje primjerak razreda koji implementira
	 * sučelje {@link ColorChangeListener}, a koji predstavlja promatrača nad
	 * ovim subjektom
	 *
	 * @param l
	 *            primjerak razreda koji implementira sučelje
	 *            {@link ColorChangeListener}
	 */
	void addColorChangeListener(ColorChangeListener l);

	/**
	 * Metoda koja briše interno pohranjeni primjerak razreda koji implementira
	 * sučelje {@link ColorChangeListener} <b>l</b>, ukoliko takav postoji.
	 *
	 * @param l
	 *            interno pohranjeni primjerak razreda koji implementira sučelje
	 *            {@link ColorChangeListener}
	 */
	void removeColorChangeListener(ColorChangeListener l);
}

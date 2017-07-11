package hr.fer.zemris.java.hw16.jvdraw.colorArea;

import java.awt.Color;

/**
 * Sučelje koje predstavlja apstraktnog promatrača iz oblikovnog obrsca
 * <a href ="https://en.wikipedia.org/wiki/Visitor_pattern">promatrač</a>. Ovaj
 * promatrač promatra promjene nad primjerkom razreda koji implementira sučelje
 * {@link IColorProvider}
 * 
 * @see IColorProvider
 * 
 * @author Davor Češljaš
 * 
 */
public interface ColorChangeListener {

	/**
	 * Metoda koja se nad konkretnim promatračem poziva u trenutku kada se
	 * dogodi promjena boje unutar subjekta predstavljenog sučeljem
	 * {@link IColorProvider}. Metodu je konkretni subjekt dužan pozvati
	 * predajući i podatke o predhodno aktivnoj boji <b>oldColor</b> te trenutno
	 * aktivnoj (novo postavljenoj) boji <b>newColor</b>
	 *
	 * @param source
	 *            konkretan subjekt modeliran sučeljem {@link IColorProvider}
	 *            nad kojim se dogodila promjena boje
	 * @param oldColor
	 *            predhodno aktivna boja predstavljena razredom {@link Color}
	 * @param newColor
	 *            trenutno aktivna (novo postavljena) boja predstavljena
	 *            razredom {@link Color}
	 */
	public void newColorSelected(IColorProvider source, Color oldColor, Color newColor);
}

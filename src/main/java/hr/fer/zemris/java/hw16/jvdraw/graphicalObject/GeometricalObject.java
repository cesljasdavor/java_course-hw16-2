package hr.fer.zemris.java.hw16.jvdraw.graphicalObject;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;

import javax.swing.JPanel;

import hr.fer.zemris.java.hw16.jvdraw.JVDraw;

/**
 * Sučelje koje predstavlja jedan geometrijski objekt. Ovo sučelje koristi se
 * unutar programa {@link JVDraw} te zbog toga nudi sljedeće specifične metode:
 * <ul>
 * <li>{@link #paint(Graphics2D)}</li>
 * <li>{@link #getBoundingBox()}</li>
 * <li>{@link #changeSecondCoordinate(Point)}</li>
 * <li>{@link #createChangePanel()}</li>
 * <li>{@link #change(JPanel)}</li>
 * <li>{@link #toJVD()}</li>
 * </ul>
 * 
 * Napomena: Svi razredi koji implementiraju ovo sučelje moraju se moći iscrtati
 * tako da im se predaju svega dvije točke, a koje oni mogu tumačiti kako god je
 * potrebno
 * 
 * @see JVDraw
 * @see JPanel
 * 
 * @author Davor Češljaš
 */
public interface GeometricalObject {

	/**
	 * Metoda koja se koristi za iscrtavanje ovog objekta koristeći predani
	 * primjerak razreda {@link Graphics2D} <b>g2</b>
	 *
	 * @param g2
	 *            primjerak razreda {@link Graphics2D} koji se koristi za
	 *            iscrtavanje
	 */
	void paint(Graphics2D g2);

	/**
	 * Metoda koja pronalazi minimalnu pravokutnu površinu potrebnu za
	 * iscrtavanje ovog objekta te povrpinu vraća kao primjerak razreda
	 * {@link Rectangle}
	 *
	 * @return primjerak razreda {@link Rectangle} koji predstavlja minimalnu
	 *         pravokutnu površinu potrebnu za iscrtavanje ovog objekta
	 */
	Rectangle getBoundingBox();

	/**
	 * Metoda koja se koristi za promjenu druge koordinate ovog objekta. Za više
	 * pojašnjenja pročitati vršnu dokumentaciju sučelja
	 *
	 * @param newCoordinate
	 *            nova druga koordinata modelirana razredom {@link Point}
	 */
	void changeSecondCoordinate(Point newCoordinate);

	/**
	 * Metoda koja se koristi za kreiranje primjerka razreda {@link JPanel}, a
	 * koji se koristi za promjenu podataka nad ovim objektom. Koja će polja
	 * imati ovaj primjerak razreda {@link JPanel} ovisi o implementaciji
	 *
	 * @return primjerak razreda {@link JPanel} koji se koristi za unos promjena
	 *         nad ovim objektom
	 */
	JPanel createChangePanel();

	/**
	 * Metoda koja se koristi da se na temelju predanog parametra <b>panel</b>
	 * naprave sve promjene koje je korisnik unio. Očekuje se da je predani
	 * <b>panel</b> isti onaj dobiven pozivom metode
	 * {@link #createChangePanel()} sa promjenjenim poljima (ovisi o
	 * implementaciji)
	 *
	 * @param panel
	 *            panel dobiven pozivom metode {@link #createChangePanel()}
	 *            nadopunjen podacima.
	 * @return <code>true</code> ukoliko je promjena uspješno obavljena,
	 *         <code>false</code> inače
	 */
	boolean change(JPanel panel);

	/**
	 * Metoda koja se koristi kako bi se ovaj objekt pretvorio u jednu liniju
	 * .jvd datoteke
	 *
	 * @return jedna linija .jvd datoteke koja predstavlja ovaj objekt
	 */
	String toJVD();
}

package hr.fer.zemris.java.hw16.jvdraw.bottomLabel;

import java.awt.Color;

import javax.swing.JLabel;

import hr.fer.zemris.java.hw16.jvdraw.colorArea.ColorChangeListener;
import hr.fer.zemris.java.hw16.jvdraw.colorArea.JColorArea;

/**
 * Razred koji nasljeđuje razred {@link JLabel}. Primjerci ovog razreda koriste
 * se za prikaz RGB komponenti dvaju primjeraka razreda {@link JColorArea}.
 * Konkretno, jedna od dvaju {@link JColorArea} predstavlja prednju boju, a
 * druga stražnju boju crtanja.
 * 
 * @see JLabel
 * @see JColorArea
 * 
 * @author Davor Češljaš
 */
public class BottomLabel extends JLabel {

	/**
	 * Konstanta koja se koristi prilikom serijalizacije objekata ovog razreda
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Članska varijabla koja predstavlja primjerak razreda {@link JColorArea}
	 * koji predstavlja prednju boju crtanja
	 */
	private JColorArea fgColorArea;

	/**
	 * Članska varijabla koja predstavlja primjerak razreda {@link JColorArea}
	 * koji predstavlja stražnju boju crtanja
	 */
	private JColorArea bgColorArea;

	/**
	 * Konstruktor koji inicijalizira primjerak ovog razreda. Unutar ovog
	 * razreda interno se pohranjuju predani parametri te se vrši registracija
	 * na promjenu boje svakog od njih.
	 *
	 * @param fgColorArea
	 *            primjerak razreda {@link JColorArea} koji predstavlja prednju
	 *            boju crtanja
	 * @param bgColorArea
	 *            primjerak razreda {@link JColorArea} koji predstavlja stražnju
	 *            boju crtanja
	 */
	public BottomLabel(JColorArea fgColorArea, JColorArea bgColorArea) {
		this.fgColorArea = fgColorArea;
		this.bgColorArea = bgColorArea;
		changeText();

		ColorChangeListener l = (source, oldColor, newColor) -> changeText();
		this.fgColorArea.addColorChangeListener(l);
		this.bgColorArea.addColorChangeListener(l);
	}

	/**
	 * Pomoćna metoda koja se koristi za promjenu teksta ove labele. Format
	 * ispisa je sljedeći:
	 * <p>
	 * <i>Foreground color: (red, green, blue), background color: (red, green,
	 * blue).</i>
	 * </p>
	 */
	private void changeText() {
		Color foreground = fgColorArea.getCurrentColor();
		Color background = bgColorArea.getCurrentColor();

		setText(String.format("Foreground color: (%d, %d, %d), background color: (%d, %d, %d).", 
				foreground.getRed(), foreground.getGreen(), foreground.getBlue(), 
				background.getRed(), background.getGreen(), background.getBlue()));
	}

}

package hr.fer.zemris.java.hw16.jvdraw.colorArea;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JColorChooser;
import javax.swing.JComponent;

/**
 * Razred koji nasljeđuje razred {@link JComponent}. Primjerci ovog razreda
 * predstavljaju pravokutno područje veličine ({@value #WIDTH} x
 * {@value #HEIGHT}). Klikom na ovo područje otvara se primjerak razreda
 * {@link JColorChooser}, pomoću kojeg se korisniku nudi izbor boje za ovo
 * područje.
 * 
 * <p>
 * Dodatno ovaj razred je i konkretni subjekt unutar oblikovnog obrasca
 * <a href ="https://en.wikipedia.org/wiki/Visitor_pattern">promatrač</a> jer
 * implementira sučelje {@link IColorProvider} te su time primjerci ovog razreda
 * zaduženi za obavještavanje svih registriranih promatrača kada korisnik
 * promjeni boju ovog područja.
 * </p>
 * 
 * @see JComponent
 * @see JColorChooser
 * @see IColorProvider
 * 
 * @author Davor Češljaš
 */
public class JColorArea extends JComponent implements IColorProvider {

	/**
	 * Konstanta koja se koristi prilikom serijalizacije objekata ovog razreda
	 */
	private static final long serialVersionUID = 1L;

	/** Konstanta koja predstavlja širinu ove komponente */
	private static final int WIDTH = 15;

	/** Konstanta koja predstavlja visinu ove komponente */
	private static final int HEIGHT = 15;

	/**
	 * Članska varijabla koja predstavlja trenutno odabranu boju površine ove
	 * komponente
	 */
	private Color selectedColor;

	/**
	 * Članska varijabla koja predstavlja {@link List} svih registriranih
	 * promatrača modeliranih sučelje {@link ColorChangeListener}
	 */
	private List<ColorChangeListener> listeners;

	/**
	 * Konstruktor koji inicijalizira primjerak ovog razreda. Unutar ovog
	 * konstruktora interno se pohranjuje predana boja <b>selectedColor</b>.
	 *
	 * @param selectedColor
	 *            inicijalna boja površine ove komponente
	 */
	public JColorArea(Color selectedColor) {
		this.selectedColor = selectedColor;
		this.listeners = new ArrayList<>();

		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				Color newColor = JColorChooser.showDialog(JColorArea.this, "Choose color", selectedColor);
				if (newColor == null) {
					return;
				}

				fireNewColorSeleted(newColor);
				repaint();
			}
		});
	}

	@Override
	public Dimension getPreferredSize() {
		return new Dimension(WIDTH, HEIGHT);
	}

	@Override
	protected void paintComponent(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		Dimension dim = getSize();
		Insets ins = getInsets();

		g2.setColor(selectedColor);

		g2.fillRect(ins.left, ins.top, dim.width - ins.left - ins.right, dim.height - ins.bottom - ins.top);
	}

	@Override
	public Color getCurrentColor() {
		return selectedColor;
	}

	@Override
	public void addColorChangeListener(ColorChangeListener l) {
		listeners = new ArrayList<>(listeners);
		listeners.add(l);
	}

	@Override
	public void removeColorChangeListener(ColorChangeListener l) {
		listeners = new ArrayList<>(listeners);
		listeners.remove(l);
	}

	/**
	 * Pomoćna metoda koja se koristi za obavještavanje svih zainteresiranih
	 * promatrača modeliranih sučeljem {@link ColorChangeListener}, da je došlo
	 * do promjene trenutno aktiven boje, odnosno da je korisnik promijenio
	 * boju.
	 *
	 * @param newColor
	 *            nova trenutno aktivna boja koju je korisnik odabra.
	 */
	private void fireNewColorSeleted(Color newColor) {
		Color oldColor = selectedColor;
		selectedColor = newColor;

		for (ColorChangeListener l : listeners) {
			l.newColorSelected(this, oldColor, newColor);
		}
	}
}

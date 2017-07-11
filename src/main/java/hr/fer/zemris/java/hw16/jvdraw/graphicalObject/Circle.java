package hr.fer.zemris.java.hw16.jvdraw.graphicalObject;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import hr.fer.zemris.java.hw16.jvdraw.colorArea.JColorArea;

/**
 * Razred koji implementira sučelje {@link GeometricalObject}.Primjerci ovog razreda
 * predstavlju jedan krug. Svakom krugu definirana je točka <b>center</b>,
 * radijus <b>radius</b> te boja oboda <b>fgColor</b>. Ovaj krug je u stvari
 * kružnica, budući da je predstavljen samo obodom.
 * <p>
 * Kreiranje ovog primjerka razreda može se ostvariti pozivom jednog od
 * konstruktora: {@link #Circle(Point, int, Color)},
 * {@link #Circle(Point, Point, Color)} ili pozivom metode tvornice
 * {@link #createFromJVDLine(String)}, koja prima jedan redak iz .jvd datoteke
 * </p>
 * 
 * @see GeometricalObject
 * 
 * @author Davor Češljaš
 */
public class Circle implements GeometricalObject {

	/** Konstanta koja predstavlja naziv koji se veže uz ovaj razred */
	public static final String NAME = "Circle";

	/**
	 * Konstanta koja predstavlja početak redka unutar .jvd datoteke u kojem se
	 * nalaze podaci za krug
	 */
	public static final String JVD_REPRESENTATION = "CIRCLE";

	/** Statički brojač, koji definira koliko je krugova do sada konstruirano */
	private static int circleCount;

	/** Članska varijabla koja predstavlja jedinstveni naziv ovog primjerka */
	private String name;

	/** Članska varijabla koja predstavlja središte kruga */
	protected Point center;

	/** Članska varijabla koja predstavlja radijus kruga */
	protected int radius;

	/** Članska varijabla koja predstavlja boju oboda kruga */
	protected Color fgColor;

	/**
	 * Konstruktor koji inicijalizira primjerak ovog razreda. Unutar
	 * konstruktora interno se pohranjuju svi predani parametri
	 *
	 * @param center
	 *            središte kruga
	 * @param radius
	 *            radijus kruga
	 * @param fgColor
	 *            boja oboda kruga
	 */
	public Circle(Point center, int radius, Color fgColor) {
		this.name = NAME + (++circleCount);
		this.center = center;
		this.fgColor = fgColor;
		this.radius = radius;
	}

	/**
	 * Konstruktor koji inicijalizira primjerak ovog razreda. Ovaj konstruktor
	 * na temelju točaka <b>center</b> i <b>end</b> poziva metodu
	 * {@link #calculateRadius(Point, Point)} te ostatak inicijalizacije
	 * delegira konstruktoru {@link #Circle(Point, int, Color)}
	 * 
	 *
	 * @param center
	 *            središte kruga
	 * @param end
	 *            točka na obodu kruga
	 * @param fgColor
	 *            boja oboda kruga
	 */
	public Circle(Point center, Point end, Color fgColor) {
		this(center, calculateRadius(center, end), fgColor);
	}

	/**
	 * Metoda koja se koristi za izračun radijus po Pitagorinom poučku, na
	 * temelju dva primjerka razreda {@link Point} <b>start</b> i <b>end</b>.
	 *
	 * @param center
	 *            središte kruga
	 * @param end
	 *            točka na obudu kruga
	 * @return radijus kruga
	 */
	public static int calculateRadius(Point center, Point end) {
		return (int) Math.sqrt(Math.pow(center.x - end.x, 2) + Math.pow(center.y - end.y, 2));
	}

	@Override
	public void paint(Graphics2D g2) {
		Color fgSave = g2.getColor();

		g2.setColor(fgColor);
		Rectangle boundingBox = getBoundingBox();
		g2.drawOval(boundingBox.x, boundingBox.y, boundingBox.width, boundingBox.height);

		g2.setColor(fgSave);
	}

	@Override
	public Rectangle getBoundingBox() {
		return new Rectangle(center.x - radius, center.y - radius, 2 * radius, 2 * radius);
	}

	@Override
	public void changeSecondCoordinate(Point newCoordinate) {
		this.radius = calculateRadius(center, newCoordinate);
	}

	@Override
	public JPanel createChangePanel() {
		return new CircleChangePanel();
	}

	@Override
	public boolean change(JPanel panel) {
		CircleChangePanel cp = null;
		try {
			cp = (CircleChangePanel) panel;
		} catch (ClassCastException e) {
			return false;
		}

		return changeCircle(cp);
	}

	/**
	 * Metoda koja na temelju predanog primjerka razreda
	 * {@link CircleChangePanel} mijenja članske varijable {@link #center},
	 * {@link #radius} i {@link #fgColor}.
	 *
	 * @param cp
	 *            primjerak razreda {@link CircleChangePanel} na temelju kojeg
	 *            se mijenja ovaj primjerak
	 * @return <code>true</code> ukoliko se promjena uspješno obavila,
	 *         <code>false</code> inače
	 */
	protected boolean changeCircle(CircleChangePanel cp) {
		try {
			center = new Point(Integer.parseInt(cp.centerX.getText()), Integer.parseInt(cp.centerY.getText()));
			radius = Integer.parseInt(cp.radiusField.getText());
		} catch (NumberFormatException nfe) {
			return false;
		}
		fgColor = cp.fgColorPicker.getCurrentColor();

		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Circle other = (Circle) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return name;
	}

	@Override
	public String toJVD() {
		return String.format("%s %d %d %d %d %d %d", JVD_REPRESENTATION, center.x, center.y, radius, fgColor.getRed(),
				fgColor.getGreen(), fgColor.getBlue());
	}

	/**
	 * Statička metoda tvorinica koja se koristi za kreiranje primjerka ovog
	 * razreda na temelju jednog redka .jvd datoteke.
	 *
	 * @param line
	 *            redak .jvd datoteke
	 * @return novi primjerak ovog razreda, nastao parsiranjem retka <b>line</b>
	 */
	public static Circle createFromJVDLine(String line) {
		String[] splitted = line.split(" ");
		if (splitted.length != 7 || !splitted[0].equals(JVD_REPRESENTATION)) {
			return null;
		}

		try {
			Point center = new Point(Integer.parseInt(splitted[1]), Integer.parseInt(splitted[2]));
			int radius = Integer.parseInt(splitted[3]);
			Color fgColor = new Color(Integer.parseInt(splitted[4]), Integer.parseInt(splitted[5]),
					Integer.parseInt(splitted[6]));

			return new Circle(center, radius, fgColor);
		} catch (NumberFormatException e) {
		}

		return null;
	}

	/**
	 * Pomoćni razred koji nasljeđuje {@link JPanel}. Primjerci ovog razreda
	 * koriste se za izmjenu primjeraka razreda {@link Circle}. Unutar ovog
	 * razreda postoji nekoliko primjeraka razreda {@link JTextArea} i
	 * {@link JColorArea} kako bi se podaci mogli upisati te naknadno izvaditi i
	 * parsirati.
	 * 
	 * @see Circle
	 * @see JPanel
	 * 
	 * @author Davor Češljaš
	 */
	protected class CircleChangePanel extends JPanel {

		/**
		 * Konstanta koja se koristi prilikom serijalizacije objekata ovog
		 * razreda
		 */
		private static final long serialVersionUID = 1L;

		/**
		 * Članska varijabla koja predstavlja primjerak razreda
		 * {@link JTextField} koji sadrži x-koordinatu točke
		 * {@link Circle#center} (trenutnu ili promijenjenu)
		 */
		protected JTextField centerX;

		/**
		 * Članska varijabla koja predstavlja primjerak razreda
		 * {@link JTextField} koji sadrži y-koordinatu točke
		 * {@link Circle#center} (trenutnu ili promijenjenu)
		 */
		protected JTextField centerY;

		/**
		 * Članska varijabla koja predstavlja primjerak razreda
		 * {@link JTextField} koji sadrži duljinu radijusa kruga (trenutni ili
		 * promijenjeni)
		 */
		protected JTextField radiusField;

		/**
		 * Članska varijabla koja predstavlja primjerak razreda
		 * {@link JColorArea} koji sadrži boju oboda {@link Circle#fgColor} (trenutnu ili
		 * promijenjenu)
		 */
		protected JColorArea fgColorPicker;

		/**
		 * Konstruktor koji inicijalizira primjerak ovog razreda. Unutar
		 * konstruktora namješta se grafičko sučelje ovog objekta
		 */
		public CircleChangePanel() {
			add(new JLabel("Center X: "));
			centerX = new JTextField(String.valueOf(center.x));
			add(centerX);
			add(new JLabel("Center Y: "));
			centerY = new JTextField(String.valueOf(center.y));
			add(centerY);

			add(new JLabel("Radius: "));
			radiusField = new JTextField(String.valueOf(radius));
			add(radiusField);

			add(new JLabel("Choose foreground color: "));
			fgColorPicker = new JColorArea(fgColor);
			add(fgColorPicker);
		}
	}
}

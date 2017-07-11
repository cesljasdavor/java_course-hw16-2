package hr.fer.zemris.java.hw16.jvdraw.graphicalObject;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;

import javax.swing.JLabel;
import javax.swing.JPanel;

import hr.fer.zemris.java.hw16.jvdraw.colorArea.JColorArea;

/**
 * Razred koji nasljeđuje razred {@link Circle}. Primjerci ovog razreda
 * predstavlju jedan ispunjeni krug, odnosno krug koji nije predstavljen samo
 * obodom. Uz članske varijable iz razreda {@link Circle}, primjerci ovog
 * razreda sadrže još i boju ispune <b>bgColor</b> *
 * <p>
 * Kreiranje ovog primjerka razreda može se ostvariti pozivom jednog od
 * konstruktora: {@link #FilledCircle(Point, int, Color, Color)},
 * {@link #FilledCircle(Point, Point, Color, Color)} ili pozivom metode tvornice
 * {@link #createFromJVDLine(String)}, koja prima jedan redak iz .jvd datoteke
 * </p>
 * 
 * @see Circle
 * 
 * @author Davor Češljaš
 */
public class FilledCircle extends Circle {

	/** Konstanta koja predstavlja naziv koji se veže uz ovaj razred */
	public static final String NAME = "Filled circle";

	/**
	 * Konstanta koja predstavlja početak redka unutar .jvd datoteke u kojem se
	 * nalaze podaci za ispunjeni krug
	 */
	public static final String JVD_REPRESENTATION = "FCIRCLE";

	/** Članska varijabla koja predstavlja boju ispune kruga */
	protected Color bgColor;

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
	 * @param bgColor
	 *            boja ispune kruga
	 */
	public FilledCircle(Point center, int radius, Color fgColor, Color bgColor) {
		super(center, radius, fgColor);
		this.bgColor = bgColor;
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
	 * @param bgColor
	 *            boja ispune kruga
	 */
	public FilledCircle(Point center, Point end, Color fgColor, Color bgColor) {
		this(center, Circle.calculateRadius(center, end), fgColor, bgColor);
	}

	@Override
	public void paint(Graphics2D g2) {
		Color bgSave = g2.getBackground();

		g2.setColor(bgColor);
		Rectangle boundingBox = getBoundingBox();
		g2.fillOval(boundingBox.x, boundingBox.y, boundingBox.width, boundingBox.height);
		super.paint(g2);

		g2.setBackground(bgSave);
	}

	@Override
	public JPanel createChangePanel() {
		return new FilledCircleChangePanel();
	}

	@Override
	public boolean change(JPanel panel) {
		FilledCircleChangePanel fcp = null;
		try {
			fcp = (FilledCircleChangePanel) panel;
		} catch (ClassCastException e) {
			return false;
		}

		return changeFilledCircle(fcp);
	}

	/**
	 * Metoda koja na temelju predanog primjerka razreda
	 * {@link FilledCircleChangePanel} mijenja članske varijable
	 * {@link #center}, {@link #radius}, {@link #fgColor} te {@link #bgColor}.
	 *
	 * @param cp
	 *            primjerak razreda {@link FilledCircleChangePanel} na temelju
	 *            kojeg se mijenja ovaj primjerak
	 * @return <code>true</code> ukoliko se promjena uspješno obavila,
	 *         <code>false</code> inače
	 */
	private boolean changeFilledCircle(FilledCircleChangePanel fcp) {
		if (!changeCircle(fcp)) {
			return false;
		}

		bgColor = fcp.bgColorPicker.getCurrentColor();
		return true;
	}

	@Override
	public String toJVD() {
		return String.format("F" + super.toJVD() + " %d %d %d", bgColor.getRed(), bgColor.getGreen(),
				bgColor.getBlue());
	}

	/**
	 * Statička metoda tvorinica koja se koristi za kreiranje primjerka ovog
	 * razreda na temelju jednog redka .jvd datoteke.
	 *
	 * @param line
	 *            redak .jvd datoteke
	 * @return novi primjerak ovog razreda, nastao parsiranjem retka <b>line</b>
	 */
	public static FilledCircle createFromJVDLine(String line) {
		String[] splitted = line.split(" ");
		if (splitted.length != 10 || !splitted[0].equals(JVD_REPRESENTATION)) {
			return null;
		}

		try {
			Point center = new Point(Integer.parseInt(splitted[1]), Integer.parseInt(splitted[2]));
			int radius = Integer.parseInt(splitted[3]);
			Color fgColor = new Color(Integer.parseInt(splitted[4]), Integer.parseInt(splitted[5]),
					Integer.parseInt(splitted[6]));
			Color bgColor = new Color(Integer.parseInt(splitted[7]), Integer.parseInt(splitted[8]),
					Integer.parseInt(splitted[9]));

			return new FilledCircle(center, radius, fgColor, bgColor);
		} catch (NumberFormatException e) {
		}

		return null;
	}

	/**
	 * Pomoćni razred koji nasljeđuje {@link CircleChangePanel}. Primjerci ovog
	 * razreda koriste se za izmjenu primjeraka razreda {@link FilledCircle}.
	 * Unutar ovog razreda dodatno postoji samo jedan primjerak razreda
	 * {@link JColorArea}, kako bi se omogučila i izmjena boje ispune.
	 * 
	 * @see FilledCircle
	 * @see JPanel
	 * 
	 * @author Davor Češljaš
	 */
	protected class FilledCircleChangePanel extends CircleChangePanel {

		/**
		 * Konstanta koja se koristi prilikom serijalizacije objekata ovog
		 * razreda
		 */
		private static final long serialVersionUID = 1L;

		/**
		 * Članska varijabla koja predstavlja primjerak razreda
		 * {@link JColorArea} koji sadrži boju ispune {@link Circle#bgColor} (trenutnu ili
		 * promijenjenu)
		 */
		protected JColorArea bgColorPicker;

		/**
		 * Konstruktor koji inicijalizira primjerak ovog razreda. Unutar
		 * konstruktora namješta se grafičko sučelje ovog objekta
		 */
		public FilledCircleChangePanel() {
			super();

			add(new JLabel("Choose background color: "));
			bgColorPicker = new JColorArea(bgColor);
			add(bgColorPicker);
		}
	}
}

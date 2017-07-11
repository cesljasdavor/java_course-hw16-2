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
 * Razred koji implementira sučelje {@link GeometricalObject}. Primjerci ovog razreda 
 * predstavlju jednu liniju. Svakoj liniji definirane su točke <b>start</b> i
 * <b>end</b> te boja <b>color</b> kojom je iscrtana ova linija.
 * <p>
 * Kreiranje ovog primjerka razreda može se ostvariti pozivom konstruktora
 * {@link #Line(Point, Point, Color)} ili pozivom metode tvornice
 * {@link #createFromJVDLine(String)}, koja prima jedan redak iz .jvd datoteke
 * </p>
 * 
 * @see GeometricalObject
 * 
 * @author Davor Češljaš
 */
public class Line implements GeometricalObject {

	/** Konstanta koja predstavlja naziv koji se veže uz ovaj razred */
	public static final String NAME = "Line";

	/**
	 * Konstanta koja predstavlja početak redka unutar .jvd datoteke u kojem se
	 * nalaze podaci za liniji
	 */
	public static final String JVD_REPRESENTATION = "LINE";

	/** Statički brojač, koji definira koliko je linija do sada konstruirano */
	private static int lineCount;

	/** Članska varijabla koja predstavlja jedinstveni naziv ovog primjerka */
	private String name;

	/** Članska varijabla koja predstavlja početnu točku linije */
	protected Point start;

	/** Članska varijabla koja predstavlja završnu točku linije */
	protected Point end;

	/**
	 * Članska varijabla koja predstavlja boju kojom se linija treba iscrtati
	 */
	protected Color color;

	/**
	 * Konstruktor koji inicijalizira primjerak ovog razreda. Unutar ovog
	 * konstruktora svi predani parametri interno se pohranjuju te se definira
	 * jedinstveno ime ovog primjerka.
	 *
	 * @param start
	 *            početna točka linije
	 * @param end
	 *            završna točka linije
	 * @param color
	 *            boja kojom se linija treba iscrtati
	 */
	public Line(Point start, Point end, Color color) {
		this.start = start;
		this.end = end;
		this.color = color;
		this.name = NAME + (++lineCount);
	}

	@Override
	public void paint(Graphics2D g2) {
		Color currentColor = g2.getColor();

		g2.setColor(color);
		g2.drawLine(start.x, start.y, end.x, end.y);

		g2.setColor(currentColor);
	}

	@Override
	public Rectangle getBoundingBox() {
		int minX = Math.min(start.x, end.x);
		int minY = Math.min(start.y, end.y);
		int maxX = Math.max(start.x, end.x);
		int maxY = Math.max(start.y, end.y);

		return new Rectangle(minX, minY, maxX - minX, maxY - minY);
	}

	@Override
	public void changeSecondCoordinate(Point newCoordinate) {
		end = newCoordinate;
	}

	@Override
	public JPanel createChangePanel() {
		return new LineChangePanel();
	}

	@Override
	public boolean change(JPanel panel) {
		LineChangePanel lcp = null;
		try {
			lcp = (LineChangePanel) panel;
		} catch (ClassCastException e) {
			return false;
		}

		return changeLine(lcp);
	}

	/**
	 * Metoda koja na temelju predanog primjerka razreda {@link LineChangePanel}
	 * mijenja članske varijable {@link #start}, {@link #end} i {@link #color}.
	 *
	 * @param lcp
	 *            primjerak razreda {@link LineChangePanel} na temelju kojeg se
	 *            mijenja ovaj primjerak
	 * @return <code>true</code> ukoliko se promjena uspješno obavila,
	 *         <code>false</code> inače
	 */
	private boolean changeLine(LineChangePanel lcp) {
		try {
			start = new Point(Integer.parseInt(lcp.startX.getText()), Integer.parseInt(lcp.startY.getText()));
			end = new Point(Integer.parseInt(lcp.endX.getText()), Integer.parseInt(lcp.endY.getText()));
		} catch (NumberFormatException nfe) {
			return false;
		}

		color = lcp.colorPicker.getCurrentColor();
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
		Line other = (Line) obj;
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

	/**
	 * Oblik: LINE x0 y0 x1 y1 red green blue
	 */
	@Override
	public String toJVD() {
		return String.format("%s %d %d %d %d %d %d %d", JVD_REPRESENTATION, start.x, start.y, end.x, end.y,
				color.getRed(), color.getGreen(), color.getBlue());
	}

	/**
	 * Statička metoda tvorinica koja se koristi za kreiranje primjerka ovog
	 * razreda na temelju jednog redka .jvd datoteke.
	 *
	 * @param line
	 *            redak .jvd datoteke
	 * @return novi primjerak ovog razreda, nastao parsiranjem retka <b>line</b>
	 */
	public static Line createFromJVDLine(String line) {
		String[] splitted = line.split(" ");
		if (splitted.length != 8 || !splitted[0].equals(JVD_REPRESENTATION)) {
			return null;
		}

		try {
			Point start = new Point(Integer.parseInt(splitted[1]), Integer.parseInt(splitted[2]));
			Point end = new Point(Integer.parseInt(splitted[3]), Integer.parseInt(splitted[4]));
			Color color = new Color(Integer.parseInt(splitted[5]), Integer.parseInt(splitted[6]),
					Integer.parseInt(splitted[7]));

			return new Line(start, end, color);
		} catch (NumberFormatException e) {
		}

		return null;
	}

	/**
	 * Pomoćni razred koji nasljeđuje {@link JPanel}. Primjerci ovog razreda
	 * koriste se za izmjenu primjeraka razreda {@link Line}. Unutar ovog
	 * razreda postoji nekoliko primjeraka razreda {@link JTextArea} i
	 * {@link JColorArea} kako bi se podaci mogli upisati te naknadno izvaditi i
	 * parsirati.
	 * 
	 * @see Line
	 * @see JPanel
	 * 
	 * @author Davor Češljaš
	 */
	private class LineChangePanel extends JPanel {

		/**
		 * Konstanta koja se koristi prilikom serijalizacije objekata ovog
		 * razreda
		 */
		private static final long serialVersionUID = 1L;

		/**
		 * Članska varijabla koja predstavlja primjerak razreda
		 * {@link JTextField} koji sadrži x-koordinatu točke {@link Line#start}
		 * (trenutnu ili promijenjenu)
		 */
		private JTextField startX;

		/**
		 * Članska varijabla koja predstavlja primjerak razreda
		 * {@link JTextField} koji sadrži y-koordinatu točke {@link Line#start}
		 * (trenutnu ili promijenjenu)
		 */
		private JTextField startY;

		/**
		 * Članska varijabla koja predstavlja primjerak razreda
		 * {@link JTextField} koji sadrži x-koordinatu točke {@link Line#end}
		 * (trenutnu ili promijenjenu)
		 */
		private JTextField endX;

		/**
		 * Članska varijabla koja predstavlja primjerak razreda
		 * {@link JTextField} koji sadrži y-koordinatu točke {@link Line#end}
		 * (trenutnu ili promijenjenu)
		 */
		private JTextField endY;

		/**
		 * Članska varijabla koja predstavlja primjerak razreda
		 * {@link JColorArea} koji sadrži boju {@link Line#color} (trenutnu ili
		 * promijenjenu)
		 */
		private JColorArea colorPicker;

		/**
		 * Konstruktor koji inicijalizira primjerak ovog razreda. Unutar
		 * konstruktora namješta se grafičko sučelje ovog objekta
		 */
		public LineChangePanel() {
			add(new JLabel("Start X: "));
			startX = new JTextField(String.valueOf(start.x));
			add(startX);
			add(new JLabel("Start Y: "));
			startY = new JTextField(String.valueOf(start.y));
			add(startY);

			add(new JLabel("End X: "));
			endX = new JTextField(String.valueOf(end.x));
			add(endX);
			add(new JLabel("End Y: "));
			endY = new JTextField(String.valueOf(end.y));
			add(endY);

			add(new JLabel("Choose color: "));
			colorPicker = new JColorArea(color);
			add(colorPicker);
		}
	}
}

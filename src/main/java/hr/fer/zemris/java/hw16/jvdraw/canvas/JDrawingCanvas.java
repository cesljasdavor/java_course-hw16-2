package hr.fer.zemris.java.hw16.jvdraw.canvas;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

import javax.swing.ButtonGroup;
import javax.swing.ButtonModel;
import javax.swing.JComponent;

import hr.fer.zemris.java.hw16.jvdraw.colorArea.IColorProvider;
import hr.fer.zemris.java.hw16.jvdraw.colorArea.JColorArea;
import hr.fer.zemris.java.hw16.jvdraw.graphicalObject.Circle;
import hr.fer.zemris.java.hw16.jvdraw.graphicalObject.FilledCircle;
import hr.fer.zemris.java.hw16.jvdraw.graphicalObject.GeometricalObject;
import hr.fer.zemris.java.hw16.jvdraw.graphicalObject.Line;
import hr.fer.zemris.java.hw16.jvdraw.model.DrawingModel;
import hr.fer.zemris.java.hw16.jvdraw.model.DrawingModelListener;

/**
 * Razred koji nasljeđuje razred {@link JComponent}. Primjerci ovog razreda
 * predstavljaju platno po kojem se može crtati. Uvjet da se neki objekt može
 * iscrtati koristeći ovaj razred jest da on implementira sučelje
 * {@link GeometricalObject}. Za dohvat prednje i stražnje boje iscrtavanja
 * koriste se dva primjerka razreda {@link JColorArea}.
 * 
 * <p>
 * Dodatno, ovaj razred je i konkretni promatrača unutar oblikovnog obrasca
 * <a href ="https://en.wikipedia.org/wiki/Visitor_pattern">promatrač</a> nad
 * razredom koji implementira sučelje {@link DrawingModel}.
 * </p>
 * 
 * @see JComponent
 * @see DrawingModelListener
 * @see DrawingModel
 * 
 * @author Davor Češljaš
 */
public class JDrawingCanvas extends JComponent implements DrawingModelListener {

	/**
	 * Konstanta koja se koristi prilikom serijalizacije objekata ovog razreda
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Članska varijabla koja predstavlja primjerak razreda koji implementira
	 * sučelje {@link DrawingModel}, a koji se koristi za dohvat i spremanje
	 * podataka koji se iscrtavaju
	 */
	private DrawingModel model;

	/**
	 * Članska varijabla koja predstavlja primjerak razreda {@link ButtonGroup}
	 * preko kojeg se ustvrđuje koju implementaciju sučelja
	 * {@link GeometricalObject} je potrebno nacrtati
	 */
	private ButtonGroup buttonGroup;

	/**
	 * Članska varijabla koja predstavlja implementaciju sučelja
	 * {@link IColorProvider} koja se koristi za dohvat prednje boje objekta
	 * kojeg se iscrtava
	 */
	private IColorProvider fgColorProvider;

	/**
	 * Članska varijabla koja predstavlja implementaciju sučelja
	 * {@link IColorProvider} koja se koristi za dohvat stražnje boje objekta
	 * kojeg se iscrtava
	 */
	private IColorProvider bgColorProvider;

	/**
	 * Članska varijabla koja predstvalja primjerak razreda koji implementira
	 * sučelje {@link GeometricalObject}, a koji se trenutno iscrtava
	 */
	private GeometricalObject currentlyDrawn;

	/**
	 * Konstruktor koji inicijalizira primjerak ovog razreda. Unutar ovog
	 * konstruktora reference na sve predane parametre se interno pohranjuju te
	 * se obavlja registracija ovog promatrača nad predanim primjerkom razreda
	 * koji implementira sučelje {@link DrawingModel} <b>model</b>
	 *
	 * @param model
	 *            primjerak razreda koji implementira sučelje
	 *            {@link DrawingModel}, a koji se koristi za dohvat i spremanje
	 *            podataka koji se iscrtavaju
	 * @param buttonGroup
	 *            primjerak razreda {@link ButtonGroup} preko kojeg se ustvrđuje
	 *            koju implementaciju sučelja {@link GeometricalObject} je
	 *            potrebno nacrtati
	 * @param fgColorProvider
	 *            implementacija sučelja {@link IColorProvider} koja se koristi
	 *            za dohvat prednje boje objekta kojeg se iscrtava
	 * @param bgColorProvider
	 *            implementaciju sučelja {@link IColorProvider} koja se koristi
	 *            za dohvat stražnje boje objekta kojeg se iscrtava
	 */
	public JDrawingCanvas(DrawingModel model, ButtonGroup buttonGroup, IColorProvider fgColorProvider,
			IColorProvider bgColorProvider) {
		setBackground(Color.WHITE);

		this.model = model;
		this.buttonGroup = buttonGroup;
		this.fgColorProvider = fgColorProvider;
		this.bgColorProvider = bgColorProvider;

		this.model.addDrawingModelListener(this);

		addMouseListener(new MouseAdapter() {
			private boolean first = true;

			@Override
			public void mouseClicked(MouseEvent e) {
				if (first) {
					if (createGeometricalObject(e)) {
						first = false;
					}
				} else {
					model.add(currentlyDrawn);
					currentlyDrawn = null;
					first = true;
				}
			}
		});

		addMouseMotionListener(new MouseMotionListener() {

			@Override
			public void mouseMoved(MouseEvent e) {
				changeGeometricalObject(e);
			}

			@Override
			public void mouseDragged(MouseEvent e) {
				changeGeometricalObject(e);
			}

			private void changeGeometricalObject(MouseEvent e) {
				if (currentlyDrawn == null) {
					return;
				}

				currentlyDrawn.changeSecondCoordinate(e.getPoint());
				repaint();
			}
		});
	}

	/**
	 * Pomoćna metoda koja koristeći člansku varijablu {@link #buttonGroup} i
	 * predani primjerak razreda {@link MouseEvent} <b>e</b>, ustvrđuje koju
	 * konkretnu implementaciju sučelja {@link GeometricalObject} je potrebno
	 * stvoriti. Nakon što je ustvrđeno primjerak kojeg razreda je potrebno
	 * stvoriti, taj se primjerak i stvara te se on pohranjuje kao članska
	 * varijabla {@link #currentlyDrawn}.
	 *
	 * @param e
	 *            primjerak razreda {@link MouseEvent} koji se koristi prilikom
	 *            stvranja novog primjerka razreda koji implementira sučelje
	 *            {@link GeometricalObject}
	 * @return <code>true</code> ukoliko je postupak stvaranja novog primjerka
	 *         razreda koji implementira sučelje {@link GeometricalObject}
	 *         uspio, <code>false</code> inače
	 */
	private boolean createGeometricalObject(MouseEvent e) {
		ButtonModel bModel = buttonGroup.getSelection();
		if (bModel == null) {
			return false;
		}

		switch (bModel.getActionCommand()) {
		case Line.NAME:
			currentlyDrawn = new Line(e.getPoint(), e.getPoint(), fgColorProvider.getCurrentColor());
			break;
		case Circle.NAME:
			currentlyDrawn = new Circle(e.getPoint(), e.getPoint(), fgColorProvider.getCurrentColor());
			break;
		case FilledCircle.NAME:
			currentlyDrawn = new FilledCircle(e.getPoint(), e.getPoint(), fgColorProvider.getCurrentColor(),
					bgColorProvider.getCurrentColor());
			break;
		}

		return true;
	}

	@Override
	protected void paintComponent(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;

		clearCanvas(g2);

		for (int i = 0, len = model.getSize(); i < len; i++) {
			GeometricalObject geometricalObject = model.getObject(i);

			geometricalObject.paint(g2);
		}

		if (currentlyDrawn != null) {
			currentlyDrawn.paint(g2);
		}
	}

	/**
	 * Pomoćna metoda koja koristeći primjerak razreda {@link Graphics2D} koji
	 * joj je predan kao parametar <b>g2</b> čisti površinu ove komponente
	 *
	 * @param g2
	 *            primjerak razreda {@link Graphics2D} koji se koristi za
	 *            čišćenje platna
	 */
	private void clearCanvas(Graphics2D g2) {
		Color saveBg = g2.getBackground();

		g2.setBackground(getBackground());
		Insets ins = getInsets();
		Dimension dim = getSize();
		g2.clearRect(ins.left, ins.top, dim.width - ins.left - ins.right, dim.height - ins.top - ins.bottom);

		g2.setBackground(saveBg);
	}

	@Override
	public void objectsRemoved(DrawingModel source, int index0, int index1) {
		repaint();
	}

	@Override
	public void objectsChanged(DrawingModel source, int index0, int index1) {
		repaint();
	}

	@Override
	public void objectsAdded(DrawingModel source, int index0, int index1) {
		repaint();
	}
}

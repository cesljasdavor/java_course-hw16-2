package hr.fer.zemris.java.hw16.jvdraw.model;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;
import java.util.function.Consumer;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import hr.fer.zemris.java.hw16.jvdraw.canvas.JDrawingCanvas;
import hr.fer.zemris.java.hw16.jvdraw.graphicalObject.Circle;
import hr.fer.zemris.java.hw16.jvdraw.graphicalObject.FilledCircle;
import hr.fer.zemris.java.hw16.jvdraw.graphicalObject.GeometricalObject;
import hr.fer.zemris.java.hw16.jvdraw.graphicalObject.Line;

/**
 * Razred koji implementira sučelje {@link DrawingModel}. Razred predstavlja
 * konkretan subjekt unutar oblikovnog obrsca
 * <a href ="https://en.wikipedia.org/wiki/Visitor_pattern">promatrač</a>. Uz
 * metode sučelja {@link DrawingModel}, razred implementira i nekolicinu
 * dodatnih metoda:
 * <ul>
 * <li>{@link #remove(GeometricalObject)}</li>
 * <li>{@link #change(GeometricalObject, JPanel)}</li>
 * <li>{@link #isModified()}</li>
 * <li>{@link #setModified(boolean)}</li>
 * <li>{@link #clear()}</li>
 * <li>{@link #addAll(List)}</li>
 * <li>{@link #loadFromJVD(Path)}</li>
 * <li>{@link #exportAsImage(File, String, JDrawingCanvas)}</li>
 * </ul>
 * 
 * @see DrawingModel
 * 
 * @author Davor Češljaš
 */
public class JVDrawingModel implements DrawingModel {

	/**
	 * Člasnka varijabla koja predstavlja {@link List} svih primjeraka razreda
	 * koji implementiraju sučelje {@link GeometricalObject}, a koji su
	 * pohranjeni unutar ovog modela
	 */
	private List<GeometricalObject> geometricalObjects;

	/**
	 * Članska varijabla koja predstavlja {@link List} svih promatrača
	 * modeliranih sučeljem {@link DrawingModelListener} koji slušaju na
	 * promjene unutar modela
	 */
	private List<DrawingModelListener> listeners;

	/**
	 * Članska varijabla koja predstavlja zastavicu koja ukazuje na to je li
	 * model mijenjan
	 */
	private boolean modified;

	/**
	 * Konstruktor koji inicijalizira primjerak ovog razreda. Unutar
	 * konstruktora inicijaliziraju se interne strukture koje primjerak ovog
	 * razreda sadrži.
	 */
	public JVDrawingModel() {
		this.geometricalObjects = new ArrayList<>();
		this.listeners = new ArrayList<>();
	}

	@Override
	public int getSize() {
		return geometricalObjects.size();
	}

	@Override
	public GeometricalObject getObject(int index) {
		try {
			return geometricalObjects.get(index);
		} catch (IndexOutOfBoundsException e) {
		}

		return null;
	}

	@Override
	public void add(GeometricalObject object) {
		geometricalObjects.add(object);
		int index = geometricalObjects.size() - 1;
		fireObjectsAdded(index, index);
	}

	/**
	 * Metoda koja se koristi za brisanje primjerka razreda koji implementira
	 * sučelje {@link GeometricalObject} predanog kao parametar <b>object</b> iz
	 * internog spremišta. O ovoj promjeni biti će obaviješteni svi promatrači
	 *
	 * @param object
	 *            primjerak razreda koji implementira sučelje
	 *            {@link GeometricalObject}, koji se briše iz internog
	 *            spremišta.
	 */
	public void remove(GeometricalObject object) {
		int index = geometricalObjects.indexOf(object);
		if (index == -1) {
			return;
		}

		geometricalObjects.remove(object);
		fireObjectsRemoved(index, index);
	}

	/**
	 * Metoda koja se koristi za izmjenu primjerka razreda koji implementira
	 * sučelje {@link GeometricalObject} predanog kao parametar <b>object</b>.
	 * Izmjena se ostvarju na temelju predanog primjerka razreda {@link JPanel},
	 * koristeći metodu {@link GeometricalObject#change(JPanel)}.O ovoj promjeni
	 * biti će obaviješteni svi promatrači
	 *
	 * @param object
	 *            primjerak razreda koji implementira sučelje
	 *            {@link GeometricalObject}, nad kojim se vrši promjena
	 * @param panel
	 *            primjerak razreda {@link JPanel} na temelju kojeg se vrši
	 *            promjena
	 */
	public void change(GeometricalObject object, JPanel panel) {
		int index = geometricalObjects.indexOf(object);
		if (index == -1) {
			return;
		}

		if (!object.change(panel)) {
			return;
		}

		fireObjectsChanged(index, index);
	}

	/**
	 * Metoda koja ukazuje na to je li model mijenjan ili ne
	 *
	 * @return <code>true</code> ukoliko je model mijenjan, <code>false</code>
	 *         inače
	 */
	public boolean isModified() {
		return modified;
	}

	/**
	 * Metoda koja postavlja status modela na <b>modified</b>
	 *
	 * @param modified
	 *            status na koji se model postavlja
	 */
	public void setModified(boolean modified) {
		this.modified = modified;
	}

	@Override
	public void addDrawingModelListener(DrawingModelListener l) {
		listeners = new ArrayList<>(listeners);
		listeners.add(l);
	}

	@Override
	public void removeDrawingModelListener(DrawingModelListener l) {
		listeners = new ArrayList<>(listeners);
		listeners.remove(l);
	}

	/**
	 * Metoda koja se koristi za brisanje svih primjeraka razreda koji
	 * implementiraju sučelje {@link GeometricalObject} koji su interno
	 * spremljeni. O ovoj promjeni biti će obaviješteni svi promatrači
	 */
	public void clear() {
		int lastIndex = getSize() - 1;
		geometricalObjects.clear();

		modified = true;
		fireObjectsRemoved(0, lastIndex < 0 ? 0 : lastIndex);
	}

	/**
	 * Metoda koja interno sprema sve primjerke razreda koji implementiraju
	 * sučelje {@link GeometricalObject} koji su predani kroz parametar
	 * <b>objects</b>, u istom redoslijedu.O ovoj promjeni biti će obaviješteni
	 * svi promatrači
	 *
	 * @param objects
	 *            {@link List} primjeraka razreda koji implementiraju sučelje
	 *            {@link GeometricalObject} koje je potrebno interno pohraniti
	 */
	public void addAll(List<GeometricalObject> objects) {
		int firstIndex = geometricalObjects.size();

		this.geometricalObjects.addAll(objects);

		modified = true;

		int lastIndex = geometricalObjects.size() - 1;
		fireObjectsAdded(firstIndex, lastIndex < 0 ? 0 : lastIndex);
	}

	/**
	 * Metoda koja se koristi za učitavanje svih primjeraka sučelja
	 * {@link GeometricalObject} koji su spremljeni unutar .jvd datoteke. Metoda
	 * najprije poziva metodu {@link #clear()}, a potom parsira datoteku čija je
	 * putanja predana kao parametar <b>jvdFile</b>. Na kraju metoda koristeći
	 * metodu {@link #addAll(List)} dodaje primjerke sučelja
	 * {@link GeometricalObject} u interno spremište. Važno je naglasiti da će
	 * ovom promjenom zastavica <b>modified</b> biti postavljena na
	 * <code>false</code>
	 *
	 * @param jvdFile
	 *            Putanja do .jvd datoteke koja se parsira
	 * @throws IOException
	 *             Ukoliko nije moguće pročitati datoteku predstavljenu putanjom
	 *             <b>jvdFile</b>
	 */
	public void loadFromJVD(Path jvdFile) throws IOException {
		clear();

		List<GeometricalObject> objects = new ArrayList<>();
		for (String line : Files.readAllLines(jvdFile, StandardCharsets.UTF_8)) {
			GeometricalObject object = createGeometricalObjectFromJVD(line);
			if (object == null) {
				continue;
			}

			objects.add(object);
		}

		addAll(objects);

		modified = false;
	}

	/**
	 * Metoda koja se koristi kako bi se svi primjerci sučelja
	 * {@link GeometricalObject} spremljeni unutar ovog modela iscrtali kao
	 * slika ekstenzije(formata) <b>extension</b>, koristeći predani parametar
	 * <b>canvas</b>. Slika će biti spremljena u datoteku predstavljenu predanim
	 * primjerkom razreda {@link File} <b>imageFile</b>.
	 * <p>
	 * Napomena: Slika neće biti veličine platna, već će se pronaći minimalna
	 * površina koja u cijelosti zahvaća sve površine primjeraka sučelja
	 * {@link GeometricalObject} koji su interno spremljeni
	 * </p>
	 *
	 * @param imageFile
	 *            apstraktna reprezentacija datoteke unutar koje se sprema slika
	 * @param extension
	 *            ekstenzija, odnosno format stvorene slike
	 * @param canvas
	 *            primjerak razreda {@link JDrawingCanvas}, koji se koristi za
	 *            crtanje slike
	 * @throws IOException
	 *             Ukoliko nije moguće pisati ili nije moguće stvoriti datoteku
	 *             predstavljenu parametrom <b>imageFile</b>
	 */
	public void exportAsImage(File imageFile, String extension, JDrawingCanvas canvas) throws IOException {
		Dimension canvasDim = canvas.getSize();

		BufferedImage image = new BufferedImage(canvasDim.width, canvasDim.height, BufferedImage.TYPE_3BYTE_BGR);
		Graphics2D g2 = (Graphics2D) image.getGraphics();

		canvas.paint(g2);
		g2.dispose();

		Rectangle boundingBox = findBoundingBox(canvasDim);
		if (boundingBox != null) {
			image = image.getSubimage(boundingBox.x, boundingBox.y, boundingBox.width, boundingBox.height);
		}

		ImageIO.write(image, extension, imageFile);
	}

	/**
	 * Pomoćna metoda koja pronalazi minimalnu površinu koja u cijelosti zahvaća
	 * sve površine primjeraka sučelja {@link GeometricalObject} koji su interno
	 * spremljeni unutar ovog modela
	 *
	 * @param canvasDim
	 *            veličina platna na kojem je iscrtana slika, a koja se koristi
	 *            kao maksimalna veličina slike
	 * @return primjerak razreda {@link Rectangle} koji predstavlja minimalnu
	 *         površinu koja u cijelosti zahvaća sve površine primjeraka sučelja
	 *         {@link GeometricalObject} koji su interno spremljeni unutar ovog
	 *         modela
	 */
	private Rectangle findBoundingBox(Dimension canvasDim) {
		if (geometricalObjects.isEmpty()) {
			return null;
		}

		int xMin = canvasDim.width;
		int yMin = canvasDim.height;
		int xMax = 0;
		int yMax = 0;

		for (GeometricalObject object : geometricalObjects) {
			Rectangle rect = object.getBoundingBox();
			xMin = Math.min(xMin, rect.x);
			yMin = Math.min(yMin, rect.y);
			xMax = Math.max(xMax, rect.x + rect.width);
			yMax = Math.max(yMax, rect.y + rect.height);
		}

		return new Rectangle(Math.max(0, xMin), Math.max(0, yMin), Math.min(canvasDim.width, xMax - xMin),
				Math.min(canvasDim.height, yMax - yMin));
	}

	/**
	 * Pomoćna metoda koja se koristi za obavještavanje svih registriranih
	 * promatrača, da se u rasponu [<b>index0</b>, <b>index1</b>] dogodilo
	 * umetanje novih objekata
	 *
	 * @param index0
	 *            početna pozicija raspona (uključivo)
	 * @param index1
	 *            završna pozicija raspona (uključivo)
	 */
	private void fireObjectsAdded(int index0, int index1) {
		notifiyListeners(l -> l.objectsAdded(this, index0, index1));
	}

	/**
	 * Pomoćna metoda koja se koristi za obavještavanje svih registriranih
	 * promatrača, da se u rasponu [<b>index0</b>, <b>index1</b>] dogodila
	 * promjena interno spremljenih objekata
	 *
	 * @param index0
	 *            početna pozicija raspona (uključivo)
	 * @param index1
	 *            završna pozicija raspona (uključivo)
	 */
	private void fireObjectsChanged(int index0, int index1) {
		notifiyListeners(l -> l.objectsChanged(this, index0, index1));
	}

	/**
	 * Pomoćna metoda koja se koristi za obavještavanje svih registriranih
	 * promatrača, da se u rasponu [<b>index0</b>, <b>index1</b>] dogodilo
	 * brisanje interno spremljenih objekata
	 *
	 * @param index0
	 *            početna pozicija raspona (uključivo)
	 * @param index1
	 *            završna pozicija raspona (uključivo)
	 */
	private void fireObjectsRemoved(int index0, int index1) {
		notifiyListeners(l -> l.objectsRemoved(this, index0, index1));
	}

	/**
	 * Pomoćna metoda koja iterira po svim registriranim promatračima i nad
	 * svakim poziva metodu {@link Consumer#accept(Object)} predanog primjerka
	 * sučelja <b>consumer</b>. Ova metoda dodatno postavlja zastavicu
	 * <b>modified</b> na vrijednost <code>true</code>
	 *
	 * @param consumer
	 *            strategija koja modelira posao koji se izvodi nad svakim od
	 *            registriranih promatrača
	 */
	private void notifiyListeners(Consumer<DrawingModelListener> consumer) {
		modified = true;
		listeners.forEach(consumer);
	}

	/**
	 * Pomoćna metoda koja se koristi za stvaranje novog primjerka sučelja
	 * {@link GeometricalObject} iz predanog retka <b>line</b> .jvd datoteke.
	 *
	 * @param line
	 *            redak .jvd datoteke
	 * @return stvoreni primjerak sučelja {@link GeometricalObject}
	 */
	private GeometricalObject createGeometricalObjectFromJVD(String line) {
		if (line.startsWith(Line.JVD_REPRESENTATION)) {
			return Line.createFromJVDLine(line);
		} else if (line.startsWith(Circle.JVD_REPRESENTATION)) {
			return Circle.createFromJVDLine(line);
		} else if (line.startsWith(FilledCircle.JVD_REPRESENTATION)) {
			return FilledCircle.createFromJVDLine(line);
		}

		return null;
	}

	@Override
	public String toString() {
		StringJoiner joiner = new StringJoiner("\n");

		for (GeometricalObject obj : geometricalObjects) {
			joiner.add(obj.toJVD());
		}

		return joiner.toString();
	}
}

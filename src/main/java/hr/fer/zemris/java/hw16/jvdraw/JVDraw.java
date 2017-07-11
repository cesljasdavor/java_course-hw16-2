package hr.fer.zemris.java.hw16.jvdraw;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.filechooser.FileNameExtensionFilter;

import hr.fer.zemris.java.hw16.jvdraw.bottomLabel.BottomLabel;
import hr.fer.zemris.java.hw16.jvdraw.canvas.JDrawingCanvas;
import hr.fer.zemris.java.hw16.jvdraw.colorArea.JColorArea;
import hr.fer.zemris.java.hw16.jvdraw.graphicalObject.Circle;
import hr.fer.zemris.java.hw16.jvdraw.graphicalObject.FilledCircle;
import hr.fer.zemris.java.hw16.jvdraw.graphicalObject.GeometricalObject;
import hr.fer.zemris.java.hw16.jvdraw.graphicalObject.Line;
import hr.fer.zemris.java.hw16.jvdraw.model.DrawingObjectListModel;
import hr.fer.zemris.java.hw16.jvdraw.model.JVDrawingModel;

/**
 * Razred koji nasljeđuje razred {@link JFrame}. Razred koji predstavlja program
 * "JVDraw". Ovaj program koristi se za jednostavno vektorsko crtanje. <br>
 * Program nudi izbor crtanja linije(engl. Line), kruga (engl. Circle), te
 * ispunjenog kruga (engl. Filled Circle).<br>
 * <br>
 * Crtanje se izvršava tako da se na početku pritisne, koji geometrijski objekt
 * želi nacrtati, te se potom pritisne na površinu platna (platno modelirano
 * razredom {@link JDrawingCanvas}). Nakon toga pomicanjem miša definira se
 * oblik objekta sve do ponovnog pritiska tipke miša na platno <br>
 * <br>
 * Program nudi i dva načina spremanja dobivene slike. Jedan je u tekstualnom
 * obliku u datoteku sa .jvd ekstenzijom, a drugi je u neki od ponuđenih formata
 * slike (png, jpg, gif). Ukoliko se sadržaj spremi kao JVD datoteka, ona se
 * može ponovno otvoriti, dok to ne vrijedi za sliku. <br>
 * <br>
 * Korisniku se nudi i izbor prednje te stražnje boje pritiskom na jednu od
 * površina koje prikazuju boju (modelirani s {@link JColorArea}).
 * 
 * @see JDrawingCanvas
 * @see JColorArea
 * 
 * @author Davor Češljaš
 */
public class JVDraw extends JFrame {

	/**
	 * Konstanta koja se koristi prilikom serijalizacije objekata ovog razreda
	 */
	private static final long serialVersionUID = 1L;

	/** Konstanta koja predstavlja pretpostavljenu stražnju boju */
	private static final Color DEFAULT_BACKGROUD = Color.WHITE;

	/** Konstanta koja predstavlja pretpostavljenu prednju boju */
	private static final Color DEFAULT_FOREGROUD = Color.BLACK;

	/**
	 * Konstanta koja predstavlja primjerak razreda
	 * {@link FileNameExtensionFilter}, a koji filtrira datoteke koje imaju .jvd
	 * ekstenziju
	 */
	private static final FileNameExtensionFilter JVD_FILTER = new FileNameExtensionFilter("JVD file", "jvd");

	/** Konstanta koja predstavlja ekstenziju JVD datoteke */
	private static final String JVD_EXTENSION = ".jvd";

	/**
	 * Članska varijabla koja predstavlja putanju do trenutno otvorene JVD
	 * datoteke
	 */
	private Path currentFile;

	/**
	 * Članska varijabla koja predstavlja model podataka za komponentu
	 * {@link JDrawingCanvas}
	 */
	private JVDrawingModel model;

	/** Članska varijabla koja predstavlja površinu po kojoj se crta (platno) */
	private JDrawingCanvas canvas;

	/**
	 * Konstruktor koji inicijalizira primjerak ovog razreda. Unutar ovog
	 * konstruktora namješta se grafičko korisničko sučelje programa.
	 */
	public JVDraw() {
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				exit();
			}
		});

		setSize(800, 600);
		setTitle("JVDraw");
		setLocationRelativeTo(null);

		initGUI();
	}

	/**
	 * Pomoćna metoda koja se koristi za postavljanje grafičkog korisničkog
	 * sučelja. Ova metoda ne mijenja sam prozor, već postavlja sadržaj koji se
	 * iscrtava unutar prozora
	 */
	private void initGUI() {
		Container cp = getContentPane();
		cp.setLayout(new BorderLayout());

		JToolBar toolBar = new JToolBar();
		cp.add(toolBar, BorderLayout.NORTH);
		toolBar.setLayout(new FlowLayout(FlowLayout.LEFT));

		JColorArea fgColorArea = new JColorArea(DEFAULT_FOREGROUD);
		JColorArea bgColorArea = new JColorArea(DEFAULT_BACKGROUD);
		toolBar.add(fgColorArea);
		toolBar.add(bgColorArea);

		ButtonGroup buttonGroup = new ButtonGroup();
		createToggleButton(Line.NAME, buttonGroup, toolBar);
		createToggleButton(Circle.NAME, buttonGroup, toolBar);
		createToggleButton(FilledCircle.NAME, buttonGroup, toolBar);

		model = new JVDrawingModel();
		canvas = new JDrawingCanvas(model, buttonGroup, fgColorArea, bgColorArea);
		cp.add(canvas, BorderLayout.CENTER);

		BottomLabel bottomLabel = new BottomLabel(fgColorArea, bgColorArea);
		cp.add(bottomLabel, BorderLayout.SOUTH);

		createList(cp);

		createMenuBar();
	}

	/**
	 * Pomoćna metoda koja se koristi za stvaranje primjerka razreda
	 * {@link JToggleButton} koristeći pri tome parametar <b>name</b>. Nakon što
	 * se primjerak stvori on se dodaje predanim parametrima <b>buttonGroup</b>
	 * i <b>toolBar</b>
	 *
	 * @param name
	 *            naziv koji će poprimiti stvoreni primjerak razreda
	 *            {@link JToggleButton}
	 * @param buttonGroup
	 *            primjerak razreda {@link ButtonGroup} kojem se dodaje stvoreni
	 *            gumb
	 * @param toolBar
	 *            primjerak razreda {@link JToolBar} kojem se dodaje stvoreni
	 *            gumb
	 */
	private void createToggleButton(String name, ButtonGroup buttonGroup, JToolBar toolBar) {
		JToggleButton toggleButton = new JToggleButton(name);
		toggleButton.setActionCommand(name);

		buttonGroup.add(toggleButton);
		toolBar.add(toggleButton);
	}

	/**
	 * Pomoćna metoda koja se koristi za stvaranje modela i stvaranje pogleda
	 * modeliranog razredom {@link JList}, a koja se pojavljuje u desnom dijelu
	 * prozora. Kao model liste ovdje se koristi primjerak razreda
	 * {@link DrawingObjectListModel}
	 *
	 * @param cp
	 *            predstavlja primjerak razreda {@link Container} kojem se
	 *            dodaje stvoreni primjerak razreda {@link JList}
	 */
	private void createList(Container cp) {

		DrawingObjectListModel listModel = new DrawingObjectListModel(model);
		JList<GeometricalObject> list = new JList<>(listModel);

		list.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() != 2) {
					return;
				}

				GeometricalObject selected = list.getSelectedValue();
				JPanel changePanel = selected.createChangePanel();
				int status = JOptionPane.showConfirmDialog(
						JVDraw.this, changePanel, 
						"Changing of element: " + selected,
						JOptionPane.OK_CANCEL_OPTION, 
						JOptionPane.PLAIN_MESSAGE);

				if (status == JOptionPane.OK_OPTION) {
					model.change(selected, changePanel);
				}
			}
		});

		list.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_DELETE) {
					return;
				}

				GeometricalObject geomObj = list.getSelectedValue();

				if (geomObj != null) {
					model.remove(geomObj);
				}
			}

		});

		cp.add(new JScrollPane(list), BorderLayout.EAST);
	}

	/**
	 * Pomoćna metoda koja se koristi za stvaranje primjerka razreda
	 * {@link JMenuBar}, te incijalizaciju njegovog grafičkog korisničkog
	 * sučelja. Ovaj {@link JMenuBar} potom se dodaje ovom prozoru
	 */
	private void createMenuBar() {
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);

		JMenu file = new JMenu("File");
		menuBar.add(file);

		JMenuItem open = new JMenuItem(openAction);
		JMenuItem save = new JMenuItem(saveAction);
		JMenuItem saveAs = new JMenuItem(saveAsAction);
		JMenuItem export = new JMenuItem(exportAction);
		JMenuItem exit = new JMenuItem(exitAction);

		file.add(open);
		file.add(save);
		file.add(saveAs);
		file.addSeparator();
		file.add(export);
		file.addSeparator();
		file.add(exit);
	}

	/**
	 * Privatni primjerak razreda koji nasljeđuje {@link AbstractAction}. Ova
	 * akcija koristi se za otvaranje postojeće JVD datoteke na disku te prikaz
	 * njenog sadržaja na platnu.
	 */
	private final AbstractAction openAction = new AbstractAction() {

		private static final long serialVersionUID = 1L;

		private JFileChooser fc;
		{
			fc = new JFileChooser();
			fc.setDialogTitle("Open");
			fc.setFileFilter(JVD_FILTER);

			putValue(Action.NAME, "Open");
			putValue(Action.SHORT_DESCRIPTION, "Used to open .jvd file");
			putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("control O"));
			putValue(Action.MNEMONIC_KEY, KeyEvent.VK_O);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if (fc.showOpenDialog(JVDraw.this) != JFileChooser.APPROVE_OPTION) {
				JOptionPane.showMessageDialog(
						JVDraw.this, 
						"Unable to open file chooser for saving data",
						"File chooser error", 
						JOptionPane.ERROR_MESSAGE);
				return;
			}

			currentFile = fc.getSelectedFile().toPath();
			try {
				model.loadFromJVD(currentFile);
			} catch (IOException e1) {
				JOptionPane.showMessageDialog(JVDraw.this, 
						"Unable to open file: " + currentFile, 
						"Unable to open file",
						JOptionPane.ERROR_MESSAGE);
			}
		}
	};

	/**
	 * Privatni primjerak razreda koji nasljeđuje {@link AbstractAction}. Ova
	 * akcija koristi se za spremanje JVD dokumenta na njegovu trenutnu lokaciju
	 * na disku ili na specifičnu lokaciju ukoliko dokument nije na disku
	 */
	private final AbstractAction saveAction = new AbstractAction() {

		private static final long serialVersionUID = 1L;

		{
			putValue(Action.NAME, "Save");
			putValue(Action.SHORT_DESCRIPTION, "Used to save .jvd file in the currently opened file");
			putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("control S"));
			putValue(Action.MNEMONIC_KEY, KeyEvent.VK_S);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			save();
		}
	};

	/**
	 * Privatni primjerak razreda koji nasljeđuje {@link AbstractAction}. Ova
	 * akcija koristi se za spremanje JVD dokumenta na specifičnu lokaciju na
	 * disku
	 */
	private final AbstractAction saveAsAction = new AbstractAction() {

		private static final long serialVersionUID = 1L;

		{
			putValue(Action.NAME, "Save As");
			putValue(Action.SHORT_DESCRIPTION,
					"Used to save content of this canvas to the specific location, as .jvd file");
			putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("control shift S"));
			putValue(Action.MNEMONIC_KEY, KeyEvent.VK_A);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			saveAs();
		}
	};

	/**
	 * Privatni primjerak razreda koji nasljeđuje {@link AbstractAction}. Ova
	 * akcija koristi se za spremanje sadržaja platna kao slike jednog od
	 * sljedećih formata:
	 * <ul>
	 * <li>gif</li>
	 * <li>png</li>
	 * <li>jpg</li>
	 * </ul>
	 */
	private final AbstractAction exportAction = new AbstractAction() {

		private static final long serialVersionUID = 1L;

		private Set<FileNameExtensionFilter> filters;
		private JFileChooser fc;

		{
			filters = new HashSet<>();
			filters.add(new FileNameExtensionFilter("PNG image", "png"));
			filters.add(new FileNameExtensionFilter("GIF image", "gif"));
			filters.add(new FileNameExtensionFilter("JPG image", "jpg"));

			fc = new JFileChooser();
			fc.setDialogTitle("Export");
			for (FileNameExtensionFilter filter : filters) {
				fc.setFileFilter(filter);
			}

			putValue(Action.NAME, "Export");
			putValue(Action.SHORT_DESCRIPTION, "Used to export canvas content as trimmed png, jpg or gif image");
			putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("control E"));
			putValue(Action.MNEMONIC_KEY, KeyEvent.VK_E);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if (fc.showSaveDialog(JVDraw.this) != JFileChooser.APPROVE_OPTION) {
				JOptionPane.showMessageDialog(
						JVDraw.this, 
						"Unable to open file chooser for exporting image",
						"File chooser error", 
						JOptionPane.ERROR_MESSAGE);
				return;
			}

			File file = fc.getSelectedFile();
			String extension = findExtension(file);

			if (extension == null) {
				extension = ((FileNameExtensionFilter) fc.getFileFilter()).getExtensions()[0];
				file = new File(file.getAbsolutePath() + "." + extension);
			}

			try {
				model.exportAsImage(file, extension, canvas);
			} catch (IOException e1) {
				JOptionPane.showMessageDialog(
						JVDraw.this, 
						"Unable to export image", 
						"Export error",
						JOptionPane.ERROR_MESSAGE);
			}
		}

		private String findExtension(File file) {
			String fileName = file.getName();
			int dotIndex = fileName.indexOf('.');
			if (dotIndex == -1) {
				return null;
			}

			String extension = fileName.substring(dotIndex + 1);
			for (FileNameExtensionFilter filter : filters) {
				if (extension.equalsIgnoreCase(filter.getExtensions()[0])) {
					return extension;
				}
			}

			return null;
		}
	};

	/**
	 * Privatni primjerak razreda koji nasljeđuje {@link AbstractAction}. Ova
	 * akcija koristi se za zatvaranje aplikacije. Ukoliko su na JVD dokumentu
	 * nastale promjene, a koje nisu spremljene, aplikacija će pitati da li
	 * korisnik želi spremiti promjene
	 */
	private final AbstractAction exitAction = new AbstractAction() {

		private static final long serialVersionUID = 1L;

		{
			putValue(Action.NAME, "Exit");
			putValue(Action.SHORT_DESCRIPTION, "Used to exit application");
			putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("control W"));
			putValue(Action.MNEMONIC_KEY, KeyEvent.VK_X);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			exit();
		}
	};

	/**
	 * Pomoćna metoda koja se koristi za spremanje dokumenta na lokaciju
	 * zapisanu unutar {@link #currentFile} ili na specifičnu lokaciju ukoliko
	 * se dokument ne nalazi u memoriji
	 */
	private void save() {
		if (currentFile != null) {
			writeToFile();
		} else {
			saveAs();
		}
	}

	/**
	 * Pomoćna metoda koja se koristi za stvaranje JVD datoteke na lokaciji koja
	 * je zapisana unutar članske varijable {@link #currentFile} te pisanje
	 * sadržaja platna u tu datoteku
	 */
	private void writeToFile() {
		try {
			Files.write(currentFile, model.toString().getBytes(StandardCharsets.UTF_8));
			model.setModified(false);

			JOptionPane.showMessageDialog(
					this, 
					"File saved successfully", 
					"Info", 
					JOptionPane.INFORMATION_MESSAGE);
		} catch (IOException e) {
			JOptionPane.showMessageDialog(
					this, 
					"Unable to sa documebt to: " + currentFile, 
					"Saving error",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	/**
	 * Pomoćna metoda koja korisnika pita za lokaciju i naziv pod kojim se
	 * sadržaj platna treba spremiti te nakon toga sa tom putanjom poziva metodu
	 * {@link #writeToFile()} s kojom stvara datoteku i u nju upisuje sadržaj
	 * dokumenta
	 */
	private void saveAs() {
		JFileChooser fc = new JFileChooser();
		fc.setDialogTitle("Save As");
		fc.setFileFilter(JVD_FILTER);

		if (fc.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) {
			JOptionPane.showMessageDialog(
					this,
					"Unable to open file chooser for saving data", 
					"File chooser error",
					JOptionPane.ERROR_MESSAGE);
			return;
		}

		currentFile = fc.getSelectedFile().toPath();
		if (!currentFile.toString().endsWith(JVD_EXTENSION)) {
			currentFile = Paths.get(currentFile.toAbsolutePath().toString() + JVD_EXTENSION);
		}

		writeToFile();
	}

	/**
	 * Pomoćna metoda koja zatvara aplikaciju. Ukoliko promjene sadržaja platna
	 * nisu spremljene u memoriju korisnika se pita želi li ih spremiti
	 */
	private void exit() {
		if (model.isModified()) {
			int status = JOptionPane.showConfirmDialog(
					this,
					"You have some unsaved changes. Do you wish to save?", 
					"Unsaved changes",
					JOptionPane.YES_NO_CANCEL_OPTION, 
					JOptionPane.WARNING_MESSAGE);

			if (status == JOptionPane.YES_OPTION) {
				save();
			} else if (status == JOptionPane.CANCEL_OPTION) {
				return;
			}
		}

		dispose();
	}

	/**
	 * Metoda od koje započinje izvođenje programa.
	 *
	 * @param args
	 *            argumenti naredbenog redka. Ovdje se ne koriste
	 */
	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> new JVDraw().setVisible(true));
	}
}

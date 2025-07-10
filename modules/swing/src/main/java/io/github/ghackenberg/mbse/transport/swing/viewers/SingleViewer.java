package io.github.ghackenberg.mbse.transport.swing.viewers;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JToolBar;
import javax.swing.SpinnerNumberModel;

import bibliothek.gui.DockController;
import bibliothek.gui.dock.DefaultDockable;
import bibliothek.gui.dock.SplitDockStation;
import bibliothek.gui.dock.station.split.SplitDockGrid;
import io.github.ghackenberg.mbse.transport.core.Controller;
import io.github.ghackenberg.mbse.transport.core.Simulator;
import io.github.ghackenberg.mbse.transport.core.controllers.SwitchableController;

public class SingleViewer {

	private static final int GAP = 10;
	private final List<Viewer> viewers = new ArrayList<>();
	
	private final Simulator simulator;
	
	private final JFrame frame;
	private final JPanel border;
	private final DockController controller;
	private final SplitDockStation station;
	private final SplitDockGrid grid;
	private final JToolBar toolbar;
	private final JButton start;
	private final JButton stop;
	private final JButton pause;
	private final JButton resume;
	private final JButton step;
	private final JComboBox<Controller> ctrl;
	private final JSpinner maxModelTimeStep;
	private final JSpinner ratioModelRealTime;
	private final JCheckBox screenshots;
	
	private int counter = 0;
	
	public SingleViewer(Simulator simulator, SwitchableController switcher) {
		this.simulator = simulator;
		
		// Create buttons
		start = new JButton("Start");
		stop = new JButton("Stop");
		pause = new JButton("Pause");
		resume = new JButton("Resume");
		step = new JButton("Step");
		
		start.addActionListener(event -> {
			start.setEnabled(false);
			stop.setEnabled(true);
			
			counter = 0;
			
			simulator.start();
		});
		stop.addActionListener(event -> {
			start.setEnabled(true);
			stop.setEnabled(false);
			
			simulator.stop();
		});
		pause.addActionListener(event -> {
			//JOptionPane.showMessageDialog(pause, "The pause button was clicked!");
			pause.setEnabled(false);
			resume.setEnabled(true);
			step.setEnabled(true);
			
			simulator.pause();
		});
		resume.addActionListener(event -> {
			//JOptionPane.showMessageDialog(resume, "The resume button was clicked!");
			pause.setEnabled(true);
			resume.setEnabled(false);
			step.setEnabled(false);
			
			simulator.resume();
		});
		step.addActionListener(event -> {
			simulator.step();
		});
		
		start.setEnabled(false);
		resume.setEnabled(false);
		step.setEnabled(false);
		
		// Create selectors
		ctrl = new JComboBox<Controller>(switcher.getControllers());
		ctrl.setSelectedItem(switcher.getActiveController());
		ctrl.addActionListener(event -> {
			switcher.setActiveController((Controller) ctrl.getSelectedItem());
		});
		
		// Create spinners
		maxModelTimeStep = new JSpinner(new SpinnerNumberModel(simulator.getMaxModelTimeStep(), 1, Double.MAX_VALUE, 1));
		ratioModelRealTime = new JSpinner(new SpinnerNumberModel(simulator.getRatioModelRealTime(), 0.1, Double.MAX_VALUE, 0.1));
		
		maxModelTimeStep.setPreferredSize(new Dimension(1, 1));
		maxModelTimeStep.addChangeListener(event -> {
			simulator.setMaxModelTimeStep((Double) maxModelTimeStep.getValue());
		});
		ratioModelRealTime.setPreferredSize(new Dimension(1, 1));
		ratioModelRealTime.addChangeListener(event -> {
			simulator.setRatioModelRealTime((Double) ratioModelRealTime.getValue());
		});
		
		// Create checkboxes
		screenshots = new JCheckBox("Take screenshots");
		
		// Create toolbar
		toolbar = new JToolBar();
		toolbar.add(start);
		toolbar.add(stop);
		toolbar.addSeparator();
		toolbar.add(pause);
		toolbar.add(resume);
		toolbar.add(step);
		toolbar.addSeparator();
		toolbar.add(new JLabel("Controller:"));
		toolbar.addSeparator();
		toolbar.add(ctrl);
		toolbar.addSeparator();
		toolbar.add(new JLabel("Maximum model time step:"));
		toolbar.addSeparator();
		toolbar.add(maxModelTimeStep);
		toolbar.addSeparator();
		toolbar.add(new JLabel("Model time / Real time:"));
		toolbar.addSeparator();
		toolbar.add(ratioModelRealTime);
		toolbar.addSeparator();
		toolbar.add(screenshots);
		
		// Create grid
		grid = new SplitDockGrid();
		
		// FIXME Remove work around for linux
		System.setProperty("java.version", "13.0.0");
		
		// Create station
		station = new SplitDockStation();
		station.dropTree(grid.toTree());
		
		// Create controller
		controller = new DockController();
		controller.add(station);
		
		// Create panel
		border = new JPanel();
		border.setLayout(new BorderLayout(GAP, GAP));
		border.add(toolbar, BorderLayout.PAGE_START);
		border.add(station.getComponent(), BorderLayout.CENTER);
		
		// Create frame
		frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(600, 400);
		frame.setResizable(true);
		frame.setTitle("Single Viewer");
		frame.setContentPane(border);
		frame.setExtendedState(frame.getExtendedState() | JFrame.MAXIMIZED_BOTH);
		frame.setVisible(true);
	}
	
	public void addViewer(double x, double y, double width, double height, Viewer viewer) {
		viewers.add(viewer);
		
		grid.addDockable(x, y, width, height, new DefaultDockable(viewer.getComponent(), viewer.getName(), viewer.getIcon()));
		
		station.dropTree(grid.toTree());
	}
	
	public void handleUpdated() {
		viewers.forEach(viewer -> {
			viewer.update();
		});
		takeScreenshot();
	}
	
	public void handleStopped() {
		start.setEnabled(true);
		stop.setEnabled(false);
		takeScreenshot();
	}
	
	public void handleFinished() {
		start.setEnabled(true);
		stop.setEnabled(false);
		takeScreenshot();
	}
	
	public void handleException(Exception exception) {
		JOptionPane.showMessageDialog(frame, exception.getMessage(), "Exception", JOptionPane.WARNING_MESSAGE);
		start.setEnabled(true);
		stop.setEnabled(false);
		takeScreenshot();
	}
	
	private void takeScreenshot() {
		if (screenshots.isSelected()) {
			try {
				BufferedImage image = new BufferedImage(border.getWidth(), border.getHeight(), BufferedImage.TYPE_INT_RGB);
				Graphics2D graphics = image.createGraphics();
				border.printAll(graphics);
				graphics.dispose();
				ImageIO.write(image, "png", new File(simulator.getRunFolder(), "frame-" + counter++ + ".png"));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
}

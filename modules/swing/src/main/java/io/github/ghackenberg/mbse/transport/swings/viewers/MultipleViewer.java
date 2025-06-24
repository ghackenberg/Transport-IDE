package io.github.ghackenberg.mbse.transport.swings.viewers;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import bibliothek.gui.DockController;
import bibliothek.gui.dock.DefaultDockable;
import bibliothek.gui.dock.SplitDockStation;
import bibliothek.gui.dock.station.split.SplitDockGrid;
import io.github.ghackenberg.mbse.transport.core.Simulator;
import io.github.ghackenberg.mbse.transport.swings.viewers.charts.multiple.DemandTimesChartViewer;

public class MultipleViewer {
	
	private static final int GAP = 10;
	
	private final JFrame frame;
	private final JPanel border;
	private final DockController controller;
	private final SplitDockStation station;
	private final SplitDockGrid grid;
	
	private final List<Viewer> viewers = new ArrayList<>();
	
	private List<Simulator> simulators;
	
	public MultipleViewer(List<Simulator> simulators) {
		this.simulators = simulators;
		
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
		border.add(station.getComponent(), BorderLayout.CENTER);
		
		// Create frame
		frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(600, 600);
		frame.setResizable(true);
		frame.setTitle("Multiple Viewer");
		frame.setContentPane(border);
		//frame.setExtendedState(frame.getExtendedState() | JFrame.MAXIMIZED_BOTH);
		frame.setVisible(true);
		
		// Process simulators
		int index = 0;
		
		for (Simulator simulator : simulators) {
			final int number = index;
			
			addViewer(number, 0, 1, 1, new ModelViewer(simulator.getModel(), simulator.getStatistics()));
			
			simulator.setHandleUpdated(() -> {
				handleUpdated(number);
			});
			simulator.setHandleStopped(() -> {
				handleStopped(number);
			});
			simulator.setHandleFinished(() -> {
				handleFinished(number);
			});
			simulator.setHandleException(exception -> {
				handleException(number, exception);
			});
			
			index++;
		}
		
		addViewer(0, 1, simulators.size(), 1, new DemandTimesChartViewer(simulators));
	}
	
	private void addViewer(double x, double y, double width, double height, Viewer viewer) {
		viewers.add(viewer);
		
		grid.addDockable(x, y, width, height, new DefaultDockable(viewer.getComponent(), viewer.getName(), viewer.getIcon()));
		
		station.dropTree(grid.toTree());
	}
	
	private int updated = 0;
	private int processed = 0;
	
	private synchronized void handleUpdated(int simulator) throws InterruptedException {
		// Wait
		
		while (processed > 0) {
			wait();
		}
		
		// Mark
		
		updated++;
			
		// Wait
		
		while (updated + processed + stopped + finished + excepted < simulators.size()) {
			wait();
		}
		
		// Update
		
		if (processed == 0) {
			for (Viewer viewer : viewers) {
				viewer.update();
			}
		}
		
		// Mark
		
		updated--;
		
		if (updated == 0) {
			processed = 0;
		} else {
			processed++;
		}
		
		// Wake-up
		
		notifyAll();
	}
	
	private int stopped = 0;
	
	private synchronized void handleStopped(int simulator) {
		stopped++;
		
		notifyAll();
		
		//JOptionPane.showMessageDialog(frame, "Simulator stopped", "Stopped (" + index + ")", JOptionPane.INFORMATION_MESSAGE);
	}
	
	private int finished = 0;
	
	private synchronized void handleFinished(int simulator) {
		finished++;
		
		notifyAll();
		
		//JOptionPane.showMessageDialog(frame, "Simulator finished", "Finished (" + index + ")", JOptionPane.INFORMATION_MESSAGE);
	}
	
	private int excepted = 0;
	
	private synchronized void handleException(int simulator, Exception exception) {
		excepted++;
		
		notifyAll();
		
		JOptionPane.showMessageDialog(frame, exception.getMessage(), "Exception (" + simulator + ")", JOptionPane.WARNING_MESSAGE);
	}
	
}

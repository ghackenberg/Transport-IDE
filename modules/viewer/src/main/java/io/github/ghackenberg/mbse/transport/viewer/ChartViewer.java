package example.viewer;

import java.awt.Image;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.data.category.DefaultCategoryDataset;

public abstract class ChartViewer implements Viewer {
	
	private String title;
	private String categoryAxisLabel;
	private String valueAxisLabel;
	
	protected DefaultCategoryDataset dataset = new DefaultCategoryDataset();
	protected JFreeChart chart;
	protected CategoryPlot plot;
	protected CategoryAxis domain;
	protected NumberAxis range;
	protected BarRenderer renderer;
	
	private ChartPanel panel;
	
	public ChartViewer(String title, String categoryAxisLabel, String valueAxisLabel) {
		this.title = title;
		this.categoryAxisLabel = categoryAxisLabel;
		this.valueAxisLabel = valueAxisLabel;
		
		initialize();
	}
	
	private void initialize() {		
		chart = ChartFactory.createStackedBarChart(title, categoryAxisLabel, valueAxisLabel, dataset);
		chart.getRenderingHints().put(JFreeChart.KEY_SUPPRESS_SHADOW_GENERATION, Boolean.TRUE);
		
		plot = chart.getCategoryPlot();
		
		domain = plot.getDomainAxis();
		
		range = (NumberAxis) plot.getRangeAxis();
		
		renderer = (BarRenderer) chart.getCategoryPlot().getRenderer();
		renderer.setBarPainter(new StandardBarPainter());
		
		panel = new ChartPanel(chart);
	}
	
	@Override
	public String getName() {
		return title;
	}
	
	@Override
	public Icon getIcon() {
		try {
			URL resource = ModelViewer.class.getResource("/icons/chart.png");
			Image image = ImageIO.read(resource);
			return new ImageIcon(image.getScaledInstance(16, 16, Image.SCALE_SMOOTH));
		} catch (IOException exception) {
			exception.printStackTrace();
			return null;
		}
	}
	
	@Override
	public JComponent getComponent() {
		return panel;
	}

	@Override
	public abstract void update();
	
}

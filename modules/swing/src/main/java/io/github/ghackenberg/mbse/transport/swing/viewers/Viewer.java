package io.github.ghackenberg.mbse.transport.swing.viewers;

import javax.swing.Icon;
import javax.swing.JComponent;

public interface Viewer {

	public String getName();
	
	public Icon getIcon();
	
	public JComponent getComponent();
	
	public void update();
	
}

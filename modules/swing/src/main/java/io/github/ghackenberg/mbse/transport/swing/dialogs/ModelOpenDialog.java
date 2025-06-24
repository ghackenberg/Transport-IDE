package io.github.ghackenberg.mbse.transport.swing.dialogs;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.JFileChooser;

public class ModelOpenDialog {
	
	private static File CACHE = new File(".transport-ide");
	private static JFileChooser FOLDER_CHOOSER = new JFileChooser();
	
	public static File choose() {
		FOLDER_CHOOSER.setDialogTitle("Select model folder");
		FOLDER_CHOOSER.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

		if (CACHE.exists()) {
			try {
				BufferedReader reader = new BufferedReader(new FileReader(CACHE));
				FOLDER_CHOOSER.setCurrentDirectory(new File(reader.readLine()));
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		if (FOLDER_CHOOSER.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
			try {
				FileWriter writer = new FileWriter(CACHE);
				writer.write(FOLDER_CHOOSER.getSelectedFile().getParentFile().getAbsolutePath());
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			return FOLDER_CHOOSER.getSelectedFile();
		} else {
			return null;
		}
	}

}

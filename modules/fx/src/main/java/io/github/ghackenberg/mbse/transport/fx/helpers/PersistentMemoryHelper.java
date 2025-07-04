package io.github.ghackenberg.mbse.transport.fx.helpers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class PersistentMemoryHelper {
	
	private static final File homeFolder = new File(System.getProperty("user.home"));
	private static final File memoryFile = new File(homeFolder, ".transport-ide");
	
	private static File contextFolder;

	public static File getContextFolder() {
		if (contextFolder == null) {
			if (memoryFile.exists()) {
				try (BufferedReader in = new BufferedReader(new FileReader(memoryFile))) {
					contextFolder = new File(in.readLine());
				} catch (FileNotFoundException e) {
					contextFolder = new File(".");
					e.printStackTrace();
				} catch (IOException e) {
					contextFolder = new File(".");
					e.printStackTrace();
				}
			} else {
				contextFolder = new File(".");
			}
		}
		return contextFolder;
	}
	
	public static void setContextFolder(File folder) {
		if (memoryFile.exists()) {
			memoryFile.delete();
		}
		try (FileWriter out = new FileWriter(memoryFile)) {
			out.write(folder.getAbsolutePath());
		} catch (IOException e) {
			e.printStackTrace();
		}
		contextFolder = folder;
	}
	
}

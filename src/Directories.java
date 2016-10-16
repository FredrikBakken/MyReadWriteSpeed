import java.io.File;
import java.io.IOException;

import javax.swing.filechooser.FileSystemView;

public class Directories {

	// Directory variables
	public static File[] paths;
	public static FileSystemView fsv;

	// Method for deleting the temporary folder
	public static void deleteFileAndFolder() throws IOException {
		File file = new File(MyReadWriteSpeed.location + "\\MyRWToolTempFile.txt");
		File directory = new File(MyReadWriteSpeed.location);

		file.delete();

		if (directory.isDirectory()) {
			if (directory.list().length == 0) {
				directory.delete();
			}
		}

	}

	// Search for all directories
	public static void findDirectories() {
		fsv = FileSystemView.getFileSystemView();

		// Return path names for files and directory
		paths = File.listRoots();
	}

}

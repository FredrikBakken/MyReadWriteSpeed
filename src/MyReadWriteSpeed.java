import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

import javax.swing.filechooser.FileSystemView;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class MyReadWriteSpeed extends Application {
	private static String location = "";
	private static int nMBs = 0;

	// Time testing values
	private static long start = 0;
	private static long stop = 0;
	private static long time = 0;

	// Placeholder for elements
	private static char[] array = new char[1024];

	// Directory variables
	private static File[] paths;
	private static FileSystemView fsv;

	ListView<String> listView;

	private static Label loading;
	private static Label writeRes1;
	private static Label writeRes2;
	private static Label writeRes3;
	private static Label readRes1;
	private static Label readRes2;
	private static Label readRes3;

	// Main method
	public static void main(String[] args) {
		MyReadWriteSpeed MyRWSpeed = new MyReadWriteSpeed();

		MyRWSpeed.findDirectories();

		launch(args);
	}

	// Primary GUI stage
	@Override
	public void start(Stage primaryStage) throws Exception {
		// Initialize stage window
		Stage window = primaryStage;
		window.setTitle("My Read/Write Speed Test Tool");
		
		// Define local elements
		TextField MBs = new TextField();
		MBs.setPromptText("(WARNING: Must be a number to work!)");
		
		Button start = new Button("START SPEED TEST");
		start.setOnAction(e -> buttonClicked(MBs));
		
		Label empty = new Label("");
		
		Text select = new Text("Select a Drive:");
		Text MBText = new Text("Inset file size in MB:");
		Text writeResults = new Text("Write Results:");
		Text readResults = new Text("Read Results:");
		select.setFont(Font.font(null, FontWeight.BOLD, 12));
		MBText.setFont(Font.font(null, FontWeight.BOLD, 12));
		writeResults.setFont(Font.font(null, FontWeight.BOLD, 12));
		readResults.setFont(Font.font(null, FontWeight.BOLD, 12));
		
		// Define global elements
		loading = new Label("");
		writeRes1 = new Label("Time: ");
		writeRes2 = new Label("File size: ");
		writeRes3 = new Label("Write speed: ");
		readRes1 = new Label("Time: ");
		readRes2 = new Label("File size: ");
		readRes3 = new Label("Read speed: ");
		
		// Initialize directory list
		ObservableList<String> dir = FXCollections.observableArrayList();
		listView = new ListView<String>(dir);
		listView.setPrefHeight(100);

		// Add values to directory list
		for (File path : paths) {
			String temp = path.getPath();
			dir.add(temp);
		}

		// Structure layout
		BorderPane border = new BorderPane();

		// Left layout
		VBox layout = new VBox(10);
		layout.setPadding(new Insets(20, 20, 20, 20));
		layout.getChildren().addAll(select, listView, MBText, MBs, start);

		// Right layout
		VBox layout2 = new VBox(10);
		layout2.setPadding(new Insets(20, 20, 20, 20));
		layout2.getChildren().addAll(loading, writeResults, writeRes1, writeRes2, writeRes3, empty, readResults, readRes1, readRes2,
				readRes3);

		border.setLeft(layout);
		border.setCenter(layout2);

		// Create a scene
		Scene scene = new Scene(border, 480, 300);
		window.setScene(scene);

		window.show();

	}

	// Button events (start test)
	private void buttonClicked(TextField MBs) {
		// Define local variables
		String selectedDir = "";
		ObservableList<String> sDir;
		
		// Get drive and set test location
		sDir = listView.getSelectionModel().getSelectedItems();
		nMBs = Integer.parseInt(MBs.getText());

		for (String d : sDir) {
			selectedDir += d;
			location = selectedDir + "MyRWTool";
		}
		
		// Call for test method
		readWriteTesting();
	}

	// Read and write testing method
	private static void readWriteTesting() {
		// Define local variables
		String writeResult1;
		String writeResult2;
		String writeResult3;
		String readResult1;
		String readResult2;
		String readResult3;
		double seconds;

		// Fill string with 8-bit char values
		Arrays.fill(array, 'A');
		String elements = new String(array);

		// Set path equal to test location
		Path path = Paths.get(location);

		// Does the directory exist?
		if (!Files.exists(path)) {
			try {
				Files.createDirectories(path);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		// Actual Read/Write Test
		try {
			// Create a temporary file
			File file = new File(location + "\\MyRWToolTempFile.txt");
			PrintWriter pw = new PrintWriter(new FileWriter(file));
			
			// Start WRITE test
			start = System.nanoTime();
			for (int i = 0; i < nMBs * 1024; i++)
				pw.println(elements);
			pw.close();
			stop = System.nanoTime();

			time = stop - start;
			seconds = (double) time / 1000000000.0;

			// Set results to Strings
			writeResult1 = String.valueOf(String.format("%.2f", time / 1e9));
			writeResult2 = String.valueOf(nMBs);
			writeResult3 = String.valueOf(String.format("%.2f", nMBs / seconds));

			// Display results
			writeRes1.setText("Time: " + writeResult1 + "s");
			writeRes2.setText("File size: " + writeResult2 + "MB");
			writeRes3.setText("Write speed: " + writeResult3 + "MB/s");

			/* **************************************************************** */
			
			// Start READ test
			start = System.nanoTime();
			BufferedReader buffRead = new BufferedReader(new FileReader(file));
			for (String line; (line = buffRead.readLine()) != null;) {
			}
			buffRead.close();
			stop = System.nanoTime();

			time = stop - start;
			seconds = (double) time / 1000000000.0;

			// Set results to Strings
			readResult1 = String.valueOf(String.format("%.2f", time / 1e9));
			readResult2 = String.valueOf(file.length() / 1000000);
			readResult3 = String.valueOf(file.length() * 1000 / time);

			// Display results
			readRes1.setText("Time: " + readResult1 + "s");
			readRes2.setText("File size: " + readResult2 + "MB");
			readRes3.setText("Read speed: " + readResult3 + "MB/s");
			
			loading.setText("Speed test completed!");
			
			// Delete temporary folder
			deleteFileAndFolder();

		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	// Method for deleting the temporary folder
	private static void deleteFileAndFolder() throws IOException {
		File file = new File(location + "\\MyRWToolTempFile.txt");
		File directory = new File(location);
		
		file.delete();
		
		if(directory.isDirectory()) {
			if(directory.list().length == 0) {
				directory.delete();
			}
		}
		
	}

	// Search for all directories
	private void findDirectories() {
		fsv = FileSystemView.getFileSystemView();

		// Return path names for files and directory
		paths = File.listRoots();
	}

}

package eu.proxima.payments.utils;

import org.springframework.stereotype.Component;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.format.DateTimeFormatter;

/**
 * Utility class for writing log messages to a file.
 */
@Component
public class FileUtil {

	private static final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

	/**
	 * Writes a single message to the specified file. Appends by default.
	 *
	 * @param filePath Path to the log file
	 * @param message  The message to write
	 */
	public void writeToFile(String filePath, String message) {
		writeToFile(filePath, message, true);
	}

	/**
	 * Writes a message to a file.
	 *
	 * @param filePath Path to the file
	 * @param message  The message to write
	 * @param append   True to append, false to overwrite
	 */
	public void writeToFile(String filePath, String message, boolean append) {
		File file = new File(filePath);
		try {
			// Ensure parent directories exist
			File parent = file.getParentFile();
			if (parent != null)
				parent.mkdirs();

			try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, append))) {
				// String timestamp = dtf.format(LocalDateTime.now());
				writer.write(message);
				writer.newLine();
			}
		} catch (IOException e) {
			System.err.println("Failed to write log to file: " + e.getMessage());
			e.printStackTrace();
		}
	}
}

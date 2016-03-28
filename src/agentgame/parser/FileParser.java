package agentgame.parser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * FileParser parses the given file and returns a List<String> with the file
 * content.
 */
public class FileParser {

	/** The file to be parsed. */
	private File file;

	/** The file reader. */
	private final FileReader fileReader;

	/** The buffered reader. */
	private final BufferedReader bufferedReader;

	/**
	 * Instantiates a new file parser.
	 *
	 * @param filePath
	 *            the file path
	 * @throws FileNotFoundException
	 *             the file not found exception
	 */
	public FileParser(final String filePath) throws FileNotFoundException {
		file = new File(filePath);
		fileReader = new FileReader(file);
		bufferedReader = new BufferedReader(fileReader);
	}

	public FileParser(final File file) throws FileNotFoundException {
		fileReader = new FileReader(file);
		bufferedReader = new BufferedReader(fileReader);
	}

	/**
	 * Parses the given file.
	 *
	 * @return the list
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public List<String> parse() throws IOException {
		final List<String> lines = new ArrayList<String>();
		String line = null;
		while ((line = bufferedReader.readLine()) != null) {
			lines.add(line);
		}

		fileReader.close();
		bufferedReader.close();
		return lines;
	}

}

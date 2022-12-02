package com.sentrysoftware.hardware.agent.process.runtime;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;

import org.springframework.util.Assert;

import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

/**
 * This class controls the process through the start, stop and addShutdownHook
 * methods
 */
@Slf4j
public class ProcessControl {

	@Getter
	private final Process process;
	@Getter
	private InputStreamReader reader;
	@Getter
	private InputStreamReader error;

	ProcessControl(final Process process) {
		this.process = process;
		final InputStream inputStream = process.getInputStream();
		if (inputStream != null) {
			this.reader = new InputStreamReader(inputStream);
		}
		final InputStream errorStream = process.getErrorStream();
		if (errorStream != null) {
			this.error = new InputStreamReader(errorStream);
		}
	}

	/**
	 * Create a new instance of the {@link ProcessBuilder} used to start the process
	 * 
	 * @param commandLine         The process command line
	 * @param environment         The process environment map
	 * @param workingDir          The process working directory
	 * @param redirectErrorStream If set to true then any error output generated by subprocesses will be merged with the standard output
	 * @return {@link ProcessBuilder} instance
	 */
	public static ProcessBuilder newProcessBuilder(
			@NonNull final List<String> commandLine,
			@NonNull final Map<String, String> environment,
			final File workingDir,
			final boolean redirectErrorStream) {

		Assert.isTrue(!commandLine.isEmpty(), "Command line cannot be empty.");

		final ProcessBuilder processBuilder = new ProcessBuilder(commandLine);

		processBuilder.environment().putAll(environment);

		if (workingDir != null) {
			processBuilder.directory(workingDir);
		}

		// Sets the process builder's redirectErrorStream property. 
		// This makes it easier to correlate error messages with the corresponding output.
		// The ProcessBuilder's initial value is false
		processBuilder.redirectErrorStream(redirectErrorStream);

		return processBuilder;
	}

	/**
	 * Starts a process and builds the {@link ProcessControl} instance
	 * 
	 * @param processBuilder
	 * @return {@link ProcessControl} instance
	 * @throws IOException
	 */
	public static ProcessControl start(final ProcessBuilder processBuilder) throws IOException {
		return new ProcessControl(processBuilder.start());
	}

	/**
	 * Close the InputStream, OutputStream and ErrorStream then destroy the process
	 */
	public void stop() {
		try {
			// Close streams
			close(process.getErrorStream());
			close(process.getInputStream());
			close(process.getOutputStream());

			// Destroy the process
			process.destroy();

		} catch (Exception e) {
			log.error("Error detected when terminating the process. Message {}.", e.getMessage());
			log.debug("Exception: ", e);
		}
		reader = null;
		error = null;
	}

	/**
	 * Close the closable and avoid any null pointer exception if the argument is <code>null</code>
	 * 
	 * @param closable
	 * @throws IOException
	 */
	private void close(Closeable closeable) throws IOException {
		if (closeable != null) {
			closeable.close();
		}
	}

	/**
	 * Registers a new virtual-machine shutdown hook. 
	 * 
	 * @param runnable
	 */
	public static void addShutdownHook(Runnable runnable) {
		Runtime.getRuntime().addShutdownHook(new Thread(runnable));
	}
}

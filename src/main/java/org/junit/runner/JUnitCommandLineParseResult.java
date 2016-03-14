package org.junit.runner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.internal.Classes;
import org.junit.runner.FilterFactory.FilterNotCreatedException;
import org.junit.runner.manipulation.Filter;
import org.junit.runners.model.InitializationError;

class JUnitCommandLineParseResult {
    private final List<String> filterSpecs = new ArrayList<String>();
    private final List<Class<?>> classes = new ArrayList<Class<?>>();
    private final List<Throwable> parserErrors = new ArrayList<Throwable>();

    /**
     * Do not use. Testing purposes only.
     */
    JUnitCommandLineParseResult() {}

    /**
     * Returns filter specs parsed from command line.
     */
    public List<String> getFilterSpecs() {
        return Collections.unmodifiableList(filterSpecs);
    }

    /**
     * Returns test classes parsed from command line.
     */
    public List<Class<?>> getClasses() {
        return Collections.unmodifiableList(classes);
    }

    /**
     * Parses the arguments.
     *
     * @param args Arguments
     */
    public static JUnitCommandLineParseResult parse(String[] args) {
        JUnitCommandLineParseResult result = new JUnitCommandLineParseResult();

        result.parseArgs(args);

        return result;
    }

    private void parseArgs(String[] args) {
        parseParameters(parseOptions(args));
    }

    String[] parseOptions(String... args) {
        for (int i = 0; i != args.length; ++i) {
            String arg = args[i];

            if (arg.equals("--")) {
                return copyArray(args, i + 1, args.length);
            } else if (arg.startsWith("--")) {
                if (arg.startsWith("--filter=") || arg.equals("--filter")) {
                    String filterSpec;
                    if (arg.equals("--filter")) {
                        ++i;

                        if (i < args.length) {
                            filterSpec = args[i];
                        } else {
                            parserErrors.add(new CommandLineParserError(arg + " value not specified"));
                            break;
                        }
                    } else {
                        filterSpec = arg.substring(arg.indexOf('=') + 1);
                    }

                    filterSpecs.add(filterSpec);
                } else {
                    parserErrors.add(new CommandLineParserError("JUnit knows nothing about the " + arg + " option"));
                }
            } else {
                return copyArray(args, i, args.length);
            }
        }

        return new String[]{};
    }

    private String[] copyArray(String[] args, int from, int to) {
        ArrayList<String> result = new ArrayList<String>();

        for (int j = from; j != to; ++j) {
            result.add(args[j]);
        }

        return result.toArray(new String[result.size()]);
    }

    void parseParameters(String[] args) {
        for (String arg : args) {
            try {
                classes.add(Classes.getClass(arg));
            } catch (ClassNotFoundException e) {
                parserErrors.add(new IllegalArgumentException("Could not find class [" + arg + "]", e));
            }
        }
    }

    private Request errorReport(Throwable cause) {
        return Request.errorReport(JUnitCommandLineParseResult.class, cause);
    }

    /**
     * Creates a {@link Request}.
     *
     * @param computer {@link Computer} to be used.
     */
    public Request createRequest(Computer computer) {
        if (parserErrors.isEmpty()) {
            Request request = Request.classes(
                    computer, classes.toArray(new Class<?>[classes.size()]));
            return applyFilterSpecs(request);
        } else {
            return errorReport(new InitializationError(parserErrors));
        }
    }

    private Request applyFilterSpecs(Request request) {
        try {
            for (String filterSpec : filterSpecs) {
                Filter filter = FilterFactories.createFilterFromFilterSpec(
                        request, filterSpec);
                request = request.filterWith(filter);
            }
            return request;
        } catch (FilterNotCreatedException e) {
            return errorReport(e);
        }
    }

    /**
     * Exception used if there's a problem parsing the command line.
     */
    public static class CommandLineParserError extends Exception {
        private static final long serialVersionUID= 1L;

        public CommandLineParserError(String message) {
            super(message);
        }
    }
}

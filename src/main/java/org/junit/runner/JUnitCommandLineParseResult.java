package org.junit.runner;

import java.util.ArrayList;
import java.util.List;

import org.junit.internal.Classes;
import org.junit.internal.runners.ErrorReportingRunner;
import org.junit.runner.manipulation.Filter;
import org.junit.runners.model.InitializationError;

import static org.junit.runner.Description.createSuiteDescription;

class JUnitCommandLineParseResult {
    private Filter filter = Filter.ALL;
    private List<Class<?>> classes = new ArrayList<Class<?>>();
    private List<Throwable> parserErrors = new ArrayList<Throwable>();

    /**
     * Do not use. Testing purposes only.
     */
    JUnitCommandLineParseResult() {}

    /**
     * Returns filters parsed from command line.
     */
    public Filter getFilter() {
        return filter;
    }

    /**
     * Returns test classes parsed from command line.
     */
    public List<Class<?>> getClasses() {
        return classes;
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

    void parseArgs(String[] args) {
        parseParameters(parseOptions(args));
    }

    String[] parseOptions(String[] args) {
        for (int i = 0; i != args.length; ++i) {
            String arg = args[i];

            try {
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

                        filter = filter.intersect(FilterFactories.createFilterFromFilterSpec(
                                createSuiteDescription(arg), filterSpec));
                    } else {
                        parserErrors.add(new CommandLineParserError("JUnit knows nothing about the " + arg + " option"));
                    }
                } else {
                    return copyArray(args, i, args.length);
                }
            } catch (FilterFactory.FilterNotCreatedException e) {
                parserErrors.add(e);
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

    /**
     * Creates a {@link Request}.
     *
     * @param computer {@link Computer} to be used.
     */
    public Request createRequest(Computer computer) {
        if (parserErrors.isEmpty()) {
            return Request
                    .classes(computer, classes.toArray(new Class<?>[classes.size()]))
                    .filterWith(filter);
        } else {
            return new Request() {
                @Override
                public Runner getRunner() {
                    return new ErrorReportingRunner(
                            JUnitCommandLineParseResult.class,
                            new InitializationError(parserErrors));
                }
            };
        }
    }

    /**
     * Exception used if there's a problem parsing the command line.
     */
    public static class CommandLineParserError extends Exception {
        public CommandLineParserError(String message) {
            super(message);
        }
    }
}

package org.junit.runner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.internal.ClassUtil;
import org.junit.internal.JUnitSystem;
import org.junit.runner.manipulation.Filter;
import org.junit.runner.notification.Failure;

import static org.junit.runner.Description.createSuiteDescription;

class JUnitCommandLineParser {
    private final JUnitSystem system;

    private Filter filter = Filter.ALL;
    private List<Class<?>> classes = new ArrayList<Class<?>>();
    private List<Failure> failures = new ArrayList<Failure>();

    public JUnitCommandLineParser(JUnitSystem system) {
        this.system = system;
    }

    public Filter getFilter() {
        return filter;
    }

    public List<Class<?>> getClasses() {
        return classes;
    }

    public List<Failure> getFailures() {
        return failures;
    }

    public void parseArgs(String[] args) {
        parseParameters(parseOptions(args));
    }

    String[] parseOptions(String[] args) {
        FilterFactoryFactory filterFactoryFactory = new FilterFactoryFactory();

        for (int i = 0; i != args.length; ++i) {
            String arg = args[i];

            try {
                if (arg.equals("--")) {
                    return Arrays.copyOfRange(args, i + 1, args.length);
                } else if (arg.startsWith("--")) {
                    if (arg.startsWith("--filter=") || arg.equals("--filter")) {
                        String filterSpec;
                        if (arg.equals("--filter")) {
                            ++i;
                            filterSpec = args[i];
                        } else {
                            filterSpec = arg.substring(arg.indexOf('=') + 1);
                        }

                        filter = filter.intersect(filterFactoryFactory.createFilterFromFilterSpec(
                                createSuiteDescription(arg), filterSpec));
                    } else {
                        Description description = createSuiteDescription(arg);
                        Failure failure = new Failure(
                                description,
                                new CommandLineParserError("JUnit knows nothing about the " + arg + " option"));

                        failures.add(failure);
                    }
                } else {
                    return Arrays.copyOfRange(args, i, args.length);
                }
            } catch (FilterFactory.FilterNotCreatedException e) {
                system.out().println("Could not find filter: " + e.getMessage());
                Description description = createSuiteDescription(arg);
                Failure failure = new Failure(description, e);
                failures.add(failure);
            } catch(FilterFactoryFactory.FilterFactoryNotCreatedException e) {
                system.out().println("Could not find filter factory: " + e.getMessage());
                Description description = createSuiteDescription(arg);
                Failure failure = new Failure(description, e);
                failures.add(failure);
            }
        }

        return null;
    }

    void parseParameters(String[] args) {
        for (String arg : args) {
            try {
                classes.add(ClassUtil.getClass(arg));
            } catch (ClassNotFoundException e) {
                system.out().println("Could not find class: " + arg);
                Description description = createSuiteDescription(arg);
                Failure failure = new Failure(description, e);
                failures.add(failure);
            }
        }
    }

    public Request createRequest(Computer computer) {
        return Request
                .classes(computer, classes.toArray(new Class<?>[0]))
                .filterWith(filter);
    }

    public static class CommandLineParserError extends Exception {
        public CommandLineParserError(String message) {
            super(message);
        }
    }
}

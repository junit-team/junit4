package org.junit.tests.running.core;

import java.io.ByteArrayOutputStream;
import java.io.FileDescriptor;
import java.io.PrintStream;
import java.net.InetAddress;
import java.security.Permission;

public class MainRunner {
    private static class ExitException extends SecurityException {
        private static final long serialVersionUID = -9104651568237766642L;

        private final int status;

        public ExitException(int status) {
            super("");
            this.status = status;
        }

        public int getStatus() {
            return status;
        }
    }

    /**
     * A {@code NoExitSecurityManager} throws a {@link ExitException} exception
     * whenever {@link #checkExit(int)} is called. All other method calls are
     * delegated to the original security manager.
     */
    public class NoExitSecurityManager extends SecurityManager {
        private final SecurityManager originalSecurityManager;

        public NoExitSecurityManager(SecurityManager originalSecurityManager) {
            this.originalSecurityManager = originalSecurityManager;
        }

        @Override
        public void checkExit(int status) {
            throw new ExitException(status);
        }

        @SuppressWarnings("deprecation")
        @Override
        public boolean getInCheck() {
            return (originalSecurityManager != null) && originalSecurityManager.getInCheck();
        }

        @Override
        public Object getSecurityContext() {
            return (originalSecurityManager == null) ? super.getSecurityContext() : originalSecurityManager.getSecurityContext();
        }

        @Override
        public void checkPermission(Permission perm) {
            if (originalSecurityManager != null) {
                originalSecurityManager.checkPermission(perm);
            }
        }

        @Override
        public void checkPermission(Permission perm, Object context) {
            if (originalSecurityManager != null) {
                originalSecurityManager.checkPermission(perm, context);
            }
        }

        @Override
        public void checkCreateClassLoader() {
            if (originalSecurityManager != null) {
                originalSecurityManager.checkCreateClassLoader();
            }
        }

        @Override
        public void checkAccess(Thread t) {
            if (originalSecurityManager != null) {
                originalSecurityManager.checkAccess(t);
            }
        }

        @Override
        public void checkAccess(ThreadGroup g) {
            if (originalSecurityManager != null) {
                originalSecurityManager.checkAccess(g);
            }
        }

        @Override
        public void checkExec(String cmd) {
            if (originalSecurityManager != null) {
                originalSecurityManager.checkExec(cmd);
            }
        }

        @Override
        public void checkLink(String lib) {
            if (originalSecurityManager != null) {
                originalSecurityManager.checkLink(lib);
            }
        }

        @Override
        public void checkRead(FileDescriptor fd) {
            if (originalSecurityManager != null) {
                originalSecurityManager.checkRead(fd);
            }
        }

        @Override
        public void checkRead(String file) {
            if (originalSecurityManager != null) {
                originalSecurityManager.checkRead(file);
            }
        }

        @Override
        public void checkRead(String file, Object context) {
            if (originalSecurityManager != null) {
                originalSecurityManager.checkRead(file, context);
            }
        }

        @Override
        public void checkWrite(FileDescriptor fd) {
            if (originalSecurityManager != null) {
                originalSecurityManager.checkWrite(fd);
            }
        }

        @Override
        public void checkWrite(String file) {
            if (originalSecurityManager != null) {
                originalSecurityManager.checkWrite(file);
            }
        }

        @Override
        public void checkDelete(String file) {
            if (originalSecurityManager != null) {
                originalSecurityManager.checkDelete(file);
            }
        }

        @Override
        public void checkConnect(String host, int port) {
            if (originalSecurityManager != null) {
                originalSecurityManager.checkConnect(host, port);
            }
        }

        @Override
        public void checkConnect(String host, int port, Object context) {
            if (originalSecurityManager != null) {
                originalSecurityManager.checkConnect(host, port, context);
            }
        }

        @Override
        public void checkListen(int port) {
            if (originalSecurityManager != null) {
                originalSecurityManager.checkListen(port);
            }
        }

        @Override
        public void checkAccept(String host, int port) {
            if (originalSecurityManager != null) {
                originalSecurityManager.checkAccept(host, port);
            }
        }

        @Override
        public void checkMulticast(InetAddress maddr) {
            if (originalSecurityManager != null) {
                originalSecurityManager.checkMulticast(maddr);
            }
        }

        @SuppressWarnings("deprecation")
        @Override
        public void checkMulticast(InetAddress maddr, byte ttl) {
            if (originalSecurityManager != null) {
                originalSecurityManager.checkMulticast(maddr, ttl);
            }
        }

        @Override
        public void checkPropertiesAccess() {
            if (originalSecurityManager != null) {
                originalSecurityManager.checkPropertiesAccess();
            }
        }

        @Override
        public void checkPropertyAccess(String key) {
            if (originalSecurityManager != null) {
                originalSecurityManager.checkPropertyAccess(key);
            }
        }

        @Override
        public boolean checkTopLevelWindow(Object window) {
            return (originalSecurityManager == null) ? super.checkTopLevelWindow(window) : originalSecurityManager.checkTopLevelWindow(window);
        }

        @Override
        public void checkPrintJobAccess() {
            if (originalSecurityManager != null) {
                originalSecurityManager.checkPrintJobAccess();
            }
        }

        @Override
        public void checkSystemClipboardAccess() {
            if (originalSecurityManager != null) {
                originalSecurityManager.checkSystemClipboardAccess();
            }
        }

        @Override
        public void checkAwtEventQueueAccess() {
            if (originalSecurityManager != null) {
                originalSecurityManager.checkAwtEventQueueAccess();
            }
        }

        @Override
        public void checkPackageAccess(String pkg) {
            if (originalSecurityManager != null) {
                originalSecurityManager.checkPackageAccess(pkg);
            }
        }

        @Override
        public void checkPackageDefinition(String pkg) {
            if (originalSecurityManager != null) {
                originalSecurityManager.checkPackageDefinition(pkg);
            }
        }

        @Override
        public void checkSetFactory() {
            if (originalSecurityManager != null) {
                originalSecurityManager.checkSetFactory();
            }
        }

        @Override
        public void checkMemberAccess(Class<?> clazz, int which) {
            if (originalSecurityManager != null) {
                originalSecurityManager.checkMemberAccess(clazz, which);
            }
        }

        @Override
        public void checkSecurityAccess(String target) {
            if (originalSecurityManager != null) {
                originalSecurityManager.checkSecurityAccess(target);
            }
        }

        @Override
        public ThreadGroup getThreadGroup() {
            return (originalSecurityManager == null) ? super.getThreadGroup() : originalSecurityManager.getThreadGroup();
        }
    }

    /**
     * Execute runnable.run(), preventing System.exit(). If System.exit() is called
     * in runnable.run(), the value is returned. If System.exit()
     * is not called, null is returned.
     *
     * @return null if System.exit() is not called, Integer.valueof(status) if not
     */
    public Integer runWithCheckForSystemExit(Runnable runnable) {
        SecurityManager oldSecurityManager = System.getSecurityManager();
        System.setSecurityManager(new NoExitSecurityManager(oldSecurityManager));
        PrintStream oldOut = System.out;

        System.setOut(new PrintStream(new ByteArrayOutputStream()));
        try {
            runnable.run();
            System.out.println("System.exit() not called, return null");
            return null;
        } catch (ExitException e) {
            System.out.println("System.exit() called, value=" + e.getStatus());
            return e.getStatus();
        } finally {
            System.setSecurityManager(oldSecurityManager);
            System.setOut(oldOut);
        }
    }
}

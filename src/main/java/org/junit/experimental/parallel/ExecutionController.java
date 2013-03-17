package org.junit.experimental.parallel;

/**
 * Used by {@link Scheduler} if has shared scheduling strategy.
 *
 * @author Tibor Digana (tibor17)
 * @since 4.12
 */
interface ExecutionController {
    static final ExecutionController DEFAULT = new ExecutionController() {
        public void resourceReleased() {
        }

        public boolean canSchedule() {
            return true;
        }
    };

    /**
     * Notifies the ancestor {@link Scheduler} as soon as the follower's thread has finished in
     * {@link Scheduler#finished()}.
     */
    void resourceReleased();

    /**
     * @return <tt>true</tt> if new children can be scheduled.
     */
    boolean canSchedule();
}

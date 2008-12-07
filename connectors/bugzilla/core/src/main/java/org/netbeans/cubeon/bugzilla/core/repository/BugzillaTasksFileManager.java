package org.netbeans.cubeon.bugzilla.core.repository;

import org.netbeans.cubeon.bugzilla.core.tasks.BugzillaTask;

import java.util.List;

/**
 * Bugzilla configuration file manager.
 * Loads and stores configuration in configuration file.
 *
 * @author radoslaw.holewa
 */
public interface BugzillaTasksFileManager {

    /**
     * Paersists given task in file repository.
     *
     * @param bugzillaTask - task to persist
     */
    void persistTask( BugzillaTask bugzillaTask );

    /**
     * Loads task from repository.
     *
     * @param taskId - task id
     * @return - bugzilla task
     */
    BugzillaTask loadTask( String taskId );

    /**
     * Removes task with given ID.
     *
     * @param taskId - task ID
     */
    void removeTask( String taskId );

    /**
     * Loads all persisted tasks.
     *
     * @return - list of all persisted tasks.
     */
    List<BugzillaTask> loadAllTasks();

}

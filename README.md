## NOTE:  Future development will at http://wiki.netbeans.org/TaskFocusedNetBeans ##

### Cube°n   Task-Focused Interface for Netbeans ###
Cube°n makes tasks a first class part of [NetBeans](http://netbeans.org/) by integrating with issue tracking systems. It then monitors your work activity to identify what's relevant and improves productivity by reducing searching, scrolling, navigation and sharing expertise.

## Cube°n Features ##

  * Task Repository Connectors
    * API/SPI for adding Connectors.
    * [Local Repository](wiki/GSLocalRepository)
      * Supports Create/Modify Task with Task Types(Task,Defect,Enhancement and Feature) with  Priorities and Statuses(New,Started and Completed).
      * [Supports Querying](wiki/GS_Local_Query) local task with Attributes (like All My Completed Tasks ).
      * Rich Task Editor.
    * [Jira Repository Connector](wiki/GSJiraRepository)
      * Supports to connect Jira repository base on project wise or repository wise.Ex:  http://jira.atlassian.com/browse/TST or http://jira.atlassian.com/ (this is really useful when working with large repository like http://jira.codehaus.org/ to prevent cache only relevant information locally ).
      * Create/Modify Task **offline/online** and merge task with remote repository.
      * Rich Task Editor with attributes editing, workflow actions and comments.
      * Create Query form remote repository filters.
      * Customized Jira Repositories including custom workflows.
      * Tracking of Locally Modified tasks and revert modifications.
      * Repository permissions based  to create, modify tasks.
    * [Trac Repository Connector](wiki/GSTracRepository)
      * Supports to connect Trac 0.11 repository.
      * Create/Modify Task **offline/online** and merge task with remote repository.
      * Rich Task Editor with attributes editing, workflow actions and comments.
      * Trac Repository Query Support.
      * Customized Trac Repositories including custom workflows.
      * Tracking of Locally Modified tasks and revert modifications.

  * [Task Explorer View](wiki/GS_Task_Explorer)
    * Drag n  Drop Task Management.
    * API/SPI for adding and customizing Views.
    * Categorizable Task View ( Folder base ).
    * Tasks Filtering and Sorting (Ex: Priority,Status etc.. base filtering and sorting ).
    * API/SPI for adding  Filtering and Comparators for Tasks.
    * Synchronizing with  Repository Queries.
    * Task Icon Badging ( Ex: Priority ).
  * [Focusable Task Context](wiki/GS_Task_Context)
    * API/SPI for adding Task Contexts.
    * Task Linking (Grouping related tasks under Task ).
    * Source Linking (Grouping related Sources under Task ).
    * Stacktraces Navigation (Scan and view task related Stacktraces and supports direct navigation to source code).
    * Active Task Context View On Task Explorer.


> http://cubeon.googlecode.com/files/home.PNG













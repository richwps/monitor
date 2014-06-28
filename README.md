RichWPS-Monitor
===============

A Monitoring System to monitor Web Processing Services (WPS)

Used Libraries and Frameworks
* [Quartz Scheduler Library](http://quartz-scheduler.org/)
* [Spark Micro Web Framewrok](http://www.sparkjava.com/)
* [JPA](http://www.oracle.com/technetwork/java/javaee/tech/persistence-jsp-140049.html) & [JAXB](https://jaxb.java.net/)
* [Apache HTTP Components](http://hc.apache.org/) (used for a simple WpsClient)


# Monitor Events

## Events triggered by  by Monitor

| Event identifier                        | Message datatype  |
|-----------------------------------------| ----------------- |
| monitor.shutdown                        |                   |

## Events triggered by MonitorControl

| Event identifier                        | Message datatype  |
|-----------------------------------------| ----------------- |
| monitorcontrol.pauseMonitoring          | WpsProcessEntity  |
| monitorcontrol.resumeMonitoring         | WpsProcessEntity  |
| monitorcontrol.deleteProcess            | WpsProcessEntity  |
| monitorcontrol.deleteWps                | WpsEntity         |
| monitorcontrol.updateWps                | WpsEntity         |
| monitorcontrol.setTestRequest           | WpsProcessEntity  |
| monitorcontrol.createAndScheduleProcess | WpsProcessEntity  |
| monitorcontrol.createWps                | WpsEntity         |  
| monitorcontrol.deleteTrigger            | TriggerKey        |
| monitorcontrol.saveTrigger              | TriggerKey        |

## Events triggered by JobExecutedHandlerThread

| Event identifier                        | Message datatype  |
|-----------------------------------------| ----------------- |
| measurement.wpsjob.wpsexception         | WpsProcessEntity  |
| scheduler.wpsjob.wasexecuted            | WpsProcessEntity  |
| monitorcontrol.pauseMonitoring          | WpsProcessEntity  |

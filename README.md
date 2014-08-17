RichWPS-Monitor
===============

A Monitoring System to monitor Web Processing Services (WPS).
See the [Project Site](http://fruchuxs.github.io/RichWPS-Monitor/) for Releases and JavaDoc!

## Libraries and Frameworks
* [Quartz Scheduler Library](http://quartz-scheduler.org/)
* [Spark Micro Web Framewrok](http://www.sparkjava.com/)
* [JPA](http://www.oracle.com/technetwork/java/javaee/tech/persistence-jsp-140049.html) 
* [GSON](https://code.google.com/p/google-gson/)
* [Apache HTTP Components](http://hc.apache.org/) (used for a simple WpsClient)
* [Apache Derby](http://db.apache.org/derby/)

## Architecture
![Software Architecture image](https://raw.githubusercontent.com/Fruchuxs/RichWPS-Monitor/master/projectfiles/softwarearchitecture.PNG)

## Monitor Events

### Events triggered by Monitor

| Event identifier                        | Message datatype  |
|-----------------------------------------| ----------------- |
| monitor.start                           |                   |
| monitor.restart                         |                   |
| monitor.shutdown                        |                   |

### Events triggered by MonitorControl

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

### Events triggered by JobExecutedHandlerThread

| Event identifier                        | Message datatype  |
|-----------------------------------------| ----------------- |
| measurement.wpsjob.wpsexception         | WpsProcessEntity  |
| scheduler.wpsjob.wasexecuted            | WpsProcessEntity  |
| monitorcontrol.pauseMonitoring          | WpsProcessEntity  |

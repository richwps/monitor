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


## V2 Changelog
Changelog: 
* WPS Identifier removed 
* MeasureJobFactory now caches WpsProcessEntity instances instead of querying the database at every create call 
* MonitorControl Facade extended and adjusted (endpoint selection instead of wpsIdentifier, selection by wpsId also possible) 
* getWpsId(URL) and getWpsProcessId(URL, String) methods added to the monitorcontrol facade to fetch the internal database IDs 
* DataAccess API extended and adjusted (selection by wpsId, endpoint selection instead of wpsIdentifier) 
* wpsUri renamed to endpoint 
* endpoint (previously wpsUri) now saved as String (VARCHAR in the database). This makes it easier to handle URLs in the database. The WpsEntity class encapsulate the String field as URL by getter and setter methods. This changes limit the URL length to 255 characters. I have tried to save the endpoints as URL and URI, but this makes selections impossible, because JPA can't compare CLOB in JPQL statements
* Display parameter removed from ListMeasurementRoute; metrics are always shown 
* ListWpsProcessesRoute added. /measurement/wps/:id now shows the processes of the WPS entry 
* Monitor GUI adjusted 
* GUI tests removed (in fact the gui should be replaced in the future and the tests are not working on every machine ..) 
* RESTful Interface display option removed.  Metrics are now always shown

## Monitor RESTful Routes
By default the Monitor RESTful Interface is reachable on port 1111.

* List registred WPS: `/measurement/wps` 
* List processes of a WPS: `/measurement/wps/:wpsId`
* List calculated metrics of a WPS Process: `/measurement/wps/:wpdId/process/:processIdentifier/count/:count` 
   * `:count` means the count of measurement values which should be considered for the calculation of metrics

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
| monitorcontrol.deleteTrigger            | TriggerConfig     |
| monitorcontrol.saveTrigger              | TriggerConfig     |

### Events triggered by JobExecutedHandlerThread

| Event identifier                        | Message datatype  |
|-----------------------------------------| ----------------- |
| measurement.wpsjob.wpsexception         | WpsProcessEntity  |
| scheduler.wpsjob.wasexecuted            | WpsProcessEntity  |
| monitorcontrol.pauseMonitoring          | WpsProcessEntity  |

## Client
The Monitor Client uses the RESTful Interface of the monitor to fetch QoSMetrics of WPS Processes of a specific WPS endpoint. The Client is a library which can be used in your project.

###Create a Client instance
To create a new `WpsMonitorClient` instance you can use the `WpsMonitorClientFactory` class.

e.g.
``` java
WpsMonitorClient wpsMonitorClient = new WpsMonitorClientFactory()
                                        .create(new URL("http://example.com:1111/"));
```

### Get Metrics 
You can use the `getWpsProcess(wpsEndpoint : URL, wpsProcessIdentifier : String) : WpsProcessResource` method to get metrics from the monitor.

e.h.
``` java 
WpsProcessResource wpsProcess = client.getWpsProcess(
		new URL("http://example.com/WebProcessingService", 
		"SimpleBuffer"
);

for (final WpsMetricResource metric : wpsProcess.getMetricsAsList()) {
	System.out.println(metric.toString());
}
```

#### Exceptions
The client will throw different exceptions if a WPS or WPS process is found within the monitor. For example:
WPS not found: `WpsMonitorClientWpsNotFoundException`
Wps process not found: `WpsMonitorClientWpsProcessNotFoundException` 

### More Examples
For more examples and a simple Testcase check out the `de.hsos.ecs.richwps.wpsmonitor.client.Main` class which supports a static void main method to test the client with a prepared WpsMonitor.

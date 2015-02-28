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
* [Apache Commons](http://commons.apache.org/)
* [Apache Log4j 2](http://logging.apache.org/log4j/2.x/)

## V2.1 Changelog
* All logging outputs now redirected to log4j to prevent massive spam
* Only Level.ERROR and above events are printed to the console
* CLI added
* Start parameters added
* `ApplicationInfo` class added with some constants like VERSION, PROJECT_SITE, etc.
* `getProcess(endpoint : URL, identifier : String) : WpsProcessEntity` method added to the `MonitorControlService` 
* The RESTful Interface now implements the `java.lang.AutoCloseable` interface
* `Monitor#shutdown()` now also shutdowns the underlying Jetty Webserver


## V2 Changelog
* WPS Identifier removed 
* MeasureJobFactory now caches WpsProcessEntity instances instead of querying the database at every create call 
* MonitorControlService extended and adjusted (endpoint selection instead of wpsIdentifier, selection by wpsId also possible) 
* getWpsId(URL) and getWpsProcessId(URL, String) methods added to the MonitorControlService  to fetch the internal database IDs 
* DataAccess API extended and adjusted (selection by wpsId, endpoint selection instead of wpsIdentifier) 
* wpsUri renamed to endpoint 
* endpoint (previously wpsUri) now saved as String (VARCHAR in the database). This makes it easier to handle URLs in the database. The WpsEntity class encapsulate the String field as URL by getter and setter methods. This changes limit the URL length to 255 characters. I have tried to save the endpoints as URL and URI, but this makes selections impossible, because JPA can't compare CLOB in JPQL statements
* Display parameter removed from ListMeasurementRoute; metrics are always shown 
* ListWpsProcessesRoute added. /measurement/wps/:id now shows the processes of the WPS entry 
* Monitor GUI adjusted 
* GUI tests removed (in fact the gui should be replaced in the future and the tests are not working on every machine ..) 
* RESTful Interface display option removed.  Metrics are now always shown


##System requirements
* Java SE 1.7
* JDK 1.7

##Installation
Clone the repository and build the Application. Move the compiled `WPSMonitor-x.jar` from the target directory to the destination directory and place the `Database` directory relative to the *.jar binary. The *.jar binary needs write rights to their directory to create the logs directory to write the logs and monitor.properties file.

**Example:**
```
./WPSMonitor-2.0.jar
./Database/MonitoredData
./Database/QuartzJobStore
```

##Start and Systemservice script for Linux (Ubuntu)

**Startscript:**
`startmonitor.sh`
```shell
#!/bin/bash
cd /home/monitor/bin/
find . -name "*.lck" -exec rm {} +
nohup java -jar target/WPSMonitor-2.1.jar --ui-type none &

```

**Startservice Script (expected you have an user called `monitor`)**
`/etc/init.d/monitor`:
```shell
#! /bin/sh
user=monitor
bin_dir=/home/monitor/bin
start_log=${bin_dir}/init.log

case "$1" in
    start)
    echo "Starting monitor."
        sudo -u ${user} /home/monitor/startmonitor.sh >> ${start_log}
        ;;
    stop)
    echo "Stoping monitor"
    pgrep -u monitor java | xargs kill    
        ;;
    restart)
    echo "Restarting monitor"
    pgrep -u monitor java | xargs kill
    sudo -u ${user} /home/monitor/startmonitor.sh >> ${start_log}
        ;;
esac

exit 0
```
## Monitor Start Parameters
```
--ui-type <cli|gui|none>
```

* `gui` The monitor starts with the GUI as user interface
* `cli` The monitor starts with the CLI as user interface
* `none` The monitor starts without any UI type

## Monitor CLI
For all commands which needs the --wps parameter (except create), you can also use --wps-id instead if you know about the WPS ID. You can find out the wps id with the show command.

**Command Overview:**
```
create --wps=<endpoint>[ --process=<identifier>[ --trigger={start:<start>, end:<end>, interval:<interval>, type:<type>}]]
add --wps=<endpoint> --process=<identifier> [ --trigger={start:<start>, end:<end>, interval:<interval>, type:<type>} | --request-file=<filepath>]
delete --wps=<endpoint>[ --process=<identifier>[ --only-measured-data [--date=<date>] | --trigger-id=<>]]
show [--wps=<endpoint> --process=<identifier> --triggers]
status --wps=<endpoint> --process=<identifier>
pause --wps=<endpoint> --process=<identifier>
resume --wps=<endpoint> --process=<identifier>
```

###Add Triggers
To add triggers you can use the --trigger parameter of the create or add command. The trigger can be in JSON or in the Simple Trigger Notation. The last of the two possibilities is easier to use.

**Json Example:**
```json
create --wps http://example.com --process SimpleBuffer --trigger '{"intervalType":"SECOND", "start":"Jan 31, 2015 7:20:04 PM", "end":"Feb 19, 2015 7:20:04 PM", "interval":120}'
```

**Simple Trigger Notation (STN) Example:**
```
create --wps http://example.com --process SimpleBuffer --trigger "@Second(120), now, 22.02.2015"
```

The keyword `now` will be replaced with the current time.

**STN general usage:**
```
@second|minute|hour|day|week|month(<interval : integer>), <start : date>, <end : date>
```

###help usage
```
WPSMonitor> help

add		Adds a Process to a already registred WPS, or a Trigger to a already registred WPS Process.

		Options:		
		--process=<identifier> : Specifies which WPS process should be selected by identifier.
		--trigger=<triggerString> : The TriggerConfig Object as JSON string. e.g.
					'{"intervalType":"SECOND|MINUTE|HOUR|DAY|WEEK|MONTH", "start":"Jan 31, 2015 7:20:04 PM",
					"end":"Feb 19, 2015 7:20:04 PM", "interval":2}'
		--wid, --wps-id=<wpsid> : Specifies which WPS should be selected by ID.
		--wps=<endpoint> : Specifies which WPS should be selected by endpoint.
		--rf, --request-file=<requestfile> : Enter a valid path to a file with a xml test request to import.

exit		Exits the Application

		Options:		
		--process=<identifier> : Specifies which WPS process should be selected by identifier.
		--wid, --wps-id=<wpsid> : Specifies which WPS should be selected by ID.
		--wps=<endpoint> : Specifies which WPS should be selected by endpoint.

create		Registers a new WPS, Process or Trigger to a Process in the monitor. If the WPS of thr entered
		Processname does not exists, the WPS will be registred also.

		Options:		
		--process=<identifier> : Specifies which WPS process should be selected by identifier.
		--trigger=<triggerString> : The TriggerConfig Object as JSON string. e.g.
					'{"intervalType":"SECOND|MINUTE|HOUR|DAY|WEEK|MONTH", "start":"Jan 31, 2015 7:20:04 PM",
					"end":"Feb 19, 2015 7:20:04 PM", "interval":2}'
		--wid, --wps-id=<wpsid> : Specifies which WPS should be selected by ID.
		--wps=<endpoint> : Specifies which WPS should be selected by endpoint.

show		shows all WPS and Processes. Can be specified by parameters. --wps to show a list of WPS and
		processes, --wps <endpoint> to show processes of a WPS. --triggers in combination with --process
		<identifier> shows the triggers of the process.If the --triggers parameter missing, the testreques
		will be displayed.

		Options:		
		--process=<identifier> : Specifies which WPS process should be selected by identifier.
		--wid, --wps-id=<wpsid> : Specifies which WPS should be selected by ID.
		--wps=<endpoint> : Specifies which WPS should be selected by endpoint.
		--md, --measured-data : Shows the last 15 measured data of the wps process. This functionallity is only for testing
					purposes.
		--triggers : Prints all Triggers of the specified WPS Process.

status		Displays the current status of a monitored WPS process.

		Options:		
		--process=<identifier> : Specifies which WPS process should be selected by identifier.
		--wid, --wps-id=<wpsid> : Specifies which WPS should be selected by ID.
		--wps=<endpoint> : Specifies which WPS should be selected by endpoint.

delete		Deletes a WPS, Process of a WPS, Trigger of a WPS process or the measured Data of a WPS process. If
		the data option is specified with the onley-measured-data option, all measured data will be deleted
		which are older than <date>.

		Options:		
		--process=<identifier> : Specifies which WPS process should be selected by identifier.
		--d, --date=<date> : Specified the date at which the measured data should be deleted. e.g. 12.03.2012
		--wid, --wps-id=<wpsid> : Specifies which WPS should be selected by ID.
		--wps=<endpoint> : Specifies which WPS should be selected by endpoint.
		--omd, --only-measured-data : Delete only the measured Data of the specified WPS process
		--tid, --trigger-id=<triggerid> : Deletes the trigger with the given ID. type show <wps> <process> --triggers to find out the
					right trigger id.

resume		Resumes the monitoring of a process.

		Options:		
		--process=<identifier> : Specifies which WPS process should be selected by identifier.
		--wid, --wps-id=<wpsid> : Specifies which WPS should be selected by ID.
		--wps=<endpoint> : Specifies which WPS should be selected by endpoint.

pause		Pauses the monitoring of a process.

		Options:		
		--process=<identifier> : Specifies which WPS process should be selected by identifier.
		--wid, --wps-id=<wpsid> : Specifies which WPS should be selected by ID.
		--wps=<endpoint> : Specifies which WPS should be selected by endpoint.

test		Requests a WPS with the testrequest of a file or an already saved testrequest of the specified
		wps/process.

		Options:		
		--process=<identifier> : Specifies which WPS process should be selected by identifier.
		--wid, --wps-id=<wpsid> : Specifies which WPS should be selected by ID.
		--wps=<endpoint> : Specifies which WPS should be selected by endpoint.
		--rf, --request-file=<requestfile> : Enter a valid path to a file with a xml test request to import.

help		Prints all Commands with their descriptions and options.
```

### Create own Commands
For own `MonitorCommand` implementations you need to create an own class which extends the abstract class `MonitorCommand` and implements the `execute`() method. For own options like `--file=<file>` it's necessary to create a non final field like `private String fileName` with the `@CommandOption` annotation.

**Annotation Overview:**
```java 
public @interface CommandOption {
    String shortOptionName();
    String description();
    String longOptionName() default "";
    boolean hasArgument() default false;
    boolean optionalArgument() default false;
    String argumentName() default "";
    boolean required() default false;
}
```

The example below creates an option `--f` (`--file` also possible) with the given description. This option is not required and has an argument with the argumentName "filename". If the command is choosen, the CLI would inject the entered string besides the `--file` option into the `file` field of the `MonitorCommand` class specialisation.
```java
    @CommandOption(
            shortOptionName = "f",
            longOptionName = "file",
            description = "Enter a valid path to a file.",
            hasArgument = true,
            argumentName = "filename"
    )
    private String file;
```

**Complete Example:**
```java
public class ExampleCommand extends MonitorCommand {
    @CommandOption(
            shortOptionName = "f",
            longOptionName = "file",
            description = "Enter a valid path to a file.",
            hasArgument = true,
            argumentName = "filename"
    )
    private String file;
	
    public PauseCommand(final Monitor monitor) {
        super("example", "Only an example command.", monitor);
    }

    @Override
    public void execute() throws CommandException {
		if (file != null) {
			super.consoleProxy.printLine(file);
		} else {
			super.consoleProxy.printLine("File option was empty");
		}
    }
    
}
```

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

### Events triggered by MonitorControlService

| Event identifier                        | Message datatype  |
|-----------------------------------------| ----------------- |
| MonitorControlService.pauseMonitoring          | WpsProcessEntity  |
| MonitorControlService.resumeMonitoring         | WpsProcessEntity  |
| MonitorControlService.deleteProcess            | WpsProcessEntity  |
| MonitorControlService.deleteWps                | WpsEntity         |
| MonitorControlService.updateWps                | WpsEntity         |
| MonitorControlService.setTestRequest           | WpsProcessEntity  |
| MonitorControlService.createAndScheduleProcess | WpsProcessEntity  |
| MonitorControlService.createWps                | WpsEntity         |  
| MonitorControlService.deleteTrigger            | TriggerConfig     |
| MonitorControlService.saveTrigger              | TriggerConfig     |

### Events triggered by JobExecutedHandlerThread

| Event identifier                        | Message datatype  |
|-----------------------------------------| ----------------- |
| measurement.wpsjob.wpsexception         | WpsProcessEntity  |
| scheduler.wpsjob.wasexecuted            | WpsProcessEntity  |
| MonitorControlService.pauseMonitoring          | WpsProcessEntity  |

### Monitor package structure for developers
```
+---boundary
¦   +---cli : The command line interface for the monitor
¦   ¦   ¦   
¦   ¦   +---command : The command API and processing
¦   ¦   ¦   ¦   
¦   ¦   ¦   +---annotation : Annontation processing
¦   ¦   ¦   ¦       
¦   ¦   ¦   +---converter : Command parameter converter API
¦   ¦   ¦   ¦       
¦   ¦   ¦   +---converters : Converter implementations
¦   ¦   ¦           
¦   ¦   +---commands : Command implementations
¦   ¦   ¦       
¦   ¦   +---console : Console implementation which is use by the CLI
¦   ¦           
¦   +---gui : The GUI for the monitor
¦   ¦   ¦   
¦   ¦   +---controls : The controls of the GUI
¦   ¦   ¦           
¦   ¦   +---datasource : Datasource API
¦   ¦   ¦        
¦   ¦   +---datasources : Datasource implementations, e.g. for the SemenaticProxy
¦   ¦           
¦   +---restful : The RESTful Service for the monitor
¦       ¦   
¦       +---metric : Metric API 
¦       ¦       
¦       +---routes : Route implementations
¦       ¦     
¦       +---strategies : Strategy implementations
¦       ¦       
¦       +---strategy : Strategy API
¦               
+---communication : The communication layer of the monitor
¦   ¦ 
¦   +---wpsclient : WPS-client API
¦       ¦   
¦       +---simple : One simple WPS-Client implementation
¦               
+---control : The control layer of the monitor
¦   ¦   
¦   +---event : Event handler system
¦   ¦       
¦   +---scheduler : Sheduler encapsulation
¦   ¦       
¦   +---threadsave : A thread save MonitorControlService implementation
¦           
+---creation : Interfaces and exceptions for the serveral Builder and Factory impelementations
¦       
+---data : The data layer of the monitor (DB abstraction, Config file)
¦   ¦ 
¦   +---config : Config implementation
¦   ¦       
¦   +---dataaccess : DataAccess API
¦   ¦   ¦   
¦   ¦   +---jpa : JPA implementation of the DataAccess API
¦   ¦           
¦   +---entity : Entities of the monitor (JPA based)
¦           
+---measurement : Measurement Layer of the monitor
    ¦   
    +---clean : Cleans all measurements
    ¦       
    +---qos : QOS implementations packge
        ¦   
        +---response : Response metric and measurement implementation
```
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

e.g.
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

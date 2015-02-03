/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsos.ecs.richwps.wpsmonitor.client.exception;

import java.net.URL;

/**
 * This exception indicates if the WpsMonitor is online or offline.
 * 
 * @author Florian Vogelpohl <floriantobias@gmail.com>
 */
public class WpsMonitorOfflineClientException extends WpsMonitorClientException {

    public WpsMonitorOfflineClientException(final URL endpoint) {
        super("Can't reach WpsMonitor at " + endpoint.toString());
    }
    
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsos.ecs.richwps.wpsmonitor.client.exception;

import java.net.URL;

/**
 *
 * @author Florian Vogelpohl <floriantobias@gmail.com>
 */
public class WpsMonitorClientWpsProcessNotFoundException extends WpsMonitorClientException {

    public WpsMonitorClientWpsProcessNotFoundException(final String processIdentifier, final URL wpsEndpoint) {
        super("The Process \"" + processIdentifier + "\" of WPS \"" + wpsEndpoint.toString() + "\" was not found within the WPSMonitor.");
    }

}

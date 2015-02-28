/*
 * Copyright 2014 Florian Vogelpohl <floriantobias@gmail.com>.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.hsos.ecs.richwps.wpsmonitor.util;

import org.apache.http.util.Args;

/**
 * Class which many validation methods.
 * 
 * @author Florian Vogelpohl <floriantobias@gmail.com>
 */
public class Validate {

    /**
     * Checks if a Parameter is null.
     *
     * @param <T> Return type
     * @param var Check if null
     * @param name Outputname for exception message
     * @return T
     */
    public static <T> T notNull(final T var, final String name) {
        Args.notNull(var, name);

        return var;
    }

    /**
     * Checks if one of the Parameters is null.
     * 
     * @param vars List of Parameters
     */
    public static void notNull(final Object... vars) {
        for (Object var : vars) {
            if (var == null) {
                throw new IllegalArgumentException("One of the given Parameters was null.");
            }
        }
    }

    /**
     * Checks the maximal length of a String.
     *
     * @param var String instance to validate
     * @param count max allowed characters
     * @return var
     */
    public static String max(final String var, final Long count) {
        if (var.length() > count) {
            throw new IllegalArgumentException("The given String is longer as allowed (" + count + ")");
        }

        return var;
    }

    /**
     * Checks if the given string var is matched the given regex.
     * 
     * @param var
     * @param regex
     * @return var
     */
    public static String matchesRegex(final String var, final String regex) {
        if (!var.matches(regex)) {
            throw new IllegalArgumentException("The given String has not the right format.");
        }

        return var;
    }

    /**
     * Checks the minimal length of a String.
     *
     * @param var String instance to validate
     * @param count minimal allowed characters
     * @return var
     */
    public static String min(final String var, final Long count) {
        if (var.length() < count) {
            throw new IllegalArgumentException("The given String is shorter as allowed (" + count + ").");
        }

        return var;
    }

    /**
     * Checks if the given String is not Empty
     *
     * @param var String to validate
     * @return var
     */
    public static String notEmpty(final String var) {
        return notEmpty(var, "");
    }
    
    /**
     * Checks if the given String is not Empty
     *
     * @param var String to validate
     * @param varName Name of the variable
     * @return var
     */
    public static String notEmpty(final String var, final String varName) {
        String msg = "The given String has no content.";
        
        if(varName != null) {
            msg = "The given String \"" + varName +"\" has no content.";
        }
        
        if (var == null || var.isEmpty()) {
            throw new IllegalArgumentException(msg);
        }

        return var;
    }

    /**
     * Checks if t is true.
     *
     * @param t
     * @param name Outputname for exception message
     */
    public static void isTrue(final Boolean t, final String name) {
        notNull(t, name);

        if (!t) {
            throw new IllegalArgumentException(name + " was not valid.");
        }
    }

    private Validate() {

    }
}

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
package de.hsos.ecs.richwps.wpsmonitor.boundary.restful.metric.units;

/**
 *
 * @author Florian Vogelpohl <floriantobias@gmail.com>
 */
public class MeasureUnit {
    public enum Unit { 
        BYTE("b", "byte"), 
        SECOND("s", "time");
        
        private final String unitSymbol;
        private final String quantityName;
        
        Unit(final String unitSymbol, final String quantityName) {
            this.unitSymbol = unitSymbol;
            this.quantityName = quantityName;
        }

        public String getUnitSymbol() {
            return unitSymbol;
        }

        public String getQuantityName() {
            return quantityName;
        }
        
        @Override
        public String toString() {
            return unitSymbol;
        }
    };
    
    /**
     * Decimal Prefix, Basis 10
     */
    public enum Prefix {
        MEGA(6, "M"),
        KILO(3, "k"),
        NONE(0, ""),
        MILLI(-3, "m"),
        MICRO(-6, "µ");
        
        private final Integer exponent;
        private final String symbol;
        
        Prefix(final Integer exponent, final String symbol) {
            this.exponent = exponent;
            this.symbol = symbol;
        }

        public Integer getExponent() {
            return exponent;
        }

        public String getSymbol() {
            return symbol;
        }
        
        public Double calculate(final Prefix p, final Double value) {
            return Math.pow(value, p.getExponent());
        }
        
        public Integer calculate(final Prefix p, final Integer value) {
            return calculate(p, value.doubleValue()).intValue();
        }
        
        @Override
        public String toString() {
            return symbol;
        }
    }
    
    private final Prefix prefix;
    private final Unit unit;

    public MeasureUnit(final Prefix p, final Unit u) {
        this.prefix = p;
        this.unit = u;
    }

    public Prefix getPrefix() {
        return prefix;
    }

    public Unit getUnit() {
        return unit;
    }
    
    @Override 
    public String toString() {
        return prefix.toString() + unit.toString();
    }
}

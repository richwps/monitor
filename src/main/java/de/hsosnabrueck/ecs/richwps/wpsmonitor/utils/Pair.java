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
package de.hsosnabrueck.ecs.richwps.wpsmonitor.utils;

/**
 * A simple Pair implementation.
 * 
 * @author Florian Vogelpohl <floriantobias@gmail.com>
 */
public class Pair<A, B> {

    private final A a;
    private final B b;

    public Pair(final A left, final B right) {
        this.a = Param.notNull(left, "left");
        this.b = Param.notNull(right, "right");
    }

    public A getLeft() {
        return a;
    }

    public B getRight() {
        return b;
    }

    @Override
    public String toString() {
        return a.toString() + "." + b.toString();
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 67 * hash + (this.a != null ? this.a.hashCode() : 0);
        hash = 67 * hash + (this.b != null ? this.b.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Pair<?, ?> other = (Pair<?, ?>) obj;
        if (this.a != other.a && (this.a == null || !this.a.equals(other.a))) {
            return false;
        }
        if (this.b != other.b && (this.b == null || !this.b.equals(other.b))) {
            return false;
        }
        return true;
    }
}

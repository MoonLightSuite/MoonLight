/*
 * MoonLight: a light-weight framework for runtime monitoring
 * Copyright (C) 2018 
 *
 * See the NOTICE file distributed with this work for additional information
 * regarding copyright ownership.  
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

package eu.quanticol.moonlight.formula;

/**
 * Abstract immutable data type that represents an interval over parameter T.
 *
 * In general, to define an interval over a set, we only need a
 * total ordering relation defined over that set.
 * For this reason, I am requiring Intervals to be defined over a type T
 * that implements a Comparable interface (of any extension of that).
 * The Comparable interface is slightly stricter than the general Interval
 * interface, as it requires the
 *
 * @param <T> Type of the interval (currently only numbers make sense)
 *
 * @see Interval for the implementation on Doubles
 */
public abstract class AbstractInterval<T extends Comparable<T>>
        implements Comparable<AbstractInterval<T>>
{
    private final T start;
    private final T end;
    private final boolean openOnRight;
    private final boolean openOnLeft;

    /**
     * Constructs an interval of any kind between start and end
     * @param start left bound of the interval
     * @param end right bound of the interval
     * @param openOnLeft marks whether the left bound is included or not
     * @param openOnRight marks whether the right bound is included or not
     */
    public AbstractInterval(T start, T end, boolean openOnLeft, boolean openOnRight) {
        this.start = start;
        this.end = end;
        this.openOnLeft = openOnLeft;
        this.openOnRight = openOnRight;
    }

    /**
     * Constructs a degenerated interval that contains only the provided number
     * @param value the only number included in the interval
     * @return an interval of the kind [value, value]
     */
    public abstract AbstractInterval<T> fromValue(T value);

    /**
     * @return an empty interval, i.e. the smallest possible interval
     */
    public abstract AbstractInterval<T> empty();

    /**
     * @return the widest possible interval
     */
    public abstract AbstractInterval<T> any();

    /**
     * @return the left bound of the interval
     */
    public T getStart() {
        return start;
    }

    /**
     * @return the right bound of the interval
     */
    public T getEnd() {
        return end;
    }

    /**
     * Checks whether the passed value belongs to the interval
     * @param value the value to be checked
     * @return true if the value belongs to the interval, false otherwise.
     */
    public boolean contains(T value) {
        if (value != null)
            return (value.compareTo(start) > 0 && value.compareTo(end) < 0)
                || (value.equals(start) && !openOnLeft)
                || (value.equals(end) && !openOnRight);

        return false;
    }

    /**
     * @return tells whether the interval is empty or not
     */
    public boolean isEmpty() {
        return start.equals(end) && (openOnLeft || openOnRight);
    }

    /**
     * @return tells whether the right bound is included in the interval or not
     */
    public boolean isOpenOnRight() {
        return openOnRight;
    }

    /**
     * @return tells whether the left bound is included in the interval or not
     */
    public boolean isOpenOnLeft() {
        return openOnLeft;
    }

    /**
     * Note that in classical interval arithmetic no total ordering
     * relation is defined over intervals. Nevertheless
     * 1 - o is null => error
     * 2 - o is empty => 0 (?)
     * 3 - [o,o] => translate & compare //removed
     * 4 - [o.inf, o.sup] => this.inf > o.sup
     * @param i target interval for the comparison
     * @return a number corresponding to the result of the comparison
     */
    @Override
    public int compareTo(AbstractInterval<T> i) {
        if(i == null) {
            throw new UnsupportedOperationException("Comparing Interval " +
                                                    "to null");
        }
        if(i.getStart() == i.getEnd() &&
                (isOpenOnLeft() || isOpenOnRight())) {
            return 0;
        }
        if(this.contains(i.getStart())) {
            return getStart().compareTo(i.getEnd());
        }

        throw new UnsupportedOperationException("Unable to compare Interval " +
                                                "with " + i.toString());
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        long temp;
        temp = end.hashCode();
        result = prime * result + (int) (temp ^ (temp >>> 32));
        result = prime * result + (openOnRight ? 1231 : 1237);
        temp = start.hashCode();
        result = prime * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        AbstractInterval<T> other = (AbstractInterval<T>) obj;
        if (!end.equals(other.end))
            return false;
        if (openOnLeft != other.openOnLeft || openOnRight != other.openOnRight)
            return false;
        if (!start.equals(other.start))
            return false;
        return true;
    }

    /**
     * @see Object#toString()
     */
    @Override
    public String toString() {
        String output = "Interval: ";

        if(openOnLeft)
            output += "(";
        else
            output += "[";

        output += start + ", " + end;

        if(openOnRight)
            output += ")";
        else
            output += "]";

        return output;
    }
}
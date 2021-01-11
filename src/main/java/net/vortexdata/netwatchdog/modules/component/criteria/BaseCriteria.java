/*
 * MIT License
 *
 * Copyright (c) 2020 VortexdataNET
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package net.vortexdata.netwatchdog.modules.component.criteria;

import net.vortexdata.netwatchdog.exceptions.CriteriaAlreadyInUseException;
import net.vortexdata.netwatchdog.exceptions.CriteriaLevelInUseException;
import net.vortexdata.netwatchdog.exceptions.InvalidCriteriaValueException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 * Base Criteria class automatically containing all shared criteria.
 * Every custom component subclass should have its own BaseCriteria
 * derivative.
 *
 * @author  Sandro Kierner
 * @since 0.0.0
 * @version 0.0.0
 */
public abstract class BaseCriteria {

    public static String[] BASE_CRITERIA_VALUES = {
            "RESPONSE_TIME",
            "RESPONSE_BODY"
    };

    protected ArrayList<String> criteria;
    protected ArrayList<String> validCriteriaValues;

    public BaseCriteria() {
        this(false);
    }

    public BaseCriteria(boolean omitBaseCriteriaAddition) {
        criteria = new HashMap<>();
        validCriteriaValues = new ArrayList<>();
        if (omitBaseCriteriaAddition)
            return;

        validCriteriaValues.addAll(Arrays.asList(BASE_CRITERIA_VALUES));
    }


    public boolean setCriteria(String value, int level) throws CriteriaLevelInUseException, InvalidCriteriaValueException, CriteriaAlreadyInUseException {

        if (criteria.values().contains(level)) {

        }

        if (criteria.get(value) != null)
            throw new CriteriaAlreadyInUseException();

        if (!validCriteriaValues.contains(value))
            throw new InvalidCriteriaValueException();

        criteria.put(value, level);
        return true;
    }

}

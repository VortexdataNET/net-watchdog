/*
 * NET Watchdog
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

package net.vortexdata.netwatchdog;

import net.vortexdata.netwatchdog.modules.component.ComponentManager;
import net.vortexdata.netwatchdog.utils.VersionUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


public class VersionUtilsTest {

    @Test
    public void testTagValidity1() {
        assertTrue(VersionUtils.isVersionTagValid("1.1.1"));
    }

    @Test
    public void testTagValidity2() {
        assertFalse(VersionUtils.isVersionTagValid("-6.4.0"));
    }

    @Test
    public void testTagValidity3() {
        assertFalse(VersionUtils.isVersionTagValid("0.0.0.0"));
    }

    @Test
    public void testTagValidity4() {
        assertFalse(VersionUtils.isVersionTagValid("0.0.0.0a"));
    }

    @Test
    public void testTagValidity5() {
        assertFalse(VersionUtils.isVersionTagValid("0.0"));
    }

    @Test
    public void testHigherTag() {
        String tag1 = "1.5.1";
        String tag2 = "0.4.1";
        assertEquals(1, VersionUtils.compareVersionTags(tag1, tag2));
    }

    @Test
    public void testEqualTag() {
        String tag1 = "1.5.1";
        String tag2 = "1.5.1";
        assertEquals(0, VersionUtils.compareVersionTags(tag1, tag2));
    }

    @Test
    public void testLowerTag() {
        String tag1 = "1.5.0";
        String tag2 = "1.5.1";
        assertEquals(-1, VersionUtils.compareVersionTags(tag1, tag2));
    }

}

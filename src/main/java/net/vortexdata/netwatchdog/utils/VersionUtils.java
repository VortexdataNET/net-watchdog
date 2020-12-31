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

package net.vortexdata.netwatchdog.utils;

/**
 * Utils class used to compare and evaluate Semantic Versioning 2.0.0 version tags.
 *
 * @author          Sandro Kierner
 * @since 0.0.1
 * @version 0.2.0
 */
public class VersionUtils {

    /**
     * Checks validity of given version tag.
     *
     * @param   tag     Tag as string.
     * @return          true if tag is valid, false if not.
     */
    public static boolean isVersionTagValid(String tag) {
        String[] split = tag.split("\\.");
        if (split.length != 3) return false;

        for (int i = 0; i < split.length; i++) {
            try {
                int parsed = Integer.parseInt(split[i]);
                if (parsed < 0) return false;
            } catch (Exception e) {
                return false;
            }
        }
        return true;
    }

    /**
     * Compares version tags and works out if parameter <code>tag1</code> is
     * equal to, newer or older than parameter <code>tag2</code>.
     *
     * @param   tag1    Tag as string.
     * @param   tag2    Tag as string.
     * @return          <code>0</code> if tags are equal;
     *                  <code>1</code> if tag1 is newer/higher than tag2;
     *                  <code>-1</code> if tag1 is older/lower than tag2;
     */
    public static int compareVersionTags(String tag1, String tag2) {
        String[] level1 = tag1.split("\\.");
        String[] level2 = tag2.split("\\.");

        //get the larger length
        int largerLength = Math.max(level1.length, level2.length);

        //iterate over this larger length
        for (int i = 0; i < largerLength; i++) {
            Integer val1 = i < level1.length ? Integer.parseInt(level1[i]) : 0;
            Integer val2 = i < level2.length ? Integer.parseInt(level2[i]) : 0;

            Integer res = val1.compareTo(val2);

            if (res != 0) {
                return res;
            }

        }

        return 0;

    }

}

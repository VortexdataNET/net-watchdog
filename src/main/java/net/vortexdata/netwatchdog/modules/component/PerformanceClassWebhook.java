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

package net.vortexdata.netwatchdog.modules.component;

import java.util.HashMap;

/**
 * Performance class webhook wrapper class.
 *
 * @author  Sandro Kierner
 * @since 0.0.1
 * @version 0.2.0
 */
public class PerformanceClassWebhook {

    private final String address;
    private final HashMap<String, String> headers;
    private final String body;

    public PerformanceClassWebhook(String address, HashMap<String, String> headers, String body) {
        this.address = address;
        this.headers = headers;
        this.body = body;
    }

    public String getBody() {
        return body;
    }

    public String getAddress() {
        return address;
    }

    public HashMap<String, String> getHeaders() {
        return headers;
    }
}

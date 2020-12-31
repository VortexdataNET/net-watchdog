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

package net.vortexdata.netwatchdog.modules.northstar;

import java.io.IOException;
import java.net.Socket;

/**
 * Socket Northstar class.
 *
 * @author Sandro Kierner
 * @version 0.2.0
 * @since 0.1.0
 */
public class SocketNorthstar extends NorthstarBase {

    private int port;

    public SocketNorthstar(NorthstarRegister northstarRegister, String address, int timeout, int port) {
        super(northstarRegister, address, timeout, 1);
        this.port = port;
    }

    @Override
    public boolean isAvailable() {

        long start = System.currentTimeMillis();

        try {
            Socket s = new Socket(address, port);
            s.close();
            long end = System.currentTimeMillis();
            if (end-start >= timeout)
                return false;
            return true;
        } catch (IOException e) {
            return false;
        }
    }

}

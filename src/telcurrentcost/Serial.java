/*
 * Copyright (C) 2011 Michael Turner <michael at turnerendlesslearning.com>
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */

package telcurrentcost;

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Serial {

    public Serial() {
        super();
    }

    void connect(String portName, DataSentListener dsl) throws Exception {
        CommPortIdentifier portIdentifier = CommPortIdentifier.getPortIdentifier(portName);
        if (portIdentifier.isCurrentlyOwned()) {
            System.out.println("Error: Port is currently in use");
        } else {
            CommPort commPort = portIdentifier.open(this.getClass().getName(), 2000);

            if (commPort instanceof SerialPort) {
                SerialPort serialPort = (SerialPort) commPort;
                serialPort.setSerialPortParams(57600, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);

                InputStream in = serialPort.getInputStream();

                SerialReader sr = new SerialReader(in);
                sr.addDataSentListener(dsl);

                (new Thread(sr)).start();
            } else {
                System.out.println("Error: Only serial ports are handled by this.");
            }
        }
    }

    /** */
    public static class SerialReader implements Runnable {

        InputStream in;
        private List _listeners = new ArrayList();
        public synchronized void addDataSentListener( DataSentListener l ) {
            _listeners.add( l );
        }
        public synchronized void removeDataSentListener( DataSentListener l ) {
            _listeners.remove( l );
        }
        private synchronized void _fireDataSentEvent(String data) {
            DataSentEvent dse = new DataSentEvent(this, data);
            Iterator listeners = _listeners.iterator();
            while (listeners.hasNext()) {
                ((DataSentListener) listeners.next()).dataReceived(dse);
            }
        }

        public SerialReader(InputStream in) {
            this.in = in;
        }

        public void run() {
            byte[] buffer = new byte[1024];
            StringBuilder sb = new StringBuilder();
            int len = -1;
            try {
                while ((len = this.in.read(buffer)) > -1) {
                    String read = new String(buffer, 0, len);
                    sb.append(read);
                    if(read.charAt(read.length() - 1) == '\n') {
                        _fireDataSentEvent(sb.toString());
                        sb = new StringBuilder();
                    }
                }
            } catch (IOException e) {
                System.out.println(e.getStackTrace());
            }
        }
    }
}
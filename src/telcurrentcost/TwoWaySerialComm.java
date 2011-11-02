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

import gnu.io.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * This version of the TwoWaySerialComm example makes use of the
 * SerialPortEventListener to avoid polling.
 *
 */
public class TwoWaySerialComm
{
    public TwoWaySerialComm()
    {
        super();
    }

    void connect ( String portName, DataSentListener dsl ) throws Exception
    {
        CommPortIdentifier portIdentifier = CommPortIdentifier.getPortIdentifier(portName);
        if ( portIdentifier.isCurrentlyOwned() )
        {
            System.out.println("Error: Port is currently in use");
        }
        else
        {
            CommPort commPort = portIdentifier.open(this.getClass().getName(),2000);

            if ( commPort instanceof SerialPort )
            {
                SerialPort serialPort = (SerialPort) commPort;
                serialPort.setSerialPortParams(57600,SerialPort.DATABITS_8,SerialPort.STOPBITS_1,SerialPort.PARITY_NONE);

                InputStream in = serialPort.getInputStream();
                OutputStream out = serialPort.getOutputStream();

                (new Thread(new SerialWriter(out))).start();

                SerialReader sr = new SerialReader(in);
                sr.addDataSentListener(dsl);

                serialPort.addEventListener(sr);
                serialPort.notifyOnDataAvailable(true);

            }
            else
            {
                System.out.println("Error: Only serial ports are handled by this example.");
            }
        }
    }

    /**
     * Handles the input coming from the serial port. A new line character
     * is treated as the end of a block in this example.
     */
    public static class SerialReader implements SerialPortEventListener
    {
        private InputStream in;
        private byte[] buffer = new byte[1024];
        private List _listeners = new ArrayList();

        public SerialReader (InputStream in)
        {
            this.in = in;
        }
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
        public void serialEvent(SerialPortEvent arg0) {
            int data;
            StringBuilder sb = new StringBuilder();

            try {
                while ((data = in.read()) > -1) {
                    if ( data == '\n' ) {
                        break;
                    }
                    sb.append((char)data);
                }
                _fireDataSentEvent(sb.toString());

            }
            catch (IOException e) {
                System.out.println(e.getStackTrace());
                //System.exit(-1);
            }
        }

    }

    /** */
    public static class SerialWriter implements Runnable
    {
        OutputStream out;

        public SerialWriter ( OutputStream out )
        {
            this.out = out;
        }

        public void run ()
        {
            try
            {
                int c = 0;
                while ( ( c = System.in.read()) > -1 )
                {
                    this.out.write(c);
                }
            }
            catch ( IOException e )
            {
                System.out.println(e.getStackTrace());
                System.exit(-1);
            }
        }
    }



    public static void main ( String[] args )
    {
        try
        {
            (new TwoWaySerialComm()).connect("COM3", null);
        }
        catch ( Exception e )
        {
            System.out.println(e.getStackTrace());
        }
    }
}
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

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * @author Michael Turner
 */
public class Weather implements Runnable {
    public URL url;
    public float temp;
    public Date updated;
    private boolean killSwitch = false;
    private boolean loaded = false;

    public Weather(String _url) {
        try {
            url = new URL(_url);
        } catch (MalformedURLException ex) {

            Logger.getLogger(Weather.class.getName()).log(Level.SEVERE, null, ex);
        }


    }
    public float getTemp() {
        return this.temp;
    }
    public Date getUpdated() {
        return this.updated;
    }
    public void loadTemp() {
        try {
            Document doc = XMLParser.parseXmlFromUrl(url);
            NodeList nl = doc.getElementsByTagName("temp_f");
            if(nl.getLength() > 0) {
                Node n = nl.item(0);
                temp = Float.valueOf(n.getTextContent());
                Calendar c = Calendar.getInstance();
                updated = c.getTime();
                this.loaded = true;
            }
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }
    public boolean isLoaded() {
        return this.loaded;
    }
    public void kill() {
        this.killSwitch = true;
    }

    public void run() {
        while(!this.killSwitch) {
            loadTemp();
            try {
                Thread.sleep(600000);
            } catch (InterruptedException ex) {
                Logger.getLogger(Weather.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}

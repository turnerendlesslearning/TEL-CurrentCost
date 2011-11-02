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

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LinuxCommand {
    public static void run(String data) {
        ProcessBuilder pb = new ProcessBuilder("rrdtool", "update", 
                System.getProperty("user.home") + "/currentcost/powertemp.rrd", data);
        try {
            Process p = pb.start();
        } catch (IOException ex) {
            Logger.getLogger(LinuxCommand.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}


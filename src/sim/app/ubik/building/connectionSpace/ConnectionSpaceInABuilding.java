/*
 * UbikSim2 has been developed by:
 * 
 * Juan A. Botía , juanbot[at] um.es
 * Pablo Campillo, pablocampillo[at] um.es
 * Francisco Campuzano, fjcampuzano[at] um.es
 * Emilio Serrano, emilioserra [at] dit.upm.es
 * 
 * This file is part of UbikSimIDE.
 * 
 *     UbikSimIDE is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 * 
 *     UbikSimIDE is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 * 
 *     You should have received a copy of the GNU General Public License
 *     along with UbikSimIDE.  If not, see <http://www.gnu.org/licenses/>
 */
package sim.app.ubik.building.connectionSpace;

import sim.app.ubik.Ubik;
import sim.app.ubik.building.SpaceArea;
import sim.util.Int2D;


public abstract class ConnectionSpaceInABuilding extends SpaceArea {

    protected float angle;
    protected Int2D[] accessPoints;
    protected boolean opened;
    protected boolean locked;

    public ConnectionSpaceInABuilding(int floor, String name, Ubik ubik, float angle, float[][] points) {
        super(floor, name, ubik, points);
        this.angle = (float) (angle - Math.PI / 2.0);
        createAccessPoints();
        this.opened = true;
        this.locked = false;
    }

    public abstract void createAccessPoints();

    public Int2D[] getAccessPoints() {
        return accessPoints;
    }

    public float getAngle() {
        return angle;
    }

    public Int2D getNearestAccessPointFrom(Int2D p) {
        return getNearestAccessPointFrom(p.x, p.y);
    }

    public Int2D getNearestAccessPointFrom(int x, int y) {
        Int2D result = null;
        double min = Double.MAX_VALUE;
        for (int i = 0; i < accessPoints.length; i++) {
            double distance = accessPoints[i].distance(x, y);
            if (distance < min) {
                result = accessPoints[i];
                min = distance;
            }
        }
        return result;
    }

    public Int2D getFarestAccessPointFrom(Int2D p) {
        return getFarestAccessPointFrom(p.x, p.y);
    }

    public Int2D getFarestAccessPointFrom(int x, int y) {
        Int2D result = null;
        double maxDistance = 0.0;
        for (int i = 0; i < accessPoints.length; i++) {
            double distance = accessPoints[i].distance(x, y);
            if (distance > maxDistance) {
                maxDistance = distance;
                result = accessPoints[i];
            }
        }
        return result;
    }

    public boolean isInAccessPoint(int x, int y) {
        for (int i = 0; i < accessPoints.length; i++) {
            if (x == accessPoints[i].x && y == accessPoints[i].y) {
                return true;
            }
        }
        return false;
    }

    public boolean isLocked() {
        return locked;
    }

    public boolean isOpened() {
        return opened;
    }

    public void open(){}
    public void close(){}
    public void lock(){}
    public void unlock(){}
}

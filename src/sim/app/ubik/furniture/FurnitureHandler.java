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
package sim.app.ubik.furniture;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import sim.app.ubik.Ubik;
import sim.app.ubik.building.Cell;
import sim.app.ubik.building.SpaceArea;
import sim.app.ubik.building.rooms.Room;
import sim.app.ubik.ocp.OCPProxyProducer;
import sim.app.ubik.utils.ElementsHandler;
import sim.field.grid.SparseGrid2D;
import sim.util.Bag;
import sim.util.Int2D;
import ubik3d.model.Home;
import ubik3d.model.HomePieceOfFurniture;



public class FurnitureHandler extends ElementsHandler {
	private String file = "handlers/furnitures.txt";
    protected List<Furniture> furnitures;
    protected OCPProxyProducer ocp;
    private static final Logger LOG = Logger.getLogger(FurnitureHandler.class.getName());

    public FurnitureHandler(Ubik ubik, int floor, int GRID_WIDTH, int GRID_HEIGHT) {
    	super(ubik, floor, GRID_WIDTH, GRID_HEIGHT);

    	initRols(file);
        furnitures = new ArrayList<Furniture>();

        this.ocp = ubik.createOCPProxyProducer("UBIK.FurnitureHandler");
    }

    public void add(Furniture p) {
        furnitures.add(p);
    }

    public List<Furniture> getFurnituresInstanceOf(Class clase) {
        List<Furniture> result = new ArrayList();
        for (Furniture p : furnitures) {
            if (clase.isInstance(p)) {
                result.add(p);
            }
        }
        return result;
    }

    public void clear() {
        furnitures.clear();
        grid.clear();
    }

    public void generateFurniture(int floor, int cellSize) {
        LOG.info("Generating furniture");
        Home home = ubik.getBuilding().getFloor(floor).getHome();
        for (HomePieceOfFurniture hpof : home.getFurniture()) {            
            if (rols.contains(hpof.getRol())) {
            	LOG.config(("Furniture: " + hpof.getRol() + ", " + hpof.getName()));
                if (hpof.getAmount() <= 1) {
                    Furniture f = createFurniture(floor, hpof, ubik);
                    if (f != null) {
                        furnitures.add(f);
                        // añadir a la habitación que pertenece
                        SpaceArea sa = ubik.getBuilding().getFloor(floor).getSpaceAreaHandler().getSpaceArea(f.getCenter().getX(), f.getCenter().getY());
                        if(sa instanceof Room) {
                            ((Room)sa).addFurniture(f);
                        }
                        Int2D[] positions = f.getPositions();
                        for (Int2D p : positions) {
                            grid.removeObjectsAtLocation(p);
                            grid.setObjectLocation(new Cell(f, p), p);
                        }
                    }
                } else {
                    home.deletePieceOfFurniture(hpof);

                    for (int i = 0; i < hpof.getAmount(); i++) {
                        HomePieceOfFurniture piece = new HomePieceOfFurniture(hpof);
                        Furniture f = createFurniture(floor, piece, ubik);
                        if (f != null) {
                            home.addPieceOfFurniture(piece);
                            furnitures.add(f);                            
                        }
                    }
                }
            }
        }
    }

    protected Furniture createFurniture(int floor, HomePieceOfFurniture piece, Ubik ubik) {
        Furniture result = null;

        try {
            for (Constructor c : Class.forName("sim.app.ubik.furniture." + piece.getRol()).getConstructors()) {
                try {
                    result = (Furniture) c.newInstance(floor, ubik, piece);
                } catch (InstantiationException ex) {
                    System.out.println("Error1");
                    continue;
                } catch (IllegalAccessException ex) {
                    System.out.println("Error2");
                    continue;
                } catch (IllegalArgumentException ex) {
                    System.out.println("Error3");
                    continue;
                } catch (InvocationTargetException ex) {
                    System.out.println("Error4\n" + ex);
                    continue;
                }
                break;
            }
        } catch (ClassNotFoundException ex) {
        }

        if (result != null) {
            if (ocp != null) {
                result.createInOCP(ocp.getContextService());
            }
        }

        return result;
    }

    public List<Furniture> getFurnitures() {
        return furnitures;
    }

    @Override
	public SparseGrid2D getGrid() {
        return grid;
    }

    @Override
	public int getFloor() {
        return floor;
    }

	public Furniture getFurniture(int x, int y) {
		if (x < grid.getWidth() && y < grid.getHeight()) {
            Bag b = grid.getObjectsAtLocation(x, y);
            if (b != null && b.size() > 0) {
                Cell cell = (Cell) b.get(0);
                if (cell.getValue() instanceof Furniture) {
                    return (Furniture) cell.getValue();
                }
            }
        }
        return null;
	}
}

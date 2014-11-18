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
package sim.app.ubik.building;

import sim.util.Int2D;

/**
 * Contiene todas la representación de una celda y los distintos valores que puede tener.
 * Hay objetos sala, puerta y planta, pero esos no se representan ni el usuario interactua con ellos, no estan incluidos en ningún "field" de MASON
 * Tanto la clase cell como fire, las que ocupan todo el espacio, son críticas en cuanto a memoria. Por eso se debe potenciar el uso de variables estaticas de calse
 * y los calculos frente a los almacenamientos
 * Esta clase es muy buen lugar para depurar la programación del espacio,  se cologan metodos get que puedes ver en el visor de mason
 */
public class Cell {
    private Int2D position;
    private Object value;

    public Cell(Object value, Int2D position) {
        this.value = value;
        this.position = position;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public Int2D getPosition() {
        return position;
    }
}

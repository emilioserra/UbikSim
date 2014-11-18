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
package sim.app.ubik.ocp;

import java.util.Hashtable;
import java.util.logging.Level;
import java.util.logging.Logger;
import ocp.service.ContextProducer;
import ocp.service.ContextService;
import ocpClient.OCPService;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;


public class OCPProxyProducer implements ContextProducer {
    protected BundleContext bundleContext;
    protected String id;
    protected ContextService cs;
    protected  ServiceRegistration sr;
    protected final Object MUTEX = new Object();

    public OCPProxyProducer(BundleContext bundleContext, String id) {
        this.bundleContext = bundleContext;
        this.id = id;
        Hashtable props = new Hashtable();
        props.put("id", id);
        sr = bundleContext.registerService(ContextProducer.class.getName(), this, props);
    }

    public OCPProxyProducer(String id, String url) {
        this.id = id;
        try {
            cs = new OCPService(this, url);
            System.out.println(id+": Establecida conexión con servidor");
        } catch (Exception e) {
            System.out.println(id+": Establecida conexión con servidor");
        }
    }

    @Override
	public void activate(ContextService cs) {
        synchronized (MUTEX) {
            this.cs = cs;
            MUTEX.notifyAll();
        }
    }

    @Override
	public void deactivate() {
        synchronized (MUTEX) {
            cs = null;
            MUTEX.notifyAll();
        }
    }

    public ContextService getContextService() {
        try {
            waitForServiceActive();
        } catch (InterruptedException ex) {
            Logger.getLogger(OCPProxyProducer.class.getName()).log(Level.SEVERE, null, ex);
        }
        return cs;
    }

    /**
     * Comprobamos que el objeto cs de acceso al servicio no sea nulo
     * @return cs
     */
    public void waitForServiceActive() throws InterruptedException {
        synchronized (MUTEX) {
            if (cs == null) {
                System.out.println("[" + id + "] " + ContextService.class.getName() + ": Servicio no activo. Esperamos activación");
                MUTEX.wait();
                System.out.println("[" + id + "] " + ContextService.class.getName() + ": Servicio activo.");
            }
        }
        if (cs == null) {
            System.out.println("[" + id + "]: ContextService null y habiamos salido del wait() !!!!");
        }
    }

    @Override
	public String getId() {
        return id;
    }
}

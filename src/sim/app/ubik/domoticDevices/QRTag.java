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
package sim.app.ubik.domoticDevices;

import java.awt.Image;
import java.net.MalformedURLException;
import java.net.URL;

import ocp.service.ContextService;
import sim.app.ubik.Ubik;
import sim.engine.Schedule;
import sim.engine.SimState;
import ubik3d.model.Content;
import ubik3d.model.HomePieceOfFurniture;
import ubik3d.model.TextureImage;
import ubik3d.tools.URLContent;


public class QRTag extends FixedDomoticDevice {
	private String data;	
	
	public QRTag(int floor, HomePieceOfFurniture device3DModel, Ubik ubik) {
		super(floor, device3DModel, ubik);

		data = device3DModel.getMetadata();
		/*TextureImage ti = new QRTexture(device3DModel);		
		HomeTexture texture = new HomeTexture(ti);
		device3DModel.setTexture(texture);*/	
	}

	private class QRTexture implements TextureImage {
		private HomePieceOfFurniture hpof;
		private Image image;
		String imageURL;
		
		public QRTexture(HomePieceOfFurniture hpof) {
			this.hpof = hpof;			 
			imageURL = data.substring(0, data.length() - 3) + "png";
		}

		@Override
		public float getHeight() {
			return device3DModel.getHeight();
		}

		@Override
		public Content getImage() {
			try {
				System.out.println("*************>>>>> "+imageURL);
				return new URLContent(new URL(imageURL));
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}
		}

		@Override
		public String getName() {
			// TODO Auto-generated method stub
			return "x";
		}

		@Override
		public float getWidth() {
			return device3DModel.getWidth();
		}
	};

	@Override
	public void createInOCP(ContextService cs) {
		super.createInOCP(cs);
	}

	@Override
	public void step(SimState state) {
	}

	@Override
	public void stop() {
	}

	@Override
	public void fixStopable(Schedule schedule) {
	}

	public String getData() {
		return data;
	}

	/*
	public static String getDataFromUrl(String dir) throws IOException {

		StringBuffer b = new StringBuffer();
		InputStream is = null;
		HttpURLConnection c = null;
		

		long len = 0;
		int ch = 0;
		c = url.openStream();
		is = c.openInputStream();
		len = c.getLength();
		if (len != -1) {
			// Read exactly Content-Length bytes
			for (int i = 0; i < len; i++)
				if ((ch = is.read()) != -1) {
					b.append((char) ch);
				}
		} else {
			// Read until the connection is closed.
			while ((ch = is.read()) != -1) {
				len = is.available();
				b.append((char) ch);
			}
		}

		is.close();
		c.close();
		return b.toString();
	}

	private Image downloadImage(String filename) { // the image file reader
		Image images = null;
		URL url;
		url = getClass().getResource("Imagestore/" + filename);
		try {
			images = ImageIO.read(url);
		} catch (IOException ex) {
			System.out.println("can not read the file: " + filename);
			ex.printStackTrace();
		}
		return images;
	}*/
}

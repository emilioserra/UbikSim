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

package sim.app.ubik.chart;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.axis.TickUnits;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.ApplicationFrame;

public class Periodogram extends ApplicationFrame {

    //TimeSeriesCollection result;
    //TimeSeries serie;

    XYSeries newSerie;
    XYSeriesCollection newResult;
    JFreeChart chart;

    public Periodogram(final String title) {

        super(title);
        newSerie=new XYSeries(title,true,true);
        newResult= new XYSeriesCollection();
        newResult.addSeries(newSerie);
        //serie=new TimeSeries(title, Minute.class);
        //result = new TimeSeriesCollection();
        //result.addSeries(serie);
        //final XYDataset data = result;
        XYDataset data = newResult;
        chart = createChart(data,title);
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
        setContentPane(chartPanel);

    }
    private JFreeChart createChart(final XYDataset data,String title) {

        final JFreeChart chart = ChartFactory.createScatterPlot(
            title,
            "X",
            "Y",
            data,
            PlotOrientation.VERTICAL,
            false,
            true,
            false
        );

        final XYPlot plot = chart.getXYPlot();
        //plot.getRenderer().setToolTipGenerator(StandardXYToolTipGenerator.getTimeSeriesInstance());

        final ValueAxis domainAxis = new NumberAxis("Time");
        domainAxis.setUpperMargin(0.50);
        domainAxis.setRange(0, 24);
        TickUnits units = new TickUnits();
        units.add(new NumberTickUnit(1));
        domainAxis.setStandardTickUnits(units);
        plot.setDomainAxis(domainAxis);

        final ValueAxis rangeAxis = plot.getRangeAxis();
        rangeAxis.setUpperMargin(0.30);
        rangeAxis.setLowerMargin(0.50);
        rangeAxis.setStandardTickUnits(units);

        return chart;

    }
    
    public void addToSeries(String dia, String hora, String minutos) {
        //Hour hour = new Hour(Integer.parseInt(hora), new Day(1, 1, 2010));
        newSerie.add(Integer.parseInt(hora)+((double)Integer.parseInt(minutos)*5/3/100), Integer.parseInt(dia));
                //serie.addOrUpdate(new Minute(Integer.parseInt(minutos), hour), Integer.parseInt(dia));
       
    }

    public JFreeChart getChart(){
        return chart;
    }

}

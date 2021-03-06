/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package qap.genetic.algorithm;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

/**
 *
 * @author juanca
 */
public class Grafico extends JFrame {

    private JPanel panel;
    private final ArrayList<Pair> datos;
    private final String tipo;

    /**
     * Constructor parametrizado del grafico
     * @param dat Array con las iteraciones y el fitness asociado
     * @param tip Tipo de algoritmo
     */
    public Grafico(ArrayList<Pair> dat,String tip) {
        setTitle("Evolución del mejor fitness "+tip);
        setSize(800,500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
        this.datos=dat;
        this.tipo=tip;
        init();
    }

    /**
     * Genera una imagen con la evolucion del fitness
     */
    private void init() {
        panel = new JPanel();
        getContentPane().add(panel);
        // Fuente de Datos
        DefaultCategoryDataset line_chart_dataset = new DefaultCategoryDataset();
        for(int i=1;i<datos.size();i++){
            line_chart_dataset.addValue(datos.get(i).getFitness(), "Fitness", Integer.toString(datos.get(i).getIteracion()));
        }

        // Creando el Grafico
        JFreeChart chart = ChartFactory.createLineChart("Evolución del fitness "+tipo, "Iteracion", "Fitness", line_chart_dataset, PlotOrientation.VERTICAL, true, true, false);

        // Mostrar Grafico
        ChartPanel chartPanel = new ChartPanel(chart);
        panel.add(chartPanel);
        
        try {
            ChartUtilities.saveChartAsJPEG(new File(this.tipo+".jpg"), chart, 2000, 500);
        } catch (IOException ex) {
            Logger.getLogger(Grafico.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}

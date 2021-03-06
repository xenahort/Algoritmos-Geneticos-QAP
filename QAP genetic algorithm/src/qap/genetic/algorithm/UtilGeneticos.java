/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package qap.genetic.algorithm;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author juanca
 */
public class UtilGeneticos {

    private final int n;
    private final int flujos[][];
    private final int distancias[][];

    private final Configuracion conf;

    /**
     * Consrtuctor parametrizado
     *
     * @param n Tamaño del array de solucion
     * @param flujos Matriz de flujos
     * @param distancias Matriz de distancias
     */
    public UtilGeneticos(int n, int flujos[][], int distancias[][]) {
        this.n = n;
        this.flujos = flujos;
        this.distancias = distancias;

        conf = new Configuracion();
    }

    /**
     * Genera una poblacion aleatoria
     *
     * @param poblacion Array en el que almacenar la poblacion
     * @param tamPoblacion Tamaño de la poblacion
     */
    public void generarPoblacionAleatoria(ArrayList<Individuo> poblacion, int tamPoblacion) {
        for (int i = 0; i < tamPoblacion; i++) {
            Individuo ind = new Individuo(n);
            ind.calcularFitness(flujos, distancias);
            poblacion.add(ind);
        }
    }

    /**
     * Elige al primer padre por torneo binario
     *
     * @param poblacion Población con los individuos
     * @return Individuo seleccionado
     */
    public Individuo torneoBinario1(ArrayList<Individuo> poblacion) {
        Random rnd = new Random();

        int p1 = rnd.nextInt((conf.getTamPoblacion() - 1) + 1);
        int p2 = rnd.nextInt((conf.getTamPoblacion() - 1) + 1);
        if (p1 == p2) {
            while (p1 == p2) {
                p2 = rnd.nextInt((conf.getTamPoblacion() - 1) + 1);
            }
        }
        Individuo ind1 = poblacion.get(p1);
        Individuo ind2 = poblacion.get(p2);

        if (ind1.getFitness() < ind2.getFitness()) {
            return ind1;
        } else {
            return ind2;
        }
    }

    /**
     * Elige al segundo padre por torneo binario
     *
     * @param poblacion Población con los individuos
     * @param padre1 Anterior individuo seleccionado
     * @return Individuo seleccionado
     */
    public Individuo torneoBinario2(ArrayList<Individuo> poblacion, Individuo padre1) {
        Random rnd = new Random();

        int p1 = rnd.nextInt((conf.getTamPoblacion() - 1) + 1);
        int p2 = rnd.nextInt((conf.getTamPoblacion() - 1) + 1);
        if (p1 == p2 || poblacion.get(p1).igualIndividuo(padre1) || poblacion.get(p2).igualIndividuo(padre1)) {
            while (p1 == p2 || poblacion.get(p1).getFitness() == padre1.getFitness() || poblacion.get(p2).getFitness() == padre1.getFitness()) {
                p1 = rnd.nextInt((conf.getTamPoblacion() - 1) + 1);
                p2 = rnd.nextInt((conf.getTamPoblacion() - 1) + 1);
            }
        }
        Individuo ind1 = poblacion.get(p1);
        Individuo ind2 = poblacion.get(p2);

        if (ind1.getFitness() < ind2.getFitness()) {
            return ind1;
        } else {
            return ind2;
        }
    }

    /**
     * Muta a toda la poblacion
     *
     * @param descendencia Población a la que se muta
     */
    public void mutarPoblacion(ArrayList<Individuo> descendencia) {
        int prob;
        int n1, n2, aux;
        for (int i = 0; i < conf.getTamPoblacion(); i++) {
            prob = (int) (Math.random() * 100) + 1;
            if (conf.getProbMutacion() > prob) {
                n1 = (int) (Math.random() * n);
                n2 = (int) (Math.random() * n);
                while (n1 == n2) {
                    n2 = (int) (Math.random() * n);
                }
                aux = descendencia.get(i).getCromosoma()[n1];
                int[] cromAux = descendencia.get(i).getCromosoma().clone();
                cromAux[n1] = cromAux[n2];
                cromAux[n2] = aux;
                descendencia.get(i).setCromosoma(cromAux);
            }
            descendencia.get(i).calcularFitness(flujos, distancias);
        }
    }

    public boolean reiniciarPorVariedad(ArrayList<Individuo> poblacion) {
        //<Fitness, cantidad>
        Map<Integer, Integer> mapa = new HashMap<>();
        for (int i = 0; i < poblacion.size(); i++) {
            if (mapa.containsKey(poblacion.get(i).getFitness())) {
                int valor = mapa.get(poblacion.get(i).getFitness());
                ++valor;
                mapa.put(poblacion.get(i).getFitness(), valor);
            } else {
                mapa.put(poblacion.get(i).getFitness(), 1);
            }
        }
        int limite = (conf.getPorcientoIguales() * 100) / n;
        return mapa.keySet().stream().anyMatch((key) -> (mapa.get(key) >= limite));
    }

    /**
     * Guarda un una solución en un fichero
     *
     * @param directorio Directorio en el que se va a guardar
     * @param mejor Individuo con la solucion a guardar
     */
    public void guardarResultado(String directorio, Individuo mejor) {
        File folder = new File(directorio);
        if (!folder.exists()) {
            folder.mkdir();
        }
        String ruta = directorio + "/" + mejor.getFitness() + ".txt";
        File archivo = new File(ruta);
        BufferedWriter bw;
        try {
            bw = new BufferedWriter(new FileWriter(archivo));
            bw.write(Arrays.toString(mejor.getCromosoma()));
            bw.close();
        } catch (IOException ex) {
            Logger.getLogger(Estandar.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Cruza dos individuos y da lugar a otros dos
     *
     * @param padre1 Individuo de cruce 1
     * @param padre2 Individuo de cruce 2
     * @param hijo1 Individuo resultante 1
     * @param hijo2 Individuo resultante 2
     */
    public void cruce(int[] padre1, int[] padre2, Individuo hijo1, Individuo hijo2) {
        int corte = (int) (Math.random() * n);
        int cromosoma1[], cromosoma2[];
        cromosoma1 = new int[n];
        cromosoma2 = new int[n];
        for (int i = 0; i < n; i++) {
            cromosoma1[i] = cromosoma2[i] = -1;
        }
        //Relleno el hijo 1
        System.arraycopy(padre1, 0, cromosoma1, 0, corte);
        int coincidencias = 0;
        for (int i = corte; i < n; i++) {
            if (!yaEsta(cromosoma1, corte + 1, padre2[i])) {
                cromosoma1[i - coincidencias] = padre2[i];
            } else {
                ++coincidencias;
            }
        }
        for (int i = n - coincidencias; i < n; i++) {
            for (int j = corte; j < n; j++) {
                if (!yaEsta(cromosoma1, n - coincidencias, padre1[j])) {
                    cromosoma1[i] = padre1[j];
                    --coincidencias;
                    break;
                }
            }
        }
        hijo1.setCromosoma(cromosoma1);

        //Relleno el hijo 2
        System.arraycopy(padre2, 0, cromosoma2, 0, corte);
        coincidencias = 0;
        for (int i = corte; i < n; i++) {
            if (!yaEsta(cromosoma2, corte + 1, padre1[i])) {
                cromosoma2[i - coincidencias] = padre1[i];
            } else {
                ++coincidencias;
            }
        }
        for (int i = n - coincidencias; i < n; i++) {
            for (int j = corte; j < n; j++) {
                if (!yaEsta(cromosoma2, n - coincidencias, padre2[j])) {
                    cromosoma2[i] = padre2[j];
                    --coincidencias;
                    break;
                }
            }
        }
        hijo2.setCromosoma(cromosoma2);
    }

    /**
     * Comprueba si ya hay un valor en el vector solucion
     *
     * @param cromosoma Vector solucion
     * @param pos Posicion limite
     * @param num Valor a comprobar
     * @return Si ya hay un valor en el vector solucion o no
     */
    private boolean yaEsta(int cromosoma[], int pos, int num) {
        for (int i = 0; i < pos; i++) {
            if (cromosoma[i] == num) {
                return true;
            }
        }
        return false;
    }
}

/**
 * @author Munkhdorj Erdenebaatar
 */

import java.util.*;

public class HitoriSolver {

    //exemple wikipédia
    /* Cet exemple a un unique résultat possible, ci-dessous ou en png dans le dossier src Hitori_2.png
    On peut tester en donnant ce résultat dans le tableau initial, il sera le seul résultat sortant si on teste n fois.
    {-2, 2, -1, 5, 3},
    { 2, 3,  1, 4,-5},
    {-1, 1, -1, 3, 5},
    { 1,-3,  5,-4, 2},
    { 5, 4,  3, 2, 1}
    */
    private static final int[][] initialGrid = {
            { 2, 2,  1, 5, 3},
            { 2, 3,  1, 4, 5},
            { 1, 1,  1, 3, 5},
            { 1, 3,  5, 4, 2},
            { 5, 4,  3, 2, 1}
    };
    private static final int GRID_SIZE= initialGrid.length;
    private static final Random random = new Random();
    private static int iteration = 0;
    private static int markedCounter = 0;

    /**
     * fonction main pour tester
     * @param args arguments
     */
    public static void main(String[] args) {
        int[][] res = solveHitori();
        System.out.println("Solution:");
        afficher(res);
    }

    /**
     * résolution du problème
     *
     * @return le résultat
     */
    private static int[][] solveHitori() {
        int[][] hitoriCourant = copie(HitoriSolver.initialGrid);
        double temperature = GRID_SIZE*GRID_SIZE;
        int energieCourant = getEnergie(hitoriCourant);

        while(energieCourant != 0) {
            //marquer ou blanchir une case aléatoire
            int[][] nouveauHitori = randomColorize(hitoriCourant);
            double dF = getEnergie(nouveauHitori) - getEnergie(hitoriCourant);
            //si on a une meilleure solution, on copie le noveau résultat dans la grille courante
            if (accepte(dF,temperature)) {
                hitoriCourant = copie(nouveauHitori);
                energieCourant = getEnergie(hitoriCourant);
            }

            temperature = temperature * 0.999;
            System.out.println("temperature :" + temperature + " , energie :" + energieCourant);
            iteration++;
            //Si le programme essaie taille x 3 fois d'affilée sans meilleur résultat. On lui redonne une nouvelle tentative avec le tableau
            if(iteration>GRID_SIZE*3)
                hitoriCourant= copie(HitoriSolver.initialGrid);
            if(temperature<0.00100073507137239)
                temperature=GRID_SIZE*GRID_SIZE;
        }
        return hitoriCourant;
    }

    /**
     * Fonction pour valider la nouvelle solution
     * @param dF différence d'énergie
     * @param temperature température courante
     * @return accepte la valeur ou non
     */
    private static boolean accepte(double dF, double temperature) {
        if (dF >= 0) {
            double a = Math.exp(-dF / temperature);
            if (Math.random() >= a) {
                return false;
            }
        }
        return true;
    }

    /**
     * Noircit ou blanchit une case aléatoirement
     * @param grid tableau des données
     * @return le tableau avec les nouvelles données
     */
    private static int[][] randomColorize(int[][] grid) {
        int[][] newGrid = copie(grid);
        int row = random.nextInt(GRID_SIZE);
        int col = random.nextInt(GRID_SIZE);

        int allow = allowMark(newGrid);
        //Marquer une case
        if(allow == 0)
            newGrid[row][col] = -newGrid[row][col];
        //Blanchir une case
        else if(allow ==1)
            newGrid[row][col] = Math.abs(newGrid[row][col]);
        else {
            //Choisir aléatoirement entre les 2 choix précédents
            int rand = random.nextInt(2);
            if(rand==0)
                newGrid[row][col] = -Math.abs(newGrid[row][col]);
            else
                newGrid[row][col] =  Math.abs(newGrid[row][col]);
        }
        return newGrid;
    }

    /**
     * Fonction pour marquer ou blanchir une case, pour éviter d'avoir que des cases marqués ou non marqués
     * @param grid tableau à vérifier
     * @return marquer / blanchir / on peut faire n'importe lequel
     */
    private static int allowMark(int[][] grid){
        int nbMarked =0;
        int nbUnmarked=0;
        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                if(grid[i][j] < 0)
                    nbMarked++;
                else
                    nbUnmarked++;
            }
        }
        if(nbMarked!= markedCounter){
            markedCounter= nbMarked;
            iteration=0;
        }
        //autorise à blanchir une case
        if(nbUnmarked<nbMarked)
            return 1;
        //autorise à marquer une case
        else if(nbMarked<nbUnmarked)
            return 0;
        //aucun des 2 précédents autorisé. On choisit aléatoirement entre les 2 cas précédents
        else return 2;
    }

    /**
     * copie un tableau
     * @param grid tableau à recopiere
     * @return la copie du tableau
     */
    private static int[][] copie(int[][] grid) {
        int[][] copy = new int[GRID_SIZE][GRID_SIZE];
        for (int i = 0; i < GRID_SIZE; i++) {
            copy[i] = Arrays.copyOf(grid[i], GRID_SIZE);
        }
        return copy;
    }

    /**
     * récupérer l'energie de la solution courante
     * @param grid tableau à parcourir(problème à résoudre)
     * @return l'energie
     */
    private static int getEnergie(int[][] grid) {
        int[][]tab = copie(grid);
        int[][]tabVertical = copie(grid);
        int energy = 0;
        //règle 1: Chaque ligne et chaque colonne ne doit contenir qu’une seule occurrence d’un chiffre donné.
        for (int ligne = 0; ligne < GRID_SIZE; ligne++){
            for (int colonne = 0; colonne < GRID_SIZE; colonne++) {
                int horizontal = countOccurrenceH(tab,ligne,colonne);
                int vertical = countOccurrenceV(tabVertical,ligne,colonne);
                //à additoner les 2
                energy = energy +horizontal+ vertical;
            }

        }
        //regle 2: Les cases marquées ne doivent pas être adjacentes horizontalement ou verticalement, elles peuvent être en diagonale.
        energy += getEnergyByRule2(copie(grid));
        //regle 3:Les cases non marquées doivent toutes être connectées entre elles par adjacence horizontalement ou verticalement.
        energy += Graph.isConnected(copie(grid));
        return energy;
    }

    /**
     * récupérer l'energie en se basant sur la règle 2
     * @param arr tableau à parcourir
     * @return l'energie
     */
    private static int getEnergyByRule2(int[][] arr){
        int energy =0;
        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                int cell_Courant = arr[i][j];
                arr[i][j]=0;
                if(cell_Courant<0) {
                    //gauche
                    if (i > 0)
                        if (arr[i - 1][j]<0) {
                            energy++;
                        }
                    //droite
                    if (i <GRID_SIZE-1)
                        if (arr[i + 1][j]<0) {
                            energy++;
                        }
                    //haut
                    if (j > 0)
                        if (arr[i][j - 1]<0){
                            energy++;
                        }
                    //bas
                    if (j <GRID_SIZE-1)
                        if (arr[i][j + 1]<0){
                            energy++;
                        }
                }
            }
        }

        return energy;
    }

    /**
     * calcul d'occurence en horizontal
     * @param arr tableau des données
     * @param ligne ligne de la valeur courante
     * @param colonne colonne de la valeur courante
     * @return occurence
     */
    private static int countOccurrenceH(int[][] arr, int ligne , int colonne) {
        int occ = 0;
        int value = arr[ligne][colonne];
        if (arr[ligne][colonne] > 0){
            arr[ligne][colonne] = -arr[ligne][colonne];
            for (int i = 0; i < GRID_SIZE; i++) {
                if (arr[ligne][i] == value && i != colonne) {
                    arr[ligne][i] = -arr[ligne][i];
                    occ++;
                }
            }
        }
        return occ;
    }

    /**
     * calcul d'occurence en vertical
     * @param arr tableau des données
     * @param ligne ligne de la valeur courante
     * @param colonne colonne de la valeur courante
     * @return occurence
     */
    private static int countOccurrenceV(int[][] arr, int ligne , int colonne) {
        int occ = 0;
        int value = arr[colonne][ligne];
        if (arr[colonne][ligne] > 0){
            arr[colonne][ligne] = -arr[colonne][ligne];
            for (int i = 0; i < GRID_SIZE; i++) {
                if (arr[i][ligne] == value && i != colonne) {
                    arr[i][ligne] = -arr[i][ligne];
                    occ++;
                }
            }
        }
        return occ;
    }

    /**
     * afficher un tableau
     * @param grid tableau à afficher
     */
    public static void afficher(int[][] grid) {
        StringBuilder sb = new StringBuilder();
        sb.append("\n");

        for (int i = 0; i < GRID_SIZE; i++) {
            sb.append("[");
            for (int j = 0; j < GRID_SIZE; j++) {
                if (j == GRID_SIZE-1)
                    sb.append(String.format("%3d",grid[i][j]));
                else
                    sb.append(String.format("%3d,",grid[i][j]) );
            }
            sb.append("]\n");
        }
        System.out.println(sb);
    }
}

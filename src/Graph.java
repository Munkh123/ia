/**
 * Classe pour créer un graphe et tester les composants fortement connexes
 * @author Munkhdorj Erdenebaatar
 */
import java.util.*;
import java.util.LinkedList;

class Graph {
    private final int taille;
    private final LinkedList<Integer>[] adjacents;

    /**
     * constructeur pour créer le graphe
     * @param s
     */
    Graph(int s) {
        taille = s;
        adjacents = new LinkedList[s];
        for (int i = 0; i < s; ++i)
            adjacents[i] = new LinkedList<>();
    }

    /**
     * créer une arete entre 2 noeuds
     * @param s noeud source
     * @param d noeud destination
     */
    void addEdge(int s, int d) {
        adjacents[s].add(d);
    }

    /**
     * parcours en profondeur
     * @param s source
     * @param tabVisites les visites
     */
    void dfs(int s, boolean[] tabVisites) {
        tabVisites[s] = true;
        int n;
        Iterator<Integer> i = adjacents[s].iterator();
        while (i.hasNext()) {
            n = i.next();
            if (!tabVisites[n])
                dfs(n, tabVisites);
        }
    }

    /**
     * transposer le graphe
     * @return graphe
     */
    Graph transposer() {
        Graph g = new Graph(taille);
        for (int s = 0; s < taille; s++) {
            Iterator<Integer> i = adjacents[s].listIterator();
            while (i.hasNext())
                g.adjacents[i.next()].add(s);
        }
        return g;
    }

    /**
     * remplir la pile
     * @param s noeud
     * @param tabVisites tableau des noeuds visités
     * @param pile pile des noeuds
     */
    void fill(int s, boolean[] tabVisites, Stack<Integer> pile) {
        tabVisites[s] = true;
        Iterator<Integer> i = adjacents[s].iterator();
        while (i.hasNext()) {
            int n = i.next();
            if (!tabVisites[n])
                fill(n, tabVisites, pile);
        }
        pile.push(s);
    }

    /**
     * calcul de la connexité des composants
     * @param arr tableau des noeuds
     * @return energie
     */
    int getConnexe(int[][] arr) {
        int energy=0;
        Stack<Integer> pile = new Stack<>();
        boolean[] tabVisites = new boolean[taille];
        for (int i = 0; i < taille; i++)
            tabVisites[i] = false;
        for (int i = 0; i < taille; i++)
            if (!tabVisites[i])
                fill(i, tabVisites, pile);
        Graph gr = transposer();
        for (int i = 0; i < taille; i++)
            tabVisites[i] = false;

        List<Integer> searchArray = new LinkedList<>();
        for (int[] ints : arr)
            for (int j = 0; j < arr.length; j++)
                if (ints[j] < 0)
                    searchArray.add(Math.abs(ints[j]));

        while (!pile.empty()) {
            int s = (int) pile.pop();
            if (!tabVisites[s]) {
                gr.dfs(s, tabVisites);
                if(s!=0 && !searchArray.contains(s)) {
                    energy++;
                }
            }
        }
        //1 seul composant connexe donc energy est 0
        if(energy == 1)
            energy--;
        return energy;
    }

    /**
     * créer un graphe à partir du tableau passé en paramètre et retourne l'energie pour la règle 3
     * @param arr tableau des données hitori
     * @return l'energie(règle 3)
     */
    public static int isConnected(int[][] arr){
        int i=1;
        //prépation des données du graphe qui vont représenter chaque nœud.
        for (int ligne = 0; ligne < arr.length; ligne++) {
            for (int colonne = 0; colonne < arr.length; colonne++) {
                if(arr[ligne][colonne]<0)
                    arr[ligne][colonne]=-i;
                else
                    arr[ligne][colonne]=i;
                i++;
            }
        }
        //Création du graphe à partir du tableau
        Graph g = new Graph((arr.length*arr.length)+1);
        for (int ligne = 0; ligne < arr.length; ligne++) {
            for (int colonne = 0; colonne < arr.length; colonne++) {
                if(arr[ligne][colonne]>=0) {
                    //droite
                    if (colonne < arr.length - 1) {
                        if (arr[ligne][colonne + 1] >= 0) {
                            g.addEdge(arr[ligne][colonne], arr[ligne][colonne + 1]);
                            g.addEdge(arr[ligne][colonne + 1], arr[ligne][colonne]);
                        }
                    }
                    //gauche
                    if (colonne > 0) {
                        if (arr[ligne][colonne - 1] >= 0) {
                            g.addEdge(arr[ligne][colonne], arr[ligne][colonne - 1]);
                            g.addEdge(arr[ligne][colonne - 1], arr[ligne][colonne]);
                        }
                    }
                    //haut
                    if (ligne < arr.length - 1) {
                        if (arr[ligne + 1][colonne] >= 0) {
                            g.addEdge(arr[ligne][colonne], arr[ligne + 1][colonne]);
                            g.addEdge(arr[ligne + 1][colonne], arr[ligne][colonne]);
                        }
                    }
                    //bas
                    if (ligne > 0) {
                        if (arr[ligne - 1][colonne] >= 0) {
                            g.addEdge(arr[ligne][colonne], arr[ligne - 1][colonne]);
                            g.addEdge(arr[ligne - 1][colonne], arr[ligne][colonne]);
                        }
                    }
                }
                else {
                    //droite
                    if (colonne < arr.length - 1) {
                        if (arr[ligne][colonne + 1] < 0) {
                            g.addEdge(Math.abs(arr[ligne][colonne]), Math.abs(arr[ligne][colonne + 1]));
                            g.addEdge(Math.abs(arr[ligne][colonne + 1]), Math.abs(arr[ligne][colonne]));
                        }
                    }
                    //gauche
                    if (colonne > 0) {
                        if (arr[ligne][colonne - 1] < 0) {
                            g.addEdge(Math.abs(arr[ligne][colonne]), Math.abs(arr[ligne][colonne - 1]));
                            g.addEdge(Math.abs(arr[ligne][colonne - 1]), Math.abs(arr[ligne][colonne]));
                        }
                    }
                    //haut
                    if (ligne < arr.length - 1) {
                        if (arr[ligne + 1][colonne] < 0) {
                            g.addEdge(Math.abs(arr[ligne][colonne]), Math.abs(arr[ligne + 1][colonne]));
                            g.addEdge(Math.abs(arr[ligne + 1][colonne]), Math.abs(arr[ligne][colonne]));
                        }
                    }
                    //bas
                    if (ligne > 0) {
                        if (arr[ligne - 1][colonne] < 0) {
                            g.addEdge(Math.abs(arr[ligne][colonne]), Math.abs(arr[ligne - 1][colonne]));
                            g.addEdge(Math.abs(arr[ligne - 1][colonne]), Math.abs(arr[ligne][colonne]));
                        }
                    }
                    //diagonales
                    //droite haut
                    if (colonne < arr.length - 1 && ligne > 0) {
                        if (arr[ligne-1][colonne + 1] < 0) {
                            g.addEdge(Math.abs(arr[ligne][colonne]), Math.abs(arr[ligne-1][colonne + 1]));
                            g.addEdge(Math.abs(arr[ligne-1][colonne + 1]), Math.abs(arr[ligne][colonne]));
                        }
                    }
                    //gauche haut
                    if (colonne > 0 && ligne > 0) {
                        if (arr[ligne-1][colonne - 1] < 0) {
                            g.addEdge(Math.abs(arr[ligne][colonne]), Math.abs(arr[ligne-1][colonne - 1]));
                            g.addEdge(Math.abs(arr[ligne-1][colonne - 1]), Math.abs(arr[ligne][colonne]));
                        }
                    }
                    //droite bas
                    if (ligne < arr.length - 1 && colonne < arr.length-1) {
                        if (arr[ligne + 1][colonne + 1] < 0) {
                            g.addEdge(Math.abs(arr[ligne][colonne]), Math.abs(arr[ligne + 1][colonne + 1]));
                            g.addEdge(Math.abs(arr[ligne + 1][colonne + 1]), Math.abs(arr[ligne][colonne]));
                        }
                    }
                    //gauche bas
                    if (colonne > 0 && ligne >0 ) {
                        if (arr[ligne - 1][colonne - 1] < 0) {
                            g.addEdge(Math.abs(arr[ligne][colonne]), Math.abs(arr[ligne - 1][colonne - 1]));
                            g.addEdge(Math.abs(arr[ligne - 1][colonne - 1]), Math.abs(arr[ligne][colonne]));
                        }
                    }
                }
            }
        }

        return g.getConnexe(arr);
    }
}
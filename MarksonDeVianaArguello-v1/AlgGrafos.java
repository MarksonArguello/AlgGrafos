/**
 * Esse código serve para ler um grafo a partir de um arquivo txt e
 * informar se o grafo lido é um grafo P4-esparso.
 *
 * P4-sparse definition:
 * A graph is P4-sparse if every set of five vertices contains at most one induced P4.
 *
 * Pn definition:
 * The path graph is a tree with two nodes of vertex degree 1, and the other
 * nodes of vertex degree 2. A path graph is therefore a graph that can be drawn
 * so that all of its vertices and edges lie on a single straight line. (Gross and Yellen 2006, p. 18).
 *
 */

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;

/**
 * Classe principal do programa.
 *
 * @author Markson Arguello - marksonarguello@gmail.com
 */
public class AlgGrafos {
    static final String path = "MarksonDeVianaArguello-v1/myfiles/Grafo01.txt";
    static boolean weightedGraph;
    static List<Integer> listOfNodes;

    /**
     * Classe responsável por ler cada linha do arquivo.
     * Possui como atributos o caminho do arquivo no formato (pasta atual/Grafo01.txt)
     * e um BufferedReader que irá abrir e ler o arquivo.
     */
    static class Leitor {
        private String path;
        private BufferedReader bufferedReader;

        public Leitor(String path) throws FileNotFoundException {
            this.path = path;
            initBufferedReader();
        }

        private void initBufferedReader() throws FileNotFoundException {
            bufferedReader = new BufferedReader(new FileReader(this.path));
        }

        public String getLine() {
            String result = "";
            try {
                result = bufferedReader.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return result;
        }

    }

    /**
     * Classe para representar um vértice.
     * <p>
     *     A classe possui como atributos o número do vértice e os vizinhos desse vértice.
     * </p>
     */
    static class Node {
        private final Integer nodeId;
        private List<Integer> neighbors;

        public Node(Integer nodeId) {
            this.nodeId = nodeId;
            neighbors = new ArrayList<>();
        }

        public void addNeighbor(Integer node) {
            neighbors.add(node);
        }

        public Integer getNodeId() {
            return nodeId;
        }

        public List<Integer> getNeighbors() {
            return neighbors;
        }
    }

    /**
     * Função para converter a linha do arquivo em um vértice.
     *
     * @param line linha do arquivo txt contendo as informações de um vértice.
     * @return Um objeto da classe Node contendo o  número do vértice e seus vizinhos.
     */
    static Node convertStringToNode(String line) {

        String[] lineInfos =  line.split("="); //Divido a linha em [Número do vértice, Vizinhos do vértice]

        String id = lineInfos[0].trim(); // Retiro os espaços do Número do Vértice

        int nodeId = Integer.parseInt(id);
        Node node = new Node(nodeId); // Inicializo o Node com o Número do vértice

        String[] strNeighbors = new String[0];

        if (lineInfos.length > 1) strNeighbors = lineInfos[1].split(" "); // Separo cada vizinho em um índice do vetor de String

        int cntNumbers = 0;
        for (String number : strNeighbors) { // Para cada String no vetor de vizinhos
            if (number.equals("")) continue;
            cntNumbers++;
            if (weightedGraph && cntNumbers % 2 == 0) continue; // Se o grafo tiver pesos ignoro os pesos
            node.addNeighbor(Integer.parseInt(number.trim())); // Adiciono o vizinho ao node
        }

        return node;
    }


    static Set<Integer> visitados; // Set de vétice visitados usado na DFS (depth-first search)

    /**
     * Função para decidir dado um conjunto de 4 vértices se eles não contém nenhum ciclo.
     *
     * <p>
     *     A função funciona como uma DFS, escolhemos um vértice para ser a raiz e a partir da raiz
     *     vamos para cada um de seus filhos e verificamos se esses filhos possuem uma conexão com
     *     algum vértice já visitado.
     *
     *     Se um vértice possui uma conexão com um vértice já visitado pela DFS então o conjunto
     *     de vértices não pode ser uma árvore. Nesse caso a função retorna False.
     *
     *     Caso não possua nenhum ciclo então ou o conjunto forma uma árvore ou eles são desconexos.
     *     Nesse caso a função retorna True.
     * </p>
     *
     * @param grafo Grafo completo contendo os vértices e seus vizinhos
     * @param listaDeVertices Lista contendo o conjunto de 4 vértices a serem verificados
     * @param vertex Vértice atual da DFS
     * @param parent Pai do vértice atual, ou seja, vértice de onde viemos
     * @return True se não possuem ciclo, False se possuem ciclo.
     */
    static boolean isTree(List<List<Integer>> grafo, List<Integer> listaDeVertices, int vertex, int parent) {
        visitados.add(vertex);
        for (Integer neighbor : grafo.get(vertex)) {
            if (neighbor == parent) continue;

            if (listaDeVertices.contains(neighbor)) {
                if (visitados.contains(neighbor)) {
                    return false;
                }
                if (!isTree(grafo, listaDeVertices, neighbor, vertex)) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     *  Função para decidir dado um conjunto de 4 vértices se eles formam um grafo caminho.
     *
     *  <p>
     *      Para formar um grafo caminho os 4 vértices não podem conter um ciclo, logo,
     *      usamos a função <i>isTree</i> para verificar se eles possuem ciclo ou não.
     *
     *      Além disso, o grafo precisa ser conexo e para isso a função verifica se
     *      todos os vértices foram vistados na DFS, se isso acontecer eles são conexos e
     *      o tamanho do Set de vértices visitados será igual a 4. Então precisamos verificar
     *      se o tamanho do Set é igual ao tamanho da lista de vértices.
     *
     *      Também é necessário verificar se cada vértice da lista está conectado com
     *      no máximo 2 outros vértices da lista pois pode acontecer da lista formar uma árvore
     *      porém um vértice ter 3 conexões.
     *
     *      Se a lista passar por todas as verificações então o conjunto de 4 vértices é
     *      um P4.
     *
     *  </p>
     *
     * @param listaDeVertices Lista contendo o conjunto de 4 vértices a serem verificados
     * @param grafo Grafo completo contendo os vértices e seus vizinhos
     * @return True se formam um grafo caminho, False caso contrário.
     */
    static boolean isP4(List<Integer> listaDeVertices, List<List<Integer>> grafo) {
        visitados = new HashSet<>();
        if (!isTree(grafo, listaDeVertices, listaDeVertices.get(0), -1) || visitados.size() != listaDeVertices.size()) {
            return false;
        }

        for (Integer vertex : listaDeVertices) {
            int cntNeighbor = 0;
            for (Integer neighbor : grafo.get(vertex)) {
                if (listaDeVertices.contains(neighbor)) cntNeighbor++;
            }
            if (cntNeighbor > 2) return false;
        }
        return true;
    }

    /**
     * Função para contar, dado um conjunto de 5 vértices, quantos grafos P4 esse conjunto pode induzir.
     *
     * <p>
     *      Dado um conjunto de 5 vértices possuimos 5 combinações de 4 vértices.
     *      Para cada combinação é feito a verificação com a função <i>isP4</i> e caso
     *      retorne True é adicionado 1 ao contador de P4.
     * </p>
     * @param subConj Lista contendo o conjunto de 5 vértices a serem verificados
     * @param grafo Grafo completo contendo os vértices e seus vizinhos
     * @return Quantidade de grafos P4 que o conjunto pode induzir
     */
    static int countP4(List<Integer> subConj, List<List<Integer>> grafo) {
        int qtdP4 = 0;

        for (int i = 0; i < subConj.size(); i++) { // Gera toda as combinações de 4 elementos
            List<Integer> listaDeVertices = new ArrayList<>();

            for (int j = 0; j < subConj.size(); j++) {
                if (j == i) continue;
                listaDeVertices.add(subConj.get(j));
                listaDeVertices.sort(Integer::compareTo);
            }

            if (isP4(listaDeVertices, grafo)) { // Testa se forma um grafo P4
                qtdP4++;
            }


        }

        return qtdP4;
    }

    /**
     * Função que gera todos os subconjuntos de 5 vértices e conta quantos grafos P4 cada
     * subconjunto pode induzir. Se algum subconjunto induzir mais de um grafo P4 então o
     * grafo não é P4-esparso. Caso contrário, ele é P4-esparso.
     *
     *
     * @param grafo Grafo completo contendo os vértices e seus vizinhos.
     * @return True se o grafo é P4-esparso e False caso contrário.
     */
    static boolean isP4Sparse(List<List<Integer>> grafo) {
        if (listOfNodes.size() < 5) return true;

        List<Integer> subConj = new ArrayList<>();

        for (int i = 0; i < listOfNodes.size(); i++) {
            subConj.add(listOfNodes.get(i));
            for (int j = i+1; j < listOfNodes.size(); j++) {
                subConj.add(listOfNodes.get(j));
                for (int k = j+1; k < listOfNodes.size(); k++) {
                    subConj.add(listOfNodes.get(k));
                    for (int l = k+1; l < listOfNodes.size(); l++) {
                        subConj.add(listOfNodes.get(l));
                        for (int m = l+1; m < listOfNodes.size(); m++) {
                            subConj.add(listOfNodes.get(m)); // Conjunto contendo uma combinação de 5 vértices.

                            if (countP4(subConj, grafo) > 1) // Testo se o subconjunto de 5 vértices induz mais de um P4
                                return false;

                            subConj.remove(4);
                        }
                        subConj.remove(3);
                    }
                    subConj.remove(2);
                }
                subConj.remove(1);
            }
            subConj.remove(0);
        }

        return true;

    }

    /**
     * Função responsável por instanciar um objeto da classe Leitor, classe responsável
     * por ler cada linha do arquivo.
     * @param path Caminho do arquivo.
     * @return Uma instância de leitor.
     */
    static Leitor createLeitor(String path) {
        Leitor leitor;
        while (true) {
            try {
                leitor = new Leitor(path);
                break;
            } catch (FileNotFoundException e) {
                System.out.print("Arquivo não encontrado, verifique o caminho do arquivo: " + path);
                System.exit(0);
            }
        }
        return leitor;
    }



    /**
     * Método principal do programa.
     * @param args
     */
    public static void main(String[] args) {
        String line;

        List<List<Integer>> grafo = new ArrayList<>();
        Leitor leitor = createLeitor(path);
        listOfNodes = new ArrayList<>();


        while ((line = leitor.getLine()) != null) {
            Node node = convertStringToNode(line); // Converte a linha para um vértice

            listOfNodes.add(node.getNodeId()); // Adiciona o vértice na lista de vértices

            if (node.getNodeId() >= grafo.size()) { // Testa se a lista grafo possui o índice nodeId
                for (int i = grafo.size(); i <= node.getNodeId(); i++) {
                    grafo.add(new ArrayList<>()); // Aumenta o tamanho do grafo até ele ter o índice do vertíce.
                }
            }

            grafo.set(node.getNodeId(), node.getNeighbors()); // Adiciona o vértice na lista grafo
        }

        if (isP4Sparse(grafo)) {
            System.out.println("O grafo é P4-esparso.");
        } else {
            System.out.println("O grafo NÃO é P4-esparso.");
        }

    }
}

package com.company.baduk.DataStruct;

public class myGraph<T> {
    private int[][] graph;
    private boolean[] used;
    private int INF = 10000000;

    public myGraph(int[][] graph) {
        this.graph = graph;
        used = new boolean[graph[0].length];
        for (int i = 0; i < graph.length; i++)
            used[i] = false;
    }

    public int BFS(int v, int j, int len) {
        int minE = INF, pos;
        if (j == graph.length - 1) return len + graph[v][1];
        pos = -1;
        for (int i = 0; i < graph.length; i++) {
            if (!used[i] && minE > graph[v][i]) {
                minE = graph[v][i];
                pos = i;
            }
        }
        used[pos] = true;
        return BFS(pos, j + 1, len + minE);
    }

    public int sumValue(int i, int j) {
        int value = 0;
        return value;
    }
}

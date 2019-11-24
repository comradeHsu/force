package ds.force.trie;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

public class SuffixTree implements Trie{

    private transient Node root;

    private transient char[] chars;

    public SuffixTree(String word){
    }

    public SuffixTree(){
        this.root = new Node();
    }

    private static class Edge {

        private int from;

        private int to;

        private Node node;

        Edge(int from, int to) {
            this.from = from;
            this.to = to;
        }

    }

    private static class Node {

        private List<Edge> edges;

        private Node link;

        Node() {
            this.edges = new ArrayList<>();
        }

        Edge getEdge(int index){
            return edges.get(index);
        }

        void addEdge(Edge edge){
            this.edges.add(edge);
        }
    }

    private static class ActivePoint {

        private Node activeNode;

        private Edge activeEdge;

        private int activeLength;

        ActivePoint(Node activeNode, Edge activeEdge, int activeLength){
            this.activeNode = activeNode;
            this.activeEdge = activeEdge;
            this.activeLength = activeLength;
        }
    }

    @Override
    public boolean find(String s) {
        return false;
    }

    public void addd(String word){
        char[] chars = word.toCharArray();
        this.chars = chars;
        int remainder = 0;
        ActivePoint activePoint = new ActivePoint(root,null,0);
        Deque<Edge> stack = new ArrayDeque<>();
        cs:for (int c = 0; c < chars.length; c++) {
            stack.addAll(root.edges);
            remainder++;
            while(!stack.isEmpty()){
                Edge edge = stack.pop();
                if (edge.node != null){
                    stack.addAll(edge.node.edges);
                } else {
                    edge.to++;
                }
            }
            Node preCreate = null;
            int rem = remainder;
            for (int i = 0; i < rem; i++) {
                if (activePoint.activeEdge == null){
                    activePoint.activeEdge = getEdge(activePoint.activeNode,chars[c]);
                }
                Edge edge = activePoint.activeEdge;
                if (edge == null){
                    edge = new Edge(c,c);
                    activePoint.activeNode.addEdge(edge);
                    activePoint.activeLength = activePoint.activeLength == 0 ? 0 : activePoint.activeLength--;
                    remainder--;
                    continue;
                }
                if (chars[c] == chars[edge.from+activePoint.activeLength]) {
                    if ((edge.to - edge.from) == activePoint.activeLength) {
                        activePoint.activeNode = edge.node;
                        activePoint.activeEdge = null;
                        activePoint.activeLength = 0;
                        continue cs;
                    }
                    activePoint.activeLength++;
                    continue cs;
                }
                Node node = new Node();
                if (preCreate == null){
                    preCreate = node;
                } else {
                    preCreate.link = node;
                    preCreate = node;
                }
                Edge spiltNode = new Edge(edge.from + activePoint.activeLength,edge.to);
                spiltNode.node = edge.node;
                node.addEdge(spiltNode);
                node.addEdge(new Edge(c,c));
                edge.node = node;
                edge.to = edge.from + activePoint.activeLength - 1;
                activePoint.activeLength--;
                activePoint.activeEdge = activePoint.activeLength == 0 ? null : getEdge(activePoint.activeNode, chars[edge.from+1]);
                remainder--;
                if (activePoint.activeNode != root && activePoint.activeNode.link != null){
                    activePoint.activeNode = activePoint.activeNode.link;
                    activePoint.activeLength = 1;
                    activePoint.activeEdge = getEdge(activePoint.activeNode, chars[edge.from]);
                } else {
                    activePoint.activeNode = root;
                    activePoint.activeEdge = getEdge(activePoint.activeNode, chars[c-remainder+1]);
                    activePoint.activeLength = activePoint.activeEdge == null ? activePoint.activeLength : 1;
                }

            }

        }
    }

    @Override
    public boolean add(String word) {
        char[] chars = word.toCharArray();
        int edgeSize = this.root.edges.size();
        int remainder = 0;
        ActivePoint activePoint = new ActivePoint(root,null,0);
        for (int c = 0; c < chars.length; c++) {
            boolean isExist = false;
            remainder++;
            for (int i = 0; i < edgeSize; i++) {
                Edge edge = this.root.getEdge(i);
                if (chars[edge.from] == chars[c]){
                    activePoint.activeEdge = activePoint.activeEdge == null ? edge : activePoint.activeEdge;
                    activePoint.activeLength++;
                    remainder++;
                    isExist = true;
                } else {
                    if (edge.node == null) {
                        edge.to++;
                    } else {
                        Edge child = getEdge(edge.node,chars[c]);
                        if (child != null) {
                            activePoint.activeNode = edge.node;
                            activePoint.activeEdge = child;
                            activePoint.activeLength = 1;
                        }
                    }
                }
            }
            Node preCreate = null;
            for (int i = 0; i < remainder;i++) {
                Edge edge = activePoint.activeEdge;
                if (edge == null){
                    edge = new Edge(c,c);
                    activePoint.activeNode.addEdge(edge);
                    activePoint.activeLength--;
                    continue;
                }
                if (chars[c] == chars[edge.from+activePoint.activeLength]) {
                    activePoint.activeLength++;
                    remainder++;
                    continue;
                }
                edge.to = edge.from + activePoint.activeLength - 1;
                edge.node = edge.node == null ? new Node() : edge.node ;
                if (preCreate == null){
                    preCreate = edge.node;
                } else {
                    preCreate.link = edge.node;
                    preCreate = edge.node;
                }
                edge.node.addEdge(new Edge(edge.from + activePoint.activeLength,c));
                edge.node.addEdge(new Edge(c,c));
                activePoint.activeEdge = getEdge(activePoint.activeNode, chars[edge.from+i]);
                activePoint.activeLength--;
                remainder--;
            }
            if (!isExist){
                Edge edge = new Edge(c,c);
                this.root.edges.add(edge);
            }
        }
        return false;
    }

    private Edge getEdge(Node node, char c){
        Edge edge = null;
        int size = node.edges.size();
        for (int i = 0; i < size; i++) {
            Edge ele = node.getEdge(i);
            if (chars[ele.from] == c)
                edge = ele;
        }
        return edge;
    }

    @Override
    public boolean remove(String s) {
        return false;
    }
}

package ds.force.trie;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

public class SuffixTree implements Trie{

    /**
     * Default terminator.
     */
    private static final char DEFAULT_TERMINATOR = '$';

    private transient char terminator = DEFAULT_TERMINATOR;

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
        char[] charArray = s.toCharArray();
        Node node = this.root;
        for (int i = 0; i < charArray.length; i++){
            Edge edge = getEdge(node,charArray[i]);
            if (edge == null) return false;
            node = edge.node;
        }
        return true;
    }

    public int repeatCount(String word){
        char[] charArray = word.toCharArray();
        Node node = this.root;
        int count = 0;
        for (int i = 0; i < charArray.length; i++){
            Edge edge = getEdge(node,charArray[i]);
            if (edge == null) return count;
            node = edge.node;
        }
        return node.edges.size();
    }

    public void addd(String word){
        char[] chars = (word + terminator).toCharArray();
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
        char[] chars = (word + terminator).toCharArray();
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
        return true;
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

    public boolean remove(String word, char terminator){
        char[] chars = (word + terminator).toCharArray();
        for (int i = 0; i < chars.length; i++) {

        }
        return true;
    }
}

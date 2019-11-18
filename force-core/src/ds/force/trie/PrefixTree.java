package ds.force.trie;

import ds.force.primitive.CharObjectMap;

public class PrefixTree implements Trie {

    private transient TrieNode root;

    public PrefixTree(){
        this.root = new TrieNode();
    }

    private static class TrieNode {

        private CharObjectMap<TrieNode> childs;

        private boolean isEnd;

        private char val;

        TrieNode() {
            childs = new CharObjectMap<>();
            isEnd = false;
        }

        boolean contains(char character){
            return childs.containsKey(character);
        }

        TrieNode getTrieNode(char character){
            return childs.get(character);
        }
    }

    @Override
    public boolean find(String s) {
        return false;
    }

    @Override
    public boolean add(String word) {
        char[] chars = word.toCharArray();
        TrieNode node = this.root;
        for (int i = 0; i < chars.length; i++) {
            char character = chars[i];
            if (node.contains(character)){
                node = node.getTrieNode(character);
            } else {
                TrieNode newNode = new TrieNode();
                node.childs.put(character,newNode);
                node = newNode;
            }
        }
        node.isEnd = true;
        return true;
    }

    @Override
    public boolean remove(String s) {
        return false;
    }
}

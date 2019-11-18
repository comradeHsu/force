package ds.force.trie;

public class PrefixTree implements Trie{

    private static class TrieNode {

        private TrieNode[] childs;

        private boolean isEnd;

        private char val;

        TrieNode() {
            childs = new TrieNode[26];
            isEnd = false;
        }
    }

    @Override
    public boolean find(String s) {
        return false;
    }

    @Override
    public boolean add(String s) {
        return false;
    }

    @Override
    public boolean remove(String s) {
        return false;
    }
}

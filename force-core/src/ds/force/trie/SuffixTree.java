package ds.force.trie;

import java.util.HashMap;
import java.util.Map;

public class SuffixTree implements Trie{

    private transient SuffixNode root;

    public SuffixTree(String word){
        this.root = new SuffixNode();
    }

    private static class SuffixNode {

        private Map<String,SuffixNode> suffixes;

        SuffixNode() {
            this.suffixes = new HashMap<>();
        }

    }

    @Override
    public boolean find(String s) {
        return false;
    }

    @Override
    public boolean add(String word) {
        char[] chars = word.toCharArray();
        int index = 0;
        for (Map.Entry<String,SuffixNode> entry : root.suffixes.entrySet()) {
            if (entry.getKey().charAt(0) == chars[index]) {

            }
        }
        return false;
    }

    @Override
    public boolean remove(String s) {
        return false;
    }
}

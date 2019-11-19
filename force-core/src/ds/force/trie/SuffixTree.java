package ds.force.trie;

import java.util.Map;

public class SuffixTree implements Trie{

    private transient TreeNode root;

    public SuffixTree(String word){
        this.root = new TreeNode();
    }

    private static class TreeNode {

        private Map<String,TreeNode> suffixes;

    }

    @Override
    public boolean find(String s) {
        return false;
    }

    @Override
    public boolean add(String word) {
        return false;
    }

    @Override
    public boolean remove(String s) {
        return false;
    }
}

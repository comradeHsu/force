package ds.force.trie;

import junit.framework.TestCase;

public class SuffixTreeTest extends TestCase {

    public void testAdd(){
        SuffixTree tree = new SuffixTree();
        tree.addd("abcabxabcd");
    }
}

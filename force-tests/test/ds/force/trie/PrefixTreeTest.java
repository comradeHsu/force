package ds.force.trie;

import junit.framework.TestCase;

public class PrefixTreeTest extends TestCase {

    public void testIsEmpty(){
        PrefixTree prefixTree = new PrefixTree();
        assertTrue(prefixTree.isEmpty());
        prefixTree.add("今天天气很好");
        assertFalse(prefixTree.isEmpty());
    }

    public void testFind(){
        PrefixTree prefixTree = new PrefixTree();
        prefixTree.add("今天天气很好");
        assertFalse(prefixTree.find("今天"));
        prefixTree.add("今天下雨");
        assertTrue(prefixTree.find("今天天气很好"));
    }

    public void testRemove(){
        PrefixTree prefixTree = new PrefixTree();
        prefixTree.add("今天天气很好");
        assertFalse(prefixTree.find("今天"));
        prefixTree.add("今天下雨");
        assertTrue(prefixTree.find("今天下雨"));
        prefixTree.remove("今天下雨");
        assertFalse(prefixTree.find("今天下雨"));
    }
}

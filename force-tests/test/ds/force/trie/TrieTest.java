package ds.force.trie;

import junit.framework.TestCase;

import java.nio.charset.Charset;

public class TrieTest extends TestCase {

    public void testUtf8(){
        char[] chars = "😄".toCharArray();
        byte[] bytes = "😄".getBytes(Charset.defaultCharset());
        System.out.println("😄".toCharArray());
        System.out.println("😄".getBytes());
        System.out.println(5535756836L);
    }
}

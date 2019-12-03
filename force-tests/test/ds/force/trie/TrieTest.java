package ds.force.trie;

import junit.framework.TestCase;

import java.nio.charset.Charset;

public class TrieTest extends TestCase {

    private int size = Integer.MAX_VALUE;

    public void testUtf8(){
        char[] chars = "😄".toCharArray();
        byte[] bytes = "😄".getBytes(Charset.defaultCharset());
        System.out.println("😄".toCharArray());
        System.out.println("😄".getBytes());
        System.out.println((int)'中');
        System.out.println((char)65535);
        char s = (char)(65536+50);
        System.out.println(s);
        char p = '2';
        System.out.println(p == s);
        System.out.println((char)65535);
        byte[] bytes1 = "😄哈哈".getBytes();
        byte[] bytes2 = "123".getBytes();
        System.out.println("哈哈".length());
    }
}

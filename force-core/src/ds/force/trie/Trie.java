package ds.force.trie;

/**
 * @author comradeHsu
 * @serial
 */
public interface Trie {

    boolean find(String s);

    boolean add(String s);

    boolean remove(String s);
}

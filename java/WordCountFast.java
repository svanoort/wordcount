import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.Comparable;
import java.util.Comparator;
import java.lang.Integer;
import java.lang.String;
import java.lang.StringBuilder;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.NavigableMap;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.Collections;
import java.util.regex.Pattern;

/** Word count for Java. 
    Slow because of boxing/unboxing.
*/
class WordCountFast {
    static final String SEPARATORS = "\t \n";

    public static int compareEntries(Map.Entry<String,Integer> entry1, Map.Entry<String,Integer> entry2) {
        int comparison = Integer.compare(entry2.getValue(), entry1.getValue()); //Reversed because descending order

        // First sort by count, then alphabetically
        if (comparison == 0) {
            comparison = entry1.getKey().compareTo(entry2.getKey());
        }
        return comparison;
    }

    // Pulled out so this can be optimized by JIT compiler
    public static void handleLine(Map<String,Integer> countsMap, String line) {
        StringTokenizer tokenizer = new StringTokenizer(line, SEPARATORS, false);
        while(tokenizer.hasMoreTokens()) {
            String word = tokenizer.nextToken();
            Integer prevCount = countsMap.get(word); // Null if not stored
            countsMap.put(word, (prevCount == null) ? 1 : prevCount + 1);
        }
    }

    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        Map<String, Integer> m = new HashMap<String, Integer>();
        String line;
        while ((line = br.readLine()) != null) {
            handleLine(m, line.trim());
        }
        // Now we dump all the counts in a list and sort them
        ArrayList<Map.Entry<String,Integer>> entries = new ArrayList<Map.Entry<String, Integer>>(m.entrySet());
        Comparator<Map.Entry<String,Integer>> entryComparator = new Comparator<Map.Entry<String,Integer>>() {
            @Override
            public int compare(Map.Entry<String,Integer> entry1, Map.Entry<String,Integer> entry2) {
                return compareEntries(entry1, entry2);
            }
        };
        Collections.sort(entries, entryComparator);

        // Now we print out the sorted words
        for (Map.Entry<String,Integer> entry : entries) {
            System.out.println(entry.getKey() + "\t" + entry.getValue());
        }
    }
}

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.Comparable;
import java.lang.Integer;
import java.lang.String;
import java.lang.StringBuilder;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.StringTokenizer;
import java.util.Collections;

/** Word count for Java. 
    Slow because of boxing/unboxing.
*/
class WordCountFast {
    static final String SEPARATORS = "\t \n";

    // Allows for more efficient sorting, since we're comparing integers mostly
    static class SimpleEntry implements Comparable<SimpleEntry> {
        int count = 0;
        String word = null;

        SimpleEntry(String word, int count) {
            this.word = word;
            this.count = count;
        }

        @Override
        public int compareTo(SimpleEntry other) {
            int returnVal =  other.count - this.count;  // Reversed sort order
            if (returnVal == 0) {
                returnVal = this.word.compareTo(other.word);
            }
            return returnVal;
        }
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
        Map<String, Integer> m = new LinkedHashMap<String, Integer>();
        String line;
        while ((line = br.readLine()) != null) {
            handleLine(m, line.trim());
        }
        // Now we dump all the counts in a list and sort them
        ArrayList<SimpleEntry> simpleEntries = new ArrayList<SimpleEntry>(m.size());
        for (Map.Entry<String,Integer> entry : m.entrySet()) {
            simpleEntries.add(new SimpleEntry(entry.getKey(), entry.getValue()));
        }
        Collections.sort(simpleEntries);

        // Now we print out the sorted words
        StringBuilder build = new StringBuilder(100);
        BufferedWriter log = new BufferedWriter(new OutputStreamWriter(System.out));
        
        for (SimpleEntry entry : simpleEntries) {
            String outputStr = entry.word + "\t" + entry.count;
            log.write(outputStr, 0, outputStr.length());
            log.newLine();
        }
        log.flush();
        log.close();
    }
}

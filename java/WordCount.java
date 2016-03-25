import java.io.BufferedReader;
import java.io.Reader;
import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Collections;

/** Word count for Java. Slow because of boxing/unboxing. */
class WordCount {
    
    private static class CountForWord implements Comparable<CountForWord>{
        String word;
        int count = 1;

        public CountForWord(String word) {
            this.word = word;
        }

        @Override
        public int compareTo(CountForWord t) {
            if(count < t.count){
                return 1;
            }else if(count > t.count){
                return -1;
            }else{
                return word.compareTo(t.word);
            }
        }
    }
    
    private static void submitWord(Map<String, CountForWord> m, String word){
        CountForWord c;
        if((c = m.get(word)) != null){
            c.count ++;
        }else{
            m.put(word, new CountForWord(word));
        }
    }

    /** Reads a chunk of characters, tokenizes it, and submits tokens
    *  After execution, it shuffles residual characters to the start of the array
    *  And returns the number of characters remaining to include in the next token
    */
    public static int tokenizeAndSubmitBlock(Map<String, CountForWord> counts, 
        final char[] inputBuffer, int startIndex, int charsRead) {
        
        int index = 0; // Start of current token
        int endIndex = startIndex + charsRead;
        
        // Go from start to end of current read
        for (int i=startIndex; i<endIndex; i++) {
            char c = inputBuffer[i];
            
            if (c == ' ' || c == '\n' || c == '\t' || c == '\r') { // New token
                if (i != index) {
                    String word = new String(inputBuffer, index, i-index);
                    submitWord(counts, word);
                    index = i;
                }
                index++;
            }
        }

        // Copy residual token content to beginning of the array, start going again
        int residualSize =  endIndex - index;
        if (residualSize > 0) {
            System.arraycopy(inputBuffer, index, inputBuffer, 0, residualSize);
            return residualSize;
        } 
        return 0;
    }
    
    public static void main(String[] args) throws IOException {
        Reader reader = new InputStreamReader(System.in);
        Map<String, CountForWord> m = new HashMap<String, CountForWord>(100000);

        final int MAX_READ = 8192;  // Chunk size to read, also maximum token size
        char[] charBuffer = new char[MAX_READ];
        int startIndex = 0; // 1st element in array with new data
        int charsRead = 0;
        StringBuffer leftovers;

        long startTime = System.currentTimeMillis();
        while ((charsRead = reader.read(charBuffer, startIndex, MAX_READ-startIndex)) > 0) {
            startIndex = tokenizeAndSubmitBlock(m, charBuffer, startIndex, charsRead);
        }
        // Final residual token content
        if (startIndex > 0) {
            String word = new String(charBuffer, 0, startIndex);
            submitWord(m, word);
        }
        reader.close();
        long endTime = System.currentTimeMillis();
        System.err.println("Parse and count time (ms): "+(endTime-startTime));


        System.err.println("sorting...");
        startTime = System.currentTimeMillis();
        ArrayList<CountForWord> lst = new ArrayList<>(m.values());
        Collections.sort(lst);
        endTime = System.currentTimeMillis();
        System.err.println("Sorting time (ms): "+(endTime-startTime));

        System.err.println("output...");
        startTime = System.currentTimeMillis();
        BufferedWriter outputWriter = new BufferedWriter(new OutputStreamWriter(System.out));
        for(CountForWord c : lst){
            outputWriter.write(c.word + "\t" + c.count);
            outputWriter.newLine();
        }
        outputWriter.close();
        endTime = System.currentTimeMillis();
        System.err.println("Output time (ms): "+(endTime-startTime));
    }
}
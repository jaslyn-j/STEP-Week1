import java.util.*;

public class PlagiarismDetector_4 {

    static class PlagiarismDetector {
        private HashMap<String, Set<String>> ngramIndex = new HashMap<>();
        private HashMap<String, Integer> documentNgramCount = new HashMap<>();
        private int n;

        PlagiarismDetector(int ngramSize) {
            this.n = ngramSize;
        }

        private List<String> extractNgrams(String text) {
            String[] words = text.toLowerCase().replaceAll("[^a-z0-9 ]", "").split("\\s+");
            List<String> ngrams = new ArrayList<>();
            for (int i = 0; i <= words.length - n; i++) {
                StringBuilder sb = new StringBuilder();
                for (int j = i; j < i + n; j++) {
                    if (j > i) sb.append(" ");
                    sb.append(words[j]);
                }
                ngrams.add(sb.toString());
            }
            return ngrams;
        }

        public void indexDocument(String docId, String text) {
            List<String> ngrams = extractNgrams(text);
            documentNgramCount.put(docId, ngrams.size());
            for (String ngram : ngrams) {
                ngramIndex.computeIfAbsent(ngram, k -> new HashSet<>()).add(docId);
            }
            System.out.println("Indexed \"" + docId + "\": " + ngrams.size() + " n-grams");
        }

        public String analyzeDocument(String docId, String text) {
            List<String> ngrams = extractNgrams(text);
            int totalNgrams = ngrams.size();

            // Count matching n-grams per document
            HashMap<String, Integer> matchCount = new HashMap<>();
            for (String ngram : ngrams) {
                Set<String> docs = ngramIndex.get(ngram);
                if (docs != null) {
                    for (String d : docs) {
                        if (!d.equals(docId)) {
                            matchCount.merge(d, 1, Integer::sum);
                        }
                    }
                }
            }

            StringBuilder result = new StringBuilder();
            result.append("Analyzing \"").append(docId).append("\" (").append(totalNgrams).append(" n-grams)\n");

            if (matchCount.isEmpty()) {
                result.append("  No matches found — document appears original.\n");
            } else {
                matchCount.entrySet().stream()
                        .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                        .forEach(e -> {
                            double similarity = (e.getValue() * 100.0) / totalNgrams;
                            String verdict = similarity >= 50 ? "PLAGIARISM DETECTED" :
                                             similarity >= 15 ? "Suspicious": "ok";
                            result.append(String.format("  vs %-20s: %3d matching n-grams, Similarity: %5.1f%%, %s%n",
                                    e.getKey(), e.getValue(), similarity, verdict));
                        });
            }
            return result.toString();
        }
    }

    public static void main(String[] args) {
        PlagiarismDetector pd = new PlagiarismDetector(5);

        String original  = "the quick brown fox jumps over the lazy dog near the riverbank on a sunny afternoon";
        String plagiarized = "the quick brown fox jumps over the lazy dog near the riverbank on a cloudy morning";
        String different = "machine learning algorithms process large datasets to identify complex patterns efficiently";
        String partial   = "the quick brown fox jumps over the lazy dog and then ran away into the forest";

        pd.indexDocument("essay_001", original);
        pd.indexDocument("essay_002", different);
        System.out.println();

        System.out.println(pd.analyzeDocument("essay_003", plagiarized));
        System.out.println(pd.analyzeDocument("essay_004", partial));
        System.out.println(pd.analyzeDocument("essay_005", different));
    }
}

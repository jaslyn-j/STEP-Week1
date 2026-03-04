import java.util.*;

public class AutocompleteSystem_7{

    static class AutocompleteSystem {
        private HashMap<String, Integer> queryFrequency = new HashMap<>();

        public void addQuery(String query, int frequency) {
            queryFrequency.merge(query.toLowerCase(), frequency, Integer::sum);
        }

        public List<String> search(String prefix) {
            String lowerPrefix = prefix.toLowerCase();
            List<String> results = new ArrayList<>();

            queryFrequency.entrySet().stream()
                    .filter(e -> e.getKey().startsWith(lowerPrefix))
                    .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                    .limit(10)
                    .forEach(e -> results.add(
                            String.format("%-35s (%,d searches)", e.getKey(), e.getValue())
                    ));

            return results;
        }

        public void updateFrequency(String query) {
            int prev = queryFrequency.getOrDefault(query.toLowerCase(), 0);
            queryFrequency.merge(query.toLowerCase(), 1, Integer::sum);
            System.out.println("updateFrequency(\"" + query + "\"):" + prev + ": " +
                    queryFrequency.get(query.toLowerCase()));
        }

        public int getTotalQueries() {
            return queryFrequency.size();
        }
    }

    public static void main(String[] args) {
        AutocompleteSystem ac = new AutocompleteSystem();

        ac.addQuery("java tutorial",1_234_567);
        ac.addQuery("javascript",987_654);
        ac.addQuery("java download",456_789);
        ac.addQuery("java 21 features", 1);
        ac.addQuery("java interview questions", 89_432);
        ac.addQuery("javascript frameworks",345_678);
        ac.addQuery("javascript vs python",78_900);
        ac.addQuery("python tutorial", 999_999);
        ac.addQuery("python data science",654_321);
        ac.addQuery("python vs java",123_456);

        System.out.println("search(\"java\"):");
        ac.search("java").forEach(r -> System.out.println("  " + r));

        System.out.println("\nsearch(\"javascript\"):");
        ac.search("javascript").forEach(r -> System.out.println("  " + r));

        System.out.println("\nsearch(\"py\"):");
        ac.search("py").forEach(r -> System.out.println("  " + r));

        System.out.println("\nTrending update:");
        ac.updateFrequency("java 21 features");
        ac.updateFrequency("java 21 features");
        ac.updateFrequency("java 21 features");

        System.out.println("\nsearch(\"java 21\"):");
        ac.search("java 21").forEach(r -> System.out.println("  " + r));

        System.out.println("\nTotal unique queries stored: " + ac.getTotalQueries());
    }
}

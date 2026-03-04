import java.util.*;

public class AnalyticsDashboard_5 {

    static class AnalyticsDashboard {
        private HashMap<String, Integer>      pageViews      = new HashMap<>();
        private HashMap<String, Set<String>>  uniqueVisitors = new HashMap<>();
        private HashMap<String, Integer>      trafficSources = new HashMap<>();
        private int totalEvents = 0;

        public void processEvent(String url, String userId, String source) {
            pageViews.merge(url, 1, Integer::sum);
            uniqueVisitors.computeIfAbsent(url, k -> new HashSet<>()).add(userId);
            trafficSources.merge(source, 1, Integer::sum);
            totalEvents++;
        }

        public String getDashboard(int topN) {
            StringBuilder sb = new StringBuilder();
            sb.append(String.format("Total Events Processed: %d%n%n", totalEvents));

            sb.append("Top Pages:\n");
            int[] rank = {1};
            pageViews.entrySet().stream()
                    .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                    .limit(topN)
                    .forEach(e -> {
                        int unique = uniqueVisitors.getOrDefault(e.getKey(), Collections.emptySet()).size();
                        sb.append(String.format("  %2d. %-35s %,6d views  (%,d unique)%n",
                                rank[0]++, e.getKey(), e.getValue(), unique));
                    });

            int total = trafficSources.values().stream().mapToInt(Integer::intValue).sum();
            sb.append("\nTraffic Sources:\n");
            trafficSources.entrySet().stream()
                    .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                    .forEach(e -> sb.append(String.format("  %-12s %,6d visits  (%.0f%%)%n",
                            e.getKey() + ":", e.getValue(), e.getValue() * 100.0 / total)));

            return sb.toString();
        }
    }

    public static void main(String[] args) {
        AnalyticsDashboard ad = new AnalyticsDashboard();

        String[][] events = {
            {"/article/breaking-news", "user_1",  "Google"},
            {"/article/breaking-news", "user_2",  "Facebook"},
            {"/article/breaking-news", "user_3",  "Google"},
            {"/article/breaking-news", "user_1",  "Google"},  
            {"/sports/championship",   "user_4",  "Direct"},
            {"/sports/championship",   "user_5",  "Google"},
            {"/sports/championship",   "user_6",  "Direct"},
            {"/tech/ai-update",        "user_7",  "Twitter"},
            {"/tech/ai-update",        "user_8",  "Direct"},
            {"/health/wellness-tips",  "user_9",  "Google"},
            {"/health/wellness-tips",  "user_10", "Facebook"},
            {"/article/breaking-news", "user_11", "Twitter"},
        };

        for (String[] e : events) {
            ad.processEvent(e[0], e[1], e[2]);
        }

        System.out.println(ad.getDashboard(5));
    }
}

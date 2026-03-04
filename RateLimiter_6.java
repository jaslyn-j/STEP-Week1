import java.util.*;

public class RateLimiter_6 {

    static class TokenBucket {
        int tokens;
        long lastRefillTime;
        int maxTokens;

        TokenBucket(int maxTokens) {
            this.maxTokens = maxTokens;
            this.tokens = maxTokens;
            this.lastRefillTime = System.currentTimeMillis();
        }

        void refillIfNeeded() {
            long now = System.currentTimeMillis();
            long elapsed = now - lastRefillTime;
            if (elapsed >= 3_600_000L) { // 1 hour
                tokens = maxTokens;
                lastRefillTime = now;
            }
        }

        long secondsUntilReset() {
            long elapsed = System.currentTimeMillis() - lastRefillTime;
            return Math.max(0, (3_600_000L - elapsed) / 1000);
        }
    }

    static class RateLimiter {
        private HashMap<String, TokenBucket> clients = new HashMap<>();
        private int defaultLimit;

        RateLimiter(int requestsPerHour) {
            this.defaultLimit = requestsPerHour;
        }

        public String checkRateLimit(String clientId) {
            clients.computeIfAbsent(clientId, k -> new TokenBucket(defaultLimit));
            TokenBucket bucket = clients.get(clientId);
            bucket.refillIfNeeded();

            if (bucket.tokens > 0) {
                bucket.tokens--;
                return "Allowed (" + bucket.tokens + " requests remaining)";
            } else {
                return "Denied  (0 requests remaining, retry after " + bucket.secondsUntilReset() + "s)";
            }
        }

        public String getRateLimitStatus(String clientId) {
            TokenBucket bucket = clients.getOrDefault(clientId, new TokenBucket(defaultLimit));
            int used = defaultLimit - bucket.tokens;
            return String.format("{used: %d, limit: %d, remaining: %d, reset_in: %ds}",
                    used, defaultLimit, bucket.tokens, bucket.secondsUntilReset());
        }

        public int getTotalTrackedClients() {
            return clients.size();
        }
    }

    public static void main(String[] args) {
        RateLimiter rl = new RateLimiter(5);

        System.out.println("Client: abc123 (limit = 5 requests/hour)\n");
        for (int i = 1; i <= 7; i++) {
            System.out.println("Request #" + i + ": " + rl.checkRateLimit("abc123"));
        }

        System.out.println("\nClient: xyz789 (fresh client)");
        System.out.println("Request #1: " + rl.checkRateLimit("xyz789"));

        System.out.println("\ngetRateLimitStatus(\"abc123\"): " + rl.getRateLimitStatus("abc123"));
        System.out.println("getTotalTrackedClients(): " + rl.getTotalTrackedClients() + " clients");
    }
}

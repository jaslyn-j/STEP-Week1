import java.util.*;

public class DNSCache_3{
    static class DNSCache {
        static class DNSEntry {
            String domain;
            String ip;
            long expiryTime;

            DNSEntry(String domain, String ip, int ttlSeconds) {
                this.domain = domain;
                this.ip = ip;
                this.expiryTime = System.currentTimeMillis() + ttlSeconds * 1000L;
            }

            boolean isExpired() {
                return System.currentTimeMillis() > expiryTime;
            }

            long ttlRemaining() {
                return Math.max(0, (expiryTime - System.currentTimeMillis()) / 1000);
            }
        }

        private LinkedHashMap<String, DNSEntry> cache = new LinkedHashMap<>();
        private int maxSize;
        private int hits = 0, misses = 0, expired = 0;

        DNSCache(int maxSize) {
            this.maxSize = maxSize;
        }
        private String queryUpstream(String domain) {
            Map<String, String> fakeDNS = new HashMap<>();
            fakeDNS.put("google.com",   "172.217.14.206");
            fakeDNS.put("github.com",   "140.82.114.4");
            fakeDNS.put("amazon.com",   "176.32.103.205");
            fakeDNS.put("facebook.com", "157.240.241.35");
            return fakeDNS.getOrDefault(domain, "93.184.216.34");
        }

        public String resolve(String domain) {
            DNSEntry entry = cache.get(domain);

            if (entry != null && !entry.isExpired()) {
                hits++;
                return "Cache HIT: " + entry.ip + " (TTL remaining: " + entry.ttlRemaining() + "s)";
            }

            if (entry != null && entry.isExpired()) {
                cache.remove(domain);
                expired++;
                String ip = queryUpstream(domain);
                cache.put(domain, new DNSEntry(domain, ip, 300));
                return "Cache EXPIRED: Query upstream: " + ip + " (new TTL: 300s)";
            }
            misses++;
            if (cache.size() >= maxSize) {
                String lruKey = cache.keySet().iterator().next();
                cache.remove(lruKey);
                System.out.println("  [LRU Evicted: " + lruKey + "]");
            }

            String ip = queryUpstream(domain);
            cache.put(domain, new DNSEntry(domain, ip, 300));
            return "Cache MISS: Query upstream: " + ip + " (TTL: 300s)";
        }

        public String getCacheStats() {
            int total = hits + misses + expired;
            double hitRate = total == 0 ? 0 : (hits * 100.0 / total);
            return String.format("Hit Rate: %.1f%%, Hits: %d, Misses: %d, Expired: %d, Cached entries: %d",
                    hitRate, hits, misses, expired, cache.size());
        }
    }

    public static void main(String[] args) {
        DNSCache dns = new DNSCache(100);

        System.out.println("resolve(\"google.com\"): " + dns.resolve("google.com"));
        System.out.println("resolve(\"google.com\"): " + dns.resolve("google.com"));
        System.out.println("resolve(\"github.com\"): " + dns.resolve("github.com"));
        System.out.println("resolve(\"amazon.com\"): " + dns.resolve("amazon.com"));
        System.out.println("resolve(\"github.com\"): " + dns.resolve("github.com"));
        System.out.println("resolve(\"facebook.com\"): " + dns.resolve("facebook.com"));

        System.out.println("\ngetCacheStats() → " + dns.getCacheStats());
    }
}

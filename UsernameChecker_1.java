import java.util.*;

public class UsernameChecker_1 {

    static class UsernameChecker {
        private HashMap<String, Integer> registeredUsers = new HashMap<>();
        private HashMap<String, Integer> attemptFrequency = new HashMap<>();
        private int nextUserId = 1;

        public void registerUser(String username) {
            registeredUsers.put(username, nextUserId++);
        }

        public boolean checkAvailability(String username) {
            attemptFrequency.merge(username, 1, Integer::sum);
            return !registeredUsers.containsKey(username);
        }

        public List<String> suggestAlternatives(String username) {
            List<String> suggestions = new ArrayList<>();
            suggestions.add(username + "1");
            suggestions.add(username + "2");
            suggestions.add(username.replace("_", "."));
            suggestions.add("the_" + username);
            return suggestions;
        }

        public String getMostAttempted() {
            return attemptFrequency.entrySet().stream()
                    .max(Map.Entry.comparingByValue())
                    .map(e -> e.getKey() + " (" + e.getValue() + " attempts)")
                    .orElse("No attempts yet");
        }
    }

    public static void main(String[] args) {
        UsernameChecker uc = new UsernameChecker();
        uc.registerUser("john_doe");
        uc.registerUser("admin");
        uc.registerUser("jane_smith");
        uc.checkAvailability("admin");
        uc.checkAvailability("admin");
        uc.checkAvailability("admin");

        System.out.println("Availability of \"john_doe\": " + uc.checkAvailability("john_doe"));
        System.out.println("Availability of \"jane_smith\": " + uc.checkAvailability("jane_smith"));
        System.out.println("Availability of\"new_user\":" + uc.checkAvailability("new_user"));
		System.out.println("Availability of \"admin\":" + uc.checkAvailability("admin"));

        System.out.println("\nAlternativesfor \"john_doe\": " + uc.suggestAlternatives("john_doe"));
        System.out.println("Most attemplted UserName:" + uc.getMostAttempted());
    }
}

import java.util.*;

public class InventoryManager_2 {
    static class InventoryManager {
        private HashMap<String, Integer> stock = new HashMap<>();
        private LinkedHashMap<String, Queue<Integer>> waitingList = new LinkedHashMap<>();

        public void addProduct(String productId, int quantity) {
            stock.put(productId, quantity);
            waitingList.put(productId, new LinkedList<>());
        }

        public int checkStock(String productId) {
            return stock.getOrDefault(productId, 0);
        }

        public String purchaseItem(String productId, int userId) {
            int available = stock.getOrDefault(productId, 0);
            if (available > 0) {
                stock.put(productId, available - 1);
                return "Success, " + (available - 1) + " units remaining";
            } else {
                Queue<Integer> queue = waitingList.get(productId);
                queue.add(userId);
                return "Added to waiting list, position #" + queue.size();
            }
        }

        public String getWaitingList(String productId) {
            Queue<Integer> queue = waitingList.getOrDefault(productId, new LinkedList<>());
            return "Waiting list size: " + queue.size() + " , Users: " + queue;
        }
    }

    public static void main(String[] args) {
        InventoryManager im = new InventoryManager();

        im.addProduct("IPHONE15", 3);

        System.out.println("checkStock(\"IPHONE15\"):" + im.checkStock("IPHONE15") + " units available");
        System.out.println("purchase with userId=10001:" + im.purchaseItem("IPHONE15", 10001));
        System.out.println("purchase with userId=10002:" + im.purchaseItem("IPHONE15", 10002));
        System.out.println("purchase with userId=10003:" + im.purchaseItem("IPHONE15", 10003));
        System.out.println("purchase with userId=10004:" + im.purchaseItem("IPHONE15", 10004));
        System.out.println("purchase with userId=10005:" + im.purchaseItem("IPHONE15", 10005));
        System.out.println("\n" + im.getWaitingList("IPHONE15"));
    }
}

import java.util.*;

public class ParkingLot_8{

    enum SpotStatus { EMPTY, OCCUPIED, DELETED }

    static class Spot {
        SpotStatus status = SpotStatus.EMPTY;
        String licensePlate;
        long entryTime;
    }

    static class ParkingLot {
        private Spot[] spots;
        private int size;
        private HashMap<String, Integer> vehicleToSpot = new HashMap<>();
        private int occupiedCount = 0;
        private int totalProbes = 0;
        private int totalParkings = 0;
        private static final double RATE_PER_HOUR = 5.0;

        ParkingLot(int size) {
            this.size = size;
            this.spots = new Spot[size];
            for (int i = 0; i < size; i++) spots[i] = new Spot();
        }
        private int hash(String plate) {
            int h = 0;
            for (char c : plate.toCharArray()) h = (h * 31 + c) % size;
            return Math.abs(h);
        }

        public String parkVehicle(String plate) {
            if (occupiedCount >= size) return "Parking lot is full!";

            int preferred = hash(plate);
            int idx = preferred;
            int probes = 0;

            while (spots[idx].status == SpotStatus.OCCUPIED) {
                idx = (idx + 1) % size;
                probes++;
            }

            spots[idx].status = SpotStatus.OCCUPIED;
            spots[idx].licensePlate = plate;
            spots[idx].entryTime = System.currentTimeMillis();
            vehicleToSpot.put(plate, idx);
            occupiedCount++;
            totalProbes += probes;
            totalParkings++;

            return String.format("Assigned spot #%-4d (preferred: #%d, probes: %d)", idx, preferred, probes);
        }

        public String exitVehicle(String plate) {
            Integer idx = vehicleToSpot.remove(plate);
            if (idx == null) return "Vehicle \"" + plate + "\" not found!";

            long durationMs = System.currentTimeMillis() - spots[idx].entryTime;
            double hours = durationMs / 3_600_000.0;
            double fee = Math.max(hours * RATE_PER_HOUR, 0.50);
            spots[idx].status = SpotStatus.DELETED;
            spots[idx].licensePlate = null;
            occupiedCount--;

            long mins = Math.max(durationMs / 60_000, 1);
            return String.format("Spot #%d freed, Duration: %dm,Fee: $%.2f", idx, mins, fee);
        }

        public String getStatistics() {
            double occupancy = (occupiedCount * 100.0) / size;
            double avgProbes = totalParkings == 0 ? 0 : (double) totalProbes / totalParkings;
            return String.format("Occupancy: %.0f%%,Spots: %d/%d, Avg Probes: %.2f",
                    occupancy, occupiedCount, size, avgProbes);
        }
    }

    public static void main(String[] args) {
        ParkingLot lot = new ParkingLot(500);

        System.out.println(lot.parkVehicle("ABC-1234"));
        System.out.println(lot.parkVehicle("ABC-1235"));
        System.out.println(lot.parkVehicle("XYZ-9999"));
        System.out.println(lot.parkVehicle("MNO-4567"));
        System.out.println(lot.parkVehicle("DEF-0001"));

        System.out.println();
        System.out.println(lot.exitVehicle("ABC-1234"));
        System.out.println(lot.exitVehicle("XYZ-9999"));
        System.out.println(lot.exitVehicle("GHOST-000")); // not found

        System.out.println();
        System.out.println("getStatistics():" + lot.getStatistics());
    }
}

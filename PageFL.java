public class PageReplacementSimulator {
    private static final int[] REFS = {7, 0, 1, 2, 0, 3, 0, 4, 2, 3, 0, 3, 2, 3};
    private static final int FRAMES = 3;

    public static void main(String[] args) {
        System.out.println("FIFO simulation (frames left->right):\n");
        int fifoFaults = simulateFIFO(REFS, FRAMES);
        System.out.println();

        System.out.println("LRU simulation (frames left->right):\n");
        int lruFaults = simulateLRU(REFS, FRAMES);
        System.out.println();

        System.out.println("Summary:");
        System.out.println("Total FIFO page faults = " + fifoFaults);
        System.out.println("Total LRU  page faults = " + lruFaults);
    }

    // Simulate FIFO and print table, return total faults
    private static int simulateFIFO(int[] refs, int frameCount) {
        int[] frames = new int[frameCount];
        for (int i = 0; i < frameCount; i++) frames[i] = -1; // -1 means empty

        int nextToReplace = 0; // circular pointer for FIFO
        int filled = 0;
        int faults = 0;

        printHeader();
        for (int r : refs) {
            boolean pageFault = false;
            int idx = indexOf(frames, r);
            if (idx == -1) {
                // not present -> fault
                pageFault = true;
                faults++;
                if (filled < frameCount) {
                    // place in next free slot
                    frames[nextToReplace] = r;
                    nextToReplace = (nextToReplace + 1) % frameCount;
                    filled++;
                } else {
                    // replace FIFO page
                    frames[nextToReplace] = r;
                    nextToReplace = (nextToReplace + 1) % frameCount;
                }
            }
            printRow(r, frames, pageFault);
        }
        System.out.println();
        return faults;
    }

    // Simulate LRU and print table, return total faults
    private static int simulateLRU(int[] refs, int frameCount) {
        int[] frames = new int[frameCount];
        int[] lastUsed = new int[frameCount]; // timestamps of last usage
        for (int i = 0; i < frameCount; i++) {
            frames[i] = -1; lastUsed[i] = -1;
        }

        int time = 0;
        int filled = 0;
        int faults = 0;

        printHeader();
        for (int r : refs) {
            boolean pageFault = false;
            int idx = indexOf(frames, r);
            if (idx != -1) {
                // hit: update timestamp
                lastUsed[idx] = time;
            } else {
                // miss
                pageFault = true;
                faults++;
                if (filled < frameCount) {
                    // put in first free spot
                    int putAt = firstIndex(frames, -1);
                    frames[putAt] = r;
                    lastUsed[putAt] = time;
                    filled++;
                } else {
                    // find LRU (smallest lastUsed)
                    int lruIndex = 0;
                    int minTime = lastUsed[0];
                    for (int i = 1; i < frameCount; i++) {
                        if (lastUsed[i] < minTime) {
                            minTime = lastUsed[i];
                            lruIndex = i;
                        }
                    }
                    frames[lruIndex] = r;
                    lastUsed[lruIndex] = time;
                }
            }
            printRow(r, frames, pageFault);
            time++;
        }
        System.out.println();
        return faults;
    }

    // Utility: find index of value in array, -1 if not found
    private static int indexOf(int[] arr, int val) {
        for (int i = 0; i < arr.length; i++) if (arr[i] == val) return i;
        return -1;
    }

    // Utility: find first index with given value
    private static int firstIndex(int[] arr, int val) {
        for (int i = 0; i < arr.length; i++) if (arr[i] == val) return i;
        return -1;
    }

    // Print table header
    private static void printHeader() {
        System.out.printf("%4s | %6s %6s %6s | %s\n", "Ref", "F1", "F2", "F3", "Fault");
        System.out.println("----------------------------------------------");
    }

    // Print single row
    private static void printRow(int ref, int[] frames, boolean fault) {
        String f1 = frames.length > 0 ? (frames[0] == -1 ? "-" : String.valueOf(frames[0])) : "-";
        String f2 = frames.length > 1 ? (frames[1] == -1 ? "-" : String.valueOf(frames[1])) : "-";
        String f3 = frames.length > 2 ? (frames[2] == -1 ? "-" : String.valueOf(frames[2])) : "-";
        System.out.printf("%4d | %6s %6s %6s | %s\n", ref, f1, f2, f3, fault ? "Yes" : "No");
    }
}

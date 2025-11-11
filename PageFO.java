public class PageReplacementFIFOOptimal {
    private static final int[] REFS = {7, 0, 1, 2, 0, 3, 0, 4, 2, 3, 0, 3, 2, 3};
    private static final int FRAMES = 4;

    public static void main(String[] args) {
        System.out.println("FIFO simulation (frames left->right):\n");
        int fifoFaults = simulateFIFO(REFS, FRAMES);
        System.out.println();

        System.out.println("OPTIMAL simulation (frames left->right):\n");
        int optFaults = simulateOPTIMAL(REFS, FRAMES);
        System.out.println();

        System.out.println("Summary:");
        System.out.println("Total FIFO page faults    = " + fifoFaults);
        System.out.println("Total OPTIMAL page faults = " + optFaults);
    }

    // FIFO simulation
    private static int simulateFIFO(int[] refs, int frameCount) {
        int[] frames = new int[frameCount];
        for (int i = 0; i < frameCount; i++) frames[i] = -1; // -1 indicates empty

        int pointer = 0; // FIFO pointer for replacement (circular)
        int filled = 0;
        int faults = 0;

        printHeader(frameCount);
        for (int r : refs) {
            boolean pageFault = false;
            int idx = indexOf(frames, r);
            if (idx == -1) {
                pageFault = true;
                faults++;
                if (filled < frameCount) {
                    frames[pointer] = r;
                    pointer = (pointer + 1) % frameCount;
                    filled++;
                } else {
                    frames[pointer] = r;
                    pointer = (pointer + 1) % frameCount;
                }
            }
            printRow(r, frames, pageFault);
        }
        return faults;
    }

    // OPTIMAL simulation
    private static int simulateOPTIMAL(int[] refs, int frameCount) {
        int[] frames = new int[frameCount];
        for (int i = 0; i < frameCount; i++) frames[i] = -1;

        int filled = 0;
        int faults = 0;

        printHeader(frameCount);
        for (int current = 0; current < refs.length; current++) {
            int r = refs[current];
            boolean pageFault = false;
            int idx = indexOf(frames, r);
            if (idx == -1) {
                pageFault = true;
                faults++;
                if (filled < frameCount) {
                    int putAt = firstIndex(frames, -1);
                    frames[putAt] = r;
                    filled++;
                } else {
                    // Choose the page whose next use is farthest in the future (or never used)
                    int evictIndex = -1;
                    int farthestNextUse = -1; // larger means used later
                    for (int i = 0; i < frameCount; i++) {
                        int page = frames[i];
                        int nextUse = Integer.MAX_VALUE; // if not found, treat as infinity
                        for (int k = current + 1; k < refs.length; k++) {
                            if (refs[k] == page) { nextUse = k; break; }
                        }
                        if (nextUse > farthestNextUse) {
                            farthestNextUse = nextUse;
                            evictIndex = i;
                        }
                    }
                    // evict at evictIndex
                    frames[evictIndex] = r;
                }
            }
            printRow(r, frames, pageFault);
        }
        return faults;
    }

    // Utility: find index of a value in array, -1 if not found
    private static int indexOf(int[] arr, int val) {
        for (int i = 0; i < arr.length; i++) if (arr[i] == val) return i;
        return -1;
    }

    // Utility: find first index of a value in array
    private static int firstIndex(int[] arr, int val) {
        for (int i = 0; i < arr.length; i++) if (arr[i] == val) return i;
        return -1;
    }

    // Print header depending on frame count
    private static void printHeader(int frameCount) {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%4s |","Ref"));
        for (int i = 1; i <= frameCount; i++) sb.append(String.format(" %6s", "F" + i));
        sb.append(String.format(" | %s\n", "Fault"));
        System.out.print(sb.toString());
        int dashes = 6 * (frameCount + 1) + 7;
        for (int i = 0; i < dashes; i++) System.out.print("-");
        System.out.println();
    }

    // Print a single row with frames and whether a fault occurred
    private static void printRow(int ref, int[] frames, boolean fault) {
        System.out.printf("%4d |", ref);
        for (int v : frames) {
            String s = (v == -1) ? "-" : String.valueOf(v);
            System.out.printf(" %6s", s);
        }
        System.out.printf(" | %s\n", fault ? "Yes" : "No");
    }
}

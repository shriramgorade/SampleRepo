import java.text.DecimalFormat;

public class PageReplacementSummary {
    private static final int[] REFS = {2, 3, 2, 1, 5, 2, 4, 5, 3, 2, 5, 2};
    private static final int FRAMES = 3;

    public static void main(String[] args) {
        int total = REFS.length;

        int fifoFaults = simulateFIFO(REFS, FRAMES);
        int fifoHits = total - fifoFaults;

        int lruFaults = simulateLRU(REFS, FRAMES);
        int lruHits = total - lruFaults;

        int optFaults = simulateOPT(REFS, FRAMES);
        int optHits = total - optFaults;

        DecimalFormat df = new DecimalFormat("#.##");

        double fifoHitRatio = fifoHits * 100.0 / total;
        double lruHitRatio = lruHits * 100.0 / total;
        double optHitRatio = optHits * 100.0 / total;

        System.out.println("FIFO: faults " + fifoFaults + ", hits " + fifoHits + ", hit ratio " + df.format(fifoHitRatio) + "%");
        System.out.println();
        System.out.println("LRU: faults " + lruFaults + ", hits " + lruHits + ", hit ratio " + df.format(lruHitRatio) + "%");
        System.out.println();
        System.out.println("OPT: faults " + optFaults + ", hits " + optHits + ", hit ratio " + df.format(optHitRatio) + "%");
    }

    // FIFO simulation: returns number of faults
    private static int simulateFIFO(int[] refs, int frameCount) {
        int[] frames = new int[frameCount];
        for (int i = 0; i < frameCount; i++) frames[i] = -1;
        int pointer = 0, filled = 0, faults = 0;

        for (int r : refs) {
            if (indexOf(frames, r) == -1) {
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
        }
        return faults;
    }

    // LRU simulation using last-used timestamps (returns faults)
    private static int simulateLRU(int[] refs, int frameCount) {
        int[] frames = new int[frameCount];
        int[] lastUsed = new int[frameCount];
        for (int i = 0; i < frameCount; i++) { frames[i] = -1; lastUsed[i] = -1; }
        int time = 0, filled = 0, faults = 0;

        for (int r : refs) {
            int idx = indexOf(frames, r);
            if (idx != -1) {
                // hit: update timestamp
                lastUsed[idx] = time;
            } else {
                // miss
                faults++;
                if (filled < frameCount) {
                    int putAt = firstIndex(frames, -1);
                    frames[putAt] = r;
                    lastUsed[putAt] = time;
                    filled++;
                } else {
                    // find LRU (smallest lastUsed)
                    int lruIndex = 0;
                    int minTime = lastUsed[0];
                    for (int i = 1; i < frameCount; i++) {
                        if (lastUsed[i] < minTime) { minTime = lastUsed[i]; lruIndex = i; }
                    }
                    frames[lruIndex] = r;
                    lastUsed[lruIndex] = time;
                }
            }
            time++;
        }
        return faults;
    }

    // OPTIMAL simulation: looks ahead to choose victim (returns faults)
    private static int simulateOPT(int[] refs, int frameCount) {
        int[] frames = new int[frameCount];
        for (int i = 0; i < frameCount; i++) frames[i] = -1;
        int filled = 0, faults = 0;

        for (int current = 0; current < refs.length; current++) {
            int r = refs[current];
            if (indexOf(frames, r) == -1) {
                faults++;
                if (filled < frameCount) {
                    int putAt = firstIndex(frames, -1);
                    frames[putAt] = r;
                    filled++;
                } else {
                    int evictIndex = -1;
                    int farthestNext = -1;
                    for (int i = 0; i < frameCount; i++) {
                        int page = frames[i];
                        int nextUse = Integer.MAX_VALUE;
                        for (int k = current + 1; k < refs.length; k++) {
                            if (refs[k] == page) { nextUse = k; break; }
                        }
                        if (nextUse > farthestNext) { farthestNext = nextUse; evictIndex = i; }
                    }
                    frames[evictIndex] = r;
                }
            }
        }
        return faults;
    }

    private static int indexOf(int[] arr, int val) {
        for (int i = 0; i < arr.length; i++) if (arr[i] == val) return i;
        return -1;
    }

    private static int firstIndex(int[] arr, int val) {
        for (int i = 0; i < arr.length; i++) if (arr[i] == val) return i;
        return -1;
    }
}

import java.util.*;

class Macro { // 'class' not 'Class'
    String name;
    int mdtIndex;

    Macro(String name, int mdtIndex) {
        this.name = name;
        this.mdtIndex = mdtIndex;
    }
}

public class PassOneMacroProcessor { // 'class' not 'Class'
    static List<Macro> MNT = new ArrayList<>();
    static List<String> MDT = new ArrayList<>();
    static Map<String, Integer> ALA = new LinkedHashMap<>();

    public static void main(String[] args) {

        // 🔹 Input Program embedded inside the code
        String[] input = {
            "START",
            "MACRO",
            "INCR &ARG3 &ARG2",
            "ADD AREG &ARG1",
            "MOVER BREG &ARG1",
            "MEND",
            "MACRO",
            "PVG &ARG2 &ARG1",
            "SUB AREG &ARG2",
            "MOVER CREG &ARG1",
            "MEND",
            "INCR",
            "DECR",
            "DATA2",
            "END"
        };

        int i = 0, mdtc = 0;

        while (i < input.length) {
            String line = input[i].trim();

            // 🔸 When a macro starts
            if (line.equalsIgnoreCase("MACRO")) {
                i++;
                String header = input[i].trim();
                String[] parts = header.split("\\s+");
                String macroName = parts[0];

                // Add macro to MNT
                MNT.add(new Macro(macroName, mdtc + 1));

                // Build ALA for this macro
                ALA.clear();
                for (int k = 1; k < parts.length; k++) {
                    ALA.put(parts[k], k);
                }

                // Read macro body until MEND
                i++;
                while (!input[i].trim().equalsIgnoreCase("MEND")) {
                    String temp = input[i].trim();

                    // Replace arguments with positional notation (#1, #2, ...)
                    for (Map.Entry<String, Integer> entry : ALA.entrySet()) {
                        String arg = entry.getKey();
                        int pos = entry.getValue();
                        if (temp.contains(arg)) {
                            temp = temp.replace(arg, "#" + pos);
                        }
                    }

                    MDT.add(temp);
                    mdtc++;
                    i++;
                }

                // Add MEND to MDT
                MDT.add("MEND");
                mdtc++;
            }
            i++;
        }

        // 🔹 Display the Tables
        System.out.println("=== PASS-I OUTPUT ===\n");

        System.out.println("MACRO NAME TABLE (MNT):");
        System.out.println("Index\tMacro Name\tMDT Index");
        for (int j = 0; j < MNT.size(); j++) {
            Macro m = MNT.get(j);
            System.out.println((j + 1) + "\t" + m.name + "\t\t" + m.mdtIndex);
        }

        System.out.println("\nMACRO DEFINITION TABLE (MDT):");
        for (int j = 0; j < MDT.size(); j++) {
            System.out.println((j + 1) + "\t" + MDT.get(j));
        }
    }
}

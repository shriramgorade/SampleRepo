import java.util.*;

class PassOneAssembler {
    static class Symbol {
        String name;
        int address;
        Symbol(String name, int address) {
            this.name = name;
            this.address = address;
        }	
    }

    static class Literal {
        String value;
        int address;
        Literal(String value, int address) {
            this.value = value;
            this.address = address;
        }
    }

    public static void main(String[] args) {
        String code[] = {
            "START 100",
            "A DS 3",
            "L1 MOVER AREG, B",
            "ADD AREG, C",
            "MOVEM AREG, ='2'",
            "MOVEM AREG, ='3'",
            "D EQU A+1",
            "LTORG",
            "L2 PRINT D",
            "MOVEM AREG, ='4'",
            "MOVEM AREG, ='5'",
            "ORIGIN L2+1",
            "LTORG",
            "B DC '19'",
            "C DC '17'",
            "END"
        };

        int LC = 0;
        List<Symbol> SYMTAB = new ArrayList<>();
        List<Literal> LITTAB = new ArrayList<>();
        List<Integer> POOLTAB = new ArrayList<>();
        POOLTAB.add(0); // first pool starts at 0

        for (String line : code) {
            String parts[] = line.split(" ");
            String op = parts[0];

            switch (op) {
                case "START":
                    LC = Integer.parseInt(parts[1]);
                    break;

                case "DS":
                    SYMTAB.add(new Symbol(parts[0], LC));
                    LC += Integer.parseInt(parts[1]);
                    break;

                case "DC":
                    SYMTAB.add(new Symbol(parts[0], LC));
                    LC++;
                    break;

                case "EQU":
                    for (Symbol s : SYMTAB) {
                        if (s.name.equals(parts[2].split("\\+")[0])) {
                            SYMTAB.add(new Symbol(parts[0], s.address + 1));
                        }
                    }
                    break;

                case "LTORG":
                case "END":
                    for (Literal lit : LITTAB) {
                        if (lit.address == -1) {
                            lit.address = LC++;
                        }
                    }
                    POOLTAB.add(LITTAB.size());
                    break;

                default:
                    // if line has label at start
                    if (!parts[0].matches("MOVER|ADD|MOVEM|PRINT|ORIGIN")) {
                        SYMTAB.add(new Symbol(parts[0], LC));
                    }

                    // check literals
                    for (String p : parts) {
                        if (p.startsWith("='")) {
                            LITTAB.add(new Literal(p, -1));
                        }
                    }

                    LC++;
            }
        }

        System.out.println("\nSYMBOL TABLE:");
        for (Symbol s : SYMTAB)
            System.out.println(s.name + "\t" + s.address);

        System.out.println("\nLITERAL TABLE:");
        for (Literal l : LITTAB)
            System.out.println(l.value + "\t" + l.address);

        System.out.println("\nPOOL TABLE:");
        for (int p : POOLTAB)
            System.out.println(p);
    }
}

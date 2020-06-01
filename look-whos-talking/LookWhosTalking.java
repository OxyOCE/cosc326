import java.util.*;
import java.io.*;

public class LookWhosTalking {
    private static Map<String, String> dict;
    private static List<String> sentences;
    private static Set<String> progressive;
    private static Set<String> simplePast;
    private static Set<String> simplePresentAndFuture;
    private static Set<String> thirdPersonSingular;
    private static Map<Person, Map<Gender, String>> pronouns;

    public static void main(String[] args) {
        createDicts();
        sentences = new ArrayList<String>();
        Scanner s = new Scanner(System.in);
        while (s.hasNextLine()) {
            sentences.add(s.nextLine().toLowerCase());
        }
        s.close();

        PrintStream out;
        try {
            out = new PrintStream(System.out, true, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            out = System.out;
        }

        for (String sentence : sentences) {
            String[] words = sentence.split(" ");

            if (words.length < 2) {
                out.println("invalid sentence");
                continue;
            }

            String v = getVerb(words);
            if (v == null) {
                out.println("unknown word \"" + words[words.length - 1] + "\" sentences should end with a verb");
                continue;
            }

            Person p = getPerson(words);
            Gender g = getGender(words);

            if (p == null || g == null) {
                out.println("invalid pronoun");
                continue;
            }

            Tense t = getTense(words, p, g);

            if (t == null) {
                out.println("invalid tense");
                continue;
            }

            out.println(getTenseString(t) + " " + v + " " + pronouns.get(p).get(g));
        }

        out.close();
    }

    private static String getVerb(String[] words) {
        String verb = words[words.length - 1];

        if (dict.containsKey(verb)) {
            return dict.get(verb);
        }

        return null;
    }

    private static Person getPerson(String[] words) {
        // First person singular
        if (words[0].equals("i")) {
            return Person.FIRSTEXCL;
        }

        // Accounts for "you two"
        if (words[1].equals("two")) {
            return Person.SECOND;
        }

        // First person dual or plural
        if (words[0].equals("we")) {
            if (words.length > 2) {
                if (words[2].charAt(0) == 'e') {
                    return Person.FIRSTEXCL;
                }

                if (words[2].charAt(0) == 'i') {
                    return Person.FIRSTINCL;
                }
            }
        }

        // Second person
        if (words[0].equals("you")) {
            return Person.SECOND;
        }

        // Third person
        if (words[0].equals("he") ^ words[0].equals("she") ^ words[0].equals("they")) {
            return Person.THIRD;
        }

        // Invalid
        return null;
    }

    private static Gender getGender(String[] words) {
        // First person singular
        if (words[0].equals("i") ^ words[0].equals("he") ^ words[0].equals("she")) {
            return Gender.SINGULAR;
        }

        // Accounts for "you two"
        if (words[1].equals("two")) {
            return Gender.DUAL;
        }

        // Third person singular
        if (words[0].equals("he") ^ words[0].equals("she")) {
            return Gender.SINGULAR;
        }

        // The rest
        int gender;
        try {
            gender = Integer.parseInt(words[1].substring(1, words[1].length()));
        } catch (NumberFormatException e) {
            return null;
        }

        switch (gender) {
            case 3: return Gender.PLURAL;
            case 2: return Gender.DUAL;
            case 1: return Gender.SINGULAR;
            default:
                if (gender > 3) {
                    return Gender.PLURAL;
                }
                return null; // Invalid
        }
    }

    private static Tense getTense(String[] words, Person p, Gender g) {
        // (First & third) person singular
        if ((p == Person.FIRSTEXCL ^ p == Person.THIRD) && g == Gender.SINGULAR) {
            // Future
            if (words[1].equals("will")) {
                // Simple XOR progressive
                if (simplePresentAndFuture.contains(words[2]) ^ (words[2].equals("be") && progressive.contains(words[3]))) {
                    return Tense.FUTURE;
                }
            }

            // Past simple XOR progressive
            if (simplePast.contains(words[1]) ^ (words[1].equals("was") && progressive.contains(words[2]))) {
                return Tense.PAST;
            }

            // Present first
            if (p == Person.FIRSTEXCL) {
                // Simple XOR progressive
                if (simplePresentAndFuture.contains(words[1]) ^ (words[1].equals("am") && progressive.contains(words[2]))) {
                    return Tense.PRESENT;
                }
            } else { // Present third
                // Simple XOR progressive
                if (thirdPersonSingular.contains(words[1]) ^ (words[1].equals("is") && progressive.contains(words[2]))) {
                    return Tense.PRESENT;
                }
            }
        } else { // (Second person single) & (dual & plural)
            int o = words[1].equals("two") ? -1 : 0; // Accounts for "you two"

            // Future
            if (words[3 + o].equals("will")) {
                // Simple XOR progressive
                if (simplePresentAndFuture.contains(words[4 + o]) ^ (words[4 + o].equals("be") && progressive.contains(words[5 + o]))) {
                    return Tense.FUTURE;
                }
            }

            // Past simple XOR progressive
            if (simplePast.contains(words[3 + o]) ^ (words[3 + o].equals("were") && progressive.contains(words[4 + o]))) {
                return Tense.PAST;
            }

            // Present simple XOR progressive
            if (simplePresentAndFuture.contains(words[3 + o]) ^ (words[3 + o].equals("are") && progressive.contains(words[4 + o]))) {
                return Tense.PRESENT;
            }
        }

        return null;
    }

    private static String getTenseString(Tense t) {
        switch (t) {
            case PAST: return "I";
            case PRESENT: return "Kei te";
            case FUTURE: return "Ka";
            default: return null;
        }
    }

    private static void createDicts() {
        dict = new LinkedHashMap<String, String>();
        progressive = new LinkedHashSet<String>();
        simplePast = new LinkedHashSet<String>();
        simplePresentAndFuture = new LinkedHashSet<String>();
        thirdPersonSingular = new LinkedHashSet<String>();
        pronouns = new LinkedHashMap<Person, Map<Gender, String>>();

        dict.put("go", "haere"); simplePresentAndFuture.add("go");
        dict.put("goes", "haere");  thirdPersonSingular.add("goes");
        dict.put("going", "haere"); progressive.add("going");
        dict.put("went", "haere");  simplePast.add("went");

        dict.put("make", "hanga"); simplePresentAndFuture.add("make");
        dict.put("makes", "hanga"); thirdPersonSingular.add("makes");
        dict.put("making", "hanga"); progressive.add("making");
        dict.put("made", "hanga"); simplePast.add("made");

        dict.put("see", "kite"); simplePresentAndFuture.add("see");
        dict.put("sees", "kite"); thirdPersonSingular.add("sees");
        dict.put("seeing", "kite"); progressive.add("seeing");
        dict.put("saw", "kite"); simplePast.add("saw");

        dict.put("want", "hiahia"); simplePresentAndFuture.add("want");
        dict.put("wants", "hiahia"); thirdPersonSingular.add("wants");
        dict.put("wanting", "hiahia"); progressive.add("wanting");
        dict.put("wanted", "hiahia"); simplePast.add("wanted");

        dict.put("call", "karanga"); simplePresentAndFuture.add("call");
        dict.put("calls", "karanga"); thirdPersonSingular.add("calls");
        dict.put("calling", "karanga"); progressive.add("calling");
        dict.put("called", "karanga"); simplePast.add("called");

        dict.put("ask", "p\u0101tai"); simplePresentAndFuture.add("ask");
        dict.put("asks", "p\u0101tai"); thirdPersonSingular.add("asks");
        dict.put("asking", "p\u0101tai"); progressive.add("asking");
        dict.put("asked", "p\u0101tai"); simplePast.add("asked");

        dict.put("read", "p\u0101nui"); simplePresentAndFuture.add("read"); simplePast.add("read");
        dict.put("reads", "p\u0101nui"); thirdPersonSingular.add("reads");
        dict.put("reading", "p\u0101nui"); progressive.add("reading");

        dict.put("learn", "ako"); simplePresentAndFuture.add("learn");
        dict.put("learns", "ako"); thirdPersonSingular.add("learns");
        dict.put("learning", "ako"); progressive.add("learning");
        dict.put("learned", "ako"); simplePast.add("learned");

        Map<Gender, String> firstIncl = new LinkedHashMap<Gender, String>();
        Map<Gender, String> firstExcl = new LinkedHashMap<Gender, String>();
        Map<Gender, String> second = new LinkedHashMap<Gender, String>();
        Map<Gender, String> third = new LinkedHashMap<Gender, String>();

        firstIncl.put(Gender.DUAL, "t\u0101ua");
        firstIncl.put(Gender.PLURAL, "t\u0101tou");

        firstExcl.put(Gender.SINGULAR, "au");
        firstExcl.put(Gender.DUAL, "m\u0101ua");
        firstExcl.put(Gender.PLURAL, "m\u0101tou");

        second.put(Gender.SINGULAR, "koe");
        second.put(Gender.DUAL, "k\u014Drua");
        second.put(Gender.PLURAL, "koutou");

        third.put(Gender.SINGULAR, "ia");
        third.put(Gender.DUAL, "r\u0101ua");
        third.put(Gender.PLURAL, "r\u0101tou");

        pronouns.put(Person.FIRSTINCL, firstIncl);
        pronouns.put(Person.FIRSTEXCL, firstExcl);
        pronouns.put(Person.SECOND, second);
        pronouns.put(Person.THIRD, third);
    }

    private static enum Tense {
        PAST, PRESENT, FUTURE
    }

    private static enum Person {
        FIRSTEXCL, FIRSTINCL, SECOND, THIRD
    }

    private static enum Gender {
        SINGULAR, DUAL, PLURAL
    }
}

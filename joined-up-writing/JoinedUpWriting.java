import java.util.*;

public class JoinedUpWriting {
    private static Map<Character, List<String>> dict;
    private static String start;
    private static String goal;
    private static Map<String, Word> words;

    public static void main(String[] args) {
        dict = new LinkedHashMap<Character, List<String>>();
        start = args[0];
        goal = args[1];
        words = new LinkedHashMap<String, Word>();

        for (char ch = 'a'; ch <= 'z'; ch++) {
            dict.put(ch, new ArrayList<String>());
        }

        Scanner sc = new Scanner(System.in);
        while (sc.hasNextLine()) {
            String line = sc.nextLine();
            dict.get(line.charAt(0)).add(line);
        }
        sc.close();

        Word s = new Word(start);
        words.put(start, s);

        dict.get(goal.charAt(0)).add(goal);
        Word g = new Word(goal);
        words.put(goal, g);

        dict.forEach((key, list) -> {
            for (String str : list) {
                if (str.equals(start) || str.equals(goal)) {
                    continue;
                }
                Word w = new Word(str);
                words.put(str, w);
            }
        });

        words.forEach((str, word) -> {
            word.setAdj(getSinglyLinkedWords(word));
        });

        printGraph(s);

        words.forEach((str, word) -> {
            word.getAdj().clear();
            word.setVisited(false);
            word.setParent(null);
            word.setAdj(getDoublyLinkedWords(word));
        });

        printGraph(s);
    }

    private static Word bfs(Word v) {
        LinkedList<Word> q = new LinkedList<Word>();
        v.setVisited(true);
        q.add(v);

        while (q.size() > 0) {
            Word w = q.poll();
            if (w.getData().equals(goal)) {
                return w;
            }
            w.getAdj().forEach(x -> {
                if (!x.isVisited()) {
                    x.setVisited(true);
                    x.setParent(w);
                    q.add(x);
                }
            });
        }

        return null;
    }

    private static void printGraph(Word s) {
        Word res = bfs(s);
        List<String> out = new ArrayList<String>();

        while (res != null) {
            out.add(res.getData());
            res = res.getParent();
        }

        System.out.print(Integer.toString(out.size()));
        for (int i = out.size() - 1; i >= 0; i--) {
            System.out.print(" " + out.get(i));
        }
        System.out.println();
    }

    private static Set<Word> getSinglyLinkedWords(Word w) {
        Set<Word> adj = new LinkedHashSet<Word>();
        int suffixIndex = w.getData().length() - 1;

        while (suffixIndex >= 0) {
            String suffix = w.getData().substring(suffixIndex);
            for (String str : dict.get(suffix.charAt(0))) {
                if (isSinglyLinked(w.getData(), str, suffix)) {
                    adj.add(words.get(str));
                }
            }
            suffixIndex--;
        }

        return adj;
    }

    private static Set<Word> getDoublyLinkedWords(Word w) {
        Set<Word> adj = new LinkedHashSet<Word>();
        int suffixIndex = -Math.floorDiv(-(w.getData().length() - 1), 2);

        while (suffixIndex >= 0) {
            String suffix = w.getData().substring(suffixIndex);
            for (String str : dict.get(suffix.charAt(0))) {
                if (isDoublyLinked(w.getData(), str, suffix)) {
                    adj.add(words.get(str));
                }
            }
            suffixIndex--;
        }

        return adj;
    }

    private static boolean isSinglyLinked(String a, String b, String suffix) {
        int maxLength = suffix.length() * 2;
        return (a.length() <= maxLength || b.length() <= maxLength) && isLinked(a, b, suffix);
    }

    private static boolean isDoublyLinked(String a, String b, String suffix) {
        int maxLength = suffix.length() * 2;
        return (a.length() <= maxLength && b.length() <= maxLength) && isLinked(a, b, suffix);
    }

    private static boolean isLinked(String a, String b, String suffix) {
        if (b.length() < suffix.length()) {
            return false;
        }
        return suffix.equals(b.substring(0, suffix.length()));
    }

    private static class Word {
        String data;
        Set<Word> adj;
        boolean visited;
        Word parent;

        public Word(String data) {
            this.data = data;
            this.adj = new LinkedHashSet<Word>();
            this.visited = false;
            this.parent = null;
        }

        public String getData() {
            return data;
        }

        public Set<Word> getAdj() {
            return adj;
        }

        public boolean isVisited() {
            return visited;
        }

        public Word getParent() {
            return parent;
        }

        public void setAdj(Set<Word> a) {
            adj = a;
        }

        public void setVisited(boolean v) {
            this.visited = v;
        }

        public void setParent(Word p) {
            this.parent = p;
        }

        public String toString() {
            return data;
        }
    }
}

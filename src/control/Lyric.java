package control;

import javafx.util.Duration;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Lyric {

    private TreeMap<Integer, ArrayList<String>> lyricMap;
    private HashMap<Integer, ArrayList<Object>> linkedMap;
    private Iterator<Map.Entry<Integer, ArrayList<String>>> iterator = null;
    private Map.Entry<Integer, ArrayList<String>> cur = null;

    public Lyric(File file) {
        FileInputStream inputStream;
        try {
            inputStream = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            throw new IllegalArgumentException(e);
        }
        lyricMap = new TreeMap<>();
        linkedMap = new HashMap<>();
        String pattern = "(\\[)(\\d+:\\d+(\\.\\d+)?)(\\])(.*)";
        Pattern r = Pattern.compile(pattern);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        String temp;
        try {
            while ((temp = bufferedReader.readLine()) != null) {
                try {
                    Matcher matcher = r.matcher(temp);
                    if (!matcher.find())
                        continue;
                    String[] splits = matcher.group(2).split(":");
                    int seconds = Integer.parseInt(splits[0]) * 60 + (int) (Double.parseDouble(splits[1]) + 0.5);
                    if (lyricMap.containsKey(seconds)) {
                        lyricMap.get(seconds).add(matcher.group(5));
                    } else {
                        lyricMap.put(seconds, new ArrayList<>() {{
                            add(matcher.group(5));
                        }});
                    }
                } catch (Exception e) {
                    throw new IOError(e);
                }
            }
        } catch (
                IOException e) {
            throw new IOError(e);
        }

        try {
            inputStream.close();
            bufferedReader.close();
        } catch (
                IOException e) {
            throw new IOError(e);
        }

    }

    final public Boolean contains(Duration duration) {
        int cmp = (int) (duration.toSeconds() + 0.5);
        return lyricMap.containsKey(cmp);
    }

    final public void initIterator() {
        iterator = lyricMap.entrySet().iterator();
    }

    final public boolean hasNext() {
        return iterator.hasNext();
    }

    final public void link(Object object) {
        if (linkedMap.containsKey(cur.getKey())) {
            linkedMap.get(cur.getKey()).add(object);
        } else {
            linkedMap.put(cur.getKey(), new ArrayList<>() {{
                add(object);
            }});
        }
    }

    final public String[] next() {
        cur = iterator.next();
        ArrayList<String> arrayList = cur.getValue();
        return arrayList.toArray(new String[arrayList.size()]);
    }

    final public String[] get(Duration duration) {
        int size = lyricMap.get((int) (duration.toSeconds() + 0.5)).size();
        return lyricMap.get((int) (duration.toSeconds() + 0.5)).toArray(new String[size]);
    }

    public final ArrayList<Object> getLinked(Duration duration) {
        return linkedMap.get((int) (duration.toSeconds() + 0.5));
    }

}

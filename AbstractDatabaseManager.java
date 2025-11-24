package Backend;

import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.List;

public abstract class AbstractDatabaseManager<T> {

    protected String filename;

    public AbstractDatabaseManager(String filename) {
        this.filename = filename;
    }

    public void save(List<T> items) {
        JSONArray arr = new JSONArray();

        for (int i = 0; i < items.size(); i++) {
            T item = items.get(i);
            arr.add(convertToJSON(item));
        }

        try (FileWriter writer = new FileWriter(filename)) {
            writer.write(arr.toJSONString());
            System.out.println("[Database] Saved " + items.size() + " items to " + filename);
        } catch (Exception e) {
            System.err.println("[Database] Error saving to " + filename);
            e.printStackTrace();
        }
    }

    public List<T> load() {
        List<T> items = createEmptyList();
        JSONParser parser = new JSONParser();
        File file = new File(filename);

        if (!file.exists()) {
            System.out.println("[Database] No " + filename + " found empty start");
            return items;
        }

        try (FileReader reader = new FileReader(file)) {
            JSONArray arr = (JSONArray) parser.parse(reader);

            for (int i = 0; i < arr.size(); i++) {
                Object obj = arr.get(i);
                T item = convertFromJSON(obj);

                if (item != null) {
                    items.add(item);
                }
            }

            System.out.println("[Database] Loaded " + items.size() + " items from " + filename);
        } catch (Exception e) {
            System.err.println("[Database] Failed loading " + filename + " empty start");
            e.printStackTrace();
        }

        return items;
    }

    public void clear() {
        try (FileWriter writer = new FileWriter(filename)) {
            writer.write("[]");
            System.out.println("[Database] Cleared " + filename);
        } catch (Exception e) {
            System.err.println("[Database] Error clearing " + filename);
            e.printStackTrace();
        }
    }

    public boolean fileExists() {
        return new File(filename).exists();
    }

    public int getCount() {
        return load().size();
    }

    protected abstract Object convertToJSON(T item);

    protected abstract T convertFromJSON(Object jsonObject);

    protected abstract List<T> createEmptyList();

}
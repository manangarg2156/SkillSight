import java.io.IOException;
import java.io.Serializable;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

/** Reads and writes the local SkillSight data file safely. */
final class LocalDataStore {
    private LocalDataStore() { }

    static Object load(Path file) {
        try (ObjectInputStream input = new ObjectInputStream(Files.newInputStream(file))) {
            return input.readObject();
        } catch (Exception ignored) {
            return null;
        }
    }

    static void save(Serializable data, Path file) throws IOException {
        Files.createDirectories(file.getParent());
        Path temporary = file.resolveSibling(file.getFileName() + ".tmp");
        try (ObjectOutputStream output = new ObjectOutputStream(Files.newOutputStream(temporary))) {
            output.writeObject(data);
        }
        try {
            Files.move(temporary, file, StandardCopyOption.REPLACE_EXISTING,
                    StandardCopyOption.ATOMIC_MOVE);
        } catch (AtomicMoveNotSupportedException ignored) {
            Files.move(temporary, file, StandardCopyOption.REPLACE_EXISTING);
        }
    }
}

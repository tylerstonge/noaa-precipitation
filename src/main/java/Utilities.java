import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

public class Utilities {
    /**
    * Takes an object and returns its corresponding byte array.
    */
    public static byte[] toByteArray(Object o) {
        ByteArrayOutputStream bos;
        ObjectOutputStream oos;
        byte[] result = null;
        try {
            bos = new ByteArrayOutputStream();
            oos = new ObjectOutputStream(bos);
            oos.writeObject(o);
            oos.flush();
            result = bos.toByteArray();
            if (oos != null) { oos.close(); }
            if (bos != null) { bos.close(); }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
}
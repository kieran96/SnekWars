package util;

/**
 * Created by Mitchell on 20-May-17.
 * Used for containing individual Moves within a @MoveSet.java
 */
public class MoveKey {
    private final int key;

    public MoveKey(int id) {
        this.key = id;
    }
    public MoveKey(MoveKey mK) {
        this.key = mK.getKey();
    }

    public int getKey() {
        return this.key;
    }
    @Override
    public int hashCode() {
        return (key * 31);
    }
    @Override
    public boolean equals(Object obj) {
        if(obj == null) {
            return false;
        }
        if(obj instanceof MoveKey) {
            MoveKey temp = (MoveKey) obj;
            return (temp.getKey() == this.getKey());
        }
        return false;
    }
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.key);
        return sb.toString();
    }
}

package util;

/**
 * Created by Mitchell on 20-May-17.
 */
public class MoveSet {
    private MoveKey[] moves;
    /*
        A MoveSet expects 4 moves in this order: UP, LEFT, DOWN, RIGHT.
        A Move MUST be encapsulated within a MoveKey.
     */
    public MoveSet(MoveKey UP, MoveKey LEFT, MoveKey DOWN, MoveKey RIGHT) {
        moves = new MoveKey[4];
        moves[0] = UP;
        moves[1] = LEFT;
        moves[2] = DOWN;
        moves[3] = RIGHT;
    }
    public MoveSet(MoveSet move) {
        this.moves = move.getMoveSet();
    }
    public MoveKey[] getMoveSet() {
        return moves;
    }
    public void setMoveSet(MoveKey[] newKeySet) {
        if(newKeySet.length > 4) {
            throw new IndexOutOfBoundsException("Too many moves in MoveSet, Must be 4.");
        } else if(newKeySet.length < 4) {
            throw new IndexOutOfBoundsException("Too little moves in MoveSet, Must be 4.");
        } else {
            this.moves = newKeySet;
        }
    }
    @Override
    public String toString() {
        StringBuilder b = new StringBuilder();
        b.append("The moves contained within this MoveSet: ");
        for(MoveKey key: this.moves) {
            b.append(key.toString()).append(" ");
        }
        return b.toString();
    }
    @Override
    public int hashCode() {
        int prime = 31;
        int result = 1;

        for(MoveKey key : this.moves) {
            result *= (key.hashCode() * 31);
        }
        return (result * 31);
    }
    @Override
    public boolean equals(Object obj) {
        if(obj == null) {
            return false;
        }
        if(obj instanceof MoveSet) {
            MoveSet temp = (MoveSet) obj;
            if(temp.hashCode() == this.hashCode()) {
                return true;
            }
        }
        return false;
    }
}

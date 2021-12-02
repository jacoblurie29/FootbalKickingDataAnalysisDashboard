import java.util.ArrayList;


    /**
     * The following abstract class holds a session of kicks and their distances.
     * Class will be modified in future to add more information about each kick.
     *
     * @author  Jacob Lurie
     * @version 1.0
     * @since   2021-10-28
     */

public class FieldGoalSession {


    private ArrayList<Integer> kicks;
    private ArrayList<Double> hashes;
    private String sessionID;
    private int month;
    private int year;
    private ArrayList<Boolean> make;
    private ArrayList<Integer> kickNumberInSession;

    // Constructor
    public FieldGoalSession(String s, int m, int y) {
        kicks = new ArrayList<>();
        hashes = new ArrayList<>();
        make = new ArrayList<>();
        sessionID = s;
        month = m;
        year = y;
        kickNumberInSession = new ArrayList<>();
    }

    public int getMonth() {
        return month;
    }

    public int getYear() {
        return year;
    }

    public ArrayList<Integer> getAllKicks() {
        return kicks;
    }

    public void addKick(int distance, double hash, boolean m, int kickNumber) {
        kicks.add(distance);
        hashes.add(hash);
        make.add(m);
        kickNumberInSession.add(kickNumber);
    }

    public int getNumKicks() {
        return kicks.size();
    }

    public String getSessionID() {
        return sessionID;
    }


    public void setSessionID(String sessionID) {
        this.sessionID = sessionID;
    }

    public int getKickAtIndex (int index) {
        return kicks.get(index);
    }

    // 1 to 5 (Right to left)
    public double getHash(int index) {
        return hashes.get(index);
    }

    public boolean getMake(int index) {
        return make.get(index);
    }

    public int getKickNum(int index) {
        return kickNumberInSession.get(index);
    }


}


import java.util.ArrayList;

/**
 * The following abstract class holds all sessions of kicks and their distances.
 * Class will be modified in future to add more information about each kick.
 * Utilizes FieldGoalSession class to hold each individual session.
 *
 * @author  Jacob Lurie
 * @version 1.0
 * @since   2021-10-28
 */

public class FieldGoalTotalSessions {

    private ArrayList<FieldGoalSession> SessionsArray;



    // Constructor
    public FieldGoalTotalSessions () {

        SessionsArray = new ArrayList<>();

    }

    public int getNumSessions() {
        return SessionsArray.size();
    }

    /*
        Adds a kick to the sessions based on the sessionID. If there is a session that exists with that ID, adds kick
        to that session. If ID doesn't exist, creates new session and adds to array of sessions
     */
    public void addKick(String sessionID, int distance, int m, int y, double hash, boolean make, int kickNum) {
        int i = 0;

        if(SessionsArray.size() == 0) {
            FieldGoalSession newSession = new FieldGoalSession(sessionID, m, y);
            newSession.addKick(distance, hash, make, kickNum);
            SessionsArray.add(newSession);


        } else {

            while (i < SessionsArray.size()) {
                if(SessionsArray.get(i).getSessionID().compareTo(sessionID) == 0) {
                    break;
                } else {
                    i++;
                }
            }

            if (i == SessionsArray.size()) {
                // case - ID doesn't exist
                FieldGoalSession newSession = new FieldGoalSession(sessionID, m, y);
                newSession.addKick(distance, hash, make, kickNum);
                SessionsArray.add(newSession);

            } else {
                // case = ID already exists
                SessionsArray.get(i).addKick(distance, hash, make, kickNum);
            }
        }
    }

    // Returns field goal session with a specified session ID
    public FieldGoalSession getFieldGoalSession(String sessionID) {
        for (int i = 0; i < SessionsArray.size(); i++) {
            if(SessionsArray.get(i).getSessionID().compareTo(sessionID) == 0) {
                return SessionsArray.get(i);
            }
        }
        return null;
    }

    // Returns field goal session at specified index
    public FieldGoalSession getFieldGoalSession(int index) {
        return SessionsArray.get(index);
    }

    // Prints all sessions in organized list
    public void printAllSessions() {

        for(int i = 0; i < SessionsArray.size(); i++) {
            System.out.println("SessionID: " + SessionsArray.get(i).getSessionID());
            for(int j = 0; j < SessionsArray.get(i).getNumKicks(); j++) {
                System.out.println("Kick #" + j + ": " + SessionsArray.get(i).getKickAtIndex(j));

            }
        }


    }




}

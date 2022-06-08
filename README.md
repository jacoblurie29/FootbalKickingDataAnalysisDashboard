# FootbalKickingDataAnalysisDashboard
Utilizes 3 CSV's of football kicking and punting data to calculate general statistics.

The app this data comes from collects field goal, punting, and kickoff data from all different levels of kickers. It takes all recorded data and sorts it into an MxN CSV file 
where M is the number categories of data and N is the number of kicks recorded. Each user has an assigned user ID and each session that they record is given a unique ID. Data is 
taken in integers which in most cases is in either yards or seconds depending on what data is in question.

The UI takes inputs from the user to narrow the availible data, calculate, and graph statistics.

For calculations:

The data is sorted into the field goal sessions data type (FieldGoalTotalSessions) that contains an arraylist of a smaller abstract data type (FieldGoalSession) that contains the 
ID of the session, the distance of all kicks in that session, the hashes of all the kicks in the session, and if every kick was a miss or a make. Sessions have a minimum of one 
kick and only have one string ID. Since adding a random kick requires the session ID to be found, the overarching FieldGoalTotalSessions object contains an adding function that 
first searches then adds the kick.

There is no need to sort the individual kicks within each session since they are already sorted within the CSV.

The statistical concept of the interface was tested in the SimpleKickingDataAnalysis.java file and moved to the SimpleKickingInterface.java file which is connected to the SimpleKickingInterface.form.

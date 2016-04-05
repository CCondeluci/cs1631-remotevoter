/*
Modified by Carmen Condeluci for CS1631
2-18-16

Handles all relevant messages being recieved by the server
and responding appropriately.

*/

import java.io.*;
import java.util.*;

public class VoterComponent implements ComponentBase{

	private final int pw = 5441;
	
	public static int nameFlag = 0;
	public static int tallyFlag = 0;

	public static VoteInstance voteInstance;

	//Constructor
	public VoterComponent(){
		nameFlag = 0;
		tallyFlag = 0;
	}

	/* just a trivial example */

	private boolean doAuthentication(int passwd){

		if (passwd == pw)
			return true;
		else 
			return false;
	}

	/* function in interface ComponentBase */

	public KeyValueList processMsg(KeyValueList kvList){

		int MsgID = Integer.parseInt(kvList.getValue("MsgID"));

		KeyValueList kvResult = new KeyValueList();


		switch (MsgID) {

			//PrjRemote connected (custom message)
			case 11: {
				kvResult.addPair("MsgID", "12");
				kvResult.addPair("Description", "PrjRemote Connected. Will act as client.");
				break;
			}

			//Initialize a voting session
			case 21: {

				int passcode = Integer.parseInt(kvList.getValue("Passcode"));

				if(doAuthentication(passcode)){

					kvResult.addPair("MsgID", "26");
					kvResult.addPair("Description", "Acknowledgement (Server acknowledges that VoteInstance was created with name: " + kvList.getValue("Name"));
					kvResult.addPair("AckMsgID", "21");
					kvResult.addPair("YesNo", "Yes");
					kvResult.addPair("Name", "VoteInstance");

					voteInstance = new VoteInstance(kvList.getValue("Name"));
					nameFlag = 1; 
				}
				else{

					kvResult.addPair("MsgID", "26");
					kvResult.addPair("Description", "Acknowledgement (Server acknowledges that VoteInstance was attempted to be made, but authentication failed.");
					kvResult.addPair("AckMsgID", "21");
					kvResult.addPair("YesNo", "No");
					kvResult.addPair("Name", "VoteInstance");
				}

				break;
			}

			//End a voting session
			case 22: {

				int passcode = Integer.parseInt(kvList.getValue("Passcode"));

				if(doAuthentication(passcode)){

					kvResult.addPair("MsgID", "26");
					kvResult.addPair("Description", "Acknowledgement (Server acknowledges that VoteInstance was ended with name: " + kvList.getValue("Name"));
					kvResult.addPair("AckMsgID", "22");
					kvResult.addPair("YesNo", "Yes");
					kvResult.addPair("Name", kvList.getValue("Name"));

					nameFlag = 0;
					tallyFlag = 0;
					voteInstance = null;
				}
				else{

					kvResult.addPair("MsgID", "26");
					kvResult.addPair("Description", "Acknowledgement (Server acknowledges that VoteInstance was attempted to be closed, but authentication failed.");
					kvResult.addPair("AckMsgID", "22");
					kvResult.addPair("YesNo", "No");
					kvResult.addPair("Name", kvList.getValue("Name"));
				}

				break;
			}

			//Connection of a client component (AdminClient, future client components that extend that interface, etc...)
			case 23: {
				kvResult.addPair("MsgID", "26");
				kvResult.addPair("Description", "Acknowledgement (Server acknowledges that Client component is now connected to Server)");
				kvResult.addPair("AckMsgID", "23");

				int passcode = Integer.parseInt(kvList.getValue("Passcode"));

				if(doAuthentication(passcode))
					kvResult.addPair("YesNo", "Yes");
				else
					kvResult.addPair("YesNo", "No");

				kvResult.addPair("Name", "Client Application");
				break;
			}

			//Vote casted, formated into a KeyValue pair by InterfaceServer
			case 701: {

				if(nameFlag == 0 || tallyFlag == 0){
					kvResult.addPair("MsgID", "711");
					kvResult.addPair("Description", "Acknowledge Vote: Invalid");
					kvResult.addPair("AckMsgID", "701");
					kvResult.addPair("Status", "2");
					break;
				}


				String tempPhone = kvList.getValue("VoterPhoneNo");

				if(voteInstance.voterTable.checkPhones(tempPhone)){

					kvResult.addPair("MsgID", "711");
					kvResult.addPair("Description", "Acknowledge Vote: Duplicate");
					kvResult.addPair("AckMsgID", "701");
					kvResult.addPair("Status", "1");
					break;
				}

				if(voteInstance.tallyTable.updateTally(Integer.valueOf(kvList.getValue("CandidateID")))){

					if(!tempPhone.equals(null))
						voteInstance.voterTable.addPhoneNumber(tempPhone);

					kvResult.addPair("MsgID", "711");
					kvResult.addPair("Description", "Acknowledge Vote: Valid");
					kvResult.addPair("AckMsgID", "701");
					kvResult.addPair("Status", "3");

				}
				else {

					kvResult.addPair("MsgID", "711");
					kvResult.addPair("Description", "Acknowledge Vote: Invalid");
					kvResult.addPair("AckMsgID", "701");
					kvResult.addPair("Status", "2");
				}

				break;
			}

			//Request voting report
			case 702: {

				int passcode = Integer.parseInt(kvList.getValue("Passcode"));

				if(doAuthentication(passcode)){

					kvResult.addPair("MsgID", "712");
					kvResult.addPair("Description", "Acknowledge RequestReport: Success");
					kvResult.addPair("AckMsgID", "702");
					kvResult.addPair("YesNo", "Yes");

					ArrayList<Integer[]> temp = voteInstance.tallyTable.getTopScorers(Integer.valueOf(kvList.getValue("N")));

					String report = "";

					for(Integer[] curr : temp){
						report += curr[0] + "," + curr[1] + ";";
					}

					kvResult.addPair("RankedReport", report);
				}
				else{

					kvResult.addPair("MsgID", "712");
					kvResult.addPair("Description", "Acknowledge RequestReport: Failure");
					kvResult.addPair("AckMsgID", "702");
					kvResult.addPair("YesNo", "No");
					kvResult.addPair("RankedReport", "AUTHENTICATION FAILURE");
				}

				break;
			}

			//Initialize the Tally Table
			case 703: {

				if(nameFlag == 0){
					kvResult.addPair("MsgID", "26");
					kvResult.addPair("Description", "Acknowledgement (Server acknowledges that TallyTable was attempted to be made, but authentication failed.");
					kvResult.addPair("AckMsgID", "703");
					kvResult.addPair("YesNo", "No");
					kvResult.addPair("Name", "Initialize TallyTable: Failure");
					break;
				}

				int passcode = Integer.parseInt(kvList.getValue("Passcode"));

				if(doAuthentication(passcode)){

					kvResult.addPair("MsgID", "26");
					kvResult.addPair("Description", "Acknowledgement (Server acknowledges that TallyTable was created)");
					kvResult.addPair("AckMsgID", "703");
					kvResult.addPair("YesNo", "Yes");
					kvResult.addPair("Name", "Initialize TallyTable: Success");

					voteInstance.initTallyTable(kvList.getValue("CandidateList"));
					tallyFlag = 1;
				}
				else{

					kvResult.addPair("MsgID", "26");
					kvResult.addPair("Description", "Acknowledgement (Server acknowledges that TallyTable was attempted to be made, but authentication failed.");
					kvResult.addPair("AckMsgID", "703");
					kvResult.addPair("YesNo", "No");
					kvResult.addPair("Name", "Initialize TallyTable: Failure");
				}

				break;
			}

		}

		return kvResult; 

	}

}
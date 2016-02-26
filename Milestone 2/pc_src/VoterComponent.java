/*
Modified by Carmen Condeluci for CS1631
2-18-16


A Simple Example--Authentication Component.
To Create a Component which works with the InterfaceServer,
the interface ComponentBase is required to be implemented.

interface ComponentBase is described in InterfaceServer.java.

*/

import java.io.*;

public class VoterComponent implements ComponentBase{

	private final int pw = 5441;

	public static VoteInstance voteInstance;

	//Constructor
	public VoterComponent(){
			
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

			case 11: {
				kvResult.addPair("MsgID", "12");
				kvResult.addPair("Description", "PrjRemote Connected. Will act as client.");
				break;
			}

			case 21: {

				int passcode = Integer.parseInt(kvList.getValue("Passcode"));

				if(doAuthentication(passcode)){

					kvResult.addPair("MsgID", "26");
					kvResult.addPair("Description", "Acknowledgement (Server acknowledges that VoteInstance was created with name: " + kvList.getValue("Name"));
					kvResult.addPair("AckMsgID", "21");
					kvResult.addPair("YesNo", "Yes");
					kvResult.addPair("Name", "VoteInstance");

					voteInstance = new VoteInstance(kvList.getValue("Name"));
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

			case 701: {

				if(voteInstance.tallyTable.updateTally(kvList.getValue("CandidateID"))){

					String tempPhone = kvList.getValue("VoterPhoneNo");
					String tempEmail = kvList.getValue("VoterEmail");

					if(voteInstance.voterTable.checkPhones(tempPhone) || voteInstance.voterTable.checkEmails(tempEmail)){
						kvResult.addPair("MsgID", "711");
						kvResult.addPair("Status", "1");
						break;
					}

					if(!tempPhone.equals(null))
						voteInstance.voterTable.addPhoneNumber(tempPhone);
					if(!tempEmail.equals(null))
						voteInstance.voterTable.addEmailAddress(tempEmail)


					kvResult.addPair("MsgID", "711");
					kvResult.addPair("Status", "3");

				}
				else {

					kvResult.addPair("MsgID", "711");
					kvResult.addPair("Status", "2");
				}

				break;
			}

			case 703: {

				int passcode = Integer.parseInt(kvList.getValue("Passcode"));

				if(doAuthentication(passcode)){

					kvResult.addPair("MsgID", "26");
					kvResult.addPair("Description", "Acknowledgement (Server acknowledges that TallyTable was created)");
					kvResult.addPair("AckMsgID", "703");
					kvResult.addPair("YesNo", "Yes");
					kvResult.addPair("Name", "Initialize TallyTable: Success");

					voteInstance.initTallyTable(kvList.getValue("CandidateList"));
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
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

		}

		return kvResult; 

	}

}
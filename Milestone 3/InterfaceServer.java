/*
Modified by Carmen Condeluci for CS1631
2-18-16

Communication Interface for Virtual Classroom Components.
Complied in JDK 1.4.2
Usage:

Step1: Create your component which implemnts Interface 
ComponentBase.

Step2: Replace the ComponentMy with your class name in 
InterfaceServer::main().

Step3: start up Interface Server by
java InterfaceServer

Step4: Use Virtual remote which is also provided on the 
web to send message and get the feedback message.

*/ 


import java.io.*;
import java.net.*;
import java.util.*;

/* 
Class KeyValueList:
List of (Key, Value) pair--the basic format of message
Keys: MsgID and Description are required for any messages
MsgID 0-999 is reserved for system use.
You MsgID could start from 1000. 
*/

class KeyValueList{

	private Vector Keys;
	private Vector Values;

	/* Constructor */
	public KeyValueList(){
		Keys = new Vector();
		Values = new Vector();
	}

	/* Look up the value given key, used in getValue() */

	private int lookupKey(String strKey){
		for(int i = 0;i < Keys.size();i++){
			String k = (String) Keys.elementAt(i);
				if (strKey.equals(k)) 
					return i;
			} 
		return -1;
	}

	/* add new (key,value) pair to list */

	public boolean addPair(String strKey,String strValue){
		return (Keys.add(strKey) && Values.add(strValue));
	}

	/* get the value given key */

	public String getValue(String strKey){
		int index = lookupKey(strKey);
		if (index == -1) 
			return null;
		return (String) Values.elementAt(index);
	} 

	/* Show whole list */
	public String toString(){
		String result = new String();
		for(int i = 0;i < Keys.size();i++){
			result += (String) Keys.elementAt(i) + ":" + (String) Values.elementAt(i) + "\n";
		} 
		return result;
	}

	public int size(){ 
		return Keys.size(); 
	}

	/* get Key or Value by index */
	public String keyAt(int index){ 
		return (String) Keys.elementAt(index);
	}
	public String valueAt(int index){ 
		return (String) Values.elementAt(index);
	}

}

/*
Class MsgEncoder:
Serialize the KeyValue List and Send it out to a Stream.
*/
class MsgEncoder{

	private PrintStream printOut;
	/* Default of delimiter in system is $$$ */
	private final String delimiter="$$$";

	public MsgEncoder(){
	}

	/* Encode the Key Value List into a string and Send it out */

	public void sendMsg(KeyValueList kvList, OutputStream out) throws IOException{

		PrintStream printOut = new PrintStream(out);

		if (kvList == null) 
			return;

		String outMsg = new String();
		for(int i=0; i<kvList.size();i++){
			if (outMsg.equals(""))
				outMsg = "(" + kvList.keyAt(i) + delimiter + kvList.valueAt(i);
			else
				outMsg += delimiter + kvList.keyAt(i) + delimiter + kvList.valueAt(i);
		}
		//System.out.println(outMsg);
		outMsg += ")";
		printOut.println(outMsg);
	}
}

/*
Class MsgDecoder:
Get String from input Stream and reconstruct it to 
a Key Value List.
*/

class MsgDecoder {

	private BufferedReader bufferIn;
	private final String delimiter="$$$";

	public MsgDecoder(InputStream in){
		bufferIn = new BufferedReader(new InputStreamReader(in)); 
	}

	/*
	get String and output KeyValueList
	*/

	public KeyValueList getMsg() throws IOException{
		String strMsg = bufferIn.readLine();

		//System.out.println(strMsg);

		if (strMsg == null) 
			return null;

		strMsg = strMsg.substring(1, strMsg.length()-1);

		KeyValueList kvList = new KeyValueList(); 
		StringTokenizer st = new StringTokenizer(strMsg , delimiter);

		while (st.hasMoreTokens()) {
			String subject = st.nextToken();
			String data = st.nextToken();
			//System.out.println(subject + ": " + data);
			kvList.addPair(subject, data);
		}

		//prjRemote doesn't send message IDs, so we'll have to add it manually.
		//we'll use 11 to signify a PrjRemote connection.
		if(kvList.getValue("MsgID") == null){
			kvList.addPair("MsgID", "11");
		}

		return kvList;
	}

}

/*
Class stringDecoder:
Takes a string and reconstructs it to 
a Key Value List.

same as a msgDecoder except no inputstream
*/

class StringDecoder {

	private final String delimiter="$$$";

	public KeyValueList getMsg(String input) throws IOException{
		String strMsg = input;

		//System.out.println(strMsg);

		if (strMsg == null) 
			return null;

		strMsg = strMsg.substring(1, strMsg.length()-1);

		KeyValueList kvList = new KeyValueList(); 
		StringTokenizer st = new StringTokenizer(strMsg , delimiter);

		while (st.hasMoreTokens()) {
			String subject = st.nextToken();
			String data = st.nextToken();
			//System.out.println(subject + ": " + data);
			kvList.addPair(subject, data);
		}

		//prjRemote doesn't send message IDs, so we'll have to add it manually.
		//we'll use 11 to signify a PrjRemote connection.
		if(kvList.getValue("MsgID") == null){
			kvList.addPair("MsgID", "11");
		}

		return kvList;
	}

}


/* 
interface ComponentBase:
The interface you have to implement in your component
*/
interface ComponentBase{
   public KeyValueList processMsg(KeyValueList kvList);
}

/*
Class InterfaceServer 
Set up a socket server waiting for the remote to connect.
*/

public class InterfaceServer
{

	public static final int port = 53217;

	public static void main(String[] args) throws Exception {
		ServerSocket server = new ServerSocket(port);

		/*
		You need to create your component here
		*/
		VoterComponent compMy = new VoterComponent();

		//Set up mail handler
		MailHandler incomingMail = new MailHandler();
		incomingMail.setupMail();
		ArrayList<String> mailMsgs = new ArrayList<String>();

		Socket client = server.accept();
		try{
			MsgDecoder mDecoder= new MsgDecoder(client.getInputStream());
			MsgEncoder mEncoder= new MsgEncoder();
			StringDecoder sDecoder = new StringDecoder();
			KeyValueList kvInput,kvOutput;

			do{
				//messages from emails
				mailMsgs = incomingMail.getEmailVotes(compMy.voteInstance.voteName);
				if(!mailMsgs.isEmpty()){

					for(String currMsg : mailMsgs){

						kvInput = sDecoder.getMsg(currMsg);
						System.out.println("Incoming Message:\n");
						System.out.println(kvInput);
						KeyValueList kvResult = compMy.processMsg(kvInput);
						System.out.println("Outgoing Message:\n");
						System.out.println(kvResult);
						mEncoder.sendMsg(kvResult, client.getOutputStream());
					}
				}

				//messages from admin and remote
				kvInput = mDecoder.getMsg();
				if (kvInput != null) {
					System.out.println("Incoming Message:\n");
					System.out.println(kvInput);
					KeyValueList kvResult = compMy.processMsg(kvInput);
					System.out.println("Outgoing Message:\n");
					System.out.println(kvResult);
					mEncoder.sendMsg(kvResult, client.getOutputStream());
				}
			} while (kvInput!=null);
		}
		catch (SocketException e){
			System.out.println("Connection was Closed by Client");
		} 
 }
}
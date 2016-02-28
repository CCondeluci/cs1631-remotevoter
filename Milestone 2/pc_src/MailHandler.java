import java.util.*;
import java.io.*;
import javax.mail.*;
import javax.mail.internet.InternetAddress;

//Checks mail received to a specific email address.
//Then, forwards formatted messages to the server 
//parsed from these emails.

//Eventually, will also include functionality to 
//send out emails to voters with confirmation that 
//their votes were recieved.

public class MailHandler {

	public static Session session;
	public static Store store;
	public static Folder inbox;
	public static Message[] messages;

	public static void main(String args[]) throws Exception {

		MailHandler test = new MailHandler();
		test.setupMail();
		test.getEmailVotes("TestVote1");
		test.closeMail();
	}

	public void setupMail() throws Exception {

		Properties properties = new Properties();
		properties.load(new FileInputStream(new File("smtp.properties")));
		session = Session.getDefaultInstance(properties, null);

		store = session.getStore("imaps");
		store.connect("smtp.gmail.com", "cs1631emailhandler@gmail.com", "ayylmao");

		inbox = store.getFolder("inbox");
		inbox.open(Folder.READ_WRITE);
	}

	public void closeMail() throws Exception {

		inbox.close(true);
		store.close();
	}

	//Test method to check inbox
	public void checkMail() throws Exception {

		
		int messageCount = inbox.getMessageCount();

		System.out.println("Total Messages: " + messageCount);

		messages = inbox.getMessages();
		System.out.println("---------------------");

		for(int i = 0; i < messageCount; i++){
			System.out.println("Subject Line: " + messages[i].getSubject());
		}


	}

	//Check inbox, parse and return relevant emails as votes
	public ArrayList<String> getEmailVotes(String voteName) throws Exception {

		ArrayList<String> parsedVotes = new ArrayList<String>();

		if(voteName == "INVALID_INSTANCE")
			return parsedVotes;

		int messageCount = inbox.getMessageCount();

		Message[] messages = inbox.getMessages();

		//For each message, examine the subject line. If it does not match
		//the vote instance name, ignore it.
		for(int i = 0; i < messageCount; i++){

			String vote = "";

			//If the subject line is the name of the voting instance
			if( messages[i].getSubject().equals(voteName) ){

				String subjectLine = messages[i].getSubject();
				Address[] froms = messages[i].getFrom();
				String emailAddr = froms == null ? null : ((InternetAddress) froms[0]).getAddress();

				Multipart mpe = (Multipart) messages[i].getContent();
				String bodyText = "";

				for(int j = 0; j < mpe.getCount(); j++) {
				    
				    BodyPart bodyPart = mpe.getBodyPart(j);

				    if (bodyPart.isMimeType("text/plain")) {
				        bodyText = (String) bodyPart.getContent();
				    }
				}

				bodyText = bodyText.replaceAll("\\r\\n", "");

				vote = "(MsgID$$$701$$$Description$$$Cast Vote$$$CandidateID$$$" + bodyText + "$$$VoterPhoneNo$$$" + emailAddr + "$$$)";

				parsedVotes.add(vote);

				messages[i].setFlag(Flags.Flag.DELETED, true);

				// System.out.println("Subject Line: " + subjectLine);
				// System.out.println("Sender: " + emailAddr);
				// System.out.println("Content: " + bodyText);
				// System.out.println("---------------------------------");
			}
		}

		inbox.expunge();

		// for( String temp : parsedVotes ){
		// 	System.out.println(temp);
		// }

		return parsedVotes;
	}

}
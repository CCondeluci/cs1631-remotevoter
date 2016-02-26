/*
Created by Carmen Condeluci for CS1631
2-18-16

Creates an instance of a voting process, containing:

TallyTable
VoterTable
Name of voting challenge

*/

import java.util.*;

class TallyTable {

	//Create simple mapping of candidate to tally counts using indexes
	public static ArrayList<Integer> candidateList;
	public static ArrayList<Integer> tallyCount;

	//Construct a new tallytable from a list of candidates
	public TallyTable(ArrayList<Integer> inputCandidateList){

		this.candidateList = inputCandidateList;
		this.tallyCount = new ArrayList<Integer>();

		for(int curr : this.candidateList){
			this.tallyCount.add(0);
		}
	}

	//Update the tally for a candidate by 1
	public void updateTally(int candidate){

		if(candidateList.contains(candidate)){
			int index = this.candidateList.indexOf(candidate);
			int currCount = this.tallyCount.get(index);
			this.tallyCount.set(index, currCount + 1);
		}
	}

	//Get the current count for a given candidate
	public int getCount(int candidate){

		int index = this.candidateList.indexOf(candidate);
		int currCount = this.tallyCount.get(index);

		return currCount;
	}

	//get candidate list
	public ArrayList<Integer> getCandidates(){

		return this.candidateList;
	}

	//get tally count list
	public ArrayList<Integer> getCounts(){
		return this.tallyCount;
	}

	//get the top N scoring candidates
	public ArrayList<Integer[]> getTopScorers(int numScorers){

		ArrayList<Integer> tempTallyCount = (ArrayList<Integer>) this.tallyCount.clone();
		ArrayList<Integer> tempCandidateList = (ArrayList<Integer>) this.candidateList.clone();

		ArrayList<Integer> sortTally = (ArrayList<Integer>) this.tallyCount.clone();
		Collections.sort(sortTally);
		Collections.reverse(sortTally);

		ArrayList<Integer[]> returnScorers = new ArrayList<Integer[]>();

		for(int i = 0; i < numScorers; i++){

			int score = sortTally.get(i);
			int index = tempTallyCount.indexOf(score);
			int candidate = tempCandidateList.get(index);

			Integer[] forAdd = new Integer[2];
			forAdd[0] = candidate;
			forAdd[1] = score;

			returnScorers.add(forAdd);

			tempTallyCount.remove(index);
			tempCandidateList.remove(index);

		}

		return returnScorers;
	}

}

class VoterTable {

	//Never need to report back any information regarding the voters, 
	//only need to prevent duplicates. No mapping required.
	public static ArrayList<String> phoneNumbers;
	public static ArrayList<String> emailAddresses;

	public VoterTable(){

		this.phoneNumbers = new ArrayList<String>();
		this.emailAddresses = new ArrayList<String>();
	}

	//add phone number to table
	public void addPhoneNumber(String phoneNumber){

		this.phoneNumbers.add(phoneNumber);
	}

	//add email address to table
	public void addEmail(String emailAddress){

		this.emailAddresses.add(emailAddress);
	}

}

public class VoteInstance {

	public static String voteName;
	public static TallyTable tallyTable;
	public static VoterTable voterTable;

	public VoteInstance(String sessionName){

		this.voteName = sessionName;
		this.voterTable = new VoterTable();
	}

	public void initTallyTable(String msgCandidateList){

		String[] splitCands = msgCandidateList.split(";");

		ArrayList<Integer> temp = new ArrayList<Integer>();

		for(String curr : splitCands){

			temp.add(Integer.valueOf(curr));
		}

		this.tallyTable = new TallyTable(temp);
	}

	//Main to test table classes
	public static void main(String args[]){

		//create mock candidate list
		ArrayList<Integer> testCandidates = new ArrayList<Integer>();
		testCandidates.add(2);
		testCandidates.add(5);
		testCandidates.add(6);
		testCandidates.add(14);

		TallyTable newTally = new TallyTable(testCandidates);

		System.out.println(newTally.getCandidates());
		System.out.println(newTally.getCounts());

		//set tallies to 2:3, 5:2, 6:3, 14:5
		newTally.updateTally(2);
		newTally.updateTally(2);
		newTally.updateTally(2);

		newTally.updateTally(5);
		newTally.updateTally(5);

		newTally.updateTally(6);
		newTally.updateTally(6);
		newTally.updateTally(6);

		newTally.updateTally(14);
		newTally.updateTally(14);
		newTally.updateTally(14);
		newTally.updateTally(14);
		newTally.updateTally(14);

		newTally.updateTally(7);
		newTally.updateTally(7);

		System.out.println("-------------");
		System.out.println(newTally.getCandidates());
		System.out.println(newTally.getCounts());

		//get 3 highest top scorers. should get back 14, 2, and 6
		ArrayList<Integer[]> topScorers = newTally.getTopScorers(3);

		System.out.println("-------------");
		for(Integer[] curr : topScorers){
			System.out.println("candidate: " + curr[0] + " , score: " + curr[1]);
		}

	}

}
import java.awt.EventQueue;

import javax.swing.JFrame;
import java.awt.Color;
import java.awt.GridBagLayout;
import javax.swing.JTextField;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import javax.swing.JLabel;
import java.awt.GridLayout;
import javax.swing.SwingConstants;
import javax.swing.JButton;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.net.*;
import java.io.*;

public class VoterGUI {

	private JFrame frmVoterGui;
	private JTextField serverAddrField;
	private JTextField serverPortField;
	private JTextField serverPasscodeField;
	private JTextField voteNameField;
	private JTextField voteOptionsField;
	private JTextField topScoreSelectField;
	private Socket sock;
	private MsgDecoder mDecoder;
	private MsgEncoder mEncoder;
	private StringDecoder sDecoder;
	private KeyValueList kvInput;
	private KeyValueList kvOutput;
	private String currPass;
	private int numOptions;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					VoterGUI window = new VoterGUI();
					window.frmVoterGui.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public VoterGUI() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmVoterGui = new JFrame();
		frmVoterGui.setTitle("Voter GUI");
		frmVoterGui.setBounds(300, 100, 700, 430);
		frmVoterGui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmVoterGui.getContentPane().setLayout(null);
		
		serverAddrField = new JTextField();
		serverAddrField.setBounds(30, 71, 180, 20);
		frmVoterGui.getContentPane().add(serverAddrField);
		serverAddrField.setColumns(10);
		
		JLabel lblServerAddr = new JLabel("Server IP Address");
		lblServerAddr.setHorizontalAlignment(SwingConstants.LEFT);
		lblServerAddr.setBounds(30, 46, 180, 14);
		frmVoterGui.getContentPane().add(lblServerAddr);
		
		serverPortField = new JTextField();
		serverPortField.setBounds(220, 71, 86, 20);
		frmVoterGui.getContentPane().add(serverPortField);
		serverPortField.setColumns(10);
		
		JLabel lblServerPort = new JLabel("Server Port");
		lblServerPort.setBounds(220, 46, 86, 14);
		frmVoterGui.getContentPane().add(lblServerPort);
		
		serverPasscodeField = new JTextField();
		serverPasscodeField.setBounds(316, 71, 120, 20);
		frmVoterGui.getContentPane().add(serverPasscodeField);
		serverPasscodeField.setColumns(10);
		
		JLabel lblServerPasscode = new JLabel("Server Passcode");
		lblServerPasscode.setBounds(316, 46, 120, 14);
		frmVoterGui.getContentPane().add(lblServerPasscode);
		
		final JButton btnConnect = new JButton("Connect");
		btnConnect.setBounds(449, 70, 89, 23);
		frmVoterGui.getContentPane().add(btnConnect);
		
		final JButton btnDisconnect = new JButton("Disconnect");
		btnDisconnect.setBounds(548, 70, 117, 23);
		frmVoterGui.getContentPane().add(btnDisconnect);
		
		JSeparator separator = new JSeparator();
		separator.setBounds(10, 104, 664, 8);
		frmVoterGui.getContentPane().add(separator);
		
		JLabel lblServerConnectdisconnect = new JLabel("Server Connect/Disconnect");
		lblServerConnectdisconnect.setBounds(30, 11, 305, 14);
		frmVoterGui.getContentPane().add(lblServerConnectdisconnect);
		
		JLabel lblCreateVotingInstance = new JLabel("Create Voting Instance");
		lblCreateVotingInstance.setBounds(30, 123, 200, 14);
		frmVoterGui.getContentPane().add(lblCreateVotingInstance);
		
		voteNameField = new JTextField();
		voteNameField.setBounds(30, 173, 180, 20);
		frmVoterGui.getContentPane().add(voteNameField);
		voteNameField.setColumns(10);
		
		JLabel lblVoteName = new JLabel("Vote Name");
		lblVoteName.setBounds(30, 148, 180, 14);
		frmVoterGui.getContentPane().add(lblVoteName);
		
		voteOptionsField = new JTextField();
		voteOptionsField.setBounds(220, 173, 216, 20);
		frmVoterGui.getContentPane().add(voteOptionsField);
		voteOptionsField.setColumns(10);
		
		JLabel lblVotingOptionsas = new JLabel("Voting Options (as a \";\" delimited list)");
		lblVotingOptionsas.setBounds(220, 148, 216, 14);
		frmVoterGui.getContentPane().add(lblVotingOptionsas);
		
		final JButton btnCreateVote = new JButton("Create Vote");
		btnCreateVote.setBounds(449, 172, 104, 23);
		frmVoterGui.getContentPane().add(btnCreateVote);
		
		JSeparator separator_1 = new JSeparator();
		separator_1.setBounds(10, 215, 664, 8);
		frmVoterGui.getContentPane().add(separator_1);
		
		JLabel lblVoteReview = new JLabel("Vote Review");
		lblVoteReview.setBounds(30, 234, 180, 14);
		frmVoterGui.getContentPane().add(lblVoteReview);
		
		final JButton btnEndVote = new JButton("End Vote");
		btnEndVote.setBounds(563, 172, 102, 23);
		frmVoterGui.getContentPane().add(btnEndVote);
		
		final JTextArea txtAreaConsoleOutput = new JTextArea();
		txtAreaConsoleOutput.setEditable(false);
		txtAreaConsoleOutput.setText("-----------\n");
		txtAreaConsoleOutput.setBounds(30, 259, 406, 118);
		frmVoterGui.getContentPane().add(txtAreaConsoleOutput);
		// PrintStream printStream = new PrintStream(new CustomOutputStream(txtAreaConsoleOutput));
		// System.setOut(printStream);
		// System.setErr(printStream);
		
		final JButton btnRequestVotingData = new JButton("Request All Voting Data");
		btnRequestVotingData.setBounds(449, 260, 216, 23);
		frmVoterGui.getContentPane().add(btnRequestVotingData);
		
		final JButton btnRefreshEmailVotes = new JButton("Refresh Email Votes");
		btnRefreshEmailVotes.setBounds(449, 294, 216, 23);
		frmVoterGui.getContentPane().add(btnRefreshEmailVotes);
		
		final JButton btnRequestTopScorers = new JButton("Get Top Scorers");
		btnRequestTopScorers.setBounds(449, 328, 171, 23);
		frmVoterGui.getContentPane().add(btnRequestTopScorers);
		
		topScoreSelectField = new JTextField();
		topScoreSelectField.setBounds(630, 329, 35, 20);
		frmVoterGui.getContentPane().add(topScoreSelectField);
		topScoreSelectField.setColumns(10);

		//Actionlisteners
		btnConnect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				attemptConnect(
						serverAddrField,
						serverPortField,
						serverPasscodeField,
						btnConnect,
						btnDisconnect,
						btnCreateVote,
						btnEndVote,
						voteNameField,
						voteOptionsField,
						txtAreaConsoleOutput
					);
			}
		});

		btnDisconnect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				attemptDisconnect(
						serverAddrField,
						serverPortField,
						serverPasscodeField,
						btnConnect,
						btnDisconnect,
						btnCreateVote,
						btnEndVote,
						voteNameField,
						voteOptionsField,
						txtAreaConsoleOutput
					);
			}
		});

		btnCreateVote.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				attemptCreateVote(
						voteNameField,
						voteOptionsField,
						btnCreateVote,
						btnEndVote,
						btnRequestVotingData,
						btnRefreshEmailVotes,
						btnRequestTopScorers,
						topScoreSelectField,
						txtAreaConsoleOutput
					);
			}
		});

		btnEndVote.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				attemptCloseVote(
						voteNameField,
						voteOptionsField,
						btnCreateVote,
						btnEndVote,
						btnRequestVotingData,
						btnRefreshEmailVotes,
						btnRequestTopScorers,
						topScoreSelectField,
						txtAreaConsoleOutput
					);
			}
		});

		btnRequestVotingData.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				attemptGetVoteData(
						voteNameField,
						voteOptionsField,
						btnCreateVote,
						btnEndVote,
						btnRequestVotingData,
						btnRefreshEmailVotes,
						btnRequestTopScorers,
						topScoreSelectField,
						txtAreaConsoleOutput
					);
			}
		});

		btnRefreshEmailVotes.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				attemptRefreshEmailVotes(
						voteNameField,
						voteOptionsField,
						btnCreateVote,
						btnEndVote,
						btnRequestVotingData,
						btnRefreshEmailVotes,
						btnRequestTopScorers,
						topScoreSelectField,
						txtAreaConsoleOutput
					);
			}
		});

		btnRequestTopScorers.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				attemptGetTopScorers(
						voteNameField,
						voteOptionsField,
						btnCreateVote,
						btnEndVote,
						btnRequestVotingData,
						btnRefreshEmailVotes,
						btnRequestTopScorers,
						topScoreSelectField,
						txtAreaConsoleOutput
					);
			}
		});

		//Set initial button states
		btnConnect.setEnabled(true);
		btnDisconnect.setEnabled(false);
		btnCreateVote.setEnabled(false);
		btnEndVote.setEnabled(false);
		btnRequestTopScorers.setEnabled(false);
		btnRefreshEmailVotes.setEnabled(false);
		btnRequestVotingData.setEnabled(false);
		topScoreSelectField.setEnabled(false);
		voteNameField.setEnabled(false);
		voteOptionsField.setEnabled(false);

		//Set conveniance connection field data
		serverAddrField.setText("localhost");
		serverPortField.setText("53217");
		serverPasscodeField.setText("5441");
	}
	
	public void attemptConnect(	JTextField serverAddrField, 
								JTextField serverPortField, 
								JTextField serverPasscodeField,
								JButton btnConnect,
								JButton btnDisconnect,
								JButton btnCreateVote,
								JButton btnEndVote,
								JTextField voteNameField,
								JTextField voteOptionsField,
								JTextArea txtAreaConsoleOutput			) {
		
		String address = serverAddrField.getText();
		int port = Integer.parseInt(serverPortField.getText());
		String passcode =serverPasscodeField.getText();

		try {
			sock = new Socket(address, port);
		
			mDecoder = new MsgDecoder(sock.getInputStream());
			mEncoder = new MsgEncoder();
			sDecoder = new StringDecoder();

			KeyValueList kvConnect = new KeyValueList();

			kvConnect.addPair("MsgID", "23");
			kvConnect.addPair("Description", "Connect to Server (GUI requests to be connected to Server)");
			kvConnect.addPair("Passcode", passcode);
			kvConnect.addPair("SecurityLevel", "3");
			kvConnect.addPair("Name", "GUI");

			System.out.println("Outgoing Message:\n");
			System.out.println(kvConnect);
			mEncoder.sendMsg(kvConnect, sock.getOutputStream());

			KeyValueList kvResponse = mDecoder.getMsg();
			System.out.println("Incoming Message:\n");
			System.out.println(kvResponse);

			if(kvResponse.getValue("YesNo").equals("Yes")){
				System.out.println("You have successfully connected to the server!");
				txtAreaConsoleOutput.append("You have successfully connected to the server!\n");
			}

			btnConnect.setEnabled(false);
			btnDisconnect.setEnabled(true);
			btnCreateVote.setEnabled(true);
			btnEndVote.setEnabled(false);
			serverPasscodeField.setEnabled(false);
			serverPortField.setEnabled(false);
			serverAddrField.setEnabled(false);
			voteNameField.setEnabled(true);
			voteOptionsField.setEnabled(true);

			currPass = passcode;

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void attemptDisconnect(	JTextField serverAddrField, 
									JTextField serverPortField, 
									JTextField serverPasscodeField,
									JButton btnConnect,
									JButton btnDisconnect,
									JButton btnCreateVote,
									JButton btnEndVote,
									JTextField voteNameField,
									JTextField voteOptionsField,
									JTextArea txtAreaConsoleOutput			){
		
		if(sock != null){
			try {
				sock.close();
				System.out.println("Disconnected from Voting Server");
				txtAreaConsoleOutput.append("Disconnected from Voting Server\n");

				btnConnect.setEnabled(true);
				btnDisconnect.setEnabled(false);
				btnCreateVote.setEnabled(false);
				btnEndVote.setEnabled(false);
				serverPasscodeField.setEnabled(true);
				serverPortField.setEnabled(true);
				serverAddrField.setEnabled(true);
				voteNameField.setEnabled(false);
				voteOptionsField.setEnabled(false);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
	}
	
	public void attemptCreateVote(	JTextField voteNameField,
									JTextField voteOptionsField,
									JButton btnCreateVote,
									JButton btnEndVote,
									JButton btnRequestVotingData,
									JButton btnRefreshEmailVotes,
									JButton btnRequestTopScorers,
									JTextField topScoreSelectField,
									JTextArea txtAreaConsoleOutput	){
		try {
			String voteName = voteNameField.getText();
			String voteOptions = voteOptionsField.getText();

			KeyValueList kvCreateVote = new KeyValueList();

			kvCreateVote.addPair("MsgID", "21");
			kvCreateVote.addPair("Description", "Create Voting Sessions via VoteInstance.java");
			kvCreateVote.addPair("Passcode", currPass);
			kvCreateVote.addPair("Name", voteName);

			System.out.println("Outgoing Message:\n");
			System.out.println(kvCreateVote);
			mEncoder.sendMsg(kvCreateVote, sock.getOutputStream());

			KeyValueList kvResponse = mDecoder.getMsg();
			System.out.println("Incoming Message:\n");
			System.out.println(kvResponse);

			if(kvResponse.getValue("YesNo").equals("Yes")){
				System.out.println("You have successfully created a voting instance!\nNow sending creation of TallyTable message...");
				txtAreaConsoleOutput.append("You have successfully created a voting instance!\nNow sending creation of TallyTable message...\n");
			} else {
				System.out.println("Your voting instance could not be created!");
				txtAreaConsoleOutput.append("Your voting instance could not be created!\n");
				return;
			}

			KeyValueList kvInitTally = new KeyValueList();

			kvInitTally.addPair("MsgID", "703");
			kvInitTally.addPair("Description", "Initialize Tally Table");
			kvInitTally.addPair("Passcode", currPass);
			kvInitTally.addPair("CandidateList", voteOptions);
			
			System.out.println("Outgoing Message:\n");
			System.out.println(kvInitTally);
			mEncoder.sendMsg(kvInitTally, sock.getOutputStream());

			kvResponse = mDecoder.getMsg();
			System.out.println("Incoming Message:\n");
			System.out.println(kvResponse);

			if(kvResponse.getValue("YesNo").equals("Yes")){
				System.out.println("You have successfully created a tally table!");
				txtAreaConsoleOutput.append("You have successfully created a tally table!\n");
			} else {
				System.out.println("Your tally table could not be created!");
				txtAreaConsoleOutput.append("Your tally table could not be created!\n");
				return;
			}

			numOptions = voteOptions.split(";").length;

			System.out.println("numOptions: " + numOptions);

			btnCreateVote.setEnabled(false);
			btnEndVote.setEnabled(true);
			btnRequestVotingData.setEnabled(true);
			btnRefreshEmailVotes.setEnabled(true);
			btnRequestTopScorers.setEnabled(true);
			topScoreSelectField.setEnabled(true);
			voteNameField.setEnabled(false);
			voteOptionsField.setEnabled(false);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void attemptCloseVote(	JTextField voteNameField,
									JTextField voteOptionsField,
									JButton btnCreateVote,
									JButton btnEndVote,
									JButton btnRequestVotingData,
									JButton btnRefreshEmailVotes,
									JButton btnRequestTopScorers,
									JTextField topScoreSelectField,
									JTextArea txtAreaConsoleOutput	){
		try {
			String voteName = voteNameField.getText();
			String voteOptions = voteOptionsField.getText();

			KeyValueList kvCloseVote = new KeyValueList();

			kvCloseVote.addPair("MsgID", "22");
			kvCloseVote.addPair("Description", "Close Voting Session");
			kvCloseVote.addPair("Passcode", currPass);
			kvCloseVote.addPair("Name", voteName);

			System.out.println("Outgoing Message:\n");
			System.out.println(kvCloseVote);
			mEncoder.sendMsg(kvCloseVote, sock.getOutputStream());

			KeyValueList kvResponse = mDecoder.getMsg();
			System.out.println("Incoming Message:\n");
			System.out.println(kvResponse);

			if(kvResponse.getValue("YesNo").equals("Yes")){
				System.out.println("You have successfully close the voting instance!");
				txtAreaConsoleOutput.append("You have successfully close the voting instance!\n");
			} else {
				System.out.println("Your voting instance could not be closed.");
				txtAreaConsoleOutput.append("Your voting instance could not be closed.\n");
				return;
			}

			btnCreateVote.setEnabled(true);
			btnEndVote.setEnabled(false);
			btnRequestVotingData.setEnabled(false);
			btnRefreshEmailVotes.setEnabled(false);
			btnRequestTopScorers.setEnabled(false);
			topScoreSelectField.setEnabled(false);
			voteNameField.setEnabled(true);
			voteOptionsField.setEnabled(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void attemptGetVoteData(	JTextField voteNameField,
									JTextField voteOptionsField,
									JButton btnCreateVote,
									JButton btnEndVote,
									JButton btnRequestVotingData,
									JButton btnRefreshEmailVotes,
									JButton btnRequestTopScorers,
									JTextField topScoreSelectField,
									JTextArea txtAreaConsoleOutput	){
		try {
			KeyValueList kvGetData = new KeyValueList();

			kvGetData.addPair("MsgID", "702");
			kvGetData.addPair("Description", "Request Report");
			kvGetData.addPair("Passcode", currPass);
			kvGetData.addPair("N", numOptions + "");

			System.out.println("Outgoing Message:\n");
			System.out.println(kvGetData);
			mEncoder.sendMsg(kvGetData, sock.getOutputStream());

			KeyValueList kvResponse = mDecoder.getMsg();
			System.out.println("Incoming Message:\n");
			System.out.println(kvResponse);

			if(kvResponse.getValue("YesNo").equals("Yes")){
				String[] results = kvResponse.getValue("RankedReport").split(";");

				txtAreaConsoleOutput.setText("-----------\n");
				txtAreaConsoleOutput.append("Current Voting Results for N = " + numOptions + ":\n");

				for(String curr : results){
					String[] temp = curr.split(",");
					txtAreaConsoleOutput.append("Option " + temp[0] + ": " + temp[1] + " votes\n");
				}

			} else {
				System.out.println("You could not retrieve voting results.");
				txtAreaConsoleOutput.append("You could not retrieve voting results.\n");
				return;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void attemptRefreshEmailVotes(	JTextField voteNameField,
											JTextField voteOptionsField,
											JButton btnCreateVote,
											JButton btnEndVote,
											JButton btnRequestVotingData,
											JButton btnRefreshEmailVotes,
											JButton btnRequestTopScorers,
											JTextField topScoreSelectField,
											JTextArea txtAreaConsoleOutput	){

		try {
			KeyValueList kvGetData = new KeyValueList();

			kvGetData.addPair("MsgID", "702");
			kvGetData.addPair("Description", "Request Report");
			kvGetData.addPair("Passcode", currPass);
			kvGetData.addPair("N", numOptions + "");

			System.out.println("Outgoing Message:\n");
			System.out.println(kvGetData);
			mEncoder.sendMsg(kvGetData, sock.getOutputStream());

			KeyValueList kvResponse = mDecoder.getMsg();
			System.out.println("Incoming Message:\n");
			System.out.println(kvResponse);

			if(kvResponse.getValue("YesNo").equals("Yes")){
				String[] results = kvResponse.getValue("RankedReport").split(";");

				txtAreaConsoleOutput.setText("----Email Votes Refreshed----\n");
				txtAreaConsoleOutput.append("Current Voting Results for N = " + numOptions + ":\n");

				for(String curr : results){
					String[] temp = curr.split(",");
					txtAreaConsoleOutput.append("Option " + temp[0] + ": " + temp[1] + " votes\n");
				}

			} else {
				System.out.println("You could not retrieve voting results.");
				txtAreaConsoleOutput.append("You could not retrieve voting results.\n");
				return;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public void attemptGetTopScorers(	JTextField voteNameField,
										JTextField voteOptionsField,
										JButton btnCreateVote,
										JButton btnEndVote,
										JButton btnRequestVotingData,
										JButton btnRefreshEmailVotes,
										JButton btnRequestTopScorers,
										JTextField topScoreSelectField,
										JTextArea txtAreaConsoleOutput	){
		try {
			int selectN = Integer.parseInt(topScoreSelectField.getText());

			KeyValueList kvGetData = new KeyValueList();

			kvGetData.addPair("MsgID", "702");
			kvGetData.addPair("Description", "Request Report");
			kvGetData.addPair("Passcode", currPass);
			kvGetData.addPair("N", selectN + "");

			System.out.println("Outgoing Message:\n");
			System.out.println(kvGetData);
			mEncoder.sendMsg(kvGetData, sock.getOutputStream());

			KeyValueList kvResponse = mDecoder.getMsg();
			System.out.println("Incoming Message:\n");
			System.out.println(kvResponse);

			if(kvResponse.getValue("YesNo").equals("Yes")){
				String[] results = kvResponse.getValue("RankedReport").split(";");

				txtAreaConsoleOutput.setText("--------\n");
				txtAreaConsoleOutput.append("Current Voting Results for N = " + selectN + ":\n");

				for(String curr : results){
					String[] temp = curr.split(",");
					txtAreaConsoleOutput.append("Option " + temp[0] + ": " + temp[1] + " votes\n");
				}

			} else {
				System.out.println("You could not retrieve voting results.");
				txtAreaConsoleOutput.append("You could not retrieve voting results.\n");
				return;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}

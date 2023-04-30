import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import java.util.Scanner;
import javax.xml.transform.Templates;
import java.io.FileNotFoundException;
import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.*;
import java.sql.*;
import java.io.FileWriter;
import javax.swing.SwingConstants;

public class GUI extends JFrame implements ActionListener {

    private static JPanel panel;
    private static JFrame frame;

    private static JLabel Title;
    private static JLabel stats;
    private static JTextField userText1;
    private static JLabel[] labels;

    public static Scanner s = new Scanner(System.in);
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_GREEN = "\u001B[32m";

    static String[] possibleWords;
    static int tries;
    static char[] input;
    static long startTime;
    static char[] answer;
    static boolean done;
    static String answerChoosen;

	public static void main(String[] args) {

        Connection connection = null;
		try {
			// below two lines are used for connectivity.
			Class.forName("com.mysql.cj.jdbc.Driver");
			connection = DriverManager.getConnection(
				"jdbc:mysql://localhost:3306/proj",
				"root", "user123");

			// mydb is database
			// mydbuser is name of database
			// mydbuser is password of database

			Statement statement;
			statement = connection.createStatement();
			ResultSet resultSet;
			resultSet = statement.executeQuery(
				"select word from wordleAnswers order by rand() limit 1");
            
			String word;
            while (resultSet.next()) {
				word = resultSet.getString("word");
				System.out.println("Word : " + word);
                FileWriter myWriter = new FileWriter("wordleAnswers.txt");
                myWriter.write(word);
                myWriter.close();
			}
			resultSet.close();
			statement.close();
			connection.close();
		}
		catch (Exception exception) {
			System.out.println(exception);
		}

        panel = new JPanel();
        frame = new JFrame();
        frame.setSize(350, 450);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setTitle("GUI");
        frame.setLocationRelativeTo(null);
        frame.add(panel);

        panel.setLayout(null);
        Title = new JLabel("Wordle");
        Title.setBounds(125, 20, 80, 25);
        Title.setFont(new Font("Arial", Font.BOLD, 20)); // Sets font to Arial, bold and size 20
        Title.setHorizontalAlignment(SwingConstants.CENTER); // Centers the text horizontally
        panel.add(Title);

        panel.setLayout(null);
        stats = new JLabel("Type a five letter word");
        stats.setBounds(95, 50, 180, 25);
        stats.setForeground(Color.black);
        panel.add(stats);

        userText1 = new JTextField();
        userText1.addActionListener(new GUI());
        userText1.setBounds(130, 95, 80, 25);
        panel.add(userText1);

        JButton button = new JButton("Enter");
        button.setBounds(130, 330, 80, 25); // adjusted y-coordinate
        button.addActionListener(new GUI());
        panel.add(button);

        //JLabel winScreen = new JLabel("Good Luck Have Fun Mate!");
        //winScreen.setBounds(10, 50, 350, 25);

        labels = new JLabel[6];
        for (int i = 0; i < 6; i++) {
            labels[i] = new JLabel("<html><font size='5' color=blue> ----- </font> <font");
            labels[i].setBounds(144, 130 + (i * 30), 80, 25);
            panel.add(labels[i]);
        }

        frame.setVisible(true);

        StartWordle(); //gets the answer word, and does some other thing like starting the timer
    }

    public static void StartWordle() {

        // Connection connection = null;
		// try {
		// 	// below two lines are used for connectivity.
		// 	Class.forName("com.mysql.cj.jdbc.Driver");
		// 	connection = DriverManager.getConnection(
		// 		"jdbc:mysql://localhost:3306/proj",
		// 		"root", "user123");

		// 	// mydb is database
		// 	// mydbuser is name of database
		// 	// mydbuser is password of database

		// 	Statement statement;
		// 	statement = connection.createStatement();
		// 	ResultSet resultSet;
		// 	resultSet = statement.executeQuery(
		// 		"select * from wordleWords");
            
		// 	String word;
        //     int indexCounter = 0;
        //     possibleWords = new String[12952];
        //     while (resultSet.next()) {
		// 		word = resultSet.getString("word");
        //         possibleWords[indexCounter] = word;
        //         System.out.println(possibleWords[indexCounter]);
        //         indexCounter++;
		// 	}
        //     //System.out.println(indexCounter);
        //     //System.out.println(possibleWords.length);
		// 	resultSet.close();
		// 	statement.close();
		// 	connection.close();
		// }
		// catch (Exception exception) {
		// 	System.out.println(exception);
		// }

        //makes an array of the possible words (12947 lines long)
        possibleWords = new String[12947];
        try { 
            File myObj = new File("src/wordleWords.txt");
            Scanner myReader = new Scanner(myObj);
            int indexCounter = 0;
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                //add data to the array
                possibleWords[indexCounter] = data;
                indexCounter++;
            }
            myReader.close();
            System.out.println(indexCounter);
            System.out.println(possibleWords.length);
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }

        startTime = System.currentTimeMillis();
        tries = 0;
        System.out.println("Wordle: Type A Five Letter Word");
        answerChoosen = ReturnRandomWord();
        answer = new char[5];
        for (int i = 0; i < 5; i++ ) answer[i] = answerChoosen.charAt(i);

        input = new char[5];
    }
    
    public static void EndWordle() {
        System.out.println("Wordle: The Answer Was: " + new String(answerChoosen));
        System.out.println("Wordle: You Found The Answer in " + ((System.currentTimeMillis() - startTime) / 1000) + " seconds and " + tries + " tries.");

        userText1.setEnabled(false);
        userText1.setVisible(false);

        if (!done) stats.setText("<html><font size='1' color=red> " + "The Answer Was: " + new String(answerChoosen) + ". You wasted \n " + ((System.currentTimeMillis() - startTime) / 1000) + " seconds (:" + "</font> <font");
        else  stats.setText("<html><font size='1' color=green> " + "You Found The Answer in \n " + ((System.currentTimeMillis() - startTime) / 1000) + " seconds and " + tries + " tries." + "</font> <font");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // TODO Auto-generated method stub // if the button is pressed
        EnterWord();
    }

    public static void EnterWord(){ //if its good, actually submit the word for checking
        if ( IsAValidWord(userText1.getText(), possibleWords) ) ButtonPressed();
        else System.out.println("Wordle: That is not a valid word");
    }

    public static void ButtonPressed(){
        userText1.setBounds(144, 130 + ((tries + 1) * 30), 80, 25);

        String userInput = userText1.getText();
        int[] colorOfLetters = PlayWordle(userInput);

        done = true;
        for (int i : colorOfLetters) {
            if (i != 2) done = false;
        }
        if (done || tries > 5) EndWordle();

        String[] numsToColors = new String[5];
        for (int i = 0; i < 5; i++) {
            if (colorOfLetters[i] == 0) numsToColors[i] = "black";
            else if (colorOfLetters[i] == 1) numsToColors[i] = "orange";
            else if (colorOfLetters[i] == 2) numsToColors[i] = "green";
        }

        System.out.println("Set colors to " + numsToColors[0] + " " + numsToColors[1] + " " + numsToColors[2] + " " + numsToColors[3] + " " + numsToColors[4] + " User Input was" + userInput + " answer was " + answerChoosen + " work on word is " + new String(answer));
        String finalString = (
        "<html><font size='5' color=" + numsToColors[0] + "> " + userInput.charAt(0) + "</font> <font            " + 
        "<html><font size='5' color=" + numsToColors[1] + "> " + userInput.charAt(1) + "</font> <font            " + 
        "<html><font size='5' color=" + numsToColors[2] + "> " + userInput.charAt(2) + "</font> <font            " + 
        "<html><font size='5' color=" + numsToColors[3] + "> " + userInput.charAt(3) + "</font> <font            " + 
        "<html><font size='5' color=" + numsToColors[4] + "> " + userInput.charAt(4) + "</font> <font            ");
        setNextLabel(finalString);

        userText1.setText(""); //set the text box to "" after all the logic is done
    }

    public static int[] PlayWordle(String InputWordleWord) {
        done = false;
        tries++;

        String R1 = InputWordleWord.toLowerCase();//String R1 = s.nextLine().toLowerCase();

        //check if it is 5 letters and is a possible word
        if (!IsAValidWord(R1, possibleWords)) {
            System.out.println("wasnt a good word");
        } else {
            for (int i = 0; i < 5; i++ ) { //puts the inputWord into a char[]
                input[i] = R1.charAt(i);
            }
        }
//just reset answer every time
        for (int i = 0; i < 5; i++ ) answer[i] = answerChoosen.charAt(i);
        return ReturnColorOfLeters(input, answer);
    }

    public static void setNextLabel(String string){
        labels[tries - 1].setText(string);
    }

    public static int[] ReturnColorOfLeters(char[] inputWord, char[] correctWord) {
        char[] answerTemp = correctWord;
        int[] colorForLetter = new int[5]; //0 is grey, yellow is 1, green is 2

        for (int i = 0; i < 5; i++) { //check for any correct position+letter (green)
            if (inputWord[i] == answerTemp[i]) {
                answerTemp[i] = '-';
                colorForLetter[i] = 2;
            }
        }

        for (int j = 0; j < 5; j++) { //check for any correct letter (yellow)
            for (int k = 0; k < 5; k++){
                if (inputWord[j] == answerTemp[k] && colorForLetter[j] != 2) {
                    //if that letter is not already green and matches some other letter
                    colorForLetter[j] = 1;
                    answerTemp[k] = '-';
                }
            }
        }

        for (int m = 0; m < 5; m++) {
            if (colorForLetter[m] == 0) System.out.print(inputWord[m]);
            if (colorForLetter[m] == 1) System.out.print(ANSI_YELLOW + inputWord[m] + ANSI_RESET);
            if (colorForLetter[m] == 2) System.out.print(ANSI_GREEN + inputWord[m] + ANSI_RESET);
        }

        System.out.println("");
        return colorForLetter;
    }

    public static boolean IsAValidWord(String input, String[] possibleWords) {
        if (input.length() < 5) {
            System.out.println("Wordle: The Word You Entered Was Not Long Enough");
            return false;
        }
        for (String string : possibleWords) {
            if (string.equals(input)) {
                return true;
            }
        }
        return false;
    }

    public static String ReturnRandomWord(){

        String[] answerList = new String[2];
        try { 
            File myObj = new File("wordleAnswers.txt");
            Scanner myReader = new Scanner(myObj);
            //int indexCounter = 0;
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                //add data to the array
                answerList[0] = data;
                //indexCounter++;
            }
            myReader.close();   
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
        
        return answerList[0]; //returns a random word from this large list
    }
}
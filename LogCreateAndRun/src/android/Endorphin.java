package android;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.EOFException;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;

import com.csvreader.CsvReader;

public class Endorphin {
	static String PATH = "c:/kenshin/";
	static String CSVFILEPATH = PATH + "test.csv"; 
	static String OUTFILEPATH = PATH + "test.sh"; 
	static String SDCARD = "/sdcard/";
	static String SDCARDFILENAME = "test.sh";

	private String SENDEVENT = "sendevent /dev/input/event3";
	private String FILEPATH = CSVFILEPATH;

	static String ENCODING = "UTF-8";

	private String s = new String();
	private ArrayList<String[]> csvList = new ArrayList<String[]>();; 
	private ArrayList<String> eventList = new ArrayList<String>();; 

	public Endorphin(String filepath, String event) {
		FILEPATH = filepath;
		SENDEVENT = "sendevent /dev/input/event" + event;
	}

	/*
	 * CSV
	 */
	public void readCsv() {

		try {
			CsvReader csvReader = new CsvReader(FILEPATH, ',',
					Charset.forName(ENCODING));// 
			csvReader.readHeaders();// 
			while (csvReader.readRecord()) {// 
				csvList.add(csvReader.getValues());
			}
			csvReader.close();// 
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/*
	 * sendevent /dev/input/event0 3 $((0x35)) $((0x8b))
	 */
	public void format() {
		Event e;
		for (int row = 0; row < csvList.size(); row++) {
			if (!csvList.get(row)[0].equals("sleep")) {
				e = new Event(csvList, row);
				System.out.println(e);

				eventList.add(SENDEVENT + " " + e.getTypeTo10() + " "
						+ +e.getCodeTo10() + " " + e.getValueTo10());
			} else {
				 int i = Integer.valueOf(csvList.get(row)[1]) / 1000;
				 if (i < 1) {
				 eventList.add("sleep" + " " + "1");
				 } else {
				 eventList.add("sleep" + " " + i);
				 }
//				eventList.add("sleep" + " " + csvList.get(row)[1]);
			}

		}
	}

	/*
	 * 
	 */
	public void outFile() {
		try {
			StringBuilder sb = new StringBuilder();
			sb.append("#!/system/bin/sh \n");
			Iterator iter = eventList.iterator();
			while (iter.hasNext()) {
				sb.append(iter.next().toString());
				if (iter.hasNext()) {
					sb.append('\n');
				}
			}
			System.out.println(sb.toString());
			BufferedReader in4 = new BufferedReader(new StringReader(
					sb.toString()));
			PrintWriter out1 = new PrintWriter(new BufferedWriter(
					new FileWriter(OUTFILEPATH)));
			while ((s = in4.readLine()) != null)
				out1.println(s);
			out1.close();
		} catch (EOFException e) {
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/*
	 *  > adb push x.sh /sdcard/ > adb shell sh /sdcard/x.sh
	 */
	public void pushFile() {
		System.out.println(SDCARD + SDCARDFILENAME);
		String reString = run(new String[] { "adb", "push", OUTFILEPATH, SDCARD });
		reString = run(new String[] { "adb", "shell", "sh",
				SDCARD + SDCARDFILENAME });
	}

	public static synchronized String run(String[] cmd) {
		String line = "";
		InputStream is = null;

		try {
			Runtime runtime = Runtime.getRuntime();
			Process proc = runtime.exec(cmd);
			is = proc.getInputStream();

			BufferedReader buf = new BufferedReader(new InputStreamReader(is));
			do {
				// line = buf.readLine();
				// if (null == line) {
				line = buf.readLine();
				break;
				// }
			} while (true);

			if (is != null) {
				buf.close();
				is.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return line;
	}

	public void start() {
		this.readCsv();
		this.format();
		this.outFile();
		this.pushFile();
	};

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// 
		Endorphin e = new Endorphin("c:/2.csv", "3");
		e.start();
	}
}

/*
 * javabean
 */
class Event {
	String type;
	String code;
	String value;

	public Event(ArrayList<String[]> csvList, int row) {
		if (type != "" && code != "" && value != "") {
			this.type = csvList.get(row)[0];
			this.code = csvList.get(row)[1];
			this.value = csvList.get(row)[2];
		}

	}

	public Event(String type, String code, String value) {
		if (type != "" && code != "" && value != "") {
			this.type = type;
			this.code = code;
			this.value = value;
		}
	}

	public String getType() {
		return type;
	}

	public int getTypeTo10() {
		return Integer.parseInt(type, 16);
	}

	public String getCode() {
		return code;
	}

	public int getCodeTo10() {
		return Integer.parseInt(code, 16);
	}

	public String getValue() {
		return value;
	}

	public int getValueTo10() {
		return Integer.parseInt(value, 16);
	}

	@Override
	public String toString() {
		return type + "," + code + "," + value;
	}
}
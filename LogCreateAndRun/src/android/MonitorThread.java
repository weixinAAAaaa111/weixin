package android;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MonitorThread extends Thread {

	private Process exeEcho = null;	
	
	private String textValue = "";	

	public void run() {

		try {

			exeEcho = Runtime.getRuntime().exec(
					"adb shell getevent /dev/input/event" + this.textValue);
			InputStream inputstream = exeEcho.getInputStream();
			InputStreamReader inputstreamreader = new InputStreamReader(
					inputstream);
			BufferedReader bufferedreader = new BufferedReader(
					inputstreamreader);

			File fold = new File("C:/kenshin");
			File file = new File("C:/kenshin/test.csv");

			if (fold.exists() == false) {
				fold.mkdir();
			}

			file.createNewFile();
			FileWriter fw = new FileWriter(file);
			BufferedWriter bw = new BufferedWriter(fw);
			
			bw.write("type,code,value");
			bw.write("\n");

			String line = "";
			SimpleDateFormat tempDate = new SimpleDateFormat(
					"yyyyMMddHHmmssSSS");
			String beforeTime = "";
			while ((line = bufferedreader.readLine()) != null) {

				if (beforeTime.equals("")) {
					beforeTime = tempDate.format(new Date());
				} else {
					long beforeLTime = Long.parseLong(beforeTime);
					long nowLTime = Long.parseLong(tempDate.format(new Date()));
					long interval = nowLTime - beforeLTime;
					if (interval >= 100) {
						bw.write("sleep," + interval);
						bw.write("\n");
					}
					
					beforeTime = tempDate.format(new Date());
				}

				if ("".equals(line) == false) {
					bw.write(line.replace(" ", ","));
					bw.write("\n");
				}
			}

			bw.close();
			fw.close();
			inputstream.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void closeCommand() {
		if (this.exeEcho != null) {
			this.exeEcho.destroy();
		}
	}
	
	public void setTextValue(String textValue) {
		this.textValue = textValue;
	}
}

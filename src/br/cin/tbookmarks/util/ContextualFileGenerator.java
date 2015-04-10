package br.cin.tbookmarks.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.PatternSyntaxException;

import br.cin.tbookmarks.recommender.database.contextual.DayTypeContextualAttribute;
import br.cin.tbookmarks.recommender.database.contextual.PeriodOfDayContextualAttribute;

public class ContextualFileGenerator {
	
	private static int getDayType(String timestamp) {

		Date d = new Date(new Long(timestamp)*1000);
		Calendar cal = Calendar.getInstance();
		cal.setTime(d);
		int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
		
		if(dayOfWeek == 1 || dayOfWeek == 7){
			return DayTypeContextualAttribute.WEEKEND.getCode();
		}else{
			return DayTypeContextualAttribute.WEEKDAY.getCode();
		}

	}
	
	private static int getPeriodOfDay(String timestamp) {
		Date d = new Date(new Long(timestamp)*1000);
		Calendar cal = Calendar.getInstance();
		cal.setTime(d);
		int hourOfDay = cal.get(Calendar.HOUR_OF_DAY)+3; //GMT +3
		
		if(hourOfDay >= 0 && hourOfDay < 6){
			return PeriodOfDayContextualAttribute.DAWN.getCode();
		}else if(hourOfDay >= 6 && hourOfDay < 12){
			return PeriodOfDayContextualAttribute.MORNING.getCode();
		}else if(hourOfDay >= 12 && hourOfDay < 18){
			return PeriodOfDayContextualAttribute.AFTERNOON.getCode();
		}else{
			return PeriodOfDayContextualAttribute.NIGHT.getCode();
		}
	}

	//test
	/*public static void main(String[] args) {
		
		String timestampsTest[] = {"983074250","978555881","1428601510","1428741910"};
		int correctResponseDay[] = {1,0,0,1};
		int correctResponsePeriod[] = {0,3,2,1};
		
		for (int indexTest = 0; indexTest < timestampsTest.length;indexTest++) {
			if(getDayType(timestampsTest[indexTest]) != correctResponseDay[indexTest] ){
				System.out.println("Erro no dia p/ timestamp: "+timestampsTest[indexTest]+" expected: "+correctResponseDay[indexTest]+" got: "+getDayType(timestampsTest[indexTest]));
			}
			if(getPeriodOfDay(timestampsTest[indexTest]) != correctResponsePeriod[indexTest] ){
				System.out.println("Erro no periodOfday p/ timestamp: "+timestampsTest[indexTest]+" expected: "+correctResponsePeriod[indexTest]+" got: "+getPeriodOfDay(timestampsTest[indexTest]));
			}
		}*/

	public static void main(String[] args) {
		File fileEN = new File(System.getProperty("user.dir")
				+ "\\resources\\datasets\\cross-domain\\movies_cross_events_books.dat");
		File fileOutput = new File(System.getProperty("user.dir") + "\\resources\\datasets\\cross-domain\\contextual_movies_cross_events_books.dat");

		if (!fileOutput.exists()) {

			FileInputStream stream;

			String line = "";
			try {
				stream = new FileInputStream(fileEN);

				InputStreamReader streamReader = new InputStreamReader(stream);
				BufferedReader reader = new BufferedReader(streamReader);

				FileOutputStream streamOutput = new FileOutputStream(fileOutput);

				OutputStreamWriter streamWriter = new OutputStreamWriter(
						streamOutput);

				BufferedWriter bw = new BufferedWriter(streamWriter);
				
				while ((line = reader.readLine()) != null) {

					String splitedLine[] = line.split("\\t"); 
					
					String userIdText = splitedLine[0];
					String itemIdText = splitedLine[1];
					String rating = splitedLine[2];
					String timestamp = splitedLine[3];
					
					int dayType = getDayType(timestamp);
					int periodOfDay = getPeriodOfDay(timestamp);

					bw.append(userIdText + "\t" + itemIdText + "\t" + rating
							+ "\t" + dayType+"|"+periodOfDay);
					bw.newLine();

				}

				reader.close();
				streamReader.close();
				// streamOutput.close();
				// streamOutputInfo.close();
				bw.close();

			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ArrayIndexOutOfBoundsException e) {
				System.err.println(line);
				e.printStackTrace();
			} catch (PatternSyntaxException e) {
				System.err.println(line);
				e.printStackTrace();
			}
		}
	

	}

	
}

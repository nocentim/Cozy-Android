package org.cozyAndroid;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.ektorp.ViewQuery;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

public class TabCalendrier extends Activity {
	
	private static final String tag = "TabCalendrier";
	private Button selectedDayMonthYearButton;
	private Button currentMonth;
	private ImageView prevMonth;
	private ImageView nextMonth;
	private GridView calendarView;
	private GridCellAdapter adapter;
	private Calendar _calendar;
	private int month, year;
	private final DateFormat dateFormatter = new DateFormat();
    private static final String dateTemplate = "MMMM yyyy";
    
    // attributs touchDB
    private static String dayclicked;
    private static ViewQuery dviewQuery;
    
	public static String getDay() {
		return dayclicked;
	}
	
	public static void setViewQuery() {
		dviewQuery = new ViewQuery().designDocId(Replication.dDocId).viewName(Replication.byDayViewName).descending(true);
	}
	
	public static ViewQuery getViewQuery(){
		return dviewQuery;
	}
	
	
	public void onCreate(Bundle saveInstanceState) {
		super.onCreate(saveInstanceState);
		setContentView(R.layout.simple_calendar_view);
		View view = findViewById(R.id.selectedDayMonthYear);
		((TextView) view).setTextColor(Color.parseColor("#FFFFFF"));
		view = findViewById(R.id.currentMonth);
		((TextView) view).setTextColor(Color.parseColor("#FFFFFF"));
		
		_calendar = Calendar.getInstance(Locale.getDefault());
		month = _calendar.get(Calendar.MONTH) + 1;
		year = _calendar.get(Calendar.YEAR);
		// Récupération de la vue associée au jour selectionné sur le calendrier
		selectedDayMonthYearButton = (Button) this.findViewById(R.id.selectedDayMonthYear);
		((TextView) view).setTextColor(Color.parseColor("#FFFFFF"));
		
		// Récupération de la vue associée au bouton permettant de passer au mois précédent
		prevMonth = (ImageView) this.findViewById(R.id.prevMonth);
		prevMonth.setOnClickListener(prevORnextMonthClicked);
		currentMonth = (Button) this.findViewById(R.id.currentMonth);
		SimpleDateFormat s;
		s = new SimpleDateFormat("MMMM yyyy",Locale.FRANCE)	;	
		
		currentMonth.setText(s.format(_calendar.getTime()));
		
		// Récupération de la vue associée au bouton permettant de passer au mois suivant
		nextMonth = (ImageView) this.findViewById(R.id.nextMonth);
		nextMonth.setOnClickListener(prevORnextMonthClicked);
		
		//Récupération du calendrier en lui-même
		calendarView = (GridView) this.findViewById(R.id.calendar);
	
		
		adapter = new GridCellAdapter(getApplicationContext(), R.id.calendar_day_gridcell, month, year);
		adapter.notifyDataSetChanged();
		calendarView.setAdapter(adapter);

	}
	
	/**
    *
    * @param month
    * @param year
    */   
   private void setGridCellAdapterToDate(int month, int year) {
		adapter = new GridCellAdapter(getApplicationContext(), R.id.calendar_day_gridcell, month, year);
		_calendar.set(year,month-1,1);
		SimpleDateFormat s;
		s = new SimpleDateFormat("MMMM yyyy",Locale.FRANCE)	;
		currentMonth.setText(s.format(_calendar.getTime()));
		adapter.notifyDataSetChanged();
		calendarView.setAdapter(adapter);
	}

   public void onClick(View v) {
	   Log.d("click", "jour");
	   if (v == prevMonth) {
		   if (month <= 1) {
			   month = 12;
			   year--;
		   } else {
			   month--;
           }
		   setGridCellAdapterToDate(month, year);
	   }
	   if (v == nextMonth) {
		   if (month > 11) {
			   month = 1;
			   year++;
		   } else {
			   month++;
		   }
		   setGridCellAdapterToDate(month, year);
	   }
   }

   @Override
   public void onDestroy()
       {
           Log.d(tag, "Destroying View ...");
           super.onDestroy();
       }
   
// Listener sur les boutons permettant de changer de mois
	private View.OnClickListener prevORnextMonthClicked = new View.OnClickListener() {
		
		public void onClick(View v) {
			if (v == prevMonth) {
				if (month <= 1) {
					month = 12;
					year--;
				} else {
					month--;
				}
				setGridCellAdapterToDate(month, year);
			}
			if (v == nextMonth) {
				if (month > 11) {
					month = 1;
					year++;
				} else {
					month++;
				}
				setGridCellAdapterToDate(month, year);
			}
		}
	};
	
	public class GridCellAdapter extends BaseAdapter {
		private final Context _context;

		private final List<String> list;
		private static final int DAY_OFFSET = 1;
		@SuppressWarnings("unused")
		private final String[] weekdays = new String[]{"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
		private final String[] months = {"Janvier", "Fevrier", "Mars", "Avril", "Mai", "Juin", "Juillet", "Août", "Septembre", "Octobre", "Novembre", "Decembre"};
		private final int[] daysOfMonth = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
		@SuppressWarnings("unused")
		private final int month, year;
		@SuppressWarnings("unused")
		private int daysInMonth, prevMonthDays;
		private int currentDayOfMonth;
		private int currentMonth;
		private int currentYear;
		private int currentWeekDay;
		private Button gridcell;	
		private Set<String> dateEvents;
		private String currentDayNumber;
		private String currentMonthNumber;
		private String currentYearNumber;
		private String currentDate; // dd-MM-yyyy

		// Jour du mois courant
		public GridCellAdapter(Context context, int textViewResourceId, int month, int year) {
			super();
			this._context = context;
			this.list = new ArrayList<String>();
			this.month = month;
			this.year = year;
		
			
			// nouvelle instance du calendrier
			Calendar calendar = Calendar.getInstance();
			setCurrentDayOfMonth(calendar.get(Calendar.DAY_OF_MONTH));
			setCurrentMonth(calendar.get(Calendar.MONTH));
			setCurrentYear(calendar.get(Calendar.YEAR));
			setCurrentWeekDay(calendar.get(Calendar.DAY_OF_WEEK));
			
			currentDayNumber = String.valueOf(getCurrentDayOfMonth());
			if (currentDayNumber.length() == 1) {
				currentDayNumber = "0"+currentDayNumber;
			}
			currentMonthNumber = String.valueOf(getCurrentMonth()+1);
			if (currentMonthNumber.length() == 1) {
				currentMonthNumber = "0"+currentMonthNumber;
			}
			currentYearNumber = String.valueOf(getCurrentYear());
			if (currentYearNumber.length() == 1) {
				currentYearNumber = "0"+currentYearNumber;
			}
			currentDate = currentDayNumber+"-"+currentMonthNumber+"-"+currentYearNumber;

			// Affichage du mois
			printMonth(month, year);	
		}
		
		private String getMonthAsString(int i) {
			return months[i];
		}

		private int getNumberOfDaysOfMonth(int i) {
			return daysOfMonth[i];
		}

		public String getItem(int position) {
			return list.get(position);
		}

		
		public int getCount() {
			return list.size();
		}

		/**
		 * Affichage du mois
		 * 
		 * @param mm
		 * @param yy
		 */
		private void printMonth(int mm, int yy) {
			// Le nombre de jour à laisser transparent 
			// au début du mois considéré.
			int trailingSpaces = 0;
			int daysInPrevMonth = 0;
			int prevMonth = 0;
			int prevYear = 0;
			int nextMonth = 0;
			int nextYear = 0;
			boolean todayInActualMonth = false;
			boolean cour = false;

			int currentMonth = mm - 1;
			
			// date courante sous le format "dd-MM-yyyy"
			String today = this.getCurrentDate();
			
			String monthNumber = String.valueOf(mm);
			if (monthNumber.length() == 1) {
				monthNumber = "0"+monthNumber;
			}
			String yearNumber = String.valueOf(yy);
			
			String month = monthNumber+"-"+yearNumber;
			if (today.contains(month)) {
				todayInActualMonth = true;
			}
			
			daysInMonth = getNumberOfDaysOfMonth(currentMonth);

			

			// Gregorian Calendar : MINUS 1, set to FIRST OF MONTH
			GregorianCalendar cal = new GregorianCalendar(yy, currentMonth, 1);
			

			if (currentMonth == 11) {
				prevMonth = currentMonth - 1;
				daysInPrevMonth = getNumberOfDaysOfMonth(prevMonth);
				nextMonth = 0;
				prevYear = yy;
				nextYear = yy + 1;
			} else if (currentMonth == 0) {
				prevMonth = 11;
				prevYear = yy - 1;
				nextYear = yy;
				daysInPrevMonth = getNumberOfDaysOfMonth(prevMonth);
				nextMonth = 1;
			} else {
				prevMonth = currentMonth - 1;
				nextMonth = currentMonth + 1;
				nextYear = yy;
				prevYear = yy;
				daysInPrevMonth = getNumberOfDaysOfMonth(prevMonth);
			}

			// Compute how much to leave before the first day of the
			// month.
			// getDay() retourne 0 pour Dimanche.
			int currentWeekDay = cal.get(Calendar.DAY_OF_WEEK) - 1;
			trailingSpaces = currentWeekDay;
			
			if (cal.isLeapYear(cal.get(Calendar.YEAR)) && mm == 1) {
				++daysInMonth;
			}

			// Trailing Month days
			for (int i = 0; i < trailingSpaces; i++) {
				list.add(String.valueOf((daysInPrevMonth - trailingSpaces + DAY_OFFSET) + i) + "-GREY" + "-" + getMonthAsString(prevMonth) + "-" + prevYear);	
			}

			// Current Month Days			
			for (int i = 1; i <= daysInMonth; i++) {
				String date = getDateDay(i,mm,yy);
                if (i == getCurrentDayOfMonth()) {
                	list.add(String.valueOf(i) + "-BLUE" + "-" + getMonthAsString(currentMonth) + "-" + yy);   
                } else if (dviewQuery.startKey(date).endKey(date)!= null) {
                	list.add(String.valueOf(i) + "-ORANGE" + "-" + getMonthAsString(currentMonth) + "-" + yy);
                } else {
                	list.add(String.valueOf(i) + "-WHITE" + "-" + getMonthAsString(currentMonth) + "-" + yy);
                }
            }
			
			
			// Leading Month days
			for (int i = 0; i < list.size() % 7; i++) {
				list.add(String.valueOf(i + 1) + "-GREY" + "-" + getMonthAsString(nextMonth) + "-" + nextYear);
			}
			
		}
		
		public String getDateDay(int d, int m, int y) {
			String dayNumber = String.valueOf(d);
			if (dayNumber.length() == 1) {
				dayNumber = "0"+dayNumber;
			}
			String monthNumber = String.valueOf(m);
			if (monthNumber.length() == 1) {
				monthNumber = "0"+monthNumber;
			}
			String yearNumber = String.valueOf(y);
			return dayNumber+"-"+monthNumber+"-"+yearNumber;
		}

		public long getItemId(int position) {
			return position;
		}

		
		public View getView(int position, View convertView, ViewGroup parent) {
			View row = convertView;
			if (row == null) {
				LayoutInflater inflater = (LayoutInflater) _context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				row = inflater.inflate(R.layout.calendar_day_gridcell, parent, false);
			}

			// Get a reference to the Day gridcell
			gridcell = (Button) row.findViewById(R.id.calendar_day_gridcell);
			gridcell.setOnClickListener(dayClicked);

			// ACCOUNT FOR SPACING

			String[] day_color = list.get(position).split("-");
			String theday = day_color[0];
			String themonth = day_color[2];
			String theyear = day_color[3];

			// Set the Day GridCell
			gridcell.setText(theday);
			gridcell.setTag(theday + "-" + themonth + "-" + theyear);

			
			if (day_color[1].equals("GREY")) {
				gridcell.setTextColor(Color.LTGRAY);
			}
			if (day_color[1].equals("WHITE")) {
				gridcell.setTextColor(Color.WHITE);
			}
			if (day_color[1].equals("BLACK")) {
				//gridcell.setTextColor(getResources().getColor(R.color.static_text_color));
				gridcell.setTextColor(Color.BLACK);
			}
			
			if (day_color[1].equals("ORANGE")) {
				gridcell.setText("  "+theday+" *");
				gridcell.setTextColor(Color.WHITE);
				//gridcell.setTextColor(Color.rgb(255,140,0));
			}
			if (day_color[1].equals("EVBLACK")) {
				//gridcell.setTextColor(getResources().getColor(R.color.static_text_color));
				gridcell.setText("  "+theday+" *");
				gridcell.setTextColor(Color.BLACK);
			}
			/*if (day_color[1].equals("RED")) {
				gridcell.setText("  "+theday+" *");
				gridcell.setTextColor(Color.WHITE);
				//gridcell.setTextColor(Color.RED);
			}*/
			return row;
		}
		
		
		private View.OnClickListener dayClicked = new View.OnClickListener() {
			public void onClick(View view) {
				String dayMonthYear = (String)view.getTag();
				int index0 = dayMonthYear.indexOf("-");
				String sub = dayMonthYear.substring(index0+1);
				int index1 = sub.indexOf("-");
				String day = dayMonthYear.substring(0,index0);
				String month = dayMonthYear.substring(index0+1,index0+index1+1);
				String year = dayMonthYear.substring(index0+index1+2,dayMonthYear.length());
				selectedDayMonthYearButton.setText(dayMonthYear);
				
				if (day.length() == 1) {
					day = "0"+day;
				}

				if (month.equalsIgnoreCase("Janvier")) {
					month = "01";
				}
				if (month.equalsIgnoreCase("Fevrier")) {
					month = "02";
				}
				if (month.equalsIgnoreCase("Mars")) {
					month = "03";
				}
				if (month.equalsIgnoreCase("Avril")) {
					month = "04";
				}
				if (month.equalsIgnoreCase("Mai")) {
					month = "05";
				}
				if (month.equalsIgnoreCase("Juin")) {
					month = "06";
				}
				if (month.equalsIgnoreCase("Juillet")) {
					month = "07";
				}
				if (month.equalsIgnoreCase("Août")) {
					month = "08";
				}
				if (month.equalsIgnoreCase("Septembre")) {
					month = "09";
				}
				if (month.equalsIgnoreCase("Octobre")) {
					month = "10";
				}
				if (month.equalsIgnoreCase("Novembre")) {
					month = "11";
				}
				if (month.equalsIgnoreCase("Decembre")) {
					month = "12";
				}
				// C'est ici qu'on fait l'action après avoir appuyé sur un jour
				// IMPORTANT
				dayclicked = " "+ day + "-" + month + "-" + year;
				Intent intent = new Intent(getBaseContext(), NoteByDay.class);
				startActivity(intent);
			}
		};

		public int getCurrentDayOfMonth() {
			return currentDayOfMonth;
		}

		private void setCurrentDayOfMonth(int currentDayOfMonth) {
			this.currentDayOfMonth = currentDayOfMonth;
		}
		
		public int getCurrentMonth() {
			return currentMonth;
		}

		private void setCurrentMonth(int currentMonth) {
			this.currentMonth = currentMonth;
		}
		
		public int getCurrentYear() {
			return currentYear;
		}

		private void setCurrentYear(int currentYear) {
			this.currentYear = currentYear;
		}
		
		public void setCurrentWeekDay(int currentWeekDay) {
			this.currentWeekDay = currentWeekDay;
		}
		
		public int getCurrentWeekDay() {
			return currentWeekDay;
		}
		
		public String getCurrentDate() {
			return currentDate;
		}
	}
}
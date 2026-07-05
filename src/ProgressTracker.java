import java.awt.*;
import java.text.*;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Ellipse2D;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;
import java.util.Calendar;
import java.util.Date;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.*;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.time.Day;

public class ProgressTracker {
	private JPanel panel, buttonPanel, frontPanel;
	private JLayeredPane basePanel;
	private ChartPanel chartPanel;
	private JFreeChart chart;
	private JButton weekView, monthView, yearView;
	private LocalDate start, end;
	private Date lower, upper;
	private Calendar calendar;
	private DateAxis x_axis;
	private NumberAxis y_axis;
	private JLabel totalHours;
	
	public ProgressTracker() {
        Image frontImg = new ImageIcon (getClass().getResource("/images/Progress_Tracker_Foreground.png")).getImage();
        Image bgImg = new ImageIcon (getClass().getResource("/images/Progress_Tracker_Background.png")).getImage();
		
		calendar = Calendar.getInstance();
		
		weekView = new JButton("Week view");
		monthView = new JButton("Month view");
		yearView = new JButton("Year view");
		
		chart = ChartFactory.createTimeSeriesChart("Hours Productive", "Day", "Hours", Main.getData().getSeriesColl(), true, true, false);
		
		//custom format to display hours and minutes
		NumberFormat yAxisFormat = new NumberFormat() { 
			public StringBuffer format(double number, StringBuffer toAppendTo, FieldPosition pos) {
				int h = (int)number;
				int m = (int)((number - h)*60);
				
				//if both hours and minutes recorded
				if (h>0 && m>0) 
					toAppendTo.append(h).append("h ").append(m).append("m");
				//if only hours
				else if (h>0)
					toAppendTo.append(h).append("h");
				//if only minutes
				else
					toAppendTo.append(m).append("m");
				
				return toAppendTo;
			}

			public StringBuffer format(long number, StringBuffer toAppendTo, FieldPosition pos) {
				return format((double) number, toAppendTo, pos);
			}

			public Number parse(String source, ParsePosition parsePosition) {
				return null;
			}
			
		};
		
		//format axes
		XYPlot plot = chart.getXYPlot();
		XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer(true,true);
		renderer.setSeriesShape(0,new Ellipse2D.Double(-6, -6, 12, 12));
		renderer.setSeriesPaint(0, new Color(82, 132, 151));		
		
		//add tool tips to display more info about data points when mouse hovers over it
		renderer.setDefaultToolTipGenerator(new StandardXYToolTipGenerator("{1}: {2}", new SimpleDateFormat("MMM dd"), yAxisFormat));		
		plot.setRenderer(renderer);
		
		x_axis = (DateAxis) plot.getDomainAxis();
		y_axis = (NumberAxis) plot.getRangeAxis();
		changeView("WEEK");
		y_axis.setAutoRange(true);
		y_axis.setTickUnit(new NumberTickUnit(Main.getData().getSeries().getMaxY()/10));
		y_axis.setNumberFormatOverride(yAxisFormat);
	
		chartPanel = new ChartPanel(chart);
		chartPanel.setDismissDelay(Integer.MAX_VALUE);
		chartPanel.setInitialDelay(0); 
		chartPanel.setReshowDelay(0);
		chartPanel.setPreferredSize(new Dimension(1150, 575));
		
		buttonPanel = new JPanel();
		buttonPanel.add(weekView);
		buttonPanel.add(monthView);
		buttonPanel.add(yearView);
		
		totalHours = new JLabel("Total Time: " + (int)Main.getData().getHoursFocused() + " hours " + (int)((Main.getData().getHoursFocused()-(int)Main.getData().getHoursFocused())*60) + " minutes");
		totalHours.setForeground(new Color(76, 82, 74));
		totalHours.setFont(Main.getCustomFont().deriveFont(25f));
		
		basePanel = new JLayeredPane() {
		    public void doLayout() {
		        for (Component c : getComponents())
		            c.setBounds(0, 0, getWidth(), getHeight());
		    }
		};
		frontPanel = new JPanel() {
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				g.drawImage(frontImg, 0, 0, getWidth(), getHeight(), this);
			}
		};
		frontPanel.setOpaque(false);	
		
		panel = new JPanel() {
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				g.drawImage(bgImg, 0, 0, getWidth(), getHeight(), this);
			}
		};
		panel.setLayout(new BorderLayout());
		
		JPanel chartWrapper = new JPanel(new GridBagLayout());
		chartWrapper.setOpaque(false); // keep background visible
		chartWrapper.add(chartPanel);
		panel.add(chartWrapper, BorderLayout.CENTER);
		panel.add(buttonPanel, BorderLayout.SOUTH);
		panel.add(totalHours, BorderLayout.NORTH);
		basePanel.add(panel, JLayeredPane.DEFAULT_LAYER);
		basePanel.add(frontPanel, JLayeredPane.PALETTE_LAYER);
		
		Main.getCardPanel().add(basePanel, "Progress Tracker");
		
		weekView.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				changeView("WEEK");
			}
		});	
		
		monthView.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				changeView("MONTH");
			}
		});	
		
		yearView.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				changeView("YEAR");
			}
		});	
	}
	
	public void addPoint(double hours) {
		Day today = new Day();
		
		Number oldValue = Main.getData().getSeries().getValue(today);
		double updatedVal;
		
		if(oldValue!=null)
			updatedVal = oldValue.doubleValue() + hours;
		else
			updatedVal = hours;
		
		//update y-axis range
		y_axis.setTickUnit(new NumberTickUnit(Main.getData().getSeries().getMaxY()/10));
		
		totalHours.setText("Total Time: " + (int)Main.getData().getHoursFocused() + " hours " + (int)((Main.getData().getHoursFocused()-(int)Main.getData().getHoursFocused())*60) + " minutes");
		Main.getData().getSeries().addOrUpdate(today, updatedVal);
		Data.saveData(Main.getData());
	}
	
	void changeView(String view) {		
		if (view.equals("WEEK")) {
			start = LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY));
			end = start.plusDays(6);
			
			x_axis.setDateFormatOverride(new SimpleDateFormat("MMM dd"));
			x_axis.setTickUnit(new DateTickUnit(DateTickUnitType.DAY, 1));
			x_axis.setLabel("Day");
		}
		else if(view.equals("MONTH")) {
			start = LocalDate.now().withDayOfMonth(1);
			end = start.plusMonths(1).minusDays(1);
			
			x_axis.setDateFormatOverride(new SimpleDateFormat("MMM dd"));
			x_axis.setTickUnit(new DateTickUnit(DateTickUnitType.DAY, 2));
			x_axis.setLabel("Day");
		}
		else if (view.equals("YEAR")){
			start = LocalDate.now().withDayOfYear(1);
			end = start.plusYears(1).minusDays(1);
			
			x_axis.setDateFormatOverride(new SimpleDateFormat("MMM"));
			x_axis.setTickUnit(new DateTickUnit(DateTickUnitType.MONTH, 1));
			x_axis.setLabel("Month");
		}
		else
			return;
		
		lower = Date.from(start.atStartOfDay(ZoneId.systemDefault()).toInstant());
		upper = Date.from(end.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant());	
		x_axis.setRange(lower, upper); 
	}
}

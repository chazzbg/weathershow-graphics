package chazzwsg;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import javax.imageio.ImageIO;
import javax.swing.*;
import org.jdesktop.swingx.painter.ImagePainter;
import org.jdesktop.swingx.painter.Painter;

/**
 *
 * @author Chazz
 */
public class WeatherShowView extends JFrame {

	private CurrentConditions cc;
	private City city;
	private Forecast[] forecast;
	private Weather weather;
	public static JDialog aboutBox;
	public static JDialog setLocationBox;
	private String location;
	Image image;
	Point locationPoint;
	Point locationPointOnScreen;
	int startX;
	int startY;
	private WeatherShowTray tray;
	private boolean visible = true;

	/**
	 * Creates new form WeatherShowView
	 */
	public WeatherShowView() {
		initComponents();
		tray = new WeatherShowTray(this);
		setTimer();
	}
	// <editor-fold defaultstate="collapsed" desc="UI Update">

	private void updateUI() {
		cityLabel.setText(city.getCity());
		tempLabel.setText(cc.getTempC() + "°C");
		windLabel.setText("Wind: " + cc.getWindSpeedKmph() + " km\\h " + cc.getWindDir16Point());
		humidityLabel.setText("Humidity: " + cc.getHumidity() + "%");
		visibilityLabel.setText("Visibility: " + cc.getVisibility() + " km");
		cloudCoverLabel.setText("Cloud cover: " + cc.getCloudCover() + "%");
		precipLabel.setText("Precip: " + cc.getPrecipMM() + " mm");
		pressureLabel.setText("Pressure: " + cc.getPressure() + " mb");
		weatherDescLabel.setText(cc.getWeatherDesc());
		weatherIcon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/chazzwsg/resources/images/weather_" + cc.getWeatherCode() + ".png")));
		DateFormat formatOrig = new SimpleDateFormat("y-m-d h:mm a", Locale.US);
		DateFormat format = new SimpleDateFormat("h:mm a", Locale.US);
		try {
			Date obsDate = formatOrig.parse(cc.getLocalObsDateTime());

			lastObservedLabel.setText("Last observation: " + format.format(obsDate));
		} catch (ParseException ex) {
			System.out.println("Parse date error " + ex);
		}

		try {
			DateFormat[] formatForecast = new SimpleDateFormat[forecast.length];
			DateFormat[] formatForecastDay = new SimpleDateFormat[forecast.length];
			Date[] day = new Date[forecast.length];
			String[] days = new String[forecast.length];

			for (int i = 0; i < forecast.length; i++) {
				formatForecast[i] = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
				formatForecastDay[i] = new SimpleDateFormat("EEEE", Locale.US);
				day[i] = formatForecast[i].parse(forecast[i].getDate());
				days[i] = formatForecastDay[i].format(day[i]);
			}

			day1Label.setText(days[0]);
			day2Label.setText(days[1]);
			day3Label.setText(days[2]);
			day4Label.setText(days[3]);

			day1H.setText("H: " + forecast[0].getTempMaxC() + "°C");
			day1L.setText("L: " + forecast[0].getTempMinC() + "°C");

			day2H.setText("H: " + forecast[1].getTempMaxC() + "°C");
			day2L.setText("L: " + forecast[1].getTempMinC() + "°C");

			day3H.setText("H: " + forecast[2].getTempMaxC() + "°C");
			day3L.setText("L: " + forecast[2].getTempMinC() + "°C");

			day4H.setText("H: " + forecast[3].getTempMaxC() + "°C");
			day4L.setText("L: " + forecast[3].getTempMinC() + "°C");

			day1Icon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/chazzwsg/resources/images/weather_" + forecast[0].getWeatherCode() + "_small.png")));
			day2Icon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/chazzwsg/resources/images/weather_" + forecast[1].getWeatherCode() + "_small.png")));
			day3Icon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/chazzwsg/resources/images/weather_" + forecast[2].getWeatherCode() + "_small.png")));
			day4Icon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/chazzwsg/resources/images/weather_" + forecast[3].getWeatherCode() + "_small.png")));
			tray.setTrayIcon(cc.getWeatherCode());
			tray.setTrayTitle(city.getCity() + "\n" + cc.getWeatherDesc() + "\n" + cc.getTempC() + "°C");
		} catch (ParseException ex) {
			System.out.println("Parse date error " + ex);
		} catch (NullPointerException ex) {
			System.out.println(ex);
		}
	}// </editor-fold>

	private void weatherFetch() {
		weather = new Weather(this);

		Runnable newTask = new Runnable() {

			@Override
			public void run() {
				//progressBar.setIndeterminate(true);
				weather.init();

				if (city != null
						&& cc != null
						&& forecast != null) {
					updateUI();
				}
				//progressBar.setIndeterminate(false);
			}
		};

		new Thread(newTask).start();
	}

	private void setTimer() {
		Timer timer = new Timer();
		TimerTask task = new TimerTask() {

			@Override
			public void run() {
				weatherFetch();
			}
		};
		timer.scheduleAtFixedRate(task, 0l, 10800000l);
	}

	public void showError(String err) {
		JOptionPane.showMessageDialog(rootPane, err, "Error", 0);
	}

	public void debug(Object obj) {
		System.out.println(obj);
	}
	//@Override

	public void paints(Graphics g) {

		Toolkit tool = Toolkit.getDefaultToolkit();
		image = tool.getImage(getClass().getResource("/chazzwsg/resources/background/default.png"));

		g.drawImage(image, 30, 85, this);

	}

	public Painter getPainter(String where) {

		BufferedImage img = null;
		try {
			if (where.equals("top")) {
				img = ImageIO.read(getClass().getResource("/chazzwsg/resources/background/default.png"));
			} else {
				img = ImageIO.read(getClass().getResource("/chazzwsg/resources/background/base.png"));
			}
		} catch (IOException ex) {
			System.out.println(ex);
		}
		ImagePainter imgPaint = new ImagePainter(img);
		return imgPaint;
	}

	/**
	 * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The content of this method is
	 * always regenerated by the Form Editor.
	 */
	@SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panel = new org.jdesktop.swingx.JXPanel();
        pressureLabel = new javax.swing.JLabel();
        visibilityLabel = new javax.swing.JLabel();
        cityLabel = new javax.swing.JLabel();
        weatherIcon = new javax.swing.JLabel();
        tempLabel = new javax.swing.JLabel();
        lastObservedLabel = new javax.swing.JLabel();
        windLabel = new javax.swing.JLabel();
        precipLabel = new javax.swing.JLabel();
        humidityLabel = new javax.swing.JLabel();
        cloudCoverLabel = new javax.swing.JLabel();
        weatherDescLabel = new javax.swing.JLabel();
        exitButton = new javax.swing.JButton();
        jXPanel1 = new org.jdesktop.swingx.JXPanel();
        day1Label = new javax.swing.JLabel();
        day1Icon = new javax.swing.JLabel();
        day2Label = new javax.swing.JLabel();
        day3Label = new javax.swing.JLabel();
        day4Label = new javax.swing.JLabel();
        day1H = new javax.swing.JLabel();
        day1L = new javax.swing.JLabel();
        day2Icon = new javax.swing.JLabel();
        day2H = new javax.swing.JLabel();
        day2L = new javax.swing.JLabel();
        day3Icon = new javax.swing.JLabel();
        day3H = new javax.swing.JLabel();
        day3L = new javax.swing.JLabel();
        day4Icon = new javax.swing.JLabel();
        day4H = new javax.swing.JLabel();
        day4L = new javax.swing.JLabel();
        aboutButton = new javax.swing.JButton();
        setLocationButton = new javax.swing.JButton();
        refreshButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Weather Show");
        setBackground(new Color(0,0,0,0));
        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        setIconImage(new ImageIcon(getClass().getResource("/chazzwsg/resources/images/icon.png")).getImage());
        setLocationByPlatform(true);
        setResizable(false);
        addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                formMousePressed(evt);
            }
        });
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowIconified(java.awt.event.WindowEvent evt) {
                formWindowIconified(evt);
            }
        });
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                formComponentResized(evt);
            }
        });
        addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                formMouseDragged(evt);
            }
        });

        panel.setBackground(new Color(0,0,0,0));
        panel.setBackgroundPainter(getPainter("top"));
        panel.setInheritAlpha(false);
        panel.setOpaque(false);
        panel.setPreferredSize(new java.awt.Dimension(520, 150));
        panel.setScrollableHeightHint(org.jdesktop.swingx.ScrollableSizeHint.NONE);

        pressureLabel.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        pressureLabel.setText("Pressure: 0 MP");
        pressureLabel.setForeground(new java.awt.Color(255, 255, 255));

        visibilityLabel.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        visibilityLabel.setText("Visibility: 0km");
        visibilityLabel.setForeground(new java.awt.Color(255, 255, 255));

        cityLabel.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        cityLabel.setText("City");
        cityLabel.setForeground(new java.awt.Color(255, 255, 255));

        weatherIcon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/chazzwsg/resources/images/na.png"))); // NOI18N

        tempLabel.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        tempLabel.setText("0°C");
        tempLabel.setForeground(new java.awt.Color(255, 255, 255));

        lastObservedLabel.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        lastObservedLabel.setText("Last observed: 10:15 PM");
        lastObservedLabel.setForeground(new java.awt.Color(255, 255, 255));

        windLabel.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        windLabel.setText("Wind: 0 kmp/h N");
        windLabel.setForeground(new java.awt.Color(255, 255, 255));

        precipLabel.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        precipLabel.setText("Precip: 0 MM");
        precipLabel.setForeground(new java.awt.Color(255, 255, 255));

        humidityLabel.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        humidityLabel.setText("Humidity: 0%");
        humidityLabel.setForeground(new java.awt.Color(255, 255, 255));

        cloudCoverLabel.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        cloudCoverLabel.setText("Cloud Cover: 0%");
        cloudCoverLabel.setForeground(new java.awt.Color(255, 255, 255));

        weatherDescLabel.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        weatherDescLabel.setText("WeatherDesc");
        weatherDescLabel.setForeground(new java.awt.Color(255, 255, 255));

        javax.swing.GroupLayout panelLayout = new javax.swing.GroupLayout(panel);
        panel.setLayout(panelLayout);
        panelLayout.setHorizontalGroup(
            panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelLayout.createSequentialGroup()
                .addContainerGap(22, Short.MAX_VALUE)
                .addGroup(panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelLayout.createSequentialGroup()
                        .addGroup(panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(panelLayout.createSequentialGroup()
                                .addComponent(windLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(humidityLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(visibilityLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(panelLayout.createSequentialGroup()
                                .addComponent(cloudCoverLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(precipLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(pressureLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(lastObservedLabel))
                        .addGap(16, 16, 16))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelLayout.createSequentialGroup()
                        .addGroup(panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, panelLayout.createSequentialGroup()
                                .addComponent(tempLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(weatherDescLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 271, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(cityLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 340, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)))
                .addComponent(weatherIcon)
                .addContainerGap(15, Short.MAX_VALUE))
        );
        panelLayout.setVerticalGroup(
            panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(weatherIcon)
                    .addGroup(panelLayout.createSequentialGroup()
                        .addComponent(cityLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(tempLabel)
                            .addComponent(weatherDescLabel))
                        .addGap(18, 18, 18)
                        .addGroup(panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(windLabel)
                            .addComponent(humidityLabel)
                            .addComponent(visibilityLabel))
                        .addGap(3, 3, 3)
                        .addGroup(panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(cloudCoverLabel)
                            .addComponent(precipLabel)
                            .addComponent(pressureLabel))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(lastObservedLabel)))
                .addGap(0, 14, Short.MAX_VALUE))
        );

        exitButton.setText("Exit");
        exitButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exitButtonActionPerformed(evt);
            }
        });

        jXPanel1.setBackground(new Color(0,0,0,1));
        jXPanel1.setBackgroundPainter(getPainter("base"));
        jXPanel1.setInheritAlpha(false);
        jXPanel1.setOpaque(false);
        jXPanel1.setPreferredSize(new java.awt.Dimension(520, 100));

        day1Label.setFont(new java.awt.Font("Tahoma", 1, 10)); // NOI18N
        day1Label.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        day1Label.setText("Day");
        day1Label.setForeground(new java.awt.Color(255, 255, 255));
        day1Label.setPreferredSize(new java.awt.Dimension(120, 14));

        day1Icon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/chazzwsg/resources/images/na_small.png"))); // NOI18N

        day2Label.setFont(new java.awt.Font("Tahoma", 1, 10)); // NOI18N
        day2Label.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        day2Label.setText("Day");
        day2Label.setFocusCycleRoot(true);
        day2Label.setForeground(new java.awt.Color(255, 255, 255));
        day2Label.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        day2Label.setPreferredSize(new java.awt.Dimension(120, 14));

        day3Label.setFont(new java.awt.Font("Tahoma", 1, 10)); // NOI18N
        day3Label.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        day3Label.setText("Day");
        day3Label.setForeground(new java.awt.Color(255, 255, 255));
        day3Label.setPreferredSize(new java.awt.Dimension(120, 14));

        day4Label.setFont(new java.awt.Font("Tahoma", 1, 10)); // NOI18N
        day4Label.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        day4Label.setText("Day");
        day4Label.setForeground(new java.awt.Color(255, 255, 255));
        day4Label.setPreferredSize(new java.awt.Dimension(120, 14));

        day1H.setFont(new java.awt.Font("Tahoma", 1, 10)); // NOI18N
        day1H.setText("H: 0°C");
        day1H.setForeground(new java.awt.Color(255, 255, 255));
        day1H.setPreferredSize(new java.awt.Dimension(50, 15));

        day1L.setFont(new java.awt.Font("Tahoma", 1, 10)); // NOI18N
        day1L.setText("L: 0°C");
        day1L.setForeground(new java.awt.Color(255, 255, 255));
        day1L.setPreferredSize(new java.awt.Dimension(50, 15));

        day2Icon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/chazzwsg/resources/images/na_small.png"))); // NOI18N

        day2H.setFont(new java.awt.Font("Tahoma", 1, 10)); // NOI18N
        day2H.setText("H: 0°C");
        day2H.setForeground(new java.awt.Color(255, 255, 255));
        day2H.setPreferredSize(new java.awt.Dimension(50, 15));

        day2L.setFont(new java.awt.Font("Tahoma", 1, 10)); // NOI18N
        day2L.setText("L: 0°C");
        day2L.setForeground(new java.awt.Color(255, 255, 255));
        day2L.setPreferredSize(new java.awt.Dimension(50, 15));

        day3Icon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/chazzwsg/resources/images/na_small.png"))); // NOI18N

        day3H.setFont(new java.awt.Font("Tahoma", 1, 10)); // NOI18N
        day3H.setText("H:0°C");
        day3H.setForeground(new java.awt.Color(255, 255, 255));
        day3H.setPreferredSize(new java.awt.Dimension(50, 15));

        day3L.setFont(new java.awt.Font("Tahoma", 1, 10)); // NOI18N
        day3L.setText("L: 0°C");
        day3L.setForeground(new java.awt.Color(255, 255, 255));
        day3L.setPreferredSize(new java.awt.Dimension(50, 15));

        day4Icon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/chazzwsg/resources/images/na_small.png"))); // NOI18N

        day4H.setFont(new java.awt.Font("Tahoma", 1, 10)); // NOI18N
        day4H.setText("H: 0°C");
        day4H.setForeground(new java.awt.Color(255, 255, 255));
        day4H.setPreferredSize(new java.awt.Dimension(50, 15));

        day4L.setFont(new java.awt.Font("Tahoma", 1, 10)); // NOI18N
        day4L.setText("L: 0°C");
        day4L.setForeground(new java.awt.Color(255, 255, 255));
        day4L.setPreferredSize(new java.awt.Dimension(50, 15));

        javax.swing.GroupLayout jXPanel1Layout = new javax.swing.GroupLayout(jXPanel1);
        jXPanel1.setLayout(jXPanel1Layout);
        jXPanel1Layout.setHorizontalGroup(
            jXPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jXPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jXPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jXPanel1Layout.createSequentialGroup()
                        .addComponent(day1Icon, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jXPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(day1H, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(day1L, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(day1Label, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jXPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jXPanel1Layout.createSequentialGroup()
                        .addComponent(day2Icon)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jXPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(day2H, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(day2L, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(day2Label, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jXPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jXPanel1Layout.createSequentialGroup()
                        .addComponent(day3Icon)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jXPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(day3L, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(day3H, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(day3Label, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jXPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(day4Label, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jXPanel1Layout.createSequentialGroup()
                        .addComponent(day4Icon)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jXPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(day4L, javax.swing.GroupLayout.DEFAULT_SIZE, 52, Short.MAX_VALUE)
                            .addComponent(day4H, javax.swing.GroupLayout.DEFAULT_SIZE, 52, Short.MAX_VALUE))))
                .addContainerGap())
        );
        jXPanel1Layout.setVerticalGroup(
            jXPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jXPanel1Layout.createSequentialGroup()
                .addContainerGap(12, Short.MAX_VALUE)
                .addGroup(jXPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(day4Label, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jXPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(day1Label, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(day2Label, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(day3Label, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGroup(jXPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jXPanel1Layout.createSequentialGroup()
                        .addGap(12, 12, 12)
                        .addGroup(jXPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jXPanel1Layout.createSequentialGroup()
                                .addComponent(day1H, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(day1L, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jXPanel1Layout.createSequentialGroup()
                                .addComponent(day2H, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(day2L, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jXPanel1Layout.createSequentialGroup()
                                .addComponent(day3H, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(day3L, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jXPanel1Layout.createSequentialGroup()
                                .addComponent(day4H, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(day4L, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addContainerGap(22, Short.MAX_VALUE))
                    .addComponent(day1Icon, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(day2Icon, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(day3Icon, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(day4Icon, javax.swing.GroupLayout.Alignment.TRAILING)))
        );

        aboutButton.setText("About");
        aboutButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                aboutButtonActionPerformed(evt);
            }
        });

        setLocationButton.setText("Set location");
        setLocationButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                setLocationButtonActionPerformed(evt);
            }
        });

        refreshButton.setText("Refresh");
        refreshButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                refreshButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jXPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(setLocationButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(refreshButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(aboutButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(exitButton, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(20, 20, 20))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(panel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(26, 26, 26)
                .addComponent(jXPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE, false)
                    .addComponent(aboutButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(setLocationButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(exitButton)
                    .addComponent(refreshButton))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

	private void formMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMousePressed
		locationPoint = this.getLocation();
		startX = (int) evt.getXOnScreen();
		startY = (int) evt.getYOnScreen();
	}//GEN-LAST:event_formMousePressed

	private void formMouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMouseDragged

		int newX = (evt.getXOnScreen() - startX);
		int newY = evt.getYOnScreen() - startY;


		this.setLocation((int) locationPoint.getX() + newX, (int) locationPoint.getY() + newY);
	}//GEN-LAST:event_formMouseDragged

	private void formComponentResized(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentResized
		setShape(new Rectangle(5, 30, getWidth() - 10, getHeight() - 35));
	}//GEN-LAST:event_formComponentResized

	private void exitButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exitButtonActionPerformed
		System.exit(0);
	}//GEN-LAST:event_exitButtonActionPerformed

	private void aboutButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_aboutButtonActionPerformed
		openAboutBox();
	}//GEN-LAST:event_aboutButtonActionPerformed

	private void setLocationButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_setLocationButtonActionPerformed
		openSetLocationBox();
	}//GEN-LAST:event_setLocationButtonActionPerformed

	private void refreshButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_refreshButtonActionPerformed
		weatherFetch();
	}//GEN-LAST:event_refreshButtonActionPerformed

	private void formWindowIconified(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowIconified
		setVisible(false);
	}//GEN-LAST:event_formWindowIconified
	public void openSetLocationBox() {

		setLocationBox = new WeatherShowSetLocation(this);
		setLocationBox.setLocationRelativeTo(rootPane);
		setLocationBox.setVisible(true);
		if (location != null) {
			weatherFetch();
		} else if (location == null && !weather.weatherFileExists()) {
			showError("You must select location");
		}

	}

	public void openAboutBox() {
		if (aboutBox == null) {
			aboutBox = new WeatherShowAbout(this);
			aboutBox.setLocationRelativeTo(rootPane);
			aboutBox.addWindowListener(new WindowAdapter() {

				@Override
				public void windowClosing(WindowEvent e) {
					aboutBox.dispose();

				}
			});
			aboutBox.setVisible(true);


		}
		aboutBox.dispose();
	}

	public void refresh() {
		weatherFetch();
	}

	public void hideShowToggle() {
		setVisible(!isVisible());
setShape(new Rectangle(5, 30, getWidth() - 10, getHeight() - 35));
	}

	public void setWeatherLocation(String location) {
		this.location = location;
	}

	public String getWeatherLocation() {
		return this.location;
	}

	public void setCc(CurrentConditions cc) {
		this.cc = cc;
	}

	public void setCity(City city) {
		this.city = city;
	}

	public void setForecast(Forecast[] forecast) {
		this.forecast = forecast;
	}

	/**
	 * @param args the command line arguments
	 */
	public static void main(String args[]) {


		JFrame.setDefaultLookAndFeelDecorated(true);

		/*
		 * Create and display the form
		 */
		JFrame.setDefaultLookAndFeelDecorated(true);
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				new WeatherShowView().setVisible(true);
			}
		});
	}
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton aboutButton;
    private javax.swing.JLabel cityLabel;
    private javax.swing.JLabel cloudCoverLabel;
    private javax.swing.JLabel day1H;
    private javax.swing.JLabel day1Icon;
    private javax.swing.JLabel day1L;
    private javax.swing.JLabel day1Label;
    private javax.swing.JLabel day2H;
    private javax.swing.JLabel day2Icon;
    private javax.swing.JLabel day2L;
    private javax.swing.JLabel day2Label;
    private javax.swing.JLabel day3H;
    private javax.swing.JLabel day3Icon;
    private javax.swing.JLabel day3L;
    private javax.swing.JLabel day3Label;
    private javax.swing.JLabel day4H;
    private javax.swing.JLabel day4Icon;
    private javax.swing.JLabel day4L;
    private javax.swing.JLabel day4Label;
    private javax.swing.JButton exitButton;
    private javax.swing.JLabel humidityLabel;
    private org.jdesktop.swingx.JXPanel jXPanel1;
    private javax.swing.JLabel lastObservedLabel;
    private org.jdesktop.swingx.JXPanel panel;
    private javax.swing.JLabel precipLabel;
    private javax.swing.JLabel pressureLabel;
    private javax.swing.JButton refreshButton;
    private javax.swing.JButton setLocationButton;
    private javax.swing.JLabel tempLabel;
    private javax.swing.JLabel visibilityLabel;
    private javax.swing.JLabel weatherDescLabel;
    private javax.swing.JLabel weatherIcon;
    private javax.swing.JLabel windLabel;
    // End of variables declaration//GEN-END:variables
}

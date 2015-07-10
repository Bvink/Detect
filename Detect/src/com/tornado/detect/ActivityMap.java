package com.tornado.detect;

import java.util.ArrayList;
import java.util.List;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.view.KeyEvent;
import android.view.WindowManager;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.tornado.core.api.utils.Hiscore;
import com.tornado.core.api.utils.Spherical;
import com.tornado.core.api.utils.Timer;
import com.tornado.detect.constants.DetectConstants;
import com.tornado.detect.settings.DetectSettings;

public class ActivityMap extends ActivityBaseMap {

	private Marker yourLocation;
	private double speed = 0.27;
	private double increment = 0;
	private boolean enemy = false;
	private boolean once = false;
	private double loc[] = new double[2];
	private final Timer timer = new Timer(0);
	private LatLng spawn;
	private int count = 0;
	private List<Marker> spine = new ArrayList<Marker>();
	private boolean alive = true;
	private boolean safe = true;

	@Override
	public void onCreate(Bundle saved) {
		super.onCreate(saved);
		startMap();
	}

	@Override
	protected int getLayoutId() {
		return R.layout.distance;
	}

	@Override
	protected void startMap() {
		int status = GooglePlayServicesUtil
				.isGooglePlayServicesAvailable(getBaseContext());
		if (status != ConnectionResult.SUCCESS) {

			Dialog dialog = GooglePlayServicesUtil.getErrorDialog(status, this,
					DetectConstants.REQUESTCODE);
			dialog.show();

		} else if (!once) {
			getWindow()
					.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
			setDifficulty();
			loc = getlocation();
			setYourLocation(getMap()
					.addMarker(
							new MarkerOptions()
									.position(new LatLng(loc[0], loc[1]))
									.title("Your Position")
									.icon(BitmapDescriptorFactory
											.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
									.draggable(DetectSettings.getTesting())));

			LocationManager locationManager = (LocationManager) this
					.getSystemService(Context.LOCATION_SERVICE);

			LocationListener locationListener = new LocationListener() {
				@Override
				public void onLocationChanged(Location location) {
					loc = getlocation();
					if (!DetectSettings.getTesting()) {
						setYourPosition(new LatLng(loc[0], loc[1]));
					}
				}

				@Override
				public void onStatusChanged(String provider, int status,
						Bundle extras) {
				}

				@Override
				public void onProviderEnabled(String provider) {
				}

				@Override
				public void onProviderDisabled(String provider) {
				}
			};

			locationManager.requestLocationUpdates(
					LocationManager.GPS_PROVIDER, 0, 0, locationListener);
			getMap().moveCamera(
					CameraUpdateFactory.newLatLngZoom(
							new LatLng(loc[0], loc[1]),
							DetectConstants.ZOOMLEVEL));
			getMap().setInfoWindowAdapter(new InfoWinAdapt(getLayoutInflater()));
			if (!enemy) {
				setSpawn(Spherical.getOffset(getYourLocation().getPosition(),
						DetectConstants.SPAWNDISTANCE,
						Spherical.getRandomHeading()));
				animateMarker();
				enemy = !enemy;
			}
			once = !once;
		}
	}

	private void setDifficulty() {
		switch (DetectSettings.getDifficulty()) {
		case 0:
			setIncrement(DetectConstants.EASYLEVEL);
			break;
		case 1:
			setIncrement(DetectConstants.NORMALLEVEL);
			break;
		case 2:
			setIncrement(DetectConstants.HARDLEVEL);
			break;
		default:
			setIncrement(DetectConstants.DEFAULTLEVEL);
		}
	}

	private double[] getlocation() {
		LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		List<String> providers = lm.getProviders(true);

		Location l = null;
		for (int i = 0; i < providers.size(); i++) {
			l = lm.getLastKnownLocation(providers.get(i));
			if (l != null)
				break;
		}
		double[] gps = new double[2];

		if (l != null) {
			gps[0] = l.getLatitude();
			gps[1] = l.getLongitude();
		}
		return gps;
	}

	private void animateMarker() {
		final LatLng begin = getSpawn();
		final Marker marker = getMap().addMarker(
				new MarkerOptions().position(begin));
		final Handler handler = new Handler();
		handler.post(new Runnable() {
			@Override
			public void run() {
				if (getAlive()) {
					LatLng position = getYourLocation().getPosition();
					double sp = getSpeed();

					LatLng destination = Spherical.getOffset(getSpawn(), sp,
							Spherical.getHeading(getSpawn(), position));
					if (DetectSettings.getWorld()) {
						LatLng step = Spherical.getOffset(destination,
								DetectConstants.ORBIT,
								Spherical.getHeading(getSpawn(), position) + 90);
						destination = step;
						if (getCount() % DetectConstants.SPINE_SPAWN_TIME == 0) {
							Marker s = getMap()
									.addMarker(
											new MarkerOptions()
													.position(getSpawn())
													.icon(BitmapDescriptorFactory
															.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));
							spine.add(s);
						}
					}
					marker.setPosition(destination);
					setSpawn(destination);
					if (Spherical.getLatLngDistance(destination,
							getYourLocation().getPosition()) > 10) {
						setCount(getCount() + 1);
						if (getCount() % DetectConstants.SPEED_INCREMENT_TIME == 0) {
							setSpeed(sp + getIncrement());
						}
						for (int i = 0; i < spine.size(); i++) {
							if (Spherical.getLatLngDistance(spine.get(i)
									.getPosition(), getYourLocation()
									.getPosition()) < 10) {
								marker.remove();
								for (Marker s : spine) {
									s.remove();
								}
								spine.clear();
								end();
								setAlive(false);
							}

						}
						handler.postDelayed(this, DetectConstants.REFRESHRATE);
					} else {
						marker.remove();
						for (Marker s : spine) {
							s.remove();
						}
						end();
						setAlive(false);
					}
					if (safe
							&& Spherical.getLatLngDistance(destination,
									getYourLocation().getPosition()) < 250) {
						safe = !safe;
						sendNotification("Warning!",
								"The enemy is approaching.", false);
						buzz(DetectConstants.WARNINGSOUNDPATTERN);
					}
					if (!safe
							&& Spherical.getLatLngDistance(destination,
									getYourLocation().getPosition()) > 350) {
						safe = !safe;
					}
				}
			}
		});
	}

	private void end() {
		beep();
		buzz(DetectConstants.SOUNDPATTERN);
		gameOver();
	}

	@TargetApi(Build.VERSION_CODES.CUPCAKE)
	private void beep() {
		ToneGenerator tg = new ToneGenerator(AudioManager.STREAM_NOTIFICATION,
				100);
		tg.startTone(ToneGenerator.TONE_PROP_BEEP);
	}

	private void buzz(long[] pattern) {
		Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
		if (vibrator.hasVibrator()) {
			vibrator.vibrate(pattern, -1);
		}
	}

	private void gameOver() {
		sendNotification("Game Over", "Game Over" + timer.toElapsedString(),
				true);
		Context context = getBaseContext();
		Hiscore.setHiScore(safeLongToInt(timer.getElapsed()), context);
	}

	@SuppressWarnings("deprecation")
	private void sendNotification(String title, String message, boolean intent) {
		NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		Notification mNotification = new Notification(R.drawable.logo, title,
				System.currentTimeMillis());
		PendingIntent pendingIntent = null;
		if (intent) {
			Intent notificationIntent = new Intent(this, MainActivity.class);
			pendingIntent = PendingIntent.getActivity(this, 0,
					notificationIntent, 0);
		}
		mNotification
				.setLatestEventInfo(this, "Detect", message, pendingIntent);
		notificationManager.notify(DetectConstants.NOTIFICATIONCODE,
				mNotification);
	}

	public static int safeLongToInt(long l) {
		if (l < Integer.MIN_VALUE || l > Integer.MAX_VALUE) {
			throw new IllegalArgumentException(l
					+ " cannot be cast to int without changing its value.");
		}
		return (int) l;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			setAlive(false);
			getMap().clear();
			finish();
		}
		return super.onKeyDown(keyCode, event);
	}

	private void setYourLocation(Marker m) {
		this.yourLocation = m;
	}

	private Marker getYourLocation() {
		return yourLocation;
	}

	private void setYourPosition(LatLng l) {
		getYourLocation().setPosition(l);
	}

	private void setSpeed(double d) {
		this.speed = d;
	}

	private double getSpeed() {
		return speed;
	}

	private void setIncrement(double d) {
		this.increment = d;
	}

	private double getIncrement() {
		return increment;
	}

	private void setSpawn(LatLng l) {
		this.spawn = l;
	}

	private LatLng getSpawn() {
		return spawn;
	}

	private void setCount(int i) {
		this.count = i;
	}

	private int getCount() {
		return count;
	}

	private void setAlive(boolean b) {
		this.alive = b;
	}

	private boolean getAlive() {
		return alive;
	}

}

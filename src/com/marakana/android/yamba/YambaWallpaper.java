package com.marakana.android.yamba;

import java.util.List;

import com.marakana.android.yamba.clientlib.YambaClient;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.service.wallpaper.WallpaperService;
import android.view.MotionEvent;
import android.view.SurfaceHolder;

public class YambaWallpaper extends WallpaperService {

    @Override
    public Engine onCreateEngine() {
        return new YambaWallpaperEngine(((YambaApplication) getApplication()).getYambaClient());
    }

    private class YambaWallpaperEngine extends Engine implements Runnable {

	private Handler handler = new Handler();
	private ContentThread contentThread = new ContentThread(); 
	private YambaClient yambaclient;	

	private Paint paint;

	private String[] content = new String[20];
	private TextPoint[] textPoints = new TextPoint[20];
	private int current = -1;
	private boolean running = true;
	private float offset = 0;

    public YambaWallpaperEngine(YambaClient client) { 
    	yambaclient = client;
	    paint = new Paint();
	    paint.setColor(0xffffffff);
        paint.setAntiAlias(true);
        paint.setStrokeWidth(1);
        paint.setStrokeCap(Paint.Cap.SQUARE);
        paint.setStyle(Paint.Style.FILL);
	    paint.setTextSize(40);
	}

        @Override
        public void onCreate(SurfaceHolder surfaceHolder) {
            super.onCreate(surfaceHolder);
	    running = true;
	    contentThread.start();

		// enable touch events
            setTouchEventsEnabled(true);
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
            handler.removeCallbacks(this);

	    running = false;

	    synchronized(contentThread) {
		contentThread.interrupt();
	    }
        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            if (visible) {
                drawFrame();
            } else {
                handler.removeCallbacks(this);
            }
        }

        @Override
        public void onSurfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            super.onSurfaceChanged(holder, format, width, height);
            drawFrame();
        }

        @Override
        public void onSurfaceCreated(SurfaceHolder holder) {
            super.onSurfaceCreated(holder);
        }

        @Override
        public void onSurfaceDestroyed(SurfaceHolder holder) {
            super.onSurfaceDestroyed(holder);
            handler.removeCallbacks(this);
        }

        @Override
        public void onOffsetsChanged(float xOffset, float yOffset,
                float xStep, float yStep, int xPixels, int yPixels) {
	    offset = xPixels;

            drawFrame();
        }

        @Override
        public void onTouchEvent(MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
		current++;

		if(current >= textPoints.length) {
			current = 0;
		}

		String text = content[current];
		if(text != null) {
			textPoints[current] = new TextPoint(text, event.getX() - offset, event.getY());
		}
            } 
            super.onTouchEvent(event);
        }

	@Override
        public void run() {
		drawFrame();
	}

	private void drawFrame() {
            final SurfaceHolder holder = getSurfaceHolder();

            Canvas c = null;
            try {
                c = holder.lockCanvas();
                if (c != null) {
                    // draw text
                    drawText(c);
                }
            } catch (Exception e) {
		e.printStackTrace();
	    } finally {
                if (c != null) {
			holder.unlockCanvasAndPost(c);
		}
            }

            // Reschedule the next redraw
            handler.removeCallbacks(this);

            if (isVisible()) {
                handler.postDelayed(this, 40); // 40 ms = 25 frames per second
            }
    }

	private boolean getContent() {
		List<YambaClient.Status> timeline = null;
		
		try {
			timeline = yambaclient.getTimeline(20);
			
			int i = -1;
			content = new String[20];
			if(timeline != null) {
				for(YambaClient.Status status: timeline) {
					i++;
					content[i] = status.getMessage();
				}
			}
		} catch (Exception e) {}
		return timeline != null && !timeline.isEmpty();
	}

	private void drawText(Canvas c) {
		c.drawColor(Color.BLACK);

		for(TextPoint textpoint: textPoints) {
			if(textpoint != null) {
				c.drawText(textpoint.text, textpoint.x + offset, textpoint.y, paint);
			}
		}

	}

	private class TextPoint {
		public String text;
		public float x;
		public float y;

		public TextPoint(String t, float xp, float yp) {
			text = t;
			x = xp;
			y = yp;
		}
	}

	private class ContentThread extends Thread {
		public void run() {
			while(running) {
				try {
					boolean hascontent = getContent();
					if(hascontent) {
						Thread.sleep(60000);  // 1 min
					} else {
						Thread.sleep(2000);  // 2 s 
					}
				} catch (InterruptedException ie) {
					return;
				} catch (Exception e) {}
			}
		}
	}
    }
}

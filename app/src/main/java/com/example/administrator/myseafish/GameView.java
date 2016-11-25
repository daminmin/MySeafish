package com.example.administrator.myseafish;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameView extends SurfaceView implements
        SurfaceHolder.Callback, Runnable, android.view.View.OnTouchListener {

    private Bitmap background;
    private Bitmap yuziji;
    private Bitmap dayu;
    private Bitmap xiaoyu;
    private Bitmap erjihuancun;
    private int display_w;
    private int display_h;
    private ArrayList<GameImage> gameImages = new ArrayList();

    public GameView(Context context) {
        super(context);
        getHolder().addCallback(this);
        this.setOnTouchListener(this);//事件注册
    }

    private void init() {
        //加载图片
        background = BitmapFactory.decodeResource(getResources(), R.drawable.background);
        yuziji = BitmapFactory.decodeResource(getResources(), R.drawable.yuziji);
        dayu = BitmapFactory.decodeResource(getResources(), R.drawable.yudayu);
        xiaoyu = BitmapFactory.decodeResource(getResources(),R.drawable.xiaoyu);
        erjihuancun = Bitmap.createBitmap(display_w, display_h, Bitmap.Config.ARGB_8888);

        gameImages.add(new BackgroundImage(background));
        gameImages.add(new ZijiImage(yuziji));
        gameImages.add(new DayuImage(dayu));
        gameImages.add(new XiaoyuImage(xiaoyu));
    }

    private interface GameImage {
        public Bitmap getBitmap();

        public int getX();

        public int getY();
    }

    private class DayuImage implements GameImage {

        private Bitmap dayu = null;
        private List<Bitmap> bitmaps = new ArrayList<Bitmap>();
        private int x;
        private int y;

        public DayuImage(Bitmap dayu) {
            this.dayu = dayu;
            bitmaps.add(Bitmap.createBitmap(dayu, 0, 0, dayu.getWidth(), dayu.getHeight()));

            y = -dayu.getHeight();
            Random ran = new Random();
            x = ran.nextInt(display_w - dayu.getWidth());//以图片左上角为顶点，让大鱼在屏幕内
        }

        private int index = 0;

        @Override
        public Bitmap getBitmap() {
            Bitmap bitmap = bitmaps.get(index);
            y += 2;
            if (y > display_h) {
                gameImages.remove(this);
            }
            return bitmap;
        }

        @Override
        public int getX() {
            return x;
        }

        @Override
        public int getY() {
            return y;
        }
    }

    private class XiaoyuImage implements GameImage {

        private Bitmap xiaoyu = null;
        private List<Bitmap> bitmaps = new ArrayList<Bitmap>();
        private int x;
        private int y;

        public XiaoyuImage(Bitmap xiaoyu) {
            this.xiaoyu = xiaoyu;
            bitmaps.add(Bitmap.createBitmap(xiaoyu, 0, 0, xiaoyu.getWidth(), xiaoyu.getHeight()));

            y = -xiaoyu.getHeight();
            Random ran = new Random();
            x = ran.nextInt(display_w - xiaoyu.getWidth());//以图片左上角为顶点，让鱼在屏幕内
        }

        private int index = 0;

        @Override
        public Bitmap getBitmap() {
            Bitmap bitmap = bitmaps.get(index);
            y += 4;
            if (y > display_h) {
                gameImages.remove(this);
            }
            return bitmap;
        }

        @Override
        public int getX() {
            return x;
        }

        @Override
        public int getY() {
            return y;
        }
    }

    private class ZijiImage implements GameImage {

        private Bitmap yuziji;
        private int x;
        private int y;
        private int width;
        private int height;

        private List<Bitmap> bitmaps = new ArrayList<Bitmap>();

        private ZijiImage(Bitmap yuziji) {
            this.yuziji = yuziji;
            bitmaps.add(Bitmap.createBitmap(yuziji, 0, 0, yuziji.getWidth(), yuziji.getHeight()));

            //得到自己控制的小鱼的图片宽高
            width = yuziji.getWidth();
            height = yuziji.getHeight();

            x = (display_w - yuziji.getWidth()) / 2;
            y = (display_h - yuziji.getHeight() - 30);
        }

        private int index = 0;

        @Override
        public Bitmap getBitmap() {
            Bitmap bitmap = bitmaps.get(index);
            return bitmap;
        }

        @Override
        public int getX() {
            return x;
        }

        @Override
        public int getY() {
            return y;
        }

        public void setX(int x) {
            this.x = x;
        }

        public void setY(int y) {
            this.y = y;
        }

        public int getWidth() {
            return width;
        }

        public int getHeight() {
            return height;
        }
    }

    private class BackgroundImage implements GameImage {
        private Bitmap background;

        private BackgroundImage(Bitmap background) {
            this.background = background;
            newBitmap = Bitmap.createBitmap(display_w, display_h, Bitmap.Config.ARGB_8888);
        }

        private Bitmap newBitmap = null;
        private int high = 0;

        public Bitmap getBitmap() {
            Paint p = new Paint();
            Canvas canvas = new Canvas(newBitmap);
            canvas.drawBitmap(background,
                    new Rect(0, 0, background.getWidth(), background.getHeight()),
                    new Rect(0, high, display_w, display_h + high),
                    p);//第一张图
            canvas.drawBitmap(background,
                    new Rect(0, 0, background.getWidth(), background.getHeight()),
                    new Rect(0, -display_h + high, display_w, high),//注意坐标
                    p);//第二张图以及以后的图

            high++;
            if (high == display_h) {
                high = 0;
            }//判断第二张图走完了没
            return newBitmap;
        }

        @Override
        public int getX() {
            return 0;
        }

        @Override
        public int getY() {
            return 0;
        }
    }

    private boolean state = false;
    private SurfaceHolder holder;
    private long score=0;

    @Override
    //绘画方法
    public void run() {
        Paint p1 = new Paint();
        Paint p2 = new Paint();
        p2.setColor(Color.BLACK);
        p2.setTextSize(50);
        p2.setDither(true);
        p2.setAntiAlias(true);
        int dayu_num = 0;
        int xiaoyu_num = 0;
        try {
            while (state) {
                Canvas newCannvas = new Canvas(erjihuancun);
                for (GameImage image : (List<GameImage>) gameImages.clone()) {
                    newCannvas.drawBitmap(image.getBitmap(), image.getX(), image.getY(), p1);
                }

                newCannvas.drawText("分数"+score,12,display_h-10,p2);

                if (dayu_num == 200) {
                    dayu_num = 0;
                    gameImages.add(new DayuImage(dayu));
                }
                dayu_num++;

                if (xiaoyu_num == 100) {
                    xiaoyu_num = 0;
                    gameImages.add(new DayuImage(xiaoyu));
                }
                xiaoyu_num++;

                Canvas canvas = holder.lockCanvas();
                canvas.drawBitmap(erjihuancun, 0, 0, p1);
                holder.unlockCanvasAndPost(canvas);
                Thread.sleep(10);
            }
        } catch (Exception e) {

        }

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        //屏幕宽高
        display_w = width;
        display_h = height;
        init();
        this.holder = holder;
        state = true;
        new Thread(this).start();

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        state = false;
    }

    ZijiImage selectziji;

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            for (GameImage game : gameImages) {
                if (game instanceof ZijiImage) {
                    ZijiImage ziji = (ZijiImage) game;
                    if (ziji.getX() < event.getX()
                            && ziji.getY() < event.getY()
                            && ziji.getX() + ziji.getWidth() > event.getX()
                            && ziji.getY() + ziji.getHeight() > event.getY()) {
                        selectziji = ziji;
                    } else {
                        selectziji = null;
                    }
                    break;
                }
            }
        } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
            if (selectziji != null) {
                selectziji.setX((int) event.getX() - selectziji.getWidth() / 2);
                selectziji.setY((int) event.getY() - selectziji.getHeight() / 2);
            }
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            selectziji = null;
        }
        return true;
    }

}

package uz.gxteam.camera.other;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.hardware.camera2.params.Face;
import android.util.AttributeSet;
import android.view.View;

import com.google.android.gms.vision.CameraSource;

import java.util.HashSet;
import java.util.Set;


public class GraphicOverlay extends View {
    private final Object lock = new Object();
    private int previewWidth;
    private float widthScaleFactor = 1.0f;
    private int previewHeight;
    private float heightScaleFactor = 1.0f;
    private int facing = CameraSource.CAMERA_FACING_BACK;
    private Set<Graphic> graphics = new HashSet<>();


    /**
     * Base class for a custom graphics object to be rendered within the graphic overlay. Subclass
     * this and implement the {@link Graphic#draw(Canvas)} method to define the graphics element. Add
     * instances to the overlay using {@link GraphicOverlay#add(Graphic)}.
     */
    public abstract static class Graphic {


        private Bitmap bitmap;
        private Bitmap sunglasses;
        private static final float FACE_POSITION_RADIUS = 10.0f;
        private static final float ID_TEXT_SIZE = 40.0f;
        private static final float ID_Y_OFFSET = 50.0f;
        private static final float ID_X_OFFSET = -50.0f;
        private static final float BOX_STROKE_WIDTH = 5.0f;

        private static final int COLOR_CHOICES[] = {
                Color.BLUE,
                Color.CYAN,
                Color.GREEN,
                Color.MAGENTA,
                Color.RED,
                Color.WHITE,
                Color.YELLOW
        };
        private static int mCurrentColorIndex = 0;

        public Paint mFacePositionPaint;
        public Paint mIdPaint;
        public Paint mBoxPaint;

        public volatile Face mFace;
        public int mFaceId;
        public float mFaceHappiness;

        private GraphicOverlay overlay;

        public Graphic(GraphicOverlay overlay) {
            this.overlay = overlay;

//            mCurrentColorIndex = (mCurrentColorIndex + 1) % COLOR_CHOICES.length;
//            final int selectedColor = COLOR_CHOICES[mCurrentColorIndex];
//
//
//            mFacePositionPaint = new Paint();
//            mFacePositionPaint.setColor(selectedColor);
//
//            mIdPaint = new Paint();
//            mIdPaint.setColor(selectedColor);
//            mIdPaint.setTextSize(ID_TEXT_SIZE);
//
//            mBoxPaint = new Paint();
//            mBoxPaint.setColor(selectedColor);
//            mBoxPaint.setStyle(Paint.Style.STROKE);
//            mBoxPaint.setStrokeWidth(BOX_STROKE_WIDTH);
//
//            bitmap = BitmapFactory.decodeResource(overlay.getContext().getResources(), R.drawable.ic_launcher_background);
//            sunglasses = bitmap;

        }




        /**
         * Draw the graphic on the supplied canvas. Drawing should use the following methods to convert
         * to view coordinates for the graphics that are drawn:
         *
         * <ol>
         *   <li>{@link Graphic#scaleX(float)} and {@link Graphic#scaleY(float)} adjust the size of the
         *       supplied value from the preview scale to the view scale.
         *   <li>{@link Graphic#translateX(float)} and {@link Graphic#translateY(float)} adjust the
         *       coordinate from the preview's coordinate system to the view coordinate system.
         * </ol>
         *
         * @param canvas drawing canvas
         */
        public abstract void draw(Canvas canvas);

        /**
         * Adjusts a horizontal value of the supplied value from the preview scale to the view scale.
         */
        public float scaleX(float horizontal) {
            return horizontal * overlay.widthScaleFactor;
        }

        /** Adjusts a vertical value of the supplied value from the preview scale to the view scale. */
        public float scaleY(float vertical) {
            return vertical * overlay.heightScaleFactor;
        }

        /** Returns the application context of the app. */
        public Context getApplicationContext() {
            return overlay.getContext().getApplicationContext();
        }

        /**
         * Adjusts the x coordinate from the preview's coordinate system to the view coordinate system.
         */
        public float translateX(float x) {
            if (overlay.facing == CameraSource.CAMERA_FACING_FRONT) {
                return overlay.getWidth() - scaleX(x);
            } else {
                return scaleX(x);
            }
        }

        /**
         * Adjusts the y coordinate from the preview's coordinate system to the view coordinate system.
         */
        public float translateY(float y) {
            return scaleY(y);
        }

        public void postInvalidate() {
            overlay.postInvalidate();
        }
    }

    public GraphicOverlay(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /** Removes all graphics from the overlay. */
    public void clear() {
        synchronized (lock) {
            graphics.clear();
        }
        postInvalidate();
    }

    /** Adds a graphic to the overlay. */
    public void add(Graphic graphic) {
        synchronized (lock) {
            graphics.add(graphic);
        }
        postInvalidate();
    }

    /** Removes a graphic from the overlay. */
    public void remove(Graphic graphic) {
        synchronized (lock) {
            graphics.remove(graphic);
        }
        postInvalidate();
    }

    /**
     * Sets the camera attributes for size and facing direction, which informs how to transform image
     * coordinates later.
     */
    public void setCameraInfo(int previewWidth, int previewHeight, int facing) {
        synchronized (lock) {
            this.previewWidth = previewWidth;
            this.previewHeight = previewHeight;
            this.facing = facing;
        }
        postInvalidate();
    }

    /** Draws the overlay with its associated graphic objects. */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        synchronized (lock) {
            if ((previewWidth != 0) && (previewHeight != 0)) {
                widthScaleFactor = (float) canvas.getWidth() / (float) previewWidth;
                heightScaleFactor = (float) canvas.getHeight() / (float) previewHeight;
            }

            for (Graphic graphic : graphics) {
                graphic.draw(canvas);
            }
        }
    }


}

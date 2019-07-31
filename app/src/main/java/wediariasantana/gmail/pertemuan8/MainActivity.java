package wediariasantana.gmail.pertemuan8;

import android.graphics.Matrix;
import android.graphics.PointF;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity implements View.OnTouchListener {

    private Matrix matriks = new Matrix();
    private Matrix simpanMatriks = new Matrix();
    private static final int NONE = 0;
    private static final int DRAG = 1;
    private static final int ZOOM = 2;
    private int mode = NONE;
    private PointF mulai = new PointF();
    private PointF tengah = new PointF();
    private float jarakAwal = 1f;
    private float jarak = 0f;
    private float rotasiAwal = 0f;
    private float[] evenAkhir = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //TODO 1 : mengambil image view dari layout kemudian mengeset touch listener pada image view tersebut
        ImageView tampilGambar = findViewById(R.id.imageView);
        tampilGambar.setOnTouchListener(this);
    }

    //TODO 2 : membuat method pada saat terjadi sentuhan
    public boolean onTouch(View view, MotionEvent event) {
        //TODO 2.1 : membuat image view dengan nama gambar
        ImageView gambar = (ImageView) view;

        //TODO 2.2 : mengecek jika terjadi action
        switch (event.getAction() & MotionEvent.ACTION_MASK) {

            //TODO 2.2.1 : jika action down maka nilai mode adalah drag
            case MotionEvent.ACTION_DOWN:
                simpanMatriks.set(matriks);
                mulai.set(event.getX(), event.getY());
                mode = DRAG;
                evenAkhir = null;
                break;

            //TODO 2.2.2 : jika action pointer down dan jarak awal > 10f maka mode adalah zoom
            case MotionEvent.ACTION_POINTER_DOWN:
                jarakAwal = jarak2(event);
                if (jarakAwal > 10f) {
                    simpanMatriks.set(matriks);
                    nilaiTengah(tengah, event);
                    mode = ZOOM;
                }
                evenAkhir = new float[4];
                evenAkhir[0] = event.getX(0);
                evenAkhir[1] = event.getX(1);
                evenAkhir[2] = event.getY(0);
                evenAkhir[3] = event.getY(1);
                jarak = rotasi(event);
                break;

            //TODO 2.2.3 : jika action up maka tidak terjadi apa-apa
            case MotionEvent.ACTION_UP:

                //TODO 2.2.4 : jika action pointer up maka mode adalah none
            case MotionEvent.ACTION_POINTER_UP:
                mode = NONE;
                evenAkhir = null;
                break;

            //TODO 2.2.5 : jika action move
            case MotionEvent.ACTION_MOVE:
                //TODO 2.2.5.1 : jika modenya drag maka akan menggeser gambar
                if (mode == DRAG) {
                    matriks.set(simpanMatriks);
                    float dx = event.getX() - mulai.x;
                    float dy = event.getY() - mulai.y;
                    matriks.postTranslate(dx, dy);
                }
                //TODO 2.2.5.2 : jika modenya zoom dan jarak > 10f maka akan mengezoom gambar
                else if (mode == ZOOM) {
                    float jarakBaru = jarak2(event);
                    if (jarakBaru > 10f) {
                        matriks.set(simpanMatriks);
                        float scale = (jarakBaru / jarakAwal);
                        matriks.postScale(scale, scale, tengah.x, tengah.y);
                    }
                    //TODO 2.2.5.3 : jika jumlah pointer 3 maka akan merotasi gambar
                    if (evenAkhir != null && event.getPointerCount() == 3) {
                        rotasiAwal = rotasi(event);
                        float r = rotasiAwal - jarak;
                        float[] values = new float[9];
                        matriks.getValues(values);
                        float tx = values[2];
                        float ty = values[5];
                        float sx = values[0];
                        float xc = (gambar.getWidth() / 2) * sx;
                        float yc = (gambar.getHeight() / 2) * sx;
                        matriks.postRotate(r, tx + xc, ty + yc);
                    }
                }
                break;
        }
        gambar.setImageMatrix(matriks);
        return true;
    }

    //TODO 3 : method untuk menghitung jarak
    private float jarak2(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }

    //TODO 4 : method untuk menentukan nilai atau titik tengah dari gambar
    private void nilaiTengah(PointF point, MotionEvent event) {
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        point.set(x / 2, y / 2);
    }

    //TODO 5 : method untuk melakukan rotasi pada gambar
    private float rotasi(MotionEvent event) {
        double delta_x = (event.getX(0) - event.getX(1));
        double delta_y = (event.getY(0) - event.getY(1));
        double radians = Math.atan2(delta_y, delta_x);
        return (float) Math.toDegrees(radians);
    }
}

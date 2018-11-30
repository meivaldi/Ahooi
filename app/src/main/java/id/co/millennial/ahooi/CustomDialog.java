package id.co.millennial.ahooi;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import org.w3c.dom.Attr;

/**
 * Created by root on 28/11/18.
 */

public class CustomDialog extends View {

    public CustomDialog(Context context) {
        super(context);

        init(null);
    }

    public CustomDialog(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomDialog(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public CustomDialog(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        init(attrs);
    }

    private void init(@Nullable AttributeSet attr) {

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawColor(Color.DKGRAY);
    }
}

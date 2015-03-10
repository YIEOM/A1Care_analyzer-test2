package isens.hba1c_analyzer.Model;

import android.content.Context;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class Graph extends SurfaceView implements SurfaceHolder.Callback {

	private SurfaceHolder holder;
	
	public Graph(Context context, SurfaceView surfaceView) {
		
		super(context); 
		
		holder = surfaceView.getHolder();
		
		holder.addCallback(this);
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {

	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		
	}
	
	public SurfaceHolder GetHolder() {
		
		return this.holder;
	}
}

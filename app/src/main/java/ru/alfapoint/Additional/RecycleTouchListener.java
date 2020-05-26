package ru.alfapoint.Additional;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

public class RecycleTouchListener implements RecyclerView.OnItemTouchListener{
    private GestureDetector gestureDetector;
    private RecycleListener recycleListener;

    public RecycleTouchListener(Context context, final RecyclerView recyclerView, final RecycleListener recycleListener){
        this.recycleListener = recycleListener;
        gestureDetector = new GestureDetector(context,new GestureDetector.SimpleOnGestureListener(){
            @Override
            public boolean onSingleTapUp(MotionEvent event){
                return true;
            }
            @Override
            public void onLongPress(MotionEvent event){
                View child = recyclerView.findChildViewUnder(event.getX(),event.getY());
                if (child != null && recycleListener != null){
                    recycleListener.onLongClick(child,recyclerView.getChildLayoutPosition(child));
                }
            }
        });
    }

    @Override
    public boolean onInterceptTouchEvent(RecyclerView recyclerView, MotionEvent e) {
        View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
        if (child != null && recycleListener != null && gestureDetector.onTouchEvent(e)) {
            //recycleListener.onClick(child, recyclerView.getChildPosition(child));
            recycleListener.onClick(child, recyclerView.getChildAdapterPosition(child));
        }
        return false;
    }

    @Override
    public void onTouchEvent(RecyclerView rv, MotionEvent e) {

    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

    }


    public interface RecycleListener {
        void onClick (View view,int position);
        void  onLongClick(View view,int position);
    }
}


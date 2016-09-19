//@author Bhavya
package com.assignment.xiaoduo.customized.view.listviewfilter;

import java.util.ArrayList;
import java.util.Locale;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.assignment.xiaoduo.survivalguide.activities.ListViewFilterActivity;
import com.assignment.xiaoduo.survivalguidefit4039assignment.R;


public class PinnedHeaderAdapter extends BaseAdapter implements OnScrollListener, IPinnedHeader ,Filterable
{
	 	private static final int TYPE_ITEM = 0;
        private static final int TYPE_SECTION = 1;
        private static final int TYPE_MAX_COUNT = TYPE_SECTION + 1;
        LayoutInflater li;
        int currentSectionPosition=0,nextSectionPostion=0;
                
        //array list to store section positions
        ArrayList<Integer> mListSectionPos;
        
        //array list to store list view data
        ArrayList<String> mListItems;
     
        Context mContext;
        
      
		public PinnedHeaderAdapter(Context mContext,ArrayList<String> mListItems, ArrayList<Integer> mListSectionPos) 
		{
			
			this.mContext=mContext;
			this.mListItems=mListItems;
			this.mListSectionPos=mListSectionPos;
			
			li=(LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			
		}

		@Override
		public int getCount() {
			return mListItems.size();
		}

		@Override
        public int getViewTypeCount() {
            return TYPE_MAX_COUNT;
        }
		
		 @Override
        public int getItemViewType(int position) {
            return mListSectionPos.contains(position) ? TYPE_SECTION : TYPE_ITEM;
        }
		 
		@Override
		public Object getItem(int position) {
			return mListItems.get(position);
		}

		@Override
		public long getItemId(int position) {
			return mListItems.get(position).hashCode();
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
            
            if (convertView == null) 
            {
                holder = new ViewHolder();
                int type = getItemViewType(position);
                
                switch (type) 
                {
                    case TYPE_ITEM:
                        convertView = li.inflate(R.layout.listview_filter_row_view,null);
                        break;
                    case TYPE_SECTION:
                        convertView = li.inflate(R.layout.listview_filter_section_row_view,null); 	    					
                        break;
                }
                holder.textView=(TextView) convertView.findViewById(R.id.row_title);
                convertView.setTag(holder);
            } 
            else
            {
                holder = (ViewHolder)convertView.getTag();
            }
            	           
            holder.textView.setText(mListItems.get(position).toString());
                 
            return convertView;
		}

		@Override
		public int getPinnedHeaderState(int position) {
			
			// hide pinned header when items count is zero OR position is less than zero OR
			// there is already a header in list view   
			if (getCount() == 0 || position < 0 || mListSectionPos.indexOf(position)!=-1) 
			 {
                return PINNED_HEADER_GONE;
			 }
			 

            // the header should get pushed up if the top item shown
            // is the last item in a section for a particular letter.
			currentSectionPosition = getCurrentSectionPosition(position);
            nextSectionPostion = getNextSectionPosition(currentSectionPosition); 
            if (nextSectionPostion != -1 && position == nextSectionPostion - 1) {
                return PINNED_HEADER_PUSHED_UP;
            }

          
            return PINNED_HEADER_VISIBLE;
		}
		
		public int getCurrentSectionPosition(int position) { 
			String list_char=mListItems.get(position).toString().substring(0, 3).toUpperCase(Locale.ENGLISH);
			return mListItems.indexOf(list_char);
		}
		
		public int getNextSectionPosition(int curr_sectionPosition) { 
			int index=mListSectionPos.indexOf(curr_sectionPosition);
			if((index+1) < mListSectionPos.size())
			{
				return mListSectionPos.get(index+1); 
			}
			return mListSectionPos.get(index);
		}

		@Override
		public void configurePinnedHeader(View v, int position) 
		{
			
			//set text in pinned header
			TextView header = (TextView) v;
			currentSectionPosition = getCurrentSectionPosition(position);
//			Log.e("currentSectionPosition","currentSectionPosition:"+currentSectionPosition);
//			Log.e("mListItems","mListItems"+mListItems.size());
			header.setText(mListItems.get(currentSectionPosition));
			
		}
			

		@Override
		public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
			if (view instanceof PinnedHeaderListView) {
                ((PinnedHeaderListView) view).configureHeaderView(firstVisibleItem);
            }	
		}

		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public Filter getFilter() {
			return ((ListViewFilterActivity)mContext).new ListFilter();
		}
		
		 public static class ViewHolder
	    {
	        public TextView textView;
	    }
	    
}

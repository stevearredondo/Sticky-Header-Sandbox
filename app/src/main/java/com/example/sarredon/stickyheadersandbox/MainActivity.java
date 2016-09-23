package com.example.sarredon.stickyheadersandbox;

import android.content.res.Resources;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    Map<Type, List<String>> mMap;

    RecyclerView mRecyclerView;
    TextView mStickyHeader;

    Adapter mAdapter;

    enum Type {
        FAVORITES ("Favorites"),
        PREVIOUS ("Previous"),
        NEARBY ("Nearby");
        public final String text;
        Type(String text) {
            this.text = text;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initMap();
        mStickyHeader = (TextView) findViewById(R.id.sticky_header);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        mAdapter = new Adapter();
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addOnScrollListener(new StickyScrollListener());
    }

    void initMap() {
        mMap = new EnumMap<>(Type.class);
        mMap.put(Type.FAVORITES, Arrays.asList("fav 1", "fav 2", "fav 3"));
        mMap.put(Type.PREVIOUS, Arrays.asList("prev 1", "prev 2", "prev 3", "prev 4"));
        mMap.put(Type.NEARBY, Arrays.asList("nearby 1", "nearby 2", "nearby 3", "nearby 4", "nearby 5"));
    }

    class Adapter extends RecyclerView.Adapter<ViewHolder> {

        static final int HEADER = 0;
        static final int LIST_ITEM = 1;

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            switch (viewType) {
                case HEADER: return new HeaderVH(parent);
                case LIST_ITEM: return new ListItemVH(parent);
                default: return null;
            }
        }

        @Override
        public void onBindViewHolder(ViewHolder vh, int position) {
            switch (vh.getItemViewType()) {
                case HEADER:
                    vh.populate(getHeaderAt(position).text);
                    break;
                case LIST_ITEM:
                    vh.populate(getListItemAt(position));
                    break;
            }
        }

        @Override
        public int getItemCount() {
            int c = 0;
            for (Type type : mMap.keySet()) {
                if (!mMap.get(type).isEmpty()) {
                    c += mMap.get(type).size() + 1;
                }
            }
            return c;
        }

        @Override
        public int getItemViewType(int position) {
            int index = 0;
            for (Type type : mMap.keySet()) {
                final List<String> listItem = mMap.get(type);
                if (!listItem.isEmpty()) {
                    if (position == index) {
                        return HEADER;
                    }
                    index += 1; // include header
                    if (position < index + listItem.size()) {
                        return LIST_ITEM;
                    }
                    index += listItem.size(); // include items in bucket
                }
            }
            return -1;
        }

        Type getHeaderAt(int position) {
            int index = 0;
            for (Type type : mMap.keySet()) {
                final List<String> listItems = mMap.get(type);
                if (!listItems.isEmpty()) {
                    if (index == position) {
                        return type;
                    }
                    index += listItems.size() + 1; // include items in bucket, plus the header
                }
            }
            return null;
        }

        String getListItemAt(int position) {
            int index = 0;
            for (Type type : mMap.keySet()) {
                final List<String> listItems = mMap.get(type);
                if (!listItems.isEmpty()) {
                    index += 1; // include header
                    if (position < index + listItems.size()) {
                        return listItems.get(position - index);
                    }
                    index += listItems.size();
                }
            }
            return null;
        }
    }


    static abstract class ViewHolder extends RecyclerView.ViewHolder {
        protected TextView listItem;
        protected TextView header;
        public final int height;

        public ViewHolder(View itemView) {
            super(itemView);
            listItem = (TextView) itemView.findViewById(R.id.normal_list_item);
            header = (TextView) itemView.findViewById(R.id.header);
            height = getHeight(itemView.getResources());
        }

        abstract void populate(String text);

        abstract int getHeight(Resources res);
    }

    static class ListItemVH extends ViewHolder {
        public ListItemVH(View itemView) {
            super(itemView);
            header.setVisibility(View.GONE);
        }

        @Override
        void populate(String text) {
            listItem.setText(text);
        }

        @Override
        int getHeight(Resources res) {
            return res.getDimensionPixelOffset(R.dimen.list_item_height);
        }
    }

    static class HeaderVH extends ViewHolder {
        public HeaderVH(View itemView) {
            super(itemView);
            listItem.setVisibility(View.GONE);
        }

        @Override
        void populate(String text) {
            header.setText(text);
        }

        @Override
        int getHeight(Resources res) {
            return res.getDimensionPixelOffset(R.dimen.header_height);
        }
    }

    static class StickyScrollListener extends RecyclerView.OnScrollListener {
        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

        }
    }
}

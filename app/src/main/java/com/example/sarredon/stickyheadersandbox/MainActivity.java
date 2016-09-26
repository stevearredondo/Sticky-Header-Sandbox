package com.example.sarredon.stickyheadersandbox;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = MainActivity.class.getSimpleName();
    Map<Type, List<String>> mMap;

    RecyclerView mRecyclerView;
    TextView mStickyHeader;

    Adapter mAdapter;

    int headerHeight;
    int itemHeight;

    static final String[] HEADERS = new String[] {
            "Header 1",
            "Header 2",
            "Header 3"
    };

    enum Type {TYPE1, TYPE2, TYPE3}

    static String getText(Type type) {
        return HEADERS[type.ordinal()];
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        headerHeight = getResources().getDimensionPixelOffset(R.dimen.header_height);
        itemHeight = getResources().getDimensionPixelOffset(R.dimen.list_item_height);
        mStickyHeader = (TextView) findViewById(R.id.sticky_header);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        initMap();
        mAdapter = new Adapter();
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        final int[] headerYs = computeHeaderYs(headerHeight, itemHeight, new ArrayList<Collection<?>>(mMap.values()));
        mRecyclerView.addOnScrollListener(new StickyScrollListener(headerYs) {
            @Override
            protected void update(int index) {
                Log.d(TAG, "update("+index+")");
                mStickyHeader.setText(HEADERS[index]);
            }
        });
    }

    void initMap() {
        mMap = new EnumMap<>(Type.class);
        mMap.put(Type.TYPE1, Arrays.asList("asdf", "sdfg", "afiowe", "adsdlk4", "asjdfklv", "afklsd;", "jllkj"));
        mMap.put(Type.TYPE2, Arrays.asList("ahsfd", "asf;dj", "asdjfkl", "asdfljkk", "asd;lk", "jakls;j", "lksjdff", "ajksldf;", "dsfal", "jskldf"));
        mMap.put(Type.TYPE3, Arrays.asList("sajdlkf", "jasdklf;", "sdjlkf", "sdlfkjsdf", "sjdlkf", "dsljdff", "sa;dj", "sld;af", "w;dafs", "as;dfkl", "zs;dflk", "wjdklds"));
    }

    class Adapter extends RecyclerView.Adapter<ViewHolder> {

        static final int HEADER = 0;
        static final int LIST_ITEM = 1;

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View vhRoot = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
            switch (viewType) {
                case HEADER: return new HeaderVH(vhRoot);
                case LIST_ITEM: return new ListItemVH(vhRoot);
                default: return null;
            }
        }

        @Override
        public void onBindViewHolder(ViewHolder vh, int position) {
            switch (vh.getItemViewType()) {
                case HEADER:
                    vh.populate(getText(getHeaderAt(position)));
                    break;
                case LIST_ITEM:
                    vh.populate(getListItemAt(position));
                    break;
            }
        }

        @Override
        public int getItemCount() {
            int c = 0;
            for (List<String> list : mMap.values()) {
                c += list.size() + (list.isEmpty() ? 0 : 1);
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

    static int[] computeHeaderYs(int headerHeight, int itemHeight, List<Collection<?>> buckets) {
        final int n = buckets.size();
        final int[] untrimmedYs = new int[n];
        int j = 0, prev = 0;
        for (int i = 0; i < n; i++) {
            int length = bucketSize(headerHeight, itemHeight, buckets.get(i));
            if (length > 0) {
                untrimmedYs[j++] = prev;
                prev += length;
            }
        }
        return Arrays.copyOf(untrimmedYs, j);
    }

    static int bucketSize(int headerHeight, int itemHeight, Collection<?> items) {
        return (items.size() * itemHeight) + ((items.isEmpty() ? 0 : 1) * headerHeight);
    }

    static abstract class ViewHolder extends RecyclerView.ViewHolder {
        protected TextView listItem;
        protected TextView header;

        public ViewHolder(View itemView) {
            super(itemView);
            listItem = (TextView) itemView.findViewById(R.id.normal_list_item);
            header = (TextView) itemView.findViewById(R.id.header);
        }

        abstract void populate(String text);

    }

    static class ListItemVH extends ViewHolder {
        public ListItemVH(View itemView) {
            super(itemView);
            header.setVisibility(View.GONE);
            listItem.setVisibility(View.VISIBLE);
        }

        @Override
        void populate(String text) {
            listItem.setText(text);
        }

    }

    static class HeaderVH extends ViewHolder {
        public HeaderVH(View itemView) {
            super(itemView);
            header.setVisibility(View.VISIBLE);
            listItem.setVisibility(View.GONE);
        }

        @Override
        void populate(String text) {
            header.setText(text);
        }
    }

    abstract class StickyScrollListener extends RecyclerView.OnScrollListener {
        final int[] headerYs;
        final int n;
        int y = 0;
        int _i = -1;
        public StickyScrollListener(int[] headerYs) {
            n = headerYs.length;
            this.headerYs = Arrays.copyOf(headerYs, n);
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            y += dy;
            for (int i = 0; i < n; i++) {
                int yi = headerYs[i];
                if (yi <= y && (i == n - 1 || y < headerYs[i+1]) && _i != i) {
                    _i = i;
                     update(i);
                }
            }
        }

        protected abstract void update(int index);
    }
}

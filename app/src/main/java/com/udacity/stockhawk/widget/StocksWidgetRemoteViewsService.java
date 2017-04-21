package com.udacity.stockhawk.widget;

import android.content.Intent;
import android.database.Cursor;
import android.os.Binder;
import android.util.Log;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.udacity.stockhawk.CurrencyUtils;
import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.Contract.Quote;

import static com.udacity.stockhawk.ui.DetailActivity.EXTRA_CHANGE;
import static com.udacity.stockhawk.ui.DetailActivity.EXTRA_PRICE;
import static com.udacity.stockhawk.ui.DetailActivity.EXTRA_SYMBOL;

public class StocksWidgetRemoteViewsService extends RemoteViewsService {

    private static final String[] QUOTE_COLUMNS = {
            Quote._ID,
            Quote.COLUMN_SYMBOL,
            Quote.COLUMN_PRICE,
            Quote.COLUMN_ABSOLUTE_CHANGE,
            Quote.COLUMN_PERCENTAGE_CHANGE,
            Quote.COLUMN_HISTORY
    };

    @Override
    public RemoteViewsFactory onGetViewFactory(final Intent intent) {
        Log.d("PEDRO", this.getClass().getSimpleName() + " onGetViewFactory");
        return new RemoteViewsFactory() {
            private Cursor data = null;

            @Override
            public void onCreate() {

            }

            @Override
            public void onDataSetChanged() {
                if (data != null) {
                    data.close();
                }

                long identityToken = Binder.clearCallingIdentity();

                data = getContentResolver().query(Quote.URI,
                        QUOTE_COLUMNS,
                        null, null, null);

                Binder.restoreCallingIdentity(identityToken);
            }

            @Override
            public void onDestroy() {
                if (data != null) {
                    data.close();
                    data = null;
                }
            }

            @Override
            public int getCount() {
                return data == null ? 0 : data.getCount();
            }

            @Override
            public RemoteViews getViewAt(int position) {
                if (position == AdapterView.INVALID_POSITION || data == null || !data.moveToPosition(position)) {
                    return null;
                }

                String symbol = data.getString(Quote.POSITION_SYMBOL);
                String price = CurrencyUtils.formatDollar(data.getDouble(Quote.POSITION_PRICE));

                double changeDouble = data.getDouble(Quote.POSITION_PERCENTAGE_CHANGE);
                String change = CurrencyUtils.formatDollarWithPlus(changeDouble);

                Intent fillInIntent = new Intent();
                intent.putExtra(EXTRA_SYMBOL, symbol);
                intent.putExtra(EXTRA_PRICE, price);
                intent.putExtra(EXTRA_CHANGE, change);

                RemoteViews views = new RemoteViews(getPackageName(), R.layout.widget_stocks_item);
                views.setTextViewText(R.id.widget_symbol, symbol);
                views.setTextViewText(R.id.widget_price, price);
                views.setTextViewText(R.id.widget_change, change);

                views.setInt(R.id.change, "setBackgroundResource",
                        changeDouble > 0 ? R.drawable.percent_change_pill_green : R.drawable.percent_change_pill_red);

                views.setOnClickFillInIntent(R.id.widget_list_item, fillInIntent);

                return views;
            }

            @Override
            public RemoteViews getLoadingView() {
                return new RemoteViews(getPackageName(), R.layout.widget_stocks_item);
            }

            @Override
            public int getViewTypeCount() {
                return 1;
            }

            @Override
            public long getItemId(int position) {
                if (data.moveToPosition(position)) {
                    return data.getLong(Quote.POSITION_ID);
                }
                return position;
            }

            @Override
            public boolean hasStableIds() {
                return true;
            }
        };
    }
}

package com.udacity.stockhawk.ui;

import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.udacity.stockhawk.CurrencyUtils;
import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.Contract.Quote;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailActivity extends AppCompatActivity {

    @BindView(R.id.details_action_bar)
    Toolbar mToolbar;

    @BindView(R.id.detail_symbol)
    TextView mSymbolTextView;

    @BindView(R.id.detail_price)
    TextView mPriceTextView;

    @BindView(R.id.detail_change)
    TextView mChangeTextView;

    @BindView(R.id.detail_chart)
    LineChart mChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        ButterKnife.bind(this);

        Uri uri = getIntent().getData();

        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.detail_activity_title);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        new LoadStockData().execute(uri);
    }

    void loadGraph(String historyString) {

        final List<String> headers = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("MM/yy", Locale.US);
        List<Entry> entries = new ArrayList<>();
        int xCounter = 0;

        String[] historyLines = historyString.split("\n");
        for (String line : historyLines) {
            headers.add(sdf.format(new Date(Long.valueOf(line.split(",")[0]))));
            entries.add(new Entry(xCounter++, Float.valueOf(line.split(",")[1])));
        }

        LineDataSet dataSet = new LineDataSet(entries, getString(R.string.detail_graph_legend));
        dataSet.setLineWidth(2);
        dataSet.setValueTextSize(10);
        dataSet.setDrawFilled(true);

        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.setTextSize(12);

        XAxis xAxis = mChart.getXAxis();
        xAxis.setTextSize(12);
        xAxis.setGranularity(1);
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return headers.get((int) value);
            }
        });

        mChart.getDescription().setEnabled(false);
        mChart.setDragEnabled(true);
        mChart.setScaleEnabled(true);
        mChart.setPinchZoom(true);
        mChart.setDrawGridBackground(false);

        mChart.setData(new LineData(dataSet));
        mChart.invalidate();
    }

    private class LoadStockData extends AsyncTask<Uri, Void, Cursor> {

        protected Cursor doInBackground(Uri... params) {
            if (params.length < 1) return null;

            String[] projection = Quote.QUOTE_COLUMNS.toArray(new String[Quote.QUOTE_COLUMNS.size()]);
            return getContentResolver().query(params[0], projection, null, null, null);
        }

        @Override
        protected void onPostExecute(Cursor data) {
            if (data == null || !data.moveToFirst()) return;

            Double changeDouble = data.getDouble(Quote.POSITION_ABSOLUTE_CHANGE);

            String symbol = data.getString(Quote.POSITION_SYMBOL);
            String price = CurrencyUtils.formatDollar(data.getDouble(Quote.POSITION_PRICE));
            String change = CurrencyUtils.formatDollarWithPlus(changeDouble);

            mSymbolTextView.setText(symbol);
            mPriceTextView.setText(price);
            mChangeTextView.setText(change);
            mChangeTextView.setBackgroundResource(changeDouble > 0 ?
                    R.drawable.percent_change_pill_green :
                    R.drawable.percent_change_pill_red);

            loadGraph(data.getString(Quote.POSITION_HISTORY));
        }
    }
}

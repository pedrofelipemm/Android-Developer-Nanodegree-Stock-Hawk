package com.udacity.stockhawk.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.udacity.stockhawk.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailActivity extends AppCompatActivity {

    public static final String EXTRA_SYMBOL = "symbol";
    public static final String EXTRA_PRICE = "price";
    public static final String EXTRA_CHANGE = "change";

    @BindView(R.id.details_action_bar)
    Toolbar mToolbar;

    @BindView(R.id.detail_price)
    TextView mPriceTextView;

    @BindView(R.id.detail_change)
    TextView mChangeTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);

        Intent intent = getIntent();
        if (getSupportActionBar() != null && intent.hasExtra(EXTRA_SYMBOL)) {
            getSupportActionBar().setTitle(intent.getStringExtra(EXTRA_SYMBOL));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        if (intent.hasExtra(EXTRA_PRICE)) {
            mPriceTextView.setText(intent.getStringExtra(EXTRA_PRICE));
        }

        if (intent.hasExtra(EXTRA_CHANGE)) {
            mChangeTextView.setText(intent.getStringExtra(EXTRA_CHANGE));
        }
    }
}

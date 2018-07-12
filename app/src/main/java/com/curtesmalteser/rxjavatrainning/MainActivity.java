package com.curtesmalteser.rxjavatrainning;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import com.jakewharton.rxbinding2.view.RxView;
import com.jakewharton.rxbinding2.widget.RxTextView;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

/**
 * Created by António "Curtes Malteser" Bastião on 12/07/2018.
 */
public class MainActivity extends AppCompatActivity {

    @BindView(R.id.editText)
    EditText mEditText;

    @BindView(R.id.btn)
    AppCompatButton btn;

    @BindView(R.id.textView)
    TextView textView;

    private int x = 0;

    private Disposable btnDisposable;
    private Disposable textDisposable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        btnDisposable = RxView.clicks(btn)
                .throttleFirst(1000, TimeUnit.MILLISECONDS)
                .subscribe(aVoid -> setText(), this::handleError);

        textDisposable = RxTextView.textChangeEvents(mEditText)
                .debounce(1000, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aVoid -> getTextFromDisposable(mEditText), this::handleError);
    }

    private void getTextFromDisposable(EditText editText) {
        if (!editText.getText().toString().equals("")) {
            textView.setText("with debounce " + editText.getText().toString());
            editText.setText("");
        }
    }

    private void setText() {
        textView.setText("with throttleFirst " + x);
        x++;
    }

    private void handleError(Throwable throwable) {
        Log.e("TAG", throwable.getMessage());
    }

    @Override
    protected void onPause() {
        super.onPause();
        btnDisposable.dispose();
        textDisposable.dispose();
    }
}

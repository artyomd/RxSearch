package com.artyomd.rx;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Cancellable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class SearchActivity extends Activity {

	private static final String API_KEY = "AIzaSyBU22SbQswBagy5Qcli8KjyDxMy19WagnE";
	private static final String CX = "003630389706661019147:i5a1vmvqv4m";

	private Disposable disposable;
	private EditText queryEditText;
	private Button searchButton;
	private SearchAdapter adapter;
	private ProgressBar progressBar;
	private CustomSearchService service;


	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		RecyclerView list = findViewById(R.id.list);
		list.setLayoutManager(new LinearLayoutManager(this));
		list.setAdapter(adapter = new SearchAdapter(getApplicationContext()));

		queryEditText = findViewById(R.id.query_edit_text);
		searchButton = findViewById(R.id.search_button);
		progressBar = findViewById(R.id.progress_bar);

		Retrofit retrofit = new Retrofit.Builder()
				.baseUrl("https://www.googleapis.com/")
				.addConverterFactory(GsonConverterFactory.create())
				.addCallAdapterFactory(RxJava2CallAdapterFactory.createAsync())
				.build();
		service = retrofit.create(CustomSearchService.class);
	}

	protected void showProgressBar() {
		progressBar.setVisibility(View.VISIBLE);
	}

	protected void hideProgressBar() {
		progressBar.setVisibility(View.GONE);
	}

	protected void showResult(List<Response> result) {
		if (result == null || result.isEmpty()) {
			adapter.setData(Collections.<Response>emptyList());
		} else {
			adapter.setData(result);
		}
	}

	@Override
	protected void onStart() {
		super.onStart();

		Observable<String> buttonClickStream = createButtonClickObservable();
		Observable<String> textChangeStream = createTextChangeObservable();

		Observable<String> searchTextObservable = Observable.merge(textChangeStream, buttonClickStream);

		disposable = searchTextObservable
				.subscribeOn(Schedulers.io())
				.flatMap(new Function<String, ObservableSource<StreamState>>() {
					@Override
					public ObservableSource<StreamState> apply(@NonNull String s) {
						return service.search(s, API_KEY, CX)
								.map(new Function<ResponseModel, StreamState>() {
									@Override
									public StreamState apply(@NonNull ResponseModel responseModel) {
										return StreamState.createSuccess(responseModel.getItems());
									}
								})
								.onErrorReturn(new Function<Throwable, StreamState>() {
									@Override
									public StreamState apply(@NonNull Throwable throwable) {
										return StreamState.setError(throwable.getMessage());
									}
								})
								.observeOn(AndroidSchedulers.mainThread())
								.startWith(StreamState.createInProgress());
					}
				})
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(new Consumer<StreamState>() {
					@Override
					public void accept(StreamState state) {
						if (state.isInProgress()) {
							showProgressBar();
						} else if (state.isSuccess()) {
							hideProgressBar();
							showResult(state.getData());
						} else if (state.getErrorMessage() != null) {
							Toast.makeText(SearchActivity.this, "error: " + state.getErrorMessage(), Toast.LENGTH_SHORT).show();
							hideProgressBar();
						}
					}
				});
	}

	@Override
	protected void onStop() {
		super.onStop();
		if (!disposable.isDisposed()) {
			disposable.dispose();
		}
	}

	private Observable<String> createButtonClickObservable() {
		return Observable.create(new ObservableOnSubscribe<String>() {
			@Override
			public void subscribe(final ObservableEmitter<String> emitter) {
				searchButton.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						searchButton.setClickable(false);
						emitter.onNext(queryEditText.getText().toString());
					}
				});
				emitter.setCancellable(new Cancellable() {
					@Override
					public void cancel() {
						searchButton.setOnClickListener(null);
					}
				});
			}
		});
	}

	private Observable<String> createTextChangeObservable() {
		return Observable.create(new ObservableOnSubscribe<String>() {
			@Override
			public void subscribe(final ObservableEmitter<String> emitter) {
				final TextWatcher watcher = new TextWatcher() {
					@Override
					public void beforeTextChanged(CharSequence s, int start, int count, int after) {
					}

					@Override
					public void afterTextChanged(Editable s) {
					}

					@Override
					public void onTextChanged(CharSequence s, int start, int before, int count) {
						emitter.onNext(s.toString());
					}
				};
				queryEditText.addTextChangedListener(watcher);
				emitter.setCancellable(new Cancellable() {
					@Override
					public void cancel() {
						queryEditText.removeTextChangedListener(watcher);
					}
				});
			}
		}).filter(new Predicate<String>() {
			@Override
			public boolean test(String query) {
				return query.length() >= 2;
			}
		}).debounce(1, TimeUnit.SECONDS);
	}
}
package com.artyomd.rx;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface CustomSearchService {
	@GET("customsearch/v1")
	Observable<ResponseModel> search(@Query("q") String querry, @Query("key") String apiKey, @Query("cx") String engine);
}

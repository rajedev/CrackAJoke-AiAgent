package org.crackajoke.agent.network

import org.crackajoke.agent.model.JokeResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Created by Rajendhiran Easu on 27/08/25.
 * Description: Retrofit interface for the Joke API
 */

interface JokeApi {
    @GET("joke/{category}")
    suspend fun getJoke(
        @Path("category") category: String,
        @Query("type") type: String,
    ): JokeResponse
}

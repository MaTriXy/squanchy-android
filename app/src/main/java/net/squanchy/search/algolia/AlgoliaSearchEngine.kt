package net.squanchy.search.algolia

import io.reactivex.Observable
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers
import net.squanchy.search.algolia.model.AlgoliaSearchResponse
import net.squanchy.search.algolia.model.AlgoliaSearchResult
import net.squanchy.search.algolia.model.AlgoliaSearchResult.ErrorSearching
import net.squanchy.search.algolia.model.AlgoliaSearchResult.QueryNotLongEnough
import net.squanchy.search.algolia.model.AlgoliaSearchResult.Matches
import timber.log.Timber

class AlgoliaSearchEngine(
    private val eventIndex: SearchIndex,
    private val speakerIndex: SearchIndex
) {

    fun query(key: String): Observable<AlgoliaSearchResult> {
        val trimmedQuery = key.trim()
        if (trimmedQuery.length < QUERY_MIN_LENGTH) {
            return Observable.just(QueryNotLongEnough)
        } else {
            return Observable.combineLatest(
                eventIndex.searchAsObservable(trimmedQuery),
                speakerIndex.searchAsObservable(trimmedQuery),
                combineInPair()
            )
                .map<AlgoliaSearchResult> { Matches(it.first.extractIds(), it.second.extractIds()) }
                .doOnError(Timber::e)
                .onErrorReturnItem(ErrorSearching)
                .subscribeOn(Schedulers.io())
        }
    }

    private fun SearchIndex.searchAsObservable(key: String): Observable<AlgoliaSearchResponse> = Observable.fromCallable { search(key) }

    private fun combineInPair(): BiFunction<AlgoliaSearchResponse, AlgoliaSearchResponse, Pair<AlgoliaSearchResponse, AlgoliaSearchResponse>> =
        BiFunction(::Pair)

    private fun AlgoliaSearchResponse.extractIds() = hits?.map { it.objectID } ?: emptyList()

    companion object {
        private const val QUERY_MIN_LENGTH = 2
    }
}

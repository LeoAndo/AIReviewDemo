package jp.ac.jec.cm0199.aireviewdemo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URLEncoder
import java.net.URL

data class RepoItem(
    val name: String,
    val fullName: String,
    val description: String?,
    val stars: Int,
    val htmlUrl: String,
)

class MainViewModel : ViewModel() {

    private val _loading = MutableLiveData(false)
    val loading: LiveData<Boolean> = _loading

    private val _error = MutableLiveData<String?>(null)
    val error: LiveData<String?> = _error

    private val _results = MutableLiveData<List<RepoItem>>(emptyList())
    val results: LiveData<List<RepoItem>> = _results

    fun search(query: String) {
        val q = query.trim()
        if (q.isEmpty()) {
            _results.value = emptyList()
            _error.value = null
            return
        }

        _loading.value = true
        _error.value = null

        Thread {
            try {
                val encoded = URLEncoder.encode(q, "UTF-8")
                val url = URL("https://api.github.com/search/repositories?q=$encoded&sort=stars&order=desc")
                val conn = (url.openConnection() as HttpURLConnection).apply {
                    requestMethod = "GET"
                    setRequestProperty("Accept", "application/vnd.github+json")
                    setRequestProperty("X-GitHub-Api-Version", "2022-11-28")
                    setRequestProperty("User-Agent", "AIReviewDemo-App")
                    connectTimeout = 10000
                    readTimeout = 10000
                }

                val code = conn.responseCode
                val stream = if (code in 200..299) conn.inputStream else conn.errorStream
                val body = BufferedReader(InputStreamReader(stream)).use { it.readText() }
                conn.disconnect()

                if (code == 403) {
                    _error.postValue("RATE_LIMIT")
                    _results.postValue(emptyList())
                } else if (code in 200..299) {
                    val json = JSONObject(body)
                    val items = json.getJSONArray("items")
                    val list = ArrayList<RepoItem>(items.length())
                    for (i in 0 until items.length()) {
                        val o = items.getJSONObject(i)
                        list.add(
                            RepoItem(
                                name = o.optString("name"),
                                fullName = o.optString("full_name"),
                                description = o.optString("description", null),
                                stars = o.optInt("stargazers_count"),
                                htmlUrl = o.optString("html_url"),
                            )
                        )
                    }
                    _results.postValue(list)
                } else {
                    _error.postValue("UNKNOWN")
                    _results.postValue(emptyList())
                }
            } catch (_: Exception) {
                _error.postValue("NETWORK")
                _results.postValue(emptyList())
            } finally {
                _loading.postValue(false)
            }
        }.start()
    }
}
package com.souldream.data

import com.google.firebase.auth.FirebaseAuth
import com.souldream.BuildConfig
import com.souldream.model.ActivityType
import com.souldream.model.CostItem
import com.souldream.model.TripActivity
import com.souldream.model.TripDay
import com.souldream.model.TripPlan
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import java.util.UUID
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await
import org.json.JSONObject

class HttpException(val code: Int) : IOException("HTTP $code")

/** What the backend streams while it plans a trip. */
sealed interface PlanEvent {
    /** Progress message, e.g. "Found restaurants". */
    data class Status(val text: String) : PlanEvent

    /** The trip summary (title, budget, costs...) with no days yet. */
    data class Meta(val plan: TripPlan) : PlanEvent

    data class DayAdded(val day: TripDay) : PlanEvent
}

/** Calls the FastAPI backend. Every request carries the signed-in user's Firebase ID token. */
object BackendApi {
    /**
     * Streams a plan as it is written: progress messages, then the summary, then one event per day.
     * The flow ends normally only if the backend said it was done; a cut connection throws.
     * ponytail: cancelling waits for the next line before the connection closes.
     */
    fun plan(query: String): Flow<PlanEvent> = flow {
        val token = FirebaseAuth.getInstance().currentUser?.getIdToken(false)?.await()?.token
            ?: throw HttpException(401)
        val conn = URL("${BuildConfig.BACKEND_URL}/plan").openConnection() as HttpURLConnection
        try {
            conn.requestMethod = "POST"
            conn.connectTimeout = 15_000
            conn.readTimeout = 120_000 // between lines, not for the whole trip
            conn.doOutput = true
            conn.setRequestProperty("Authorization", "Bearer $token")
            conn.setRequestProperty("Content-Type", "application/json")
            conn.outputStream.use { it.write(JSONObject().put("query", query).toString().toByteArray()) }
            if (conn.responseCode !in 200..299) throw HttpException(conn.responseCode)

            var finished = false
            conn.inputStream.bufferedReader().use { reader ->
                while (!finished) {
                    val line = reader.readLine() ?: break
                    if (line.isBlank()) continue
                    val json = JSONObject(line)
                    when (json.getString("type")) {
                        "status" -> emit(PlanEvent.Status(json.getString("text")))
                        "meta" -> emit(PlanEvent.Meta(parsePlan(json)))
                        "day" -> emit(PlanEvent.DayAdded(parseDay(json, 0)))
                        "done" -> finished = true
                        "error" -> throw IOException(json.optString("message"))
                    }
                }
            }
            if (!finished) throw IOException("Connection closed before the plan was finished")
        } finally {
            conn.disconnect()
        }
    }.flowOn(Dispatchers.IO)
}

// optString would turn a JSON null into the text "null", which Coil then tries to load.
private fun JSONObject.stringOrNull(key: String) = if (isNull(key)) null else getString(key)

internal fun parsePlan(json: JSONObject): TripPlan {
    val days = json.optJSONArray("days")
    return TripPlan(
        id = UUID.randomUUID().toString(),
        title = json.getString("title"),
        dateRange = json.optString("dateRange"),
        location = json.optString("location"),
        budget = json.optString("budget"),
        duration = json.optString("duration"),
        rating = json.optString("rating", "-"),
        heroImageUrl = json.stringOrNull("heroImageUrl"),
        perDayBudget = json.optString("perDayBudget"),
        costBreakdown = json.optJSONArray("costBreakdown")?.let { costs ->
            List(costs.length()) { i ->
                val c = costs.getJSONObject(i)
                CostItem(c.optString("label"), c.optString("amount"))
            }
        }.orEmpty(),
        weather = json.optString("weather"),
        transport = json.optString("transport"),
        days = List(days?.length() ?: 0) { i -> parseDay(days!!.getJSONObject(i), i) },
    )
}

internal fun parseDay(d: JSONObject, i: Int): TripDay {
    val acts = d.getJSONArray("activities")
    val number = d.optInt("dayNumber", i + 1)
    return TripDay(
        id = "d$number",
        dayNumber = number,
        date = d.optString("date"),
        theme = d.optString("theme"),
        activities = List(acts.length()) { j ->
            val a = acts.getJSONObject(j)
            TripActivity(
                id = "d$number-a$j",
                title = a.getString("title"),
                description = a.optString("description"),
                location = a.optString("location"),
                type = ActivityType.entries.firstOrNull { it.name == a.optString("type") } ?: ActivityType.Activity,
                imageUrl = a.stringOrNull("imageUrl"),
            )
        },
    )
}

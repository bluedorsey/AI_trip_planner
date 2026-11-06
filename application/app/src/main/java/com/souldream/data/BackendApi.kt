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
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import org.json.JSONObject

class HttpException(val code: Int) : IOException("HTTP $code")

/** Calls the FastAPI backend. Every request carries the signed-in user's Firebase ID token. */
object BackendApi {
    /** Generates a structured plan. The agent makes several tool calls, so the read timeout is long. */
    suspend fun plan(query: String): TripPlan = withContext(Dispatchers.IO) {
        val token = FirebaseAuth.getInstance().currentUser?.getIdToken(false)?.await()?.token
            ?: throw HttpException(401)
        val conn = URL("${BuildConfig.BACKEND_URL}/plan").openConnection() as HttpURLConnection
        try {
            conn.requestMethod = "POST"
            conn.connectTimeout = 15_000
            conn.readTimeout = 180_000
            conn.doOutput = true
            conn.setRequestProperty("Authorization", "Bearer $token")
            conn.setRequestProperty("Content-Type", "application/json")
            conn.outputStream.use { it.write(JSONObject().put("query", query).toString().toByteArray()) }
            if (conn.responseCode !in 200..299) throw HttpException(conn.responseCode)
            parsePlan(JSONObject(conn.inputStream.bufferedReader().use { it.readText() }))
        } finally {
            conn.disconnect()
        }
    }
}

// optString would turn a JSON null into the text "null", which Coil then tries to load.
private fun JSONObject.stringOrNull(key: String) = if (isNull(key)) null else getString(key)

internal fun parsePlan(json: JSONObject): TripPlan {
    val days = json.getJSONArray("days")
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
        days = List(days.length()) { i ->
            val d = days.getJSONObject(i)
            val acts = d.getJSONArray("activities")
            val number = d.optInt("dayNumber", i + 1)
            TripDay(
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
        },
    )
}

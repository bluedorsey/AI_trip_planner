package com.souldream.data

import android.util.Log
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.souldream.model.ActivityType
import com.souldream.model.Destination
import com.souldream.model.TripActivity
import com.souldream.model.TripDay
import com.souldream.model.TripPlan
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

/** A plan as stored in Firestore: [saved] is the user's "Save trip" flag. */
data class StoredTrip(val plan: TripPlan, val saved: Boolean)

/**
 * Firestore access. Layout (enforced by firestore.rules):
 *   users/{uid}/trips/{tripId}   private to that user, written by the app
 *   destinations/{id}            read-only for signed-in users, managed from the console
 * Writes are fire-and-forget: Firestore applies them to its local cache at once and syncs later.
 */
object TripStore {
    private const val TAG = "TripStore"
    private val db get() = FirebaseFirestore.getInstance()

    private fun trips(uid: String) = db.collection("users").document(uid).collection("trips")

    fun observeTrips(uid: String): Flow<List<StoredTrip>> = callbackFlow {
        val reg = trips(uid).orderBy("createdAt", Query.Direction.DESCENDING).limit(50)
            .addSnapshotListener { snap, err ->
                if (err != null || snap == null) close(err) else trySend(snap.documents.mapNotNull { it.toStoredTrip() })
            }
        awaitClose { reg.remove() }
    }

    fun observeDestinations(): Flow<List<Destination>> = callbackFlow {
        val reg = db.collection("destinations").limit(30).addSnapshotListener { snap, err ->
            if (err != null || snap == null) close(err) else trySend(
                snap.documents.mapNotNull { d ->
                    Destination(
                        id = d.id,
                        name = d.getString("name") ?: return@mapNotNull null,
                        tagline = d.getString("tagline").orEmpty(),
                        imageUrl = d.getString("imageUrl"),
                    )
                },
            )
        }
        awaitClose { reg.remove() }
    }

    fun add(uid: String, plan: TripPlan, query: String) {
        trips(uid).document(plan.id).set(plan.toMap(query))
            .addOnFailureListener { Log.w(TAG, "save failed", it) }
    }

    fun setSaved(uid: String, tripId: String, saved: Boolean) {
        trips(uid).document(tripId).update("saved", saved)
            .addOnFailureListener { Log.w(TAG, "setSaved failed", it) }
    }
}

private fun TripPlan.toMap(query: String): Map<String, Any> = buildMap {
    put("title", title)
    put("location", location)
    put("dateRange", dateRange)
    put("budget", budget)
    put("duration", duration)
    put("rating", rating)
    heroImageUrl?.let { put("heroImageUrl", it) }
    put("query", query.take(500))
    put("saved", false)
    put("createdAt", FieldValue.serverTimestamp())
    put(
        "days",
        days.map { d ->
            mapOf(
                "dayNumber" to d.dayNumber,
                "date" to d.date,
                "theme" to d.theme,
                "activities" to d.activities.map { a ->
                    mapOf(
                        "title" to a.title,
                        "description" to a.description,
                        "location" to a.location,
                        "type" to a.type.name,
                    )
                },
            )
        },
    )
}

@Suppress("UNCHECKED_CAST")
private fun DocumentSnapshot.toStoredTrip(): StoredTrip? {
    val title = getString("title") ?: return null
    val days = (get("days") as? List<Map<String, Any?>>).orEmpty().mapIndexed { i, d ->
        val number = (d["dayNumber"] as? Number)?.toInt() ?: (i + 1)
        TripDay(
            id = "d$number",
            dayNumber = number,
            date = d["date"] as? String ?: "",
            theme = d["theme"] as? String ?: "",
            activities = (d["activities"] as? List<Map<String, Any?>>).orEmpty().mapIndexed { j, a ->
                TripActivity(
                    id = "d$number-a$j",
                    title = a["title"] as? String ?: "",
                    description = a["description"] as? String ?: "",
                    location = a["location"] as? String ?: "",
                    type = ActivityType.entries.firstOrNull { it.name == a["type"] } ?: ActivityType.Activity,
                )
            },
        )
    }
    val plan = TripPlan(
        id = id,
        title = title,
        dateRange = getString("dateRange").orEmpty(),
        location = getString("location").orEmpty(),
        budget = getString("budget").orEmpty(),
        duration = getString("duration").orEmpty(),
        rating = getString("rating") ?: "-",
        days = days,
        heroImageUrl = getString("heroImageUrl"),
    )
    return StoredTrip(plan, saved = getBoolean("saved") ?: false)
}

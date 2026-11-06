package com.souldream

import com.souldream.data.parsePlan
import com.souldream.model.ActivityType
import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Test

class ParsePlanTest {
    private val json = JSONObject(
        """{"title":"Jaipur","location":"Jaipur","dateRange":"Dec 20 - Dec 21","budget":"Rs 18,000",
        "duration":"2 days","rating":"-","days":[{"dayNumber":1,"date":"Dec 20","theme":"Pink City",
        "activities":[{"title":"Hawa Mahal","description":"d","location":"MI Road","type":"Sightseeing"},
        {"title":"Mystery","description":"d","location":"x","type":"Spaceship"}]}]}""",
    )

    @Test
    fun mapsDaysAndActivities() {
        val plan = parsePlan(json)
        assertEquals("Jaipur", plan.title)
        assertEquals(1, plan.days.size)
        assertEquals("d1-a0", plan.days[0].activities[0].id)
        assertEquals(ActivityType.Sightseeing, plan.days[0].activities[0].type)
    }

    @Test
    fun unknownActivityTypeFallsBackToActivity() {
        assertEquals(ActivityType.Activity, parsePlan(json).days[0].activities[1].type)
    }

    @Test
    fun readsImageUrlsAndTreatsJsonNullAsNoImage() {
        val plan = parsePlan(
            JSONObject(
                """{"title":"T","heroImageUrl":"https://x/hero.jpg","days":[{"dayNumber":1,"activities":[
                {"title":"A","imageUrl":"https://x/a.jpg"},{"title":"B","imageUrl":null}]}]}""",
            ),
        )
        assertEquals("https://x/hero.jpg", plan.heroImageUrl)
        assertEquals("https://x/a.jpg", plan.days[0].activities[0].imageUrl)
        assertEquals(null, plan.days[0].activities[1].imageUrl)
        assertEquals(null, parsePlan(JSONObject("""{"title":"T","days":[]}""")).heroImageUrl)
    }

    @Test
    fun readsTripDetails() {
        val plan = parsePlan(
            JSONObject(
                """{"title":"T","days":[],"perDayBudget":"Rs 6,000","weather":"Sunny","transport":"Metro",
                "costBreakdown":[{"label":"Hotel","amount":"Rs 8,000"}]}""",
            ),
        )
        assertEquals("Rs 6,000", plan.perDayBudget)
        assertEquals("Sunny", plan.weather)
        assertEquals("Metro", plan.transport)
        assertEquals("Rs 8,000", plan.costBreakdown.single().amount)
        assertEquals(0, parsePlan(JSONObject("""{"title":"T","days":[]}""")).costBreakdown.size)
    }

    @Test
    fun missingOptionalFieldsDoNotCrash() {
        val plan = parsePlan(JSONObject("""{"title":"T","days":[]}"""))
        assertEquals("-", plan.rating.ifEmpty { "-" })
        assertEquals(0, plan.days.size)
    }
}

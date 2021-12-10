package ru.get.better.event

data class ShowSpotlightEvent(val spotlightType: SpotlightType)
data class TryShowSpotlightEvent(val spotlightType: SpotlightType)

enum class SpotlightType(val id: Int) {
    MetricWheel(0),
    MainAddPost(1),
    DiaryHabits(2)
}
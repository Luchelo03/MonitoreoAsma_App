package com.example.monitoreoasma.data.remote.dto

data class RiskSummary(
    val level: String,
    val score: Double
)

data class TestRunItem(
    val test_run_id: String,
    val started_at: String,
    val finished_at: String? = null,
    val risk: RiskSummary
)

data class TestRunsResponse(
    val child_id: String,
    val limit: Int,
    val offset: Int,
    val items: List<TestRunItem>
)

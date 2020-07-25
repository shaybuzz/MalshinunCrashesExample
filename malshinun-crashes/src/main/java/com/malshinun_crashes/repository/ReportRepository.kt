package com.malshinun_crashes.repository

import com.malshinun_crashes.model.Report

internal interface ReportRepository {
    fun saveReport(report: Report)
    fun getReport(): Report?
    fun delete(report: Report)
}
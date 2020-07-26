package com.malshinun_crashes

import com.malshinun_crashes.model.MiscData
import com.malshinun_crashes.model.Report
import com.malshinun_crashes.remote.ReportApi
import com.malshinun_crashes.repository.ReportRepository
import com.nhaarman.mockitokotlin2.*
import okhttp3.ResponseBody
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import retrofit2.Call
import retrofit2.Response


@RunWith(JUnit4::class)
class SenderManagerTest {
    private val serverResponse: Call<Void> = mock()

    private val reportApi: ReportApi = mock() {
        on { sendReport(any()) } doReturn serverResponse
    }
    private val reportRepository: ReportRepository = mock()
    private val miscData: MiscData =
        MiscData("somename", "someVersion", "manufactor", "model", 1, "version")
    private val interval = 10000L
    private val report: Report = Report("some time", "some stackTrace")
    private lateinit var senderManger: SenderManger

    @Before
    fun setup() {
        senderManger = SenderManger(reportApi, reportRepository, miscData, interval)
    }

    @Test
    fun `check positive scenario`() {
        whenever(reportRepository.getReport()).doReturn(report)
        whenever(reportApi.sendReport(report)).thenReturn(serverResponse)
        whenever(serverResponse.execute()).thenReturn(Response.success<Void>(null))
        senderManger.sendReportTask()
        verify(reportApi).sendReport(report)
        verify(reportRepository).delete(report)
    }

    @Test
    fun `check server returns error scenario`() {
        whenever(reportRepository.getReport()).doReturn(report)
        whenever(reportApi.sendReport(report)).thenReturn(serverResponse)
        whenever(serverResponse.execute()).thenReturn(
            Response.error(
                400,
                ResponseBody.create(null, "error")
            )
        )
        senderManger.sendReportTask()
        verify(reportApi).sendReport(report)
        verify(reportRepository, never()).delete(report)
    }

    @Test
    fun `check repository return no report`() {
        whenever(reportRepository.getReport()).doReturn(null)
        senderManger.sendReportTask()
        verify(reportApi, never()).sendReport(report)
        verify(reportRepository, never()).delete(report)
    }


}
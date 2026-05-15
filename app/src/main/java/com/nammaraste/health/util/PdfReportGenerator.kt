package com.nammaraste.health.util

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.os.Environment
import com.nammaraste.health.data.local.entity.DamageReport
import com.nammaraste.health.data.local.entity.Road
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

object PdfReportGenerator {

    private const val PAGE_WIDTH  = 595
    private const val PAGE_HEIGHT = 842
    private const val MARGIN      = 40f
    private const val LINE        = 22f

    fun generateRoadReport(
        context: Context,
        road: Road,
        healthScore: Int,
        reports: List<DamageReport>
    ): File {
        val doc = PdfDocument()
        var pageNum = 1
        var page    = doc.startPage(
            PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, pageNum).create()
        )
        var canvas  = page.canvas
        var y       = MARGIN + 20f

        // ── Paints ──
        val green  = Color.parseColor("#1B6B3A")
        val grey   = Color.parseColor("#666666")
        val black  = Color.parseColor("#1A1A1A")
        val red    = Color.parseColor("#C0392B")
        val amber  = Color.parseColor("#E67E22")

        val titleP = Paint().apply { textSize = 20f; isFakeBoldText = true; color = green }
        val headP  = Paint().apply { textSize = 13f; isFakeBoldText = true; color = green }
        val labelP = Paint().apply { textSize = 11f; color = grey }
        val valueP = Paint().apply { textSize = 11f; isFakeBoldText = true; color = black }
        val divP   = Paint().apply { color = Color.parseColor("#E0E0E0"); strokeWidth = 1f }
        val statusColor = when (HealthCalculator.getStatus(healthScore)) {
            HealthStatus.GOOD     -> green
            HealthStatus.AT_RISK  -> amber
            HealthStatus.CRITICAL -> red
        }
        val statusP = Paint().apply { textSize = 12f; isFakeBoldText = true; color = statusColor }

        fun newPageIfNeeded() {
            if (y > PAGE_HEIGHT - 100f) {
                doc.finishPage(page)
                pageNum++
                page   = doc.startPage(
                    PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, pageNum).create()
                )
                canvas = page.canvas
                y      = MARGIN + 20f
            }
        }

        fun row(label: String, value: String) {
            newPageIfNeeded()
            canvas.drawText(label, MARGIN, y, labelP)
            canvas.drawText(value, MARGIN + 150f, y, valueP)
            y += LINE
        }

        fun divider() {
            canvas.drawLine(MARGIN, y, PAGE_WIDTH - MARGIN, y, divP)
            y += LINE
        }

        val sdf = SimpleDateFormat("dd MMMM yyyy, hh:mm a", Locale.getDefault())

        // ── HEADER ──
        canvas.drawText("Namma-Raste Health", MARGIN, y, titleP)
        y += LINE
        canvas.drawText("PMGSY Road Infrastructure Report", MARGIN, y, labelP)
        y += LINE
        canvas.drawText("Generated: ${sdf.format(Date())}", MARGIN, y, labelP)
        y += LINE
        divider()

        // ── ROAD NAME ──
        canvas.drawText(road.name, MARGIN, y, titleP); y += LINE
        canvas.drawText("${road.roadCode}  ·  ${road.scheme}", MARGIN, y, labelP); y += LINE * 1.5f

        // ── HEALTH ──
        canvas.drawText("HEALTH STATUS", MARGIN, y, headP); y += LINE
        canvas.drawText(
            "Score: $healthScore%  —  ${HealthCalculator.getStatusLabel(healthScore)}",
            MARGIN, y, statusP
        ); y += LINE
        val wText = when (HealthCalculator.getWarrantyStatus(road.warrantyEnd)) {
            WarrantyStatus.ACTIVE   -> "Under Warranty until ${road.warrantyEnd}"
            WarrantyStatus.EXPIRING -> "Expires ${road.warrantyEnd}"
            WarrantyStatus.EXPIRED  -> "Expired ${road.warrantyEnd}"
            else -> road.warrantyEnd
        }
        canvas.drawText("Warranty: $wText", MARGIN, y, valueP); y += LINE
        divider()

        // ── ROAD INFO ──
        canvas.drawText("ROAD INFORMATION", MARGIN, y, headP); y += LINE
        row("District:",     road.district)
        row("Taluka:",       road.taluka)
        row("Hobli / Ward:", road.hobli)
        row("Pincode:",      road.pincode)
        row("Length:",       "${road.lengthKm} km")
        row("Built Year:",   road.constructionYear.toString())
        divider()

        // ── CONTRACTOR ──
        canvas.drawText("CONTRACTOR", MARGIN, y, headP); y += LINE
        row("Name:",    road.contractorName)
        row("License:", road.contractorLicense)
        row("Phone:",   road.contractorPhone)
        divider()

        // ── REPORTS ──
        val open     = reports.count { !it.isResolved }
        val resolved = reports.count {  it.isResolved }
        canvas.drawText(
            "DAMAGE REPORTS  (${reports.size} total  ·  $open open  ·  $resolved resolved)",
            MARGIN, y, headP
        ); y += LINE * 1.5f

        val timeSdf = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())

        reports.forEachIndexed { i, r ->
            newPageIfNeeded()
            val status = if (r.isResolved) "Resolved ✓" else "Open ●"
            canvas.drawText("${i + 1}. ${r.damageType}  —  $status", MARGIN, y, valueP)
            y += LINE
            if (r.description.isNotBlank()) {
                canvas.drawText("   ${r.description}", MARGIN, y, labelP); y += LINE
            }
            canvas.drawText(
                "   GPS: ${r.latitude}, ${r.longitude}   Time: ${timeSdf.format(Date(r.timestamp))}",
                MARGIN, y, labelP
            )
            y += LINE * 1.4f
        }

        doc.finishPage(page)

        // ── SAVE FILE ──
        val name = "RoadReport_${road.roadCode}_${System.currentTimeMillis()}.pdf"
        val dir  = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
        val file = File(dir, name)
        doc.writeTo(FileOutputStream(file))
        doc.close()
        return file
    }
}